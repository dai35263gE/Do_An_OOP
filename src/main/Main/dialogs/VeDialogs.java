package Main.dialogs;

import Main.MainGUI;
import Main.utils.GUIUtils;
import Main.utils.ValidatorUtils;
import model.*;
import Sevice.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Vector;

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
        JDialog dialog = new JDialog(mainGUI, "Đặt Vé Máy Bay Mới", true);
        dialog.setSize(700, 600);
        dialog.setLocationRelativeTo(mainGUI);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Tự động tạo mã vé
        int soVeHienTai = quanLy.getDsVe().demSoLuong();
        String maVeTuDong = "VE" + String.format("%04d", soVeHienTai + 1);
        JTextField txtMaVe = new JTextField(maVeTuDong, 20);
        txtMaVe.setEditable(false);
        txtMaVe.setBackground(new Color(240, 240, 240));

        // ComboBox chọn chuyến bay
        JComboBox<String> cbChuyenBay = new JComboBox<>(loadChuyenBayItems());
        
        // ComboBox chọn loại vé
        JComboBox<String> cbLoaiVe = new JComboBox<>(new String[]{"Thương gia", "Phổ thông", "Tiết kiệm"});
        
        // Thông tin hành khách
        JTextField txtHoTen = new JTextField(20);
        JTextField txtCMND = new JTextField(20);
        JTextField txtSoGhe = new JTextField(10);
        
        // ComboBox chọn khách hàng (nếu có)
        JComboBox<String> cbKhachHang = new JComboBox<>(loadKhachHangItems());

        // Thêm components vào panel
        GUIUtils.addFormRow(panel, gbc, "Mã vé:", txtMaVe);
        GUIUtils.addFormRow(panel, gbc, "Chuyến bay:*", cbChuyenBay);
        GUIUtils.addFormRow(panel, gbc, "Loại vé:*", cbLoaiVe);
        GUIUtils.addFormRow(panel, gbc, "Khách hàng:", cbKhachHang);
        GUIUtils.addFormRow(panel, gbc, "Họ tên hành khách:*", txtHoTen);
        GUIUtils.addFormRow(panel, gbc, "CMND/CCCD:*", txtCMND);
        GUIUtils.addFormRow(panel, gbc, "Số ghế:*", txtSoGhe);

        // Panel hiển thị thông tin
        JTextArea txtThongTin = createThongTinTextArea();
        JPanel panelThongTin = createThongTinPanel(txtThongTin);

        // Cập nhật thông tin khi thay đổi dữ liệu
        Runnable updateVeInfo = () -> updateVeInfo(txtMaVe, txtHoTen, txtCMND, txtSoGhe, cbLoaiVe, cbChuyenBay, txtThongTin);
        
        // Thêm listeners
        addUpdateListeners(cbChuyenBay, cbLoaiVe, cbKhachHang, txtHoTen, txtCMND, txtSoGhe, 
                          txtHoTen, txtCMND, updateVeInfo);

        // Gọi lần đầu
        updateVeInfo.run();

        // Panel button
        JPanel panelButton = createButtonPanel(dialog, txtMaVe, txtHoTen, txtCMND, txtSoGhe, 
                                             cbChuyenBay, cbLoaiVe, cbKhachHang, updateVeInfo);

        // Thêm các panel vào dialog
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(panel, BorderLayout.NORTH);
        mainPanel.add(panelThongTin, BorderLayout.CENTER);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(panelButton, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }


    // ========== CÁC PHƯƠNG THỨC HỖ TRỢ ==========

    private Vector<String> loadChuyenBayItems() {
        Vector<String> chuyenBayItems = new Vector<>();
        DanhSachChuyenBay dsChuyenBay = quanLy.getDsChuyenBay();
        
        if (dsChuyenBay != null && dsChuyenBay.getDanhSachChuyenBay() != null) {
            for (ChuyenBay cb : dsChuyenBay.getDanhSachChuyenBay()) {
                if (cb.conGheTrong()) {
                    String item = String.format("%s - %s → %s - %s - %,d VND - %d ghế trống",
                            cb.getMaChuyen(), cb.getDiemDi(), cb.getDiemDen(),
                            new SimpleDateFormat("dd/MM/yyyy HH:mm").format(cb.getGioKhoiHanh()),
                            (int) cb.getGiaCoBan(), cb.getSoGheTrong());
                    chuyenBayItems.add(item);
                }
            }
        }
        
        if (chuyenBayItems.isEmpty()) {
            chuyenBayItems.add("-- Không có chuyến bay nào --");
        }
        
        return chuyenBayItems;
    }

    private Vector<String> loadKhachHangItems() {
        Vector<String> khachHangItems = new Vector<>();
        khachHangItems.add("-- Chọn khách hàng --");
        
        DanhSachKhachHang dsKhachHang = quanLy.getDsKhachHang();
        if (dsKhachHang != null && dsKhachHang.getDanhSach() != null) {
            for (KhachHang kh : dsKhachHang.getDanhSach()) {
                String item = String.format("%s - %s - %s", kh.getMa(), kh.getHoTen(), kh.getCmnd());
                khachHangItems.add(item);
            }
        }
        
        return khachHangItems;
    }

    private JTextArea createThongTinTextArea() {
        JTextArea txtThongTin = new JTextArea(6, 40);
        txtThongTin.setEditable(false);
        txtThongTin.setBackground(new Color(240, 240, 240));
        txtThongTin.setMargin(new Insets(10, 10, 10, 10));
        return txtThongTin;
    }

    private JPanel createThongTinPanel(JTextArea txtThongTin) {
        JPanel panelThongTin = new JPanel(new BorderLayout());
        panelThongTin.setBorder(BorderFactory.createTitledBorder("THÔNG TIN VÉ"));
        panelThongTin.add(new JScrollPane(txtThongTin), BorderLayout.CENTER);
        return panelThongTin;
    }

    private void updateVeInfo(JTextField txtMaVe, JTextField txtHoTen, JTextField txtCMND, 
                             JTextField txtSoGhe, JComboBox<String> cbLoaiVe, 
                             JComboBox<String> cbChuyenBay, JTextArea txtThongTin) {
        try {
            String maVe = txtMaVe.getText().trim();
            String hoTen = txtHoTen.getText().trim();
            String cmnd = txtCMND.getText().trim();
            String soGhe = txtSoGhe.getText().trim();
            String loaiVe = (String) cbLoaiVe.getSelectedItem();
            
            StringBuilder info = new StringBuilder("THÔNG TIN VÉ:\n\n");
            info.append("Mã vé: ").append(maVe).append("\n");
            
            if (cbChuyenBay.getSelectedIndex() >= 0 && !cbChuyenBay.getSelectedItem().toString().contains("-- Không có")) {
                String selectedChuyen = (String) cbChuyenBay.getSelectedItem();
                info.append("Chuyến bay: ").append(selectedChuyen.split(" - ")[0]).append("\n");
            }
            
            info.append("Loại vé: ").append(loaiVe).append("\n");
            info.append("Hành khách: ").append(hoTen.isEmpty() ? "..." : hoTen).append("\n");
            info.append("CMND: ").append(cmnd.isEmpty() ? "..." : cmnd).append("\n");
            info.append("Số ghế: ").append(soGhe.isEmpty() ? "..." : soGhe).append("\n");
            
            // Tính giá vé dự kiến
            if (cbChuyenBay.getSelectedIndex() >= 0 && !cbChuyenBay.getSelectedItem().toString().contains("-- Không có")) {
                String selectedChuyen = (String) cbChuyenBay.getSelectedItem();
                String maChuyen = selectedChuyen.split(" - ")[0];
                ChuyenBay cb = quanLy.getDsChuyenBay().timKiemTheoMa(maChuyen);
                if (cb != null) {
                    double giaCoBan = cb.getGiaCoBan();
                    double giaVe = tinhGiaVe(loaiVe, giaCoBan);
                    info.append("Giá vé dự kiến: ").append(String.format("%,.0f VND", giaVe)).append("\n");
                }
            }
            
            txtThongTin.setText(info.toString());
        } catch (Exception ex) {
            txtThongTin.setText("Đang cập nhật thông tin...");
        }
    }

    private void addUpdateListeners(JComboBox<String> cbChuyenBay, JComboBox<String> cbLoaiVe,
                                   JComboBox<String> cbKhachHang, JTextField txtHoTen, 
                                   JTextField txtCMND, JTextField txtSoGhe,
                                   JTextField txtHoTenField, JTextField txtCMNDField, 
                                   Runnable updateVeInfo) {
        
        cbChuyenBay.addActionListener(e -> updateVeInfo.run());
        cbLoaiVe.addActionListener(e -> updateVeInfo.run());
        
        cbKhachHang.addActionListener(e -> {
            if (cbKhachHang.getSelectedIndex() > 0) {
                String selectedKH = (String) cbKhachHang.getSelectedItem();
                String[] parts = selectedKH.split(" - ");
                if (parts.length >= 3) {
                    KhachHang kh = quanLy.getDsKhachHang().timKiemTheoMa(parts[0]);
                    if (kh != null) {
                        txtHoTenField.setText(kh.getHoTen());
                        txtCMNDField.setText(kh.getCmnd());
                    }
                }
            }
            updateVeInfo.run();
        });
        
        DocumentListener docListener = new DocumentListener() {
            public void anyUpdate() { updateVeInfo.run(); }
            public void insertUpdate(DocumentEvent e) { anyUpdate(); }
            public void removeUpdate(DocumentEvent e) { anyUpdate(); }
            public void changedUpdate(DocumentEvent e) { anyUpdate(); }
        };
        
        txtHoTen.getDocument().addDocumentListener(docListener);
        txtCMND.getDocument().addDocumentListener(docListener);
        txtSoGhe.getDocument().addDocumentListener(docListener);
    }

    private JPanel createButtonPanel(JDialog dialog, JTextField txtMaVe, JTextField txtHoTen,
                                    JTextField txtCMND, JTextField txtSoGhe, 
                                    JComboBox<String> cbChuyenBay, JComboBox<String> cbLoaiVe,
                                    JComboBox<String> cbKhachHang, Runnable updateVeInfo) {
        
        JPanel panelButton = new JPanel(new FlowLayout());
        JButton btnDatVe = new JButton("Đặt Vé");
        JButton btnHuy = new JButton("Hủy");
        JButton btnLamMoi = new JButton("Làm Mới");

        btnDatVe.setBackground(new Color(70, 130, 180));
        btnDatVe.setForeground(Color.WHITE);
        btnLamMoi.setBackground(new Color(255, 165, 0));
        btnLamMoi.setForeground(Color.WHITE);

        btnDatVe.addActionListener(e -> handleDatVe(dialog, cbChuyenBay, txtHoTen, txtCMND, txtSoGhe, 
                                                   cbLoaiVe, txtMaVe, updateVeInfo));

        btnLamMoi.addActionListener(e -> {
            resetForm(txtMaVe, cbChuyenBay, cbLoaiVe, cbKhachHang, txtHoTen, txtCMND, txtSoGhe);
            ValidatorUtils.showSuccessDialog(dialog, "Đã làm mới form với mã vé mới!");
        });

        btnHuy.addActionListener(e -> dialog.dispose());

        panelButton.add(btnDatVe);
        panelButton.add(btnLamMoi);
        panelButton.add(btnHuy);

        return panelButton;
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
                String.format("%,.0f", veMoi.getGiaVe())
            );

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
                return new VeThuongGia(maKH,maVe, chuyenBay.getGioKhoiHanh(), giaVe, 
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

    // ========== CÁC PHƯƠNG THỨC KHÁC GIỮ NGUYÊN ==========
    // (giữ nguyên phần sửa vé, xóa vé, v.v.)

    public void xoaVe() {
        // Giữ nguyên implementation cũ
        // ...
    }
}