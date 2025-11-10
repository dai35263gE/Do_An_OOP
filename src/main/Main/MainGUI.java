package Main;

import javax.swing.*;

import Main.components.*;
import Main.dialogs.*;
import Main.utils.*;

import java.awt.*;
import java.util.Map;

import Sevice.QuanLyBanVeMayBay;
import model.*;

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
            QuanLyBanVeMayBay.getPhienBan(), tongVe, tongChuyenBay
        );
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
            thongKe.get("tongDoanhThu")
        );
        
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
        String message = 
            "HỆ THỐNG QUẢN LÝ VÉ MÁY BAY\n\n" +
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
        String huongDan = 
            "HƯỚNG DẪN SỬ DỤNG HỆ THỐNG\n\n" +
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
}