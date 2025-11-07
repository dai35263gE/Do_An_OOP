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

    // ========== DIALOG SỬA VÉ ==========
public void moDialogSuaVe() {
    int selectedRow = tableVe.getSelectedRow();
    if (selectedRow == -1) {
        ValidatorUtils.showErrorDialog(mainGUI, "Vui lòng chọn vé cần sửa!");
        return;
    }

    String maVe = tableVe.getValueAt(selectedRow, 0).toString();
    VeMayBay veCanSua = quanLy.getDsVe().timKiemTheoMa(maVe);
    
    if (veCanSua == null) {
        ValidatorUtils.showErrorDialog(mainGUI, "Không tìm thấy vé cần sửa!");
        return;
    }

    JDialog dialog = new JDialog(mainGUI, "Sửa Thông Tin Vé", true);
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

    // Hiển thị mã vé (không thể sửa)
    JTextField txtMaVe = new JTextField(veCanSua.getMaVe(), 20);
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
    
    // ComboBox chọn khách hàng
    JComboBox<String> cbKhachHang = new JComboBox<>(loadKhachHangItems());

    // Điền dữ liệu hiện tại vào form
    populateFormData(veCanSua, cbChuyenBay, cbLoaiVe, cbKhachHang, txtHoTen, txtCMND, txtSoGhe);

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
    JPanel panelButton = createSuaVeButtonPanel(dialog, veCanSua, txtMaVe, txtHoTen, txtCMND, txtSoGhe, 
                                               cbChuyenBay, cbLoaiVe, cbKhachHang, updateVeInfo);

    // Thêm các panel vào dialog
    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.add(panel, BorderLayout.NORTH);
    mainPanel.add(panelThongTin, BorderLayout.CENTER);

    dialog.add(mainPanel, BorderLayout.CENTER);
    dialog.add(panelButton, BorderLayout.SOUTH);
    dialog.setVisible(true);
}

private void populateFormData(VeMayBay ve, JComboBox<String> cbChuyenBay, 
                             JComboBox<String> cbLoaiVe, JComboBox<String> cbKhachHang,
                             JTextField txtHoTen, JTextField txtCMND, JTextField txtSoGhe) {
    
    // Chọn chuyến bay
    for (int i = 0; i < cbChuyenBay.getItemCount(); i++) {
        String item = cbChuyenBay.getItemAt(i);
        if (item.startsWith(ve.getMaChuyen())) {
            cbChuyenBay.setSelectedIndex(i);
            break;
        }
    }

    // Chọn loại vé
    if (ve instanceof VeThuongGia) {
        cbLoaiVe.setSelectedItem("Thương gia");
    } else if (ve instanceof VePhoThong) {
        cbLoaiVe.setSelectedItem("Phổ thông");
    } else if (ve instanceof VeTietKiem) {
        cbLoaiVe.setSelectedItem("Tiết kiệm");
    }

    // Chọn khách hàng và điền thông tin
    DanhSachKhachHang dsKH = quanLy.getDsKhachHang();
    KhachHang kh = dsKH.timKiemTheoMa(ve.getmaKH());
    if (kh != null) {
        for (int i = 0; i < cbKhachHang.getItemCount(); i++) {
            String item = cbKhachHang.getItemAt(i);
            if (item.startsWith(kh.getMa())) {
                cbKhachHang.setSelectedIndex(i);
                break;
            }
        }
        txtHoTen.setText(kh.getHoTen());
        txtCMND.setText(kh.getCmnd());
    }

    txtSoGhe.setText(ve.getSoGhe());
}

private JPanel createSuaVeButtonPanel(JDialog dialog, VeMayBay veCanSua, JTextField txtMaVe, 
                                     JTextField txtHoTen, JTextField txtCMND, JTextField txtSoGhe, 
                                     JComboBox<String> cbChuyenBay, JComboBox<String> cbLoaiVe,
                                     JComboBox<String> cbKhachHang, Runnable updateVeInfo) {
    
    JPanel panelButton = new JPanel(new FlowLayout());
    JButton btnLuu = new JButton("Lưu Thay Đổi");
    JButton btnHuy = new JButton("Hủy");

    btnLuu.setBackground(new Color(70, 130, 180));
    btnLuu.setForeground(Color.WHITE);

    btnLuu.addActionListener(e -> handleSuaVe(dialog, veCanSua, cbChuyenBay, txtHoTen, txtCMND, 
                                             txtSoGhe, cbLoaiVe, txtMaVe, updateVeInfo));

    btnHuy.addActionListener(e -> dialog.dispose());

    panelButton.add(btnLuu);
    panelButton.add(btnHuy);

    return panelButton;
}

private void handleSuaVe(JDialog dialog, VeMayBay veCanSua, JComboBox<String> cbChuyenBay, 
                        JTextField txtHoTen, JTextField txtCMND, JTextField txtSoGhe, 
                        JComboBox<String> cbLoaiVe, JTextField txtMaVe, Runnable updateVeInfo) {
    
    if (!validateDatVe(dialog, cbChuyenBay, txtHoTen, txtCMND, txtSoGhe)) {
        return;
    }

    try {
        // Lấy thông tin từ form
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

        // Kiểm tra ghế đã có người đặt chưa (trừ vé hiện tại)
        boolean gheDaCo = quanLy.getDsVe().timKiemTheoChuyenBay(maChuyen)
                .stream()
                .anyMatch(ve -> ve.getSoGhe().equals(soGhe) && !ve.getMaVe().equals(veCanSua.getMaVe()));
        
        if (gheDaCo) {
            ValidatorUtils.showErrorDialog(dialog, "Số ghế " + soGhe + " đã có người đặt trong chuyến bay này!");
            return;
        }

        // Cập nhật thông tin khách hàng nếu có
        DanhSachKhachHang dsKH = quanLy.getDsKhachHang();
        KhachHang khachHang = dsKH.timKiemTheoCMND(cmnd);
        String maKH = khachHang != null ? khachHang.getMa() : veCanSua.getmaKH();

        // Xóa vé cũ và tạo vé mới với thông tin đã sửa
        quanLy.getDsVe().xoa(veCanSua.getMaVe());

        // Tạo vé mới với thông tin đã cập nhật
        VeMayBay veMoi = taoVeTheoLoai(maKH, veCanSua.getMaVe(), soGhe, loaiVe, chuyenBay);
        
        if (veMoi == null) {
            ValidatorUtils.showErrorDialog(dialog, "Không thể cập nhật vé với loại: " + loaiVe);
            // Khôi phục vé cũ nếu lỗi
            quanLy.getDsVe().them(veCanSua);
            return;
        }

        // Thêm vé mới vào danh sách
        quanLy.getDsVe().them(veMoi);

        // Hiển thị thông báo thành công
        String message = String.format(
            "Cập nhật vé thành công!\n\n" +
            "Mã vé: %s\n" +
            "Hành khách: %s\n" +
            "CMND: %s\n" +
            "Chuyến bay: %s\n" +
            "Số ghế: %s\n" +
            "Loại vé: %s\n" +
            "Giá vé: %s VND",
            veCanSua.getMaVe(), hoTen, cmnd, maChuyen, soGhe, loaiVe,
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

// ========== XÓA VÉ ==========
public void xoaVe() {
    int selectedRow = tableVe.getSelectedRow();
    if (selectedRow == -1) {
        ValidatorUtils.showErrorDialog(mainGUI, "Vui lòng chọn vé cần xóa!");
        return;
    }

    String maVe = tableVe.getValueAt(selectedRow, 0).toString();
    VeMayBay veCanXoa = quanLy.getDsVe().timKiemTheoMa(maVe);
    
    if (veCanXoa == null) {
        ValidatorUtils.showErrorDialog(mainGUI, "Không tìm thấy vé cần xóa!");
        return;
    }

    // Hiển thị dialog xác nhận
    int confirm = JOptionPane.showConfirmDialog(
        mainGUI,
        String.format("Bạn có chắc chắn muốn xóa vé này?\n\n" +
                     "Mã vé: %s\n" +
                     "Chuyến bay: %s\n" +
                     "Số ghế: %s\n" +
                     "Loại vé: %s",
                     veCanXoa.getMaVe(), 
                     veCanXoa.getMaChuyen(),
                     veCanXoa.getSoGhe(),
                     getLoaiVeString(veCanXoa)),
        "Xác nhận xóa vé",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE
    );

    if (confirm == JOptionPane.YES_OPTION) {
        try {
            // Cập nhật số ghế trống của chuyến bay
            ChuyenBay chuyenBay = quanLy.getDsChuyenBay().timKiemTheoMa(veCanXoa.getMaChuyen());
            if (chuyenBay != null) {
                chuyenBay.setSoGheTrong(chuyenBay.getSoGheTrong() + 1);
            }

            // Xóa vé
            quanLy.getDsVe().xoa(veCanXoa.getMaVe());

            ValidatorUtils.showSuccessDialog(mainGUI, "Xóa vé thành công!");
            mainGUI.capNhatDuLieuGUI();

        } catch (Exception ex) {
            ValidatorUtils.showErrorDialog(mainGUI, "Lỗi khi xóa vé: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}

// ========== XEM CHI TIẾT VÉ ==========
public void xemChiTietVe() {
    int selectedRow = tableVe.getSelectedRow();
    if (selectedRow == -1) {
        ValidatorUtils.showErrorDialog(mainGUI, "Vui lòng chọn vé để xem chi tiết!");
        return;
    }

    String maVe = tableVe.getValueAt(selectedRow, 0).toString();
    VeMayBay ve = quanLy.getDsVe().timKiemTheoMa(maVe);
    
    if (ve == null) {
        ValidatorUtils.showErrorDialog(mainGUI, "Không tìm thấy vé!");
        return;
    }

    JDialog dialog = new JDialog(mainGUI, "Chi Tiết Vé Máy Bay", true);
    dialog.setSize(500, 600);
    dialog.setLocationRelativeTo(mainGUI);
    dialog.setLayout(new BorderLayout());

    JTextArea txtChiTiet = new JTextArea();
    txtChiTiet.setEditable(false);
    txtChiTiet.setBackground(new Color(240, 240, 240));
    txtChiTiet.setMargin(new Insets(15, 15, 15, 15));
    txtChiTiet.setFont(new Font("Arial", Font.PLAIN, 13));

    // Hiển thị thông tin chi tiết
    String chiTiet = buildChiTietVe(ve);
    txtChiTiet.setText(chiTiet);

    JScrollPane scrollPane = new JScrollPane(txtChiTiet);
    scrollPane.setBorder(BorderFactory.createTitledBorder("THÔNG TIN CHI TIẾT VÉ"));

    JButton btnDong = new JButton("Đóng");
    btnDong.addActionListener(e -> dialog.dispose());

    JPanel panelButton = new JPanel(new FlowLayout());
    panelButton.add(btnDong);

    dialog.add(scrollPane, BorderLayout.CENTER);
    dialog.add(panelButton, BorderLayout.SOUTH);
    dialog.setVisible(true);
}

private String buildChiTietVe(VeMayBay ve) {
    StringBuilder sb = new StringBuilder();
    
    // Thông tin cơ bản
    sb.append("=== THÔNG TIN CƠ BẢN ===\n");
    sb.append("Mã vé: ").append(ve.getMaVe()).append("\n");
    sb.append("Mã chuyến bay: ").append(ve.getMaChuyen()).append("\n");
    sb.append("Số ghế: ").append(ve.getSoGhe()).append("\n");
    sb.append("Loại vé: ").append(getLoaiVeString(ve)).append("\n");
    sb.append("Giá vé: ").append(String.format("%,.0f VND", ve.getGiaVe())).append("\n");
    sb.append("Ngày giờ bay: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(ve.getNgayBay())).append("\n\n");

    // Thông tin khách hàng
    DanhSachKhachHang dsKH = quanLy.getDsKhachHang();
    KhachHang kh = dsKH.timKiemTheoMa(ve.getmaKH());
    if (kh != null) {
        sb.append("=== THÔNG TIN HÀNH KHÁCH ===\n");
        sb.append("Mã KH: ").append(kh.getMa()).append("\n");
        sb.append("Họ tên: ").append(kh.getHoTen()).append("\n");
        sb.append("CMND/CCCD: ").append(kh.getCmnd()).append("\n");
        sb.append("Số ĐT: ").append(kh.getSoDT()).append("\n");
        sb.append("Email: ").append(kh.getEmail()).append("\n\n");
    }

    // Thông tin chuyến bay
    ChuyenBay cb = quanLy.getDsChuyenBay().timKiemTheoMa(ve.getMaChuyen());
    if (cb != null) {
        sb.append("=== THÔNG TIN CHUYẾN BAY ===\n");
        sb.append("Điểm đi: ").append(cb.getDiemDi()).append("\n");
        sb.append("Điểm đến: ").append(cb.getDiemDen()).append("\n");
        sb.append("Thời gian bay: ").append(cb.getThoiGianBayFormatted()).append(" phút\n");
        sb.append("Số ghế trống: ").append(cb.getSoGheTrong()).append("\n\n");
    }

    // Thông tin đặc thù theo loại vé
    sb.append("=== THÔNG TIN ĐẶC THÙ ===\n");
    if (ve instanceof VeThuongGia) {
        VeThuongGia vtg = (VeThuongGia) ve;
        sb.append("Hạng vé: ").append(vtg.loaiVe()).append("\n");
        sb.append("Phí dịch vụ: ").append(String.format("%,.0f VND", vtg.getPhuThu())).append("\n");
        sb.append("Số kg hành lý: ").append(vtg.getSoKgHanhLyMienPhi()).append("\n");
        sb.append("Phòng chờ VIP: ").append(vtg.isPhongChoVIP() ? "Có" : "Không").append("\n");
        sb.append("Dịch vụ đặc biệt: ").append(vtg.getDichVuDacBiet()).append("\n");
    } else if (ve instanceof VePhoThong) {
        VePhoThong vpt = (VePhoThong) ve;
        sb.append("Hành lý xách tay: ").append(vpt.isHanhLyXachTay() ? "Có" : "Không").append("\n");
        sb.append("Số kg hành lý: ").append(vpt.getSoKgHanhLyKyGui()).append("\n");
        sb.append("Phí dịch vụ: ").append(String.format("%,.0f VND", vpt.getPhiHanhLy())).append("\n");
        sb.append("Vị trí ghế: ").append(vpt.getSoGhe()).append("\n");
        sb.append("Bữa ăn: ").append(vpt.isDoAn() ? "Có" : "Không").append("\n");
    } else if (ve instanceof VeTietKiem) {
        VeTietKiem vtk = (VeTietKiem) ve;
        sb.append("Thời hạn hủy vé: ").append(4).append(" giờ\n");
        sb.append("Phí HOÀN ĐỔI: ").append(String.format("%.0f%%", vtk.getPhiHoanDoi() * 100)).append("\n");
    }

    return sb.toString();
}

private String getLoaiVeString(VeMayBay ve) {
    if (ve instanceof VeThuongGia) {
        return "Thương gia";
    } else if (ve instanceof VePhoThong) {
        return "Phổ thông";
    } else if (ve instanceof VeTietKiem) {
        return "Tiết kiệm";
    }
    return "Không xác định";
}
}