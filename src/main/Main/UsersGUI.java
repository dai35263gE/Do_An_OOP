package Main;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.HashMap;
import Sevice.*;
import model.*;

public class UsersGUI extends JFrame {
    private QuanLyBanVeMayBay quanLy;
    private KhachHang khachHangDangNhap;
    private DanhSachChuyenBay dsChuyenBay;
    private DanhSachVeMayBay dsVe;
    private DanhSachHoaDon dsHoaDon;

    // Components
    private JTabbedPane tabbedPane;
    private JLabel lblWelcome;
    
    // Tab Đặt vé
    private JComboBox<String> cbDiemDi, cbDiemDen, cbChuyenBay;
    private JTextField txtNgayDi;
    private JButton btnTimChuyen, btnDatVe;
    private JTable tableChuyenBay;
    private DefaultTableModel modelChuyenBay;
    
    // Tab Vé của tôi
    private JTable tableVeCuaToi;
    private DefaultTableModel modelVeCuaToi;
    private JButton btnXemHoaDon, btnHuyVe;
    
    // Tab Lịch sử
    private JTable tableLichSu;
    private DefaultTableModel modelLichSu;
    
    // Tab Thông tin
    private JTextField txtHoTen, txtEmail, txtSoDT, txtDiaChi, txtGioiTinh, txtCmnd, txtNgaySinh;
    private JButton btnCapNhatThongTin;

    public UsersGUI(QuanLyBanVeMayBay quanLy) {
        this.quanLy = quanLy;
        this.dsChuyenBay = quanLy.getDsChuyenBay();
        this.dsVe = quanLy.getDsVe();
        this.dsHoaDon = quanLy.getDsHoaDon();
        
        initComponents();
        setupLayout();
        setupEvents();
    }

    public boolean dangNhap(String maKH) {
        khachHangDangNhap = quanLy.getDsKhachHang().timKiemTheoMa(maKH);
        if (khachHangDangNhap != null) {
            lblWelcome.setText("Xin chào, " + khachHangDangNhap.getHoTen() + "! - Hạng: " + khachHangDangNhap.getHangKhachHang());
            capNhatThongTinCaNhan();
            taiVeCuaToi();
            taiLichSu();
            return true;
        }
        return false;
    }

    private void initComponents() {
        setTitle("Hệ Thống Đặt Vé Máy Bay - Khách Hàng");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // Welcome label
        lblWelcome = new JLabel("Vui lòng đăng nhập", JLabel.CENTER);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 16));
        lblWelcome.setForeground(Color.BLUE);

        // Tabbed pane
        tabbedPane = new JTabbedPane();

        // Tab Đặt vé
        JPanel panelDatVe = createTabDatVe();
        tabbedPane.addTab("Đặt Vé", panelDatVe);

        // Tab Vé của tôi
        JPanel panelVeCuaToi = createTabVeCuaToi();
        tabbedPane.addTab("Vé Của Tôi", panelVeCuaToi);

        // Tab Lịch sử
        JPanel panelLichSu = createTabLichSu();
        tabbedPane.addTab("Lịch Sử", panelLichSu);

        // Tab Thông tin
        JPanel panelThongTin = createTabThongTin();
        tabbedPane.addTab("Thông Tin", panelThongTin);
    }

    private JPanel createTabDatVe() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel tìm kiếm
        JPanel panelTimKiem = new JPanel(new GridLayout(2, 4, 10, 10));
        panelTimKiem.setBorder(BorderFactory.createTitledBorder("Tìm kiếm chuyến bay"));

        // Lấy danh sách điểm đi, điểm duy nhất
        java.util.Set<String> diemDiSet = new java.util.HashSet<>();
        java.util.Set<String> diemDenSet = new java.util.HashSet<>();
        
        if (dsChuyenBay != null && dsChuyenBay.getDanhSachChuyenBay() != null) {
            for (ChuyenBay cb : dsChuyenBay.getDanhSachChuyenBay()) {
                if ("CHƯA BAY".equals(cb.getTrangThai())) {
                    diemDiSet.add(cb.getDiemDi());
                    diemDenSet.add(cb.getDiemDen());
                }
            }
        }

        cbDiemDi = new JComboBox<>(new Vector<>(diemDiSet));
        cbDiemDen = new JComboBox<>(new Vector<>(diemDenSet));
        txtNgayDi = new JTextField();
        btnTimChuyen = new JButton("Tìm Chuyến Bay");

        panelTimKiem.add(new JLabel("Điểm đi:"));
        panelTimKiem.add(cbDiemDi);
        panelTimKiem.add(new JLabel("Điểm đến:"));
        panelTimKiem.add(cbDiemDen);
        panelTimKiem.add(new JLabel("Ngày đi (dd/MM/yyyy):"));
        panelTimKiem.add(txtNgayDi);
        panelTimKiem.add(new JLabel(""));
        panelTimKiem.add(btnTimChuyen);

        // Table chuyến bay
        String[] columns = {"Mã CB", "Điểm đi", "Điểm đến", "Ngày giờ", "Giá", "Ghế trống", "Trạng thái"};
        modelChuyenBay = new DefaultTableModel(columns, 0);
        tableChuyenBay = new JTable(modelChuyenBay);
        tableChuyenBay.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollChuyenBay = new JScrollPane(tableChuyenBay);

        // Panel đặt vé
        JPanel panelDatVe = new JPanel(new FlowLayout());
        cbChuyenBay = new JComboBox<>();
        btnDatVe = new JButton("Đặt Vé");

        panelDatVe.add(new JLabel("Chọn chuyến bay:"));
        panelDatVe.add(cbChuyenBay);
        panelDatVe.add(btnDatVe);

        panel.add(panelTimKiem, BorderLayout.NORTH);
        panel.add(scrollChuyenBay, BorderLayout.CENTER);
        panel.add(panelDatVe, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createTabVeCuaToi() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"Mã Vé", "Chuyến Bay", "Loại Vé", "Số Ghế", "Giá Vé", "Ngày Đặt", "Trạng Thái"};
        modelVeCuaToi = new DefaultTableModel(columns, 0);
        tableVeCuaToi = new JTable(modelVeCuaToi);
        JScrollPane scrollVeCuaToi = new JScrollPane(tableVeCuaToi);

        // Panel button
        JPanel panelButton = new JPanel(new FlowLayout());
        btnXemHoaDon = new JButton("Xem Hóa Đơn");
        btnHuyVe = new JButton("Hủy Vé");

        panelButton.add(btnXemHoaDon);
        panelButton.add(btnHuyVe);

        panel.add(new JLabel("Vé đang có:"), BorderLayout.NORTH);
        panel.add(scrollVeCuaToi, BorderLayout.CENTER);
        panel.add(panelButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createTabLichSu() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"Mã Vé", "Chuyến Bay", "Loại Vé", "Giá Vé", "Ngày Đặt", "Trạng Thái"};
        modelLichSu = new DefaultTableModel(columns, 0);
        tableLichSu = new JTable(modelLichSu);
        JScrollPane scrollLichSu = new JScrollPane(tableLichSu);

        panel.add(new JLabel("Lịch sử đặt vé:"), BorderLayout.NORTH);
        panel.add(scrollLichSu, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTabThongTin() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        txtHoTen = new JTextField();
        txtEmail = new JTextField();
        txtSoDT = new JTextField();
        txtDiaChi = new JTextField();
        txtGioiTinh = new JTextField();
        txtCmnd = new JTextField();
        txtNgaySinh = new JTextField();
        btnCapNhatThongTin = new JButton("Cập Nhật Thông Tin");

        panel.add(new JLabel("Họ tên:"));
        panel.add(txtHoTen);
        panel.add(new JLabel("Email:"));
        panel.add(txtEmail);
        panel.add(new JLabel("Số điện thoại:"));
        panel.add(txtSoDT);
        panel.add(new JLabel("Địa chỉ:"));
        panel.add(txtDiaChi);
        panel.add(new JLabel("Giới tính:"));
        panel.add(txtGioiTinh);
        panel.add(new JLabel("CCCD:"));
        panel.add(txtCmnd);
        panel.add(new JLabel("Ngày Sinh:"));
        panel.add(txtNgaySinh);
        panel.add(new JLabel(""));
        panel.add(btnCapNhatThongTin);

        return panel;
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        add(lblWelcome, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void setupEvents() {
        // Tab Đặt vé
        btnTimChuyen.addActionListener(e -> timChuyenBay());
        btnDatVe.addActionListener(e -> datVe());

        // Tab Vé của tôi
        btnXemHoaDon.addActionListener(e -> xemHoaDon());
        btnHuyVe.addActionListener(e -> huyVe());

        // Tab Thông tin
        btnCapNhatThongTin.addActionListener(e -> capNhatThongTin());

        // Double click để chọn chuyến bay
        tableChuyenBay.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tableChuyenBay.getSelectedRow();
                    if (row >= 0) {
                        String maChuyen = (String) modelChuyenBay.getValueAt(row, 0);
                        cbChuyenBay.removeAllItems();
                        cbChuyenBay.addItem(maChuyen);
                    }
                }
            }
        });
    }

    private void timChuyenBay() {
        modelChuyenBay.setRowCount(0);
        
        String diemDi = (String) cbDiemDi.getSelectedItem();
        String diemDen = (String) cbDiemDen.getSelectedItem();
        String ngayDiStr = txtNgayDi.getText().trim();

        if (diemDi == null || diemDen == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn điểm đi và điểm đến!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<ChuyenBay> ketQua = dsChuyenBay.timKiemTheoTuyen(diemDi, diemDen);

        if (ngayDiStr != null && !ngayDiStr.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date ngayDi = sdf.parse(ngayDiStr);
                ketQua.removeIf(cb -> !sdf.format(cb.getGioKhoiHanh()).equals(sdf.format(ngayDi)));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Định dạng ngày không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        for (ChuyenBay cb : ketQua) {
            if ("CHƯA BAY".equals(cb.getTrangThai()) && cb.getSoGheTrong() > 0) {
                modelChuyenBay.addRow(new Object[]{
                    cb.getMaChuyen(),
                    cb.getDiemDi(),
                    cb.getDiemDen(),
                    new SimpleDateFormat("dd/MM/yyyy HH:mm").format(cb.getGioKhoiHanh()),
                    String.format("%,d VND", (int)cb.getGiaCoBan()),
                    cb.getSoGheTrong(),
                    cb.getTrangThai()
                });
            }
        }

        if (modelChuyenBay.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy chuyến bay phù hợp!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void datVe() {
        if (khachHangDangNhap == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng đăng nhập!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (cbChuyenBay.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn chuyến bay!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String maChuyen = (String) cbChuyenBay.getSelectedItem();
        ChuyenBay chuyenBay = dsChuyenBay.timKiemTheoMa(maChuyen);

        if (chuyenBay == null || chuyenBay.getSoGheTrong() <= 0) {
            JOptionPane.showMessageDialog(this, "Chuyến bay không khả dụng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Hiển thị dialog chọn loại vé
        String[] loaiVeOptions = {"THƯƠNG GIA", "PHỔ THÔNG", "TIẾT KIỆM"};
        String loaiVe = (String) JOptionPane.showInputDialog(this,
                "Chọn loại vé:",
                "Chọn loại vé",
                JOptionPane.QUESTION_MESSAGE,
                null,
                loaiVeOptions,
                loaiVeOptions[1]);

        if (loaiVe == null) return;

        // Tính giá vé
        double giaCoBan = chuyenBay.getGiaCoBan();
        double heSoGia = 1.0;
        switch (loaiVe) {
            case "THƯƠNG GIA": heSoGia = 2.0; break;
            case "PHỔ THÔNG": heSoGia = 1.2; break;
            case "TIẾT KIỆM": heSoGia = 0.9; break;
        }
        double giaVe = giaCoBan * heSoGia;

        // Tạo vé mới
        String maVe = "VE" + System.currentTimeMillis();
        VeMayBay ve;
        
        if ("THƯƠNG GIA".equals(loaiVe)) {
            ve = new VeThuongGia(maVe, khachHangDangNhap.getMaKH(), khachHangDangNhap.getHoTen(),
                    khachHangDangNhap.getCmnd(), new Date(), giaVe, maChuyen, 
                    "A" + chuyenBay.getSoGheTrong(), "DAT", "Massage", 500000.0, 20, true, "Rượu vang");
        } else {
            ve = new VePhoThong(maVe, khachHangDangNhap.getMaKH(), khachHangDangNhap.getHoTen(),
                    khachHangDangNhap.getCmnd(), new Date(), giaVe, maChuyen,
                    "A" + chuyenBay.getSoGheTrong(), "DAT", true, 5, 200000, "Cửa sổ", true);
        }

        // Thêm vé và cập nhật chuyến bay
        if (dsVe.them(ve)) {
            chuyenBay.setSoGheTrong(chuyenBay.getSoGheTrong() - 1);
            
            // Tạo hóa đơn
            String maHoaDon = "HD" + System.currentTimeMillis();
            HoaDon hoaDon = new HoaDon(maHoaDon, khachHangDangNhap.getMaKH(), "KHACH_HANG",
                    giaVe, giaVe * 0.1, 200000, "DA_THANH_TOAN");
            dsHoaDon.them(hoaDon);

            // Cập nhật điểm tích lũy
            int diemThuong = (int) (giaVe / 100000); // 1 điểm cho mỗi 100,000 VND
            khachHangDangNhap.tangDiemTichLuy(diemThuong);

            JOptionPane.showMessageDialog(this,
                    "Đặt vé thành công!\n" +
                    "Mã vé: " + maVe + "\n" +
                    "Chuyến bay: " + chuyenBay.getDiemDi() + " → " + chuyenBay.getDiemDen() + "\n" +
                    "Loại vé: " + loaiVe + "\n" +
                    "Giá vé: " + String.format("%,d VND", (int)giaVe) + "\n" +
                    "Điểm tích lũy nhận được: " + diemThuong,
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);

            taiVeCuaToi();
            taiLichSu();
        } else {
            JOptionPane.showMessageDialog(this, "Đặt vé thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void taiVeCuaToi() {
        if (khachHangDangNhap == null) return;
        
        modelVeCuaToi.setRowCount(0);
        List<VeMayBay> veCuaToi = dsVe.timKiemTheoMaKH(khachHangDangNhap.getMaKH());
        
        for (VeMayBay ve : veCuaToi) {
            if ("DAT".equals(ve.getTrangThai()) || "CONFIRMED".equals(ve.getTrangThai())) {
                ChuyenBay cb = dsChuyenBay.timKiemTheoMa(ve.getMaChuyen());
                String tenChuyen = cb != null ? cb.getDiemDi() + " → " + cb.getDiemDen() : "N/A";
                String loaiVe = ve instanceof VeThuongGia ? "Thương gia" : "Phổ thông";
                
                modelVeCuaToi.addRow(new Object[]{
                    ve.getMaVe(),
                    tenChuyen,
                    loaiVe,
                    ve.getSoGhe(),
                    String.format("%,d VND", (int)ve.getGiaVe()),
                    new SimpleDateFormat("dd/MM/yyyy").format(ve.getNgayDat()),
                    ve.getTrangThai()
                });
            }
        }
    }

    private void taiLichSu() {
        if (khachHangDangNhap == null) return;
        
        modelLichSu.setRowCount(0);
        List<VeMayBay> lichSu = dsVe.timKiemTheoMaKH(khachHangDangNhap.getMaKH());
        
        for (VeMayBay ve : lichSu) {
            ChuyenBay cb = dsChuyenBay.timKiemTheoMa(ve.getMaChuyen());
            String tenChuyen = cb != null ? cb.getDiemDi() + " → " + cb.getDiemDen() : "N/A";
            String loaiVe = ve instanceof VeThuongGia ? "Thương gia" : "Phổ thông";
            
            modelLichSu.addRow(new Object[]{
                ve.getMaVe(),
                tenChuyen,
                loaiVe,
                String.format("%,d VND", (int)ve.getGiaVe()),
                new SimpleDateFormat("dd/MM/yyyy").format(ve.getNgayDat()),
                ve.getTrangThai()
            });
        }
    }

    private void xemHoaDon() {
        int row = tableVeCuaToi.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một vé!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maVe = (String) modelVeCuaToi.getValueAt(row, 0);
        VeMayBay ve = dsVe.timKiemTheoMa(maVe);
        
        if (ve != null) {
            // Tìm hóa đơn tương ứng
            List<HoaDon> hoaDonList = dsHoaDon.timKiemTheoMaKH(khachHangDangNhap.getMaKH());
            for (HoaDon hd : hoaDonList) {
                // Giả sử có phương thức lấy vé từ hóa đơn
                // Hiển thị thông tin hóa đơn
                StringBuilder sb = new StringBuilder();
                sb.append("=== HÓA ĐƠN ===\n\n");
                sb.append("Mã hóa đơn: ").append(hd.getMaHoaDon()).append("\n");
                sb.append("Ngày lập: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(hd.getNgayLap())).append("\n");
                sb.append("Khách hàng: ").append(khachHangDangNhap.getHoTen()).append("\n");
                sb.append("Tổng tiền: ").append(String.format("%,d VND", (int)hd.getTongTien())).append("\n");
                sb.append("Trạng thái: ").append(hd.getTrangThai()).append("\n");

                JTextArea textArea = new JTextArea(sb.toString());
                textArea.setEditable(false);
                JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Hóa Đơn", JOptionPane.INFORMATION_MESSAGE);
                break;
            }
        }
    }

    private void huyVe() {
        int row = tableVeCuaToi.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một vé!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maVe = (String) modelVeCuaToi.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn hủy vé " + maVe + "?",
                "Xác nhận hủy vé",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            VeMayBay ve = dsVe.timKiemTheoMa(maVe);
            if (ve != null) {
                ve.setTrangThai("DA_HUY");
                
                // Cập nhật số ghế trống của chuyến bay
                ChuyenBay cb = dsChuyenBay.timKiemTheoMa(ve.getMaChuyen());
                if (cb != null) {
                    cb.setSoGheTrong(cb.getSoGheTrong() + 1);
                }

                JOptionPane.showMessageDialog(this, "Đã hủy vé thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                taiVeCuaToi();
                taiLichSu();
            }
        }
    }

    private void capNhatThongTinCaNhan() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        if (khachHangDangNhap != null) {
            txtHoTen.setText(khachHangDangNhap.getHoTen());
            txtEmail.setText(khachHangDangNhap.getEmail());
            txtSoDT.setText(khachHangDangNhap.getSoDT());
            txtDiaChi.setText(khachHangDangNhap.getDiaChi());
            txtGioiTinh.setText(khachHangDangNhap.getGioiTinh());
            txtCmnd.setText(khachHangDangNhap.getCmnd());
            txtNgaySinh.setText(sdf.format(khachHangDangNhap.getNgaySinh()));
        }
    }

    private void capNhatThongTin() {
    // Kiểm tra xem khách hàng có đang đăng nhập không
    if (khachHangDangNhap == null) {
        JOptionPane.showMessageDialog(this, 
            "Không tìm thấy thông tin khách hàng! Vui lòng đăng nhập lại.", 
            "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    try {
        // Lấy dữ liệu từ các trường nhập liệu
        String hoTen = txtHoTen.getText().trim();
        String email = txtEmail.getText().trim();
        String soDT = txtSoDT.getText().trim();
        String diaChi = txtDiaChi.getText().trim();
        String gioiTinh = txtGioiTinh.getText().toString();
        String cccd = txtCmnd.getText().trim();
        String ngaySinhStr = txtNgaySinh.getText().trim();

        // Validate dữ liệu đầu vào
        StringBuilder loi = new StringBuilder();
        
        // Validate họ tên
        if (hoTen.isEmpty()) {
            loi.append("• Họ tên không được để trống\n");
        } else if (hoTen.length() < 2) {
            loi.append("• Họ tên phải có ít nhất 2 ký tự\n");
        } else if (!isValidName(hoTen)) {
            loi.append("• Họ tên chỉ được chứa chữ cái và khoảng trắng\n");
        }
        
        // Validate email
        if (email.isEmpty()) {
            loi.append("• Email không được để trống\n");
        } else if (!isValidEmail(email)) {
            loi.append("• Email không đúng định dạng\n");
        }
        
        // Validate số điện thoại
        if (soDT.isEmpty()) {
            loi.append("• Số điện thoại không được để trống\n");
        } else if (!isValidPhoneNumber(soDT)) {
            loi.append("• Số điện thoại không hợp lệ (10-11 số)\n");
        }
        
        // Validate địa chỉ
        if (diaChi.isEmpty()) {
            loi.append("• Địa chỉ không được để trống\n");
        }
        
        // Validate CCCD
        if (cccd.isEmpty()) {
            loi.append("• Số CCCD không được để trống\n");
        } else if (!isValidCCCD(cccd)) {
            loi.append("• Số CCCD phải có 12 chữ số\n");
        } else {
            // Kiểm tra CCCD có trùng với khách hàng khác không
            KhachHang khachHangTrung = quanLy.getDsKhachHang().timKiemTheoCMND(cccd);
            if (khachHangTrung != null && !khachHangTrung.getMaKH().equals(khachHangDangNhap.getMaKH())) {
                loi.append("• Số CCCD đã được sử dụng bởi khách hàng khác\n");
            }
        }
        
        // Validate ngày sinh
        if (ngaySinhStr.isEmpty()) {
            loi.append("• Ngày sinh không được để trống\n");
        } else if (!isValidDate(ngaySinhStr)) {
            loi.append("• Ngày sinh không hợp lệ (định dạng dd/MM/yyyy)\n");
        } else {
            // Kiểm tra tuổi (ít nhất 1 tuổi, nhiều nhất 120 tuổi)
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                sdf.setLenient(false);
                Date ngaySinh = sdf.parse(ngaySinhStr);
                if (!isValidAge(ngaySinh)) {
                    loi.append("• Tuổi phải từ 1 đến 120\n");
                }
            } catch (Exception ex) {
                loi.append("• Ngày sinh không hợp lệ\n");
            }
        }

        // Validate giới tính
        if (gioiTinh == null || gioiTinh.isEmpty() || gioiTinh.equals("-- Chọn --")) {
            loi.append("• Vui lòng chọn giới tính\n");
        }

        // Hiển thị lỗi nếu có
        if (loi.length() > 0) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng sửa các lỗi sau:\n" + loi.toString(), 
                "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Parse ngày sinh
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);
        Date ngaySinh = sdf.parse(ngaySinhStr);

        // Hiển thị xác nhận trước khi cập nhật
        int xacNhan = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn cập nhật thông tin cá nhân?\n\n" +
            "Họ tên: " + hoTen + "\n" +
            "Email: " + email + "\n" +
            "Số ĐT: " + soDT + "\n" +
            "Địa chỉ: " + diaChi + "\n" +
            "Giới tính: " + gioiTinh + "\n" +
            "CCCD: " + cccd + "\n" +
            "Ngày sinh: " + ngaySinhStr,
            "Xác nhận cập nhật",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (xacNhan != JOptionPane.YES_OPTION) {
            return;
        }

        // Lưu thông tin cũ để so sánh
        String hoTenCu = khachHangDangNhap.getHoTen();
        String emailCu = khachHangDangNhap.getEmail();
        String soDTCu = khachHangDangNhap.getSoDT();
        String diaChiCu = khachHangDangNhap.getDiaChi();
        String gioiTinhCu = khachHangDangNhap.getGioiTinh();
        String cccdCu = khachHangDangNhap.getCmnd();
        Date ngaySinhCu = khachHangDangNhap.getNgaySinh();

        // Cập nhật thông tin mới
        khachHangDangNhap.setHoTen(hoTen);
        khachHangDangNhap.setEmail(email);
        khachHangDangNhap.setSoDT(soDT);
        khachHangDangNhap.setDiaChi(diaChi);
        khachHangDangNhap.setGioiTinh(gioiTinh);
        khachHangDangNhap.setCmnd(cccd);
        khachHangDangNhap.setNgaySinh(ngaySinh);

        // Kiểm tra xem có thay đổi gì không
        boolean coThayDoi = !hoTen.equals(hoTenCu) || !email.equals(emailCu) || 
                           !soDT.equals(soDTCu) || !diaChi.equals(diaChiCu) ||
                           !gioiTinh.equals(gioiTinhCu) || !cccd.equals(cccdCu) ||
                           !ngaySinh.equals(ngaySinhCu);

        if (coThayDoi) {
            // Ghi log thay đổi
            System.out.println("Khách hàng " + khachHangDangNhap.getMaKH() + " đã cập nhật thông tin:");
            if (!hoTen.equals(hoTenCu)) {
                System.out.println("  - Họ tên: " + hoTenCu + " → " + hoTen);
            }
            if (!email.equals(emailCu)) {
                System.out.println("  - Email: " + emailCu + " → " + email);
            }
            if (!soDT.equals(soDTCu)) {
                System.out.println("  - Số ĐT: " + soDTCu + " → " + soDT);
            }
            if (!diaChi.equals(diaChiCu)) {
                System.out.println("  - Địa chỉ: " + diaChiCu + " → " + diaChi);
            }
            if (!gioiTinh.equals(gioiTinhCu)) {
                System.out.println("  - Giới tính: " + gioiTinhCu + " → " + gioiTinh);
            }
            if (!cccd.equals(cccdCu)) {
                System.out.println("  - CCCD: " + cccdCu + " → " + cccd);
            }
            if (!ngaySinh.equals(ngaySinhCu)) {
                System.out.println("  - Ngày sinh: " + sdf.format(ngaySinhCu) + " → " + sdf.format(ngaySinh));
            }

            // Hiển thị thông báo thành công
            JOptionPane.showMessageDialog(this,
                "✅ Cập nhật thông tin thành công!\n\n" +
                "Thông tin đã được thay đổi:\n" +
                (hoTen.equals(hoTenCu) ? "" : "• Họ tên\n") +
                (email.equals(emailCu) ? "" : "• Email\n") +
                (soDT.equals(soDTCu) ? "" : "• Số điện thoại\n") +
                (diaChi.equals(diaChiCu) ? "" : "• Địa chỉ\n") +
                (gioiTinh.equals(gioiTinhCu) ? "" : "• Giới tính\n") +
                (cccd.equals(cccdCu) ? "" : "• CCCD\n") +
                (ngaySinh.equals(ngaySinhCu) ? "" : "• Ngày sinh"),
                "Thành công",
                JOptionPane.INFORMATION_MESSAGE);

            // Cập nhật lại thông tin hiển thị
            capNhatHienThiThongTin();
            
        } else {
            JOptionPane.showMessageDialog(this,
                "Không có thông tin nào được thay đổi.",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
        }

    } catch (Exception ex) {
        // Xử lý lỗi
        JOptionPane.showMessageDialog(this,
            "❌ Có lỗi xảy ra khi cập nhật thông tin:\n" + ex.getMessage(),
            "Lỗi hệ thống",
            JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
}

// Method hỗ trợ kiểm tra định dạng email
private boolean isValidEmail(String email) {
    String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    return email.matches(emailRegex);
}

// Method hỗ trợ kiểm tra định dạng số điện thoại
private boolean isValidPhoneNumber(String phone) {
    // Chấp nhận số điện thoại 10-11 số, có thể bắt đầu bằng 0, 84, +84
    String phoneRegex = "^(0|84|\\+84)([3|5|7|8|9])[0-9]{8}$";
    return phone.replace("+", "").matches(phoneRegex);
}

// Method kiểm tra định dạng CCCD (12 chữ số)
private boolean isValidCCCD(String cccd) {
    return cccd.matches("^[0-9]{12}$");
}

// Method kiểm tra định dạng ngày
private boolean isValidDate(String dateStr) {
    try {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);
        sdf.parse(dateStr);
        return true;
    } catch (Exception e) {
        return false;
    }
}

// Method kiểm tra tuổi hợp lệ
private boolean isValidAge(Date ngaySinh) {
    Calendar calNgaySinh = Calendar.getInstance();
    calNgaySinh.setTime(ngaySinh);
    
    Calendar calHienTai = Calendar.getInstance();
    
    int tuoi = calHienTai.get(Calendar.YEAR) - calNgaySinh.get(Calendar.YEAR);
    
    // Kiểm tra nếu chưa đến sinh nhật trong năm nay thì trừ đi 1 tuổi
    if (calHienTai.get(Calendar.DAY_OF_YEAR) < calNgaySinh.get(Calendar.DAY_OF_YEAR)) {
        tuoi--;
    }
    
    return tuoi >= 1 && tuoi <= 120;
}

// Method kiểm tra tên hợp lệ (chỉ chứa chữ cái và khoảng trắng)
private boolean isValidName(String name) {
    return name.matches("^[a-zA-ZÀ-ỹ\\s]+$");
}

// Method cập nhật hiển thị thông tin
private void capNhatHienThiThongTin() {
    if (khachHangDangNhap != null) {
        lblWelcome.setText("Xin chào, " + khachHangDangNhap.getHoTen() + "! - Hạng: " + khachHangDangNhap.getHangKhachHang());
    }
}

// Method để hiển thị dialog đăng nhập (phiên bản cải tiến)
public static boolean showDangNhap(QuanLyBanVeMayBay quanLy) {
    JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
    JTextField txtMaKH = new JTextField(15);
    JPasswordField txtMatKhau = new JPasswordField(15);
    
    panel.add(new JLabel("Mã khách hàng:"));
    panel.add(txtMaKH);
    
    int result = JOptionPane.showConfirmDialog(null, panel, "Đăng nhập hệ thống", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    
    if (result == JOptionPane.OK_OPTION) {
        String maKH = txtMaKH.getText().trim();
        String matKhau = new String(txtMatKhau.getPassword());
        
        if (maKH.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập mã khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        

        
        // Kiểm tra thông tin đăng nhập
        UsersGUI usersGUI = new UsersGUI(quanLy);
        if (usersGUI.dangNhap(maKH, matKhau)) {
            usersGUI.setVisible(true);
            return true;
        } else {
            JOptionPane.showMessageDialog(null, 
                "Mã khách hàng hoặc mật khẩu không đúng!\nVui lòng kiểm tra lại.", 
                "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
        }
    }
    return false;
}

// Cập nhật method đăng nhập để hỗ trợ mật khẩu
public boolean dangNhap(String maKH, String matKhau) {
    khachHangDangNhap = quanLy.getDsKhachHang().timKiemTheoMa(maKH);
    
    if (khachHangDangNhap != null) {
        // Giả sử có phương thức kiểm tra mật khẩu
        // if (khachHangDangNhap.kiemTraMatKhau(matKhau)) {
        if (true) { // Tạm thời bỏ qua kiểm tra mật khẩu
            lblWelcome.setText("Xin chào, " + khachHangDangNhap.getHoTen() + "! - Hạng: " + khachHangDangNhap.getHangKhachHang());
            capNhatThongTinCaNhan();
            taiVeCuaToi();
            taiLichSu();
            return true;
        }
    }
    return false;
}


   public static void main(String[] args) {
        QuanLyBanVeMayBay quanLy = new QuanLyBanVeMayBay();
        quanLy.docDuLieuTuFile();
        UsersGUI.showDangNhap(quanLy);
    }
}
