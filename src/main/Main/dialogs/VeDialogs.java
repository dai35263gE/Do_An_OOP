package Main.dialogs;

import Main.MainGUI;
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
    JDialog dialog = new JDialog(mainGUI, "Đặt Vé Máy Bay Mới", true);
    dialog.setSize(800, 700);
    dialog.setLocationRelativeTo(mainGUI);
    dialog.setLayout(new BorderLayout());

    JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

    // === PANEL CHỌN TUYẾN BAY VÀ LOẠI VÉ ===
    JPanel panelChonTuyen = new JPanel(new GridBagLayout());
    panelChonTuyen.setBorder(BorderFactory.createTitledBorder("CHỌN TUYẾN BAY VÀ LOẠI VÉ"));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.gridx = 0;
    gbc.gridy = 0;

    // ComboBox điểm đi và điểm đến
    JComboBox<String> cbDiemDi = new JComboBox<>(loadDiemDiItems());
    JComboBox<String> cbDiemDen = new JComboBox<>(loadDiemDenItems());
    
    // ComboBox loại vé
    JComboBox<String> cbLoaiVe = new JComboBox<>(new String[]{"Thương gia", "Phổ thông", "Tiết kiệm"});
    
    // Nút tìm chuyến bay
    JButton btnTimChuyenBay = new JButton("Tìm Chuyến Bay");
    btnTimChuyenBay.setBackground(new Color(70, 130, 180));
    btnTimChuyenBay.setForeground(Color.WHITE);

    // Thêm components vào panel
    GUIUtils.addFormRow(panelChonTuyen, gbc, "Điểm đi:*", cbDiemDi);
    GUIUtils.addFormRow(panelChonTuyen, gbc, "Điểm đến:*", cbDiemDen);
    GUIUtils.addFormRow(panelChonTuyen, gbc, "Loại vé:*", cbLoaiVe);
    
    gbc.gridx = 0;
    gbc.gridy++;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.CENTER;
    panelChonTuyen.add(btnTimChuyenBay, gbc);

    // === PANEL DANH SÁCH CHUYẾN BAY ===
    JPanel panelDanhSachChuyen = new JPanel(new BorderLayout());
    panelDanhSachChuyen.setBorder(BorderFactory.createTitledBorder("DANH SÁCH CHUYẾN BAY PHÙ HỢP"));
    panelDanhSachChuyen.setPreferredSize(new Dimension(700, 150));

    // Table danh sách chuyến bay
    String[] columnNames = {"Chọn", "Mã Chuyến", "Điểm đi", "Điểm đến", "Giờ khởi hành", "Giá cơ bản", "Ghế trống"};
    DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnIndex == 0 ? Boolean.class : String.class;
        }
    };
    
    JTable tableChuyenBay = new JTable(tableModel);
    tableChuyenBay.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    JScrollPane scrollChuyenBay = new JScrollPane(tableChuyenBay);
    panelDanhSachChuyen.add(scrollChuyenBay, BorderLayout.CENTER);

    // === PANEL THÔNG TIN HÀNH KHÁCH ===
    JPanel panelThongTinHK = new JPanel(new GridBagLayout());
    panelThongTinHK.setBorder(BorderFactory.createTitledBorder("THÔNG TIN HÀNH KHÁCH"));
    GridBagConstraints gbcHK = new GridBagConstraints();
    gbcHK.fill = GridBagConstraints.HORIZONTAL;
    gbcHK.insets = new Insets(5, 5, 5, 5);
    gbcHK.gridx = 0;
    gbcHK.gridy = 0;

    // Tự động tạo mã vé
    int soVeHienTai = quanLy.getDsVe().demSoLuong();
    String maVeTuDong = "VE" + String.format("%04d", soVeHienTai + 1);
    JTextField txtMaVe = new JTextField(maVeTuDong, 20);
    txtMaVe.setEditable(false);
    txtMaVe.setBackground(new Color(240, 240, 240));

    // ComboBox chọn khách hàng
    JComboBox<String> cbKhachHang = new JComboBox<>(loadKhachHangItems());
    
    // Thông tin hành khách
    JTextField txtHoTen = new JTextField(20);
    JTextField txtCMND = new JTextField(20);
    JTextField txtSoGhe = new JTextField(10);

    // Thêm components vào panel
    GUIUtils.addFormRow(panelThongTinHK, gbcHK, "Mã vé:", txtMaVe);
    GUIUtils.addFormRow(panelThongTinHK, gbcHK, "Khách hàng:", cbKhachHang);
    GUIUtils.addFormRow(panelThongTinHK, gbcHK, "Họ tên hành khách:*", txtHoTen);
    GUIUtils.addFormRow(panelThongTinHK, gbcHK, "CMND/CCCD:*", txtCMND);
    GUIUtils.addFormRow(panelThongTinHK, gbcHK, "Số ghế:*", txtSoGhe);

    // === PANEL THÔNG TIN VÉ ===
    JTextArea txtThongTin = createThongTinTextArea();
    JPanel panelThongTin = createThongTinPanel(txtThongTin);

    // === PANEL BUTTON ===
    JPanel panelButton = new JPanel(new FlowLayout());
    JButton btnDatVe = new JButton("Đặt Vé");
    JButton btnLamMoi = new JButton("Làm Mới");
    JButton btnHuy = new JButton("Hủy");

    btnDatVe.setBackground(new Color(70, 130, 180));
    btnDatVe.setForeground(Color.WHITE);
    btnDatVe.setEnabled(false); // Disable cho đến khi chọn chuyến bay

    btnLamMoi.setBackground(new Color(255, 165, 0));
    btnLamMoi.setForeground(Color.WHITE);

    panelButton.add(btnDatVe);
    panelButton.add(btnLamMoi);
    panelButton.add(btnHuy);

    // === XỬ LÝ SỰ KIỆN ===
    
    // Xử lý tìm chuyến bay
    btnTimChuyenBay.addActionListener(e -> {
        String diemDi = (String) cbDiemDi.getSelectedItem();
        String diemDen = (String) cbDiemDen.getSelectedItem();
        String loaiVe = (String) cbLoaiVe.getSelectedItem();
        
        if (diemDi == null || diemDen == null || diemDi.equals(diemDen)) {
            ValidatorUtils.showErrorDialog(dialog, "Vui lòng chọn điểm đi và điểm đến khác nhau!");
            return;
        }
        
        loadDanhSachChuyenBay(tableModel, diemDi, diemDen, loaiVe);
    });

    // Xử lý chọn khách hàng
    cbKhachHang.addActionListener(e -> {
        if (cbKhachHang.getSelectedIndex() > 0) {
            String selectedKH = (String) cbKhachHang.getSelectedItem();
            String[] parts = selectedKH.split(" - ");
            if (parts.length >= 3) {
                KhachHang kh = quanLy.getDsKhachHang().timKiemTheoMa(parts[0]);
                if (kh != null) {
                    txtHoTen.setText(kh.getHoTen());
                    txtCMND.setText(kh.getCmnd());
                    updateThongTinVe(txtMaVe, txtHoTen, txtCMND, txtSoGhe, cbLoaiVe, tableChuyenBay, txtThongTin);
                }
            }
        }
    });

    // Xử lý chọn chuyến bay từ table
    tableChuyenBay.getSelectionModel().addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) {
            int selectedRow = tableChuyenBay.getSelectedRow();
            if (selectedRow >= 0) {
                btnDatVe.setEnabled(true);
                updateThongTinVe(txtMaVe, txtHoTen, txtCMND, txtSoGhe, cbLoaiVe, tableChuyenBay, txtThongTin);
            }
        }
    });

    // Document listeners cho các text field
    DocumentListener docListener = new DocumentListener() {
        public void anyUpdate() { 
            updateThongTinVe(txtMaVe, txtHoTen, txtCMND, txtSoGhe, cbLoaiVe, tableChuyenBay, txtThongTin); 
        }
        public void insertUpdate(DocumentEvent e) { anyUpdate(); }
        public void removeUpdate(DocumentEvent e) { anyUpdate(); }
        public void changedUpdate(DocumentEvent e) { anyUpdate(); }
    };
    
    txtHoTen.getDocument().addDocumentListener(docListener);
    txtCMND.getDocument().addDocumentListener(docListener);
    txtSoGhe.getDocument().addDocumentListener(docListener);

    // Xử lý đặt vé
    btnDatVe.addActionListener(e -> {
        handleDatVe(dialog, tableChuyenBay, txtHoTen, txtCMND, txtSoGhe, 
                   cbLoaiVe, txtMaVe, cbKhachHang);
    });

    // Xử lý làm mới
    btnLamMoi.addActionListener(e -> {
        resetFormDatVe(txtMaVe, cbDiemDi, cbDiemDen, cbLoaiVe, cbKhachHang, 
                      txtHoTen, txtCMND, txtSoGhe, tableModel);
        btnDatVe.setEnabled(false);
        ValidatorUtils.showSuccessDialog(dialog, "Đã làm mới form với mã vé mới!");
    });

    // Xử lý hủy
    btnHuy.addActionListener(e -> dialog.dispose());

    // === SẮP XẾP CÁC PANEL VÀO DIALOG ===
    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.add(panelChonTuyen, BorderLayout.NORTH);
    topPanel.add(panelDanhSachChuyen, BorderLayout.CENTER);

    JPanel centerPanel = new JPanel(new BorderLayout());
    centerPanel.add(panelThongTinHK, BorderLayout.NORTH);
    centerPanel.add(panelThongTin, BorderLayout.CENTER);

    mainPanel.add(topPanel, BorderLayout.NORTH);
    mainPanel.add(centerPanel, BorderLayout.CENTER);
    mainPanel.add(panelButton, BorderLayout.SOUTH);

    dialog.add(mainPanel);
    dialog.setVisible(true);
}

// ========== CÁC PHƯƠNG THỨC HỖ TRỢ CHO DIALOG MỚI ==========

private Vector<String> loadDiemDiItems() {
    Vector<String> diemDiItems = new Vector<>();
    diemDiItems.add("-- Chọn điểm đi --");
    
    DanhSachChuyenBay dsChuyenBay = quanLy.getDsChuyenBay();
    if (dsChuyenBay != null && dsChuyenBay.getDanhSachChuyenBay() != null) {
        for (ChuyenBay cb : dsChuyenBay.getDanhSachChuyenBay()) {
            if (!diemDiItems.contains(cb.getDiemDi())) {
                diemDiItems.add(cb.getDiemDi());
            }
        }
    }
    
    return diemDiItems;
}

private Vector<String> loadDiemDenItems() {
    Vector<String> diemDenItems = new Vector<>();
    diemDenItems.add("-- Chọn điểm đến --");
    
    DanhSachChuyenBay dsChuyenBay = quanLy.getDsChuyenBay();
    if (dsChuyenBay != null && dsChuyenBay.getDanhSachChuyenBay() != null) {
        for (ChuyenBay cb : dsChuyenBay.getDanhSachChuyenBay()) {
            if (!diemDenItems.contains(cb.getDiemDen())) {
                diemDenItems.add(cb.getDiemDen());
            }
        }
    }
    
    return diemDenItems;
}

private void loadDanhSachChuyenBay(DefaultTableModel tableModel, String diemDi, String diemDen, String loaiVe) {
    tableModel.setRowCount(0); // Xóa dữ liệu cũ
    
    DanhSachChuyenBay dsChuyenBay = quanLy.getDsChuyenBay();
    if (dsChuyenBay != null && dsChuyenBay.getDanhSachChuyenBay() != null) {
        for (ChuyenBay cb : dsChuyenBay.getDanhSachChuyenBay()) {
            if (cb.getDiemDi().equals(diemDi) && 
                cb.getDiemDen().equals(diemDen) && 
                cb.conGheTrong()) {
                
                // Tính giá vé theo loại
                double giaVe = tinhGiaVe(loaiVe, cb.getGiaCoBan());
                
                Object[] rowData = {
                    false, // Checkbox
                    cb.getMaChuyen(),
                    cb.getDiemDi(),
                    cb.getDiemDen(),
                    new SimpleDateFormat("dd/MM/yyyy HH:mm").format(cb.getGioKhoiHanh()),
                    String.format("%,.0f VND", giaVe),
                    cb.getSoGheTrong()
                };
                tableModel.addRow(rowData);
            }
        }
    }
    
    if (tableModel.getRowCount() == 0) {
        ValidatorUtils.showErrorDialog(mainGUI, "Không tìm thấy chuyến bay nào phù hợp!");
    }
}

private void updateThongTinVe(JTextField txtMaVe, JTextField txtHoTen, JTextField txtCMND, 
                             JTextField txtSoGhe, JComboBox<String> cbLoaiVe,
                             JTable tableChuyenBay, JTextArea txtThongTin) {
    try {
        int selectedRow = tableChuyenBay.getSelectedRow();
        if (selectedRow < 0) {
            txtThongTin.setText("Vui lòng chọn một chuyến bay từ danh sách.");
            return;
        }

        String maVe = txtMaVe.getText().trim();
        String hoTen = txtHoTen.getText().trim();
        String cmnd = txtCMND.getText().trim();
        String soGhe = txtSoGhe.getText().trim();
        String loaiVe = (String) cbLoaiVe.getSelectedItem();
        String maChuyen = tableChuyenBay.getValueAt(selectedRow, 1).toString();
        String giaVeStr = tableChuyenBay.getValueAt(selectedRow, 5).toString();

        StringBuilder info = new StringBuilder("THÔNG TIN VÉ ĐẶT CHỖ:\n\n");
        info.append("Mã vé: ").append(maVe).append("\n");
        info.append("Chuyến bay: ").append(maChuyen).append("\n");
        info.append("Loại vé: ").append(loaiVe).append("\n");
        info.append("Hành khách: ").append(hoTen.isEmpty() ? "..." : hoTen).append("\n");
        info.append("CMND: ").append(cmnd.isEmpty() ? "..." : cmnd).append("\n");
        info.append("Số ghế: ").append(soGhe.isEmpty() ? "..." : soGhe).append("\n");
        info.append("Giá vé: ").append(giaVeStr).append("\n");
        
        // Thông tin chuyến bay chi tiết
        ChuyenBay cb = quanLy.getDsChuyenBay().timKiemTheoMa(maChuyen);
        if (cb != null) {
            info.append("\nTHÔNG TIN CHUYẾN BAY:\n");
            info.append("Tuyến: ").append(cb.getDiemDi()).append(" → ").append(cb.getDiemDen()).append("\n");
            info.append("Khởi hành: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(cb.getGioKhoiHanh())).append("\n");
            info.append("Thời gian bay: ").append(cb.getThoiGianBayFormatted()).append(" phút\n");
        }

        txtThongTin.setText(info.toString());
    } catch (Exception ex) {
        txtThongTin.setText("Đang cập nhật thông tin...");
    }
}

private void handleDatVe(JDialog dialog, JTable tableChuyenBay, JTextField txtHoTen,
                        JTextField txtCMND, JTextField txtSoGhe, JComboBox<String> cbLoaiVe,
                        JTextField txtMaVe, JComboBox<String> cbKhachHang) {
    
    int selectedRow = tableChuyenBay.getSelectedRow();
    if (selectedRow == -1) {
        ValidatorUtils.showErrorDialog(dialog, "Vui lòng chọn chuyến bay!");
        return;
    }

    if (!validateDatVe(dialog, txtHoTen, txtCMND, txtSoGhe)) {
        return;
    }

    try {
        // Lấy thông tin từ form
        String maVe = txtMaVe.getText().trim();
        String maChuyen = tableChuyenBay.getValueAt(selectedRow, 1).toString();
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

        // Lấy mã khách hàng
        String maKH = null;
        if (cbKhachHang.getSelectedIndex() > 0) {
            String selectedKH = (String) cbKhachHang.getSelectedItem();
            String[] parts = selectedKH.split(" - ");
            maKH = parts[0];
        }

        // Tạo vé mới
        VeMayBay veMoi = taoVeTheoLoai(maKH, maVe, soGhe, loaiVe, chuyenBay);
        
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

private boolean validateDatVe(JDialog dialog, JTextField txtHoTen, 
                             JTextField txtCMND, JTextField txtSoGhe) {
    
    if (!ValidatorUtils.validateRequiredFields(dialog, txtHoTen, txtCMND, txtSoGhe)) {
        return false;
    }

    if (!ValidatorUtils.isValidCMND(txtCMND.getText().trim())) {
        ValidatorUtils.showErrorDialog(dialog, "CMND/CCCD không hợp lệ! Phải có 9 hoặc 12 số.");
        return false;
    }

    return true;
}

private void resetFormDatVe(JTextField txtMaVe, JComboBox<String> cbDiemDi, 
                           JComboBox<String> cbDiemDen, JComboBox<String> cbLoaiVe,
                           JComboBox<String> cbKhachHang, JTextField txtHoTen, 
                           JTextField txtCMND, JTextField txtSoGhe, DefaultTableModel tableModel) {
    
    // Tạo mã vé mới
    int soVeMoi = quanLy.getDsVe().demSoLuong();
    String maVeMoi = "VE" + String.format("%04d", soVeMoi + 1);
    txtMaVe.setText(maVeMoi);
    
    // Reset các field
    cbDiemDi.setSelectedIndex(0);
    cbDiemDen.setSelectedIndex(0);
    cbLoaiVe.setSelectedIndex(0);
    cbKhachHang.setSelectedIndex(0);
    txtHoTen.setText("");
    txtCMND.setText("");
    txtSoGhe.setText("");
    tableModel.setRowCount(0);
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

// ========== DIALOG TÌM KIẾM VÉ ==========
public void moDialogTimKiemVe() {
    JDialog dialog = new JDialog(mainGUI, "Tìm Kiếm Vé", true);
    dialog.setSize(1000, 700); // Tăng chiều cao để hiển thị tốt hơn
    dialog.setLocationRelativeTo(mainGUI);
    dialog.setLayout(new BorderLayout());

    JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

    // === PANEL ĐIỀU KIỆN TÌM KIẾM ===
    JPanel panelTimKiem = new JPanel(new GridBagLayout());
    panelTimKiem.setBorder(BorderFactory.createTitledBorder("ĐIỀU KIỆN TÌM KIẾM"));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(8, 20, 8, 20);
    gbc.gridx = 0;
    gbc.gridy = 0;

    // Các field tìm kiếm - sắp xếp thành 2 cột
    JTextField txtMaVe = new JTextField(15);
    JTextField txtMaChuyen = new JTextField(15);
    JTextField txtSoGhe = new JTextField(10);
    JComboBox<String> cbLoaiVe = new JComboBox<>(new String[]{"Tất cả", "Thương gia", "Phổ thông", "Tiết kiệm"});
    JComboBox<String> cbTrangThai = new JComboBox<>(new String[]{"Tất cả", "Đã đặt", "Đã thanh toán", "Đã hủy", "Đã bay"});
    
    // Tìm kiếm theo khoảng giá
    JTextField txtGiaMin = new JTextField(10);
    JTextField txtGiaMax = new JTextField(10);
    
    // Sử dụng JSpinner cho ngày tháng
    SpinnerDateModel modelTuNgay = new SpinnerDateModel();
    JSpinner spinnerTuNgay = new JSpinner(modelTuNgay);
    JSpinner.DateEditor editorTuNgay = new JSpinner.DateEditor(spinnerTuNgay, "dd/MM/yyyy");
    spinnerTuNgay.setEditor(editorTuNgay);
    spinnerTuNgay.setValue("01/11/2025"); // Mặc định là ngày hiện tại

    SpinnerDateModel modelDenNgay = new SpinnerDateModel();
    JSpinner spinnerDenNgay = new JSpinner(modelDenNgay);
    JSpinner.DateEditor editorDenNgay = new JSpinner.DateEditor(spinnerDenNgay, "dd/MM/yyyy");
    spinnerDenNgay.setEditor(editorDenNgay);
    spinnerDenNgay.setValue("01/12/3025"); // Mặc định là ngày hiện tại


    // Tạo panel chứa các điều kiện tìm kiếm - chia thành 2 cột
    JPanel panelDieuKien = new JPanel(new GridLayout(0, 2, 20, 10));
    panelDieuKien.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));

    // Cột 1
    JPanel col1 = new JPanel(new GridBagLayout());
    GridBagConstraints gbc1 = new GridBagConstraints();
    gbc1.fill = GridBagConstraints.HORIZONTAL;
    gbc1.insets = new Insets(5, 5, 5, 5);
    gbc1.gridx = 0;
    gbc1.gridy = 0;

    GUIUtils.addFormRow(col1, gbc1, "Mã vé:", txtMaVe);
    GUIUtils.addFormRow(col1, gbc1, "Mã chuyến bay:", txtMaChuyen);
    GUIUtils.addFormRow(col1, gbc1, "Số ghế:", txtSoGhe);

    // Cột 2
    JPanel col2 = new JPanel(new GridBagLayout());
    GridBagConstraints gbc2 = new GridBagConstraints();
    gbc2.fill = GridBagConstraints.HORIZONTAL;
    gbc2.insets = new Insets(5, 5, 5, 5);
    gbc2.gridx = 0;
    gbc2.gridy = 0;

    GUIUtils.addFormRow(col2, gbc2, "Loại vé:", cbLoaiVe);
    GUIUtils.addFormRow(col2, gbc2, "Trạng thái:", cbTrangThai);

    // Hàng 2 - Khoảng giá và ngày
    JPanel col3 = new JPanel(new GridBagLayout());
    GridBagConstraints gbc3 = new GridBagConstraints();
    gbc3.fill = GridBagConstraints.HORIZONTAL;
    gbc3.insets = new Insets(5, 5, 5, 5);
    gbc3.gridx = 0;
    gbc3.gridy = 0;
    gbc3.gridwidth = 2;

    JPanel panelGia = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
    panelGia.add(new JLabel("Giá từ:"));
    panelGia.add(txtGiaMin);
    panelGia.add(new JLabel("đến:"));
    panelGia.add(txtGiaMax);
    panelGia.add(new JLabel("VND"));

    JPanel panelNgay = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
    panelNgay.add(new JLabel("Từ ngày:"));
    panelNgay.add(spinnerTuNgay);
    panelNgay.add(new JLabel("Đến ngày:"));
    panelNgay.add(spinnerDenNgay);

    GUIUtils.addFormRow(col3, gbc3, "Khoảng giá:", panelGia);
    GUIUtils.addFormRow(col3, gbc3, "Khoảng ngày:", panelNgay);

    panelDieuKien.add(col1);
    panelDieuKien.add(col2);
    
    // Thêm panel điều kiện vào panel chính
    panelTimKiem.add(panelDieuKien, gbc);
    gbc.gridy++;
    panelTimKiem.add(col3, gbc);

    // === PANEL KẾT QUẢ TÌM KIẾM ===
    JPanel panelKetQua = new JPanel(new BorderLayout());
    panelKetQua.setBorder(BorderFactory.createTitledBorder("KẾT QUẢ TÌM KIẾM"));
    panelKetQua.setPreferredSize(new Dimension(900, 300)); // Tăng chiều cao cho kết quả
    
    DefaultTableModel modelKetQua = TableUtils.createVeTableModel();
    JTable tableKetQua = new JTable(modelKetQua);
    tableKetQua.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tableKetQua.setRowHeight(25); // Tăng chiều cao dòng cho dễ đọc
    JScrollPane scrollKetQua = new JScrollPane(tableKetQua);
    panelKetQua.add(scrollKetQua, BorderLayout.CENTER);

    // Label hiển thị số kết quả
    JLabel lblSoKetQua = new JLabel("Tìm thấy: 0 vé");
    lblSoKetQua.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    lblSoKetQua.setFont(new Font("Arial", Font.BOLD, 12));
    panelKetQua.add(lblSoKetQua, BorderLayout.SOUTH);

    // === PANEL BUTTON ===
    JPanel panelButton = new JPanel(new FlowLayout());
    JButton btnTimKiem = new JButton("Tìm Kiếm");
    JButton btnXemTatCa = new JButton("Xem Tất Cả");
    JButton btnXemChiTiet = new JButton("Xem Chi Tiết");
    JButton btnDong = new JButton("Đóng");

    btnTimKiem.setBackground(new Color(70, 130, 180));
    btnTimKiem.setForeground(Color.WHITE);
    btnTimKiem.setPreferredSize(new Dimension(100, 30));
    
    btnXemTatCa.setBackground(new Color(60, 179, 113));
    btnXemTatCa.setForeground(Color.WHITE);
    btnXemTatCa.setPreferredSize(new Dimension(100, 30));

    btnXemChiTiet.setBackground(new Color(255, 165, 0));
    btnXemChiTiet.setForeground(Color.WHITE);
    btnXemChiTiet.setPreferredSize(new Dimension(120, 30));

    panelButton.add(btnTimKiem);
    panelButton.add(btnXemTatCa);
    panelButton.add(btnXemChiTiet);
    panelButton.add(btnDong);

    // === XỬ LÝ SỰ KIỆN ===
    
    // Xử lý tìm kiếm
    btnTimKiem.addActionListener(e -> {
        Map<String, Object> filters = taoFiltersTimKiem(
            txtMaVe, txtMaChuyen, txtSoGhe, cbLoaiVe, cbTrangThai,
            txtGiaMin, txtGiaMax, spinnerTuNgay, spinnerDenNgay
        );
        
        List<VeMayBay> ketQua = quanLy.getDsVe().timKiemDaTieuChi(filters);
        hienThiKetQuaTimKiem(modelKetQua, ketQua, lblSoKetQua);
    });

    // Xử lý xem tất cả
    btnXemTatCa.addActionListener(e -> {
        List<VeMayBay> tatCaVe = quanLy.getDsVe().getDanhSach();
        hienThiKetQuaTimKiem(modelKetQua, tatCaVe, lblSoKetQua);
    });

    // Xử lý xem chi tiết
    btnXemChiTiet.addActionListener(e -> {
        int selectedRow = tableKetQua.getSelectedRow();
        if (selectedRow == -1) {
            ValidatorUtils.showErrorDialog(dialog, "Vui lòng chọn vé để xem chi tiết!");
            return;
        }
        
        String maVe = tableKetQua.getValueAt(selectedRow, 0).toString();
        VeMayBay ve = quanLy.getDsVe().timKiemTheoMa(maVe);
        if (ve != null) {
            hienThiDialogChiTietVe(ve);
        }
    });

    // Xử lý đóng
    btnDong.addActionListener(e -> dialog.dispose());

    // === SẮP XẾP LAYOUT ===
    mainPanel.add(panelTimKiem, BorderLayout.NORTH);
    mainPanel.add(panelKetQua, BorderLayout.CENTER);
    mainPanel.add(panelButton, BorderLayout.SOUTH);

    dialog.add(mainPanel);
    dialog.setVisible(true);
}

// ========== CÁC PHƯƠNG THỨC HỖ TRỢ TÌM KIẾM ==========

private Map<String, Object> taoFiltersTimKiem(JTextField txtMaVe, JTextField txtMaChuyen, 
                                             JTextField txtSoGhe, JComboBox<String> cbLoaiVe,
                                             JComboBox<String> cbTrangThai, JTextField txtGiaMin,
                                             JTextField txtGiaMax, JSpinner spinnerTuNgay,
                                             JSpinner spinnerDenNgay) {
    
    Map<String, Object> filters = new HashMap<>();
    
    // Lọc theo mã vé
    if (!txtMaVe.getText().trim().isEmpty()) {
        filters.put("maVe", txtMaVe.getText().trim());
    }
    
    // Lọc theo mã chuyến bay
    if (!txtMaChuyen.getText().trim().isEmpty()) {
        filters.put("maChuyen", txtMaChuyen.getText().trim());
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
    
    // Lọc theo khoảng ngày - sử dụng JSpinner
    try {
        Date tuNgay = (Date) spinnerTuNgay.getValue();
        Date denNgay = (Date) spinnerDenNgay.getValue();
        
        // Đặt thời gian cho denNgay là 23:59:59 để bao gồm cả ngày đó
        Calendar cal = Calendar.getInstance();
        cal.setTime(denNgay);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        Date denNgayCuoi = (Date) cal.getTime();
        
        filters.put("tuNgay", tuNgay);
        filters.put("denNgay", denNgayCuoi);
        
    } catch (Exception ex) {
        ValidatorUtils.showErrorDialog(mainGUI, "Lỗi xử lý ngày tháng!");
        ex.printStackTrace();
    }
    
    return filters;
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
    // Sử dụng TableUtils để hiển thị kết quả
    TableUtils.hienThiKetQuaTimKiemVe(model, danhSach, quanLy);
    lblSoKetQua.setText("Tìm thấy: " + danhSach.size() + " vé");
    
    // Đổi màu label theo số lượng kết quả
    if (danhSach.size() == 0) {
        lblSoKetQua.setForeground(Color.RED);
    } else if (danhSach.size() < 10) {
        lblSoKetQua.setForeground(Color.ORANGE);
    } else {
        lblSoKetQua.setForeground(new Color(0, 128, 0)); // Xanh lá
    }
}

private void hienThiDialogChiTietVe(VeMayBay ve) {
    JDialog dialog = new JDialog(mainGUI, "Chi Tiết Vé - " + ve.getMaVe(), true);
    dialog.setSize(600, 700); // Tăng kích thước dialog chi tiết
    dialog.setLocationRelativeTo(mainGUI);
    dialog.setLayout(new BorderLayout());

    JTextArea txtChiTiet = new JTextArea();
    txtChiTiet.setEditable(false);
    txtChiTiet.setBackground(new Color(250, 250, 250));
    txtChiTiet.setMargin(new Insets(20, 20, 20, 20));
    txtChiTiet.setFont(new Font("Arial", Font.PLAIN, 14));
    txtChiTiet.setLineWrap(true);
    txtChiTiet.setWrapStyleWord(true);

    String chiTiet = buildChiTietVe(ve);
    txtChiTiet.setText(chiTiet);

    JScrollPane scrollPane = new JScrollPane(txtChiTiet);
    scrollPane.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
        "THÔNG TIN CHI TIẾT VÉ",
        javax.swing.border.TitledBorder.CENTER,
        javax.swing.border.TitledBorder.TOP,
        new Font("Arial", Font.BOLD, 14),
        new Color(70, 130, 180)
    ));

    JButton btnDong = new JButton("Đóng");
    btnDong.setBackground(new Color(70, 130, 180));
    btnDong.setForeground(Color.WHITE);
    btnDong.setPreferredSize(new Dimension(100, 35));
    btnDong.addActionListener(e -> dialog.dispose());

    JPanel panelButton = new JPanel(new FlowLayout());
    panelButton.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
    panelButton.add(btnDong);

    dialog.add(scrollPane, BorderLayout.CENTER);
    dialog.add(panelButton, BorderLayout.SOUTH);
    dialog.setVisible(true);
}

}