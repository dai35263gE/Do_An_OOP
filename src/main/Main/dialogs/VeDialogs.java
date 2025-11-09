package Main.dialogs;

import Main.MainGUI;
import Main.UsersGUI;
import Main.utils.GUIUtils;
import Main.utils.TableUtils;
import Main.utils.ValidatorUtils;
import model.*;
import Sevice.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.List;

public class VeDialogs {
    private MainGUI mainGUI;
    private QuanLyBanVeMayBay quanLy;
    private JTable tableVe;

    public VeDialogs(MainGUI mainGUI, QuanLyBanVeMayBay quanLy, JTable tableVe) {
        this.mainGUI = mainGUI;
        this.quanLy = quanLy;
        this.tableVe = tableVe;
    }

    // ========== DIALOG ĐẶT VÉ MỚI ==========
    // ========== DIALOG ĐẶT VÉ MỚI ==========
    public void moDialogDatVe() {
        UsersGUI.showDangNhap(quanLy);
        mainGUI.dispose();
    }

    private void handleDatVe(JDialog dialog, JComboBox<String> cbChuyenBay, JTextField txtHoTen,
            JTextField txtCMND, JTextField txtSoGhe, JComboBox<String> cbLoaiVe,
            JTextField txtMaVe, Runnable updateVeInfo) {

        if (!validateDatVe(dialog, cbChuyenBay, txtHoTen, txtCMND, txtSoGhe)) {
            return;
        }

        try {
            // Lấy thông tin từ form
            String maVe = txtMaVe.getText().trim();
            String selectedChuyen = (String) cbChuyenBay.getSelectedItem();
            String maChuyen = selectedChuyen.split(" - ")[0];
            String loaiVe = (String) cbLoaiVe.getSelectedItem();
            String hoTen = txtHoTen.getText().trim();
            String cmnd = txtCMND.getText().trim();
            String soGhe = txtSoGhe.getText().trim();

            // Lấy thông tin chuyến bay
            ChuyenBay chuyenBay = quanLy.getDsChuyenBay().timKiemTheoMa(maChuyen);
            if (chuyenBay == null) {
                ValidatorUtils.showErrorDialog(dialog, "Không tìm thấy thông tin chuyến bay!");
                return;
            }

            // Kiểm tra số ghế hợp lệ
            if (!ValidatorUtils.isValidSoGhe(soGhe)) {
                ValidatorUtils.showErrorDialog(dialog, "Số ghế không hợp lệ! Format: 1A, 12B, 25C");
                return;
            }

            // Kiểm tra ghế đã có người đặt chưa
            boolean gheDaCo = quanLy.getDsVe().timKiemTheoChuyenBay(maChuyen)
                    .stream()
                    .anyMatch(ve -> ve.getSoGhe().equals(soGhe));

            if (gheDaCo) {
                ValidatorUtils.showErrorDialog(dialog, "Số ghế " + soGhe + " đã có người đặt trong chuyến bay này!");
                return;
            }
            DanhSachKhachHang dsKH = quanLy.getDsKhachHang();
            // Tạo vé mới
            VeMayBay veMoi = taoVeTheoLoai(dsKH.timKiemTheoCMND(cmnd).getMa(), maVe, soGhe, loaiVe, chuyenBay);

            if (veMoi == null) {
                ValidatorUtils.showErrorDialog(dialog, "Không thể tạo vé với loại: " + loaiVe);
                return;
            }

            // Thêm vào danh sách
            quanLy.getDsVe().them(veMoi);

            // Cập nhật số ghế trống
            chuyenBay.setSoGheTrong(chuyenBay.getSoGheTrong() - 1);

            // Hiển thị thông báo thành công
            String message = String.format(
                    "Đặt vé thành công!\n\n" +
                            "Mã vé: %s\n" +
                            "Hành khách: %s\n" +
                            "CMND: %s\n" +
                            "Chuyến bay: %s\n" +
                            "Số ghế: %s\n" +
                            "Loại vé: %s\n" +
                            "Giá vé: %s VND",
                    maVe, hoTen, cmnd, maChuyen, soGhe, loaiVe,
                    String.format("%,.0f", veMoi.getGiaVe()));

            ValidatorUtils.showSuccessDialog(dialog, message);

            // Đóng dialog và cập nhật giao diện
            dialog.dispose();
            mainGUI.capNhatDuLieuGUI();

        } catch (Exception ex) {
            ValidatorUtils.showErrorDialog(dialog, "Lỗi: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void resetForm(JTextField txtMaVe, JComboBox<String> cbChuyenBay,
            JComboBox<String> cbLoaiVe, JComboBox<String> cbKhachHang,
            JTextField txtHoTen, JTextField txtCMND, JTextField txtSoGhe) {

        // Tạo mã vé mới
        int soVeMoi = quanLy.getDsVe().demSoLuong();
        String maVeMoi = "VE" + String.format("%04d", soVeMoi + 1);
        txtMaVe.setText(maVeMoi);

        // Reset các field
        cbChuyenBay.setSelectedIndex(0);
        cbLoaiVe.setSelectedIndex(0);
        cbKhachHang.setSelectedIndex(0);
        txtHoTen.setText("");
        txtCMND.setText("");
        txtSoGhe.setText("");
    }

    private boolean validateDatVe(JDialog dialog, JComboBox<String> cbChuyenBay,
            JTextField txtHoTen, JTextField txtCMND, JTextField txtSoGhe) {

        if (cbChuyenBay.getSelectedIndex() < 0 || cbChuyenBay.getSelectedItem().toString().contains("-- Không có")) {
            ValidatorUtils.showErrorDialog(dialog, "Vui lòng chọn chuyến bay!");
            return false;
        }

        if (!ValidatorUtils.validateRequiredFields(dialog, txtHoTen, txtCMND, txtSoGhe)) {
            return false;
        }

        if (!ValidatorUtils.isValidCMND(txtCMND.getText().trim())) {
            ValidatorUtils.showErrorDialog(dialog, "CMND/CCCD không hợp lệ! Phải có 9 hoặc 12 số.");
            return false;
        }

        return true;
    }

    private double tinhGiaVe(String loaiVe, double giaCoBan) {
        switch (loaiVe) {
            case "Thương gia":
                return giaCoBan * 2.5; // Giá gấp 2.5 lần
            case "Phổ thông":
                return giaCoBan * 1.5; // Giá gấp 1.5 lần
            case "Tiết kiệm":
                return giaCoBan; // Giá cơ bản
            default:
                return giaCoBan;
        }
    }

    private VeMayBay taoVeTheoLoai(String maKH, String maVe, String soGhe, String loaiVe, ChuyenBay chuyenBay) {
        double giaVe = tinhGiaVe(loaiVe, chuyenBay.getGiaCoBan());

        switch (loaiVe) {
            case "Thương gia":
                return new VeThuongGia(maKH, maVe, chuyenBay.getGioKhoiHanh(), giaVe,
                        chuyenBay.getMaChuyen(), soGhe,
                        "VIP", 500000, 40, true, "Rượu vang");
            case "Phổ thông":
                return new VePhoThong(maKH, maVe, chuyenBay.getGioKhoiHanh(), giaVe,
                        chuyenBay.getMaChuyen(), soGhe,
                        true, 20, 200000, "Cửa sổ", true);
            case "Tiết kiệm":
                return new VeTietKiem(maKH, maVe, chuyenBay.getGioKhoiHanh(), giaVe,
                        chuyenBay.getMaChuyen(), soGhe,
                        48, 0.1, true, 100000, "Không hoàn hủy");
            default:
                return null;
        }
    }

    // ========== DIALOG TÌM KIẾM VÉ ĐA TIÊU CHÍ ==========
public void moDialogTimKiemVe() {
    JDialog dialog = new JDialog(mainGUI, "Tìm Kiếm Vé Đa Tiêu Chí", true);
    dialog.setSize(1200, 800);
    dialog.setLocationRelativeTo(mainGUI);
    dialog.setLayout(new BorderLayout());

    JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

    // === PANEL ĐIỀU KIỆN TÌM KIẾM ===
    JPanel panelTimKiem = new JPanel(new GridBagLayout());
    panelTimKiem.setBorder(BorderFactory.createTitledBorder("ĐIỀU KIỆN TÌM KIẾM"));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 10, 5, 10);
    gbc.gridx = 0;
    gbc.gridy = 0;

    // Tạo các component tìm kiếm
    JTextField txtMaVe = new JTextField(15);
    JTextField txtMaKhachHang = new JTextField(15);
    JTextField txtTenKhachHang = new JTextField(15);
    JTextField txtCMND = new JTextField(15);
    JTextField txtMaChuyen = new JTextField(15);
    
    // ComboBox cho điểm đi và điểm đến
    JComboBox<String> cbDiemDi = new JComboBox<>(loadDiemDiItems());
    JComboBox<String> cbDiemDen = new JComboBox<>(loadDiemDenItems());
    
    JTextField txtSoGhe = new JTextField(10);
    JComboBox<String> cbLoaiVe = new JComboBox<>(new String[]{"Tất cả", "Thương gia", "Phổ thông", "Tiết kiệm"});
    JComboBox<String> cbTrangThai = new JComboBox<>(new String[]{"Tất cả", "Đã đặt", "Đã thanh toán", "Đã hủy", "Đã bay"});
    
    // Khoảng giá
    JTextField txtGiaMin = new JTextField(10);
    JTextField txtGiaMax = new JTextField(10);
    
    // Khoảng ngày đặt và ngày bay
    SpinnerDateModel modelTuNgayDat = new SpinnerDateModel();
    JSpinner spinnerTuNgayDat = new JSpinner(modelTuNgayDat);
    JSpinner.DateEditor editorTuNgayDat = new JSpinner.DateEditor(spinnerTuNgayDat, "dd/MM/yyyy");
    spinnerTuNgayDat.setEditor(editorTuNgayDat);

    SpinnerDateModel modelDenNgayDat = new SpinnerDateModel();
    JSpinner spinnerDenNgayDat = new JSpinner(modelDenNgayDat);
    JSpinner.DateEditor editorDenNgayDat = new JSpinner.DateEditor(spinnerDenNgayDat, "dd/MM/yyyy");
    spinnerDenNgayDat.setEditor(editorDenNgayDat);

    SpinnerDateModel modelTuNgayBay = new SpinnerDateModel();
    JSpinner spinnerTuNgayBay = new JSpinner(modelTuNgayBay);
    JSpinner.DateEditor editorTuNgayBay = new JSpinner.DateEditor(spinnerTuNgayBay, "dd/MM/yyyy");
    spinnerTuNgayBay.setEditor(editorTuNgayBay);

    SpinnerDateModel modelDenNgayBay = new SpinnerDateModel();
    JSpinner spinnerDenNgayBay = new JSpinner(modelDenNgayBay);
    JSpinner.DateEditor editorDenNgayBay = new JSpinner.DateEditor(spinnerDenNgayBay, "dd/MM/yyyy");
    spinnerDenNgayBay.setEditor(editorDenNgayBay);

    // Thiết lập ngày mặc định
    Calendar cal = Calendar.getInstance();
    spinnerTuNgayDat.setValue(cal.getTime());
    spinnerDenNgayDat.setValue(cal.getTime());
    spinnerTuNgayBay.setValue(cal.getTime());
    spinnerDenNgayBay.setValue(cal.getTime());

    // Tạo panel chứa các điều kiện tìm kiếm - chia thành 3 cột
    JPanel panelDieuKien = new JPanel(new GridLayout(0, 3, 15, 8));
    panelDieuKien.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

    // Cột 1 - Thông tin vé
    JPanel col1 = new JPanel(new GridBagLayout());
    GridBagConstraints gbc1 = new GridBagConstraints();
    gbc1.fill = GridBagConstraints.HORIZONTAL;
    gbc1.insets = new Insets(3, 3, 3, 3);
    gbc1.gridx = 0;
    gbc1.gridy = 0;

    GUIUtils.addFormRow(col1, gbc1, "Mã vé:", txtMaVe);
    GUIUtils.addFormRow(col1, gbc1, "Mã chuyến bay:", txtMaChuyen);
    GUIUtils.addFormRow(col1, gbc1, "Loại vé:", cbLoaiVe);
    GUIUtils.addFormRow(col1, gbc1, "Trạng thái:", cbTrangThai);

    // Cột 2 - Thông tin khách hàng
    JPanel col2 = new JPanel(new GridBagLayout());
    GridBagConstraints gbc2 = new GridBagConstraints();
    gbc2.fill = GridBagConstraints.HORIZONTAL;
    gbc2.insets = new Insets(3, 3, 3, 3);
    gbc2.gridx = 0;
    gbc2.gridy = 0;

    GUIUtils.addFormRow(col2, gbc2, "Mã KH:", txtMaKhachHang);
    GUIUtils.addFormRow(col2, gbc2, "Tên KH:", txtTenKhachHang);
    GUIUtils.addFormRow(col2, gbc2, "CMND/CCCD:", txtCMND);

    // Cột 3 - Thông tin chuyến bay và giá
    JPanel col3 = new JPanel(new GridBagLayout());
    GridBagConstraints gbc3 = new GridBagConstraints();
    gbc3.fill = GridBagConstraints.HORIZONTAL;
    gbc3.insets = new Insets(3, 3, 3, 3);
    gbc3.gridx = 0;
    gbc3.gridy = 0;

    GUIUtils.addFormRow(col3, gbc3, "Điểm đi:", cbDiemDi);
    GUIUtils.addFormRow(col3, gbc3, "Điểm đến:", cbDiemDen);

    // Panel khoảng giá
    JPanel panelGia = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
    panelGia.add(txtGiaMin);
    panelGia.add(new JLabel(" - "));
    panelGia.add(txtGiaMax);
    panelGia.add(new JLabel(" VND"));
    GUIUtils.addFormRow(col3, gbc3, "Khoảng giá:", panelGia);

    panelDieuKien.add(col1);
    panelDieuKien.add(col2);
    panelDieuKien.add(col3);

    // Panel ngày đặt và ngày bay
    JPanel panelNgay = new JPanel(new GridLayout(2, 2, 10, 5));
    panelNgay.setBorder(BorderFactory.createTitledBorder("LỌC THEO THỜI GIAN"));

    // Ngày đặt vé
    JPanel panelNgayDat = new JPanel(new GridBagLayout());
    GridBagConstraints gbcNgayDat = new GridBagConstraints();
    gbcNgayDat.insets = new Insets(2, 2, 2, 2);
    gbcNgayDat.gridx = 0;
    gbcNgayDat.gridy = 0;
    
    panelNgayDat.add(new JLabel("Từ ngày:"), gbcNgayDat);
    gbcNgayDat.gridx = 1;
    panelNgayDat.add(spinnerTuNgayDat, gbcNgayDat);
    gbcNgayDat.gridx = 0;
    gbcNgayDat.gridy = 1;
    panelNgayDat.add(new JLabel("Đến ngày:"), gbcNgayDat);
    gbcNgayDat.gridx = 1;
    panelNgayDat.add(spinnerDenNgayDat, gbcNgayDat);

    // Ngày bay
    JPanel panelNgayBay = new JPanel(new GridBagLayout());
    GridBagConstraints gbcNgayBay = new GridBagConstraints();
    gbcNgayBay.insets = new Insets(2, 2, 2, 2);
    gbcNgayBay.gridx = 0;
    gbcNgayBay.gridy = 0;
    
    panelNgayBay.add(new JLabel("Từ ngày:"), gbcNgayBay);
    gbcNgayBay.gridx = 1;
    panelNgayBay.add(spinnerTuNgayBay, gbcNgayBay);
    gbcNgayBay.gridx = 0;
    gbcNgayBay.gridy = 1;
    panelNgayBay.add(new JLabel("Đến ngày:"), gbcNgayBay);
    gbcNgayBay.gridx = 1;
    panelNgayBay.add(spinnerDenNgayBay, gbcNgayBay);

    panelNgay.add(new JPanel() {{add(new JLabel("Ngày đặt vé"));}});
    panelNgay.add(new JPanel() {{add(new JLabel("Ngày bay"));}});
    panelNgay.add(panelNgayDat);
    panelNgay.add(panelNgayBay);

    // Thêm các panel vào panel tìm kiếm chính
    gbc.gridwidth = 3;
    panelTimKiem.add(panelDieuKien, gbc);
    gbc.gridy++;
    panelTimKiem.add(panelNgay, gbc);

    // === PANEL KẾT QUẢ TÌM KIẾM ===
    JPanel panelKetQua = new JPanel(new BorderLayout());
    panelKetQua.setBorder(BorderFactory.createTitledBorder("KẾT QUẢ TÌM KIẾM"));
    panelKetQua.setPreferredSize(new Dimension(1100, 350));
    
    DefaultTableModel modelKetQua = TableUtils.createVeTableModel();
    JTable tableKetQua = new JTable(modelKetQua);
    tableKetQua.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tableKetQua.setRowHeight(25);
    tableKetQua.setAutoCreateRowSorter(true); // Cho phép sắp xếp
    JScrollPane scrollKetQua = new JScrollPane(tableKetQua);
    panelKetQua.add(scrollKetQua, BorderLayout.CENTER);

    // Label hiển thị số kết quả và tổng giá trị
    JLabel lblSoKetQua = new JLabel("Tìm thấy: 0 vé");
    lblSoKetQua.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    lblSoKetQua.setFont(new Font("Arial", Font.BOLD, 12));
    panelKetQua.add(lblSoKetQua, BorderLayout.SOUTH);

    // === PANEL BUTTON ===
    JPanel panelButton = new JPanel(new FlowLayout());
    JButton btnTimKiem = new JButton("Tìm Kiếm");
    JButton btnXemTatCa = new JButton("Xem Tất Cả");
    JButton btnLamMoi = new JButton("Làm Mới");
    JButton btnDong = new JButton("Đóng");

    // Thiết lập màu sắc cho button
    btnTimKiem.setBackground(new Color(70, 130, 180));
    btnTimKiem.setForeground(Color.WHITE);
    btnXemTatCa.setBackground(new Color(60, 179, 113));
    btnXemTatCa.setForeground(Color.WHITE);
    btnLamMoi.setBackground(new Color(220, 20, 60));
    btnLamMoi.setForeground(Color.WHITE);

    // Thiết lập kích thước button
    Dimension btnSize = new Dimension(120, 30);
    btnTimKiem.setPreferredSize(btnSize);
    btnXemTatCa.setPreferredSize(btnSize);
    btnLamMoi.setPreferredSize(btnSize);
    btnDong.setPreferredSize(btnSize);

    panelButton.add(btnTimKiem);
    panelButton.add(btnXemTatCa);
    panelButton.add(btnLamMoi);
    panelButton.add(btnDong);

    // === XỬ LÝ SỰ KIỆN ===
    
    // Xử lý tìm kiếm
    btnTimKiem.addActionListener(e -> {
        Map<String, Object> filters = taoFiltersTimKiemDaTieuChi(
            txtMaVe, txtMaKhachHang, txtTenKhachHang, txtCMND,
            txtMaChuyen, cbDiemDi, cbDiemDen, txtSoGhe, 
            cbLoaiVe, cbTrangThai, txtGiaMin, txtGiaMax,
            spinnerTuNgayDat, spinnerDenNgayDat, 
            spinnerTuNgayBay, spinnerDenNgayBay
        );
        
        List<VeMayBay> ketQua = quanLy.getDsVe().timKiemDaTieuChi(filters);
        hienThiKetQuaTimKiem(modelKetQua, ketQua, lblSoKetQua);
    });

    // Xử lý xem tất cả
    btnXemTatCa.addActionListener(e -> {
        List<VeMayBay> tatCaVe = quanLy.getDsVe().getDanhSach();
        hienThiKetQuaTimKiem(modelKetQua, tatCaVe, lblSoKetQua);
    });


    // Xử lý làm mới
    btnLamMoi.addActionListener(e -> {
        lamMoiFormTimKiem(txtMaVe, txtMaKhachHang, txtTenKhachHang, txtCMND, 
                         txtMaChuyen, cbDiemDi, cbDiemDen, txtSoGhe, 
                         cbLoaiVe, cbTrangThai, txtGiaMin, txtGiaMax,
                         spinnerTuNgayDat, spinnerDenNgayDat, 
                         spinnerTuNgayBay, spinnerDenNgayBay);
        ValidatorUtils.showSuccessDialog(dialog, "Đã làm mới form tìm kiếm!");
    });

    // Xử lý đóng
    btnDong.addActionListener(e -> dialog.dispose());

    // === SẮP XẾP LAYOUT ===
    mainPanel.add(panelTimKiem, BorderLayout.NORTH);
    mainPanel.add(panelKetQua, BorderLayout.CENTER);
    mainPanel.add(panelButton, BorderLayout.SOUTH);

    dialog.add(mainPanel);
    
    // Hiển thị tất cả vé khi mở dialog
    List<VeMayBay> tatCaVe = quanLy.getDsVe().getDanhSach();
    hienThiKetQuaTimKiem(modelKetQua, tatCaVe, lblSoKetQua);
    
    dialog.setVisible(true);
}

// ========== CÁC PHƯƠNG THỨC HỖ TRỢ TÌM KIẾM ĐA TIÊU CHÍ ==========

private Vector<String> loadDiemDiItems() {
    Vector<String> items = new Vector<>();
    items.add("Tất cả");
    
    DanhSachChuyenBay dsChuyenBay = quanLy.getDsChuyenBay();
    if (dsChuyenBay != null && dsChuyenBay.getDanhSachChuyenBay() != null) {
        for (ChuyenBay cb : dsChuyenBay.getDanhSachChuyenBay()) {
            if (!items.contains(cb.getDiemDi())) {
                items.add(cb.getDiemDi());
            }
        }
    }
    return items;
}

private Vector<String> loadDiemDenItems() {
    Vector<String> items = new Vector<>();
    items.add("Tất cả");
    
    DanhSachChuyenBay dsChuyenBay = quanLy.getDsChuyenBay();
    if (dsChuyenBay != null && dsChuyenBay.getDanhSachChuyenBay() != null) {
        for (ChuyenBay cb : dsChuyenBay.getDanhSachChuyenBay()) {
            if (!items.contains(cb.getDiemDen())) {
                items.add(cb.getDiemDen());
            }
        }
    }
    return items;
}

private Map<String, Object> taoFiltersTimKiemDaTieuChi(JTextField txtMaVe, JTextField txtMaKhachHang,
                                                      JTextField txtTenKhachHang, JTextField txtCMND,
                                                      JTextField txtMaChuyen, JComboBox<String> cbDiemDi,
                                                      JComboBox<String> cbDiemDen, JTextField txtSoGhe,
                                                      JComboBox<String> cbLoaiVe, JComboBox<String> cbTrangThai,
                                                      JTextField txtGiaMin, JTextField txtGiaMax,
                                                      JSpinner spinnerTuNgayDat, JSpinner spinnerDenNgayDat,
                                                      JSpinner spinnerTuNgayBay, JSpinner spinnerDenNgayBay) {
    
    Map<String, Object> filters = new HashMap<>();
    
    // Lọc theo mã vé
    if (!txtMaVe.getText().trim().isEmpty()) {
        filters.put("maVe", txtMaVe.getText().trim());
    }
    
    // Lọc theo mã khách hàng
    if (!txtMaKhachHang.getText().trim().isEmpty()) {
        filters.put("maKhachHang", txtMaKhachHang.getText().trim());
    }
    
    // Lọc theo tên khách hàng
    if (!txtTenKhachHang.getText().trim().isEmpty()) {
        filters.put("tenKhachHang", txtTenKhachHang.getText().trim());
    }
    
    // Lọc theo CMND
    if (!txtCMND.getText().trim().isEmpty()) {
        filters.put("cmnd", txtCMND.getText().trim());
    }
    
    // Lọc theo mã chuyến bay
    if (!txtMaChuyen.getText().trim().isEmpty()) {
        filters.put("maChuyen", txtMaChuyen.getText().trim());
    }
    
    // Lọc theo điểm đi
    String diemDi = (String) cbDiemDi.getSelectedItem();
    if (!"Tất cả".equals(diemDi)) {
        filters.put("diemDi", diemDi);
    }
    
    // Lọc theo điểm đến
    String diemDen = (String) cbDiemDen.getSelectedItem();
    if (!"Tất cả".equals(diemDen)) {
        filters.put("diemDen", diemDen);
    }
    
    // Lọc theo số ghế
    if (!txtSoGhe.getText().trim().isEmpty()) {
        filters.put("soGhe", txtSoGhe.getText().trim());
    }
    
    // Lọc theo loại vé
    String loaiVe = (String) cbLoaiVe.getSelectedItem();
    if (!"Tất cả".equals(loaiVe)) {
        filters.put("loaiVe", loaiVe);
    }
    
    // Lọc theo trạng thái
    String trangThai = (String) cbTrangThai.getSelectedItem();
    if (!"Tất cả".equals(trangThai)) {
        String trangThaiCode = chuyenTrangThaiSangCode(trangThai);
        filters.put("trangThai", trangThaiCode);
    }
    
    // Lọc theo khoảng giá
    try {
        if (!txtGiaMin.getText().trim().isEmpty()) {
            double giaMin = Double.parseDouble(txtGiaMin.getText().trim().replaceAll("[^\\d.]", ""));
            filters.put("giaMin", giaMin);
        }
        if (!txtGiaMax.getText().trim().isEmpty()) {
            double giaMax = Double.parseDouble(txtGiaMax.getText().trim().replaceAll("[^\\d.]", ""));
            filters.put("giaMax", giaMax);
        }
    } catch (NumberFormatException ex) {
        ValidatorUtils.showErrorDialog(mainGUI, "Giá tiền không hợp lệ!");
    }
    
    // Lọc theo khoảng ngày đặt
    try {
        Date tuNgayDat = (Date) spinnerTuNgayDat.getValue();
        Date denNgayDat = (Date) spinnerDenNgayDat.getValue();
        
        if (tuNgayDat != null && denNgayDat != null) {
            // Đặt thời gian cho denNgay là 23:59:59 để bao gồm cả ngày đó
            Calendar cal = Calendar.getInstance();
            cal.setTime(denNgayDat);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            Date denNgayDatCuoi = (Date) cal.getTime();
            
            filters.put("tuNgayDat", tuNgayDat);
            filters.put("denNgayDat", denNgayDatCuoi);
        }
    } catch (Exception ex) {
        System.err.println("Lỗi xử lý ngày đặt: " + ex.getMessage());
    }
    
    // Lọc theo khoảng ngày bay
    try {
        Date tuNgayBay = (Date) spinnerTuNgayBay.getValue();
        Date denNgayBay = (Date) spinnerDenNgayBay.getValue();
        
        if (tuNgayBay != null && denNgayBay != null) {
            // Đặt thời gian cho denNgay là 23:59:59 để bao gồm cả ngày đó
            Calendar cal = Calendar.getInstance();
            cal.setTime(denNgayBay);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            Date denNgayBayCuoi = (Date) cal.getTime();
            
            filters.put("tuNgayBay", tuNgayBay);
            filters.put("denNgayBay", denNgayBayCuoi);
        }
    } catch (Exception ex) {
        System.err.println("Lỗi xử lý ngày bay: " + ex.getMessage());
    }
    
    return filters;
}

private void lamMoiFormTimKiem(JTextField txtMaVe, JTextField txtMaKhachHang, JTextField txtTenKhachHang,
                              JTextField txtCMND, JTextField txtMaChuyen, JComboBox<String> cbDiemDi,
                              JComboBox<String> cbDiemDen, JTextField txtSoGhe, JComboBox<String> cbLoaiVe,
                              JComboBox<String> cbTrangThai, JTextField txtGiaMin, JTextField txtGiaMax,
                              JSpinner spinnerTuNgayDat, JSpinner spinnerDenNgayDat,
                              JSpinner spinnerTuNgayBay, JSpinner spinnerDenNgayBay) {
    
    // Reset text fields
    txtMaVe.setText("");
    txtMaKhachHang.setText("");
    txtTenKhachHang.setText("");
    txtCMND.setText("");
    txtMaChuyen.setText("");
    txtSoGhe.setText("");
    txtGiaMin.setText("");
    txtGiaMax.setText("");
    
    // Reset combo boxes
    cbDiemDi.setSelectedIndex(0);
    cbDiemDen.setSelectedIndex(0);
    cbLoaiVe.setSelectedIndex(0);
    cbTrangThai.setSelectedIndex(0);
    
    // Reset date spinners to current date
    Calendar cal = Calendar.getInstance();
    spinnerTuNgayDat.setValue(cal.getTime());
    spinnerDenNgayDat.setValue(cal.getTime());
    spinnerTuNgayBay.setValue(cal.getTime());
    spinnerDenNgayBay.setValue(cal.getTime());
}

private String chuyenTrangThaiSangCode(String trangThai) {
    switch (trangThai) {
        case "Đã đặt": return "DaDat";
        case "Đã thanh toán": return "DaThanhToan";
        case "Đã hủy": return "DaHuy";
        case "Đã bay": return "DaBay";
        default: return trangThai;
    }
}

private void hienThiKetQuaTimKiem(DefaultTableModel model, List<VeMayBay> danhSach, JLabel lblSoKetQua) {
    TableUtils.hienThiKetQuaTimKiemVe(model, danhSach, quanLy);
    
    // Tính tổng giá trị
    double tongGiaTri = danhSach.stream()
            .mapToDouble(VeMayBay::getGiaVe)
            .sum();
    
    lblSoKetQua.setText(String.format("Tìm thấy: %d vé - Tổng giá trị: %,d VND", 
                                     danhSach.size(), (long)tongGiaTri));
    
    // Đổi màu label theo số lượng kết quả
    if (danhSach.size() == 0) {
        lblSoKetQua.setForeground(Color.RED);
    } else if (danhSach.size() < 10) {
        lblSoKetQua.setForeground(Color.ORANGE);
    } else {
        lblSoKetQua.setForeground(new Color(0, 128, 0));
    }
}
}