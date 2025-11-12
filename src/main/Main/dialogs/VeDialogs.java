package Main.dialogs;

import Main.MainGUI;
import Main.UsersGUI;
import Main.utils.GUIUtils;
import Main.utils.TableUtils;
import Main.utils.ValidatorUtils;
import model.*;
import Sevice.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.List;

import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;

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
    public void moDialogDatVe() {
        JDialog dialog = new JDialog(mainGUI, "Admin - Tạo Vé Mới", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(mainGUI);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Components
        JTextField txtMaKH = new JTextField(15);
        JTextField txtMaChuyen = new JTextField(15);
        JTextField txtSoGhe = new JTextField(5);
        JComboBox<String> cbLoaiVe = new JComboBox<>(new String[]{"VeThuongGia", "VePhoThong", "VeTietKiem"});

        // Gợi ý: Có thể thay txtMaKH và txtMaChuyen bằng JComboBox
        // để admin chọn từ danh sách thay vì gõ tay.

        GUIUtils.addFormRow(panel, gbc, "Mã Khách Hàng:", txtMaKH);
        GUIUtils.addFormRow(panel, gbc, "Mã Chuyến Bay:", txtMaChuyen);
        GUIUtils.addFormRow(panel, gbc, "Số Ghế (vd: 12A):", txtSoGhe);
        GUIUtils.addFormRow(panel, gbc, "Loại Vé:", cbLoaiVe);

        JButton btnTaoVe = new JButton("Tạo Vé");
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnTaoVe, gbc);

        dialog.add(panel, BorderLayout.CENTER);

        btnTaoVe.addActionListener(e -> {
            try {
                String maKH = txtMaKH.getText().trim();
                String maChuyen = txtMaChuyen.getText().trim();
                String soGhe = txtSoGhe.getText().trim().toUpperCase();
                String loaiVe = (String) cbLoaiVe.getSelectedItem();

                // --- Validation (Kiểm tra dữ liệu) ---
                if (maKH.isEmpty() || maChuyen.isEmpty() || soGhe.isEmpty()) {
                    ValidatorUtils.showErrorDialog(dialog, "Vui lòng nhập đầy đủ thông tin.");
                    return;
                }

                KhachHang kh = quanLy.getDsKhachHang().timKiemTheoMa(maKH); //
                if (kh == null) {
                    ValidatorUtils.showErrorDialog(dialog, "Lỗi: Mã khách hàng không tồn tại.");
                    return;
                }

                ChuyenBay cb = quanLy.getDsChuyenBay().timKiemTheoMa(maChuyen); //
                if (cb == null) {
                    ValidatorUtils.showErrorDialog(dialog, "Lỗi: Mã chuyến bay không tồn tại.");
                    return;
                }

                if (!ValidatorUtils.isValidSoGhe(soGhe)) { //
                    ValidatorUtils.showErrorDialog(dialog, "Lỗi: Số ghế không hợp lệ (ví dụ: 12A).");
                    return;
                }

                if (cb.kiemTraGheDaDat(soGhe)) { //
                    ValidatorUtils.showErrorDialog(dialog, "Lỗi: Ghế " + soGhe + " đã có người đặt trên chuyến bay này.");
                    return;
                }

                // --- Tạo vé ---
                String maVe = loaiVe.substring(2, 4).toUpperCase() + String.format("%03d", quanLy.getDsVe().demSoLuong() + 1);
                double giaVe = cb.getGiaCoBan(); // Admin tạo vé với giá cơ bản

                VeMayBay veMoi = null;
                switch (loaiVe) {
                    case "VeThuongGia":
                        // Admin tạo vé thương gia với các giá trị mặc định
                        veMoi = new VeThuongGia(maKH, maVe, cb.getGioKhoiHanh(), giaVe * 2.0, maChuyen, soGhe, "Phòng chờ VIP", 500000, true, 20, "Rượu vang"); //
                        break;
                    case "VePhoThong":
                        veMoi = new VePhoThong(maKH, maVe, cb.getGioKhoiHanh(), giaVe * 1.2, maChuyen, soGhe, true, 7, "Cửa sổ", true); //
                        break;
                    case "VeTietKiem":
                        veMoi = new VeTietKiem(maKH, maVe, cb.getGioKhoiHanh(), giaVe * 0.9, maChuyen, soGhe, true); //
                        break;
                }

                if (veMoi != null) {
                    quanLy.getDsVe().them(veMoi); //
                    cb.datGhe(); // Giảm số ghế trống
                    quanLy.ghiDuLieuRaFile(); //

                    ValidatorUtils.showSuccessDialog(dialog, "Tạo vé thành công! Mã vé: " + maVe);
                    dialog.dispose();
                    mainGUI.capNhatTableVe(); //
                    mainGUI.capNhatTableChuyenBay(); //
                }

            } catch (Exception ex) {
                ValidatorUtils.showExceptionDialog(dialog, "Lỗi khi tạo vé:", ex);
            }
        });

        dialog.setVisible(true);
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
        JComboBox<String> cbLoaiVe = new JComboBox<>(new String[]{"Tất cả", "VeThuongGia", "VePhoThong", "VeTietKiem"});
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

        // Thêm các panel vào panel tìm kiếm chính
        gbc.gridwidth = 3;
        panelTimKiem.add(panelDieuKien, gbc);
        gbc.gridy++;

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
                cbLoaiVe, cbTrangThai, txtGiaMin, txtGiaMax
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
                             cbLoaiVe, cbTrangThai, txtGiaMin, txtGiaMax);
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
                                                          JTextField txtGiaMin, JTextField txtGiaMax) {
        
        Map<String, Object> filters = new HashMap<>();
        
        // Lọc theo mã vé
        if (!txtMaVe.getText().trim().isEmpty()) {
            filters.put("maVe", txtMaVe.getText().trim());
        }
        
        // Lọc theo mã khách hàng
        if (!txtMaKhachHang.getText().trim().isEmpty()) {
            filters.put("maKH", txtMaKhachHang.getText().trim());
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


    
        
        return filters;
    }

    private void lamMoiFormTimKiem(JTextField txtMaVe, JTextField txtMaKhachHang, JTextField txtTenKhachHang,
                                  JTextField txtCMND, JTextField txtMaChuyen, JComboBox<String> cbDiemDi,
                                  JComboBox<String> cbDiemDen, JTextField txtSoGhe, JComboBox<String> cbLoaiVe,
                                  JComboBox<String> cbTrangThai, JTextField txtGiaMin, JTextField txtGiaMax) {
        
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
    }

    private String chuyenTrangThaiSangCode(String trangThai) {
        switch (trangThai) {
            case "Đã đặt": return "ĐÃ_ĐẶT";
            case "Đã thanh toán": return "ĐÃ_THANH_TOÁN";
            case "Đã hủy": return "ĐÃ_HỦY";
            case "Đã bay": return "ĐÃ_BAY";
            default: return trangThai;
        }
    }

    private void hienThiKetQuaTimKiem(DefaultTableModel model, List<VeMayBay> danhSach, JLabel lblSoKetQua) {
        TableUtils.hienThiKetQuaTimKiemVe(model, danhSach, quanLy);
        
        // Tính tổng giá trị
        double tongGiaTri = danhSach.stream().mapToDouble(VeMayBay::getGiaVe).sum();
        
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


    // ========== DIALOG SỬA VÉ (ADMIN) ==========
    public void moDialogSuaVe() {
        int selectedRow = tableVe.getSelectedRow();
        if (selectedRow == -1) {
            ValidatorUtils.showWarningDialog(mainGUI, "Vui lòng chọn một vé để sửa!");
            return;
        }

        String maVe = (String) tableVe.getValueAt(selectedRow, 0);
        VeMayBay veCanSua = quanLy.getDsVe().timKiemTheoMa(maVe);

        if (veCanSua == null) {
            ValidatorUtils.showErrorDialog(mainGUI, "Không tìm thấy vé trong hệ thống!");
            return;
        }

        // Tạo Dialog
        JDialog dialog = new JDialog(mainGUI, "Admin - Sửa Thông Tin Vé: " + maVe, true);
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(mainGUI);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Các trường cho phép sửa
        JTextField txtSoGhe = new JTextField(veCanSua.getSoGhe(), 15);
        JTextField txtGiaVe = new JTextField(String.valueOf(veCanSua.getGiaVe()), 15);

        // Lấy danh sách trạng thái từ model VeMayBay
        String[] trangThaiOptions = {
                VeMayBay.TRANG_THAI_DA_DAT,
                VeMayBay.TRANG_THAI_DA_THANH_TOAN,
                VeMayBay.TRANG_THAI_DA_HUY,
                VeMayBay.TRANG_THAI_DA_BAY
        };
        JComboBox<String> cbTrangThai = new JComboBox<>(trangThaiOptions);
        cbTrangThai.setSelectedItem(veCanSua.getTrangThai());

        GUIUtils.addFormRow(panel, gbc, "Mã Vé:", new JLabel(maVe));
        GUIUtils.addFormRow(panel, gbc, "Số Ghế:", txtSoGhe);
        GUIUtils.addFormRow(panel, gbc, "Giá Vé:", txtGiaVe);
        GUIUtils.addFormRow(panel, gbc, "Trạng Thái:", cbTrangThai);

        JButton btnLuu = new JButton("Lưu Thay Đổi");
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnLuu, gbc);

        dialog.add(panel, BorderLayout.CENTER);

        // Xử lý sự kiện Lưu
        btnLuu.addActionListener(e -> {
            try {
                String soGheMoi = txtSoGhe.getText().trim().toUpperCase();
                double giaVeMoi = Double.parseDouble(txtGiaVe.getText().trim());
                String trangThaiMoi = (String) cbTrangThai.getSelectedItem();

                // Validation
                if (!ValidatorUtils.isValidSoGhe(soGheMoi)) {
                    ValidatorUtils.showErrorDialog(dialog, "Số ghế không hợp lệ (ví dụ: 12A).");
                    return;
                }

                // Kiểm tra xem ghế mới có bị trùng không (trừ chính vé này)
                ChuyenBay cb = quanLy.getDsChuyenBay().timKiemTheoMa(veCanSua.getMaChuyen());
                if (cb.kiemTraGheDaDat(soGheMoi) && !veCanSua.getSoGhe().equals(soGheMoi)) {
                    ValidatorUtils.showErrorDialog(dialog, "Ghế " + soGheMoi + " đã bị đặt trên chuyến bay này!");
                    return;
                }

                // Cập nhật thông tin vé
                veCanSua.setSoGhe(soGheMoi);
                veCanSua.setGiaVe(giaVeMoi);
                veCanSua.setTrangThai(trangThaiMoi);

                // Gọi hàm sua() của DanhSachVeMayBay (dù nó đang trống, nhưng nên có)
                // quanLy.suaVe(maVe, veCanSua);

                // Lưu file
                quanLy.ghiDuLieuRaFile();
                ValidatorUtils.showSuccessDialog(dialog, "Cập nhật vé thành công!");
                dialog.dispose();
                mainGUI.capNhatTableVe();

            } catch (NumberFormatException ex) {
                ValidatorUtils.showErrorDialog(dialog, "Giá vé phải là một con số!");
            } catch (Exception ex) {
                ValidatorUtils.showExceptionDialog(dialog, "Lỗi khi cập nhật vé:", ex);
            }
        });

        dialog.setVisible(true);
    }

    // ========== CHỨC NĂNG XÓA VÉ (ADMIN) ==========
    public void xoaVe() {
        int selectedRow = tableVe.getSelectedRow();
        if (selectedRow == -1) {
            ValidatorUtils.showWarningDialog(mainGUI, "Vui lòng chọn một vé để xóa!");
            return;
        }

        String maVe = (String) tableVe.getValueAt(selectedRow, 0);
        VeMayBay veCanXoa = quanLy.getDsVe().timKiemTheoMa(maVe);

        if (veCanXoa == null) {
            ValidatorUtils.showErrorDialog(mainGUI, "Không tìm thấy vé trong hệ thống!");
            return;
        }

        // Xác nhận
        int confirm = JOptionPane.showConfirmDialog(mainGUI,
                "Bạn có chắc chắn muốn XÓA VĨNH VIỄN vé " + maVe + "?\n" +
                        "Thao tác này KHÔNG THỂ hoàn tác và sẽ giải phóng ghế trên chuyến bay.",
                "Xác nhận xóa vé",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // 1. Giải phóng ghế trên chuyến bay
                ChuyenBay cb = quanLy.getDsChuyenBay().timKiemTheoMa(veCanXoa.getMaChuyen());
                if (cb != null) {
                    // Chỉ hủy (tăng ghế trống) nếu vé chưa bị hủy
                    if (!veCanXoa.isDaHuy()) {
                        cb.huyGhe(); //
                    }
                }

                // 2. Xóa vé khỏi hệ thống
                quanLy.xoaVe(maVe); //

                // 3. (Logic nâng cao): Xóa vé khỏi hóa đơn
                // Code này cần được thêm vào:
                HoaDon hd = quanLy.getDsHoaDon().timHoaDonChuaVe(maVe);
                if (hd != null) {
                    hd.xoaVe(veCanXoa); //
                }

                // 4. Lưu file và cập nhật GUI
                quanLy.ghiDuLieuRaFile();
                ValidatorUtils.showSuccessDialog(mainGUI, "Đã xóa vé " + maVe + " thành công.");
                mainGUI.capNhatTableVe();
                mainGUI.capNhatTableChuyenBay();
                mainGUI.capNhatTableHoaDon();

            } catch (IllegalStateException ex) {
                // Bắt lỗi từ dsVe.xoa() nếu vé đã thanh toán
                ValidatorUtils.showErrorDialog(mainGUI, "Không thể xóa vé: " + ex.getMessage());
            } catch (Exception ex) {
                ValidatorUtils.showExceptionDialog(mainGUI, "Lỗi khi xóa vé:", ex);
            }
        }
    }
    // ========== CHỨC NĂNG LIÊN HỆ KHÁCH HÀNG (ADMIN) ==========
    public void lienHeKhachHang() {
        int selectedRow = tableVe.getSelectedRow();
        if (selectedRow == -1) {
            ValidatorUtils.showWarningDialog(mainGUI, "Vui lòng chọn một vé để liên hệ khách hàng!");
            return;
        }

        // Lấy Mã KH từ cột 1 của bảng vé
        // (Theo TableUtils.createVeTableModel, "Mã KH" là cột 1)
        String maKH = (String) tableVe.getValueAt(selectedRow, 1);

        if (maKH == null || maKH.equals("N/A")) {
            ValidatorUtils.showErrorDialog(mainGUI, "Vé này không liên kết với khách hàng nào (N/A).");
            return;
        }

        // Tìm khách hàng trong danh sách
        KhachHang khachHang = quanLy.getDsKhachHang().timKiemTheoMa(maKH); //

        if (khachHang == null) {
            ValidatorUtils.showErrorDialog(mainGUI, "Không tìm thấy khách hàng với mã " + maKH + " trong hệ thống!");
            return;
        }

        String email = khachHang.getEmail(); //
        if (email == null || email.trim().isEmpty()) {
            ValidatorUtils.showErrorDialog(mainGUI, "Khách hàng " + khachHang.getHoTen() + " không có email.");
            return;
        }

        // Kiểm tra xem Desktop API có được hỗ trợ không
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.MAIL)) {
            try {
                // Tạo một URI mailto: với email và chủ đề (subject)
                String subject = "Thông tin về vé " + tableVe.getValueAt(selectedRow, 0); // Lấy mã vé từ cột 0
                String body = "Xin chào " + khachHang.getHoTen() + ",\n\nChúng tôi liên hệ bạn về vé...\n\n";

                // Mã hóa subject và body để URL hợp lệ
                String subjectEncoded = subject.replace(" ", "%20");
                String bodyEncoded = body.replace(" ", "%20").replace("\n", "%0A");

                URI mailto = new URI("mailto:" + email + "?subject=" + subjectEncoded + "&body=" + bodyEncoded);

                // Mở ứng dụng email mặc định
                Desktop.getDesktop().mail(mailto);

            } catch (URISyntaxException | IOException ex) {
                ValidatorUtils.showExceptionDialog(mainGUI, "Không thể mở ứng dụng email.", ex);
            }
        } else {
            // Nếu không hỗ trợ, hiển thị email để Admin tự copy
            ValidatorUtils.showWarningDialog(mainGUI, "Không thể tự động mở email.\n" +
                    "Email của khách hàng là: " + email + "\n(Vui lòng copy thủ công)");
        }
    }
}