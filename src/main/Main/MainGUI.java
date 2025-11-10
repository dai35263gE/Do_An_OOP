package Main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
import model.VeMayBay;

public class MainGUI extends JFrame {
    private QuanLyBanVeMayBay quanLy;
    private TabManager tabManager;
    private StatCardManager statCardManager;
    private MenuManager menuManager;
    private VeDialogs veDialogs;
    private ChuyenBayDialogs chuyenBayDialogs;
    private KhachHangDialogs khachHangDialogs;
    private ThongKeDialogs thongKeDialogs;

    public MainGUI() {
        this.quanLy = new QuanLyBanVeMayBay();
        quanLy.docDuLieuTuFile();
        initializeManagers();
        initComponents();
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
        setTitle("HỆ THỐNG QUẢN LÝ VÉ MÁY BAY - Phiên bản " + QuanLyBanVeMayBay.getPhienBan());
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
        menuManager.updateMenuState(true); // true = admin, có thể thay đổi theo user

        // Cập nhật trạng thái các nút trên toolbar
        capNhatTrangThaiToolbar();

        // Cập nhật thông tin trạng thái hệ thống
        capNhatTrangThaiHeThong();
    }

    /**
     * Cập nhật tiêu đề cửa sổ với thông tin mới
     */
    private void updateWindowTitle() {
        Map<String, Object> thongKe = quanLy.thongKeTongQuan();
        int tongVe = (int) thongKe.get("tongVe");
        int tongChuyenBay = (int) thongKe.get("tongChuyenBay");

        String newTitle = String.format(
                "HỆ THỐNG QUẢN LÝ VÉ MÁY BAY - Phiên bản %s - Vé: %d - Chuyến bay: %d",
                QuanLyBanVeMayBay.getPhienBan(), tongVe, tongChuyenBay);
        setTitle(newTitle);
    }

    /**
     * Cập nhật trạng thái các nút trên toolbar
     */
    private void capNhatTrangThaiToolbar() {
        // Có thể thêm logic để enable/disable các nút dựa trên trạng thái hệ thống
        // Ví dụ: disable nút xóa nếu không có dòng nào được chọn

        // Lấy số lượng dữ liệu hiện tại
        int soVe = quanLy.getDsVe().demSoLuong();
        int soChuyenBay = quanLy.getDsChuyenBay().demSoLuong();
        int soKhachHang = quanLy.getDsKhachHang().demSoLuong();

        // Có thể cập nhật tooltip hoặc trạng thái nút dựa trên số lượng
        // Ví dụ: hiển thị cảnh báo nếu số vé quá ít
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
        // Có thể thêm thanh trạng thái (status bar) ở đây
        Map<String, Object> thongKe = quanLy.thongKeTongQuan();

        String trangThai = String.format(
                "Hệ thống: %d vé | %d chuyến bay | %d khách hàng | Doanh thu: %,.0f VND",
                thongKe.get("tongVe"),
                thongKe.get("tongChuyenBay"),
                thongKe.get("tongKhachHang"),
                thongKe.get("tongDoanhThu"));

        // Lưu trạng thái để có thể hiển thị ở đâu đó
        // statusBar.setText(trangThai);
    }

    /**
     * Hiển thị thông báo trạng thái
     */
    private void showStatusMessage(String message) {
        // Có thể hiển thị trong status bar hoặc dialog auto-close
        System.out.println("STATUS: " + message);

        // Hiển thị dialog auto-close cho các thao tác quan trọng
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

        // Cập nhật thông tin liên quan đến chuyến bay (số ghế trống)
        tabManager.capNhatTableChuyenBay();
    }

    /**
     * Cập nhật dữ liệu sau khi thêm/xóa/sửa chuyến bay
     */
    public void capNhatSauKhiThayDoiChuyenBay() {
        capNhatTableChuyenBay();
        capNhatThongKeTrangChu();

        // Có thể cần cập nhật lại danh sách vé nếu chuyến bay bị thay đổi
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
        // Cập nhật dữ liệu cho tab được chọn
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
                // Cập nhật text area thống kê nếu cần
                break;
        }
    }

    /**
     * Xử lý chức năng nhanh từ trang chủ
     */
    public void xuLyChucNangNhanh(String chucNang) {
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
            case "Làm mới":
                capNhatTableChuyenBay();
                break;
        }
    }

    public void xuLyQuanLyKhachHang(String action) {
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
        int confirm = ValidatorUtils.showConfirmDialogWithCancel(this,
                "Bạn có muốn lưu dữ liệu trước khi thoát?");

        if (confirm == JOptionPane.YES_OPTION) {
            luuDuLieu();
            System.exit(0);
        } else if (confirm == JOptionPane.NO_OPTION) {
            System.exit(0);
        }
        // Nếu CANCEL thì không làm gì
    }

    /**
     * Xử lý sự kiện từ menu hệ thống
     */
    public void xuLyHeThong(String action) {
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
        // Giả lập kiểm tra cập nhật
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
            new MainGUI().setVisible(true);
        });
    }

    private static void showSplashScreen() {
        // Có thể thêm splash screen ở đây nếu cần
        System.out.println("🚀 Khởi động hệ thống quản lý vé máy bay...");
    }

    public void xuLyQuanLyHoaDon(String action) {
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
    int selectedRow = tabManager.getTableHoaDon().getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn cần xử lý!", "Thông báo",
                JOptionPane.WARNING_MESSAGE);
        return;
    }

    String maHoaDon = (String) tabManager.getTableHoaDon().getValueAt(selectedRow, 0);
    HoaDon hoaDon = quanLy.getDsHoaDon().timKiemTheoMa(maHoaDon);

    if (hoaDon == null) {
        JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Hiển thị dialog chọn trạng thái mới
    String currentStatus = hoaDon.getTrangThai();

    // Kiểm tra trạng thái hiện tại có cho phép chuyển không
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

    // Chỉ cho phép chuyển từ CHƯA_THANH_TOÁN
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

    // Xử lý khi chọn "Đã thanh toán" - thêm chọn phương thức thanh toán
    String newStatusCode = "";
    String phuongThucThanhToan = "";

    if (newStatus.equals("Đã thanh toán")) {
        // Hiển thị dialog chọn phương thức thanh toán
        String[] ptOptions = { "Tiền mặt", "Chuyển khoản", "Thẻ tín dụng", "Ví điện tử" };
        phuongThucThanhToan = (String) JOptionPane.showInputDialog(
                this,
                "Chọn phương thức thanh toán cho hóa đơn " + maHoaDon,
                "Phương thức thanh toán",
                JOptionPane.QUESTION_MESSAGE,
                null,
                ptOptions,
                ptOptions[0]);

        if (phuongThucThanhToan == null) {
            return; // Hủy chọn phương thức thanh toán
        }

        newStatusCode = HoaDon.TT_DA_TT;
    } else {
        newStatusCode = HoaDon.TT_HUY;
    }

    // Xác nhận
    String confirmMessage = "";
    if (newStatus.equals("Đã thanh toán")) {
        confirmMessage = "Bạn có chắc chắn muốn thanh toán hóa đơn?\n"
                + "Phương thức thanh toán: " + phuongThucThanhToan + "\n"
                + "Hóa đơn: " + maHoaDon;
    } else {
        confirmMessage = "Bạn có chắc chắn muốn hủy hóa đơn " + maHoaDon + "?";
    }

    int confirm = JOptionPane.showConfirmDialog(
            this,
            confirmMessage,
            "Xác nhận",
            JOptionPane.YES_NO_OPTION);

    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }

    // Cập nhật trạng thái
    hoaDon.setTrangThai(newStatusCode);

    // Nếu là thanh toán, lưu thông tin phương thức thanh toán
    if (newStatusCode.equals(HoaDon.TT_DA_TT)) {
        // Giả sử lớp HoaDon có phương thức setPhuongThucThanhToan
        // Nếu chưa có, bạn cần thêm thuộc tính này vào lớp HoaDon
        hoaDon.setPhuongThucTT(phuongThucThanhToan);
        hoaDon.setNgayLap(new java.util.Date()); // Lưu ngày thanh toán
    }

    // Nếu hủy, cập nhật trạng thái vé
    if (newStatusCode.equals(HoaDon.TT_HUY)) {
        for (VeMayBay ve : hoaDon.getDanhSachVe()) {
            ve.setTrangThai(VeMayBay.TRANG_THAI_DA_HUY);
        }
    }
    int index = -1;
    for(int i =0; i < quanLy.getDsHoaDon().getDanhSach().size(); i++){
        if(maHoaDon.equals(quanLy.getDsHoaDon().getDanhSach().get(i).getMaHoaDon())){
            index = i;
            break;
        }
    }
    quanLy.getDsHoaDon().getDanhSach().set(index, hoaDon);
    quanLy.ghiDuLieuRaFile();
    // Cập nhật giao diện
    capNhatTableHoaDon();
    tabManager.capNhatTableVe(); // Cập nhật vé nếu trạng thái thay đổi

    JOptionPane.showMessageDialog(this, "Đã cập nhật trạng thái hóa đơn thành công!", "Thành công",
            JOptionPane.INFORMATION_MESSAGE);
}

    private void moDialogTimKiemHoaDon() {
        JDialog dialog = new JDialog(this, "Tìm kiếm hóa đơn", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Mã hóa đơn
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Mã hóa đơn:"), gbc);
        JTextField txtMaHoaDon = new JTextField(20);
        gbc.gridx = 1;
        panel.add(txtMaHoaDon, gbc);

        // Mã khách hàng
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Mã khách hàng:"), gbc);
        JTextField txtMaKhachHang = new JTextField(20);
        gbc.gridx = 1;
        panel.add(txtMaKhachHang, gbc);

        // Trạng thái
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Trạng thái:"), gbc);
        JComboBox<String> cbTrangThai = new JComboBox<>(
                new String[] { "Tất cả", "Chưa thanh toán", "Đã thanh toán", "Hủy" });
        gbc.gridx = 1;
        panel.add(cbTrangThai, gbc);

        // Nút tìm kiếm
        JButton btnTimKiem = new JButton("Tìm kiếm");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnTimKiem, gbc);

        dialog.add(panel, BorderLayout.CENTER);

        // Xử lý tìm kiếm
        btnTimKiem.addActionListener(e -> {
            String maHoaDon = txtMaHoaDon.getText().trim();
            String maKhachHang = txtMaKhachHang.getText().trim();
            String trangThai = (String) cbTrangThai.getSelectedItem();

            // Lọc danh sách hóa đơn
            java.util.List<HoaDon> ketQua = new java.util.ArrayList<>();
            for (HoaDon hd : quanLy.getDsHoaDon().getDanhSach()) {
                boolean match = true;

                if (!maHoaDon.isEmpty() && !hd.getMaHoaDon().toLowerCase().contains(maHoaDon.toLowerCase())) {
                    match = false;
                }

                if (!maKhachHang.isEmpty()
                        && !hd.getKhachHang().getMa().toLowerCase().contains(maKhachHang.toLowerCase())) {
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

            // Hiển thị kết quả
            if (ketQua.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Không tìm thấy hóa đơn nào phù hợp!", "Kết quả tìm kiếm",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Cập nhật bảng với kết quả tìm kiếm
                capNhatTableHoaDonVoiKetQua(ketQua);
                JOptionPane.showMessageDialog(dialog, "Tìm thấy " + ketQua.size() + " hóa đơn!", "Kết quả tìm kiếm",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }

    private void capNhatTableHoaDonVoiKetQua(java.util.List<HoaDon> danhSachHoaDon) {
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
