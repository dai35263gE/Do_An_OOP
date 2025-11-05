package Main;
import Sevice.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import Sevice.*;
import model.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainGUI extends JFrame {
    private QuanLyBanVeMayBay quanLy;
    private JTabbedPane tabbedPane;
    
    // Các panel
    private JPanel panelTrangChu;
    private JPanel panelQuanLyVe;
    private JPanel panelQuanLyChuyenBay;
    private JPanel panelQuanLyKhachHang;
    private JPanel panelThongKe;
    
    // Tables
    private JTable tableVe;
    private JTable tableChuyenBay;
    private JTable tableKhachHang;
    
    // Các component thống kê - SỬA THÀNH JPanel để chứa cả card
    private JPanel[] statCards = new JPanel[8];
    
    public MainGUI() {
        this.quanLy = new QuanLyBanVeMayBay();
        quanLy.docDuLieuTuFile();
        initComponents();
    }
    
    private void initComponents() {
        setTitle("HỆ THỐNG QUẢN LÝ BÁN VÉ MÁY BAY - Phiên bản " + QuanLyBanVeMayBay.getPhienBan());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Tạo tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Tạo các panel
        taoPanelTrangChu();
        taoPanelQuanLyVe();
        taoPanelQuanLyChuyenBay();
        taoPanelQuanLyKhachHang();
        taoPanelThongKe();
        
        // Thêm các panel vào tabbed pane
        tabbedPane.addTab("🏠 Trang Chủ", panelTrangChu);
        tabbedPane.addTab("🎫 Quản Lý Vé", panelQuanLyVe);
        tabbedPane.addTab("✈️ Thông tin Chuyến Bay", panelQuanLyChuyenBay);
        tabbedPane.addTab("👥 Thông tin Khách Hàng", panelQuanLyKhachHang);
        tabbedPane.addTab("📊 Thống Kê", panelThongKe);
        
        add(tabbedPane);
        
        // Tạo menu bar
        taoMenuBar();
    }
    
    private void taoPanelTrangChu() {
        panelTrangChu = new JPanel(new BorderLayout());
        panelTrangChu.setBackground(new Color(240, 245, 255));
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setPreferredSize(new Dimension(0, 120));
        headerPanel.setLayout(new BorderLayout());
        
        JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN LÝ BÁN VÉ MÁY BAY", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.CENTER);
        
        JLabel lblSubTitle = new JLabel("Phiên bản " + QuanLyBanVeMayBay.getPhienBan() + " | Số lượt truy cập: " + QuanLyBanVeMayBay.getSoLanTruyCap(), JLabel.CENTER);
        lblSubTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSubTitle.setForeground(Color.LIGHT_GRAY);
        headerPanel.add(lblSubTitle, BorderLayout.SOUTH);
        
        // Thống kê nhanh
        JPanel statsPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        statsPanel.setBackground(Color.WHITE);
        
        // Tạo các card thống kê
        String str1 =String.valueOf(quanLy.getDsVe().demSoLuong());
        statCards[0] = taoStatCard("Tổng số vé", str1 , "🎫", "primary");
        String str2 = String.valueOf(quanLy.getDsChuyenBay().demSoLuong());
        statCards[1] = taoStatCard("Tổng chuyến bay", str2 , "✈️", "success");
        String str3 = String.valueOf(quanLy.getDsKhachHang().demSoLuong());
        statCards[2] = taoStatCard("Tổng khách hàng", str3 , "👥", "info");
        String str4 = String.valueOf((long)quanLy.getDsVe().tinhTongDoanhThu());
        statCards[3] = taoStatCard("Doanh thu", str4 + "VND" , "💰", "warning");
        String str5 = String.valueOf(quanLy.getDsVe().demSoLuongTheoLoai("VeThuongGia"));
        statCards[4] = taoStatCard("Vé thương gia", str5 , "⭐", "primary");
        String str6 = String.valueOf(quanLy.getDsVe().demSoLuongTheoLoai("VePhoThong"));
        statCards[5] = taoStatCard("Vé phổ thông",str6, "💺", "success");
        String str7 = String.valueOf(quanLy.getDsVe().demSoLuongTheoLoai("VeTietKiem"));
        statCards[6] = taoStatCard("Vé tiết kiệm", str7 , "💸", "info");
        statCards[7] = taoStatCard("Tỷ lệ lấp đầy", "0%", "📊", "warning");
        
        for (JPanel card : statCards) {
            statsPanel.add(card);
        }
        
        // Chức năng nhanh
        JPanel quickActionsPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        quickActionsPanel.setBorder(BorderFactory.createTitledBorder("Chức năng nhanh"));
        quickActionsPanel.setBackground(Color.WHITE);
        quickActionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        String[][] actions = {
            {"Đặt vé mới", "🎫", "Đặt vé máy bay mới"},
            {"Tìm chuyến bay", "🔍", "Tìm kiếm chuyến bay"},
            {"Thống kê", "📈", "Xem báo cáo thống kê"},
            {"Quản lý", "⚙️", "Cài đặt hệ thống"}
        };
        
        for (String[] action : actions) {
            quickActionsPanel.add(taoActionButton(action[0], action[1], action[2]));
        }
        
        panelTrangChu.add(headerPanel, BorderLayout.NORTH);
        panelTrangChu.add(statsPanel, BorderLayout.CENTER);
        panelTrangChu.add(quickActionsPanel, BorderLayout.SOUTH);
    }
    
    private JPanel taoStatCard(String title, String value, String icon, String type) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(getColorByType(type));
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        card.setPreferredSize(new Dimension(150, 80));
        
        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Arial", Font.PLAIN, 24));
        
        JLabel lblValue = new JLabel(value, JLabel.CENTER);
        lblValue.setFont(new Font("Arial", Font.BOLD, 16));
        lblValue.setForeground(Color.WHITE);
        lblValue.setName("value"); // Đặt tên để dễ tìm
        
        JLabel lblTitle = new JLabel(title, JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 12));
        lblTitle.setForeground(Color.WHITE);
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(lblIcon, BorderLayout.WEST);
        contentPanel.add(lblValue, BorderLayout.CENTER);
        contentPanel.add(lblTitle, BorderLayout.SOUTH);
        
        card.add(contentPanel, BorderLayout.CENTER);
        return card;
    }
    
    private void updateStatCard(int index, String newValue) {
        JPanel card = statCards[index];
        Component[] components = card.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                Component[] subComps = ((JPanel) comp).getComponents();
                for (Component subComp : subComps) {
                    if (subComp instanceof JLabel && "value".equals(subComp.getName())) {
                        ((JLabel) subComp).setText(newValue);
                        return;
                    }
                }
            }
        }
    }
    
    private JButton taoActionButton(String text, String icon, String tooltip) {
        JButton button = new JButton("<html><center>" + icon + "<br>" + text + "</center></html>");
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        button.setToolTipText(tooltip);
        button.setPreferredSize(new Dimension(120, 80));
        button.addActionListener(e -> xuLyChucNangNhanh(text));
        return button;
    }
    
    private Color getColorByType(String type) {
        switch (type) {
            case "primary": return new Color(70, 130, 180);
            case "success": return new Color(60, 179, 113);
            case "info": return new Color(30, 144, 255);
            case "warning": return new Color(255, 165, 0);
            default: return new Color(70, 130, 180);
        }
    }
    
    private void taoPanelQuanLyVe() {
        panelQuanLyVe = new JPanel(new BorderLayout());
        
        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        String[] buttonNames = {"Thêm vé", "Sửa vé", "Xóa vé", "Tìm kiếm", "Lọc", "Làm mới"};
        for (String name : buttonNames) {
            JButton btn = new JButton(name);
            btn.addActionListener(e -> xuLyQuanLyVe(name));
            toolbar.add(btn);
        }
        
        // Bảng dữ liệu
        String[] columns = {"Mã vé", "Hành khách", "CMND", "Chuyến bay", "Loại vé", "Giá vé", "Trạng thái"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableVe = new JTable(model);
        tableVe.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tableVe);
        
        panelQuanLyVe.add(toolbar, BorderLayout.NORTH);
        panelQuanLyVe.add(scrollPane, BorderLayout.CENTER);
    }
    
    private void taoPanelQuanLyChuyenBay() {
        panelQuanLyChuyenBay = new JPanel(new BorderLayout());
        
        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        String[] buttonNames = {"Thêm chuyến", "Sửa chuyến", "Xóa chuyến", "Tìm kiếm", "Lọc", "Làm mới"};
        for (String name : buttonNames) {
            JButton btn = new JButton(name);
            btn.addActionListener(e -> xuLyQuanLyChuyenBay(name));
            toolbar.add(btn);
        }
        
        // Bảng dữ liệu
        String[] columns = {"Mã chuyến", "Điểm đi", "Điểm đến", "Giờ khởi hành", "Ghế trống", "Giá cơ bản", "Trạng thái"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        tableChuyenBay = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(tableChuyenBay);
        
        panelQuanLyChuyenBay.add(toolbar, BorderLayout.NORTH);
        panelQuanLyChuyenBay.add(scrollPane, BorderLayout.CENTER);
    }
    
    private void taoPanelQuanLyKhachHang() {
        panelQuanLyKhachHang = new JPanel(new BorderLayout());
        
        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        String[] buttonNames = {"Thêm KH", "Sửa KH", "Xóa KH", "Tìm kiếm", "Lọc", "Làm mới"};
        for (String name : buttonNames) {
            JButton btn = new JButton(name);
            btn.addActionListener(e -> xuLyQuanLyKhachHang(name));
            toolbar.add(btn);
        }
        
        // Bảng dữ liệu
        String[] columns = {"Mã KH", "Họ tên", "SĐT", "Email", "CMND", "Hạng", "Điểm tích lũy"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        tableKhachHang = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(tableKhachHang);
        
        panelQuanLyKhachHang.add(toolbar, BorderLayout.NORTH);
        panelQuanLyKhachHang.add(scrollPane, BorderLayout.CENTER);
    }
    
    private void taoPanelThongKe() {
        panelThongKe = new JPanel(new BorderLayout());
        
        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        textArea.setEditable(false);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        String[] buttonNames = {"Thống kê tổng quan", "Doanh thu", "Vé theo loại", "Khách hàng", "Chuyến bay", "Làm mới"};
        for (String name : buttonNames) {
            JButton btn = new JButton(name);
            btn.addActionListener(e -> hienThiThongKe(name, textArea));
            buttonPanel.add(btn);
        }
        
        panelThongKe.add(buttonPanel, BorderLayout.NORTH);
        panelThongKe.add(scrollPane, BorderLayout.CENTER);
    }
    
    private void taoMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // Menu File
        JMenu menuFile = new JMenu("File");
        JMenuItem itemLoad = new JMenuItem("Tải dữ liệu");
        JMenuItem itemSave = new JMenuItem("Lưu dữ liệu");
        JMenuItem itemBackup = new JMenuItem("Sao lưu");
        JMenuItem itemExit = new JMenuItem("Thoát");

        itemSave.addActionListener(e -> saveDuLieu());
        itemBackup.addActionListener(e -> backupDuLieu());
        itemExit.addActionListener(e -> thoatChuongTrinh());
        
        menuFile.add(itemLoad);
        menuFile.add(itemSave);
        menuFile.add(itemBackup);
        menuFile.addSeparator();
        menuFile.add(itemExit);
        
        // Menu Help
        JMenu menuHelp = new JMenu("Help");
        JMenuItem itemAbout = new JMenuItem("About");
        itemAbout.addActionListener(e -> hienThiAbout());
        
        menuHelp.add(itemAbout);
        
        menuBar.add(menuFile);
        menuBar.add(menuHelp);
        
        setJMenuBar(menuBar);
    }
    
    private void saveDuLieu() {
        try {
            quanLy.ghiDuLieuRaFile();
            JOptionPane.showMessageDialog(this, "Đã lưu dữ liệu thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void backupDuLieu() {
        try {
            JOptionPane.showMessageDialog(this, "Đã sao lưu dữ liệu thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi sao lưu dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void thoatChuongTrinh() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có muốn lưu dữ liệu trước khi thoát?", "Xác nhận thoát", 
            JOptionPane.YES_NO_CANCEL_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            saveDuLieu();
            System.exit(0);
        } else if (confirm == JOptionPane.NO_OPTION) {
            System.exit(0);
        }
    }
    
    private void capNhatDuLieuGUI() {
        capNhatTableVe();
        capNhatTableChuyenBay();
        capNhatTableKhachHang();
        capNhatThongKeTrangChu();
    }
    
    private void capNhatTableVe() {
        DefaultTableModel model = (DefaultTableModel) tableVe.getModel();
        model.setRowCount(0);
        
        DanhSachVeMayBay dsVe = quanLy.getDsVe();
        if (dsVe != null && dsVe.getDanhSach() != null) {
            for (VeMayBay ve : dsVe.getDanhSach()) {
                Object[] row = {
                    ve.getMaVe(),
                    ve.getHoTenKH(),
                    ve.getCmnd(),
                    ve.getmaChuyen(),
                    ve.loaiVe(),
                    String.format("%,.0f VND", ve.getGiaVe()),
                    ve.getTrangThai()
                };
                model.addRow(row);
            }
        }
    }
    
    private void capNhatTableChuyenBay() {
        DefaultTableModel model = (DefaultTableModel) tableChuyenBay.getModel();
        model.setRowCount(0);
        
        DanhSachChuyenBay dsChuyenBay = quanLy.getDsChuyenBay();
        if (dsChuyenBay != null && dsChuyenBay.getDanhSach() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            for (ChuyenBay cb : dsChuyenBay.getDanhSach()) {
                Object[] row = {
                    cb.getMaChuyen(),
                    cb.getDiemDi(),
                    cb.getDiemDen(),
                    sdf.format(cb.getGioKhoiHanh()),
                    cb.getSoGheTrong() + "/" + cb.getSoGhe(),
                    String.format("%,.0f VND", cb.getGiaCoBan()),
                    cb.getTrangThai()
                };
                model.addRow(row);
            }
        }
    }
    
    private void capNhatTableKhachHang() {
        DefaultTableModel model = (DefaultTableModel) tableKhachHang.getModel();
        model.setRowCount(0);
        
        DanhSachKhachHang dsKhachHang = quanLy.getDsKhachHang();
        if (dsKhachHang != null && dsKhachHang.getDanhSach() != null) {
            for (KhachHang kh : dsKhachHang.getDanhSach()) {
                Object[] row = {
                    kh.getMaKH(),
                    kh.getHoTen(),
                    kh.getSoDT(),
                    kh.getEmail(),
                    kh.getCmnd(),
                    kh.getHangKhachHang(),
                    String.format("%,d", kh.getDiemTichLuy())
                };
                model.addRow(row);
            }
        }
    }
    
    private void capNhatThongKeTrangChu() {
        Map<String, Object> thongKe = quanLy.thongKeTongQuan();
        
        updateStatCard(0, String.valueOf(thongKe.get("tongVe")));
        updateStatCard(1, String.valueOf(thongKe.get("tongChuyenBay")));
        updateStatCard(2, String.valueOf(thongKe.get("tongKhachHang")));
        updateStatCard(3, String.format("%,.0f VND", thongKe.get("tongDoanhThu")));
        updateStatCard(4, String.valueOf(thongKe.get("veThuongGia")));
        updateStatCard(5, String.valueOf(thongKe.get("vePhoThong")));
        updateStatCard(6, String.valueOf(thongKe.get("veTietKiem")));
        updateStatCard(7, String.format("%.1f%%", thongKe.get("tiLeLapDay")));
    }
    
    // ========== XỬ LÝ SỰ KIỆN ==========
    
    private void xuLyChucNangNhanh(String chucNang) {
        switch (chucNang) {
            case "Đặt vé mới":
                tabbedPane.setSelectedIndex(1);
                moDialogDatVe();
                break;
            case "Tìm chuyến bay":
                tabbedPane.setSelectedIndex(2);
                break;
            case "Thống kê":
                tabbedPane.setSelectedIndex(4);
                break;
            case "Quản lý":
                hienThiQuanLyHeThong();
                break;
        }
    }
    
    private void xuLyQuanLyVe(String action) {
        switch (action) {
            case "Thêm vé":
                moDialogThemVe();
                break;
            case "Sửa vé":
                // suaVe();
                break;
            case "Xóa vé":
                // xoaVe();
                break;
            case "Tìm kiếm":
                // timKiemVe();
                break;
            case "Lọc":
                // locVe();
                break;
            case "Làm mới":
                capNhatTableVe();
                break;
        }
    }
    
    private void xuLyQuanLyChuyenBay(String action) {
        switch (action) {
            case "Thêm chuyến":
                moDialogThemChuyenBay();
                break;
            case "Sửa chuyến":
                // suaChuyenBay();
                break;
            case "Xóa chuyến":
                // xoaChuyenBay();
                break;
            case "Làm mới":
                capNhatTableChuyenBay();
                break;
        }
    }
    
    private void xuLyQuanLyKhachHang(String action) {
        switch (action) {
            case "Thêm KH":
                moDialogThemKhachHang();
                break;
            case "Làm mới":
                capNhatTableKhachHang();
                break;
        }
    }
    
    private void hienThiThongKe(String loai, JTextArea textArea) {
        StringBuilder sb = new StringBuilder();
        
        switch (loai) {
            case "Thống kê tổng quan":
                Map<String, Object> thongKe = quanLy.thongKeTongQuan();
                sb.append("=== THỐNG KÊ TỔNG QUAN HỆ THỐNG ===\n\n");
                sb.append("Tổng số vé: ").append(thongKe.get("tongVe")).append("\n");
                sb.append("Tổng số chuyến bay: ").append(thongKe.get("tongChuyenBay")).append("\n");
                sb.append("Tổng số khách hàng: ").append(thongKe.get("tongKhachHang")).append("\n");
                sb.append("Tổng doanh thu: ").append(String.format("%,.0f VND", thongKe.get("tongDoanhThu"))).append("\n\n");
                
                sb.append("Phân loại vé:\n");
                sb.append("- Thương gia: ").append(thongKe.get("veThuongGia")).append(" vé\n");
                sb.append("- Phổ thông: ").append(thongKe.get("vePhoThong")).append(" vé\n");
                sb.append("- Tiết kiệm: ").append(thongKe.get("veTietKiem")).append(" vé\n");
                break;
                
            case "Doanh thu":
                Map<String, Double> doanhThu = quanLy.thongKeDoanhThu();
                sb.append("=== THỐNG KÊ DOANH THU ===\n\n");
                sb.append("Doanh thu theo loại vé:\n");
                sb.append("- Thương gia: ").append(String.format("%,.0f VND", doanhThu.get("thuongGia"))).append("\n");
                sb.append("- Phổ thông: ").append(String.format("%,.0f VND", doanhThu.get("phoThong"))).append("\n");
                sb.append("- Tiết kiệm: ").append(String.format("%,.0f VND", doanhThu.get("tietKiem"))).append("\n");
                sb.append("Tổng cộng: ").append(String.format("%,.0f VND", doanhThu.get("tongCong"))).append("\n");
                break;
                
            case "Làm mới":
                capNhatThongKeTrangChu();
                return;
        }
        
        textArea.setText(sb.toString());
    }
    
    private void moDialogDatVe() {
        JDialog dialog = new JDialog(this, "Đặt Vé Máy Bay", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Các component nhập liệu
        panel.add(new JLabel("Họ tên hành khách:"));
        panel.add(new JTextField());
        
        panel.add(new JLabel("CMND:"));
        panel.add(new JTextField());
        
        panel.add(new JLabel("Số điện thoại:"));
        panel.add(new JTextField());
        
        panel.add(new JLabel("Email:"));
        panel.add(new JTextField());
        
        panel.add(new JLabel("Chuyến bay:"));
        JComboBox<String> cbChuyenBay = new JComboBox<>(new String[]{"CB001 - HAN → SGN", "CB002 - SGN → HAN", "CB003 - HAN → DAD"});
        panel.add(cbChuyenBay);
        
        panel.add(new JLabel("Loại vé:"));
        JComboBox<String> cbLoaiVe = new JComboBox<>(new String[]{"Thương gia", "Phổ thông", "Tiết kiệm"});
        panel.add(cbLoaiVe);
        
        JButton btnDatVe = new JButton("Đặt Vé");
        JButton btnHuy = new JButton("Hủy");
        
        btnDatVe.addActionListener(e -> {
            JOptionPane.showMessageDialog(dialog, "Đặt vé thành công!");
            dialog.dispose();
            capNhatDuLieuGUI();
        });
        
        btnHuy.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnDatVe);
        buttonPanel.add(btnHuy);
        
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void hienThiQuanLyHeThong() {
        String info = "THÔNG TIN HỆ THỐNG\n\n" +
                     "Phiên bản: " + QuanLyBanVeMayBay.getPhienBan() + "\n" +
                     "Số lượt truy cập: " + QuanLyBanVeMayBay.getSoLanTruyCap() + "\n" +
                     "Dữ liệu được lưu trữ: File text\n" +
                     "Số file dữ liệu: 4\n\n" +
                     "Đường dẫn thư mục data: ./data/";
        
        JOptionPane.showMessageDialog(this, info, "Quản lý hệ thống", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void hienThiAbout() {
        String aboutText = "HỆ THỐNG QUẢN LÝ BÁN VÉ MÁY BAY\n\n" +
                          "Phiên bản: " + QuanLyBanVeMayBay.getPhienBan() + "\n" +
                          "Số lượt truy cập: " + QuanLyBanVeMayBay.getSoLanTruyCap() + "\n\n" +
                          "Đặc điểm:\n" +
                          "- Lưu trữ dữ liệu bằng file text\n" +
                          "- Giao diện đồ họa thân thiện\n" +
                          "- Quản lý toàn diện: vé, chuyến bay, khách hàng\n\n" +
                          "© 2024 - Đồ án Lập trình Hướng đối tượng";
        
        JOptionPane.showMessageDialog(this, aboutText, "About", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void moDialogThemVe() {
        JOptionPane.showMessageDialog(this, "Chức năng đang phát triển", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void moDialogThemChuyenBay() {
        JOptionPane.showMessageDialog(this, "Chức năng đang phát triển", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void moDialogThemKhachHang() {
        JOptionPane.showMessageDialog(this, "Chức năng đang phát triển", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new MainGUI().setVisible(true);
        });
    }
}