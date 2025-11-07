package Main.dialogs;

import Main.MainGUI;
import javax.swing.*;
import Main.utils.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;

import model.*;
import Sevice.*;

public class KhachHangDialogs {
    private QuanLyBanVeMayBay quanLy;
    private MainGUI mainGUI;

    public KhachHangDialogs(QuanLyBanVeMayBay quanLy, MainGUI mainGUI) {
        this.quanLy = quanLy;
        this.mainGUI = mainGUI;
    }

    public void moDialogThemKhachHang() {
        JDialog dialog = new JDialog(mainGUI, "Thêm Khách Hàng Mới", true);
        dialog.setSize(500, 550);
        dialog.setLocationRelativeTo(mainGUI);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Các component nhập liệu
        JTextField txtMaKH = new JTextField();
        JTextField txtHoTen = new JTextField();
        JTextField txtSoDT = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtCMND = new JTextField();
        JTextField txtNgaySinh = new JTextField();
        JTextField txtDiaChi = new JTextField();
        
        JComboBox<String> cboGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});
        JSpinner spinnerDiemTichLuy = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1));

        // Thêm components vào panel
        panel.add(new JLabel("Mã KH:*"));
        panel.add(txtMaKH);

        panel.add(new JLabel("Họ tên:*"));
        panel.add(txtHoTen);

        panel.add(new JLabel("Số điện thoại:*"));
        panel.add(txtSoDT);

        panel.add(new JLabel("Email:*"));
        panel.add(txtEmail);

        panel.add(new JLabel("CMND/CCCD:*"));
        panel.add(txtCMND);

        panel.add(new JLabel("Ngày sinh:"));
        panel.add(txtNgaySinh);

        panel.add(new JLabel("Giới tính:"));
        panel.add(cboGioiTinh);

        panel.add(new JLabel("Địa chỉ:"));
        panel.add(txtDiaChi);

        panel.add(new JLabel("Điểm tích lũy:"));
        panel.add(spinnerDiemTichLuy);

        // Panel hiển thị thông tin
        JPanel panelThongTin = new JPanel(new BorderLayout());
        panelThongTin.setBorder(BorderFactory.createTitledBorder("Thông tin khách hàng"));
        JTextArea txtThongTin = new JTextArea(4, 30);
        txtThongTin.setEditable(false);
        txtThongTin.setBackground(new Color(240, 240, 240));
        txtThongTin.setMargin(new Insets(10, 10, 10, 10));
        panelThongTin.add(new JScrollPane(txtThongTin), BorderLayout.CENTER);

        // Cập nhật thông tin khi nhập liệu
        javax.swing.event.DocumentListener updateInfoListener = new javax.swing.event.DocumentListener() {
            public void anyUpdate() {
                updateThongTin();
            }

            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                anyUpdate();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                anyUpdate();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                anyUpdate();
            }

            private void updateThongTin() {
                String maKH = txtMaKH.getText().trim();
                String hoTen = txtHoTen.getText().trim();
                String cmnd = txtCMND.getText().trim();
                String soDT = txtSoDT.getText().trim();
                String email = txtEmail.getText().trim();
                String gioiTinh = (String) cboGioiTinh.getSelectedItem();
                int diemTichLuy = (Integer) spinnerDiemTichLuy.getValue();

                StringBuilder info = new StringBuilder();
                
                if (!maKH.isEmpty()) {
                    info.append("Mã KH: ").append(maKH).append("\n");
                }
                if (!hoTen.isEmpty()) {
                    info.append("Họ tên: ").append(hoTen).append("\n");
                }
                if (!cmnd.isEmpty()) {
                    info.append("CMND: ").append(cmnd).append("\n");
                }
                if (!soDT.isEmpty()) {
                    info.append("Số ĐT: ").append(soDT).append("\n");
                }
                if (!email.isEmpty()) {
                    info.append("Email: ").append(email).append("\n");
                }
                if (gioiTinh != null && !gioiTinh.isEmpty()) {
                    info.append("Giới tính: ").append(gioiTinh).append("\n");
                }
                if (diemTichLuy > 0) {
                    info.append("Điểm tích lũy: ").append(diemTichLuy).append("\n");
                }

                if (info.length() == 0) {
                    txtThongTin.setText("Thông tin khách hàng sẽ hiển thị ở đây");
                } else {
                    txtThongTin.setText(info.toString());
                }
            }
        };

        txtMaKH.getDocument().addDocumentListener(updateInfoListener);
        txtHoTen.getDocument().addDocumentListener(updateInfoListener);
        txtCMND.getDocument().addDocumentListener(updateInfoListener);
        txtSoDT.getDocument().addDocumentListener(updateInfoListener);
        txtEmail.getDocument().addDocumentListener(updateInfoListener);

        ActionListener updateInfoActionListener = e -> {
            String maKH = txtMaKH.getText().trim();
            String hoTen = txtHoTen.getText().trim();
            String cmnd = txtCMND.getText().trim();
            String soDT = txtSoDT.getText().trim();
            String email = txtEmail.getText().trim();
            String gioiTinh = (String) cboGioiTinh.getSelectedItem();
            int diemTichLuy = (Integer) spinnerDiemTichLuy.getValue();

            StringBuilder info = new StringBuilder();
            
            if (!maKH.isEmpty()) info.append("Mã KH: ").append(maKH).append("\n");
            if (!hoTen.isEmpty()) info.append("Họ tên: ").append(hoTen).append("\n");
            if (!cmnd.isEmpty()) info.append("CMND: ").append(cmnd).append("\n");
            if (!soDT.isEmpty()) info.append("Số ĐT: ").append(soDT).append("\n");
            if (!email.isEmpty()) info.append("Email: ").append(email).append("\n");
            if (gioiTinh != null && !gioiTinh.isEmpty()) info.append("Giới tính: ").append(gioiTinh).append("\n");
            if (diemTichLuy > 0) info.append("Điểm tích lũy: ").append(diemTichLuy).append("\n");

            txtThongTin.setText(info.length() == 0 ? "Thông tin khách hàng sẽ hiển thị ở đây" : info.toString());
        };

        cboGioiTinh.addActionListener(updateInfoActionListener);
        spinnerDiemTichLuy.addChangeListener(e -> updateInfoActionListener.actionPerformed(null));

        // Panel button
        JPanel panelButton = new JPanel(new FlowLayout());
        JButton btnThem = new JButton("Thêm Khách Hàng");
        JButton btnHuy = new JButton("Hủy");

        btnThem.addActionListener(e -> {
            // Validate dữ liệu
            if (txtMaKH.getText().trim().isEmpty() ||
                txtHoTen.getText().trim().isEmpty() ||
                txtSoDT.getText().trim().isEmpty() ||
                txtEmail.getText().trim().isEmpty() ||
                txtCMND.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(dialog,
                        "Vui lòng nhập đầy đủ thông tin bắt buộc (*)",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // Lấy dữ liệu từ form
                String maKH = txtMaKH.getText().trim();
                String hoTen = txtHoTen.getText().trim();
                String soDT = txtSoDT.getText().trim();
                String email = txtEmail.getText().trim();
                String cmnd = txtCMND.getText().trim();
                String ngaySinhStr = txtNgaySinh.getText().trim();
                String gioiTinh = (String) cboGioiTinh.getSelectedItem();
                String diaChi = txtDiaChi.getText().trim();
                int diemTichLuy = (Integer) spinnerDiemTichLuy.getValue();

                // Parse ngày sinh
                java.util.Date ngaySinh = null;
                if (!ngaySinhStr.isEmpty()) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        sdf.setLenient(false);
                        ngaySinh = sdf.parse(ngaySinhStr);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog, 
                            "Định dạng ngày sinh không hợp lệ. Vui lòng nhập theo định dạng dd/MM/yyyy", 
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                // Tạo đối tượng KhachHang
                KhachHang khachHang = new KhachHang(maKH, hoTen, soDT, email, cmnd, ngaySinh, gioiTinh, diaChi, maKH, soDT);
                khachHang.setDiemTichLuy(diemTichLuy);
                khachHang.setNgayDangKy(new java.util.Date());

                // Thêm vào danh sách
                boolean result = quanLy.getDsKhachHang().them(khachHang);
                
                if (result) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Thêm khách hàng thành công!\n\n" +
                        "Mã KH: " + maKH + "\n" +
                        "Họ tên: " + hoTen + "\n" +
                        "CMND: " + cmnd + "\n" +
                        "Số ĐT: " + soDT + "\n" +
                        "Hạng: " + khachHang.getHangKhachHang(), 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Đóng dialog và cập nhật giao diện
                    dialog.dispose();
                    mainGUI.capNhatDuLieuGUI();
                } else {
                    JOptionPane.showMessageDialog(dialog, 
                        "Không thể thêm khách hàng!\n\n" +
                        "Có thể mã KH hoặc CMND đã tồn tại trong hệ thống.", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Lỗi khi thêm khách hàng: " + ex.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        btnHuy.addActionListener(e -> dialog.dispose());

        panelButton.add(btnThem);
        panelButton.add(btnHuy);

        // Thêm các panel vào dialog
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(panel, BorderLayout.NORTH);
        mainPanel.add(panelThongTin, BorderLayout.CENTER);
        mainPanel.add(panelButton, BorderLayout.SOUTH);

        dialog.add(mainPanel, BorderLayout.CENTER);
        
        // Set default button và hỗ trợ phím ESC
        dialog.getRootPane().setDefaultButton(btnThem);
        
        // Hiển thị dialog
        dialog.setVisible(true);
    }

    public void xemChiTietHoaDon() {
        int khachHang1 = mainGUI.getTableKhachHang().getSelectedRow();
        String maKH = (String) mainGUI.getTableKhachHang().getValueAt(khachHang1, 0);
        KhachHang khachHang = quanLy.getDsKhachHang().timKiemTheoMa(maKH);

        JDialog dialog = new JDialog(mainGUI, "Chi Tiết Hóa Đơn - " + khachHang.getHoTen(), true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(mainGUI);
        dialog.setLayout(new BorderLayout());

        // Lấy danh sách hóa đơn của khách hàng
        List<HoaDon> hoaDonCuaKH = khachHang.getLichSuHoaDon();

        if (hoaDonCuaKH.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, 
                "Khách hàng " + khachHang.getHoTen() + " chưa có hóa đơn nào!", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
            return;
        }

        // Panel thông tin khách hàng
        JPanel panelThongTinKH = new JPanel(new GridLayout(0, 2, 10, 5));
        panelThongTinKH.setBorder(BorderFactory.createTitledBorder("Thông tin khách hàng"));
        panelThongTinKH.add(new JLabel("Mã KH:"));
        panelThongTinKH.add(new JLabel(khachHang.getMa()));
        panelThongTinKH.add(new JLabel("Họ tên:"));
        panelThongTinKH.add(new JLabel(khachHang.getHoTen()));
        panelThongTinKH.add(new JLabel("CMND:"));
        panelThongTinKH.add(new JLabel(khachHang.getCmnd()));
        panelThongTinKH.add(new JLabel("Số ĐT:"));
        panelThongTinKH.add(new JLabel(khachHang.getSoDT()));
        panelThongTinKH.add(new JLabel("Email:"));
        panelThongTinKH.add(new JLabel(khachHang.getEmail()));
        panelThongTinKH.add(new JLabel("Hạng:"));
        panelThongTinKH.add(new JLabel(khachHang.getHangKhachHang()));
        panelThongTinKH.add(new JLabel("Điểm tích lũy:"));
        panelThongTinKH.add(new JLabel(String.valueOf(khachHang.getDiemTichLuy())));

        // ComboBox chọn hóa đơn
        JPanel panelChonHoaDon = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelChonHoaDon.add(new JLabel("Chọn hóa đơn:"));
        JComboBox<String> cbHoaDon = new JComboBox<>();
        
        // Thêm các hóa đơn vào combobox
        for (HoaDon hd : hoaDonCuaKH) {
            String item = String.format("%s - %s - %,d VND - %s", 
                hd.getMaHoaDon(), 
                new SimpleDateFormat("dd/MM/yyyy HH:mm").format(hd.getNgayLap()),
                (int)hd.getTongTien(),
                hd.getTrangThai());
            cbHoaDon.addItem(item);
        }
        panelChonHoaDon.add(cbHoaDon);

        // Panel chi tiết hóa đơn
        JPanel panelChiTietHoaDon = new JPanel(new BorderLayout());
        panelChiTietHoaDon.setBorder(BorderFactory.createTitledBorder("Chi tiết hóa đơn"));

        // Table hiển thị vé máy bay
        String[] columnNames = {"Mã Vé", "Chuyến Bay", "Loại Vé", "Số Ghế", "Giá Vé", "Trạng Thái"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable tableVe = new JTable(tableModel);
        tableVe.setRowHeight(25);
        JScrollPane scrollPaneVe = new JScrollPane(tableVe);

        // Panel thông tin tổng hợp hóa đơn
        JPanel panelTongHop = new JPanel(new GridLayout(0, 2, 10, 5));
        panelTongHop.setBorder(BorderFactory.createTitledBorder("Thông tin tổng hợp"));

        JLabel lblMaHoaDon = new JLabel();
        JLabel lblNgayLap = new JLabel();
        JLabel lblTongTien = new JLabel();
        JLabel lblThueVAT = new JLabel();
        JLabel lblPhiDichVu = new JLabel();
        JLabel lblThanhTien = new JLabel();
        JLabel lblTrangThai = new JLabel();
        JLabel lblSoLuongVe = new JLabel();

        panelTongHop.add(new JLabel("Mã hóa đơn:"));
        panelTongHop.add(lblMaHoaDon);
        panelTongHop.add(new JLabel("Ngày lập:"));
        panelTongHop.add(lblNgayLap);
        panelTongHop.add(new JLabel("Tổng tiền:"));
        panelTongHop.add(lblTongTien);
        panelTongHop.add(new JLabel("Thuế VAT:"));
        panelTongHop.add(lblThueVAT);
        panelTongHop.add(new JLabel("Phí dịch vụ:"));
        panelTongHop.add(lblPhiDichVu);
        panelTongHop.add(new JLabel("Thành tiền:"));
        panelTongHop.add(lblThanhTien);
        panelTongHop.add(new JLabel("Số lượng vé:"));
        panelTongHop.add(lblSoLuongVe);
        panelTongHop.add(new JLabel("Trạng thái:"));
        panelTongHop.add(lblTrangThai);

        // Panel button
        JPanel panelButton = new JPanel(new FlowLayout());
        JButton btnInHoaDon = new JButton("In Hóa Đơn");
        JButton btnDong = new JButton("Đóng");

        // Method cập nhật chi tiết hóa đơn khi chọn
        ActionListener updateChiTietHoaDon = e -> {
            int selectedIndex = cbHoaDon.getSelectedIndex();
            if (selectedIndex >= 0) {
                HoaDon hoaDon = hoaDonCuaKH.get(selectedIndex);
                
                // Cập nhật thông tin tổng hợp
                lblMaHoaDon.setText(hoaDon.getMaHoaDon());
                lblNgayLap.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(hoaDon.getNgayLap()));
                lblTongTien.setText(String.format("%,d VND", (int)hoaDon.getTongTien()));
                lblThueVAT.setText(String.format("%,d VND", (int)hoaDon.getThue()));
                lblThanhTien.setText(String.format("%,d VND", (int)(hoaDon.getTongTien() + hoaDon.getThue())));
                lblTrangThai.setText(hoaDon.getTrangThai());
                
                // Lấy danh sách vé từ hóa đơn
                List<VeMayBay> veTrongHoaDon = hoaDon.getDanhSachVe();
                
                lblSoLuongVe.setText(String.valueOf(veTrongHoaDon.size()));
                
                // Cập nhật table vé
                tableModel.setRowCount(0);
                for (VeMayBay ve : veTrongHoaDon) {
                    // Lấy thông tin chuyến bay
                    ChuyenBay chuyenBay = quanLy.getDsChuyenBay().timKiemTheoMa(ve.getMaChuyen());
                    String tenChuyenBay = chuyenBay != null ? 
                        String.format("%s → %s", chuyenBay.getDiemDi(), chuyenBay.getDiemDen()) : "N/A";
                    
                    // Xác định loại vé
                    String loaiVe = ve instanceof VeThuongGia ? "Thương gia" : 
                                   ve instanceof VePhoThong ? "Phổ thông" : "Tiết kiệm";
                    
                    tableModel.addRow(new Object[]{
                        ve.getMaVe(),
                        tenChuyenBay,
                        loaiVe,
                        ve.getSoGhe(),
                        String.format("%,d VND", (int)ve.getGiaVe()),
                        ve.getTrangThai()
                    });
                }
            }
        };

        cbHoaDon.addActionListener(updateChiTietHoaDon);

        // Hiển thị chi tiết hóa đơn đầu tiên
        if (!hoaDonCuaKH.isEmpty()) {
            updateChiTietHoaDon.actionPerformed(null);
        }

        // Xử lý nút in hóa đơn
        btnInHoaDon.addActionListener(e -> {
            int selectedIndex = cbHoaDon.getSelectedIndex();
            if (selectedIndex >= 0) {
                HoaDon hoaDon = hoaDonCuaKH.get(selectedIndex);
                inHoaDon(hoaDon, khachHang);
            }
        });

        btnDong.addActionListener(e -> dialog.dispose());

        panelButton.add(btnInHoaDon);
        panelButton.add(btnDong);

        // Sắp xếp layout
        JPanel panelNorth = new JPanel(new BorderLayout());
        panelNorth.add(panelThongTinKH, BorderLayout.NORTH);
        panelNorth.add(panelChonHoaDon, BorderLayout.SOUTH);

        JPanel panelCenter = new JPanel(new BorderLayout());
        panelCenter.add(panelTongHop, BorderLayout.NORTH);
        panelCenter.add(scrollPaneVe, BorderLayout.CENTER);

        JPanel panelMain = new JPanel(new BorderLayout());
        panelMain.add(panelNorth, BorderLayout.NORTH);
        panelMain.add(panelChiTietHoaDon, BorderLayout.CENTER);
        panelChiTietHoaDon.add(panelCenter, BorderLayout.CENTER);

        dialog.add(panelMain, BorderLayout.CENTER);
        dialog.add(panelButton, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    // Method in hóa đơn (có thể phát triển thêm)
    private void inHoaDon(HoaDon hoaDon, KhachHang khachHang) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== HÓA ĐƠN BÁN VÉ MÁY BAY ===\n\n");
        sb.append("Mã hóa đơn: ").append(hoaDon.getMaHoaDon()).append("\n");
        sb.append("Ngày lập: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(hoaDon.getNgayLap())).append("\n");
        sb.append("Khách hàng: ").append(khachHang.getHoTen()).append("\n");
        sb.append("CMND: ").append(khachHang.getCmnd()).append("\n");
        sb.append("Số ĐT: ").append(khachHang.getSoDT()).append("\n\n");
        
        // Lấy danh sách vé
        DanhSachVeMayBay dsVe = quanLy.getDsVe();
        List<VeMayBay> veTrongHoaDon = hoaDon.getDanhSachVe();
        
        sb.append("Chi tiết vé:\n");
        for (VeMayBay ve : veTrongHoaDon) {
            ChuyenBay chuyenBay = quanLy.getDsChuyenBay().timKiemTheoMa(ve.getMaChuyen());
            String loaiVe = ve instanceof VeThuongGia ? "Thương gia" : "Phổ thông";
            
            sb.append(String.format(" - %s | %s → %s | %s | %,d VND\n",
                ve.getMaVe(),
                chuyenBay.getDiemDi(), chuyenBay.getDiemDen(),
                loaiVe,
                (int)ve.getGiaVe()
            ));
        }
        
        sb.append("\nTỔNG HỢP:\n");
        sb.append(String.format("Tổng tiền: %,d VND\n", (int)hoaDon.getTongTien()));
        sb.append(String.format("Thuế VAT: %,d VND\n", (int)hoaDon.getThue()));
        sb.append(String.format("THÀNH TIỀN: %,d VND\n", 
            (int)(hoaDon.getTongTien() + hoaDon.getThue())));
        sb.append("\nTrạng thái: ").append(hoaDon.getTrangThai());
        sb.append("\n\nCảm ơn quý khách!");
        
        JTextArea textArea = new JTextArea(sb.toString(), 20, 50);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        
        JOptionPane.showMessageDialog(mainGUI, scrollPane, "Hóa Đơn " + hoaDon.getMaHoaDon(), 
            JOptionPane.INFORMATION_MESSAGE);
    }

    public void xemChiTietKhachHang() {
        int selectedRow = mainGUI.getTableKhachHang().getSelectedRow();
        if (selectedRow >= 0) {
            String maKH = (String) mainGUI.getTableKhachHang().getValueAt(selectedRow, 0);
            KhachHang khachHang = quanLy.getDsKhachHang().timKiemTheoMa(maKH);
            if (khachHang != null) {
                xemChiTietHoaDon();
            }
        } else {
            JOptionPane.showMessageDialog(mainGUI, "Vui lòng chọn một khách hàng!", "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
        }
    }
}