package Main;

import javax.swing.*;

import Main.components.*;
import Main.dialogs.*;
import Main.utils.*;

import java.awt.*;
import java.util.Map;

import Sevice.QuanLyBanVeMayBay;

public class MainGUI extends JFrame {
    private QuanLyBanVeMayBay quanLy;
    private TabManager tabManager;
    private StatCardManager statCardManager;
    private MenuManager menuManager;
    private VeDialogs veDialogs;
    private ChuyenBayDialogs chuyenBayDialogs;

    private KhachHangDialogs khachHangDialogs;

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
        // ... khởi tạo các manager khác
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
            thongKe.get("TongVe"),
            thongKe.get("TongChuyenBay"),
            thongKe.get("TongKhachHang"),
            thongKe.get("TongDoanhThu")
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
        if (message.contains("thành công") || message.contains("thành công")) {
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
        
        // Ghi log hoặc xử lý thêm
        System.out.println("Đã chuyển sang tab: " + tabName);
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
            case "Tìm chuyến bay":
                tabManager.chuyenTab(2); // Tab chuyến bay
                // Có thể mở dialog tìm kiếm ở đây
                break;
            case "Thống kê":
                tabManager.chuyenTab(4); // Tab thống kê
                break;
            case "Quản lý":
                // Có thể mở dialog quản lý hệ thống
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
            case "Tìm kiếm":
                veDialogs.moDialogTimKiemVe();
                break;
            case "Lọc":
                // Gọi dialog lọc vé
                break;
            case "Làm mới":
                capNhatTableVe();
                break;
            case "Xem chi tiết":
                veDialogs.xemChiTietVe();
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
                chuyenBayDialogs.xoaChuyenBay();
                break;
            case "Tìm kiếm":
                // Gọi dialog tìm kiếm chuyến bay
                break;
            case "Lọc":
                // Gọi dialog lọc chuyến bay
                break;
            case "Xem chi tiết":
                // Gọi dialog xem chi tiết chuyến bay
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
            case "Làm mới":
                capNhatTableKhachHang();
                break;
            case "Xem hóa đơn":
                khachHangDialogs.xemChiTietHoaDon();
                break;
        }
    }

    public void hienThiThongKe(String loai) {
        // Xử lý hiển thị thống kê
        // Có thể gọi từ TabManager
    }

    // ========== PHƯƠNG THỨC LƯU VÀ THOÁT ==========

    /**
     * Lưu dữ liệu trước khi thoát
     */
    public void luuDuLieu() {
        try {
            quanLy.ghiDuLieuRaFile();
            ValidatorUtils.showSuccessDialog(this, "Đã lưu dữ liệu thành công!");
        } catch (Exception e) {
            ValidatorUtils.showErrorDialog(this, "Lỗi khi lưu dữ liệu: " + e.getMessage());
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

    public static void main(String[] args) {
       

        SwingUtilities.invokeLater(() -> {
            new MainGUI().setVisible(true);
        });
    }
}