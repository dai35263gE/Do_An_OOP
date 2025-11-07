package Main;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private DanhSachKhachHang dsKhachHang;

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
    private JButton btnXemHoaDon, btnHuyVe, btnXemChiTietVe;
    
    // Tab Lịch sử
    private JTable tableLichSu;
    private DefaultTableModel modelLichSu;
    
    // Tab Thông tin
    private JTextField txtHoTen, txtEmail, txtSoDT, txtDiaChi, txtCmnd, txtNgaySinh;
    private JComboBox<String> cbGioiTinh;
    private JButton btnCapNhatThongTin;
    private JLabel lblDiemTichLuy, lblHangKhachHang;

    public UsersGUI(QuanLyBanVeMayBay quanLy) {
        this.quanLy = quanLy;
        this.dsChuyenBay = quanLy.getDsChuyenBay();
        this.dsVe = quanLy.getDsVe();
        this.dsHoaDon = quanLy.getDsHoaDon();
        this.dsKhachHang = quanLy.getDsKhachHang();
        
        initComponents();
        setupLayout();
        setupEvents();
    }

    public boolean dangNhap(String maKH, String matKhau) {
        khachHangDangNhap = dsKhachHang.timKiemTheoMa(maKH);
        if (khachHangDangNhap != null && kiemTraMatKhau(matKhau)) {
            lblWelcome.setText("Xin chào, " + khachHangDangNhap.getHoTen() + "! - Hạng: " + khachHangDangNhap.getHangKhachHangText());
            capNhatThongTinCaNhan();
            taiVeCuaToi();
            taiLichSu();
            return true;
        }
        return false;
    }

    private boolean kiemTraMatKhau(String matKhau) {
        // Trong thực tế, nên mã hóa và so sánh mật khẩu
        return khachHangDangNhap != null && khachHangDangNhap.getMatKhau().equals(matKhau);
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

        // Lấy danh sách điểm đi, điểm đến từ danh sách chuyến bay
        java.util.Set<String> diemDiSet = new java.util.HashSet<>();
        java.util.Set<String> diemDenSet = new java.util.HashSet<>();
        
        List<ChuyenBay> danhSachCB = dsChuyenBay.getDanhSach();
        if (danhSachCB != null) {
            for (ChuyenBay cb : danhSachCB) {
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
        modelChuyenBay = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
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
        modelVeCuaToi = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableVeCuaToi = new JTable(modelVeCuaToi);
        JScrollPane scrollVeCuaToi = new JScrollPane(tableVeCuaToi);

        // Panel button
        JPanel panelButton = new JPanel(new FlowLayout());
        btnXemChiTietVe = new JButton("Xem Chi Tiết");
        btnXemHoaDon = new JButton("Xem Hóa Đơn");
        btnHuyVe = new JButton("Hủy Vé");

        panelButton.add(btnXemChiTietVe);
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
        modelLichSu = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableLichSu = new JTable(modelLichSu);
        JScrollPane scrollLichSu = new JScrollPane(tableLichSu);

        panel.add(new JLabel("Lịch sử đặt vé:"), BorderLayout.NORTH);
        panel.add(scrollLichSu, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTabThongTin() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel thông tin cơ bản
        JPanel panelThongTin = new JPanel(new GridLayout(0, 2, 10, 10));
        
        txtHoTen = new JTextField();
        txtEmail = new JTextField();
        txtSoDT = new JTextField();
        txtDiaChi = new JTextField();
        txtCmnd = new JTextField();
        txtNgaySinh = new JTextField();
        
        String[] gioiTinhOptions = {"Nam", "Nữ", "Khác"};
        cbGioiTinh = new JComboBox<>(gioiTinhOptions);
        
        btnCapNhatThongTin = new JButton("Cập Nhật Thông Tin");

        panelThongTin.add(new JLabel("Họ tên:"));
        panelThongTin.add(txtHoTen);
        panelThongTin.add(new JLabel("Email:"));
        panelThongTin.add(txtEmail);
        panelThongTin.add(new JLabel("Số điện thoại:"));
        panelThongTin.add(txtSoDT);
        panelThongTin.add(new JLabel("Địa chỉ:"));
        panelThongTin.add(txtDiaChi);
        panelThongTin.add(new JLabel("Giới tính:"));
        panelThongTin.add(cbGioiTinh);
        panelThongTin.add(new JLabel("CCCD:"));
        panelThongTin.add(txtCmnd);
        panelThongTin.add(new JLabel("Ngày Sinh (dd/MM/yyyy):"));
        panelThongTin.add(txtNgaySinh);
        panelThongTin.add(new JLabel(""));
        panelThongTin.add(btnCapNhatThongTin);

        // Panel thông tin thành viên
        JPanel panelThanhVien = new JPanel(new GridLayout(2, 2, 10, 10));
        panelThanhVien.setBorder(BorderFactory.createTitledBorder("Thông tin thành viên"));
        
        lblHangKhachHang = new JLabel("Hạng: Chưa đăng nhập");
        lblDiemTichLuy = new JLabel("Điểm tích lũy: 0");
        
        panelThanhVien.add(new JLabel("Hạng khách hàng:"));
        panelThanhVien.add(lblHangKhachHang);
        panelThanhVien.add(new JLabel("Điểm tích lũy:"));
        panelThanhVien.add(lblDiemTichLuy);

        panel.add(panelThongTin, BorderLayout.CENTER);
        panel.add(panelThanhVien, BorderLayout.SOUTH);

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
        btnXemChiTietVe.addActionListener(e -> xemChiTietVe());
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

        // Sử dụng phương thức có sẵn từ DanhSachChuyenBay
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

        // Tính giá vé với giảm giá theo hạng khách hàng
        double giaCoBan = chuyenBay.getGiaCoBan();
        double heSoGia = 1.0;
        switch (loaiVe) {
            case "THƯƠNG GIA": heSoGia = 2.0; break;
            case "PHỔ THÔNG": heSoGia = 1.2; break;
            case "TIẾT KIỆM": heSoGia = 0.9; break;
        }
        double giaVe = giaCoBan * heSoGia;
        
        // Áp dụng giảm giá theo hạng khách hàng
        double giamGia = khachHangDangNhap.tinhMucGiamGia(giaVe);
        giaVe -= giamGia;

        // Tạo vé mới
        String maVe = "VE" + System.currentTimeMillis();
        VeMayBay ve;
        
        if ("THƯƠNG GIA".equals(loaiVe)) {
            ve = new VeThuongGia( khachHangDangNhap.getMa(),maVe,new Date(),  giaVe,maChuyen,
                    "A" + chuyenBay.getSoGheTrong()+1,"Massage", 500000.0, 20, true, "Rượu vang");
        } else if("PHỔ THÔNG".equals(loaiVe)){
            ve = new VePhoThong( khachHangDangNhap.getMa(),maVe,new Date(),  giaVe,maChuyen,
                    "B" + chuyenBay.getSoGheTrong()+1, true, 5, 200000, "Cửa sổ", true);
        } else {
            ve = new VeTietKiem( khachHangDangNhap.getMa(),maVe,new Date(),  giaVe,maChuyen,
                    "B" + chuyenBay.getSoGheTrong()+1, 10, 0.1, true, 100000.0, "Khong");
        }

        // Thêm vé và cập nhật chuyến bay
        if (dsVe.them(ve)) {
            chuyenBay.setSoGheTrong(chuyenBay.getSoGheTrong() - 1);
            
            // Tạo hóa đơn
            String maHoaDon = "HD" + System.currentTimeMillis();
            List<VeMayBay> dsVe = new ArrayList<>();
            dsVe.add(ve);
            HoaDon hoaDon = new HoaDon( khachHangDangNhap, dsVe, 0,"DA_THANH_TOAN");
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
                    "Giảm giá: " + String.format("%,d VND", (int)giamGia) + "\n" +
                    "Điểm tích lũy nhận được: " + diemThuong,
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);

            taiVeCuaToi();
            taiLichSu();
            capNhatThongTinCaNhan();
        } else {
            JOptionPane.showMessageDialog(this, "Đặt vé thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void taiVeCuaToi() {
        if (khachHangDangNhap == null) return;
        
        modelVeCuaToi.setRowCount(0);
        List<VeMayBay> veCuaToi = khachHangDangNhap.getVeDaDat();
        
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
        List<VeMayBay> lichSu = khachHangDangNhap.getVeDaDat();
        
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

    private void xemChiTietVe() {
        int row = tableVeCuaToi.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một vé!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maVe = (String) modelVeCuaToi.getValueAt(row, 0);
        VeMayBay ve = dsVe.timKiemTheoMa(maVe);
        
        if (ve != null) {
            ChuyenBay cb = dsChuyenBay.timKiemTheoMa(ve.getMaChuyen());
            
            StringBuilder sb = new StringBuilder();
            sb.append("=== CHI TIẾT VÉ ===\n\n");
            sb.append("Mã vé: ").append(ve.getMaVe()).append("\n");
            sb.append("Ma khach hang: ").append(ve.getmaKH()).append("\n");
            sb.append("Chuyến bay: ").append(cb != null ? cb.getDiemDi() + " → " + cb.getDiemDen() : "N/A").append("\n");
            sb.append("Số ghế: ").append(ve.getSoGhe()).append("\n");
            sb.append("Loại vé: ").append(ve instanceof VeThuongGia ? "Thương gia" : "Phổ thông").append("\n");
            sb.append("Giá vé: ").append(String.format("%,d VND", (int)ve.getGiaVe())).append("\n");
            sb.append("Ngày đặt: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(ve.getNgayDat())).append("\n");
            sb.append("Trạng thái: ").append(ve.getTrangThai()).append("\n");

            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Chi Tiết Vé", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void xemHoaDon() {
        int row = tableVeCuaToi.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một vé!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maVe = (String) modelVeCuaToi.getValueAt(row, 0);
        
        // Tìm hóa đơn tương ứng
        List<HoaDon> hoaDonList = khachHangDangNhap.getLichSuHoaDon();
        for (HoaDon hd : hoaDonList) {
            // Hiển thị thông tin hóa đơn
            StringBuilder sb = new StringBuilder();
            sb.append("=== HÓA ĐƠN ===\n\n");
            sb.append("Mã hóa đơn: ").append(hd.getMaHoaDon()).append("\n");
            sb.append("Ngày lập: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(hd.getNgayLap())).append("\n");
            sb.append("Khách hàng: ").append(khachHangDangNhap.getHoTen()).append("\n");
            sb.append("Tổng tiền: ").append(String.format("%,d VND", (int)hd.getTongTien())).append("\n");
            sb.append("Giảm giá: ").append(String.format("%,d VND", (int)hd.getKhuyenMai())).append("\n");
            sb.append("Phí dịch vụ: ").append(String.format("%,d VND", (int)hd.getThue())).append("\n");
            sb.append("Thành tiền: ").append(String.format("%,d VND", (int)hd.getThanhTien())).append("\n");
            sb.append("Trạng thái: ").append(hd.getTrangThai()).append("\n");

            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Hóa Đơn", JOptionPane.INFORMATION_MESSAGE);
            break;
        }
    }

    private void huyVe() {
        int row = tableVeCuaToi.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một vé!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maVe = (String) modelVeCuaToi.getValueAt(row, 0);
        VeMayBay ve = dsVe.timKiemTheoMa(maVe);
        
        if (ve == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy vé!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Kiểm tra khả năng hủy vé
        String khaNangHuy = khachHangDangNhap.kiemTraKhaNangHuyVe(ve);
        if (!khaNangHuy.equals("Có thể hủy")) {
            JOptionPane.showMessageDialog(this, 
                "Không thể hủy vé:\n" + khaNangHuy, 
                "Không thể hủy", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn hủy vé " + maVe + "?",
                "Xác nhận hủy vé",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (khachHangDangNhap.huyVe(ve)) {
                // Cập nhật số ghế trống của chuyến bay
                ChuyenBay cb = dsChuyenBay.timKiemTheoMa(ve.getMaChuyen());
                if (cb != null) {
                    cb.setSoGheTrong(cb.getSoGheTrong() + 1);
                }

                JOptionPane.showMessageDialog(this, "Đã hủy vé thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                taiVeCuaToi();
                taiLichSu();
            } else {
                JOptionPane.showMessageDialog(this, "Hủy vé thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void capNhatThongTinCaNhan() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        if (khachHangDangNhap != null) {
            txtHoTen.setText(khachHangDangNhap.getHoTen());
            txtEmail.setText(khachHangDangNhap.getEmail());
            txtSoDT.setText(khachHangDangNhap.getSoDT());
            txtDiaChi.setText(khachHangDangNhap.getDiaChi());
            txtCmnd.setText(khachHangDangNhap.getCmnd());
            txtNgaySinh.setText(sdf.format(khachHangDangNhap.getNgaySinh()));
            
            // Set giới tính
            String gioiTinh = khachHangDangNhap.getGioiTinh();
            if (gioiTinh != null) {
                cbGioiTinh.setSelectedItem(gioiTinh);
            }
            
            // Cập nhật thông tin thành viên
            lblHangKhachHang.setText("Hạng: " + khachHangDangNhap.getHangKhachHangText());
            lblDiemTichLuy.setText("Điểm tích lũy: " + khachHangDangNhap.getDiemTichLuy());
        }
    }

    private void capNhatThongTin() {
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
            String gioiTinh = (String) cbGioiTinh.getSelectedItem();
            String cccd = txtCmnd.getText().trim();
            String ngaySinhStr = txtNgaySinh.getText().trim();

            // Validate dữ liệu đầu vào
            StringBuilder loi = new StringBuilder();
            
            if (hoTen.isEmpty()) loi.append("• Họ tên không được để trống\n");
            if (email.isEmpty()) loi.append("• Email không được để trống\n");
            if (soDT.isEmpty()) loi.append("• Số điện thoại không được để trống\n");
            if (diaChi.isEmpty()) loi.append("• Địa chỉ không được để trống\n");
            if (cccd.isEmpty()) loi.append("• CCCD không được để trống\n");
            if (ngaySinhStr.isEmpty()) loi.append("• Ngày sinh không được để trống\n");

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

            // Cập nhật thông tin
            if (khachHangDangNhap.capNhatThongTinCaNhan(hoTen, soDT, email, diaChi, gioiTinh)) {
                khachHangDangNhap.setCmnd(cccd);
                khachHangDangNhap.setNgaySinh(ngaySinh);
                
                JOptionPane.showMessageDialog(this,
                    "Cập nhật thông tin thành công!",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);

                // Cập nhật lại hiển thị
                capNhatThongTinCaNhan();
                lblWelcome.setText("Xin chào, " + khachHangDangNhap.getHoTen() + "! - Hạng: " + khachHangDangNhap.getHangKhachHangText());
                
            } else {
                JOptionPane.showMessageDialog(this,
                    "Cập nhật thông tin thất bại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Có lỗi xảy ra khi cập nhật thông tin:\n" + ex.getMessage(),
                "Lỗi hệ thống",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public static boolean showDangNhap(QuanLyBanVeMayBay quanLy) {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField txtMaKH = new JTextField(15);
        JPasswordField txtMatKhau = new JPasswordField(15);
        
        panel.add(new JLabel("Mã khách hàng:"));
        panel.add(txtMaKH);
        panel.add(new JLabel("Mật khẩu:"));
        panel.add(txtMatKhau);
        panel.add(new JLabel(""));
        panel.add(new JLabel(""));
        
        int result = JOptionPane.showConfirmDialog(null, panel, "Đăng nhập hệ thống", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String maKH = txtMaKH.getText().trim();
            String matKhau = new String(txtMatKhau.getPassword());
            
            if (maKH.isEmpty() || matKhau.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
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

    public static void main(String[] args) {
        QuanLyBanVeMayBay quanLy = new QuanLyBanVeMayBay();
        quanLy.docDuLieuTuFile();
        
        SwingUtilities.invokeLater(() -> {
            UsersGUI.showDangNhap(quanLy);
        });
    }
}