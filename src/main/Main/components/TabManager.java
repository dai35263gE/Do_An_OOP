package Main.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import Main.MainGUI;
import Main.utils.TableUtils;
import Sevice.QuanLyBanVeMayBay;

public class TabManager {
  private MainGUI mainGUI;
  private QuanLyBanVeMayBay quanLy;
  private JTabbedPane tabbedPane;

  // Các table
  private JTable tableVe;
  private JTable tableChuyenBay;
  private JTable tableKhachHang;
  private JTable tableHoaDon;

  // Các component khác
  private JTextArea textAreaThongKe;
  private StatCardManager statCardManager;

  public TabManager(MainGUI mainGUI, QuanLyBanVeMayBay quanLy) {
    this.mainGUI = mainGUI;
    this.quanLy = quanLy;
    this.tabbedPane = new JTabbedPane();
    initializeTabs();
  }

  private void initializeTabs() {
    // Tab 0: Trang chủ
    tabbedPane.addTab("🏠 Trang Chủ", taoTabTrangChu());

    // Tab 1: Quản lý vé
    tabbedPane.addTab("🎫 Quản Lý Vé", taoTabQuanLyVe());

    // Tab 2: Quản lý chuyến bay
    tabbedPane.addTab("✈️ Quản Lý Chuyến Bay", taoTabQuanLyChuyenBay());

    // Tab 3: Quản lý khách hàng
    tabbedPane.addTab("👥 Quản Lý Khách Hàng", taoTabQuanLyKhachHang());

    // Tab 4: Quản lý hóa đơn
    tabbedPane.addTab("📄 Quản Lý Hóa Đơn", taoTabQuanLyHoaDon());

    // Tab 5: Thống kê
    tabbedPane.addTab("📊 Thống Kê", taoTabThongKe());

    // Thêm listener cho tab change
    tabbedPane.addChangeListener(e -> {
      int selectedIndex = tabbedPane.getSelectedIndex();
      String tabName = tabbedPane.getTitleAt(selectedIndex);
      mainGUI.onTabChanged(tabName, selectedIndex);
    });
  }

  private JPanel taoTabTrangChu() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(new EmptyBorder(20, 20, 20, 20));

    // Header
    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setBackground(new Color(70, 130, 180));
    headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

    JLabel lblTitle = new JLabel("🏠 TRANG CHỦ - TỔNG QUAN HỆ THỐNG");
    lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
    lblTitle.setForeground(Color.WHITE);
    headerPanel.add(lblTitle, BorderLayout.WEST);

    JLabel lblSubTitle = new JLabel("Phiên bản: " + QuanLyBanVeMayBay.getPhienBan());
    lblSubTitle.setFont(new Font("Arial", Font.PLAIN, 12));
    lblSubTitle.setForeground(new Color(200, 220, 240));
    headerPanel.add(lblSubTitle, BorderLayout.EAST);

    // Panel thống kê
    // Panel thông tin hệ thống
    JPanel systemInfoPanel = taoSystemInfoPanel();
    statCardManager = new StatCardManager(quanLy);
    JPanel statsPanel = statCardManager.getStatsPanel();

    // Panel chức năng nhanh
    JPanel quickActionsPanel = taoQuickActionsPanel();

    // Sắp xếp layout
    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.add(quickActionsPanel, BorderLayout.SOUTH);
    panel.add(headerPanel, BorderLayout.NORTH);
    panel.add(topPanel, BorderLayout.SOUTH);
    panel.add(systemInfoPanel, BorderLayout.NORTH);
    topPanel.add(statsPanel, BorderLayout.CENTER);

    return panel;
  }

  private JPanel taoQuickActionsPanel() {
    JPanel panel = new JPanel(new GridLayout(2, 2, 15, 15));
    panel.setBorder(BorderFactory.createTitledBorder("🚀 Chức Năng Nhanh"));
    panel.setBackground(Color.WHITE);

    String[][] actions = {
        { "Đặt vé mới", "🎫", "Thêm vé máy bay mới" },
        { "Thêm chuyến bay", "✈️", "Tạo chuyến bay mới" },
        { "Thêm khách hàng", "👥", "Đăng ký khách hàng mới" },
        { "Thống kê nâng cao", "📊", "Xem báo cáo chi tiết" }
    };

    for (String[] action : actions) {
      JButton btn = new JButton(
          "<html><center><font size=5>" + action[1] + "</font><br>" + action[0] + "</center></html>");
      btn.setBackground(new Color(70, 130, 180));
      btn.setForeground(Color.WHITE);
      btn.setFont(new Font("Arial", Font.BOLD, 12));
      btn.setToolTipText(action[2]);
      btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

      btn.addActionListener(e -> mainGUI.xuLyChucNangNhanh(action[0]));

      // Hiệu ứng hover
      btn.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseEntered(java.awt.event.MouseEvent evt) {
          btn.setBackground(new Color(50, 110, 160));
        }

        public void mouseExited(java.awt.event.MouseEvent evt) {
          btn.setBackground(new Color(70, 130, 180));
        }
      });

      panel.add(btn);
    }

    return panel;
  }

  private JPanel taoSystemInfoPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createTitledBorder("Thông Tin Hệ Thống"));
    panel.setBackground(Color.WHITE);

    JTextArea textArea = new JTextArea();
    textArea.setEditable(false);
    textArea.setBackground(new Color(240, 248, 255));
    textArea.setFont(new Font("Consolas", Font.PLAIN, 12));
    textArea.setBorder(new EmptyBorder(10, 10, 10, 10));

    // Cập nhật thông tin hệ thống
    capNhatThongTinHeThong(textArea);

    panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
    return panel;
  }

  private void capNhatThongTinHeThong(JTextArea textArea) {
    Map<String, Object> thongKe = quanLy.thongKeTongQuan();
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    StringBuilder sb = new StringBuilder();
    sb.append("THÔNG TIN HỆ THỐNG\n");
    sb.append("==================\n\n");
    sb.append("Phiên bản: ").append(QuanLyBanVeMayBay.getPhienBan()).append("\n");
    sb.append("Thời gian: ").append(sdf.format(new java.util.Date())).append("\n\n");

    sb.append("THỐNG KÊ HIỆN TẠI:\n");
    sb.append("• Tổng số vé: ").append(thongKe.get("tongVe")).append("\n");
    sb.append("• Tổng chuyến bay: ").append(thongKe.get("tongChuyenBay")).append("\n");
    sb.append("• Tổng khách hàng: ").append(thongKe.get("tongKhachHang")).append("\n");
    sb.append("• Tổng doanh thu: ").append(String.format("%,.0f", thongKe.get("tongDoanhThu"))).append("\n");
    sb.append("• Tỷ lệ lấp đầy: ").append(String.format("%.1f%%", thongKe.get("tiLeLapDay"))).append("\n\n");

    sb.append("PHÂN LOẠI VÉ:\n");
    sb.append("• Thương gia: ").append(thongKe.get("veThuongGia")).append(" vé\n");
    sb.append("• Phổ thông: ").append(thongKe.get("vePhoThong")).append(" vé\n");
    sb.append("• Tiết kiệm: ").append(thongKe.get("veTietKiem")).append(" vé\n\n");

    sb.append("HƯỚNG DẪN SỬ DỤNG:\n");
    sb.append("• Sử dụng menu hoặc các nút chức năng nhanh\n");
    sb.append("• Luôn lưu dữ liệu trước khi thoát\n");
    sb.append("• Kiểm tra cập nhật thường xuyên\n");

    textArea.setText(sb.toString());
  }

  private JPanel taoTabQuanLyVe() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(new EmptyBorder(10, 10, 10, 10));

    // Toolbar
    JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));

    String[][] veActions = {
        { "Thêm vé", "🎫", "Thêm vé mới" },
        { "Tìm kiếm", "🔍", "Tìm kiếm vé" },
        { "Làm mới", "🔄", "Làm mới dữ liệu" }
    };

    for (String[] action : veActions) {
      JButton btn = new JButton(action[0] + " " + action[1]);
      btn.setToolTipText(action[2]);
      btn.addActionListener(e -> mainGUI.xuLyQuanLyVe(action[0]));
      toolbar.add(btn);
    }

    // Table vé
    tableVe = new JTable(TableUtils.createVeTableModel());
    tableVe.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tableVe.setRowHeight(25);
    JScrollPane scrollPane = new JScrollPane(tableVe);

    panel.add(toolbar, BorderLayout.NORTH);
    panel.add(scrollPane, BorderLayout.CENTER);

    return panel;
  }

  private JPanel taoTabQuanLyChuyenBay() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(new EmptyBorder(10, 10, 10, 10));

    // Toolbar
    JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));

    String[][] cbActions = {
        { "Thêm chuyến", "✈️", "Thêm chuyến bay mới" },
        { "Sửa chuyến", "✏️", "Sửa chuyến bay" },
        { "Làm mới", "🔄", "Làm mới dữ liệu" }
    };

    for (String[] action : cbActions) {
      JButton btn = new JButton(action[0] + " " + action[1]);
      btn.setToolTipText(action[2]);
      btn.addActionListener(e -> mainGUI.xuLyQuanLyChuyenBay(action[0]));
      toolbar.add(btn);
    }

    // Table chuyến bay
    tableChuyenBay = new JTable(TableUtils.createChuyenBayTableModel());
    tableChuyenBay.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tableChuyenBay.setRowHeight(25);
    JScrollPane scrollPane = new JScrollPane(tableChuyenBay);

    panel.add(toolbar, BorderLayout.NORTH);
    panel.add(scrollPane, BorderLayout.CENTER);

    return panel;
  }

  private JPanel taoTabQuanLyKhachHang() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(new EmptyBorder(10, 10, 10, 10));

    // Toolbar
    JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));

    String[][] khActions = {
        { "Thêm KH", "👤", "Thêm khách hàng mới" },
        { "Sửa KH", "✏️", "Sửa thông tin khách hàng" },
        { "Xóa KH", "❌", "Xóa khách hàng" },
        { "Tìm kiếm", "🔍", "Tìm kiếm & lọc khách hàng" },
        { "Xem chi tiết", "👁️", "Xem chi tiết khách hàng" },
        { "Làm mới", "🔄", "Làm mới dữ liệu" }
    };

    for (String[] action : khActions) {
      JButton btn = new JButton(action[0] + " " + action[1]);
      btn.setToolTipText(action[2]);
      btn.addActionListener(e -> mainGUI.xuLyQuanLyKhachHang(action[0]));
      toolbar.add(btn);
    }

    // Table khách hàng
    tableKhachHang = new JTable(TableUtils.createKhachHangTableModel());
    tableKhachHang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tableKhachHang.setRowHeight(25);
    JScrollPane scrollPane = new JScrollPane(tableKhachHang);

    panel.add(toolbar, BorderLayout.NORTH);
    panel.add(scrollPane, BorderLayout.CENTER);

    return panel;
  }

  private JPanel taoTabQuanLyHoaDon() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(new EmptyBorder(10, 10, 10, 10));

    // Toolbar
    JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));

    String[][] hdActions = {
        { "Tìm kiếm", "🔍", "Tìm kiếm hóa đơn" },
        { "Làm mới", "🔄", "Làm mới dữ liệu" },
        { "Xử lý trạng thái", "⚙️", "Xử lý trạng thái đơn hàng" }
    };

    for (String[] action : hdActions) {
      JButton btn = new JButton(action[0] + " " + action[1]);
      btn.setToolTipText(action[2]);
      btn.addActionListener(e -> mainGUI.xuLyQuanLyHoaDon(action[0]));
      toolbar.add(btn);
    }

    // Table hóa đơn
    tableHoaDon = new JTable(TableUtils.createHoaDonTableModel());
    tableHoaDon.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tableHoaDon.setRowHeight(25);
    JScrollPane scrollPane = new JScrollPane(tableHoaDon);

    panel.add(toolbar, BorderLayout.NORTH);
    panel.add(scrollPane, BorderLayout.CENTER);

    return panel;
  }

  private JPanel taoTabThongKe() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(new EmptyBorder(10, 10, 10, 10));

    // Toolbar
    JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));

    String[][] tkActions = {
        { "Thống kê tổng quan", "📈", "Thống kê tổng quan hệ thống" },
        { "Doanh thu", "💰", "Thống kê doanh thu" },
        { "Vé theo loại", "🎫", "Thống kê vé theo loại" },
        { "Khách hàng", "👥", "Thống kê khách hàng" },
        { "Chuyến bay", "✈️", "Thống kê chuyến bay" },
        { "Thống kê nâng cao", "📊", "Thống kê nâng cao với biểu đồ" },
        { "Làm mới", "🔄", "Làm mới thống kê" }
    };

    for (String[] action : tkActions) {
      JButton btn = new JButton(action[0] + " " + action[1]);
      btn.setToolTipText(action[2]);
      btn.addActionListener(e -> mainGUI.xuLyThongKe(action[0]));
      toolbar.add(btn);
    }

    // Text area hiển thị thống kê
    textAreaThongKe = new JTextArea(20, 50);
    textAreaThongKe.setEditable(false);
    textAreaThongKe.setBackground(new Color(240, 248, 255));
    textAreaThongKe.setFont(new Font("Consolas", Font.PLAIN, 12));
    textAreaThongKe.setBorder(new EmptyBorder(10, 10, 10, 10));

    // Hiển thị thống kê tổng quan mặc định
    Map<String, Object> thongKe = quanLy.thongKeTongQuan();
    StringBuilder sb = new StringBuilder();
    sb.append("=== THỐNG KÊ TỔNG QUAN HỆ THỐNG ===\n\n");
    sb.append("Tổng số vé: ").append(thongKe.get("tongVe")).append("\n");
    sb.append("Tổng số chuyến bay: ").append(thongKe.get("tongChuyenBay")).append("\n");
    sb.append("Tổng số khách hàng: ").append(thongKe.get("tongKhachHang")).append("\n");
    sb.append("Tổng doanh thu: ").append(String.format("%,.0f VND", thongKe.get("tongDoanhThu"))).append("\n\n");

    sb.append("Phân loại vé:\n");
    sb.append("- Thương gia: ").append(thongKe.get("veThuongGia")).append(" vé\n");
    sb.append("- Phổ thông: ").append(thongKe.get("vePhoThong")).append(" vé\n");
    sb.append("- Tiết kiệm: ").append(thongKe.get("veTietKiem")).append(" vé\n\n");

    sb.append("Chọn loại thống kê từ thanh công cụ bên trên...");

    textAreaThongKe.setText(sb.toString());

    JScrollPane scrollPane = new JScrollPane(textAreaThongKe);

    panel.add(toolbar, BorderLayout.NORTH);
    panel.add(scrollPane, BorderLayout.CENTER);

    return panel;
  }

  // ========== PHƯƠNG THỨC GETTER ==========

  public JTabbedPane getTabbedPane() {
    return tabbedPane;
  }

  public JTable getTableVe() {
    return tableVe;
  }

  public JTable getTableChuyenBay() {
    return tableChuyenBay;
  }

  public JTable getTableKhachHang() {
    return tableKhachHang;
  }

  public JTable getTableHoaDon() {
    return tableHoaDon;
  }

  /**
   * Lấy JTextArea hiển thị thống kê từ tab Thống kê
   * 
   * @return JTextArea hiển thị nội dung thống kê
   */
  public JTextArea getTextAreaThongKe() {
    return textAreaThongKe;
  }

  // ========== PHƯƠNG THỨC CẬP NHẬT DỮ LIỆU ==========

  public void capNhatTableVe() {
    TableUtils.capNhatTableVe(tableVe, quanLy);
  }

  public void capNhatTableChuyenBay() {
    TableUtils.capNhatTableChuyenBay(tableChuyenBay, quanLy);
  }

  public void capNhatTableKhachHang() {
    TableUtils.capNhatTableKhachHang(tableKhachHang, quanLy);
  }

  public void capNhatTableHoaDon() {
    TableUtils.capNhatTableHoaDon(tableHoaDon, quanLy);
  }

  // ========== PHƯƠNG THỨC HỖ TRỢ ==========

  public void chuyenTab(int tabIndex) {
    if (tabIndex >= 0 && tabIndex < tabbedPane.getTabCount()) {
      tabbedPane.setSelectedIndex(tabIndex);
    }
  }

  public void showTabNotification(int tabIndex, String message) {
    if (tabIndex >= 0 && tabIndex < tabbedPane.getTabCount()) {
      String originalTitle = tabbedPane.getTitleAt(tabIndex).replace(" ⚠️", "").replace(" 🔔", "");
      tabbedPane.setTitleAt(tabIndex, originalTitle + " ⚠️");
      tabbedPane.setToolTipTextAt(tabIndex, message);

      // Tự động xóa thông báo sau 5 giây
      Timer timer = new Timer(5000, e -> {
        tabbedPane.setTitleAt(tabIndex, originalTitle);
        tabbedPane.setToolTipTextAt(tabIndex, null);
      });
      timer.setRepeats(false);
      timer.start();
    }
  }

  public void clearTabNotification(int tabIndex) {
    if (tabIndex >= 0 && tabIndex < tabbedPane.getTabCount()) {
      String originalTitle = tabbedPane.getTitleAt(tabIndex).replace(" ⚠️", "").replace(" 🔔", "");
      tabbedPane.setTitleAt(tabIndex, originalTitle);
      tabbedPane.setToolTipTextAt(tabIndex, null);
    }
  }

  /**
   * Cập nhật nội dung thống kê trong text area
   * 
   * @param content Nội dung thống kê mới
   */
  public void capNhatNoiDungThongKe(String content) {
    if (textAreaThongKe != null) {
      textAreaThongKe.setText(content);
      // Tự động scroll lên đầu
      textAreaThongKe.setCaretPosition(0);
    }
  }

  /**
   * Thêm dòng mới vào nội dung thống kê
   * 
   * @param line Dòng cần thêm
   */
  public void themDongThongKe(String line) {
    if (textAreaThongKe != null) {
      textAreaThongKe.append("\n" + line);
    }
  }

  /**
   * Xóa toàn bộ nội dung thống kê
   */
  public void xoaNoiDungThongKe() {
    if (textAreaThongKe != null) {
      textAreaThongKe.setText("");
    }
  }

  /**
   * Định dạng nội dung thống kê với tiêu đề
   * 
   * @param title   Tiêu đề
   * @param content Nội dung
   */
  public void hienThiThongKeCoDinhDang(String title, String content) {
    if (textAreaThongKe != null) {
      StringBuilder sb = new StringBuilder();
      sb.append("=== ").append(title.toUpperCase()).append(" ===\n\n");
      sb.append(content);
      sb.append("\n\n").append("=".repeat(50)).append("\n");
      textAreaThongKe.setText(sb.toString());
    }
  }
}
