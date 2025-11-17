package Main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension; // Thêm import
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame; // Thêm import
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel; // Thêm import
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import Sevice.DanhSachChuyenBay;
import Sevice.DanhSachHoaDon;
import Sevice.DanhSachKhachHang;
import Sevice.DanhSachVeMayBay;
import Sevice.QuanLyBanVeMayBay;
// Thêm import cho ValidatorUtils
import Main.utils.ValidatorUtils;
import model.ChuyenBay;
import model.HoaDon;
import model.KhachHang;
import model.VeMayBay;
import model.VePhoThong;
import model.VeThuongGia;
import model.VeTietKiem;

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
  private JButton btnDangXuat;

  // Tab Đặt vé
  private JComboBox<String> cbDiemDi, cbDiemDen, cbChuyenBay;
  private JSpinner spinnerNgayDi;
  // === THÊM MỚI: Spinner số lượng vé ===
  private JSpinner spinnerSoLuong;
  private JButton btnTimChuyen, btnDatVe, btnXemTatCa;
  private JTable tableChuyenBay;
  private DefaultTableModel modelChuyenBay;

  // Tab Vé của tôi
  private JTable tableVeCuaToi;
  private DefaultTableModel modelVeCuaToi;
  // === SỬA ĐỔI: Thêm btnThanhToan ===
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
    if (khachHangDangNhap.dangNhap(maKH, matKhau)) {
      // Sau khi đăng nhập, cập nhật lịch sử hóa đơn từ danh sách hóa đơn toàn hệ thống
      capNhatLichSuHoaDonChoKhachHang();
      
      lblWelcome.setText("Xin chào, " + khachHangDangNhap.getHoTen() + "! - Hạng: "
          + khachHangDangNhap.getHangKhachHangText());
      capNhatThongTinCaNhan();
      taiVeCuaToi();
      taiLichSu();
      return true;
    }
    return false;
  }

  /**
   * Cập nhật lịch sử hóa đơn cho khách hàng đã đăng nhập từ danh sách hóa đơn toàn hệ thống.
   * Điều này đảm bảo rằng khách hàng có thể thấy tất cả hóa đơn của họ ngay cả những cái
   * được tải từ XML hoặc tạo bởi những phiên làm việc khác.
   */
  private void capNhatLichSuHoaDonChoKhachHang() {
    if (khachHangDangNhap == null) return;
    
    // Xóa lịch sử hóa đơn hiện tại (sẽ được nạp lại từ danh sách hóa đơn toàn hệ thống)
    khachHangDangNhap.getLichSuHoaDon().clear();
    
    // Lấy tất cả hóa đơn của khách hàng từ danh sách hóa đơn toàn hệ thống
    for (HoaDon hoaDon : dsHoaDon.getDanhSach()) {
      // Kiểm tra xem hóa đơn này có thuộc về khách hàng đang đăng nhập không
      if (hoaDon != null && hoaDon.getKhachHang() != null 
          && hoaDon.getKhachHang().getMa().equals(khachHangDangNhap.getMa())) {
        // Thêm hóa đơn vào lịch sử khách hàng (không tính điểm vì đã được cộng khi thanh toán)
        khachHangDangNhap.getLichSuHoaDon().add(hoaDon);
      }
    }
    
    // Cập nhật điểm tích lũy dựa trên tất cả hóa đơn đã thanh toán
    khachHangDangNhap.setDiemTichLuy(0);  // Reset điểm trước
    for (HoaDon hoaDon : khachHangDangNhap.getLichSuHoaDon()) {
      if (hoaDon.getTrangThai().equals(HoaDon.TT_DA_TT)) {
        khachHangDangNhap.tangDiemTichLuy(hoaDon.tinhDiemTichLuy());
      }
    }
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
    btnXemTatCa = new JButton("Xem tất cả");

    // === THÊM MỚI: Spinner số lượng vé ===
    panelDatVe.add(new JLabel("Chọn chuyến bay:"));
    panelDatVe.add(cbChuyenBay);
    panelDatVe.add(new JLabel("Số lượng:"));
    spinnerSoLuong = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1)); // Min 1, Max 10, Step 1
    spinnerSoLuong.setPreferredSize(new Dimension(50, 25));
    panelDatVe.add(spinnerSoLuong);
    // === KẾT THÚC THÊM MỚI ===

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
    // === KẾT THÚC SỬA ĐỔI ===

    panel.add(new JLabel("Vé đang có:"), BorderLayout.NORTH);
    panel.add(scrollVeCuaToi, BorderLayout.CENTER);
    panel.add(panelButton, BorderLayout.SOUTH);

    return panel;
  }

    private JPanel createTabLichSu() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    String[] columns = { "Mã Hóa Đơn", "Mã Khách Hàng", "Ngày Lập", "DS Vé", "Tổng Tiền", "Thuế", "Thành Tiền",
        "Trạng Thái", "PP Thanh Toán" };
    modelLichSu = new DefaultTableModel(columns, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    tableLichSu = new JTable(modelLichSu);
    JScrollPane scrollLichSu = new JScrollPane(tableLichSu);

    // Panel button cho lịch sử
    JPanel panelButton = new JPanel(new FlowLayout());
    JButton btnXemChiTietHD = new JButton("Xem Chi Tiết HĐ");
    JButton btnThanhToanHD = new JButton("Thanh Toán HĐ");
    JButton btnHuyHD = new JButton("Hủy Hóa Đơn");

    panelButton.add(btnXemChiTietHD);
    panelButton.add(btnThanhToanHD);
    panelButton.add(btnHuyHD);

    // Thêm sự kiện cho các nút
    btnXemChiTietHD.addActionListener(e -> xemChiTietHoaDon());
    btnThanhToanHD.addActionListener(e -> thanhToanHoaDon());
    btnHuyHD.addActionListener(e -> huyHoaDon());

    panel.add(new JLabel("Lịch sử đặt vé:"), BorderLayout.NORTH);
    panel.add(scrollLichSu, BorderLayout.CENTER);
    panel.add(panelButton, BorderLayout.SOUTH);

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
        new Color(70, 130, 180)));
    panelThongTin.setBackground(Color.WHITE);
    panelThongTin.setBorder(BorderFactory.createCompoundBorder(
        panelThongTin.getBorder(),
        BorderFactory.createEmptyBorder(20, 20, 20, 20)));

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

    String[] gioiTinhOptions = { "Nam", "Nữ" };
    cbGioiTinh = createStyledComboBox(gioiTinhOptions);

    btnCapNhatThongTin = createStyledButton("Cập Nhật Thông Tin", new Color(70, 130, 180));

    btnDangXuat = createStyledButton("Đăng Xuất", new Color(220, 53, 69));
    // Row 1: Họ tên và Email
    gbc.gridx = 0;
    gbc.gridy = 0;
    panelThongTin.add(createStyledLabel("Họ tên:"), gbc);

    gbc.gridx = 1;
    panelThongTin.add(txtHoTen, gbc);

    gbc.gridx = 2;
    panelThongTin.add(createStyledLabel("Email:"), gbc);

    gbc.gridx = 3;
    panelThongTin.add(txtEmail, gbc);

    // Row 2: Số điện thoại và Địa chỉ
    gbc.gridx = 0;
    gbc.gridy = 1;
    panelThongTin.add(createStyledLabel("Số điện thoại:"), gbc);

    gbc.gridx = 1;
    panelThongTin.add(txtSoDT, gbc);

    gbc.gridx = 2;
    panelThongTin.add(createStyledLabel("Địa chỉ:"), gbc);

    gbc.gridx = 3;
    panelThongTin.add(txtDiaChi, gbc);

    // Row 3: Giới tính và CCCD
    gbc.gridx = 0;
    gbc.gridy = 2;
    panelThongTin.add(createStyledLabel("Giới tính:"), gbc);

    gbc.gridx = 1;
    panelThongTin.add(cbGioiTinh, gbc);

    gbc.gridx = 2;
    panelThongTin.add(createStyledLabel(" CCCD:"), gbc);

    gbc.gridx = 3;
    panelThongTin.add(txtCmnd, gbc);

    // Row 4: Ngày sinh và Các nút
    gbc.gridx = 0;
    gbc.gridy = 3;
    panelThongTin.add(createStyledLabel("Ngày Sinh:"), gbc);

    gbc.gridx = 1;
    panelThongTin.add(txtNgaySinh, gbc);

    // Panel mới để chứa 2 nút
    gbc.gridx = 2;
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.CENTER;

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
    buttonPanel.setBackground(Color.WHITE); // Đặt nền trắng giống panel cha
    buttonPanel.add(btnCapNhatThongTin);
    buttonPanel.add(btnDangXuat); // Thêm nút đăng xuất

    panelThongTin.add(buttonPanel, gbc); // Thêm panel chứa 2 nút

    // === PANEL THÔNG TIN THÀNH VIÊN ===
    JPanel panelThanhVien = new JPanel(new GridBagLayout());
    panelThanhVien.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(new Color(60, 179, 113), 2),
        " THÔNG TIN THÀNH VIÊN",
        javax.swing.border.TitledBorder.CENTER,
        javax.swing.border.TitledBorder.TOP,
        new Font("Segoe UI", Font.BOLD, 14),
        new Color(60, 179, 113)));
    panelThanhVien.setBackground(Color.WHITE);
    panelThanhVien.setBorder(BorderFactory.createCompoundBorder(
        panelThanhVien.getBorder(),
        BorderFactory.createEmptyBorder(15, 15, 15, 15)));

    lblHangKhachHang = createInfoLabel("Hạng: Chưa đăng nhập");
    lblDiemTichLuy = createInfoLabel("Điểm tích lũy: 0");

    GridBagConstraints gbc2 = new GridBagConstraints();
    gbc2.insets = new Insets(10, 15, 10, 15);
    gbc2.fill = GridBagConstraints.HORIZONTAL;

    gbc2.gridx = 0;
    gbc2.gridy = 0;
    panelThanhVien.add(createStyledLabel(" Hạng khách hàng:"), gbc2);

    gbc2.gridx = 1;
    panelThanhVien.add(lblHangKhachHang, gbc2);

    gbc2.gridx = 0;
    gbc2.gridy = 1;
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
        BorderFactory.createEmptyBorder(10, 12, 10, 12)));
    txt.setBackground(new Color(252, 252, 252));
    txt.setPreferredSize(new Dimension(200, 40));

    // Hiệu ứng focus
    txt.addFocusListener(new FocusAdapter() {
      @Override
      public void focusGained(FocusEvent e) {
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        txt.setBackground(new Color(255, 255, 255));
      }

      @Override
      public void focusLost(FocusEvent e) {
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        txt.setBackground(new Color(252, 252, 252));
      }
    });

    return txt;
  }

  private <T> JComboBox<T> createStyledComboBox(T[] items) {
    JComboBox<T> cb = new JComboBox<>(items);
    cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    cb.setBackground(Color.WHITE);
    cb.setPreferredSize(new Dimension(200, 40));
    cb.setRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
          boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
            cellHasFocus);
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
        BorderFactory.createEmptyBorder(12, 25, 12, 25)));
    btn.setFocusPainted(false);
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

    // Hiệu ứng hover
    btn.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        btn.setBackground(color.brighter());
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.brighter()),
            BorderFactory.createEmptyBorder(12, 25, 12, 25)));
      }

      public void mouseExited(java.awt.event.MouseEvent evt) {
        btn.setBackground(color);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker()),
            BorderFactory.createEmptyBorder(12, 25, 12, 25)));
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
        BorderFactory.createEmptyBorder(8, 15, 8, 15)));
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

    // === SỬA ĐỔI: Thêm sự kiện cho nút Thanh Toán ===
    // Tab Vé của tôi
    btnXemChiTietVe.addActionListener(e -> xemChiTietVe());
    btnXemHoaDon.addActionListener(e -> xemHoaDon());
    btnHuyVe.addActionListener(e -> huyVe());

    // Tab Thông tin
    btnCapNhatThongTin.addActionListener(e -> capNhatThongTin());

    // === Sự kiện cho nút Đăng Xuất ===
    btnDangXuat.addActionListener(e -> xuLyDangXuat());

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

  // === SỬA ĐỔI: Viết lại hàm datVe() để hỗ trợ đặt nhiều vé ===
  private void datVe() {
    // 1. Kiểm tra cơ bản
    if (!kiemTraDangNhap())
      return;
    if (!kiemTraChonChuyenBay())
      return;

    String maChuyen = (String) cbChuyenBay.getSelectedItem();
    ChuyenBay chuyenBay = dsChuyenBay.timKiemTheoMa(maChuyen);
    int soLuongDat = (Integer) spinnerSoLuong.getValue();

    // 2. Kiểm tra chuyến bay và số lượng ghế
    if (!kiemTraChuyenBayKhaDung(chuyenBay, soLuongDat))
      return;

    // 3. Tạo danh sách để giữ vé
    List<VeMayBay> danhSachVeMoi = new ArrayList<>();
    List<String> gheDaChonTrongLanNay = new ArrayList<>(); // Chống chọn trùng ghế

    // 4. Lặp N lần (N = số lượng) để chọn vé
    for (int i = 0; i < soLuongDat; i++) {
      // Hiển thị dialog, truyền vào các ghế đã chọn (để vô hiệu hóa)
      String title = String.format("Đặt Vé [Vé %d/%d] - Chuyến %s", (i + 1), soLuongDat, maChuyen);
      VeMayBay ve = hienThiDialogDatVe(chuyenBay, title, gheDaChonTrongLanNay);

      if (ve == null) {
        // Người dùng nhấn Hủy giữa chừng
        JOptionPane.showMessageDialog(this, "Quá trình đặt vé đã bị hủy.", "Đã hủy", JOptionPane.INFORMATION_MESSAGE);
        return; // Hủy toàn bộ
      }
      danhSachVeMoi.add(ve);
      gheDaChonTrongLanNay.add(ve.getSoGhe()); // Thêm ghế vừa chọn vào danh sách
    }

    // 5. Xử lý logic đặt vé (với List)
    if (xuLyDatVe(danhSachVeMoi, chuyenBay)) {
      hienThiThongBaoThanhCong(danhSachVeMoi, chuyenBay); // Sửa hàm này
      capNhatDuLieuSauKhiDatVe();
    }
  }
  // === KẾT THÚC SỬA ĐỔI ===

  // ========== CÁC PHƯƠNG THỨC HỖ TRỢ ==========

  private boolean kiemTraDangNhap() {
    if (khachHangDangNhap == null) {
      JOptionPane.showMessageDialog(this, "Vui lòng đăng nhập!", "Lỗi", JOptionPane.ERROR_MESSAGE);
      return false;
    }
    return true;
  }

  private boolean kiemTraChonChuyenBay() {
    if (cbChuyenBay.getSelectedItem() == null) {
      JOptionPane.showMessageDialog(this, "Vui lòng chọn chuyến bay!", "Lỗi", JOptionPane.ERROR_MESSAGE);
      return false;
    }
    return true;
  }

  // Sửa: Hàm cũ giữ nguyên
  private boolean kiemTraChuyenBayKhaDung(ChuyenBay chuyenBay) {
    if (chuyenBay == null) {
      JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin chuyến bay!", "Lỗi",
          JOptionPane.ERROR_MESSAGE);
      return false;
    }
    if (chuyenBay.getSoGheTrong() <= 0) {
      JOptionPane.showMessageDialog(this, "Chuyến bay đã hết chỗ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
      return false;
    }
    if (!chuyenBay.getTrangThai().equals(ChuyenBay.TRANG_THAI_CHUA_BAY)) {
      JOptionPane.showMessageDialog(this, "Chuyến bay không khả dụng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
      return false;
    }
    return true;
  }

  // === THÊM MỚI: Hàm overload để kiểm tra số lượng ===
  private boolean kiemTraChuyenBayKhaDung(ChuyenBay chuyenBay, int soLuongDat) {
    if (!kiemTraChuyenBayKhaDung(chuyenBay)) { // Gọi hàm cũ
      return false;
    }
    // Kiểm tra thêm số lượng
    if (chuyenBay.getSoGheTrong() < soLuongDat) {
      JOptionPane.showMessageDialog(this,
          "Chuyến bay chỉ còn " + chuyenBay.getSoGheTrong() + " ghế trống.\n" +
              "Không đủ cho " + soLuongDat + " hành khách.",
          "Lỗi", JOptionPane.ERROR_MESSAGE);
      return false;
    }
    return true;
  }

  // === SỬA ĐỔI: Viết lại hàm xuLyDatVe() để hỗ trợ List<VeMayBay> ===
  private boolean xuLyDatVe(List<VeMayBay> danhSachVeMoi, ChuyenBay chuyenBay) {
    try {
      // 1. Hiển thị thông tin xác nhận dựa trên một hóa đơn tạm để tính toán tự động
      if (!hienThiThongTinVeXacNhan(danhSachVeMoi, chuyenBay)) {
        return false;
      }

      // 2. Thêm vé vào hệ thống và chuyến bay (không thay đổi giá gốc trong đối tượng vé)
      for (VeMayBay ve : danhSachVeMoi) {
        if (!dsVe.them(ve)) {
          JOptionPane.showMessageDialog(this, "Lỗi khi thêm vé " + ve.getMaVe() + " vào hệ thống!", "Lỗi",
              JOptionPane.ERROR_MESSAGE);
          return false;
        }
        if (!chuyenBay.themVe(ve)) { // Đã bao gồm cả datGhe()
          JOptionPane.showMessageDialog(this, "Lỗi khi thêm vé " + ve.getMaVe() + " vào chuyến bay!", "Lỗi",
              JOptionPane.ERROR_MESSAGE);
          dsVe.xoa(ve.getMaVe()); // Rollback
          return false;
        }
      }

      // 3. Tạo một hóa đơn CHỨA NHIỀU VÉ (hóa đơn sẽ tính giảm giá + thuế tự động)
      if (!taoHoaDon(danhSachVeMoi)) {
        JOptionPane.showMessageDialog(this, "Lỗi khi tạo hóa đơn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        // Rollback
        for (VeMayBay ve : danhSachVeMoi) {
          dsVe.xoa(ve.getMaVe());
          chuyenBay.huyGhe();
        }
        return false;
      }

      // 4. Lưu file
      quanLy.ghiDuLieuRaFile();
      return true;

    } catch (Exception e) {
      JOptionPane.showMessageDialog(this,
          "Lỗi trong quá trình đặt vé: " + e.getMessage(),
          "Lỗi hệ thống",
          JOptionPane.ERROR_MESSAGE);
      e.printStackTrace();
      return false;
    }
  }

  // === SỬA ĐỔI: Viết lại hàm taoHoaDon() để hỗ trợ List<VeMayBay> ===
  private boolean taoHoaDon(List<VeMayBay> danhSachVeMoi) {
    try {
      // Hàm tạo HoaDon đã hỗ trợ List, chỉ cần truyền vào
      String maHoaDon = "HD" + String.format("%03d", quanLy.getDsHoaDon().getDanhSach().size()+1);
      HoaDon hoaDon = new HoaDon(maHoaDon, khachHangDangNhap, danhSachVeMoi, HoaDon.PT_NONE);
      dsHoaDon.them(hoaDon);
      // CHƯA thêm vào lịch sử khách hàng và CHƯA cộng điểm tại đây.
      // Điểm chỉ được cộng khi hóa đơn được thanh toán (thanhToanHoaDon).
      return true;
    } catch (Exception e) {
      System.err.println("Lỗi khi tạo hóa đơn: " + e.getMessage());
      return false;
    }
  }

  // === SỬA ĐỔI: Viết lại hàm hienThiThongBaoThanhCong() để hỗ trợ List<VeMayBay>
  // ===
  private void hienThiThongBaoThanhCong(List<VeMayBay> danhSachVeMoi, ChuyenBay chuyenBay) {

    double tongGia = 0;
    StringBuilder veInfo = new StringBuilder();
    for (VeMayBay ve : danhSachVeMoi) {
      tongGia += ve.getGiaVe();
      veInfo.append(String.format("• Mã vé: %s (Ghế: %s, Loại: %s)\n",
          ve.getMaVe(), ve.getSoGhe(), getTenLoaiVe(ve)));
    }

    // Tính điểm sẽ nhận được khi thanh toán
    int diemThuong = (int) (tongGia / 10000);

    String message = String.format(
        "ĐẶT VÉ THÀNH CÔNG (%d VÉ)\n\n" +
            "✈️ Chuyến bay: %s → %s\n\n" +
            "📋 Thông tin vé:\n" +
            "%s\n" + // Danh sách vé
            "💰 Tổng thành tiền: %s VND\n" +
            "⭐ Điểm sẽ nhận được khi thanh toán: %d điểm\n\n" +
            "📌 Ghi chú: Vé đã được tạo nhưng chưa được thanh toán.\n" +
            "Vui lòng thanh toán hóa đơn để hoàn tất đặt vé và nhận điểm tích lũy.\n\n" +
            "Cảm ơn bạn đã sử dụng dịch vụ!",
        danhSachVeMoi.size(),
        chuyenBay.getDiemDi(),
        chuyenBay.getDiemDen(),
        veInfo.toString(),
        String.format("%,d", (int) tongGia),
        diemThuong);

    JOptionPane.showMessageDialog(this, message, "Đặt Vé Thành Công", JOptionPane.INFORMATION_MESSAGE);
  }

  private void capNhatDuLieuSauKhiDatVe() {
    // Cập nhật giao diện
    taiVeCuaToi();
    taiLichSu();
    capNhatThongTinCaNhan();

    // Làm mới danh sách chuyến bay
    timChuyenBay();
  }

  // === SỬA ĐỔI: Viết lại hàm hienThiThongTinVeXacNhan() để hỗ trợ List<VeMayBay>
  // ===
    private boolean hienThiThongTinVeXacNhan(List<VeMayBay> danhSachVe, ChuyenBay chuyenBay) {

    // Tạo một hóa đơn tạm để tận dụng logic tính toán tổng, khuyến mãi, thuế
    HoaDon tmp = new HoaDon("TMP", khachHangDangNhap, danhSachVe, HoaDon.PT_NONE);

    double tongGiaGoc = tmp.getTongTien();
    double khuyenMai = tmp.getKhuyenMai();
    double thue = tmp.getThue();
    double thanhTien = tmp.getThanhTien();

    StringBuilder veInfo = new StringBuilder();
    for (VeMayBay ve : danhSachVe) {
      veInfo.append(String.format("• Vé (Ghế %s, Loại: %s)\n",
        ve.getSoGhe(), getTenLoaiVe(ve)));
    }

    String message = String.format(
      "XÁC NHẬN THÔNG TIN ĐẶT VÉ\n\n" +
        "Chuyến bay: %s → %s\n" +
        "Số lượng: %d vé\n\n" +
        "%s\n" + // Danh sách vé
        "Giá gốc: %s VND\n" +
        "Giảm giá (Hạng %s): -%s VND\n" +
        "Thuế (5%%): +%s VND\n" +
        "Tổng thành tiền: %s VND\n\n" +
        "Bạn có chắc chắn đặt %d vé này?",
      chuyenBay.getDiemDi(),
      chuyenBay.getDiemDen(),
      danhSachVe.size(),
      veInfo.toString(),
      String.format("%,d", (int) tongGiaGoc),
      khachHangDangNhap.getHangKhachHangText(),
      String.format("%,d", (int) khuyenMai),
      String.format("%,d", (int) thue),
      String.format("%,d", (int) thanhTien),
      danhSachVe.size());

    int result = JOptionPane.showConfirmDialog(
      this,
      message,
      "Xác Nhận Đặt Vé",
      JOptionPane.YES_NO_OPTION,
      JOptionPane.QUESTION_MESSAGE);

    return result == JOptionPane.YES_OPTION;
    }

  // === SỬA ĐỔI: Sửa chữ ký hàm hienThiDialogDatVe() ===
  private VeMayBay hienThiDialogDatVe(ChuyenBay chuyenBay, String dialogTitle, List<String> gheDaChonTruoc) {
    JDialog dialog = new JDialog(this, dialogTitle, true); // Sửa tiêu đề
    dialog.setSize(700, 700);
    dialog.setLocationRelativeTo(this);
    dialog.setLayout(new BorderLayout(10, 10));

    JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

    // ========== PANEL THÔNG TIN CHUYẾN BAY ==========
    JPanel panelChuyenBay = new JPanel(new GridLayout(0, 2, 10, 5));
    panelChuyenBay.setBorder(BorderFactory.createTitledBorder("Thông tin chuyến bay"));

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    panelChuyenBay.add(new JLabel("Mã chuyến bay:"));
    panelChuyenBay.add(new JLabel(chuyenBay.getMaChuyen()));
    panelChuyenBay.add(new JLabel("Tuyến bay:"));
    panelChuyenBay.add(new JLabel(chuyenBay.getDiemDi() + " → " + chuyenBay.getDiemDen()));
    panelChuyenBay.add(new JLabel("Giờ khởi hành:"));
    panelChuyenBay.add(new JLabel(sdf.format(chuyenBay.getGioKhoiHanh())));
    panelChuyenBay.add(new JLabel("Giờ đến:"));
    panelChuyenBay.add(new JLabel(sdf.format(chuyenBay.getGioDen())));
    panelChuyenBay.add(new JLabel("Ghế trống:"));
    panelChuyenBay.add(new JLabel(String.valueOf(chuyenBay.getSoGheTrong())));

    // ========== PANEL LỰA CHỌN VÉ ==========
    JPanel panelLuaChon = new JPanel(new GridBagLayout());
    panelLuaChon.setBorder(BorderFactory.createTitledBorder("Lựa chọn vé"));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 5, 5, 5);

    // Loại vé
    gbc.gridx = 0;
    gbc.gridy = 0;
    panelLuaChon.add(new JLabel("Loại vé:"), gbc);
    JComboBox<String> cbLoaiVe = new JComboBox<>(new String[] {
        "THƯƠNG GIA", "PHỔ THÔNG", "TIẾT KIỆM"
    });
    gbc.gridx = 1;
    panelLuaChon.add(cbLoaiVe, gbc);

    // Loại ghế (sẽ thay đổi theo loại vé)
    gbc.gridx = 0;
    gbc.gridy = 1;
    panelLuaChon.add(new JLabel("Loại ghế:"), gbc);
    JComboBox<String> cbLoaiGhe = new JComboBox<>();
    gbc.gridx = 1;
    panelLuaChon.add(cbLoaiGhe, gbc);

    // Dịch vụ 1 (sẽ thay đổi theo loại vé)
    gbc.gridx = 0;
    gbc.gridy = 2;
    JLabel lblDichVu1 = new JLabel("Dịch vụ:");
    panelLuaChon.add(lblDichVu1, gbc);
    JComboBox<String> cbDichVu1 = new JComboBox<>();
    gbc.gridx = 1;
    panelLuaChon.add(cbDichVu1, gbc);

    // Dịch vụ 2 (sẽ thay đổi theo loại vé)
    gbc.gridx = 0;
    gbc.gridy = 3;
    JLabel lblDichVu2 = new JLabel("Dịch vụ bổ sung:");
    panelLuaChon.add(lblDichVu2, gbc);
    JComboBox<String> cbDichVu2 = new JComboBox<>();
    gbc.gridx = 1;
    panelLuaChon.add(cbDichVu2, gbc);

    // Hành lý
    gbc.gridx = 0;
    gbc.gridy = 4;
    panelLuaChon.add(new JLabel("Hành lý:"), gbc);
    JComboBox<String> cbHanhLy = new JComboBox<>();
    gbc.gridx = 1;
    panelLuaChon.add(cbHanhLy, gbc);

    // ========== PANEL THÔNG TIN GIÁ ==========
    JPanel panelGia = new JPanel(new GridLayout(0, 2, 10, 5));
    panelGia.setBorder(BorderFactory.createTitledBorder("Thông tin giá"));

    JLabel lblGiaCoBan = new JLabel(String.format("%,d VND", (int) chuyenBay.getGiaCoBan()));
    JLabel lblHeSoGia = new JLabel("1.0");
    JLabel lblPhiDichVu = new JLabel("0 VND");
    JLabel lblPhiHanhLy = new JLabel("0 VND");
    JLabel lblGiamGia = new JLabel("0 VND");

    panelGia.add(new JLabel("Giá cơ bản:"));
    panelGia.add(lblGiaCoBan);
    panelGia.add(new JLabel("Hệ số loại vé:"));
    panelGia.add(lblHeSoGia);
    panelGia.add(new JLabel("Phụ thu:"));
    panelGia.add(lblPhiDichVu);
    panelGia.add(new JLabel("Phí hành lý:"));
    panelGia.add(lblPhiHanhLy);
    panelGia.add(new JLabel("Tổng thành tiền:"));
    JLabel lblTongThanhTien = new JLabel(String.format("%,d VND", (int) chuyenBay.getGiaCoBan()));
    lblTongThanhTien.setFont(new Font("Arial", Font.BOLD, 14));
    lblTongThanhTien.setForeground(Color.RED);
    panelGia.add(lblTongThanhTien);

    // ========== CẬP NHẬT LỰA CHỌN THEO LOẠI VÉ ==========
    ActionListener capNhatLuaChon = e -> {
      String loaiVe = (String) cbLoaiVe.getSelectedItem();
      cbLoaiGhe.removeAllItems();
      cbDichVu1.removeAllItems();
      cbDichVu2.removeAllItems();
      cbHanhLy.removeAllItems();

      switch (loaiVe) {
        case "THƯƠNG GIA":
          lblDichVu1.setText("Dịch vụ đặc biệt:");
          lblDichVu2.setText("Dịch vụ ăn uống:");
          cbLoaiGhe.addItem("Giường nằm");
          cbDichVu1.addItem("Phòng chờ VIP, Ưu tiên lên máy bay, Xe đưa đón");
          cbDichVu1.addItem("Phòng chờ VIP, Hỗ trợ check-in riêng");
          cbDichVu1.addItem("Phòng chờ VIP, Ghế ngả hoàn toàn");
          cbDichVu2.addItem("Rượu vang cao cấp");
          cbDichVu2.addItem("Champagne");
          cbDichVu2.addItem("Cocktail đặc biệt");
          // Hành lý với gói: 25kg, 30kg, 35kg, 40kg (miễn phí 20kg)
          cbHanhLy.addItem("20kg (miễn phí)");
          cbHanhLy.addItem("25kg");
          cbHanhLy.addItem("30kg");
          cbHanhLy.addItem("35kg");
          cbHanhLy.addItem("40kg");
          break;

        case "PHỔ THÔNG":
          lblDichVu1.setText("Vị trí ghế:");
          lblDichVu2.setText("Dịch vụ ăn uống:");
          cbLoaiGhe.addItem("Ghế ngồi");
          cbDichVu1.addItem("Cửa sổ");
          cbDichVu1.addItem("Lối đi");
          cbDichVu1.addItem("Giữa");
          cbDichVu2.addItem("Không ăn uống");
          cbDichVu2.addItem("Set ăn phổ thông");
          // Hành lý với gói: 15kg, 20kg, 25kg (miễn phí 10kg)
          cbHanhLy.addItem("10kg (miễn phí)");
          cbHanhLy.addItem("15kg");
          cbHanhLy.addItem("20kg");
          cbHanhLy.addItem("25kg");
          break;

        case "TIẾT KIỆM":
          lblDichVu1.setText("Loại vé TK:");
          lblDichVu2.setText("Dịch vụ:");
          cbLoaiGhe.addItem("Ghế ngồi");
          cbDichVu1.addItem("TK không hoàn hủy");
          cbDichVu2.addItem("Không dịch vụ");
          cbHanhLy.addItem("Không hành lý");
          cbHanhLy.addItem("Có xách tay (<= 7kg)");
          break;
      }
    };

    cbLoaiVe.addActionListener(capNhatLuaChon);

    // ========== CẬP NHẬT GIÁ ==========
    Runnable capNhatGia = () -> {
      double giaCoBan = chuyenBay.getGiaCoBan();
      String loaiVe = (String) cbLoaiVe.getSelectedItem();

      double heSoGia = 1.0;
      double phuThu = 0;
      double phiHanhLy = 0;

      // Tính hệ số giá theo loại vé
      switch (loaiVe) {
        case "THƯƠNG GIA":
          heSoGia = VeThuongGia.hsg;
          phuThu = 500000;
          String loaiGheTG = (String) cbLoaiGhe.getSelectedItem();
          if ("Giường nằm".equals(loaiGheTG))
            phuThu += 100000;
          if ("Suite".equals(loaiGheTG))
            phuThu += 150000;
          // Tính phí hành lý cho vé thương gia dựa trên gói được chọn
          String hanhLyTG = (String) cbHanhLy.getSelectedItem();
          double soKgHanhLy = 20; // Mặc định 20kg miễn phí
          if ("25kg".equals(hanhLyTG))
            soKgHanhLy = 25;
          else if ("30kg".equals(hanhLyTG))
            soKgHanhLy = 30;
          else if ("35kg".equals(hanhLyTG))
            soKgHanhLy = 35;
          else if ("40kg".equals(hanhLyTG))
            soKgHanhLy = 40;
          
          // Phí hành lý = (trọng lượng - 20) * 15,000 VND
          if (soKgHanhLy > VeThuongGia.SO_KG_MIEN_PHI) {
            phiHanhLy = (soKgHanhLy - VeThuongGia.SO_KG_MIEN_PHI) * VeThuongGia.PHI_HANH_LY_THEM;
          } else {
            phiHanhLy = 0;
          }
          break;

        case "PHỔ THÔNG":
          heSoGia = VePhoThong.hsg;
          // Tính phí hành lý cho vé phổ thông dựa trên gói được chọn
          String hanhLyPT = (String) cbHanhLy.getSelectedItem();
          double soKgHanhLyPT = 10; // Mặc định 10kg miễn phí
          if ("15kg".equals(hanhLyPT))
            soKgHanhLyPT = 15;
          else if ("20kg".equals(hanhLyPT))
            soKgHanhLyPT = 20;
          else if ("25kg".equals(hanhLyPT))
            soKgHanhLyPT = 25;
          
          // Phí hành lý = (trọng lượng - 10) * VeMayBay.PHI_HANH_LY
          if (soKgHanhLyPT > VePhoThong.SO_KG_MIEN_PHI) {
            phiHanhLy = (soKgHanhLyPT - VePhoThong.SO_KG_MIEN_PHI) * VeMayBay.PHI_HANH_LY;
          } else {
            phiHanhLy = 0;
          }
          // Thêm phí ăn uống
          String anUongPT = (String) cbDichVu2.getSelectedItem();
          if ("Set ăn phổ thông".equals(anUongPT))
            phuThu = 150000;
          else
            phuThu = 0;
          break;

        case "TIẾT KIỆM":
          heSoGia = VeTietKiem.hsg;
          phuThu = 100000;
          // Điều chỉnh theo loại vé TK
          String loaiVeTK = (String) cbLoaiGhe.getSelectedItem();
          if ("Tiết kiệm linh hoạt".equals(loaiVeTK)) {
            heSoGia = 0.85;
            phuThu = 150000;
          } else if ("Tiết kiệm siêu rẻ".equals(loaiVeTK)) {
            heSoGia = 0.8;
            phuThu = 200000;
          }
          // Thêm phí hành lý
          String hanhLyTK = (String) cbHanhLy.getSelectedItem();
          if ("7kg xách tay".equals(hanhLyTK))
            phiHanhLy = 50;
          if ("10kg ký gửi".equals(hanhLyTK))
            phiHanhLy = 100000;
          break;
      }

      double tongGiaTruocGiam = giaCoBan * heSoGia + phuThu + phiHanhLy;
      double giamGia = khachHangDangNhap.tinhMucGiamGia(tongGiaTruocGiam);
      double thanhTien = tongGiaTruocGiam - giamGia;

      // Cập nhật hiển thị
      lblHeSoGia.setText(String.format("%.1f", heSoGia));
      lblPhiDichVu.setText(String.format("%,d VND", (int) phuThu));
      lblPhiHanhLy.setText(String.format("%,d VND", (int) phiHanhLy));
      lblGiamGia.setText(String.format("%,d VND", (int) giamGia));
      lblTongThanhTien.setText(String.format("%,d VND", (int) thanhTien));
    };

    // Thêm listener cho tất cả combobox
    ActionListener capNhatGiaListener = e -> capNhatGia.run();
    cbLoaiVe.addActionListener(capNhatGiaListener); // Sửa: Thêm lại listener này
    cbLoaiGhe.addActionListener(capNhatGiaListener);
    cbDichVu1.addActionListener(capNhatGiaListener);
    cbDichVu2.addActionListener(capNhatGiaListener);
    cbHanhLy.addActionListener(capNhatGiaListener);

    // ========== PANEL BUTTON ==========
    JPanel panelButton = new JPanel(new FlowLayout());
    JButton btnDatVe = new JButton("Chọn Ghế"); // Sửa: Đổi tên nút
    JButton btnHuy = new JButton("Hủy");

    final VeMayBay[] veResult = { null };

    btnDatVe.addActionListener(e -> {
      String loaiVe = (String) cbLoaiVe.getSelectedItem();
      
      // Map Vietnamese names to ticket class codes
      String loaiVeCode = loaiVe.equals("THƯƠNG GIA") ? "VeThuongGia" :
                          loaiVe.equals("PHỔ THÔNG") ? "VePhoThong" : "VeTietKiem";

      // === SỬA ĐỔI: Gọi moDialogChonGhe với loại vé để lọc ghế ===
      String soGhe = moDialogChonGhe(chuyenBay, gheDaChonTruoc, loaiVeCode);
      if (soGhe == null) {
        // Người dùng nhấn Hủy trong dialog chọn ghế
        return; // Quay lại dialog đặt vé, không đóng
      }

      double tongGia = Double.parseDouble(lblTongThanhTien.getText().replaceAll("[^0-9]", ""));

      // Tạo vé theo loại
      switch (loaiVe) {
        case "THƯƠNG GIA":
          String maVe = "VG" + String.format("%03d", quanLy.getDsVe().demSoLuongTheoLoai("VeThuongGia")+1);
          String dichVuGiaiTri = (String) cbDichVu1.getSelectedItem();
          String dichVuAnUong = (String) cbDichVu2.getSelectedItem();
          double phiDichVuTG = Double.parseDouble(lblPhiDichVu.getText().replaceAll("[^0-9]", ""));
          
          // Lấy trọng lượng hành lý từ lựa chọn
          String hanhLyTG = (String) cbHanhLy.getSelectedItem();
          double soKgHanhLy = 20;
          if ("25kg".equals(hanhLyTG))
            soKgHanhLy = 25;
          else if ("30kg".equals(hanhLyTG))
            soKgHanhLy = 30;
          else if ("35kg".equals(hanhLyTG))
            soKgHanhLy = 35;
          else if ("40kg".equals(hanhLyTG))
            soKgHanhLy = 40;
          
          veResult[0] = new VeThuongGia(
              khachHangDangNhap.getMa(), maVe, chuyenBay.getGioKhoiHanh(), tongGia, // Sửa: Dùng ngày bay của CB
              chuyenBay.getMaChuyen(), soGhe, dichVuGiaiTri,
              phiDichVuTG, true, soKgHanhLy, dichVuAnUong);
          break;

        case "PHỔ THÔNG":
          String maVe1 = "VP" + String.format("%03d", quanLy.getDsVe().demSoLuongTheoLoai("VePhoThong")+1);
          String viTriGhe = (String) cbDichVu1.getSelectedItem();
          boolean coAnUong = !"Không ăn uống".equals(cbDichVu2.getSelectedItem());
          
          // Lấy trọng lượng hành lý từ lựa chọn
          String hanhLyPT2 = (String) cbHanhLy.getSelectedItem();
          int soKgHanhLyPT2 = 10;
          if ("15kg".equals(hanhLyPT2))
            soKgHanhLyPT2 = 15;
          else if ("20kg".equals(hanhLyPT2))
            soKgHanhLyPT2 = 20;
          else if ("25kg".equals(hanhLyPT2))
            soKgHanhLyPT2 = 25;
          
          veResult[0] = new VePhoThong(
              khachHangDangNhap.getMa(), maVe1, chuyenBay.getGioKhoiHanh(), tongGia, // Sửa: Dùng ngày bay của CB
              chuyenBay.getMaChuyen(), soGhe, coAnUong,
              soKgHanhLyPT2, viTriGhe, true);
          break;

        case "TIẾT KIỆM":
        String hanhLyTK = (String) cbHanhLy.getSelectedItem();
          String maVe2 = "VT" + String.format("%03d", quanLy.getDsVe().demSoLuongTheoLoai("VeTietKiem")+1);
          veResult[0] = new VeTietKiem(
              khachHangDangNhap.getMa(), maVe2, chuyenBay.getGioKhoiHanh(), tongGia, // Sửa: Dùng ngày bay của CB
              chuyenBay.getMaChuyen(), soGhe, hanhLyTK.equals("Không hành lý") ? true : false);
          break;
      }

      dialog.dispose();
    });

    btnHuy.addActionListener(e -> {
      veResult[0] = null; // Đảm bảo trả về null khi hủy
      dialog.dispose();
    });

    panelButton.add(btnDatVe);
    panelButton.add(btnHuy);

    // ========== SẮP XẾP LAYOUT ==========
    JPanel panelContent = new JPanel(new GridLayout(3, 1, 10, 10));
    panelContent.add(panelChuyenBay);
    panelContent.add(panelLuaChon);
    panelContent.add(panelGia);

    mainPanel.add(panelContent, BorderLayout.CENTER);
    mainPanel.add(panelButton, BorderLayout.SOUTH);

    dialog.add(mainPanel);

    // Khởi tạo giá trị mặc định
    capNhatLuaChon.actionPerformed(null);
    capNhatGia.run(); // Sửa: Chạy capNhatGia() lần đầu

    dialog.setVisible(true);
    return veResult[0];
  }


  private String getTenLoaiVe(VeMayBay ve) {
    if (ve instanceof VeThuongGia)
      return "Thương gia";
    if (ve instanceof VePhoThong)
      return "Phổ thông";
    if (ve instanceof VeTietKiem)
      return "Tiết kiệm";
    return "Không xác định";
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
            "• Chưa khởi hành: %d chuyến", // Xóa \n+tongSo
        tongSo,
        conGhe, chuaBay);

    JOptionPane.showMessageDialog(this, thongKe, "Đã hiển thị tất cả chuyến bay", JOptionPane.INFORMATION_MESSAGE);
  }

  private void taiVeCuaToi() {
    try {
      modelVeCuaToi.setRowCount(0);

      // SỬA: Không cần đọc file, chỉ cần cập nhật
      // dsHoaDon.docFile("src/resources/data/4_HoaDons.xml");

      // Cập nhật lịch sử hóa đơn từ dsHoaDon đã tải lúc khởi động
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

    // SỬA: Dùng dsHoaDon từ quanLy, không đọc file
    for (HoaDon hd : quanLy.getDsHoaDon().getDanhSach()) {
      if (hd.getKhachHang() != null && hd.getKhachHang().getMa() != null
          && hd.getKhachHang().getMa().equals(khachHangDangNhap.getMa())) {
        lichSuMoi.add(hd);
      }
    }

    // Cập nhật lịch sử
    khachHangDangNhap.getLichSuHoaDon().clear();
    khachHangDangNhap.getLichSuHoaDon().addAll(lichSuMoi);
  }

  private void hienThiVeTrongBang(List<VeMayBay> danhSachVe) {
    modelVeCuaToi.setRowCount(0); // Sửa: Luôn xóa bảng trước khi tải

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
    if (count == 0 && (danhSachVe != null && danhSachVe.size() > 0)) { // Sửa: Chỉ thông báo nếu có vé nhưng bị ẩn (vé
                                                                       // hủy)
      JOptionPane.showMessageDialog(this,
          "Bạn không có vé nào (chưa bị hủy) để hiển thị!",
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
        return "Chưa thanh toán"; // Sửa: Rõ nghĩa hơn
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
      if (i > 0)
        sb.append(", ");
      sb.append(String.format("%s",
          ve.getMaVe()));
    }
    sb.append("</html>");
    return sb.toString();
  }

  private String chuyenTrangThaiSangText1(String trangThai) {
    switch (trangThai) {
      case HoaDon.TT_CHUA_TT:
        return "Chưa thanh toán";
      case HoaDon.TT_DA_TT:
        return "Đã thanh toán";
      case HoaDon.TT_HUY:
        return "Đã hủy";
      default:
        return trangThai;
    }
  }

  private String chuyenPhuongThucTTSangText(String phuongThuc) {
    switch (phuongThuc) {
      case HoaDon.PT_TIEN_MAT:
        return "Tiền mặt";
      case HoaDon.PT_CHUYEN_KHOAN:
        return "Chuyển khoản";
      case HoaDon.PT_THE:
        return "Thẻ tín dụng";
      case HoaDon.PT_VI_DIEN_TU:
        return "Ví điện tử";
      default:
        return phuongThuc;
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
      sb.append("Chuyến bay: ").append(cb != null ? cb.getDiemDi() + " → " + cb.getDiemDen() : "N/A")
          .append("\n");
      sb.append("Số ghế: ").append(ve.getSoGhe()).append("\n");
      sb.append("Loại vé: ").append(ve instanceof VeThuongGia ? "Thương gia"
          : (ve.loaiVe().equals("VePhoThong") ? "Phổ Thông" : "Tiết Kiệm")).append("\n");
      sb.append("Giá vé: ").append(String.format("%,d VND", (int) ve.getGiaVe())).append("\n");
      sb.append("Ngày đặt: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(ve.getNgayDat()))
          .append("\n");
      sb.append("Trạng thái: ").append(chuyenTrangThaiSangText(ve.getTrangThai())).append("\n\n"); // Sửa: Dùng hàm
                                                                                                   // chuyển đổi

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
    HoaDon hdCuaVe = null;
    for (HoaDon hd : hoaDonList) {
      if (hd.getDanhSachVe().stream().anyMatch(v -> v.getMaVe().equals(maVe))) {
        hdCuaVe = hd;
        break;
      }
    }

    if (hdCuaVe == null) {
      JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn cho vé này!", "Lỗi", JOptionPane.ERROR_MESSAGE);
      return;
    }

    // Hiển thị thông tin hóa đơn
    StringBuilder sb = new StringBuilder();
    sb.append("=== HÓA ĐƠN ===\n\n");
    sb.append("Mã hóa đơn: ").append(hdCuaVe.getMaHoaDon()).append("\n");
    sb.append("Ngày lập: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(hdCuaVe.getNgayLap()))
        .append("\n");
    sb.append("Khách hàng: ").append(khachHangDangNhap.getHoTen()).append("\n");
    sb.append("Tổng tiền: ").append(String.format("%,d VND", (int) hdCuaVe.getTongTien())).append("\n");
    sb.append("Giảm giá: ").append(String.format("%,d VND", (int) hdCuaVe.getKhuyenMai())).append("\n");
    sb.append("Phí dịch vụ (Thuế): ").append(String.format("%,d VND", (int) hdCuaVe.getThue())).append("\n");
    sb.append("Thành tiền: ").append(String.format("%,d VND", (int) hdCuaVe.getThanhTien())).append("\n");
    sb.append("Trạng thái: ").append(chuyenTrangThaiSangText1(hdCuaVe.getTrangThai())).append("\n"); // Sửa: Dùng hàm

    JTextArea textArea = new JTextArea(sb.toString());
    textArea.setEditable(false);
    JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Hóa Đơn", JOptionPane.INFORMATION_MESSAGE);
  }

  // === SỬA ĐỔI: Hoàn thiện hàm HuyVe() ===
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

    // 1. Kiểm tra khả năng hủy vé
    String thongBaoLoi = khachHangDangNhap.kiemTraKhaNangHuyVe(ve);

    // Nếu có thông báo lỗi (khác null), hiển thị lỗi và dừng lại
    if (thongBaoLoi != null) {
      JOptionPane.showMessageDialog(this,
          "Không thể hủy vé:\n" + thongBaoLoi,
          "Không thể hủy", JOptionPane.WARNING_MESSAGE);
      return;
    }

    // Nếu thongBaoLoi là null, tức là vé CÓ THỂ HỦY
    int confirm = JOptionPane.showConfirmDialog(this,
        "Bạn có chắc chắn muốn hủy vé " + maVe + "?\n" +
            "Lưu ý: Thao tác này sẽ hủy toàn bộ hóa đơn liên quan.",
        "Xác nhận hủy vé",
        JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
      try {
        // 2. Thực hiện hủy vé (đặt trạng thái vé là HỦY)
        boolean huyVeThanhCong = khachHangDangNhap.huyVe(ve);

        if (huyVeThanhCong) {
          // 3. Cập nhật số ghế trống của chuyến bay
          ChuyenBay cb = dsChuyenBay.timKiemTheoMa(ve.getMaChuyen());
          if (cb != null) {
            cb.huyGhe(); // Tăng soGheTrong lên 1
          }

          // 4. Tìm và hủy Hóa đơn liên quan
          HoaDon hoaDonCuaVe = null;
          for (HoaDon hd : khachHangDangNhap.getLichSuHoaDon()) {
            if (hd.getDanhSachVe().stream().anyMatch(v -> v.getMaVe().equals(maVe))) {
              hoaDonCuaVe = hd;
              break;
            }
          }

          if (hoaDonCuaVe != null) {
            hoaDonCuaVe.setTrangThai(HoaDon.TT_HUY);
          }

          // 5. (Tùy chọn) Trừ điểm tích lũy (nếu cần)

          // 6. Lưu file và cập nhật GUI
          quanLy.ghiDuLieuRaFile();

          JOptionPane.showMessageDialog(this, "Đã hủy vé và hóa đơn thành công!", "Thành công",
              JOptionPane.INFORMATION_MESSAGE);

          // Tải lại toàn bộ dữ liệu
          taiVeCuaToi();
          taiLichSu();
          capNhatThongTinCaNhan();

        } else {
          JOptionPane.showMessageDialog(this, "Hủy vé thất bại! (Lỗi logic nội bộ)", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
      } catch (Exception ex) {
        ValidatorUtils.showExceptionDialog(this, "Lỗi khi hủy vé:", ex);
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

      // Cập nhật thông tin thành viên (đảm bảo hạng được đánh giá lại theo tháng)
      khachHangDangNhap.capNhatHangTheoThang();
      lblHangKhachHang.setText(String.format("Hạng: %s (Tháng: %,.0f VND)", khachHangDangNhap.getHangKhachHangText(), khachHangDangNhap.getTongChiTieuThang()));
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
        khachHangDangNhap.setHoTen(hoTen);
        khachHangDangNhap.setDiaChi(diaChi);
        khachHangDangNhap.setGioiTinh(gioiTinh);
        khachHangDangNhap.setSoDT(soDT);
        khachHangDangNhap.setEmail(email);
        int index = -1;
        for (int i = 0; i < quanLy.getDsKhachHang().getDanhSach().size(); i++) {
          if (quanLy.getDsKhachHang().getDanhSach().get(i).getMa().equals(khachHangDangNhap.getMa())) {
            index = i;
            break;
          }
        }
        quanLy.getDsKhachHang().getDanhSach().set(index, khachHangDangNhap);
        quanLy.ghiDuLieuRaFile();
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

  // === SỬA ĐỔI: Thêm nút "Đăng ký" vào `showDangNhap` ===
  public static boolean showDangNhap(QuanLyBanVeMayBay quanLy) {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
    JTextField txtMaKH = new JTextField(15);
    JPasswordField txtMatKhau = new JPasswordField(15);

    formPanel.add(new JLabel("Mã khách hàng:"));
    formPanel.add(txtMaKH);
    formPanel.add(new JLabel("Mật khẩu:"));
    formPanel.add(txtMatKhau);

    // Panel cho các nút
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    JButton btnDangKy = new JButton("Đăng ký");
    btnDangKy.setPreferredSize(new Dimension(80, 30));

    // Thêm nút Đăng ký vào panel
    buttonPanel.add(btnDangKy);
    panel.add(formPanel, BorderLayout.CENTER);
    panel.add(buttonPanel, BorderLayout.SOUTH);

    // Thêm hành động cho nút Đăng ký
    btnDangKy.addActionListener(e -> {
      // Đóng cửa sổ đăng nhập hiện tại
      SwingUtilities.getWindowAncestor(panel).dispose();
      // Mở cửa sổ đăng ký
      showDangKy(quanLy);
    });

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

  // === THÊM MỚI: Hàm `showDangKy` ===
  /**
   * Hiển thị dialog cho phép người dùng mới đăng ký tài khoản.
   * 
   * @param quanLy Đối tượng quản lý chính của hệ thống.
   */
  public static void showDangKy(QuanLyBanVeMayBay quanLy) {
    JDialog dialog = new JDialog((Frame) null, "Đăng Ký Tài Khoản Mới", true);
    dialog.setSize(500, 600);
    dialog.setLocationRelativeTo(null);
    dialog.setLayout(new BorderLayout());

    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 0;
    gbc.gridy = 0;

    // Các trường nhập liệu
    JTextField txtHoTen = new JTextField(20);
    JTextField txtCmnd = new JTextField(20);
    JTextField txtSoDT = new JTextField(20);
    JTextField txtEmail = new JTextField(20);
    JTextField txtNgaySinh = new JTextField(20); // (vd: 25/12/2000)
    JComboBox<String> cboGioiTinh = new JComboBox<>(new String[] { "Nam", "Nữ" });
    JTextField txtDiaChi = new JTextField(20);
    JPasswordField txtMatKhau = new JPasswordField(20);
    JPasswordField txtNhapLaiMatKhau = new JPasswordField(20);

    // Thêm vào panel
    panel.add(new JLabel("Họ tên:*"), gbc);
    gbc.gridx = 1;
    panel.add(txtHoTen, gbc);
    gbc.gridx = 0;
    gbc.gridy++;
    panel.add(new JLabel("CMND/CCCD:*"), gbc);
    gbc.gridx = 1;
    panel.add(txtCmnd, gbc);
    gbc.gridx = 0;
    gbc.gridy++;
    panel.add(new JLabel("Số điện thoại:*"), gbc);
    gbc.gridx = 1;
    panel.add(txtSoDT, gbc);
    gbc.gridx = 0;
    gbc.gridy++;
    panel.add(new JLabel("Email:*"), gbc);
    gbc.gridx = 1;
    panel.add(txtEmail, gbc);
    gbc.gridx = 0;
    gbc.gridy++;
    panel.add(new JLabel("Ngày sinh (dd/MM/yyyy):*"), gbc);
    gbc.gridx = 1;
    panel.add(txtNgaySinh, gbc);
    gbc.gridx = 0;
    gbc.gridy++;
    panel.add(new JLabel("Giới tính:*"), gbc);
    gbc.gridx = 1;
    panel.add(cboGioiTinh, gbc);
    gbc.gridx = 0;
    gbc.gridy++;
    panel.add(new JLabel("Địa chỉ:*"), gbc);
    gbc.gridx = 1;
    panel.add(txtDiaChi, gbc);
    gbc.gridx = 0;
    gbc.gridy++;
    panel.add(new JLabel("Mật khẩu:*"), gbc);
    gbc.gridx = 1;
    panel.add(txtMatKhau, gbc);
    gbc.gridx = 0;
    gbc.gridy++;
    panel.add(new JLabel("Nhập lại mật khẩu:*"), gbc);
    gbc.gridx = 1;
    panel.add(txtNhapLaiMatKhau, gbc);
    gbc.gridx = 0;
    gbc.gridy++;

    // Panel nút
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    JButton btnDangKy = new JButton("Đăng Ký");
    JButton btnHuy = new JButton("Hủy");
    buttonPanel.add(btnDangKy);
    buttonPanel.add(btnHuy);

    dialog.add(panel, BorderLayout.CENTER);
    dialog.add(buttonPanel, BorderLayout.SOUTH);

    // Xử lý sự kiện Hủy
    btnHuy.addActionListener(e -> {
      dialog.dispose();
      // Mở lại dialog đăng nhập
      showDangNhap(quanLy);
    });

    // Xử lý sự kiện Đăng Ký
    btnDangKy.addActionListener(e -> {
      try {
        // Lấy dữ liệu
        String hoTen = txtHoTen.getText().trim();
        String cmnd = txtCmnd.getText().trim();
        String soDT = txtSoDT.getText().trim();
        String email = txtEmail.getText().trim();
        String ngaySinhStr = txtNgaySinh.getText().trim();
        String gioiTinh = (String) cboGioiTinh.getSelectedItem();
        String diaChi = txtDiaChi.getText().trim();
        String matKhau = new String(txtMatKhau.getPassword());
        String nhapLaiMatKhau = new String(txtNhapLaiMatKhau.getPassword());

        // --- Validation ---
        if (hoTen.isEmpty() || cmnd.isEmpty() || soDT.isEmpty() || email.isEmpty() ||
            ngaySinhStr.isEmpty() || diaChi.isEmpty() || matKhau.isEmpty()) {
          ValidatorUtils.showErrorDialog(dialog, "Vui lòng nhập đầy đủ thông tin bắt buộc (*)");
          return;
        }
        if (!matKhau.equals(nhapLaiMatKhau)) {
          ValidatorUtils.showErrorDialog(dialog, "Mật khẩu nhập lại không khớp!");
          return;
        }

        // Kiểm tra trùng lặp
        DanhSachKhachHang dsKH = quanLy.getDsKhachHang();
        if (dsKH.timKiemTheoCMND(cmnd) != null) {
          ValidatorUtils.showErrorDialog(dialog, "CMND này đã được đăng ký!");
          return;
        }

        // Parse ngày sinh
        Date ngaySinh = ValidatorUtils.parseDate(ngaySinhStr);
        if (ngaySinh == null) {
          ValidatorUtils.showErrorDialog(dialog, "Định dạng ngày sinh không hợp lệ (dd/MM/yyyy).");
          return;
        }

        // --- Tạo tài khoản ---
        // Tạo mã KH tự động
        int soKHHienTai = dsKH.demSoLuong();
        String maKH = "KH" + String.format("%04d", soKHHienTai + 1);

        KhachHang khachHangMoi = new KhachHang(
            maKH, hoTen, soDT, email, cmnd,
            ngaySinh, gioiTinh, diaChi,
            matKhau
        );

        // Thêm vào hệ thống
        if (quanLy.themKhachHang(khachHangMoi)) {
          quanLy.ghiDuLieuRaFile();
          ValidatorUtils.showSuccessDialog(dialog, "Đăng ký thành công!\n" +
              "Mã khách hàng (tên đăng nhập) của bạn là: " + maKH);
          dialog.dispose();
          showDangNhap(quanLy); // Quay lại màn hình đăng nhập
        } else {
          ValidatorUtils.showErrorDialog(dialog, "Đăng ký thất bại do lỗi hệ thống.");
        }

      } catch (Exception ex) {
        ValidatorUtils.showExceptionDialog(dialog, "Lỗi nghiêm trọng khi đăng ký:", ex);
      }
    });

    dialog.setVisible(true);
  }

  // === THÊM MỚI: Hàm `thanhToanVe` ===
  /**
   * Xử lý logic thanh toán cho vé được chọn trong "Vé Của Tôi".
   */

  public static void main(String[] args) {
    QuanLyBanVeMayBay quanLy = new QuanLyBanVeMayBay();
    quanLy.docDuLieuTuFile();
    SwingUtilities.invokeLater(() -> {
      UsersGUI.showDangNhap(quanLy);
    });
  }

  // === THAY THẾ TOÀN BỘ HÀM NÀY ===
  private String moDialogChonGhe(ChuyenBay chuyenBay, List<String> gheDaChonTruoc, String loaiVe) {
    JDialog dialog = new JDialog(this, "Chọn Ghế Ngồi - " + chuyenBay.getMaChuyen(), true);
    dialog.setSize(600, 650);
    dialog.setLocationRelativeTo(this);
    dialog.setLayout(new BorderLayout(10, 10));

    List<String> gheDaDat = chuyenBay.getDanhSachGheDaDat();
    int tongSoGhe = chuyenBay.getSoGheToiDa();

    // 6 columns (A, B, C, D, E, F), rows computed from capacity
    final int SO_COT = 6;
    int soHang = (int) Math.ceil((double) tongSoGhe / SO_COT);

    // Add legend showing ticket class row ranges (dynamic based on capacity)
    int[] allocation = ChuyenBay.tinhViTriLoaiGhe(soHang);
    int businessEnd = allocation[0];
    int economyEnd = allocation[1];
    String legendText = String.format("Hạng vé - Thương gia (1-%d), Phổ thông (%d-%d), Tiết kiệm (%d-%d)", 
        businessEnd, businessEnd + 1, economyEnd, economyEnd + 1, soHang);
    JPanel panelLegend = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelLegend.add(new JLabel(legendText));
    dialog.add(panelLegend, BorderLayout.NORTH);

    JPanel panelGhe = new JPanel(new GridLayout(soHang, SO_COT, 5, 5));
    panelGhe.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    final String[] gheDuocChon = { null };
    int soGheDaTao = 0;

    // Tạo các nút ghế
    for (int i = 1; i <= soHang; i++) {
      for (char c = 'A'; c < 'A' + SO_COT; c++) {
        if (soGheDaTao >= tongSoGhe) {
          panelGhe.add(new JPanel());
          continue;
        }

        String tenGhe = i + String.valueOf(c);  // Format: 1A, 12B, 25F (row + column)
        String seatClassOfThisRow = ChuyenBay.getViTriLoaiGhe(i, soHang);
        
        JButton btnGhe = new JButton(tenGhe);
        btnGhe.setFont(new Font("Arial", Font.BOLD, 12));
        btnGhe.setMargin(new Insets(5, 5, 5, 5));

        // If user booked a different ticket class, disable seats outside their class
        boolean seatNotAllowed = !seatClassOfThisRow.equals(loaiVe);
        boolean seatAlreadyBooked = gheDaDat.contains(tenGhe) || gheDaChonTruoc.contains(tenGhe);

        if (seatNotAllowed) {
          // Seat exists but is for a different ticket class
          btnGhe.setEnabled(false);
          btnGhe.setBackground(new Color(200, 200, 200));  // Gray
          btnGhe.setText("×");  // Mark unavailable for this class
        } else if (seatAlreadyBooked) {
          btnGhe.setEnabled(false);
          btnGhe.setBackground(Color.RED);
          btnGhe.setText("X");  // Marked booked
        } else {
          btnGhe.setBackground(new Color(60, 179, 113));  // Green
          btnGhe.setForeground(Color.WHITE);
          btnGhe.setCursor(new Cursor(Cursor.HAND_CURSOR));

          btnGhe.addActionListener(e -> {
            gheDuocChon[0] = tenGhe;
            dialog.dispose();
          });
        }
        panelGhe.add(btnGhe);
        soGheDaTao++;
      }
    }

    // Panel chú thích
    JPanel panelChuThich = new JPanel(new FlowLayout());
    JButton btnTrong = new JButton("Trống");
    btnTrong.setBackground(new Color(60, 179, 113));
    btnTrong.setForeground(Color.WHITE);
    btnTrong.setEnabled(false);
    JButton btnDaDat = new JButton("Đã đặt");
    btnDaDat.setBackground(Color.RED);
    btnDaDat.setEnabled(false);
    panelChuThich.add(btnTrong);
    panelChuThich.add(btnDaDat);

    dialog.add(new JLabel("Vui lòng chọn ghế còn trống", JLabel.CENTER), BorderLayout.NORTH);
    dialog.add(new JScrollPane(panelGhe), BorderLayout.CENTER);
    dialog.add(panelChuThich, BorderLayout.SOUTH);

    dialog.setVisible(true);

    // Trả về ghế đã được chọn (hoặc null nếu không chọn)
    return gheDuocChon[0];
  }

  /**
   * Xử lý sự kiện khi người dùng nhấn nút Đăng Xuất.
   */
  private void xuLyDangXuat() {
    int confirm = JOptionPane.showConfirmDialog(
        this,
        "Bạn có chắc chắn muốn đăng xuất?",
        "Xác nhận đăng xuất",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE);

    if (confirm == JOptionPane.YES_OPTION) {
      // Đóng cửa sổ UsersGUI hiện tại
      this.dispose();

      // Hiển thị lại màn hình đăng nhập
      // Chúng ta gọi lại hàm static và truyền 'quanLy' (đã được nạp) vào
      UsersGUI.showDangNhap(this.quanLy);
    }
  }
  private void thanhToanHoaDon() {
    int row = tableLichSu.getSelectedRow();
    if (row < 0) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn một hóa đơn!", "Thông báo", JOptionPane.WARNING_MESSAGE);
        return;
    }

    String maHoaDon = (String) modelLichSu.getValueAt(row, 0);
    HoaDon hoaDon = dsHoaDon.timKiemTheoMa(maHoaDon);

    if (hoaDon == null) {
        JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Kiểm tra trạng thái hóa đơn
    if (hoaDon.getTrangThai().equals(HoaDon.TT_DA_TT)) {
        JOptionPane.showMessageDialog(this, "Hóa đơn đã được thanh toán!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    if (hoaDon.getTrangThai().equals(HoaDon.TT_HUY)) {
        JOptionPane.showMessageDialog(this, "Hóa đơn đã bị hủy, không thể thanh toán!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Hiển thị dialog chọn phương thức thanh toán và sử dụng điểm
    JPanel panelThanhToan = new JPanel(new GridLayout(4, 2, 10, 10));
    panelThanhToan.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    String[] ptOptions = { "Tiền mặt", "Chuyển khoản", "Thẻ tín dụng", "Ví điện tử" };
    JComboBox<String> cbPhuongThuc = new JComboBox<>(ptOptions);

    // Thông tin thanh toán
    double thanhTien = hoaDon.getThanhTien();
    int diemTichLuyHienCo = khachHangDangNhap.getDiemTichLuy();
    int diemToiDaDuocDung = (int) Math.min(diemTichLuyHienCo, thanhTien);
    
    // Sử dụng mảng để lưu giá trị diemSuDung (fix lỗi lambda)
    final int[] diemSuDungArr = {0};

    JLabel lblThanhTien = new JLabel(String.format("%,d VND", (int) thanhTien));
    JLabel lblDiemHienCo = new JLabel(String.valueOf(diemTichLuyHienCo));
    JSpinner spinnerDiemSuDung = new JSpinner(new SpinnerNumberModel(0, 0, diemToiDaDuocDung, 1));

    // Cập nhật khi điểm sử dụng thay đổi
    spinnerDiemSuDung.addChangeListener(e -> {
        int diemSuDung = (int) spinnerDiemSuDung.getValue();
        diemSuDungArr[0] = diemSuDung; // Lưu giá trị vào mảng
    });

    panelThanhToan.add(new JLabel("Tổng tiền:"));
    panelThanhToan.add(lblThanhTien);
    panelThanhToan.add(new JLabel("Điểm tích lũy hiện có:"));
    panelThanhToan.add(lblDiemHienCo);
    panelThanhToan.add(new JLabel("Sử dụng điểm (tối đa " + diemToiDaDuocDung + " điểm):"));
    panelThanhToan.add(spinnerDiemSuDung);
    panelThanhToan.add(new JLabel("Phương thức thanh toán:"));
    panelThanhToan.add(cbPhuongThuc);

    int result = JOptionPane.showConfirmDialog(
            this,
            panelThanhToan,
            "Thanh Toán Hóa Đơn " + maHoaDon,
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);

    if (result != JOptionPane.OK_OPTION) {
        return; // Hủy
    }

    String phuongThucTT = (String) cbPhuongThuc.getSelectedItem();
    int diemSuDung = diemSuDungArr[0]; // Lấy giá trị cuối cùng từ mảng

    // Tính toán số tiền thực tế
    double tienGiamTuDiem = diemSuDung;
    double thanhTienThucTe = thanhTien - tienGiamTuDiem;

    // Xác nhận thanh toán
    int confirm = JOptionPane.showConfirmDialog(
            this,
            "XÁC NHẬN THANH TOÁN\n\n" +
                    "Mã hóa đơn: " + maHoaDon + "\n" +
                    "Tổng tiền: " + String.format("%,d VND", (int) thanhTien) + "\n" +
                    "Sử dụng điểm: " + diemSuDung + " điểm\n" +
                    "Giảm giá từ điểm: " + String.format("%,d VND", (int) tienGiamTuDiem) + "\n" +
                    "Thành tiền: " + String.format("%,d VND", (int) thanhTienThucTe) + "\n" +
                    "Phương thức: " + phuongThucTT + "\n\n" +
                    "Bạn có chắc chắn muốn thanh toán?",
            "Xác Nhận Thanh Toán",
            JOptionPane.YES_NO_OPTION);

    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }

    try {
        // Trừ điểm tích lũy nếu có sử dụng trước khi thanh toán
        if (diemSuDung > 0) {
            khachHangDangNhap.suDungDiemTichLuy(diemSuDung);
        }

        // Gọi service để thanh toán (sẽ cập nhật trạng thái, đăng ký hóa đơn vào khách hàng, và cộng điểm tự động)
        try {
            dsHoaDon.thanhToanHoaDon(maHoaDon);
        } catch (Exception ex) {
            throw new Exception("Lỗi khi thực hiện thanh toán qua service: " + ex.getMessage());
        }

        // Cập nhật phương thức thanh toán
        hoaDon.setPhuongThucTT(chuyenPhuongThucTextSangMa(phuongThucTT));

        // Lưu dữ liệu
        quanLy.ghiDuLieuRaFile();

        // Cập nhật giao diện
        taiLichSu();
        taiVeCuaToi();
        capNhatThongTinCaNhan();

        // Hiển thị thông báo thành công
        StringBuilder message = new StringBuilder();
        message.append("Thanh toán hóa đơn thành công!\n\n");
        message.append("Mã hóa đơn: ").append(maHoaDon).append("\n");
        message.append("Tổng tiền: ").append(String.format("%,d VND", (int) thanhTien)).append("\n");
        
        if (diemSuDung > 0) {
            message.append("Đã sử dụng: ").append(diemSuDung).append(" điểm\n");
            message.append("Giảm giá từ điểm: ").append(String.format("%,d VND", (int) tienGiamTuDiem)).append("\n");
            message.append("Điểm còn lại: ").append(khachHangDangNhap.getDiemTichLuy()).append(" điểm\n");
        }
        
        if (thanhTienThucTe > 0) {
            int diemThuongMoi = (int) (thanhTienThucTe / 10000);
            message.append("Điểm tích lũy nhận được: ").append(diemThuongMoi).append(" điểm\n");
        }
        
        message.append("Thành tiền: ").append(String.format("%,d VND", (int) thanhTienThucTe)).append("\n");
        message.append("Phương thức: ").append(phuongThucTT);

        JOptionPane.showMessageDialog(this, 
            message.toString(), 
            "Thành Công", 
            JOptionPane.INFORMATION_MESSAGE);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, 
            "Lỗi khi thanh toán hóa đơn: " + e.getMessage(), 
            "Lỗi", 
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}
// ========== PHƯƠNG THỨC HỦY HÓA ĐƠN ==========
private void huyHoaDon() {
    int row = tableLichSu.getSelectedRow();
    if (row < 0) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn một hóa đơn!", "Thông báo", JOptionPane.WARNING_MESSAGE);
        return;
    }

    String maHoaDon = (String) modelLichSu.getValueAt(row, 0);
    HoaDon hoaDon = dsHoaDon.timKiemTheoMa(maHoaDon);

    if (hoaDon == null) {
        JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Kiểm tra trạng thái hóa đơn
    if (hoaDon.getTrangThai().equals(HoaDon.TT_HUY)) {
        JOptionPane.showMessageDialog(this, "Hóa đơn đã bị hủy!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    if (hoaDon.getTrangThai().equals(HoaDon.TT_DA_TT)) {
        JOptionPane.showMessageDialog(this, "Hóa đơn đã thanh toán, không thể hủy!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Hiển thị thông tin vé sẽ bị hủy
    StringBuilder veInfo = new StringBuilder();
    veInfo.append("Các vé sau sẽ bị hủy:\n\n");
    for (VeMayBay ve : hoaDon.getDanhSachVe()) {
        ChuyenBay cb = dsChuyenBay.timKiemTheoMa(ve.getMaChuyen());
        veInfo.append("• ").append(ve.getMaVe())
               .append(" - ").append(cb != null ? cb.getDiemDi() + " → " + cb.getDiemDen() : "N/A")
               .append(" - ").append(ve.getSoGhe()).append("\n");
    }

    // Xác nhận hủy
    int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn HỦY hóa đơn " + maHoaDon + "?\n\n" +
                    veInfo.toString() + "\n" +
                    "Lưu ý: Tất cả vé trong hóa đơn sẽ chuyển sang trạng thái HỦY!",
            "Xác Nhận Hủy Hóa Đơn",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }

    try {
        // Thực hiện hủy hóa đơn
        hoaDon.setTrangThai(HoaDon.TT_HUY);

        // Cập nhật trạng thái các vé trong hóa đơn thành HỦY
        for (VeMayBay ve : hoaDon.getDanhSachVe()) {
            ve.setTrangThai(VeMayBay.TRANG_THAI_DA_HUY);
            
            // Cập nhật số ghế trống của chuyến bay
            ChuyenBay cb = dsChuyenBay.timKiemTheoMa(ve.getMaChuyen());
            if (cb != null) {
                cb.setSoGheTrong(cb.getSoGheTrong() + 1);
            }
        }

        // Lưu dữ liệu
        quanLy.ghiDuLieuRaFile();

        // Cập nhật giao diện
        taiLichSu();
        taiVeCuaToi();

        JOptionPane.showMessageDialog(this, 
            "Hủy hóa đơn thành công!\nMã hóa đơn: " + maHoaDon + "\nTất cả vé đã được hủy.", 
            "Thành Công", 
            JOptionPane.INFORMATION_MESSAGE);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, 
            "Lỗi khi hủy hóa đơn: " + e.getMessage(), 
            "Lỗi", 
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}

// ========== PHƯƠNG THỨC HỖ TRỢ CHUYỂN ĐỔI PHƯƠNG THỨC THANH TOÁN ==========
private String chuyenPhuongThucTextSangMa(String phuongThucText) {
    switch (phuongThucText) {
        case "Tiền mặt":
            return HoaDon.PT_TIEN_MAT;
        case "Chuyển khoản":
            return HoaDon.PT_CHUYEN_KHOAN;
        case "Thẻ tín dụng":
            return HoaDon.PT_THE;
        case "Ví điện tử":
            return HoaDon.PT_VI_DIEN_TU;
        default:
            return HoaDon.PT_NONE;
    }
}
private void xemChiTietHoaDon() {
    int row = tableLichSu.getSelectedRow();
    if (row < 0) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn một hóa đơn!", "Thông báo", JOptionPane.WARNING_MESSAGE);
        return;
    }

    String maHoaDon = (String) modelLichSu.getValueAt(row, 0);
    HoaDon hoaDon = dsHoaDon.timKiemTheoMa(maHoaDon);

    if (hoaDon != null) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== CHI TIẾT HÓA ĐƠN ===\n\n");
        sb.append("Mã hóa đơn: ").append(hoaDon.getMaHoaDon()).append("\n");
        sb.append("Ngày lập: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(hoaDon.getNgayLap())).append("\n");
        sb.append("Khách hàng: ").append(hoaDon.getKhachHang().getHoTen()).append("\n");
        sb.append("Số điện thoại: ").append(hoaDon.getKhachHang().getSoDT()).append("\n");
        sb.append("Email: ").append(hoaDon.getKhachHang().getEmail()).append("\n\n");
        
        sb.append("=== DANH SÁCH VÉ ===\n");
        int stt = 1;
        for (VeMayBay ve : hoaDon.getDanhSachVe()) {
            ChuyenBay cb = dsChuyenBay.timKiemTheoMa(ve.getMaChuyen());
            sb.append(stt++).append(". ").append(ve.getMaVe())
              .append(" - ").append(cb != null ? cb.getDiemDi() + " → " + cb.getDiemDen() : "N/A")
              .append(" - ").append(ve.getSoGhe())
              .append(" - ").append(String.format("%,d VND", (int) ve.getGiaVe()))
              .append(" - ").append(chuyenTrangThaiSangText(ve.getTrangThai())).append("\n");
        }
        
        sb.append("\n=== THÔNG TIN THANH TOÁN ===\n");
        sb.append("Tổng tiền: ").append(String.format("%,d VND", (int) hoaDon.getTongTien())).append("\n");

        sb.append("Thuế/VAT: ").append(String.format("%,d VND", (int) hoaDon.getThue())).append("\n");
        sb.append("Thành tiền: ").append(String.format("%,d VND", (int) hoaDon.getThanhTien())).append("\n");
        sb.append("Phương thức TT: ").append(chuyenPhuongThucTTSangText(hoaDon.getPhuongThucTT())).append("\n");
        sb.append("Trạng thái: ").append(chuyenTrangThaiSangText1(hoaDon.getTrangThai())).append("\n");

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Chi Tiết Hóa Đơn " + maHoaDon, 
            JOptionPane.INFORMATION_MESSAGE);
    }
}
}