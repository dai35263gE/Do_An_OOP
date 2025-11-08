package Main;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import Sevice.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
    private JSpinner spinnerNgayDi;
    private JButton btnTimChuyen, btnDatVe, btnXemTatCa;
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
        quanLy.docDuLieuTuFile();
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
            lblWelcome.setText("Xin chào, " + khachHangDangNhap.getHoTen() + "! - Hạng: "
                    + khachHangDangNhap.getHangKhachHangText());
            capNhatThongTinCaNhan();
            taiVeCuaToi();
            taiLichSu();
            return true;
        }
        return false;
    }

    private boolean kiemTraMatKhau(String matKhau) {
        return khachHangDangNhap != null && khachHangDangNhap.getMatKhau().equals(matKhau);
    }

    private void initComponents() {
        setTitle("Hệ Thống Vé Máy Bay - Khách Hàng");
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
        JPanel panel = new JPanel(new BorderLayout(10, 10)); // tab
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // nguyên khung

        JPanel panelTimKiem = new JPanel(new GridLayout(2, 4, 10, 10)); // Tiêu đề
        panelTimKiem.setBorder(BorderFactory.createTitledBorder("Tìm kiếm chuyến bay phù hợp"));

        // Tạo list điểm đi điểm đến
        List<String> diemDiList = new ArrayList<>(
                Arrays.asList("Hà Nội (HAN)", "Đà Nẵng (DAD)", "TP.HCM (SGN)", "Nha Trang (CXR)", "Phú Quốc (PQC)"));
        List<String> diemDenList = new ArrayList<>(
                Arrays.asList("Hà Nội (HAN)", "Đà Nẵng (DAD)", "TP.HCM (SGN)", "Nha Trang (CXR)", "Phú Quốc (PQC)"));
        // Thêm điểm đi điểm đến vào list
        cbDiemDi = new JComboBox<>(diemDiList.toArray(new String[0]));
        cbDiemDen = new JComboBox<>(diemDenList.toArray(new String[0]));

        // Thêm Chọn ngày đi
        SpinnerDateModel modelNgayDi = new SpinnerDateModel();
        spinnerNgayDi = new JSpinner(modelNgayDi); // <-- KHỞI TẠO spinnerNgayDi

        JSpinner.DateEditor editorNgayDi = new JSpinner.DateEditor(spinnerNgayDi, "📅 dd/MM/yyyy");
        spinnerNgayDi.setEditor(editorNgayDi);
        spinnerNgayDi.setValue(new Date());

        btnTimChuyen = new JButton("Tìm Chuyến Bay");
        panelTimKiem.add(new JLabel("Điểm đi:"));
        panelTimKiem.add(cbDiemDi);
        panelTimKiem.add(new JLabel("Điểm đến:"));
        panelTimKiem.add(cbDiemDen);
        panelTimKiem.add(new JLabel("Ngày đi (dd/MM/yyyy):"));
        panelTimKiem.add(spinnerNgayDi);
        panelTimKiem.add(new JLabel(""));
        panelTimKiem.add(btnTimChuyen);

        // Table chuyến bay
        String[] columns = { "Mã CB", "Điểm đi", "Điểm đến", "Giờ đi", "Giờ đến", "Ghế trống", "Giá cơ bản",
                "Trạng thái" };
        modelChuyenBay = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableChuyenBay = new JTable(modelChuyenBay);
        tableChuyenBay.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollChuyenBay = new JScrollPane(tableChuyenBay);
        modelChuyenBay.setRowCount(0);
        for (ChuyenBay cb : quanLy.getDsChuyenBay().getDanhSach()) {
            if (cb.getTrangThai().equals(ChuyenBay.TRANG_THAI_CHUA_BAY)) {
                modelChuyenBay.addRow(cb.toRowData());
            }
        }

        // Panel đặt vé
        JPanel panelDatVe = new JPanel(new FlowLayout());
        cbChuyenBay = new JComboBox<>();
        btnDatVe = new JButton("Đặt Vé");

        JPanel panelXemTatCa = new JPanel(new FlowLayout());
        btnXemTatCa = new JButton("Xem tất cả");

        panelDatVe.add(new JLabel("Chọn chuyến bay:"));
        panelDatVe.add(cbChuyenBay);
        panelDatVe.add(btnDatVe);
        panelDatVe.add(btnXemTatCa);

        panel.add(panelTimKiem, BorderLayout.NORTH);
        panel.add(scrollChuyenBay, BorderLayout.CENTER);
        panel.add(panelDatVe, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createTabVeCuaToi() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = { "Mã Vé", "Chuyến Bay", "Loại Vé", "Số Ghế", "Giá Vé", "Ngày Đặt", "Trạng Thái" };
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
        btnXemChiTietVe = new JButton("Xem Chi Tiết Vé");
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

        String[] columns = { "Mã Hóa Đơn", "Mã Khách Hàng", "Ngày Lập", "DS Vé", "Tổng Tiền", "Thuế", "Thành Tiền", "Trạng Thái", "PP Thanh Toán" };
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
    JPanel panel = new JPanel(new BorderLayout(15, 15));
    panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
    panel.setBackground(new Color(248, 250, 252));

    // === PANEL THÔNG TIN CÁ NHÂN ===
    JPanel panelThongTin = new JPanel(new GridBagLayout());
    panelThongTin.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
        "THÔNG TIN CÁ NHÂN",
        javax.swing.border.TitledBorder.CENTER,
        javax.swing.border.TitledBorder.TOP,
        new Font("Segoe UI", Font.BOLD, 14),
        new Color(70, 130, 180)
    ));
    panelThongTin.setBackground(Color.WHITE);
    panelThongTin.setBorder(BorderFactory.createCompoundBorder(
        panelThongTin.getBorder(),
        BorderFactory.createEmptyBorder(20, 20, 20, 20)
    ));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(8, 10, 8, 10);
    gbc.weightx = 1.0;

    // Khởi tạo components với style
    txtHoTen = createStyledTextField();
    txtEmail = createStyledTextField();
    txtSoDT = createStyledTextField();
    txtDiaChi = createStyledTextField();
    txtCmnd = createStyledTextField();
    txtNgaySinh = createStyledTextField();

    String[] gioiTinhOptions = {"Nam", "Nữ", "Khác"};
    cbGioiTinh = createStyledComboBox(gioiTinhOptions);

    btnCapNhatThongTin = createStyledButton("Cập Nhật Thông Tin", new Color(70, 130, 180));

    // Row 1: Họ tên và Email
    gbc.gridx = 0; gbc.gridy = 0;
    panelThongTin.add(createStyledLabel("Họ tên:"), gbc);
    
    gbc.gridx = 1;
    panelThongTin.add(txtHoTen, gbc);
    
    gbc.gridx = 2;
    panelThongTin.add(createStyledLabel("Email:"), gbc);
    
    gbc.gridx = 3;
    panelThongTin.add(txtEmail, gbc);

    // Row 2: Số điện thoại và Địa chỉ
    gbc.gridx = 0; gbc.gridy = 1;
    panelThongTin.add(createStyledLabel("Số điện thoại:"), gbc);
    
    gbc.gridx = 1;
    panelThongTin.add(txtSoDT, gbc);
    
    gbc.gridx = 2;
    panelThongTin.add(createStyledLabel("Địa chỉ:"), gbc);
    
    gbc.gridx = 3;
    panelThongTin.add(txtDiaChi, gbc);

    // Row 3: Giới tính và CCCD
    gbc.gridx = 0; gbc.gridy = 2;
    panelThongTin.add(createStyledLabel("Giới tính:"), gbc);
    
    gbc.gridx = 1;
    panelThongTin.add(cbGioiTinh, gbc);
    
    gbc.gridx = 2;
    panelThongTin.add(createStyledLabel(" CCCD:"), gbc);
    
    gbc.gridx = 3;
    panelThongTin.add(txtCmnd, gbc);

    // Row 4: Ngày sinh và Nút cập nhật
    gbc.gridx = 0; gbc.gridy = 3;
    panelThongTin.add(createStyledLabel("Ngày Sinh:"), gbc);
    
    gbc.gridx = 1;
    panelThongTin.add(txtNgaySinh, gbc);
    
    gbc.gridx = 2;
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.CENTER;
    panelThongTin.add(btnCapNhatThongTin, gbc);

    // === PANEL THÔNG TIN THÀNH VIÊN ===
    JPanel panelThanhVien = new JPanel(new GridBagLayout());
    panelThanhVien.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(new Color(60, 179, 113), 2),
        " THÔNG TIN THÀNH VIÊN",
        javax.swing.border.TitledBorder.CENTER,
        javax.swing.border.TitledBorder.TOP,
        new Font("Segoe UI", Font.BOLD, 14),
        new Color(60, 179, 113)
    ));
    panelThanhVien.setBackground(Color.WHITE);
    panelThanhVien.setBorder(BorderFactory.createCompoundBorder(
        panelThanhVien.getBorder(),
        BorderFactory.createEmptyBorder(15, 15, 15, 15)
    ));

    lblHangKhachHang = createInfoLabel("Hạng: Chưa đăng nhập");
    lblDiemTichLuy = createInfoLabel("Điểm tích lũy: 0");

    GridBagConstraints gbc2 = new GridBagConstraints();
    gbc2.insets = new Insets(10, 15, 10, 15);
    gbc2.fill = GridBagConstraints.HORIZONTAL;

    gbc2.gridx = 0; gbc2.gridy = 0;
    panelThanhVien.add(createStyledLabel(" Hạng khách hàng:"), gbc2);
    
    gbc2.gridx = 1;
    panelThanhVien.add(lblHangKhachHang, gbc2);
    
    gbc2.gridx = 0; gbc2.gridy = 1;
    panelThanhVien.add(createStyledLabel(" Điểm tích lũy:"), gbc2);
    
    gbc2.gridx = 1;
    panelThanhVien.add(lblDiemTichLuy, gbc2);

    // === ADD TO MAIN PANEL ===
    panel.add(panelThongTin, BorderLayout.CENTER);
    panel.add(panelThanhVien, BorderLayout.SOUTH);

    return panel;
}

// ========== CÁC PHƯƠNG THỨC HỖ TRỢ STYLE ==========

private JTextField createStyledTextField() {
    JTextField txt = new JTextField();
    txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    txt.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(200, 200, 200)),
        BorderFactory.createEmptyBorder(10, 12, 10, 12)
    ));
    txt.setBackground(new Color(252, 252, 252));
    txt.setPreferredSize(new Dimension(200, 40));
    
    // Hiệu ứng focus
    txt.addFocusListener(new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
            txt.setBackground(new Color(255, 255, 255));
        }
        
        @Override
        public void focusLost(FocusEvent e) {
            txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
            ));
            txt.setBackground(new Color(252, 252, 252));
        }
    });
    
    return txt;
}

private <T> JComboBox<T> createStyledComboBox(T[] items) {
    JComboBox<T> cb = new JComboBox<>(items);
    cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    cb.setBackground(Color.WHITE);
    cb.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(200, 200, 200)),
        BorderFactory.createEmptyBorder(8, 12, 8, 12)
    ));
    cb.setPreferredSize(new Dimension(200, 40));
    cb.setRenderer(new DefaultListCellRenderer() {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            return label;
        }
    });
    return cb;
}

private JButton createStyledButton(String text, Color color) {
    JButton btn = new JButton(text);
    btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
    btn.setBackground(color);
    btn.setForeground(Color.WHITE);
    btn.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(color.darker()),
        BorderFactory.createEmptyBorder(12, 25, 12, 25)
    ));
    btn.setFocusPainted(false);
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    
    // Hiệu ứng hover
    btn.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            btn.setBackground(color.brighter());
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.brighter()),
                BorderFactory.createEmptyBorder(12, 25, 12, 25)
            ));
        }
        public void mouseExited(java.awt.event.MouseEvent evt) {
            btn.setBackground(color);
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker()),
                BorderFactory.createEmptyBorder(12, 25, 12, 25)
            ));
        }
    });
    
    return btn;
}

private JLabel createStyledLabel(String text) {
    JLabel lbl = new JLabel(text);
    lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
    lbl.setForeground(new Color(60, 60, 60));
    return lbl;
}

private JLabel createInfoLabel(String text) {
    JLabel lbl = new JLabel(text);
    lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
    lbl.setForeground(new Color(70, 130, 180));
    lbl.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(220, 220, 220)),
        BorderFactory.createEmptyBorder(8, 15, 8, 15)
    ));
    lbl.setOpaque(true);
    lbl.setBackground(new Color(245, 245, 245));
    return lbl;
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
        btnXemTatCa.addActionListener(e -> xemTatCa());

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

        // Lấy giá trị từ JSpinner
        Date ngayDiDate = (Date) spinnerNgayDi.getValue();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        // Sử dụng phương thức có sẵn từ DanhSachChuyenBay
        List<ChuyenBay> ketQua = dsChuyenBay.timKiemTheoTuyen(diemDi, diemDen);

        // Lọc theo ngày
        if (ngayDiDate != null) {
            try {
                // Reset lại danh sách kết quả
                List<ChuyenBay> ketQuaLocNgay = new ArrayList<>();

                for (ChuyenBay cb : ketQua) {
                    // So sánh ngày (bỏ qua giờ phút)
                    Date ngayChuyenBay = cb.getGioKhoiHanh();
                    if (ngayChuyenBay != null) {
                        String ngayCBStr = sdf.format(ngayChuyenBay);
                        String ngayTimStr = sdf.format(ngayDiDate);
                        if (ngayCBStr.equals(ngayTimStr)) {
                            ketQuaLocNgay.add(cb);
                        }
                    }
                }
                ketQua = ketQuaLocNgay;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi xử lý ngày tháng: " + ex.getMessage(), "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
                return;
            }
        }

        // Lọc theo trạng thái và ghế trống
        for (ChuyenBay cb : ketQua) {
            if (cb.getSoGheTrong() > 0) {
                modelChuyenBay.addRow(cb.toRowData());
            }
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
        String[] loaiVeOptions = { "THƯƠNG GIA", "PHỔ THÔNG", "TIẾT KIỆM" };
        String loaiVe = (String) JOptionPane.showInputDialog(this,
                "Chọn loại vé:",
                "Chọn loại vé",
                JOptionPane.QUESTION_MESSAGE,
                null,
                loaiVeOptions,
                loaiVeOptions[1]);

        if (loaiVe == null)
            return;

        // Tính giá vé với giảm giá theo hạng khách hàng
        double giaCoBan = chuyenBay.getGiaCoBan();
        double heSoGia = 1.0;
        switch (loaiVe) {
            case "THƯƠNG GIA":
                heSoGia = 2.0;
                break;
            case "PHỔ THÔNG":
                heSoGia = 1.2;
                break;
            case "TIẾT KIỆM":
                heSoGia = 0.9;
                break;
        }
        double giaVe = giaCoBan * heSoGia;

        // Áp dụng giảm giá theo hạng khách hàng
        double giamGia = khachHangDangNhap.tinhMucGiamGia(giaVe);
        giaVe -= giamGia;

        // Tạo vé mới
        String maVe = "VE" + System.currentTimeMillis();
        VeMayBay ve;

        if ("THƯƠNG GIA".equals(loaiVe)) {
            ve = new VeThuongGia(khachHangDangNhap.getMa(), maVe, new Date(), giaVe, maChuyen,
                    "A" + chuyenBay.getSoGheTrong() + 1, "Massage", 500000.0, 20, true, "Rượu vang");
        } else if ("PHỔ THÔNG".equals(loaiVe)) {
            ve = new VePhoThong(khachHangDangNhap.getMa(), maVe, new Date(), giaVe, maChuyen,
                    "B" + chuyenBay.getSoGheTrong() + 1, true, 5, 200000, "Cửa sổ", true);
        } else {
            ve = new VeTietKiem(khachHangDangNhap.getMa(), maVe, new Date(), giaVe, maChuyen,
                    "B" + chuyenBay.getSoGheTrong() + 1, 10, 0.1, true, 100000.0, "Khong");
        }

        // Thêm vé và cập nhật chuyến bay
        if (dsVe.them(ve)) {
            chuyenBay.setSoGheTrong(chuyenBay.getSoGheTrong() - 1);

            // Tạo hóa đơn
            String maHoaDon = "HD" + System.currentTimeMillis();
            List<VeMayBay> dsVe = new ArrayList<>();
            dsVe.add(ve);
            HoaDon hoaDon = new HoaDon(khachHangDangNhap, dsVe, 0, "DA_THANH_TOAN");
            dsHoaDon.them(hoaDon);

            // Cập nhật điểm tích lũy
            int diemThuong = (int) (giaVe / 100000); // 1 điểm cho mỗi 100,000 VND
            khachHangDangNhap.tangDiemTichLuy(diemThuong);

            JOptionPane.showMessageDialog(this,
                    "Đặt vé thành công!\n" +
                            "Mã vé: " + maVe + "\n" +
                            "Chuyến bay: " + chuyenBay.getDiemDi() + " → " + chuyenBay.getDiemDen() + "\n" +
                            "Loại vé: " + loaiVe + "\n" +
                            "Giá vé: " + String.format("%,d VND", (int) giaVe) + "\n" +
                            "Giảm giá: " + String.format("%,d VND", (int) giamGia) + "\n" +
                            "Điểm tích lũy nhận được: " + diemThuong,
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);

            taiVeCuaToi();
            taiLichSu();
            capNhatThongTinCaNhan();
        } else {
            JOptionPane.showMessageDialog(this, "Đặt vé thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xemTatCa() {
        // Xóa dữ liệu cũ
        modelChuyenBay.setRowCount(0);

        List<ChuyenBay> tatCaChuyenBay = quanLy.getDsChuyenBay().getDanhSach();

        if (tatCaChuyenBay == null || tatCaChuyenBay.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Không có chuyến bay nào trong hệ thống!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Thống kê
        int tongSo = tatCaChuyenBay.size();
        int conGhe = 0;
        int chuaBay = 0;
        int daBay = 0;

        // Thêm dữ liệu và thống kê
        for (ChuyenBay cb : tatCaChuyenBay) {
            if (cb.getTrangThai().equals(ChuyenBay.TRANG_THAI_CHUA_BAY)) {
                modelChuyenBay.addRow(cb.toRowData());

                // Thống kê
                if (cb.getSoGheTrong() > 0)
                    conGhe++;
                chuaBay++;
            }
        }
        // Hiển thị thống kê
        String thongKe = String.format(
                "THỐNG KÊ CHUYẾN BAY:\n" +
                        "• Tổng số: %d chuyến\n" +
                        "• Còn ghế trống: %d chuyến\n" +
                        "• Chưa khởi hành: %d chuyến\n" +
                        tongSo,
                conGhe, chuaBay);

        JOptionPane.showMessageDialog(this, thongKe, "Đã hiển thị tất cả chuyến bay", JOptionPane.INFORMATION_MESSAGE);
    }

    private void taiVeCuaToi() {
        try {
            modelVeCuaToi.setRowCount(0);
            // Đọc dữ liệu mới nhất
            dsHoaDon.docFile("src/resources/data/4_HoaDons.xml");
            // Cập nhật lịch sử hóa đơn
            capNhatLichSuHoaDon();
            // Lấy và hiển thị vé
            List<VeMayBay> veCuaToi = khachHangDangNhap.getVeDaDat();
            hienThiVeTrongBang(veCuaToi);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi tải dữ liệu vé: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void capNhatLichSuHoaDon() {
        if (khachHangDangNhap == null)
            return;

        List<HoaDon> lichSuMoi = new ArrayList<>();

        for (HoaDon hd : dsHoaDon.getDanhSach()) {
            if (hd.getKhachHang() != null &&hd.getKhachHang().getMa() != null &&hd.getKhachHang().getMa().equals(khachHangDangNhap.getMa())) {lichSuMoi.add(hd);
            }
        }

        // Cập nhật lịch sử
        khachHangDangNhap.getLichSuHoaDon().clear();
        khachHangDangNhap.getLichSuHoaDon().addAll(lichSuMoi);
    }

    private void hienThiVeTrongBang(List<VeMayBay> danhSachVe) {
        if (danhSachVe == null || danhSachVe.isEmpty()) {
            modelVeCuaToi.addRow(new Object[] {
                    "", "Không có vé nào", "", "", "", "", ""
            });
            return;
        }

        int count = 0;
        for (VeMayBay ve : danhSachVe) {
            // Bỏ qua vé đã hủy
            if (ve.getTrangThai().equals(VeMayBay.TRANG_THAI_DA_HUY)) {
                continue;
            }

            ChuyenBay cb = dsChuyenBay.timKiemTheoMa(ve.getMaChuyen());
            String tenChuyen = (cb != null) ? cb.getDiemDi() + " → " + cb.getDiemDen() : "N/A";

            String loaiVe = xacDinhLoaiVe(ve);
            String trangThai = chuyenTrangThaiSangText(ve.getTrangThai());

            modelVeCuaToi.addRow(new Object[] {
                    ve.getMaVe(),
                    tenChuyen,
                    loaiVe,
                    ve.getSoGhe(),
                    String.format("%,d VND", (int) ve.getGiaVe()),
                    new SimpleDateFormat("dd/MM/yyyy").format(ve.getNgayDat()),
                    trangThai
            });
            count++;
        }

        // Thông báo kết quả
        if (count == 0) {
            JOptionPane.showMessageDialog(this,
                    "Bạn không có vé nào để hiển thị!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private String xacDinhLoaiVe(VeMayBay ve) {
        if (ve instanceof VeThuongGia)
            return "Thương gia";
        if (ve instanceof VeTietKiem)
            return "Tiết kiệm";
        return "Phổ thông";
    }

    private String chuyenTrangThaiSangText(String trangThai) {
        switch (trangThai) {
            case VeMayBay.TRANG_THAI_DA_DAT:
                return "Đã đặt";
            case VeMayBay.TRANG_THAI_DA_THANH_TOAN:
                return "Đã thanh toán";
            case VeMayBay.TRANG_THAI_DA_BAY:
                return "Đã bay";
            case VeMayBay.TRANG_THAI_DA_HUY:
                return "Đã hủy";
            default:
                return trangThai;
        }
    }

    private void taiLichSu() {
        if (khachHangDangNhap == null)
            return;

        modelLichSu.setRowCount(0);
        List<HoaDon> lichSu = khachHangDangNhap.getLichSuHoaDon();

        for (HoaDon hd : lichSu) {
    // Tạo thông tin vé chi tiết hơn
    String thongTinVe = taoThongTinVeChiTiet(hd.getDanhSachVe());
    String trangThai = chuyenTrangThaiSangText1(hd.getTrangThai());
    String phuongThucTT = chuyenPhuongThucTTSangText(hd.getPhuongThucTT());
    
    modelLichSu.addRow(new Object[] {
        hd.getMaHoaDon(),
        hd.getKhachHang().getMa(), // Hiển thị tên thay vì mã
        new SimpleDateFormat("dd/MM/yyyy HH:mm").format(hd.getNgayLap()),
        thongTinVe,
        String.format("%,d VND", (int) hd.getTongTien()),
        String.format("%,d VND", (int) hd.getThue()),
        String.format("%,d VND", (int) hd.getThanhTien()),
        trangThai,
        phuongThucTT
    });
        }
    }
    

// Các phương thức hỗ trợ
private String taoThongTinVeChiTiet(List<VeMayBay> danhSachVe) {
    StringBuilder sb = new StringBuilder();
    sb.append("<html>");
    for (int i = 0; i < danhSachVe.size(); i++) {
        VeMayBay ve = danhSachVe.get(i);
        if (i > 0) sb.append(", ");   
        sb.append(String.format("%s", 
            ve.getMaVe()
        ));
    }
    sb.append("</html>");
    return sb.toString();
}

private String chuyenTrangThaiSangText1(String trangThai) {
    switch (trangThai) {
        case HoaDon.TT_CHUA_TT: return "Chưa thanh toán";
        case HoaDon.TT_DA_TT: return "Đã thanh toán";
        case HoaDon.TT_HUY: return "Đã hủy";
        default: return trangThai;
    }
}

private String chuyenPhuongThucTTSangText(String phuongThuc) {
    switch (phuongThuc) {
        case HoaDon.PT_TIEN_MAT: return "Tiền mặt";
        case HoaDon.PT_CHUYEN_KHOAN: return "Chuyển khoản";
        case HoaDon.PT_THE: return "Thẻ tín dụng";
        case HoaDon.PT_VI_DIEN_TU: return "Ví điện tử";
        default: return phuongThuc;
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
            sb.append("Loại vé: ").append(ve instanceof VeThuongGia ? "Thương gia" : (ve.loaiVe().equals("VePhoThong") ? "Phổ Thông" : "Tiết Kiệm")).append("\n");
            sb.append("Giá vé: ").append(String.format("%,d VND", (int) ve.getGiaVe())).append("\n");
            sb.append("Ngày đặt: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(ve.getNgayDat())).append("\n");
            sb.append("Trạng thái: ").append(ve.getTrangThai()).append("\n\n");
            sb.append("=== CHI TIẾT ĐẶC THÙ ===\n\n");
            

            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Chi Tiết Vé",
                    JOptionPane.INFORMATION_MESSAGE);
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
            sb.append("Ngày lập: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(hd.getNgayLap()))
                    .append("\n");
            sb.append("Khách hàng: ").append(khachHangDangNhap.getHoTen()).append("\n");
            sb.append("Tổng tiền: ").append(String.format("%,d VND", (int) hd.getTongTien())).append("\n");
            sb.append("Giảm giá: ").append(String.format("%,d VND", (int) hd.getKhuyenMai())).append("\n");
            sb.append("Phí dịch vụ: ").append(String.format("%,d VND", (int) hd.getThue())).append("\n");
            sb.append("Thành tiền: ").append(String.format("%,d VND", (int) hd.getThanhTien())).append("\n");
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

                JOptionPane.showMessageDialog(this, "Đã hủy vé thành công!", "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
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

            if (hoTen.isEmpty())
                loi.append("• Họ tên không được để trống\n");
            if (email.isEmpty())
                loi.append("• Email không được để trống\n");
            if (soDT.isEmpty())
                loi.append("• Số điện thoại không được để trống\n");
            if (diaChi.isEmpty())
                loi.append("• Địa chỉ không được để trống\n");
            if (cccd.isEmpty())
                loi.append("• CCCD không được để trống\n");
            if (ngaySinhStr.isEmpty())
                loi.append("• Ngày sinh không được để trống\n");

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
                lblWelcome.setText("Xin chào, " + khachHangDangNhap.getHoTen() + "! - Hạng: "
                        + khachHangDangNhap.getHangKhachHangText());

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
                JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin!", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
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