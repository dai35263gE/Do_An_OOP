package Main.components;
import Main.*;
import Main.utils.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import Sevice.QuanLyBanVeMayBay;

public class TabManager {
    private JTabbedPane tabbedPane;
    private QuanLyBanVeMayBay quanLy;
    private MainGUI mainGUI;
    
    // Các panel chính
    private JPanel panelTrangChu;
    private JPanel panelQuanLyVe;
    private JPanel panelQuanLyChuyenBay;
    private JPanel panelQuanLyKhachHang;
    private JPanel panelThongKe;
    
    // Các bảng dữ liệu
    private JTable tableVe;
    private JTable tableChuyenBay;
    private JTable tableKhachHang;
    
    // Map lưu trữ các component theo tab
    private Map<String, JComponent> tabComponents;
    
    public TabManager(MainGUI mainGUI, QuanLyBanVeMayBay quanLy) {
        this.mainGUI = mainGUI;
        this.quanLy = quanLy;
        this.tabbedPane = new JTabbedPane();
        this.tabComponents = new HashMap<>();
        initializeTabs();
    }
    
    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }
    
    private void initializeTabs() {
        taoPanelTrangChu();
        taoPanelQuanLyVe();
        taoPanelQuanLyChuyenBay();
        taoPanelQuanLyKhachHang();
        taoPanelThongKe();
        
        // Thêm các tab vào tabbed pane
        tabbedPane.addTab("🏠 Trang Chủ", panelTrangChu);
        tabbedPane.addTab("🎫 Quản Lý Vé", panelQuanLyVe);
        tabbedPane.addTab("✈️ Thông tin Chuyến Bay", panelQuanLyChuyenBay);
        tabbedPane.addTab("👥 Thông tin Khách Hàng", panelQuanLyKhachHang);
        tabbedPane.addTab("📊 Thống Kê", panelThongKe);
        
        // Đăng ký các component
        registerTabComponents();
        
        // Thêm listener cho tab change
        tabbedPane.addChangeListener(e -> onTabChanged());
    }
    
    private void registerTabComponents() {
        tabComponents.put("tableVe", tableVe);
        tabComponents.put("tableChuyenBay", tableChuyenBay);
        tabComponents.put("tableKhachHang", tableKhachHang);
        tabComponents.put("panelTrangChu", panelTrangChu);
        tabComponents.put("panelQuanLyVe", panelQuanLyVe);
        tabComponents.put("panelQuanLyChuyenBay", panelQuanLyChuyenBay);
        tabComponents.put("panelQuanLyKhachHang", panelQuanLyKhachHang);
        tabComponents.put("panelThongKe", panelThongKe);
    }
    
    private void onTabChanged() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        String tabName = tabbedPane.getTitleAt(selectedIndex);
        
        // Cập nhật dữ liệu khi chuyển tab
        switch (selectedIndex) {
            case 1: // Quản lý vé
                capNhatTableVe();
                break;
            case 2: // Quản lý chuyến bay
                capNhatTableChuyenBay();
                break;
            case 3: // Quản lý khách hàng
                capNhatTableKhachHang();
                break;
            case 4: // Thống kê
                // Có thể cập nhật thống kê ở đây
                break;
        }
        
        // Gọi sự kiện tab changed cho mainGUI (nếu cần)
        mainGUI.onTabChanged(tabName, selectedIndex);
    }
    
    // ========== TẠO CÁC PANEL TAB ==========
    
    private void taoPanelTrangChu() {
    panelTrangChu = new JPanel(new BorderLayout(0, 20));
    panelTrangChu.setBorder(new EmptyBorder(20, 20, 20, 20));
    panelTrangChu.setBackground(new Color(240, 245, 250));

    // ========== PHẦN TIÊU ĐỀ ==========
    JPanel titlePanel = new JPanel(new BorderLayout());
    titlePanel.setBackground(new Color(240, 245, 250));
    
    JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN LÝ BÁN VÉ MÁY BAY", JLabel.CENTER);
    lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
    lblTitle.setForeground(new Color(70, 130, 180));
    lblTitle.setBorder(new EmptyBorder(0, 0, 10, 0));

    JLabel lblSubTitle = new JLabel("Quản lý toàn diện - Hiệu quả tối ưu", JLabel.CENTER);
    lblSubTitle.setFont(new Font("Arial", Font.ITALIC, 16));
    lblSubTitle.setForeground(new Color(100, 100, 100));
    lblSubTitle.setBorder(new EmptyBorder(0, 0, 20, 0));

    titlePanel.add(lblTitle, BorderLayout.NORTH);
    titlePanel.add(lblSubTitle, BorderLayout.CENTER);

    // ========== PHẦN THỐNG KÊ NHANH ==========
    JPanel statsPanel = new StatCardManager(quanLy).getStatsPanel();
    statsPanel.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
        "📊 THỐNG KÊ NHANH",
        TitledBorder.CENTER,
        TitledBorder.TOP,
        new Font("Arial", Font.BOLD, 14),
        new Color(70, 130, 180)
    ));

    // ========== PHẦN CHÀO MỪNG VÀ THÔNG TIN ==========
    JPanel welcomePanel = new JPanel(new BorderLayout(10, 10));
    welcomePanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(200, 220, 240), 2),
        BorderFactory.createEmptyBorder(20, 20, 20, 20)
    ));
    welcomePanel.setBackground(Color.WHITE);

    // Phần thông tin hệ thống
    JPanel infoPanel = new JPanel(new GridLayout(0, 1, 5, 5));
    infoPanel.setBackground(Color.WHITE);
    
    String[] systemInfo = {
        "🚀 Phiên bản: " + QuanLyBanVeMayBay.getPhienBan(),
        "📈 Số lượt truy cập: " + QuanLyBanVeMayBay.getSoLanTruyCap(),
        "🕐 Thời gian hệ thống: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
        "💾 Trạng thái: Đang hoạt động"
    };
    
    for (String info : systemInfo) {
        JLabel lblInfo = new JLabel(info);
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 14));
        lblInfo.setBorder(new EmptyBorder(5, 10, 5, 10));
        infoPanel.add(lblInfo);
    }

    // Phần mô tả chức năng
    JTextArea welcomeText = new JTextArea();
    welcomeText.setText("Chào mừng đến với Hệ thống Quản lý Bán Vé Máy Bay!\n\n" +
                       "Hệ thống cung cấp các chức năng chính:\n" +
                       "• 🎫 Quản lý vé máy bay: Đặt, sửa, xóa, tìm kiếm vé\n" +
                       "• ✈️ Quản lý chuyến bay: Thêm, cập nhật thông tin chuyến bay\n" +
                       "• 👥 Quản lý khách hàng: Thông tin và lịch sử đặt vé\n" +
                       "• 📊 Thống kê và báo cáo: Doanh thu, hiệu suất kinh doanh\n\n" +
                       "💡 Mẹo: Sử dụng menu và các nút chức năng để khám phá hệ thống!");
    welcomeText.setEditable(false);
    welcomeText.setFont(new Font("Arial", Font.PLAIN, 14));
    welcomeText.setBackground(Color.WHITE);
    welcomeText.setLineWrap(true);
    welcomeText.setWrapStyleWord(true);
    welcomeText.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    welcomePanel.add(infoPanel, BorderLayout.NORTH);
    welcomePanel.add(new JScrollPane(welcomeText), BorderLayout.CENTER);

    // ========== PHẦN CHỨC NĂNG NHANH ==========
    JPanel quickActionsPanel = new JPanel(new GridLayout(2, 3, 15, 15));
    quickActionsPanel.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(new Color(60, 179, 113), 2),
        "⚡ CHỨC NĂNG NHANH",
        TitledBorder.CENTER,
        TitledBorder.TOP,
        new Font("Arial", Font.BOLD, 14),
        new Color(60, 179, 113)
    ));
    quickActionsPanel.setBackground(new Color(240, 245, 250));

    String[] quickActions = {
        "Đặt vé mới", "Tìm chuyến bay", "Thêm khách hàng",
        "Xem thống kê", "Quản lý hệ thống", "In báo cáo"
    };

    String[] icons = {"🎫", "✈️", "👥", "📊", "⚙️", "📄"};

    for (int i = 0; i < quickActions.length; i++) {
        JButton btn = new JButton("<html><center>" + icons[i] + "<br>" + quickActions[i] + "</center></html>");
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBackground(new Color(70, 130, 180));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Thêm hiệu ứng hover
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(50, 110, 160));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(70, 130, 180));
            }
        });

        final String action = quickActions[i];
        btn.addActionListener(e -> mainGUI.xuLyChucNangNhanh(action));
        
        quickActionsPanel.add(btn);
    }

    // ========== SẮP XẾP LAYOUT CHÍNH ==========
    JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
    centerPanel.setBackground(new Color(240, 245, 250));
    centerPanel.add(statsPanel, BorderLayout.NORTH);
    centerPanel.add(welcomePanel, BorderLayout.CENTER);
    centerPanel.add(quickActionsPanel, BorderLayout.SOUTH);

    panelTrangChu.add(titlePanel, BorderLayout.NORTH);
    panelTrangChu.add(centerPanel, BorderLayout.CENTER);

    // Cập nhật thống kê ngay khi khởi tạo
    SwingUtilities.invokeLater(() -> {
        StatCardManager statManager = new StatCardManager(quanLy);
        statManager.capNhatThongKeTrangChu();
    });
}



    
    private void taoPanelQuanLyVe() {
        panelQuanLyVe = new JPanel(new BorderLayout());

        // Toolbar
        JPanel toolbar = taoToolbarVe();
        
        // Bảng dữ liệu
        DefaultTableModel model = TableUtils.createVeTableModel();
        tableVe = new JTable(model);
        tableVe.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tableVe);

        panelQuanLyVe.add(toolbar, BorderLayout.NORTH);
        panelQuanLyVe.add(scrollPane, BorderLayout.CENTER);
        
        // Cập nhật dữ liệu ban đầu
        capNhatTableVe();
    }
    
    private JPanel taoToolbarVe() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        String[] buttonNames = { "Thêm vé", "Sửa vé", "Xóa vé", "Tìm kiếm", "Lọc", "Làm mới", "Xem chi tiết" };
        for (String name : buttonNames) {
            JButton btn = new JButton(name);
            btn.addActionListener(e -> mainGUI.xuLyQuanLyVe(name));
            toolbar.add(btn);
        }
        
        return toolbar;
    }
    
    private void taoPanelQuanLyChuyenBay() {
        panelQuanLyChuyenBay = new JPanel(new BorderLayout());

        // Toolbar
        JPanel toolbar = taoToolbarChuyenBay();
        
        // Bảng dữ liệu
        DefaultTableModel model = TableUtils.createChuyenBayTableModel();
        tableChuyenBay = new JTable(model);
        tableChuyenBay.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tableChuyenBay);

        panelQuanLyChuyenBay.add(toolbar, BorderLayout.NORTH);
        panelQuanLyChuyenBay.add(scrollPane, BorderLayout.CENTER);
        
        // Cập nhật dữ liệu ban đầu
        capNhatTableChuyenBay();
    }
    
    private JPanel taoToolbarChuyenBay() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        String[] buttonNames = { "Thêm chuyến", "Sửa chuyến", "Xóa chuyến", "Tìm kiếm", "Lọc", "Xem chi tiết", "Làm mới" };
        for (String name : buttonNames) {
            JButton btn = new JButton(name);
            btn.addActionListener(e -> mainGUI.xuLyQuanLyChuyenBay(name));
            toolbar.add(btn);
        }
        
        return toolbar;
    }
    
    private void taoPanelQuanLyKhachHang() {
        panelQuanLyKhachHang = new JPanel(new BorderLayout());

        // Toolbar
        JPanel toolbar = taoToolbarKhachHang();
        
        // Bảng dữ liệu
        DefaultTableModel model = TableUtils.createKhachHangTableModel();
        tableKhachHang = new JTable(model);
        tableKhachHang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tableKhachHang);

        panelQuanLyKhachHang.add(toolbar, BorderLayout.NORTH);
        panelQuanLyKhachHang.add(scrollPane, BorderLayout.CENTER);
        
        // Cập nhật dữ liệu ban đầu
        capNhatTableKhachHang();
    }
    
    private JPanel taoToolbarKhachHang() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        String[] buttonNames = { "Thêm KH", "Sửa KH", "Xóa KH", "Tìm kiếm", "Lọc", "Làm mới", "Xem hóa đơn" };
        for (String name : buttonNames) {
            JButton btn = new JButton(name);
            btn.addActionListener(e -> mainGUI.xuLyQuanLyKhachHang(name));
            toolbar.add(btn);
        }
        
        return toolbar;
    }
    
    private void taoPanelThongKe() {
        panelThongKe = new JPanel(new BorderLayout());

        // Toolbar thống kê
        JPanel buttonPanel = new JPanel(new FlowLayout());
        String[] buttonNames = { "Thống kê tổng quan", "Doanh thu", "Vé theo loại", "Khách hàng", "Chuyến bay", "Làm mới" };
        for (String name : buttonNames) {
            JButton btn = new JButton(name);
            btn.addActionListener(e -> mainGUI.hienThiThongKe(name));
            buttonPanel.add(btn);
        }

        // TextArea hiển thị kết quả thống kê
        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        
        // Lưu textArea để có thể truy cập từ bên ngoài
        tabComponents.put("thongKeTextArea", textArea);

        panelThongKe.add(buttonPanel, BorderLayout.NORTH);
        panelThongKe.add(scrollPane, BorderLayout.CENTER);
    }
    
    // ========== PHƯƠNG THỨC CẬP NHẬT TABLE ==========
    
    public void capNhatTableVe() {
        TableUtils.capNhatTableVe(tableVe, quanLy);
    }
    
    public void capNhatTableChuyenBay() {
        TableUtils.capNhatTableChuyenBay(tableChuyenBay, quanLy);
    }
    
    public void capNhatTableKhachHang() {
        TableUtils.capNhatTableKhachHang(tableKhachHang, quanLy);
    }
    
    public void capNhatTatCaTables() {
        capNhatTableVe();
        capNhatTableChuyenBay();
        capNhatTableKhachHang();
    }
    
    // ========== PHƯƠNG THỨC TRUY CẬP COMPONENT ==========
    
    public JTable getTableVe() {
        return tableVe;
    }
    
    public JTable getTableChuyenBay() {
        return tableChuyenBay;
    }
    
    public JTable getTableKhachHang() {
        return tableKhachHang;
    }
    
    public JComponent getTabComponent(String key) {
        return tabComponents.get(key);
    }
    
    public JTextArea getThongKeTextArea() {
        return (JTextArea) tabComponents.get("thongKeTextArea");
    }
    
    // ========== PHƯƠNG THỨC QUẢN LÝ TAB ==========
    
    public void chuyenTab(String tabName) {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (tabbedPane.getTitleAt(i).contains(tabName)) {
                tabbedPane.setSelectedIndex(i);
                return;
            }
        }
    }
    
    public void chuyenTab(int index) {
        if (index >= 0 && index < tabbedPane.getTabCount()) {
            tabbedPane.setSelectedIndex(index);
        }
    }
    
    public int getCurrentTabIndex() {
        return tabbedPane.getSelectedIndex();
    }
    
    public String getCurrentTabName() {
        int index = tabbedPane.getSelectedIndex();
        return index >= 0 ? tabbedPane.getTitleAt(index) : "";
    }
    
    public void themTabMoi(String title, JComponent component, String icon) {
        String tabTitle = icon != null ? icon + " " + title : title;
        tabbedPane.addTab(tabTitle, component);
        tabComponents.put("custom_" + title, component);
    }
    
    public void xoaTab(String title) {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (tabbedPane.getTitleAt(i).contains(title)) {
                tabbedPane.remove(i);
                tabComponents.remove("custom_" + title);
                return;
            }
        }
    }
    
    public void setTabEnabled(String tabName, boolean enabled) {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (tabbedPane.getTitleAt(i).contains(tabName)) {
                tabbedPane.setEnabledAt(i, enabled);
                return;
            }
        }
    }
    
    public void setTabToolTip(String tabName, String tooltip) {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (tabbedPane.getTitleAt(i).contains(tabName)) {
                tabbedPane.setToolTipTextAt(i, tooltip);
                return;
            }
        }
    }
    
    public void refreshCurrentTab() {
        int currentIndex = getCurrentTabIndex();
        switch (currentIndex) {
            case 1: // Quản lý vé
                capNhatTableVe();
                break;
            case 2: // Quản lý chuyến bay
                capNhatTableChuyenBay();
                break;
            case 3: // Quản lý khách hàng
                capNhatTableKhachHang();
                break;
        }
    }
    
    public void setTabIcons(String[] icons) {
        if (icons.length == tabbedPane.getTabCount()) {
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                String currentTitle = tabbedPane.getTitleAt(i);
                // Loại bỏ icon cũ (nếu có) và thêm icon mới
                String newTitle = icons[i] + " " + currentTitle.replaceAll("^[^\\w\\s]*\\s*", "");
                tabbedPane.setTitleAt(i, newTitle);
            }
        }
    }
    
    public void showTabNotification(int tabIndex, String message) {
        if (tabIndex >= 0 && tabIndex < tabbedPane.getTabCount()) {
            String originalTitle = tabbedPane.getTitleAt(tabIndex);
            String newTitle = originalTitle + " (!)";
            tabbedPane.setTitleAt(tabIndex, newTitle);
            tabbedPane.setToolTipTextAt(tabIndex, message);
            
            // Tự động reset sau 5 giây
            Timer timer = new Timer(5000, e -> {
                tabbedPane.setTitleAt(tabIndex, originalTitle);
                tabbedPane.setToolTipTextAt(tabIndex, null);
            });
            timer.setRepeats(false);
            timer.start();
        }
    }
    
    // Phương thức để lấy thông tin về các tab
    public String[] getTabNames() {
        String[] names = new String[tabbedPane.getTabCount()];
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            names[i] = tabbedPane.getTitleAt(i);
        }
        return names;
    }
    
    public int getTabCount() {
        return tabbedPane.getTabCount();
    }
}