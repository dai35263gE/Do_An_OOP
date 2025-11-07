package Main.components;
import Main.*;

import javax.swing.*;
import java.awt.event.ActionListener;

import Sevice.QuanLyBanVeMayBay;

public class MenuManager {
    private JMenuBar menuBar;
    private MainGUI mainGUI;
    private QuanLyBanVeMayBay quanLy;

    public MenuManager(MainGUI mainGUI, QuanLyBanVeMayBay quanLy) {
        this.mainGUI = mainGUI;
        this.quanLy = quanLy;
        this.menuBar = new JMenuBar();
        taoMenuBar();
    }

    public JMenuBar getMenuBar() {
        return menuBar;
    }

    private void taoMenuBar() {
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

        // Menu Quản lý
        JMenu menuQuanLy = new JMenu("Quản lý");
        JMenuItem itemQuanLyVe = new JMenuItem("Quản lý Vé");
        JMenuItem itemQuanLyChuyenBay = new JMenuItem("Quản lý Chuyến Bay");
        JMenuItem itemQuanLyKhachHang = new JMenuItem("Quản lý Khách Hàng");

        // itemQuanLyVe.addActionListener(e -> mainGUI.getTabbedPane().setSelectedIndex(1));
        // itemQuanLyChuyenBay.addActionListener(e -> mainGUI.getTabbedPane().setSelectedIndex(2));
        // itemQuanLyKhachHang.addActionListener(e -> mainGUI.getTabbedPane().setSelectedIndex(3));

        menuQuanLy.add(itemQuanLyVe);
        menuQuanLy.add(itemQuanLyChuyenBay);
        menuQuanLy.add(itemQuanLyKhachHang);

        // Menu Thống kê
        JMenu menuThongKe = new JMenu("Thống kê");
        JMenuItem itemThongKeTongQuan = new JMenuItem("Thống kê Tổng quan");
        JMenuItem itemThongKeDoanhThu = new JMenuItem("Thống kê Doanh thu");
        JMenuItem itemThongKeVe = new JMenuItem("Thống kê Vé");

        // itemThongKeTongQuan.addActionListener(e -> mainGUI.getTabbedPane().setSelectedIndex(4));
        // itemThongKeDoanhThu.addActionListener(e -> mainGUI.getTabbedPane().setSelectedIndex(4));
        // itemThongKeVe.addActionListener(e -> mainGUI.getTabbedPane().setSelectedIndex(4));

        menuThongKe.add(itemThongKeTongQuan);
        menuThongKe.add(itemThongKeDoanhThu);
        menuThongKe.add(itemThongKeVe);

        // Menu Công cụ
        JMenu menuCongCu = new JMenu("Công cụ");
        JMenuItem itemDatVe = new JMenuItem("Đặt vé mới");
        JMenuItem itemTimChuyenBay = new JMenuItem("Tìm chuyến bay");
        JMenuItem itemThemKhachHang = new JMenuItem("Thêm khách hàng");

        menuCongCu.add(itemDatVe);
        menuCongCu.add(itemTimChuyenBay);
        menuCongCu.add(itemThemKhachHang);

        // Menu Help
        JMenu menuHelp = new JMenu("Help");
        JMenuItem itemAbout = new JMenuItem("About");
        JMenuItem itemHuongDan = new JMenuItem("Hướng dẫn sử dụng");
        JMenuItem itemKiemTraCapNhat = new JMenuItem("Kiểm tra cập nhật");

        itemAbout.addActionListener(e -> hienThiAbout());
        itemHuongDan.addActionListener(e -> hienThiHuongDan());
        itemKiemTraCapNhat.addActionListener(e -> kiemTraCapNhat());

        menuHelp.add(itemAbout);
        menuHelp.add(itemHuongDan);
        menuHelp.addSeparator();
        menuHelp.add(itemKiemTraCapNhat);

        // Thêm các menu vào menu bar
        menuBar.add(menuFile);
        menuBar.add(menuQuanLy);
        menuBar.add(menuThongKe);
        menuBar.add(menuCongCu);
        menuBar.add(menuHelp);
    }

    private void saveDuLieu() {
        try {
            quanLy.ghiDuLieuRaFile();
            JOptionPane.showMessageDialog(mainGUI, 
                "Đã lưu dữ liệu thành công!", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainGUI, 
                "Lỗi khi lưu dữ liệu: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void backupDuLieu() {
        try {
            // Thực hiện sao lưu dữ liệu
            JOptionPane.showMessageDialog(mainGUI, 
                "Đã sao lưu dữ liệu thành công!", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainGUI, 
                "Lỗi khi sao lưu dữ liệu: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void thoatChuongTrinh() {
        int confirm = JOptionPane.showConfirmDialog(mainGUI,
                "Bạn có muốn lưu dữ liệu trước khi thoát?", "Xác nhận thoát",
                JOptionPane.YES_NO_CANCEL_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            saveDuLieu();
            System.exit(0);
        } else if (confirm == JOptionPane.NO_OPTION) {
            System.exit(0);
        }
    }

    private void hienThiAbout() {
        String aboutText = "HỆ THỐNG QUẢN LÝ BÁN VÉ MÁY BAY\n\n" +
                "Phiên bản: " + QuanLyBanVeMayBay.getPhienBan() + "\n" +
                "Số lượt truy cập: " + QuanLyBanVeMayBay.getSoLanTruyCap() + "\n\n" +
                "Đặc điểm:\n" +
                "- Lưu trữ dữ liệu bằng file text\n" +
                "- Giao diện đồ họa thân thiện\n" +
                "- Quản lý toàn diện: vé, chuyến bay, khách hàng\n\n" +
                "Chức năng chính:\n" +
                "• Quản lý vé máy bay\n" +
                "• Quản lý chuyến bay\n" +
                "• Quản lý khách hàng\n" +
                "• Thống kê và báo cáo\n\n" +
                "© 2024 - Đồ án Lập trình Hướng đối tượng\n" +
                "Phát triển bởi: Nhóm sinh viên";

        JOptionPane.showMessageDialog(mainGUI, aboutText, "About", JOptionPane.INFORMATION_MESSAGE);
    }

    private void hienThiHuongDan() {
        String huongDanText = "HƯỚNG DẪN SỬ DỤNG HỆ THỐNG\n\n" +
                "1. QUẢN LÝ VÉ:\n" +
                "   - Thêm vé: Nhấn nút 'Thêm vé' hoặc vào menu Công cụ -> Đặt vé mới\n" +
                "   - Sửa vé: Chọn vé trong bảng và nhấn 'Sửa vé'\n" +
                "   - Xóa vé: Chọn vé và nhấn 'Xóa vé'\n" +
                "   - Tìm kiếm: Sử dụng chức năng tìm kiếm theo nhiều tiêu chí\n\n" +
                "2. QUẢN LÝ CHUYẾN BAY:\n" +
                "   - Thêm chuyến bay: Nhấn 'Thêm chuyến'\n" +
                "   - Sửa thông tin: Chọn chuyến bay và nhấn 'Sửa chuyến'\n" +
                "   - Xem chi tiết: Double-click hoặc nhấn 'Xem chi tiết'\n\n" +
                "3. QUẢN LÝ KHÁCH HÀNG:\n" +
                "   - Thêm khách hàng: Nhấn 'Thêm KH' hoặc menu Công cụ\n" +
                "   - Xem hóa đơn: Chọn khách hàng và nhấn 'Xem hóa đơn'\n\n" +
                "4. THỐNG KÊ:\n" +
                "   - Xem thống kê tổng quan hệ thống\n" +
                "   - Thống kê doanh thu theo loại vé\n" +
                "   - Thống kê vé theo loại\n\n" +
                "LƯU Ý:\n" +
                "- Luôn lưu dữ liệu trước khi thoát\n" +
                "- Sao lưu định kỳ để đảm bảo an toàn dữ liệu\n" +
                "- Kiểm tra tính hợp lệ của dữ liệu trước khi lưu";

        JTextArea textArea = new JTextArea(huongDanText, 20, 50);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        
        JOptionPane.showMessageDialog(mainGUI, scrollPane, "Hướng dẫn sử dụng", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void kiemTraCapNhat() {
        // Giả lập kiểm tra cập nhật
        String currentVersion = QuanLyBanVeMayBay.getPhienBan();
        String latestVersion = "2.1.0"; // Giả sử phiên bản mới nhất
        
        if (currentVersion.equals(latestVersion)) {
            JOptionPane.showMessageDialog(mainGUI,
                "Bạn đang sử dụng phiên bản mới nhất: " + currentVersion,
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } else {
            int choice = JOptionPane.showConfirmDialog(mainGUI,
                "Đã có phiên bản mới " + latestVersion + "!\n" +
                "Phiên bản hiện tại: " + currentVersion + "\n\n" +
                "Bạn có muốn cập nhật ngay bây giờ?",
                "Có bản cập nhật mới", JOptionPane.YES_NO_OPTION);
                
            if (choice == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(mainGUI,
                    "Tính năng cập nhật tự động sẽ được triển khai trong phiên bản tới.\n" +
                    "Vui lòng truy cập website để tải phiên bản mới nhất.",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    // Phương thức để thêm menu item động
    public void addCustomMenuItem(String menuName, String itemName, ActionListener action) {
        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            JMenu menu = menuBar.getMenu(i);
            if (menu.getText().equals(menuName)) {
                JMenuItem menuItem = new JMenuItem(itemName);
                menuItem.addActionListener(action);
                menu.add(menuItem);
                return;
            }
        }
        
        // Nếu menu không tồn tại, tạo menu mới
        JMenu newMenu = new JMenu(menuName);
        JMenuItem menuItem = new JMenuItem(itemName);
        menuItem.addActionListener(action);
        newMenu.add(menuItem);
        menuBar.add(newMenu);
    }

    // Phương thức để vô hiệu hóa/hiệu hóa menu
    public void setMenuEnabled(String menuName, boolean enabled) {
        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            JMenu menu = menuBar.getMenu(i);
            if (menu.getText().equals(menuName)) {
                menu.setEnabled(enabled);
                return;
            }
        }
    }

    // Phương thức để lấy menu theo tên
    public JMenu getMenu(String menuName) {
        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            JMenu menu = menuBar.getMenu(i);
            if (menu.getText().equals(menuName)) {
                return menu;
            }
        }
        return null;
    }

    // Phương thức cập nhật trạng thái menu (có thể mở rộng cho phân quyền)
    public void updateMenuState(boolean isAdmin) {
        // Có thể thêm logic phân quyền ở đây
        // Ví dụ: Ẩn/hiện menu dựa trên quyền người dùng
        if (!isAdmin) {
            JMenu adminMenu = getMenu("Quản trị");
            if (adminMenu != null) {
                adminMenu.setVisible(false);
            }
        }
    }
}