package Main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout; // Thêm import
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField; // Thêm import
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

import Main.components.MenuManager;
import Main.components.StatCardManager;
import Main.components.TabManager;
import Main.dialogs.ChuyenBayDialogs;
import Main.dialogs.KhachHangDialogs;
import Main.dialogs.ThongKeDialogs;
import Main.dialogs.VeDialogs;
import Main.utils.ValidatorUtils;
import Sevice.DanhSachKhachHang;
import Sevice.QuanLyBanVeMayBay;
import model.ChuyenBay;
import model.HoaDon;
import model.KhachHang;
import model.NhanVien; // Thêm import
import model.VeMayBay;

public class MainGUI extends JFrame {
  private QuanLyBanVeMayBay quanLy;
  private NhanVien nhanVienHienTai; // THÊM MỚI: Lưu nhân viên đang đăng nhập
  private TabManager tabManager;
  private StatCardManager statCardManager;
  private MenuManager menuManager;
  private VeDialogs veDialogs;
  private ChuyenBayDialogs chuyenBayDialogs;
  private KhachHangDialogs khachHangDialogs;
  private ThongKeDialogs thongKeDialogs;

  // === SỬA ĐỔI CONSTRUCTOR ===
  // Constructor cũ: public MainGUI()
  // Constructor mới:
  public MainGUI(QuanLyBanVeMayBay quanLy, NhanVien nv) {
    this.quanLy = quanLy; // Nhận quanLy từ bên ngoài
    this.nhanVienHienTai = nv; // Nhận nhân viên đã đăng nhập
    // Bỏ dòng: this.quanLy = new QuanLyBanVeMayBay();
    // Bỏ dòng: quanLy.docDuLieuTuFile();

    initializeManagers();
    initComponents();

    // Cập nhật tiêu đề chào mừng
    updateWindowTitle();
  }


  private void initializeManagers() {
    this.tabManager = new TabManager(this, quanLy);
    this.statCardManager = new StatCardManager(quanLy);
    this.menuManager = new MenuManager(this, quanLy);
    this.veDialogs = new VeDialogs(this, quanLy, tabManager.getTableVe());
    this.chuyenBayDialogs = new ChuyenBayDialogs(this, quanLy, tabManager.getTableChuyenBay());
    this.khachHangDialogs = new KhachHangDialogs(quanLy, this);
    this.thongKeDialogs = new ThongKeDialogs(quanLy, this);
  }

  private void initComponents() {
    // Sửa tiêu đề, sẽ được cập nhật lại trong updateWindowTitle
    setTitle("HỆ THỐNG QUẢN LÝ VÉ MÁY BAY");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1200, 800);
    setLocationRelativeTo(null);

    // Sử dụng TabManager
    add(tabManager.getTabbedPane());

    // Sử dụng MenuManager
    setJMenuBar(menuManager.getMenuBar());

    // Hiển thị GUI
    setVisible(true);

    // Cập nhật dữ liệu lần đầu
    capNhatDuLieuGUI();
  }

  // ========== PHƯƠNG THỨC CẬP NHẬT DỮ LIỆU GUI ==========

  /**
   * Cập nhật toàn bộ dữ liệu trên giao diện
   */
  public void capNhatDuLieuGUI() {
    try {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

      capNhatTatCaTables();

      // Cập nhật thống kê trang chủ
      capNhatThongKeTrangChu();

      // Cập nhật trạng thái menu và các component khác
      capNhatTrangThaiGUI();

      // Hiển thị thông báo thành công (tùy chọn)
      showStatusMessage("Dữ liệu đã được cập nhật thành công!");

    } catch (Exception ex) {
      ValidatorUtils.showExceptionDialog(this, "Lỗi khi cập nhật dữ liệu GUI", ex);
    } finally {
      setCursor(Cursor.getDefaultCursor());
    }
  }

  /**
   * Cập nhật tất cả các bảng dữ liệu
   */
  public void capNhatTatCaTables() {
    tabManager.capNhatTableVe();
    tabManager.capNhatTableChuyenBay();
    tabManager.capNhatTableKhachHang();
    tabManager.capNhatTableHoaDon();
  }

  /**
   * Cập nhật thống kê trang chủ
   */
  public void capNhatThongKeTrangChu() {
    statCardManager.capNhatThongKeTrangChu();
  }

  /**
   * Cập nhật thống kê theo loại
   */
  public void capNhatThongKeTheoLoai(String loai) {
    statCardManager.capNhatThongKeTheoLoai(loai);
  }

  /**
   * Cập nhật trạng thái các component trên GUI
   */
  private void capNhatTrangThaiGUI() {
    // Cập nhật title với thông tin mới nhất
    updateWindowTitle();

    // Cập nhật trạng thái menu (nếu có phân quyền)
    // Giả sử chỉ Admin mới có mọi quyền
    boolean isAdmin = (nhanVienHienTai != null && nhanVienHienTai.getChucVu().equals(NhanVien.ROLE_ADMIN));
    menuManager.updateMenuState(isAdmin);

    // Cập nhật trạng thái các nút trên toolbar
    capNhatTrangThaiToolbar();

    // Cập nhật thông tin trạng thái hệ thống
    capNhatTrangThaiHeThong();
  }

  /**
   * Cập nhật tiêu đề cửa sổ với thông tin mới
   */
  private void updateWindowTitle() {
    // Sửa đổi: Chào mừng nhân viên
    String tenNV = (nhanVienHienTai != null) ? nhanVienHienTai.getHoTen() : "Admin";
    String chucVu = (nhanVienHienTai != null) ? nhanVienHienTai.getChucVu() : "Quản lý";

    String newTitle = String.format(
            "HỆ THỐNG QUẢN LÝ VÉ MÁY BAY - Xin chào: %s (%s)",
            tenNV, chucVu);
    setTitle(newTitle);
  }

  /**
   * Cập nhật trạng thái các nút trên toolbar
   */
  private void capNhatTrangThaiToolbar() {
    // (Giữ nguyên code của bạn)
    int soVe = quanLy.getDsVe().demSoLuong();
    int soChuyenBay = quanLy.getDsChuyenBay().demSoLuong();
    int soKhachHang = quanLy.getDsKhachHang().demSoLuong();

    if (soVe < 10) {
      tabManager.showTabNotification(1, "Số lượng vé ít, cần thêm vé mới!");
    }

    if (soChuyenBay < 5) {
      tabManager.showTabNotification(2, "Số lượng chuyến bay ít, cần thêm chuyến bay mới!");
    }
  }

  /**
   * Cập nhật thông tin trạng thái hệ thống
   */
  private void capNhatTrangThaiHeThong() {
    // (Giữ nguyên code của bạn)
  }

  /**
   * Hiển thị thông báo trạng thái
   */
  private void showStatusMessage(String message) {
    // (Giữ nguyên code của bạn)
    System.out.println("STATUS: " + message);
    if (message.contains("thành công") || message.contains("success")) {
      ValidatorUtils.showAutoCloseDialog(this, message, 2000);
    }
  }

  /**
   * Cập nhật dữ liệu sau khi thêm/xóa/sửa vé
   */
  public void capNhatSauKhiThayDoiVe() {
    capNhatTableVe();
    capNhatThongKeTrangChu();
    tabManager.capNhatTableChuyenBay();
  }

  /**
   * Cập nhật dữ liệu sau khi thêm/xóa/sửa chuyến bay
   */
  public void capNhatSauKhiThayDoiChuyenBay() {
    capNhatTableChuyenBay();
    capNhatThongKeTrangChu();
    tabManager.capNhatTableVe();
  }

  /**
   * Cập nhật dữ liệu sau khi thêm/xóa/sửa khách hàng
   */
  public void capNhatSauKhiThayDoiKhachHang() {
    capNhatTableKhachHang();
    capNhatThongKeTrangChu();
  }

  // ========== PHƯƠNG THỨC CẬP NHẬT TABLE RIÊNG LẺ ==========

  public void capNhatTableVe() {
    tabManager.capNhatTableVe();
  }

  public void capNhatTableChuyenBay() {
    tabManager.capNhatTableChuyenBay();
  }

  public void capNhatTableKhachHang() {
    tabManager.capNhatTableKhachHang();
  }

  public void capNhatTableHoaDon() {
    tabManager.capNhatTableHoaDon();
  }

  // ========== PHƯƠNG THỨC XỬ LÝ SỰ KIỆN TỪ CÁC MANAGER ==========

  /**
   * Được gọi khi tab thay đổi
   */
  public void onTabChanged(String tabName, int tabIndex) {
    // (Giữ nguyên code của bạn)
    switch (tabIndex) {
      case 0: // Trang chủ
        capNhatThongKeTrangChu();
        break;
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
        capNhatThongKeTrangChu();
        break;
    }
  }

  /**
   * Xử lý chức năng nhanh từ trang chủ
   */
  public void xuLyChucNangNhanh(String chucNang) {
    // (Giữ nguyên code của bạn)
    switch (chucNang) {
      case "Đặt vé mới":
        tabManager.chuyenTab(1); // Tab quản lý vé
        veDialogs.moDialogDatVe();
        break;
      case "Thêm chuyến bay":
        tabManager.chuyenTab(2); // Tab chuyến bay
        chuyenBayDialogs.moDialogThemChuyenBay();
        break;
      case "Thêm khách hàng":
        tabManager.chuyenTab(3); // Tab khách hàng
        khachHangDialogs.moDialogThemKhachHang();
        break;
      case "Thống kê nâng cao":
        tabManager.chuyenTab(4); // Tab thống kê
        thongKeDialogs.hienThiThongKeNangCao();
        break;
    }
  }

  // ========== PHƯƠNG THỨC TRUY CẬP CHO CÁC MANAGER ==========

  public JTable getTableVe() {
    return tabManager.getTableVe();
  }

  public JTable getTableChuyenBay() {
    return tabManager.getTableChuyenBay();
  }

  public JTable getTableKhachHang() {
    return tabManager.getTableKhachHang();
  }

  public QuanLyBanVeMayBay getQuanLy() {
    return quanLy;
  }

  // ========== PHƯƠNG THỨC XỬ LÝ SỰ KIỆN CÔNG CỤ ==========

  public void xuLyQuanLyVe(String action) {
    switch (action) {
      case "Thêm vé":
        veDialogs.moDialogDatVe();
        break;
      case "Sửa vé":
        veDialogs.moDialogSuaVe();
        break;
      case "Xóa vé":
        veDialogs.xoaVe();
        break;
      // === THÊM MỚI ===
      case "Liên hệ KH":
        veDialogs.lienHeKhachHang();
        break;
      // === KẾT THÚC THÊM MỚI ===
      case "Tìm kiếm":
        veDialogs.moDialogTimKiemVe();
        break;
      case "Làm mới":
        capNhatTableVe();
        break;
    }
  }

  public void xuLyQuanLyChuyenBay(String action) {
    switch (action) {
      case "Thêm chuyến":
        chuyenBayDialogs.moDialogThemChuyenBay();
        break;
      case "Sửa chuyến":
        chuyenBayDialogs.moDialogSuaChuyenBay();
        break;
      case "Xóa chuyến":
        // Gọi hàm đã có sẵn trong ChuyenBayDialogs
        chuyenBayDialogs.xoaChuyenBay();
        break;
      case "Làm mới":
        capNhatTableChuyenBay();
        break;
    }
  }

  public void xuLyQuanLyKhachHang(String action) {
    // (Giữ nguyên code của bạn)
    switch (action) {
      case "Thêm KH":
        khachHangDialogs.moDialogThemKhachHang();
        break;
      case "Sửa KH":
        khachHangDialogs.suaKhachHang();
        break;
      case "Xóa KH":
        khachHangDialogs.xoaKhachHang();
        break;
      case "Tìm kiếm":
        khachHangDialogs.moDialogTimKiemLoc();
        break;
      case "Xem chi tiết":
        khachHangDialogs.xemChiTietKhachHang();
        break;
      case "Làm mới":
        capNhatTableKhachHang();
        break;
    }
  }

  public void xuLyThongKe(String action) {
    // (Giữ nguyên code của bạn)
    switch (action) {
      case "Thống kê tổng quan":
        thongKeDialogs.hienThiThongKe("Thống kê tổng quan", tabManager.getTextAreaThongKe());
        break;
      case "Doanh thu":
        thongKeDialogs.hienThiThongKe("Doanh thu", tabManager.getTextAreaThongKe());
        break;
      case "Vé theo loại":
        thongKeDialogs.hienThiThongKe("Vé theo loại", tabManager.getTextAreaThongKe());
        break;
      case "Khách hàng":
        thongKeDialogs.hienThiThongKe("Khách hàng", tabManager.getTextAreaThongKe());
        break;
      case "Chuyến bay":
        thongKeDialogs.hienThiThongKe("Chuyến bay", tabManager.getTextAreaThongKe());
        break;
      case "Thống kê nâng cao":
        thongKeDialogs.hienThiThongKeNangCao();
        break;
      case "Làm mới":
        thongKeDialogs.hienThiThongKe("Làm mới", tabManager.getTextAreaThongKe());
        break;
    }
  }

  // ========== PHƯƠNG THỨC LƯU VÀ THOÁT ==========

  /**
   * Lưu dữ liệu trước khi thoát
   */
  public void luuDuLieu() {
    // (Giữ nguyên code của bạn)
    try {
      quanLy.ghiDuLieuRaFile();
      ValidatorUtils.showSuccessDialog(this, "✅ Đã lưu dữ liệu thành công!");
    } catch (Exception e) {
      ValidatorUtils.showErrorDialog(this, "❌ Lỗi khi lưu dữ liệu: " + e.getMessage());
    }
  }

  /**
   * Thoát chương trình
   */
  public void thoatChuongTrinh() {
    // (Giữ nguyên code của bạn)
    int confirm = ValidatorUtils.showConfirmDialogWithCancel(this,
            "Bạn có muốn lưu dữ liệu trước khi thoát?");

    if (confirm == JOptionPane.YES_OPTION) {
      luuDuLieu();
      System.exit(0);
    } else if (confirm == JOptionPane.NO_OPTION) {
      System.exit(0);
    }
  }

  /**
   * Xử lý sự kiện từ menu hệ thống
   */
  public void xuLyHeThong(String action) {
    // (Giữ nguyên code của bạn)
    switch (action) {
      case "Lưu dữ liệu":
        luuDuLieu();
        break;
      case "Tải lại dữ liệu":
        quanLy.docDuLieuTuFile();
        capNhatDuLieuGUI();
        ValidatorUtils.showSuccessDialog(this, "✅ Đã tải lại dữ liệu thành công!");
        break;
      case "Thoát":
        thoatChuongTrinh();
        break;
    }
  }

  /**
   * Xử lý sự kiện từ menu trợ giúp
   */
  public void xuLyTroGiup(String action) {
    // (GiVũ nguyên code của bạn)
    switch (action) {
      case "Giới thiệu":
        hienThiGioiThieu();
        break;
      case "Hướng dẫn sử dụng":
        hienThiHuongDan();
        break;
      case "Kiểm tra cập nhật":
        kiemTraCapNhat();
        break;
    }
  }

  private void hienThiGioiThieu() {
    // (Giữ nguyên code của bạn)
    String message = "HỆ THỐNG QUẢN LÝ VÉ MÁY BAY\n\n" +
            "Phiên bản: " + QuanLyBanVeMayBay.getPhienBan() + "\n" +
            "Phát triển bởi: Nhóm phát triển phần mềm\n\n" +
            "Chức năng chính:\n" +
            "• Quản lý vé máy bay\n" +
            "• Quản lý chuyến bay\n" +
            "• Quản lý khách hàng\n" +
            "• Thống kê và báo cáo\n\n" +
            "© 2024 - All rights reserved";

    JOptionPane.showMessageDialog(this, message, "Giới thiệu", JOptionPane.INFORMATION_MESSAGE);
  }

  private void hienThiHuongDan() {
    // (Giữ nguyên code của bạn)
    String huongDan = "HƯỚNG DẪN SỬ DỤNG HỆ THỐNG\n\n" +
            "1. QUẢN LÝ VÉ:\n" +
            "   - Thêm vé: Chọn tab Quản lý vé → Nhấn 'Thêm vé'\n" +
            "   - Tìm kiếm: Sử dụng chức năng tìm kiếm đa tiêu chí\n" +
            "   - Làm mới: Cập nhật lại dữ liệu bảng\n\n" +
            "2. QUẢN LÝ CHUYẾN BAY:\n" +
            "   - Thêm chuyến: Tạo chuyến bay mới với đầy đủ thông tin\n" +
            "   - Sửa chuyến: Chọn chuyến bay và nhấn 'Sửa'\n" +
            "   - Xóa chuyến: Chỉ xóa được chuyến bay có trạng thái HỦY\n\n" +
            "3. QUẢN LÝ KHÁCH HÀNG:\n" +
            "   - Thêm KH: Đăng ký khách hàng mới\n" +
            "   - Tìm kiếm & Lọc: Tìm kiếm theo nhiều tiêu chí\n" +
            "   - Xem chi tiết: Xem thông tin đầy đủ của khách hàng\n\n" +
            "4. THỐNG KÊ:\n" +
            "   - Xem các báo cáo thống kê chi tiết\n" +
            "   - Thống kê nâng cao với biểu đồ và bảng\n\n" +
            "LƯU Ý: Luôn lưu dữ liệu trước khi thoát chương trình!";

    JTextArea textArea = new JTextArea(huongDan);
    textArea.setEditable(false);
    textArea.setFont(new Font("Arial", Font.PLAIN, 12));
    textArea.setBackground(new Color(240, 240, 240));

    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setPreferredSize(new Dimension(500, 400));

    JOptionPane.showMessageDialog(this, scrollPane, "Hướng dẫn sử dụng", JOptionPane.INFORMATION_MESSAGE);
  }

  private void kiemTraCapNhat() {
    // (Giữ nguyên code của bạn)
    JOptionPane.showMessageDialog(this,
            "✅ Bạn đang sử dụng phiên bản mới nhất!\n\n" +
                    "Phiên bản hiện tại: " + QuanLyBanVeMayBay.getPhienBan() + "\n" +
                    "Không có bản cập nhật mới.",
            "Kiểm tra cập nhật",
            JOptionPane.INFORMATION_MESSAGE);
  }

  // ========== PHƯƠNG THỨC XỬ LÝ LỖI VÀ THÔNG BÁO ==========

  /**
   * Hiển thị thông báo lỗi
   */
  public void hienThiLoi(String message) {
    ValidatorUtils.showErrorDialog(this, message);
  }

  /**
   * Hiển thị thông báo thành công
   */
  public void hienThiThanhCong(String message) {
    ValidatorUtils.showSuccessDialog(this, message);
  }

  /**
   * Hiển thị thông báo cảnh báo
   */
  public void hienThiCanhBao(String message) {
    ValidatorUtils.showWarningDialog(this, message);
  }

  // ========== PHƯƠNG THỨC QUẢN LÝ TRẠNG THÁI ỨNG DỤNG ==========

  /**
   * Minimize ứng dụng
   */
  public void minimize() {
    setState(JFrame.ICONIFIED);
  }

  /**
   * Maximize ứng dụng
   */
  public void maximize() {
    if (getExtendedState() == JFrame.MAXIMIZED_BOTH) {
      setExtendedState(JFrame.NORMAL);
    } else {
      setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
  }

  /**
   * Kiểm tra xem ứng dụng có đang maximize không
   */
  public boolean isMaximized() {
    return getExtendedState() == JFrame.MAXIMIZED_BOTH;
  }

  // ========== PHƯƠNG THỨC ĐƯỢC GỌI TỪ CÁC DIALOG ==========

  /**
   * Được gọi khi thêm thành công một chuyến bay mới
   */
  public void onThemChuyenBayThanhCong(ChuyenBay chuyenBay) {
    capNhatSauKhiThayDoiChuyenBay();
    hienThiThanhCong("Thêm chuyến bay thành công: " + chuyenBay.getMaChuyen());
  }

  /**
   * Được gọi khi sửa thành công chuyến bay
   */
  public void onSuaChuyenBayThanhCong(ChuyenBay chuyenBay) {
    capNhatSauKhiThayDoiChuyenBay();
    hienThiThanhCong("Cập nhật chuyến bay thành công: " + chuyenBay.getMaChuyen());
  }

  /**
   * Được gọi khi xóa thành công chuyến bay
   */
  public void onXoaChuyenBayThanhCong(String maChuyen) {
    capNhatSauKhiThayDoiChuyenBay();
    hienThiThanhCong("Xóa chuyến bay thành công: " + maChuyen);
  }

  /**
   * Được gọi khi thêm thành công khách hàng mới
   */
  public void onThemKhachHangThanhCong(KhachHang khachHang) {
    capNhatSauKhiThayDoiKhachHang();
    hienThiThanhCong("Thêm khách hàng thành công: " + khachHang.getHoTen());
  }

  /**
   * Được gọi khi sửa thành công khách hàng
   */
  public void onSuaKhachHangThanhCong(KhachHang khachHang) {
    capNhatSauKhiThayDoiKhachHang();
    hienThiThanhCong("Cập nhật khách hàng thành công: " + khachHang.getHoTen());
  }

  /**
   * Được gọi khi xóa thành công khách hàng
   */
  public void onXoaKhachHangThanhCong(String maKH) {
    capNhatSauKhiThayDoiKhachHang();
    hienThiThanhCong("Xóa khách hàng thành công: " + maKH);
  }

  // ========== PHƯƠNG THỨC KIỂM TRA VÀ VALIDATE ==========

  /**
   * Kiểm tra xem có dữ liệu nào được chọn trong bảng không
   */
  public boolean kiemTraDuocChon(JTable table) {
    return table.getSelectedRow() >= 0;
  }

  /**
   * Hiển thị thông báo yêu cầu chọn dòng
   */
  public void hienThiYeuCauChon(String tenDoiTuong) {
    hienThiCanhBao("Vui lòng chọn một " + tenDoiTuong + " để thực hiện thao tác này!");
  }

  /**
   * Lấy dòng được chọn từ bảng
   */
  public int getSelectedRow(JTable table) {
    return table.getSelectedRow();
  }

  /**
   * Lấy giá trị từ bảng tại dòng và cột được chỉ định
   */
  public Object getValueAt(JTable table, int row, int column) {
    return table.getValueAt(row, column);
  }

  // === SỬA ĐỔI: Thay đổi hàm main, Thêm hàm showDangNhapAdmin ===

  public static void main(String[] args) {
    // Set look and feel
    try {
      UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    } catch (Exception e) {
      e.printStackTrace();
    }

    // Hiển thị splash screen (tùy chọn)
    showSplashScreen();

    SwingUtilities.invokeLater(() -> {
      // SỬA: Không gọi new MainGUI() trực tiếp
      // new MainGUI().setVisible(true);

      // GỌI HÀM ĐĂNG NHẬP ADMIN MỚI
      showDangNhapAdmin();
    });
  }

  /**
   * THÊM MỚI: Hiển thị dialog đăng nhập cho Admin
   */
  public static void showDangNhapAdmin() {
    // 1. Khởi tạo QuanLy và đọc dữ liệu (chỉ 1 lần)
    QuanLyBanVeMayBay quanLy = new QuanLyBanVeMayBay();
    quanLy.docDuLieuTuFile();

    // 2. Tạo dialog
    JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    JTextField txtTenDangNhap = new JTextField(15);
    JPasswordField txtMatKhau = new JPasswordField(15);

    panel.add(new JLabel("Tên đăng nhập (Admin):"));
    panel.add(txtTenDangNhap);
    panel.add(new JLabel("Mật khẩu:"));
    panel.add(txtMatKhau);

    // Tài khoản admin mẫu: admin / 123 (đã tạo trong QuanLyBanVeMayBay)
    txtTenDangNhap.setText("admin");
    txtMatKhau.setText("123");

    int result = JOptionPane.showConfirmDialog(null, panel, "Đăng Nhập Quản Trị Viên",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
      String tenDangNhap = txtTenDangNhap.getText().trim();
      String matKhau = new String(txtMatKhau.getPassword());

      // 3. Gọi hàm đăng nhập
      NhanVien nv = quanLy.dangNhapAdmin(tenDangNhap, matKhau);

      if (nv != null && nv.getChucVu().equals(NhanVien.ROLE_ADMIN)) {
        // 4. Nếu thành công, mở MainGUI và truyền dữ liệu vào
        new MainGUI(quanLy, nv).setVisible(true);
      } else {
        JOptionPane.showMessageDialog(null,
                "Tên đăng nhập, mật khẩu không đúng hoặc bạn không có quyền Admin.",
                "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
        // Gọi lại hàm đăng nhập
        showDangNhapAdmin();
      }
    } else {
      // Người dùng nhấn Cancel hoặc đóng
      System.exit(0);
    }
  }


  private static void showSplashScreen() {
    // Có thể thêm splash screen ở đây nếu cần
    System.out.println("🚀 Khởi động hệ thống quản lý vé máy bay...");
  }

  public void xuLyQuanLyHoaDon(String action) {
    // (Giữ nguyên code của bạn)
    switch (action) {
      case "Tìm kiếm":
        moDialogTimKiemHoaDon();
        break;
      case "Làm mới":
        capNhatTableHoaDon();
        break;
      case "Xử lý trạng thái":
        xuLyTrangThaiHoaDon();
        break;
    }
  }

  private void xuLyTrangThaiHoaDon() {
    // (Giữ nguyên code của bạn)
    int selectedRow = tabManager.getTableHoaDon().getSelectedRow();
    if (selectedRow == -1) {
      JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn cần xử lý!", "Thông báo", JOptionPane.WARNING_MESSAGE);
      return;
    }

    String maHoaDon = (String) tabManager.getTableHoaDon().getValueAt(selectedRow, 0);
    HoaDon hoaDon = quanLy.getDsHoaDon().timKiemTheoMa(maHoaDon);

    if (hoaDon == null) {
      JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
      return;
    }

    String currentStatus = hoaDon.getTrangThai();

    if (currentStatus.equals(HoaDon.TT_DA_TT)) {
      JOptionPane.showMessageDialog(this, "Hóa đơn đã thanh toán không thể thay đổi trạng thái!", "Thông báo",
              JOptionPane.WARNING_MESSAGE);
      return;
    }

    if (currentStatus.equals(HoaDon.TT_HUY)) {
      JOptionPane.showMessageDialog(this, "Hóa đơn đã hủy không thể thay đổi trạng thái!", "Thông báo",
              JOptionPane.WARNING_MESSAGE);
      return;
    }

    String[] options = { "Đã thanh toán", "Hủy" };
    String newStatus = (String) JOptionPane.showInputDialog(
            this,
            "Chọn trạng thái mới cho hóa đơn " + maHoaDon + ":\nTrạng thái hiện tại: " + currentStatus,
            "Xử lý trạng thái hóa đơn",
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);

    if (newStatus == null) {
      return; // Hủy
    }

    int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn thay đổi trạng thái hóa đơn thành: " + newStatus + "?",
            "Xác nhận",
            JOptionPane.YES_NO_OPTION);

    if (confirm != JOptionPane.YES_OPTION) {
      return;
    }

    String newStatusCode = newStatus.equals("Đã thanh toán") ? HoaDon.TT_DA_TT : HoaDon.TT_HUY;

    hoaDon.setTrangThai(newStatusCode);

    if (newStatusCode.equals(HoaDon.TT_HUY)) {
      for (VeMayBay ve : hoaDon.getDanhSachVe()) {
        ve.setTrangThai(VeMayBay.TRANG_THAI_DA_HUY);
      }
    }

    quanLy.ghiDuLieuRaFile();

    capNhatTableHoaDon();
    tabManager.capNhatTableVe();

    JOptionPane.showMessageDialog(this, "Đã cập nhật trạng thái hóa đơn thành công!", "Thành công",
            JOptionPane.INFORMATION_MESSAGE);
  }

  private void moDialogTimKiemHoaDon() {
    // (Giữ nguyên code của bạn)
    JDialog dialog = new JDialog(this, "Tìm kiếm hóa đơn", true);
    dialog.setSize(500, 400);
    dialog.setLocationRelativeTo(this);
    dialog.setLayout(new BorderLayout(10, 10));

    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.anchor = GridBagConstraints.WEST;

    gbc.gridx = 0;
    gbc.gridy = 0;
    panel.add(new JLabel("Mã hóa đơn:"), gbc);
    JTextField txtMaHoaDon = new JTextField(20);
    gbc.gridx = 1;
    panel.add(txtMaHoaDon, gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    panel.add(new JLabel("Mã khách hàng:"), gbc);
    JTextField txtMaKhachHang = new JTextField(20);
    gbc.gridx = 1;
    panel.add(txtMaKhachHang, gbc);

    gbc.gridx = 0;
    gbc.gridy = 2;
    panel.add(new JLabel("Trạng thái:"), gbc);
    JComboBox<String> cbTrangThai = new JComboBox<>(
            new String[] { "Tất cả", "Chưa thanh toán", "Đã thanh toán", "Hủy" });
    gbc.gridx = 1;
    panel.add(cbTrangThai, gbc);

    JButton btnTimKiem = new JButton("Tìm kiếm");
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.CENTER;
    panel.add(btnTimKiem, gbc);

    dialog.add(panel, BorderLayout.CENTER);

    btnTimKiem.addActionListener(e -> {
      String maHoaDon = txtMaHoaDon.getText().trim();
      String maKhachHang = txtMaKhachHang.getText().trim();
      String trangThai = (String) cbTrangThai.getSelectedItem();

      java.util.List<HoaDon> ketQua = new java.util.ArrayList<>();
      for (HoaDon hd : quanLy.getDsHoaDon().getDanhSach()) {
        boolean match = true;

        if (!maHoaDon.isEmpty() && !hd.getMaHoaDon().toLowerCase().contains(maHoaDon.toLowerCase())) {
          match = false;
        }

        if (!maKhachHang.isEmpty() && !hd.getKhachHang().getMa().toLowerCase().contains(maKhachHang.toLowerCase())) {
          match = false;
        }

        if (!"Tất cả".equals(trangThai)) {
          String ttCode = switch (trangThai) {
            case "Đã thanh toán" -> HoaDon.TT_DA_TT;
            case "Hủy" -> HoaDon.TT_HUY;
            default -> HoaDon.TT_CHUA_TT;
          };
          if (!hd.getTrangThai().equals(ttCode)) {
            match = false;
          }
        }

        if (match) {
          ketQua.add(hd);
        }
      }

      if (ketQua.isEmpty()) {
        JOptionPane.showMessageDialog(dialog, "Không tìm thấy hóa đơn nào phù hợp!", "Kết quả tìm kiếm",
                JOptionPane.INFORMATION_MESSAGE);
      } else {
        capNhatTableHoaDonVoiKetQua(ketQua);
        JOptionPane.showMessageDialog(dialog, "Tìm thấy " + ketQua.size() + " hóa đơn!", "Kết quả tìm kiếm",
                JOptionPane.INFORMATION_MESSAGE);
      }
    });

    dialog.setVisible(true);
  }

  private void capNhatTableHoaDonVoiKetQua(java.util.List<HoaDon> danhSachHoaDon) {
    // (Giữ nguyên code của bạn)
    DefaultTableModel model = (DefaultTableModel) tabManager.getTableHoaDon().getModel();
    model.setRowCount(0);

    DanhSachKhachHang dsKH = quanLy.getDsKhachHang();

    for (HoaDon hd : danhSachHoaDon) {
      KhachHang kh = dsKH.timKiemTheoMa(hd.getKhachHang().getMa());
      Object[] row = {
              hd.getMaHoaDon(),
              hd.getKhachHang().getMa(),
              kh != null ? kh.getHoTen() : "N/A",
              hd.getNgayLap() != null ? new SimpleDateFormat("dd/MM/yyyy HH:mm").format(hd.getNgayLap()) : "N/A",
              String.format("%,.0f VND", hd.getThanhTien()),
              hd.getTrangThai(),
              hd.getPhuongThucTT()
      };
      model.addRow(row);
    }
  }
}