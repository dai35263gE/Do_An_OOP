/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.mycompany.QuanLyVeMayBay;

/**
 *
 * @author HP
 */
// File: MainGUI.java


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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
    
    public MainGUI() {
        this.quanLy = new QuanLyBanVeMayBay();
        initComponents();
        loadDuLieu();
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
        tabbedPane.addTab("✈️ Quản Lý Chuyến Bay", panelQuanLyChuyenBay);
        tabbedPane.addTab("👥 Quản Lý Khách Hàng", panelQuanLyKhachHang);
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
        
        JLabel lblSubTitle = new JLabel("Phiên bản " + QuanLyBanVeMayBay.getPhienBan(), JLabel.CENTER);
        lblSubTitle.setFont(new Font("Arial", Font.PLAIN, 16));
        lblSubTitle.setForeground(Color.LIGHT_GRAY);
        headerPanel.add(lblSubTitle, BorderLayout.SOUTH);
        
        // Thống kê nhanh
        JPanel statsPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        statsPanel.setBackground(Color.WHITE);
        
        String[][] statsData = {
            {"Tổng số vé", "0", "🎫", "primary"},
            {"Tổng chuyến bay", "0", "✈️", "success"},
            {"Tổng khách hàng", "0", "👥", "info"},
            {"Doanh thu", "0", "💰", "warning"},
            {"Vé thương gia", "0", "⭐", "primary"},
            {"Vé phổ thông", "0", "💺", "success"},
            {"Vé tiết kiệm", "0", "💸", "info"},
            {"Tỷ lệ lấp đầy", "0%", "📊", "warning"}
        };
        
        for (String[] stat : statsData) {
            statsPanel.add(taoStatCard(stat[0], stat[1], stat[2], stat[3]));
        }
        
        // Chức năng nhanh
        JPanel quickActionsPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        quickActionsPanel.setBorder(BorderFactory.createTitledBorder("Chức năng nhanh"));
        quickActionsPanel.setBackground(Color.WHITE);
        
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
        lblValue.setFont(new Font("Arial", Font.BOLD, 20));
        lblValue.setForeground(Color.WHITE);
        
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
        
        String[] buttonNames = {"Thêm vé", "Sửa vé", "Xóa vé", "Tìm kiếm", "Lọc", "Xuất Excel", "In vé"};
        for (String name : buttonNames) {
            JButton btn = new JButton(name);
            btn.addActionListener(e -> xuLyQuanLyVe(name));
            toolbar.add(btn);
        }
        
        // Bảng dữ liệu
        String[] columns = {"Mã vé", "Hành khách", "CMND", "Chuyến bay", "Loại vé", "Giá vé", "Trạng thái"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        tableVe = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(tableVe);
        
        panelQuanLyVe.add(toolbar, BorderLayout.NORTH);
        panelQuanLyVe.add(scrollPane, BorderLayout.CENTER);
    }
    
    private void taoPanelQuanLyChuyenBay() {
        panelQuanLyChuyenBay = new JPanel(new BorderLayout());
        
        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        String[] buttonNames = {"Thêm chuyến", "Sửa chuyến", "Xóa chuyến", "Tìm kiếm", "Lọc", "Cập nhật trạng thái"};
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
        
        String[] buttonNames = {"Thêm KH", "Sửa KH", "Xóa KH", "Tìm kiếm", "Lọc", "Thống kê hạng"};
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
        String[] buttonNames = {"Thống kê tổng quan", "Doanh thu", "Vé theo loại", "Khách hàng", "Chuyến bay"};
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
        
        itemLoad.addActionListener(e -> loadDuLieu());
        itemSave.addActionListener(e -> saveDuLieu());
        itemBackup.addActionListener(e -> backupDuLieu());
        itemExit.addActionListener(e -> System.exit(0));
        
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
    
   
    
    private void loadDuLieu() {
        quanLy.khoiTaoDuLieuMau();
        capNhatDuLieuGUI();
        JOptionPane.showMessageDialog(this, "Đã tải dữ liệu thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void saveDuLieu() {
        quanLy.ghiDuLieuRaFile();
        JOptionPane.showMessageDialog(this, "Đã lưu dữ liệu thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void backupDuLieu() {
        quanLy.saoLuuDuLieu();
        JOptionPane.showMessageDialog(this, "Đã sao lưu dữ liệu thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
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
        
        // Giả lập dữ liệu - trong thực tế sẽ lấy từ quanLy.getDsVe()
        Object[][] sampleData = {
            {"VG001", "Nguyễn Văn An", "001123456789", "CB001", "THƯƠNG GIA", "3,750,000", "HOÀN TẤT"},
            {"VP001", "Trần Thị Bình", "001234567890", "CB002", "PHỔ THÔNG", "960,000", "HOÀN TẤT"},
            {"VT001", "Lê Văn Cường", "001345678901", "CB003", "TIẾT KIỆM", "480,000", "ĐẶT"}
        };
        
        for (Object[] row : sampleData) {
            model.addRow(row);
        }
    }
    
    private void capNhatTableChuyenBay() {
        DefaultTableModel model = (DefaultTableModel) tableChuyenBay.getModel();
        model.setRowCount(0);
        
        Object[][] sampleData = {
            {"CB001", "Hà Nội (HAN)", "TP.HCM (SGN)", "15/01/2024 08:00", "150/180", "1,500,000", "CHƯA BAY"},
            {"CB002", "TP.HCM (SGN)", "Đà Nẵng (DAD)", "15/01/2024 14:00", "80/120", "800,000", "CHƯA BAY"},
            {"CB003", "Đà Nẵng (DAD)", "Nha Trang (CXR)", "16/01/2024 09:30", "60/100", "600,000", "CHƯA BAY"}
        };
        
        for (Object[] row : sampleData) {
            model.addRow(row);
        }
    }
    
    private void capNhatTableKhachHang() {
        DefaultTableModel model = (DefaultTableModel) tableKhachHang.getModel();
        model.setRowCount(0);
        
        Object[][] sampleData = {
            {"KH001", "Nguyễn Văn An", "0912345678", "nguyenvanan@email.com", "001123456789", "GOLD", "6,500"},
            {"KH002", "Trần Thị Bình", "0923456789", "tranthibinh@email.com", "001234567890", "PLATINUM", "12,000"},
            {"KH003", "Lê Văn Cường", "0934567890", "levancuong@email.com", "001345678901", "SILVER", "2,500"}
        };
        
        for (Object[] row : sampleData) {
            model.addRow(row);
        }
    }
    
    private void capNhatThongKeTrangChu() {
        // Cập nhật các thống kê trên trang chủ
        // Trong thực tế sẽ lấy dữ liệu thực từ hệ thống
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
                // Mở dialog quản lý hệ thống
                break;
        }
    }
    
    private void xuLyQuanLyVe(String action) {
        switch (action) {
            case "Thêm vé":
                moDialogThemVe();
                break;
            case "Sửa vé":
                suaVe();
                break;
            case "Xóa vé":
                xoaVe();
                break;
            case "Tìm kiếm":
                timKiemVe();
                break;
            case "Lọc":
                locVe();
                break;
        }
    }
    
    private void xuLyQuanLyChuyenBay(String action) {
        switch (action) {
            case "Thêm chuyến":
                moDialogThemChuyenBay();
                break;
            case "Sửa chuyến":
                suaChuyenBay();
                break;
            case "Tìm kiếm":
                timKiemChuyenBay();
                break;
        }
    }
    
    private void xuLyQuanLyKhachHang(String action) {
        switch (action) {
            case "Thêm KH":
                moDialogThemKhachHang();
                break;
            case "Thống kê hạng":
                hienThiThongKeHangKhachHang();
                break;
        }
    }
    
    private void hienThiThongKe(String loai, JTextArea textArea) {
        StringBuilder sb = new StringBuilder();
        
        switch (loai) {
            case "Thống kê tổng quan":
                sb.append("=== THỐNG KÊ TỔNG QUAN HỆ THỐNG ===\n\n");
                sb.append("Tổng số vé: 3\n");
                sb.append("Tổng số chuyến bay: 3\n");
                sb.append("Tổng số khách hàng: 3\n");
                sb.append("Tổng doanh thu: 5,190,000 VND\n\n");
                
                sb.append("Phân loại vé:\n");
                sb.append("- Thương gia: 1 vé (33.3%)\n");
                sb.append("- Phổ thông: 1 vé (33.3%)\n");
                sb.append("- Tiết kiệm: 1 vé (33.3%)\n");
                break;
                
            case "Doanh thu":
                sb.append("=== THỐNG KÊ DOANH THU ===\n\n");
                sb.append("Doanh thu theo loại vé:\n");
                sb.append("- Thương gia: 3,750,000 VND\n");
                sb.append("- Phổ thông: 960,000 VND\n");
                sb.append("- Tiết kiệm: 480,000 VND\n");
                sb.append("Tổng cộng: 5,190,000 VND\n");
                break;
        }
        
        textArea.setText(sb.toString());
    }
    
    private void moDialogDatVe() {
        JDialog dialog = new JDialog(this, "Đặt Vé Máy Bay", true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Các component nhập liệu
        String[] labels = {"Họ tên hành khách:", "CMND:", "Số điện thoại:", "Email:", 
                          "Chuyến bay:", "Loại vé:", "Ghế ngồi:"};
        
        for (String label : labels) {
            panel.add(new JLabel(label));
            if (label.equals("Loại vé:")) {
                JComboBox<String> comboBox = new JComboBox<>(new String[]{"Thương gia", "Phổ thông", "Tiết kiệm"});
                panel.add(comboBox);
            } else if (label.equals("Chuyến bay:")) {
                JComboBox<String> comboBox = new JComboBox<>(new String[]{"CB001 - HAN → SGN", "CB002 - SGN → DAD", "CB003 - DAD → CXR"});
                panel.add(comboBox);
            } else {
                panel.add(new JTextField());
            }
        }
        
        JButton btnDatVe = new JButton("Đặt Vé");
        JButton btnHuy = new JButton("Hủy");
        
        btnDatVe.addActionListener(e -> {
            JOptionPane.showMessageDialog(dialog, "Đặt vé thành công!");
            dialog.dispose();
        });
        
        btnHuy.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnDatVe);
        buttonPanel.add(btnHuy);
        
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void moDialogThemVe() {
        // Tương tự moDialogDatVe nhưng với đầy đủ thông tin hơn
    }
    
    private void moDialogThemChuyenBay() {
        JDialog dialog = new JDialog(this, "Thêm Chuyến Bay", true);
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);
        
        // Triển khai form thêm chuyến bay
        dialog.setVisible(true);
    }
    
    private void moDialogThemKhachHang() {
        JDialog dialog = new JDialog(this, "Thêm Khách Hàng", true);
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);
        
        // Triển khai form thêm khách hàng
        dialog.setVisible(true);
    }
    
    private void hienThiAbout() {
        String aboutText = "HỆ THỐNG QUẢN LÝ BÁN VÉ MÁY BAY\n\n" +
                          "Phiên bản: " + QuanLyBanVeMayBay.getPhienBan() + "\n" +
                          "Số lượt truy cập: " + QuanLyBanVeMayBay.getSoLanTruyCap() + "\n\n" +
                          "Phát triển bởi:\n" +
                          "- Nguyễn Văn A\n" +
                          "- Trần Thị B\n" +
                          "- Lê Văn C\n\n" +
                          "© 2024 - Đồ án Lập trình Hướng đối tượng";
        
        JOptionPane.showMessageDialog(this, aboutText, "About", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Các phương thức xử lý khác...
    private void suaVe() {}
    private void xoaVe() {}
    private void timKiemVe() {}
    private void locVe() {}
    private void suaChuyenBay() {}
    private void timKiemChuyenBay() {}
    private void hienThiThongKeHangKhachHang() {}
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainGUI().setVisible(true);
        });
    }
}
