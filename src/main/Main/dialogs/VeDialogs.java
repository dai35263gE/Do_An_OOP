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
import java.util.List;

public class VeDialogs {
    private MainGUI mainGUI;
    private QuanLyBanVeMayBay quanLy;
    @SuppressWarnings("unused")
    private JTable tableVe;

    public VeDialogs(MainGUI mainGUI, QuanLyBanVeMayBay quanLy, JTable tableVe) {
        this.mainGUI = mainGUI;
        this.quanLy = quanLy;
        this.tableVe = tableVe;
    }

    // ========== DIALOG ĐẶT VÉ MỚI ==========
    public void moDialogDatVe() {
        UsersGUI.showDangNhap(quanLy);
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
                txtMaChuyen, txtSoGhe, 
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
                             txtMaChuyen, txtSoGhe, 
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




    private Map<String, Object> taoFiltersTimKiemDaTieuChi(JTextField txtMaVe, JTextField txtMaKhachHang,
                                                          JTextField txtTenKhachHang, JTextField txtCMND,
                                                          JTextField txtMaChuyen, 
                                                          JTextField txtSoGhe,
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
                                  JTextField txtCMND, JTextField txtMaChuyen, JTextField txtSoGhe, JComboBox<String> cbLoaiVe,
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
}