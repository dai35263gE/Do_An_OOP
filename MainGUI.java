package Main;

import Sevice.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import model.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class MainGUI extends JFrame {
    private QuanLyBanVeMayBay quanLy;
    private JTabbedPane tabbedPane;
    // Các Tab
    private JPanel panelTrangChu;
    private JPanel panelQuanLyVe;
    private JPanel panelQuanLyChuyenBay;
    private JPanel panelQuanLyKhachHang;
    private JPanel panelThongKe;

    // Bảng
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
        setTitle("HỆ THỐNG QUẢN LÝ VÉ MÁY BAY - Phiên bản " + QuanLyBanVeMayBay.getPhienBan());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null); // GUI nam chinh giua
        // Tạo tabbed pane
        tabbedPane = new JTabbedPane();
        // Tạo các tab
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

    // TẠO TAB TRANG CHỦ

    private JPanel taoStatCard(String title, String value, String type) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(getColorByType(type));
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        card.setPreferredSize(new Dimension(150, 80));

        JLabel lblValue = new JLabel(value, JLabel.CENTER);
        lblValue.setFont(new Font("Arial", Font.BOLD, 25));
        lblValue.setForeground(Color.WHITE);
        lblValue.setName("value"); // Đặt tên để dễ tìm

        JLabel lblTitle = new JLabel(title, JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 20));
        lblTitle.setForeground(Color.WHITE);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
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
        button.setFont(new Font("Arial", Font.PLAIN, 15));
        button.setToolTipText(tooltip);
        button.setPreferredSize(new Dimension(120, 80));
        button.addActionListener(e -> xuLyChucNangNhanh(text));
        return button;
    }

    private Color getColorByType(String type) {
        switch (type) {
            case "primary":
                return new Color(70, 130, 180);
            case "success":
                return new Color(60, 179, 113);
            case "info":
                return new Color(30, 144, 255);
            case "warning":
                return new Color(255, 165, 0);
            default:
                return new Color(70, 130, 180);
        }
    }

    private void taoPanelQuanLyVe() {
        panelQuanLyVe = new JPanel(new BorderLayout());

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));

        String[] buttonNames = { "Thêm vé", "Sửa vé", "Xóa vé", "Tìm kiếm", "Lọc", "Làm mới", "Xem chi tiết" };
        for (String name : buttonNames) {
            JButton btn = new JButton(name);
            btn.addActionListener(e -> xuLyQuanLyVe(name));
            toolbar.add(btn);
        }

        // Bảng dữ liệu
        String[] columns = { "Mã vé", "Mã KH", "Hành khách", "CMND", "Chuyến bay", "Số ghế", "Giờ khởi hành", "Loại vé",
                "Giá vé", "Trạng thái" };
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
        capNhatTableVe();
    }

    private void saveDuLieu() {
        try {
            quanLy.ghiDuLieuRaFile();
            JOptionPane.showMessageDialog(this, "Đã lưu dữ liệu thành công!", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu dữ liệu: " + e.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void backupDuLieu() {
        try {
            JOptionPane.showMessageDialog(this, "Đã sao lưu dữ liệu thành công!", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi sao lưu dữ liệu: " + e.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
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
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                Object[] row = {
                        ve.getMaVe(),
                        ve.getMaKH(),
                        ve.getHoTenKH(),
                        ve.getCmnd(),
                        ve.getMaChuyen(),
                        ve.getSoGhe(),
                        sdf.format(ve.getNgayBay()),
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

        updateStatCard(0, String.valueOf(thongKe.get("TongVe")));
        updateStatCard(1, String.valueOf(thongKe.get("TongChuyenBay")));
        updateStatCard(2, String.valueOf(thongKe.get("TongKhachHang")));
        updateStatCard(3, String.format("%,.0f VND", thongKe.get("TongDoanhThu")));
        updateStatCard(4, String.valueOf(thongKe.get("VeThuongGia")));
        updateStatCard(5, String.valueOf(thongKe.get("VePhoThong")));
        updateStatCard(6, String.valueOf(thongKe.get("VeTietKiem")));
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
                break;
        }
    }

    private void xuLyQuanLyVe(String action) {
        switch (action) {
            case "Thêm vé":
                moDialogDatVe();
                break;
            case "Sửa vé":
                moDialogSuaVe();
                break;
            case "Xóa vé":
                xoaVe();
                break;
            case "Tìm kiếm":
                timKiemVe();
                break;
            case "Lọc":
                sapXepVe();
                break;
            case "Làm mới":
                capNhatTableVe();
                break;
            case "Xem chi tiết":
                xemChiTietVe();
                break;
        }
    }

    private void xuLyQuanLyChuyenBay(String action) {
        switch (action) {
            case "Thêm chuyến":
                moDialogThemChuyenBay();
                break;
            case "Sửa chuyến":
                moDialogSuaChuyenBay();
                break;
            case "Xóa chuyến":
                xoaChuyenBay();
                break;
            case "Tìm kiếm":
                timKiemChuyenBay();
                break;
            case "Lọc":
                locChuyenBay();
                break;
            case "Xem chi tiết":
                xemChiTietChuyenBay();
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
            case "Xem hóa đơn":
                xemChiTietKhachHang();
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
                sb.append("Tổng doanh thu: ").append(String.format("%,.0f VND", thongKe.get("tongDoanhThu")))
                        .append("\n\n");

                sb.append("Phân loại vé:\n");
                sb.append("- Thương gia: ").append(thongKe.get("VeThuongGia")).append(" vé\n");
                sb.append("- Phổ thông: ").append(thongKe.get("VePhoThong")).append(" vé\n");
                sb.append("- Tiết kiệm: ").append(thongKe.get("VeTietKiem")).append(" vé\n");
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

    

    private void xoaVe() {
        int selectedRow = tableVe.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một vé để xóa!",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Lấy thông tin vé được chọn
        String maVe = (String) tableVe.getValueAt(selectedRow, 0);
        String hoTen = (String) tableVe.getValueAt(selectedRow, 1);
        String chuyenBay = (String) tableVe.getValueAt(selectedRow, 3);
        String trangThai = (String) tableVe.getValueAt(selectedRow, 6);

        VeMayBay veCanXoa = quanLy.getDsVe().timKiemTheoMa(maVe);

        if (veCanXoa == null) {
            JOptionPane.showMessageDialog(this,
                    "Không tìm thấy thông tin vé!",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Hiển thị dialog xác nhận với thông tin chi tiết
        JDialog dialogXacNhan = new JDialog(this, "Xác Nhận Xóa Vé", true);
        dialogXacNhan.setSize(450, 300);
        dialogXacNhan.setLocationRelativeTo(this);
        dialogXacNhan.setLayout(new BorderLayout());

        // Panel thông tin vé sẽ xóa
        JPanel panelThongTin = new JPanel(new BorderLayout());
        panelThongTin.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JLabel lblTitle = new JLabel("BẠN CÓ CHẮC CHẮN MUỐN XÓA VÉ NÀY?", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitle.setForeground(Color.RED);

        JTextArea txtThongTin = new JTextArea(8, 30);
        txtThongTin.setEditable(false);
        txtThongTin.setBackground(new Color(255, 245, 245));
        txtThongTin.setBorder(BorderFactory.createLineBorder(new Color(255, 200, 200), 1));
        txtThongTin.setFont(new Font("Arial", Font.PLAIN, 12));
        txtThongTin.setMargin(new Insets(10, 10, 10, 10));

        String thongTinChiTiet = String.format(
                "THÔNG TIN VÉ SẼ XÓA:\n\n" +
                        "🔸 Mã vé: %s\n" +
                        "🔸 Hành khách: %s\n" +
                        "🔸 Chuyến bay: %s\n" +
                        "🔸 Trạng thái: %s\n" +
                        "🔸 Giá vé: %,d VND\n\n" +
                        " CẢNH BÁO: Thao tác này không thể hoàn tác!",
                maVe,
                hoTen,
                chuyenBay,
                trangThai,
                (int) veCanXoa.getGiaVe());
        txtThongTin.setText(thongTinChiTiet);

        panelThongTin.add(lblTitle, BorderLayout.NORTH);
        panelThongTin.add(new JScrollPane(txtThongTin), BorderLayout.CENTER);

        // Panel cảnh báo đặc biệt
        JPanel panelCanhBao = new JPanel(new BorderLayout());
        panelCanhBao.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panelCanhBao.setBackground(new Color(255, 250, 230));

        JLabel lblCanhBao = new JLabel(" LƯU Ý QUAN TRỌNG:");
        lblCanhBao.setFont(new Font("Arial", Font.BOLD, 12));
        lblCanhBao.setForeground(new Color(255, 140, 0));

        JTextArea txtCanhBao = new JTextArea(3, 30);
        txtCanhBao.setEditable(false);
        txtCanhBao.setBackground(new Color(255, 250, 230));
        txtCanhBao.setFont(new Font("Arial", Font.PLAIN, 11));
        txtCanhBao.setLineWrap(true);
        txtCanhBao.setWrapStyleWord(true);

        // Kiểm tra các điều kiện đặc biệt
        List<String> canhBaoList = new ArrayList<>();

        // Kiểm tra nếu vé đã được sử dụng
        if ("ĐÃ SỬ DỤNG".equals(trangThai)) {
            canhBaoList.add("• Vé đã được sử dụng, không thể hoàn tiền");
        }

        // Kiểm tra nếu vé thuộc chuyến bay sắp khởi hành
        ChuyenBay chuyenBayInfo = quanLy.getDsChuyenBay().timKiemTheoMa(veCanXoa.getMaChuyen());
        if (chuyenBayInfo != null) {
            Date now = new Date();
            long diffHours = (chuyenBayInfo.getGioKhoiHanh().getTime() - now.getTime()) / (60 * 60 * 1000);
            if (diffHours < 24) {
                canhBaoList.add("• Chuyến bay khởi hành trong vòng 24h, có thể bị phí hủy");
            }
        }

        // Kiểm tra nếu vé có trong hóa đơn
        // if (veCoTrongHoaDon(maVe)) {
        // canhBaoList.add("• Vé đã được xuất hóa đơn, cần xóa hóa đơn trước");
        // }

        if (canhBaoList.isEmpty()) {
            txtCanhBao.setText("• Xóa vé sẽ giải phóng 1 ghế trên chuyến bay");
        } else {
            StringBuilder canhBaoText = new StringBuilder();
            for (String cb : canhBaoList) {
                canhBaoText.append(cb).append("\n");
            }
            txtCanhBao.setText(canhBaoText.toString());
        }

        panelCanhBao.add(lblCanhBao, BorderLayout.NORTH);
        panelCanhBao.add(txtCanhBao, BorderLayout.CENTER);

        // Panel button
        JPanel panelButton = new JPanel(new FlowLayout());
        panelButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JButton btnXacNhanXoa = new JButton("Xác Nhận Xóa");
        JButton btnHuy = new JButton("Hủy Bỏ");

        // Style cho buttons
        btnXacNhanXoa.setBackground(new Color(220, 80, 60));
        btnXacNhanXoa.setForeground(Color.WHITE);
        btnXacNhanXoa.setFont(new Font("Arial", Font.BOLD, 12));

        btnHuy.setBackground(new Color(100, 100, 100));
        btnHuy.setForeground(Color.WHITE);

        // Xử lý sự kiện xóa
        btnXacNhanXoa.addActionListener(e -> {
            try {
                // Thực hiện xóa vé
                boolean xoaThanhCong = quanLy.xoaVe(maVe);

                if (xoaThanhCong) {
                    // Cập nhật số ghế trống nếu vé chưa sử dụng
                    if (!"ĐÃ SỬ DỤNG".equals(trangThai) && chuyenBayInfo != null) {
                        chuyenBayInfo.setSoGheTrong(chuyenBayInfo.getSoGheTrong() + 1);
                    }

                    // Hiển thị thông báo thành công
                    String message = String.format(
                            " Xóa vé thành công!\n\n" +
                                    "Mã vé: %s\n" +
                                    "Hành khách: %s\n" +
                                    "Chuyến bay: %s",
                            maVe, hoTen, chuyenBay);

                    JOptionPane.showMessageDialog(dialogXacNhan, message, "Thành công",
                            JOptionPane.INFORMATION_MESSAGE);

                    // Đóng dialog và cập nhật giao diện
                    dialogXacNhan.dispose();
                    capNhatDuLieuGUI();

                } else {
                    JOptionPane.showMessageDialog(dialogXacNhan,
                            "Không thể xóa vé!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialogXacNhan,
                        "Lỗi khi xóa vé: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnHuy.addActionListener(e -> dialogXacNhan.dispose());

        panelButton.add(btnXacNhanXoa);
        panelButton.add(btnHuy);

        // Thêm các panel vào dialog
        dialogXacNhan.add(panelThongTin, BorderLayout.NORTH);
        dialogXacNhan.add(panelCanhBao, BorderLayout.CENTER);
        dialogXacNhan.add(panelButton, BorderLayout.SOUTH);

        dialogXacNhan.setVisible(true);
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

    private void timKiemVe() {
        JDialog dialog = new JDialog(this, "Tìm Kiếm Vé Máy Bay", true);
        dialog.setSize(900, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Panel chứa các tiêu chí tìm kiếm
        JPanel panelTimKiem = new JPanel(new GridBagLayout());
        panelTimKiem.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // ComboBox chọn loại tìm kiếm
        JComboBox<String> cbLoaiTimKiem = new JComboBox<>(new String[] {
                "Theo mã vé", "Theo tên khách hàng", "Theo chuyến bay",
                "Theo khoảng giá", "Theo ngày bay", "Theo loại vé", "Theo CMND"
        });

        // Các component cho từng loại tìm kiếm
        JTextField txtMaVe = new JTextField(15);
        JTextField txtTenKH = new JTextField(15);

        // ComboBox chuyến bay
        DanhSachChuyenBay dsChuyenBay = quanLy.getDsChuyenBay();
        Vector<String> chuyenBayItems = new Vector<>();
        if (dsChuyenBay != null && dsChuyenBay.getDanhSachChuyenBay() != null) {
            for (ChuyenBay cb : dsChuyenBay.getDanhSachChuyenBay()) {
                String item = String.format("%s - %s → %s",
                        cb.getMaChuyen(), cb.getDiemDi(), cb.getDiemDen());
                chuyenBayItems.add(item);
            }
        }
        JComboBox<String> cbChuyenBay = new JComboBox<>(chuyenBayItems);

        // Khoảng giá
        JTextField txtGiaMin = new JTextField(10);
        JTextField txtGiaMax = new JTextField(10);
        JPanel panelKhoangGia = new JPanel(new FlowLayout());
        panelKhoangGia.add(new JLabel("Từ:"));
        panelKhoangGia.add(txtGiaMin);
        panelKhoangGia.add(new JLabel("Đến:"));
        panelKhoangGia.add(txtGiaMax);
        panelKhoangGia.add(new JLabel("VND"));

        // Ngày bay
        JSpinner spinnerNgayBay = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinnerNgayBay, "dd/MM/yyyy");
        spinnerNgayBay.setEditor(editor);
        spinnerNgayBay.setValue(new Date());

        // ComboBox loại vé
        JComboBox<String> cbLoaiVe = new JComboBox<>(new String[] {
                "VeThuongGia", "VePhoThong", "VeTietKiem"
        });

        JTextField txtCMND = new JTextField(15);

        // Panel chứa các component tìm kiếm (sẽ thay đổi theo loại tìm kiếm)
        JPanel panelComponent = new JPanel(new FlowLayout());
        panelComponent.add(txtMaVe); // Mặc định hiển thị tìm theo mã vé

        // Xử lý thay đổi loại tìm kiếm
        cbLoaiTimKiem.addActionListener(e -> {
            panelComponent.removeAll();
            String loaiTimKiem = (String) cbLoaiTimKiem.getSelectedItem();

            switch (loaiTimKiem) {
                case "Theo mã vé":
                    panelComponent.add(txtMaVe);
                    break;
                case "Theo tên khách hàng":
                    panelComponent.add(txtTenKH);
                    break;
                case "Theo chuyến bay":
                    panelComponent.add(cbChuyenBay);
                    break;
                case "Theo khoảng giá":
                    panelComponent.add(panelKhoangGia);
                    break;
                case "Theo ngày bay":
                    panelComponent.add(spinnerNgayBay);
                    break;
                case "Theo loại vé":
                    panelComponent.add(cbLoaiVe);
                    break;
                case "Theo CMND":
                    panelComponent.add(txtCMND);
                    break;
            }

            panelComponent.revalidate();
            panelComponent.repaint();
        });

        // Thêm components vào panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelTimKiem.add(new JLabel("Loại tìm kiếm:"), gbc);

        gbc.gridx = 1;
        panelTimKiem.add(cbLoaiTimKiem, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panelTimKiem.add(new JLabel("Giá trị tìm kiếm:"), gbc);

        gbc.gridx = 1;
        panelTimKiem.add(panelComponent, gbc);

        // Panel kết quả
        JPanel panelKetQua = new JPanel(new BorderLayout());
        panelKetQua.setBorder(BorderFactory.createTitledBorder("KẾT QUẢ TÌM KIẾM"));

        // Table kết quả
        String[] columns = { "Mã Vé", "Hành Khách", "CMND", "Chuyến Bay", "Loại Vé", "Ngày Bay", "Giá Vé",
                "Trạng Thái" };
        DefaultTableModel modelKetQua = new DefaultTableModel(columns, 0);
        JTable tableKetQua = new JTable(modelKetQua);
        JScrollPane scrollKetQua = new JScrollPane(tableKetQua);
        panelKetQua.add(scrollKetQua, BorderLayout.CENTER);

        // Label thống kê
        JLabel lblThongKe = new JLabel("Tìm thấy: 0 vé");
        lblThongKe.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelKetQua.add(lblThongKe, BorderLayout.SOUTH);

        // Panel button
        JPanel panelButton = new JPanel(new FlowLayout());
        JButton btnTimKiem = new JButton("Tìm Kiếm");
        JButton btnXoa = new JButton("Xóa Kết Quả");
        JButton btnDong = new JButton("Đóng");

        btnTimKiem.setBackground(new Color(70, 130, 180));
        btnTimKiem.setForeground(Color.WHITE);
        btnXoa.setBackground(new Color(220, 20, 60));
        btnXoa.setForeground(Color.WHITE);

        btnTimKiem.addActionListener(e -> {
            try {
                String loaiTimKiem = (String) cbLoaiTimKiem.getSelectedItem();
                List<VeMayBay> ketQua = new ArrayList<>();

                switch (loaiTimKiem) {
                    case "Theo mã vé":
                        String maVeStr = txtMaVe.getText().trim();
                        if (!maVeStr.isEmpty()) {
                            VeMayBay ve = quanLy.getDsVe().timKiemTheoMa(maVeStr);
                            if (ve != null) {
                                ketQua.add(ve);
                            }
                        }
                        break;

                    case "Theo tên khách hàng":
                        String tenKH = txtTenKH.getText().trim();
                        if (!tenKH.isEmpty()) {
                            ketQua = quanLy.getDsVe().timKiemTheoTen(tenKH);
                        }
                        break;

                    case "Theo chuyến bay":
                        if (cbChuyenBay.getSelectedIndex() >= 0) {
                            String selectedItem = (String) cbChuyenBay.getSelectedItem();
                            String maChuyen = selectedItem.split(" - ")[0];
                            ketQua = quanLy.getDsVe().timKiemTheoChuyenBay(maChuyen);
                        }
                        break;

                    case "Theo khoảng giá":
                        double giaMin = txtGiaMin.getText().trim().isEmpty() ? 0
                                : Double.parseDouble(txtGiaMin.getText().trim());
                        double giaMax = txtGiaMax.getText().trim().isEmpty() ? Double.MAX_VALUE
                                : Double.parseDouble(txtGiaMax.getText().trim());
                        if (giaMin <= giaMax) {
                            ketQua = quanLy.getDsVe().timKiemTheoKhoangGia(giaMin, giaMax);
                        } else {
                            JOptionPane.showMessageDialog(dialog, "Giá min phải nhỏ hơn hoặc bằng giá max!", "Lỗi",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        break;

                    case "Theo ngày bay":
                        Date ngayBay = (Date) spinnerNgayBay.getValue();
                        ketQua = quanLy.getDsVe().timKiemTheoNgayBay(ngayBay);
                        break;

                    case "Theo loại vé":
                        String loaiVe = (String) cbLoaiVe.getSelectedItem();
                        // Sử dụng tìm kiếm đa tiêu chí hoặc lọc thủ công
                        ketQua = quanLy.getDsVe().getDanhSach().stream()
                                .filter(ve -> ve.loaiVe().equals(loaiVe))
                                .collect(Collectors.toList());
                        break;

                    case "Theo CMND":
                        String cmnd = txtCMND.getText().trim();
                        if (!cmnd.isEmpty()) {
                            VeMayBay ve = quanLy.getDsVe().timKiemTheoCMND(cmnd);
                            if (ve != null) {
                                ketQua.add(ve);
                            }
                        }
                        break;
                }

                // Hiển thị kết quả
                hienThiKetQuaTimKiem(modelKetQua, ketQua);
                lblThongKe.setText("Tìm thấy: " + ketQua.size() + " vé");

                if (ketQua.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Không tìm thấy vé nào phù hợp!", "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Giá vé phải là số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnXoa.addActionListener(e -> {
            modelKetQua.setRowCount(0);
            lblThongKe.setText("Tìm thấy: 0 vé");

            // Xóa các trường nhập liệu
            txtMaVe.setText("");
            txtTenKH.setText("");
            txtCMND.setText("");
            txtGiaMin.setText("");
            txtGiaMax.setText("");
            spinnerNgayBay.setValue(new Date());
        });

        btnDong.addActionListener(e -> dialog.dispose());

        panelButton.add(btnTimKiem);
        panelButton.add(btnXoa);
        panelButton.add(btnDong);

        // Thêm các panel vào dialog
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(panelTimKiem, BorderLayout.NORTH);
        mainPanel.add(panelKetQua, BorderLayout.CENTER);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(panelButton, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // Phương thức hiển thị kết quả tìm kiếm
    private void hienThiKetQuaTimKiem(DefaultTableModel model, List<VeMayBay> danhSach) {
        model.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        for (VeMayBay ve : danhSach) {
            Object[] row = {
                    ve.getMaVe(),
                    ve.getHoTenKH(),
                    ve.getCmnd(),
                    ve.getMaChuyen(),
                    ve.loaiVe(),
                    ve.getNgayBay() != null ? sdf.format(ve.getNgayBay()) : "N/A",
                    String.format("%,d VND", (int) ve.getGiaVe()),
                    ve.getTrangThai()
            };
            model.addRow(row);
        }
    }

    private void sapXepVe() {
        JDialog dialog = new JDialog(this, "Sắp Xếp Vé Máy Bay", true);
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Panel chứa các tùy chọn sắp xếp
        JPanel panelSapXep = new JPanel(new GridBagLayout());
        panelSapXep.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // ComboBox chọn tiêu chí sắp xếp
        JComboBox<String> cbTieuChi = new JComboBox<>(new String[] {
                "Theo mã vé", "Theo giá vé", "Theo ngày bay",
                "Theo tên khách hàng", "Theo chuyến bay", "Theo loại vé"
        });

        // ComboBox chọn thứ tự sắp xếp
        JComboBox<String> cbThuTu = new JComboBox<>(new String[] {
                "Tăng dần (A-Z, 0-9)", "Giảm dần (Z-A, 9-0)"
        });

        // Radio buttons cho các tùy chọn bổ sung
        JRadioButton rbTatCa = new JRadioButton("Sắp xếp tất cả vé", true);
        JRadioButton rbTheoTrangThai = new JRadioButton("Sắp xếp theo trạng thái:");
        JComboBox<String> cbTrangThai = new JComboBox<>(new String[] {
                VeMayBay.TRANG_THAI_DAT,
                VeMayBay.TRANG_THAI_HOAN_TAT,
                VeMayBay.TRANG_THAI_HUY,
                VeMayBay.TRANG_THAI_DA_BAY
        });
        cbTrangThai.setEnabled(false);

        ButtonGroup group = new ButtonGroup();
        group.add(rbTatCa);
        group.add(rbTheoTrangThai);

        // Xử lý sự kiện radio button
        rbTheoTrangThai.addActionListener(e -> {
            cbTrangThai.setEnabled(rbTheoTrangThai.isSelected());
        });

        // Thêm components vào panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelSapXep.add(new JLabel("Tiêu chí sắp xếp:"), gbc);

        gbc.gridx = 1;
        panelSapXep.add(cbTieuChi, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panelSapXep.add(new JLabel("Thứ tự:"), gbc);

        gbc.gridx = 1;
        panelSapXep.add(cbThuTu, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panelSapXep.add(rbTatCa, gbc);

        gbc.gridy = 3;
        JPanel panelTrangThai = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTrangThai.add(rbTheoTrangThai);
        panelTrangThai.add(cbTrangThai);
        panelSapXep.add(panelTrangThai, gbc);

        // Panel hiển thị thông tin sắp xếp
        JPanel panelThongTin = new JPanel(new BorderLayout());
        panelThongTin.setBorder(BorderFactory.createTitledBorder("THÔNG TIN SẮP XẾP"));
        JTextArea txtThongTin = new JTextArea(4, 40);
        txtThongTin.setEditable(false);
        txtThongTin.setBackground(new Color(245, 245, 245));
        txtThongTin.setMargin(new Insets(10, 10, 10, 10));
        panelThongTin.add(new JScrollPane(txtThongTin), BorderLayout.CENTER);

        // Cập nhật thông tin khi thay đổi lựa chọn
        ActionListener updateThongTin = e -> {
            String tieuChi = (String) cbTieuChi.getSelectedItem();
            String thuTu = (String) cbThuTu.getSelectedItem();
            String phamVi = rbTatCa.isSelected() ? "tất cả vé" : "vé có trạng thái: " + cbTrangThai.getSelectedItem();

            String thongTin = String.format(
                    "Sắp xếp %s\n" +
                            "Tiêu chí: %s\n" +
                            "Thứ tự: %s\n" +
                            "Tổng số vé sẽ sắp xếp: %d",
                    phamVi, tieuChi, thuTu,
                    rbTatCa.isSelected() ? quanLy.getDsVe().demSoLuong()
                            : quanLy.getDsVe().getDanhSach().stream()
                                    .filter(ve -> ve.getTrangThai().equals(cbTrangThai.getSelectedItem()))
                                    .count());
            txtThongTin.setText(thongTin);
        };

        cbTieuChi.addActionListener(updateThongTin);
        cbThuTu.addActionListener(updateThongTin);
        rbTatCa.addActionListener(updateThongTin);
        rbTheoTrangThai.addActionListener(updateThongTin);
        cbTrangThai.addActionListener(updateThongTin);

        // Gọi lần đầu để hiển thị thông tin
        updateThongTin.actionPerformed(null);

        // Panel button
        JPanel panelButton = new JPanel(new FlowLayout());
        JButton btnSapXep = new JButton("Sắp Xếp");
        JButton btnXemTruoc = new JButton("Xem Trước");
        JButton btnHuy = new JButton("Hủy");

        btnSapXep.setBackground(new Color(70, 130, 180));
        btnSapXep.setForeground(Color.WHITE);
        btnXemTruoc.setBackground(new Color(255, 165, 0));
        btnXemTruoc.setForeground(Color.WHITE);

        btnSapXep.addActionListener(e -> {
            try {
                String tieuChi = (String) cbTieuChi.getSelectedItem();
                boolean tangDan = cbThuTu.getSelectedIndex() == 0;
                List<VeMayBay> danhSachSapXep;

                // Lấy danh sách cần sắp xếp
                if (rbTatCa.isSelected()) {
                    danhSachSapXep = new ArrayList<>(quanLy.getDsVe().getDanhSach());
                } else {
                    String trangThai = (String) cbTrangThai.getSelectedItem();
                    danhSachSapXep = quanLy.getDsVe().getDanhSach().stream()
                            .filter(ve -> ve.getTrangThai().equals(trangThai))
                            .collect(Collectors.toList());
                }

                // Thực hiện sắp xếp
                switch (tieuChi) {
                    case "Theo mã vé":
                        danhSachSapXep.sort(tangDan ? Comparator.comparing(VeMayBay::getMaVe)
                                : Comparator.comparing(VeMayBay::getMaVe).reversed());
                        break;

                    case "Theo giá vé":
                        danhSachSapXep.sort(tangDan ? Comparator.comparingDouble(VeMayBay::getGiaVe)
                                : Comparator.comparingDouble(VeMayBay::getGiaVe).reversed());
                        break;

                    case "Theo ngày bay":
                        danhSachSapXep.sort((v1, v2) -> {
                            if (v1.getNgayBay() == null && v2.getNgayBay() == null)
                                return 0;
                            if (v1.getNgayBay() == null)
                                return tangDan ? -1 : 1;
                            if (v2.getNgayBay() == null)
                                return tangDan ? 1 : -1;
                            return tangDan ? v1.getNgayBay().compareTo(v2.getNgayBay())
                                    : v2.getNgayBay().compareTo(v1.getNgayBay());
                        });
                        break;

                    case "Theo tên khách hàng":
                        danhSachSapXep.sort(tangDan ? Comparator.comparing(VeMayBay::getHoTenKH)
                                : Comparator.comparing(VeMayBay::getHoTenKH).reversed());
                        break;

                    case "Theo chuyến bay":
                        danhSachSapXep.sort(tangDan ? Comparator.comparing(VeMayBay::getMaChuyen)
                                : Comparator.comparing(VeMayBay::getMaChuyen).reversed());
                        break;

                    case "Theo loại vé":
                        danhSachSapXep.sort(tangDan ? Comparator.comparing(VeMayBay::loaiVe)
                                : Comparator.comparing(VeMayBay::loaiVe).reversed());
                        break;
                }

                // Cập nhật lại danh sách chính (nếu sắp xếp tất cả)
                if (rbTatCa.isSelected()) {
                    quanLy.getDsVe().getDanhSach().clear();
                    quanLy.getDsVe().getDanhSach().addAll(danhSachSapXep);
                }

                // Hiển thị thông báo thành công
                String message = String.format(
                        "Sắp xếp thành công!\n\n" +
                                "Tiêu chí: %s\n" +
                                "Thứ tự: %s\n" +
                                "Số lượng vé: %d",
                        tieuChi,
                        tangDan ? "Tăng dần" : "Giảm dần",
                        danhSachSapXep.size());

                JOptionPane.showMessageDialog(dialog, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);

                // Đóng dialog và cập nhật giao diện
                dialog.dispose();
                capNhatDuLieuGUI();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Lỗi: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnXemTruoc.addActionListener(e -> {
            // Hiển thị dialog xem trước kết quả sắp xếp
            hienThiXemTruocSapXep();
        });

        btnHuy.addActionListener(e -> dialog.dispose());

        panelButton.add(btnSapXep);
        panelButton.add(btnXemTruoc);
        panelButton.add(btnHuy);

        // Thêm các panel vào dialog
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(panelSapXep, BorderLayout.NORTH);
        mainPanel.add(panelThongTin, BorderLayout.CENTER);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(panelButton, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // Phương thức hiển thị xem trước kết quả sắp xếp
    private void hienThiXemTruocSapXep() {
        JDialog dialogXemTruoc = new JDialog(this, "Xem Trước Kết Quả Sắp Xếp", true);
        dialogXemTruoc.setSize(800, 500);
        dialogXemTruoc.setLocationRelativeTo(this);
        dialogXemTruoc.setLayout(new BorderLayout());

        // Table hiển thị kết quả xem trước
        String[] columns = { "Mã Vé", "Hành Khách", "Chuyến Bay", "Loại Vé", "Ngày Bay", "Giá Vé", "Trạng Thái" };
        DefaultTableModel modelXemTruoc = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tableXemTruoc = new JTable(modelXemTruoc);
        tableXemTruoc.setAutoCreateRowSorter(false);

        // Sắp xếp mẫu 10 vé đầu tiên theo các tiêu chí
        List<VeMayBay> danhSachXemTruoc = quanLy.getDsVe().getDanhSach().stream()
                .limit(10)
                .collect(Collectors.toList());

        // Sắp xếp theo mã vé tăng dần để demo
        danhSachXemTruoc.sort(Comparator.comparing(VeMayBay::getMaVe));

        // Hiển thị kết quả
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        for (VeMayBay ve : danhSachXemTruoc) {
            Object[] row = {
                    ve.getMaVe(),
                    ve.getHoTenKH(),
                    ve.getMaChuyen(),
                    ve.loaiVe(),
                    ve.getNgayBay() != null ? sdf.format(ve.getNgayBay()) : "N/A",
                    String.format("%,d VND", (int) ve.getGiaVe()),
                    ve.getTrangThai()
            };
            modelXemTruoc.addRow(row);
        }

        JScrollPane scrollXemTruoc = new JScrollPane(tableXemTruoc);

        // Panel thông tin
        JPanel panelInfo = new JPanel(new FlowLayout());
        panelInfo.add(new JLabel("Đây là kết quả xem trước (10 vé đầu tiên) - Sắp xếp theo mã vé tăng dần"));

        // Panel button
        JPanel panelButton = new JPanel(new FlowLayout());
        JButton btnDong = new JButton("Đóng");
        btnDong.addActionListener(e -> dialogXemTruoc.dispose());
        panelButton.add(btnDong);

        dialogXemTruoc.add(panelInfo, BorderLayout.NORTH);
        dialogXemTruoc.add(scrollXemTruoc, BorderLayout.CENTER);
        dialogXemTruoc.add(panelButton, BorderLayout.SOUTH);
        dialogXemTruoc.setVisible(true);
    }

    private void xemChiTietVe() {
        // Kiểm tra có vé nào được chọn không
        int selectedRow = tableVe.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một vé để xem chi tiết!",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Lấy thông tin vé được chọn
        String maVe = (String) tableVe.getValueAt(selectedRow, 0);
        VeMayBay ve = quanLy.getDsVe().timKiemTheoMa(maVe);

        if (ve == null) {
            JOptionPane.showMessageDialog(this,
                    "Không tìm thấy thông tin vé!",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Tạo dialog chi tiết
        JDialog dialog = new JDialog(this, "Chi Tiết Vé Máy Bay - " + maVe, true);
        dialog.setSize(800, 700);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Tạo tabbed pane để phân loại thông tin
        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab 1: Thông tin chung
        tabbedPane.addTab("📋 Thông Tin Chung", taoThongTinChungPanel(ve));

        // Tab 2: Thông tin chuyến bay
        tabbedPane.addTab("✈️ Chuyến Bay", taoThongTinChuyenBayPanel(ve));

        // Tab 3: Thông tin khách hàng
        tabbedPane.addTab("👤 Khách Hàng", taoThongTinKhachHangPanel(ve));

        // Tab 4: Thông tin đặc biệt theo loại vé
        if (ve instanceof VeThuongGia) {
            tabbedPane.addTab("⭐ Thương Gia", taoThongTinThuongGiaPanel((VeThuongGia) ve));
        } else if (ve instanceof VePhoThong) {
            tabbedPane.addTab("💺 Phổ Thông", taoThongTinPhoThongPanel((VePhoThong) ve));
        }

        // Panel button
        JPanel panelButton = new JPanel(new FlowLayout());
        JButton btnIn = new JButton("🖨️ In Thông Tin");
        JButton btnDong = new JButton("Đóng");

        btnIn.setBackground(new Color(70, 130, 180));
        btnIn.setForeground(Color.WHITE);

        btnDong.addActionListener(e -> dialog.dispose());

        panelButton.add(btnIn);
        panelButton.add(btnDong);

        dialog.add(tabbedPane, BorderLayout.CENTER);
        dialog.add(panelButton, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JPanel taoThongTinChungPanel(VeMayBay ve) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 1;

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String ngayBayStr = ve.getNgayBay() != null ? sdf.format(ve.getNgayBay()) : "Chưa xác định";

        // Tiêu đề
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel lblTitle = new JLabel("THÔNG TIN CHI TIẾT VÉ MÁY BAY");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(new Color(70, 130, 180));
        panel.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;

        // Tất cả thông tin trong 1 cột
        addInfoRow(panel, gbc, 1, "🔸 Mã Vé:", ve.getMaVe());
        addInfoRow(panel, gbc, 2, "🔸 Loại Vé:", ve.loaiVe());
        addInfoRow(panel, gbc, 3, "🔸 Trạng Thái:", getTrangThaiWithIcon(ve.getTrangThai()));
        addInfoRow(panel, gbc, 4, "🔸 Ngày Bay:", ngayBayStr);
        addInfoRow(panel, gbc, 5, "🔸 Số Ghế:", ve.getSoGhe());
        addInfoRow(panel, gbc, 6, "🔸 Giá Vé:", String.format("%,d VND", (int) ve.getGiaVe()));
        addInfoRow(panel, gbc, 7, "🔸 Mã KH:", ve.getMaKH());
        addInfoRow(panel, gbc, 8, "🔸 Hành Khách:", ve.getHoTenKH());
        addInfoRow(panel, gbc, 9, "🔸 CMND:", ve.getCmnd());
        addInfoRow(panel, gbc, 10, "🔸 Mã Chuyến:", ve.getMaChuyen());

        // Hiển thị thời gian còn lại nếu chưa bay
        if ("ĐẶT".equals(ve.getTrangThai())) {
            ChuyenBay cb = quanLy.getDsChuyenBay().timKiemTheoMa(ve.getMaChuyen());
            if (cb != null) {
                long diffHours = (cb.getGioKhoiHanh().getTime() - new Date().getTime()) / (60 * 60 * 1000);
                if (diffHours > 0) {
                    addInfoRow(panel, gbc, 11, "⏰ Còn lại:", String.format("%d giờ", diffHours));
                }
            }
        }

        return panel;
    }

    private JPanel taoThongTinChuyenBayPanel(VeMayBay ve) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Tạo GridBagConstraints mới cho panel này
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Lấy thông tin chuyến bay
        ChuyenBay cb = quanLy.getDsChuyenBay().timKiemTheoMa(ve.getMaChuyen());

        if (cb == null) {
            JLabel lblError = new JLabel("Không tìm thấy thông tin chuyến bay!");
            lblError.setForeground(Color.RED);
            panel.add(lblError);
            return panel;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        // Tiêu đề
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel lblTitle = new JLabel("THÔNG TIN CHUYẾN BAY");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(new Color(70, 130, 180));
        panel.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;

        // Thông tin chuyến bay
        addInfoRow(panel, gbc, 1, "✈️ Mã Chuyến:", cb.getMaChuyen());
        addInfoRow(panel, gbc, 2, "📍 Điểm Đi:", cb.getDiemDi());
        addInfoRow(panel, gbc, 3, "🎯 Điểm Đến:", cb.getDiemDen());
        addInfoRow(panel, gbc, 4, "🕒 Khởi Hành:", sdf.format(cb.getGioKhoiHanh()));
        addInfoRow(panel, gbc, 5, "💺 Ghế Trống:", cb.getSoGheTrong() + "/" + cb.getSoGhe());
        addInfoRow(panel, gbc, 6, "💰 Giá Cơ Bản:", String.format("%,d VND", (int) cb.getGiaCoBan()));
        addInfoRow(panel, gbc, 7, "📊 Trạng Thái:", cb.getTrangThai());

        return panel;
    }

    private JPanel taoThongTinKhachHangPanel(VeMayBay ve) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Tạo GridBagConstraints mới cho panel này
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Lấy thông tin khách hàng
        KhachHang kh = quanLy.getDsKhachHang().timKiemTheoCMND(ve.getCmnd());

        if (kh == null) {
            JLabel lblError = new JLabel("Không tìm thấy thông tin khách hàng!");
            lblError.setForeground(Color.RED);
            panel.add(lblError);
            return panel;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        // Tiêu đề
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel lblTitle = new JLabel("THÔNG TIN KHÁCH HÀNG");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(new Color(70, 130, 180));
        panel.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;

        // Thông tin khách hàng
        addInfoRow(panel, gbc, 1, "👤 Mã KH:", kh.getMaKH());
        addInfoRow(panel, gbc, 2, "📛 Họ Tên:", kh.getHoTen());
        addInfoRow(panel, gbc, 3, "📞 Điện Thoại:", kh.getSoDT());
        addInfoRow(panel, gbc, 4, "📧 Email:", kh.getEmail());
        addInfoRow(panel, gbc, 5, "🆔 CMND:", kh.getCmnd());
        addInfoRow(panel, gbc, 6, "⭐ Hạng:", kh.getHangKhachHang());
        addInfoRow(panel, gbc, 7, "🏆 Điểm Tích Lũy:", String.format("%,d điểm", kh.getDiemTichLuy()));
        if (kh.getNgaySinh() != null) {
            addInfoRow(panel, gbc, 8, "🎂 Ngày Sinh:", sdf.format(kh.getNgaySinh()));
        }

        return panel;
    }

    private JPanel taoThongTinThuongGiaPanel(VeThuongGia ve) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(255, 250, 240));

        // Tạo GridBagConstraints mới cho panel này
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Tiêu đề
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel lblTitle = new JLabel("ĐẶC QUYỀN HẠNG THƯƠNG GIA");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(new Color(218, 165, 32));
        panel.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;

        // Thông tin dịch vụ thương gia
        addInfoRow(panel, gbc, 1, "⭐ Dịch Vụ:", ve.getDichVuDacBiet());
        addInfoRow(panel, gbc, 2, "💰 Phí Dịch Vụ:", String.format("%,d VND", (int) ve.getPhuThu()));
        addInfoRow(panel, gbc, 3, "🎒 Hành Lý:", ve.getSoKgHanhLyMienPhi() + " kg");
        addInfoRow(panel, gbc, 4, "🚀 Phòng chờ:", ve.isPhongChoVIP() ? "Có" : "Không");
        addInfoRow(panel, gbc, 5, "🍷 Đồ Uống:", ve.getLoaiDoUong());

        // Thông tin bổ sung
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        JTextArea txtBenefits = new JTextArea(4, 40);
        txtBenefits.setText("✨ ĐẶC QUYỀN:\n" +
                "• Phòng chờ thương gia riêng biệt\n" +
                "• Hành lý miễn phí lên đến " + ve.getSoKgHanhLyMienPhi() + "kg\n" +
                "• Ưu tiên làm thủ tục và lên máy bay\n" +
                "• Thực đơn đa dạng với đồ uống cao cấp\n" +
                "• Ghế ngả hoàn toàn thành giường nằm");
        txtBenefits.setEditable(false);
        txtBenefits.setBackground(new Color(255, 250, 240));
        txtBenefits.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(txtBenefits, gbc);

        return panel;
    }

    private JPanel taoThongTinPhoThongPanel(VePhoThong ve) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 248, 255));

        // Tạo GridBagConstraints mới cho panel này
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Tiêu đề
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel lblTitle = new JLabel("THÔNG TIN HẠNG PHỔ THÔNG");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(new Color(30, 144, 255));
        panel.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;

        // Thông tin vé phổ thông
        addInfoRow(panel, gbc, 1, "💺 Vị Trí Ghế:", ve.getLoaiGhe());
        addInfoRow(panel, gbc, 2, "🍽️ Dịch Vụ Ăn:", ve.isDoAn() ? "Có" : "Không");
        addInfoRow(panel, gbc, 3, "🎒 Hành Lý:", String.format("%,d VND", ve.getSoKgHanhLyKyGui()));
        addInfoRow(panel, gbc, 4, "🚀 Túi xách:", ve.isHanhLyXachTay() ? "Có" : "Không");

        return panel;
    }

    // Phương thức hỗ trợ thêm dòng thông tin - SỬA LẠI
    // Phương thức hỗ trợ thêm dòng thông tin - SỬA LẠI cho 1 cột
    private void addInfoRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        // Reset grid position - chỉ có 1 cột
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        lbl.setPreferredSize(new Dimension(150, 20)); // Cố định chiều rộng label
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(lblValue, gbc);
    }

    // Phương thức hiển thị icon trạng thái
    private String getTrangThaiWithIcon(String trangThai) {
        switch (trangThai) {
            case "ĐẶT":
                return "✅ " + trangThai;
            case "HOÀN TẤT":
                return "🎫 " + trangThai;
            case "HỦY":
                return "❌ " + trangThai;
            case "ĐÃ BAY":
                return "✈️ " + trangThai;
            default:
                return trangThai;
        }
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

    private void taoPanelQuanLyChuyenBay() {
        panelQuanLyChuyenBay = new JPanel(new BorderLayout());

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));

        String[] buttonNames = { "Thêm chuyến", "Sửa chuyến", "Xóa chuyến", "Tìm kiếm", "Lọc", "Xem chi tiết",
                "Làm mới" };
        for (String name : buttonNames) {
            JButton btn = new JButton(name);
            btn.addActionListener(e -> xuLyQuanLyChuyenBay(name));
            toolbar.add(btn);
        }

        // Bảng dữ liệu
        String[] columns = { "Mã chuyến", "Điểm đi", "Điểm đến", "Giờ khởi hành", "Ghế trống", "Giá cơ bản",
                "Trạng thái" };
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        tableChuyenBay = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(tableChuyenBay);

        panelQuanLyChuyenBay.add(toolbar, BorderLayout.NORTH);
        panelQuanLyChuyenBay.add(scrollPane, BorderLayout.CENTER);
        capNhatTableChuyenBay();
    }

    private void taoPanelQuanLyKhachHang() {
        panelQuanLyKhachHang = new JPanel(new BorderLayout());

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));

        String[] buttonNames = { "Thêm KH", "Sửa KH", "Xóa KH", "Tìm kiếm", "Lọc", "Làm mới", "Xem hóa đơn" };
        for (String name : buttonNames) {
            JButton btn = new JButton(name);
            btn.addActionListener(e -> xuLyQuanLyKhachHang(name));
            toolbar.add(btn);
        }

        // Bảng dữ liệu
        String[] columns = { "Mã KH", "Họ tên", "SĐT", "Email", "CMND", "Hạng", "Điểm tích lũy" };
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        tableKhachHang = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(tableKhachHang);

        panelQuanLyKhachHang.add(toolbar, BorderLayout.NORTH);
        panelQuanLyKhachHang.add(scrollPane, BorderLayout.CENTER);
        capNhatTableKhachHang();
    }

    private void taoPanelThongKe() {
        panelThongKe = new JPanel(new BorderLayout());

        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        String[] buttonNames = { "Thống kê tổng quan", "Doanh thu", "Vé theo loại", "Khách hàng", "Chuyến bay",
                "Làm mới" };
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

    private void moDialogThemChuyenBay() {
        JDialog dialog = new JDialog(this, "Thêm Chuyến Bay Mới", true);
        dialog.setSize(600, 650);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Tự động tạo mã chuyến bay
        int soChuyenBayHienTai = quanLy.getDsChuyenBay().demSoLuong();
        String maChuyenTuDong = "CB" + String.format("%03d", soChuyenBayHienTai + 1);
        JTextField txtMaChuyen = new JTextField(maChuyenTuDong, 20);
        txtMaChuyen.setEditable(false);
        txtMaChuyen.setBackground(new Color(240, 240, 240));

        // ComboBox cho điểm đi và điểm đến
        String[] diaDiem = { "Hà Nội (HAN)", "TP.HCM (SGN)", "Đà Nẵng (DAD)", "Nha Trang (CXR)", "Phú Quốc (PQC)",
                "Huế (HUI)" };
        JComboBox<String> cbDiemDi = new JComboBox<>(diaDiem);
        JComboBox<String> cbDiemDen = new JComboBox<>(diaDiem);

        // Spinner cho giờ khởi hành và giờ đến
        JSpinner spinnerGioKhoiHanh = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editorKhoiHanh = new JSpinner.DateEditor(spinnerGioKhoiHanh, "dd/MM/yyyy HH:mm");
        spinnerGioKhoiHanh.setEditor(editorKhoiHanh);

        // Đặt giờ khởi hành mặc định là ngày mai 6:00 sáng
        Calendar calKhoiHanh = Calendar.getInstance();
        calKhoiHanh.add(Calendar.DAY_OF_MONTH, 1);
        calKhoiHanh.set(Calendar.HOUR_OF_DAY, 6);
        calKhoiHanh.set(Calendar.MINUTE, 0);
        spinnerGioKhoiHanh.setValue(calKhoiHanh.getTime());

        JSpinner spinnerGioDen = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editorDen = new JSpinner.DateEditor(spinnerGioDen, "dd/MM/yyyy HH:mm");
        spinnerGioDen.setEditor(editorDen);

        // Đặt giờ đến mặc định là ngày mai 8:00 sáng (sau 2 giờ)
        Calendar calDen = Calendar.getInstance();
        calDen.add(Calendar.DAY_OF_MONTH, 1);
        calDen.set(Calendar.HOUR_OF_DAY, 8);
        calDen.set(Calendar.MINUTE, 0);
        spinnerGioDen.setValue(calDen.getTime());

        JSpinner spinnerSoGhe = new JSpinner(new SpinnerNumberModel(150, 50, 500, 10));

        // ComboBox cho mã máy bay
        String[] mayBay = { "VN-A321", "VN-B787", "VN-A350" };
        JComboBox<String> cbMaMayBay = new JComboBox<>(mayBay);

        JSpinner spinnerGiaCoBan = new JSpinner(new SpinnerNumberModel(1500000.0, 500000.0, 50000000.0, 100000.0));

        // Định dạng spinner giá
        JSpinner.NumberEditor editorGia = new JSpinner.NumberEditor(spinnerGiaCoBan, "#,##0 VND");
        spinnerGiaCoBan.setEditor(editorGia);

        // Thêm components vào panel
        addFormRow(panel, gbc, "Mã chuyến bay:", txtMaChuyen);
        addFormRow(panel, gbc, "Điểm đi:*", cbDiemDi);
        addFormRow(panel, gbc, "Điểm đến:*", cbDiemDen);
        addFormRow(panel, gbc, "Giờ khởi hành:*", spinnerGioKhoiHanh);
        addFormRow(panel, gbc, "Giờ đến:*", spinnerGioDen);
        addFormRow(panel, gbc, "Số ghế:*", spinnerSoGhe);
        addFormRow(panel, gbc, "Mã máy bay:*", cbMaMayBay);
        addFormRow(panel, gbc, "Giá cơ bản:*", spinnerGiaCoBan);

        // Panel hiển thị thông tin
        JPanel panelThongTin = new JPanel(new BorderLayout());
        panelThongTin.setBorder(BorderFactory.createTitledBorder("THÔNG TIN CHUYẾN BAY"));
        JTextArea txtThongTin = new JTextArea(6, 40);
        txtThongTin.setEditable(false);
        txtThongTin.setBackground(new Color(240, 240, 240));
        txtThongTin.setMargin(new Insets(10, 10, 10, 10));
        panelThongTin.add(new JScrollPane(txtThongTin), BorderLayout.CENTER);

        // Cập nhật thông tin khi thay đổi dữ liệu
        Runnable updateChuyenBayInfo = () -> {
            try {
                String maChuyen = txtMaChuyen.getText().trim();
                String diemDi = (String) cbDiemDi.getSelectedItem();
                String diemDen = (String) cbDiemDen.getSelectedItem();
                Date gioKhoiHanh = (Date) spinnerGioKhoiHanh.getValue();
                Date gioDen = (Date) spinnerGioDen.getValue();
                int soGhe = (Integer) spinnerSoGhe.getValue();
                String maMayBay = (String) cbMaMayBay.getSelectedItem();
                double giaCoBan = (Double) spinnerGiaCoBan.getValue();

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                long thoiGianBay = (gioDen.getTime() - gioKhoiHanh.getTime()) / (1000 * 60); // phút

                String info = String.format(
                        "Mã chuyến: %s\n" +
                                "Lộ trình: %s → %s\n" +
                                "Khởi hành: %s\n" +
                                "Đến: %s\n" +
                                "Thời gian bay: %d phút\n" +
                                "Số ghế: %d\n" +
                                "Máy bay: %s\n" +
                                "Giá cơ bản: %s VND\n" +
                                "Khoảng cách: %.0f km",
                        maChuyen,
                        diemDi,
                        diemDen,
                        sdf.format(gioKhoiHanh),
                        sdf.format(gioDen),
                        thoiGianBay,
                        soGhe,
                        maMayBay,
                        String.format("%,.0f", giaCoBan),
                        ChuyenBay.tinhKhoangCach(diemDi, diemDen));

                txtThongTin.setText(info);
            } catch (Exception ex) {
                txtThongTin.setText("Đang cập nhật thông tin...");
            }
        };

        // Thêm listeners
        cbDiemDi.addActionListener(e -> updateChuyenBayInfo.run());
        cbDiemDen.addActionListener(e -> updateChuyenBayInfo.run());
        cbMaMayBay.addActionListener(e -> updateChuyenBayInfo.run());
        spinnerGioKhoiHanh.addChangeListener(e -> updateChuyenBayInfo.run());
        spinnerGioDen.addChangeListener(e -> updateChuyenBayInfo.run());
        spinnerSoGhe.addChangeListener(e -> updateChuyenBayInfo.run());
        spinnerGiaCoBan.addChangeListener(e -> updateChuyenBayInfo.run());

        // Gọi lần đầu
        updateChuyenBayInfo.run();

        // Panel button
        JPanel panelButton = new JPanel(new FlowLayout());
        JButton btnThem = new JButton("Thêm Chuyến Bay");
        JButton btnHuy = new JButton("Hủy");
        JButton btnLamMoi = new JButton("Làm Mới");

        btnThem.setBackground(new Color(70, 130, 180));
        btnThem.setForeground(Color.WHITE);
        btnLamMoi.setBackground(new Color(255, 165, 0));
        btnLamMoi.setForeground(Color.WHITE);

        btnThem.addActionListener(e -> {
            // Validate dữ liệu
            String diemDi = (String) cbDiemDi.getSelectedItem();
            String diemDen = (String) cbDiemDen.getSelectedItem();

            if (diemDi.equals(diemDen)) {
                JOptionPane.showMessageDialog(dialog,
                        "Điểm đi và điểm đến không được trùng nhau!",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // Lấy thông tin từ form
                String maChuyen = txtMaChuyen.getText().trim();
                Date gioKhoiHanh = (Date) spinnerGioKhoiHanh.getValue();
                Date gioDen = (Date) spinnerGioDen.getValue();
                int soGhe = (Integer) spinnerSoGhe.getValue();
                String maMayBay = (String) cbMaMayBay.getSelectedItem();
                double giaCoBan = (Double) spinnerGiaCoBan.getValue();

                // Kiểm tra mã chuyến bay đã tồn tại chưa
                if (quanLy.getDsChuyenBay().timKiemTheoMa(maChuyen) != null) {
                    JOptionPane.showMessageDialog(dialog,
                            "Mã chuyến bay đã tồn tại!",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Kiểm tra thời gian hợp lệ
                if (gioKhoiHanh.after(gioDen)) {
                    JOptionPane.showMessageDialog(dialog,
                            "Giờ khởi hành phải trước giờ đến!",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Kiểm tra thời gian khởi hành phải trong tương lai
                if (gioKhoiHanh.before(new Date())) {
                    JOptionPane.showMessageDialog(dialog,
                            "Giờ khởi hành phải trong tương lai!",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Tạo chuyến bay mới
                ChuyenBay chuyenBayMoi = new ChuyenBay(
                        maChuyen, diemDi, diemDen, gioKhoiHanh, gioDen,
                        soGhe, soGhe, maMayBay, giaCoBan);

                // Thêm vào danh sách
                quanLy.themChuyenBay(chuyenBayMoi);

                // Hiển thị thông báo thành công
                String message = String.format(
                        "Thêm chuyến bay thành công!\n\n" +
                                "Mã chuyến: %s\n" +
                                "Lộ trình: %s → %s\n" +
                                "Khởi hành: %s\n" +
                                "Số ghế: %d\n" +
                                "Máy bay: %s\n" +
                                "Giá cơ bản: %s VND",
                        maChuyen, diemDi, diemDen,
                        new SimpleDateFormat("dd/MM/yyyy HH:mm").format(gioKhoiHanh),
                        soGhe,
                        maMayBay,
                        String.format("%,.0f", giaCoBan));

                JOptionPane.showMessageDialog(dialog, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);

                // Đóng dialog và cập nhật giao diện
                dialog.dispose();
                capNhatDuLieuGUI();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog,
                        "Lỗi: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnLamMoi.addActionListener(e -> {
            // Tạo mã chuyến bay mới
            int soChuyenBayMoi = quanLy.getDsChuyenBay().demSoLuong();
            String maChuyenMoi = "CB" + String.format("%03d", soChuyenBayMoi + 1);
            txtMaChuyen.setText(maChuyenMoi);

            // Reset các combobox
            cbDiemDi.setSelectedIndex(0);
            cbDiemDen.setSelectedIndex(1); // Chọn điểm đến khác mặc định

            // Reset thời gian
            Calendar calNow = Calendar.getInstance();
            calNow.add(Calendar.DAY_OF_MONTH, 1);
            calNow.set(Calendar.HOUR_OF_DAY, 6);
            calNow.set(Calendar.MINUTE, 0);
            spinnerGioKhoiHanh.setValue(calNow.getTime());

            calNow.add(Calendar.HOUR, 2);
            spinnerGioDen.setValue(calNow.getTime());

            // Reset các giá trị khác
            spinnerSoGhe.setValue(150);
            cbMaMayBay.setSelectedIndex(0);
            spinnerGiaCoBan.setValue(1500000.0);

            JOptionPane.showMessageDialog(dialog,
                    "Đã làm mới form với mã chuyến bay mới!",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });

        btnHuy.addActionListener(e -> dialog.dispose());

        panelButton.add(btnThem);
        panelButton.add(btnLamMoi);
        panelButton.add(btnHuy);

        // Thêm các panel vào dialog
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(panel, BorderLayout.NORTH);
        mainPanel.add(panelThongTin, BorderLayout.CENTER);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(panelButton, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // Phương thức hỗ trợ thêm dòng form
    private void addFormRow(JPanel panel, GridBagConstraints gbc, String label, JComponent component) {
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(component, gbc);

        gbc.gridy++;
    }

    private void moDialogSuaChuyenBay() {
        // Kiểm tra có chuyến bay nào được chọn không
        int selectedRow = tableChuyenBay.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một chuyến bay để sửa!",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Lấy thông tin chuyến bay được chọn
        String maChuyen = (String) tableChuyenBay.getValueAt(selectedRow, 0);
        ChuyenBay cbCanSua = quanLy.getDsChuyenBay().timKiemTheoMa(maChuyen);

        if (cbCanSua == null) {
            JOptionPane.showMessageDialog(this,
                    "Không tìm thấy thông tin chuyến bay!",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Sửa Thông Tin Chuyến Bay - " + maChuyen, true);
        dialog.setSize(600, 700);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Panel hiển thị thông tin hiện tại
        JPanel panelThongTinHienTai = new JPanel(new BorderLayout());
        panelThongTinHienTai.setBorder(BorderFactory.createTitledBorder("THÔNG TIN HIỆN TẠI"));
        JTextArea txtThongTinHienTai = new JTextArea(6, 40);
        txtThongTinHienTai.setEditable(false);
        txtThongTinHienTai.setBackground(new Color(245, 245, 245));
        txtThongTinHienTai.setForeground(new Color(70, 130, 180));
        txtThongTinHienTai.setFont(new Font("Arial", Font.BOLD, 12));
        txtThongTinHienTai.setMargin(new Insets(10, 10, 10, 10));

        // Hiển thị thông tin chuyến bay hiện tại
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String thongTinHienTai = String.format(
                "Mã chuyến: %s\n" +
                        "Lộ trình: %s → %s\n" +
                        "Khởi hành: %s\n" +
                        "Đến: %s\n" +
                        "Số ghế: %d/%d\n" +
                        "Giá cơ bản: %,d VND\n" +
                        "Trạng thái: %s",
                cbCanSua.getMaChuyen(),
                cbCanSua.getDiemDi(),
                cbCanSua.getDiemDen(),
                sdf.format(cbCanSua.getGioKhoiHanh()),
                sdf.format(cbCanSua.getGioDen()),
                cbCanSua.getSoGheTrong(),
                cbCanSua.getSoGhe(),
                (int) cbCanSua.getGiaCoBan(),
                cbCanSua.getTrangThai());
        txtThongTinHienTai.setText(thongTinHienTai);
        panelThongTinHienTai.add(new JScrollPane(txtThongTinHienTai), BorderLayout.CENTER);

        // Các component nhập liệu để sửa
        JTextField txtMaChuyen = new JTextField(cbCanSua.getMaChuyen());
        txtMaChuyen.setEditable(false); // Không cho sửa mã chuyến
        txtMaChuyen.setBackground(new Color(240, 240, 240));

        JTextField txtDiemDi = new JTextField(cbCanSua.getDiemDi());
        JTextField txtDiemDen = new JTextField(cbCanSua.getDiemDen());

        // Spinner cho giờ khởi hành và giờ đến
        JSpinner spinnerGioKhoiHanh = new JSpinner(
                new SpinnerDateModel(cbCanSua.getGioKhoiHanh(), null, null, Calendar.HOUR_OF_DAY));
        JSpinner.DateEditor editorKhoiHanh = new JSpinner.DateEditor(spinnerGioKhoiHanh, "dd/MM/yyyy HH:mm");
        spinnerGioKhoiHanh.setEditor(editorKhoiHanh);

        JSpinner spinnerGioDen = new JSpinner(
                new SpinnerDateModel(cbCanSua.getGioDen(), null, null, Calendar.HOUR_OF_DAY));
        JSpinner.DateEditor editorDen = new JSpinner.DateEditor(spinnerGioDen, "dd/MM/yyyy HH:mm");
        spinnerGioDen.setEditor(editorDen);

        JSpinner spinnerSoGhe = new JSpinner(new SpinnerNumberModel(cbCanSua.getSoGhe(), 50, 500, 10));
        JSpinner spinnerSoGheTrong = new JSpinner(
                new SpinnerNumberModel(cbCanSua.getSoGheTrong(), 0, cbCanSua.getSoGhe(), 1));
        JTextField txtMaMayBay = new JTextField(cbCanSua.getMaMayBay());
        JSpinner spinnerGiaCoBan = new JSpinner(
                new SpinnerNumberModel(cbCanSua.getGiaCoBan(), 500000.0, 50000000.0, 100000.0));

        // Định dạng spinner giá
        JSpinner.NumberEditor editorGia = new JSpinner.NumberEditor(spinnerGiaCoBan, "#,##0 VND");
        spinnerGiaCoBan.setEditor(editorGia);

        // ComboBox trạng thái
        JComboBox<String> cbTrangThai = new JComboBox<>(new String[] {
                ChuyenBay.TRANG_THAI_CHUA_BAY,
                ChuyenBay.TRANG_THAI_DANG_BAY,
                ChuyenBay.TRANG_THAI_DA_BAY,
                ChuyenBay.TRANG_THAI_HUY
        });
        cbTrangThai.setSelectedItem(cbCanSua.getTrangThai());

        // Thêm components vào panel
        addFormRow(panel, gbc, "Mã chuyến bay:", txtMaChuyen);
        addFormRow(panel, gbc, "Điểm đi:*", txtDiemDi);
        addFormRow(panel, gbc, "Điểm đến:*", txtDiemDen);
        addFormRow(panel, gbc, "Giờ khởi hành:*", spinnerGioKhoiHanh);
        addFormRow(panel, gbc, "Giờ đến:*", spinnerGioDen);
        addFormRow(panel, gbc, "Tổng số ghế:*", spinnerSoGhe);
        addFormRow(panel, gbc, "Số ghế trống:*", spinnerSoGheTrong);
        addFormRow(panel, gbc, "Mã máy bay:*", txtMaMayBay);
        addFormRow(panel, gbc, "Giá cơ bản:*", spinnerGiaCoBan);
        addFormRow(panel, gbc, "Trạng thái:*", cbTrangThai);

        // Panel hiển thị thông tin cập nhật
        JPanel panelThongTinCapNhat = new JPanel(new BorderLayout());
        panelThongTinCapNhat.setBorder(BorderFactory.createTitledBorder("THÔNG TIN CẬP NHẬT"));
        JTextArea txtThongTinCapNhat = new JTextArea(6, 40);
        txtThongTinCapNhat.setEditable(false);
        txtThongTinCapNhat.setBackground(new Color(240, 248, 255));
        txtThongTinCapNhat.setMargin(new Insets(10, 10, 10, 10));
        panelThongTinCapNhat.add(new JScrollPane(txtThongTinCapNhat), BorderLayout.CENTER);

        // Cập nhật thông tin khi thay đổi dữ liệu
        Runnable updateThongTinCapNhat = () -> {
            try {
                String diemDi = txtDiemDi.getText().trim();
                String diemDen = txtDiemDen.getText().trim();
                Date gioKhoiHanh = (Date) spinnerGioKhoiHanh.getValue();
                Date gioDen = (Date) spinnerGioDen.getValue();
                int soGhe = (Integer) spinnerSoGhe.getValue();
                int soGheTrong = (Integer) spinnerSoGheTrong.getValue();
                double giaCoBan = (Double) spinnerGiaCoBan.getValue();
                String trangThai = (String) cbTrangThai.getSelectedItem();

                long thoiGianBay = (gioDen.getTime() - gioKhoiHanh.getTime()) / (1000 * 60); // phút

                String info = String.format(
                        "THÔNG TIN CẬP NHẬT:\n\n" +
                                "Lộ trình: %s → %s\n" +
                                "Khởi hành: %s\n" +
                                "Đến: %s\n" +
                                "Thời gian bay: %d phút\n" +
                                "Ghế: %d/%d (%.1f%% lấp đầy)\n" +
                                "Giá cơ bản: %s VND\n" +
                                "Trạng thái: %s",
                        diemDi.isEmpty() ? "?" : diemDi,
                        diemDen.isEmpty() ? "?" : diemDen,
                        sdf.format(gioKhoiHanh),
                        sdf.format(gioDen),
                        thoiGianBay,
                        soGhe - soGheTrong, soGhe,
                        ((double) (soGhe - soGheTrong) / soGhe) * 100,
                        String.format("%,.0f", giaCoBan),
                        trangThai);

                txtThongTinCapNhat.setText(info);
            } catch (Exception ex) {
                txtThongTinCapNhat.setText("Đang cập nhật thông tin...");
            }
        };

        // Thêm listener cho các component
        DocumentListener docListener = new DocumentListener() {
            public void anyUpdate() {
                updateThongTinCapNhat.run();
            }

            public void insertUpdate(DocumentEvent e) {
                anyUpdate();
            }

            public void removeUpdate(DocumentEvent e) {
                anyUpdate();
            }

            public void changedUpdate(DocumentEvent e) {
                anyUpdate();
            }
        };

        txtDiemDi.getDocument().addDocumentListener(docListener);
        txtDiemDen.getDocument().addDocumentListener(docListener);
        txtMaMayBay.getDocument().addDocumentListener(docListener);

        spinnerGioKhoiHanh.addChangeListener(e -> updateThongTinCapNhat.run());
        spinnerGioDen.addChangeListener(e -> updateThongTinCapNhat.run());
        spinnerSoGhe.addChangeListener(e -> {
            // Cập nhật giới hạn số ghế trống khi tổng số ghế thay đổi
            int soGheMoi = (Integer) spinnerSoGhe.getValue();
            int soGheTrongHienTai = (Integer) spinnerSoGheTrong.getValue();

            if (soGheTrongHienTai > soGheMoi) {
                spinnerSoGheTrong.setValue(soGheMoi);
            }
            spinnerSoGheTrong.setModel(new SpinnerNumberModel(
                    Math.min(soGheTrongHienTai, soGheMoi), 0, soGheMoi, 1));
            updateThongTinCapNhat.run();
        });
        spinnerSoGheTrong.addChangeListener(e -> updateThongTinCapNhat.run());
        spinnerGiaCoBan.addChangeListener(e -> updateThongTinCapNhat.run());
        cbTrangThai.addActionListener(e -> updateThongTinCapNhat.run());

        // Gọi lần đầu
        updateThongTinCapNhat.run();

        // Panel button
        JPanel panelButton = new JPanel(new FlowLayout());
        JButton btnLuu = new JButton("Lưu Thay Đổi");
        JButton btnHuy = new JButton("Hủy");
        JButton btnKhoiPhuc = new JButton("Khôi Phục Mặc Định");

        btnLuu.setBackground(new Color(70, 130, 180));
        btnLuu.setForeground(Color.WHITE);
        btnKhoiPhuc.setBackground(new Color(255, 165, 0));
        btnKhoiPhuc.setForeground(Color.WHITE);

        btnLuu.addActionListener(e -> {
            // Validate dữ liệu
            if (txtDiemDi.getText().trim().isEmpty() ||
                    txtDiemDen.getText().trim().isEmpty() ||
                    txtMaMayBay.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(dialog,
                        "Vui lòng nhập đầy đủ thông tin bắt buộc (*)",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // Lấy thông tin từ form
                String diemDi = txtDiemDi.getText().trim();
                String diemDen = txtDiemDen.getText().trim();
                Date gioKhoiHanh = (Date) spinnerGioKhoiHanh.getValue();
                Date gioDen = (Date) spinnerGioDen.getValue();
                int soGhe = (Integer) spinnerSoGhe.getValue();
                int soGheTrong = (Integer) spinnerSoGheTrong.getValue();
                String maMayBay = txtMaMayBay.getText().trim();
                double giaCoBan = (Double) spinnerGiaCoBan.getValue();
                String trangThai = (String) cbTrangThai.getSelectedItem();

                // Kiểm tra thời gian hợp lệ
                if (gioKhoiHanh.after(gioDen)) {
                    JOptionPane.showMessageDialog(dialog,
                            "Giờ khởi hành phải trước giờ đến!",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Kiểm tra số ghế trống hợp lệ
                if (soGheTrong > soGhe) {
                    JOptionPane.showMessageDialog(dialog,
                            "Số ghế trống không được lớn hơn tổng số ghế!",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Cập nhật thông tin chuyến bay
                cbCanSua.setDiemDi(diemDi);
                cbCanSua.setDiemDen(diemDen);
                cbCanSua.setGioKhoiHanh(gioKhoiHanh);
                cbCanSua.setGioDen(gioDen);
                cbCanSua.setSoGhe(soGhe);
                cbCanSua.setSoGheTrong(soGheTrong);
                cbCanSua.setMaMayBay(maMayBay);
                cbCanSua.setGiaCoBan(giaCoBan);
                cbCanSua.setTrangThai(trangThai);

                // Hiển thị thông báo thành công
                String message = String.format(
                        "Cập nhật chuyến bay thành công!\n\n" +
                                "Mã chuyến: %s\n" +
                                "Lộ trình: %s → %s\n" +
                                "Khởi hành: %s\n" +
                                "Số ghế: %d/%d\n" +
                                "Giá cơ bản: %s VND\n" +
                                "Trạng thái: %s",
                        cbCanSua.getMaChuyen(),
                        diemDi, diemDen,
                        sdf.format(gioKhoiHanh),
                        soGhe - soGheTrong, soGhe,
                        String.format("%,.0f", giaCoBan),
                        trangThai);

                JOptionPane.showMessageDialog(dialog, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);

                // Đóng dialog và cập nhật giao diện
                dialog.dispose();
                capNhatDuLieuGUI();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Lỗi: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnKhoiPhuc.addActionListener(e -> {
            // Khôi phục về giá trị ban đầu
            txtDiemDi.setText(cbCanSua.getDiemDi());
            txtDiemDen.setText(cbCanSua.getDiemDen());
            spinnerGioKhoiHanh.setValue(cbCanSua.getGioKhoiHanh());
            spinnerGioDen.setValue(cbCanSua.getGioDen());
            spinnerSoGhe.setValue(cbCanSua.getSoGhe());
            spinnerSoGheTrong.setValue(cbCanSua.getSoGheTrong());
            txtMaMayBay.setText(cbCanSua.getMaMayBay());
            spinnerGiaCoBan.setValue(cbCanSua.getGiaCoBan());
            cbTrangThai.setSelectedItem(cbCanSua.getTrangThai());

            JOptionPane.showMessageDialog(dialog,
                    "Đã khôi phục thông tin ban đầu!",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });

        btnHuy.addActionListener(e -> dialog.dispose());

        panelButton.add(btnLuu);
        panelButton.add(btnKhoiPhuc);
        panelButton.add(btnHuy);

        // Thêm các panel vào dialog
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(panelThongTinHienTai, BorderLayout.NORTH);
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.add(panelThongTinCapNhat, BorderLayout.SOUTH);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(panelButton, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void xoaChuyenBay() {
        // Kiểm tra có chuyến bay nào được chọn không
        int selectedRow = tableChuyenBay.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một chuyến bay để xóa!",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Lấy thông tin chuyến bay được chọn
        String maChuyen = (String) tableChuyenBay.getValueAt(selectedRow, 0);
        String diemDi = (String) tableChuyenBay.getValueAt(selectedRow, 1);
        String diemDen = (String) tableChuyenBay.getValueAt(selectedRow, 2);
        String trangThai = (String) tableChuyenBay.getValueAt(selectedRow, 6);

        ChuyenBay cbCanXoa = quanLy.getDsChuyenBay().timKiemTheoMa(maChuyen);

        if (cbCanXoa == null) {
            JOptionPane.showMessageDialog(this,
                    "Không tìm thấy thông tin chuyến bay!",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Kiểm tra trạng thái chuyến bay - CHỈ cho phép xóa nếu trạng thái là HỦY
        if (!trangThai.equals(ChuyenBay.TRANG_THAI_HUY)) {
            JOptionPane.showMessageDialog(this,
                    "Chỉ có thể xóa chuyến bay có trạng thái HỦY!\n" +
                            "Trạng thái hiện tại: " + trangThai + "\n\n" +
                            "Vui lòng chuyển trạng thái chuyến bay sang HỦY trước khi xóa.",
                    "Không thể xóa", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Hiển thị dialog xác nhận với thông tin chi tiết
        JDialog dialogXacNhan = new JDialog(this, "Xác Nhận Xóa Chuyến Bay", true);
        dialogXacNhan.setSize(500, 350);
        dialogXacNhan.setLocationRelativeTo(this);
        dialogXacNhan.setLayout(new BorderLayout());

        // Panel thông tin chuyến bay sẽ xóa
        JPanel panelThongTin = new JPanel(new BorderLayout());
        panelThongTin.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JLabel lblTitle = new JLabel("XÁC NHẬN XÓA CHUYẾN BAY", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(Color.RED);

        JTextArea txtThongTin = new JTextArea(8, 40);
        txtThongTin.setEditable(false);
        txtThongTin.setBackground(new Color(255, 245, 245));
        txtThongTin.setBorder(BorderFactory.createLineBorder(new Color(255, 200, 200), 1));
        txtThongTin.setFont(new Font("Arial", Font.PLAIN, 12));
        txtThongTin.setMargin(new Insets(10, 10, 10, 10));

        String thongTinChiTiet = String.format(
                "THÔNG TIN CHUYẾN BAY SẼ XÓA:\n\n" +
                        "✈️ Mã chuyến: %s\n" +
                        "📍 Lộ trình: %s → %s\n" +
                        "🕒 Trạng thái: %s\n" +
                        "💺 Số ghế: %s\n" +
                        "💰 Giá cơ bản: %s\n\n" +
                        "CẢNH BÁO: Thao tác này không thể hoàn tác!",
                maChuyen,
                diemDi,
                diemDen,
                trangThai,
                tableChuyenBay.getValueAt(selectedRow, 4),
                tableChuyenBay.getValueAt(selectedRow, 5));
        txtThongTin.setText(thongTinChiTiet);

        panelThongTin.add(lblTitle, BorderLayout.NORTH);
        panelThongTin.add(new JScrollPane(txtThongTin), BorderLayout.CENTER);

        // Panel kiểm tra vé liên quan
        JPanel panelKiemTra = new JPanel(new BorderLayout());
        panelKiemTra.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panelKiemTra.setBackground(new Color(255, 250, 230));

        // Kiểm tra xem chuyến bay có vé nào không
        int soVeLienQuan = quanLy.getDsVe().demSoLuongTheoChuyenBay(maChuyen);

        JLabel lblKiemTra = new JLabel("KIỂM TRA RÀNG BUỘC:");
        lblKiemTra.setFont(new Font("Arial", Font.BOLD, 12));
        lblKiemTra.setForeground(new Color(255, 140, 0));

        JTextArea txtKiemTra = new JTextArea(3, 40);
        txtKiemTra.setEditable(false);
        txtKiemTra.setBackground(new Color(255, 250, 230));
        txtKiemTra.setFont(new Font("Arial", Font.PLAIN, 11));
        txtKiemTra.setLineWrap(true);
        txtKiemTra.setWrapStyleWord(true);

        if (soVeLienQuan > 0) {
            txtKiemTra.setText(String.format(
                    "Không thể xóa! Chuyến bay này có %d vé đang liên quan.\n\n" +
                            "Vui lòng xóa hoặc hủy tất cả vé liên quan trước khi xóa chuyến bay.",
                    soVeLienQuan));
        } else {
            txtKiemTra.setText("✅ Có thể xóa: Không có vé nào liên quan đến chuyến bay này.");
        }

        panelKiemTra.add(lblKiemTra, BorderLayout.NORTH);
        panelKiemTra.add(txtKiemTra, BorderLayout.CENTER);

        // Panel button
        JPanel panelButton = new JPanel(new FlowLayout());
        panelButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JButton btnXacNhanXoa = new JButton("Xác Nhận Xóa");
        JButton btnHuy = new JButton("Hủy Bỏ");

        // Style cho buttons
        btnXacNhanXoa.setBackground(new Color(220, 80, 60));
        btnXacNhanXoa.setForeground(Color.WHITE);
        btnXacNhanXoa.setFont(new Font("Arial", Font.BOLD, 12));

        btnHuy.setBackground(new Color(100, 100, 100));
        btnHuy.setForeground(Color.WHITE);

        // Disable nút xóa nếu có vé liên quan
        if (soVeLienQuan > 0) {
            btnXacNhanXoa.setEnabled(false);
            btnXacNhanXoa.setToolTipText("Không thể xóa vì có vé liên quan");
        }

        // Xử lý sự kiện xóa
        btnXacNhanXoa.addActionListener(e -> {
            try {
                // Thực hiện xóa chuyến bay
                boolean xoaThanhCong = quanLy.xoaChuyenBay(maChuyen);

                if (xoaThanhCong) {
                    // Hiển thị thông báo thành công
                    String message = String.format(
                            "Xóa chuyến bay thành công!\n\n" +
                                    "Mã chuyến: %s\n" +
                                    "Lộ trình: %s → %s\n" +
                                    "Trạng thái: %s",
                            maChuyen, diemDi, diemDen, trangThai);

                    JOptionPane.showMessageDialog(dialogXacNhan, message, "Thành công",
                            JOptionPane.INFORMATION_MESSAGE);

                    // Đóng dialog và cập nhật giao diện
                    dialogXacNhan.dispose();
                    capNhatDuLieuGUI();

                } else {
                    JOptionPane.showMessageDialog(dialogXacNhan,
                            "Không thể xóa chuyến bay!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialogXacNhan,
                        "Lỗi khi xóa chuyến bay: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnHuy.addActionListener(e -> dialogXacNhan.dispose());

        panelButton.add(btnXacNhanXoa);
        panelButton.add(btnHuy);

        // Thêm các panel vào dialog
        dialogXacNhan.add(panelThongTin, BorderLayout.NORTH);
        dialogXacNhan.add(panelKiemTra, BorderLayout.CENTER);
        dialogXacNhan.add(panelButton, BorderLayout.SOUTH);

        dialogXacNhan.setVisible(true);
    }

    private void timKiemChuyenBay() {
        JDialog dialog = new JDialog(this, "Tìm Kiếm Chuyến Bay", true);
        dialog.setSize(900, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Panel chứa các tiêu chí tìm kiếm
        JPanel panelTimKiem = new JPanel(new GridBagLayout());
        panelTimKiem.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // ComboBox chọn loại tìm kiếm
        String[] loaiTimKiem = {
                "Tất cả chuyến bay",
                "Theo mã chuyến bay",
                "Theo tuyến bay",
                "Theo ngày bay",
                "Theo khoảng giá",
                "Còn chỗ trống",
                "Tìm kiếm gần đúng",
                "Đa tiêu chí"
        };
        JComboBox<String> cbLoaiTimKiem = new JComboBox<>(loaiTimKiem);

        // Các component cho từng loại tìm kiếm
        JTextField txtMaChuyen = new JTextField(15);

        // ComboBox cho điểm đi và điểm đến
        String[] diaDiem = { "Hà Nội (HAN)", "TP.HCM (SGN)", "Đà Nẵng (DAD)", "Nha Trang (CXR)", "Phú Quốc (PQC)",
                "Huế (HUI)" };
        JComboBox<String> cbDiemDi = new JComboBox<>(diaDiem);
        JComboBox<String> cbDiemDen = new JComboBox<>(diaDiem);

        // Spinner cho ngày bay
        JSpinner spinnerNgayBay = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editorNgay = new JSpinner.DateEditor(spinnerNgayBay, "dd/MM/yyyy");
        spinnerNgayBay.setEditor(editorNgay);
        spinnerNgayBay.setValue(new Date());

        // Khoảng giá
        JTextField txtGiaMin = new JTextField(10);
        JTextField txtGiaMax = new JTextField(10);
        JPanel panelKhoangGia = new JPanel(new FlowLayout());
        panelKhoangGia.add(new JLabel("Từ:"));
        panelKhoangGia.add(txtGiaMin);
        panelKhoangGia.add(new JLabel("Đến:"));
        panelKhoangGia.add(txtGiaMax);
        panelKhoangGia.add(new JLabel("VND"));

        // Tìm kiếm gần đúng
        JTextField txtKeyword = new JTextField(20);

        // Panel đa tiêu chí
        JPanel panelDaTieuChi = new JPanel(new GridLayout(0, 2, 5, 5));
        panelDaTieuChi.setBorder(BorderFactory.createTitledBorder("Đa tiêu chí"));

        JComboBox<String> cbDiemDiMulti = new JComboBox<>(diaDiem);
        cbDiemDiMulti.insertItemAt("-- Tất cả --", 0);
        cbDiemDiMulti.setSelectedIndex(0);

        JComboBox<String> cbDiemDenMulti = new JComboBox<>(diaDiem);
        cbDiemDenMulti.insertItemAt("-- Tất cả --", 0);
        cbDiemDenMulti.setSelectedIndex(0);

        JCheckBox chkConCho = new JCheckBox("Chỉ hiện chuyến còn chỗ");

        JSpinner spinnerTuNgay = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editorTuNgay = new JSpinner.DateEditor(spinnerTuNgay, "dd/MM/yyyy");
        spinnerTuNgay.setEditor(editorTuNgay);

        JSpinner spinnerDenNgay = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editorDenNgay = new JSpinner.DateEditor(spinnerDenNgay, "dd/MM/yyyy");
        spinnerDenNgay.setEditor(editorDenNgay);

        Calendar cal = Calendar.getInstance();
        spinnerTuNgay.setValue(cal.getTime());
        cal.add(Calendar.MONTH, 1);
        spinnerDenNgay.setValue(cal.getTime());

        panelDaTieuChi.add(new JLabel("Điểm đi:"));
        panelDaTieuChi.add(cbDiemDiMulti);
        panelDaTieuChi.add(new JLabel("Điểm đến:"));
        panelDaTieuChi.add(cbDiemDenMulti);
        panelDaTieuChi.add(new JLabel("Từ ngày:"));
        panelDaTieuChi.add(spinnerTuNgay);
        panelDaTieuChi.add(new JLabel("Đến ngày:"));
        panelDaTieuChi.add(spinnerDenNgay);
        panelDaTieuChi.add(new JLabel());
        panelDaTieuChi.add(chkConCho);

        // Panel chứa các component tìm kiếm (sẽ thay đổi theo loại tìm kiếm)
        JPanel panelComponent = new JPanel(new FlowLayout());
        panelComponent.add(new JLabel("Chọn loại tìm kiếm")); // Mặc định

        // Xử lý thay đổi loại tìm kiếm
        cbLoaiTimKiem.addActionListener(e -> {
            panelComponent.removeAll();
            String loaiTim = (String) cbLoaiTimKiem.getSelectedItem();

            switch (loaiTim) {
                case "Tất cả chuyến bay":
                    panelComponent.add(new JLabel("Hiển thị tất cả chuyến bay"));
                    break;
                case "Theo mã chuyến bay":
                    panelComponent.add(new JLabel("Mã chuyến bay:"));
                    panelComponent.add(txtMaChuyen);
                    break;
                case "Theo tuyến bay":
                    panelComponent.add(new JLabel("Điểm đi:"));
                    panelComponent.add(cbDiemDi);
                    panelComponent.add(new JLabel("Điểm đến:"));
                    panelComponent.add(cbDiemDen);
                    break;
                case "Theo ngày bay":
                    panelComponent.add(new JLabel("Ngày bay:"));
                    panelComponent.add(spinnerNgayBay);
                    break;
                case "Theo khoảng giá":
                    panelComponent.add(panelKhoangGia);
                    break;
                case "Còn chỗ trống":
                    panelComponent.add(new JLabel("Chỉ hiện chuyến bay còn chỗ trống"));
                    break;
                case "Tìm kiếm gần đúng":
                    panelComponent.add(new JLabel("Từ khóa:"));
                    panelComponent.add(txtKeyword);
                    break;
                case "Đa tiêu chí":
                    panelComponent.add(panelDaTieuChi);
                    break;
            }

            panelComponent.revalidate();
            panelComponent.repaint();
        });

        // Thêm components vào panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelTimKiem.add(new JLabel("Loại tìm kiếm:"), gbc);

        gbc.gridx = 1;
        panelTimKiem.add(cbLoaiTimKiem, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panelTimKiem.add(new JLabel("Tiêu chí:"), gbc);

        gbc.gridx = 1;
        panelTimKiem.add(panelComponent, gbc);

        // Panel kết quả
        JPanel panelKetQua = new JPanel(new BorderLayout());
        panelKetQua.setBorder(BorderFactory.createTitledBorder("KẾT QUẢ TÌM KIẾM"));

        // Table kết quả
        String[] columns = { "Mã Chuyến", "Điểm Đi", "Điểm Đến", "Giờ Khởi Hành", "Ghế Trống", "Giá Cơ Bản",
                "Trạng Thái" };
        DefaultTableModel modelKetQua = new DefaultTableModel(columns, 0);
        JTable tableKetQua = new JTable(modelKetQua);
        tableKetQua.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollKetQua = new JScrollPane(tableKetQua);
        panelKetQua.add(scrollKetQua, BorderLayout.CENTER);

        // Label thống kê
        JLabel lblThongKe = new JLabel("Tìm thấy: 0 chuyến bay");
        lblThongKe.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelKetQua.add(lblThongKe, BorderLayout.SOUTH);

        // Panel button
        JPanel panelButton = new JPanel(new FlowLayout());
        JButton btnTimKiem = new JButton("Tìm Kiếm");
        JButton btnXoa = new JButton("Xóa Kết Quả");
        JButton btnChon = new JButton("Chọn");
        JButton btnDong = new JButton("Đóng");

        btnTimKiem.setBackground(new Color(70, 130, 180));
        btnTimKiem.setForeground(Color.WHITE);
        btnXoa.setBackground(new Color(220, 20, 60));
        btnXoa.setForeground(Color.WHITE);
        btnChon.setBackground(new Color(60, 179, 113));
        btnChon.setForeground(Color.WHITE);

        btnTimKiem.addActionListener(e -> {
            try {
                String loaiTim = (String) cbLoaiTimKiem.getSelectedItem();
                List<ChuyenBay> ketQua = new ArrayList<>();
                DanhSachChuyenBay dsChuyenBay = quanLy.getDsChuyenBay();

                switch (loaiTim) {
                    case "Tất cả chuyến bay":
                        ketQua = dsChuyenBay.getDanhSach();
                        break;

                    case "Theo mã chuyến bay":
                        String maChuyen = txtMaChuyen.getText().trim();
                        if (!maChuyen.isEmpty()) {
                            ChuyenBay cb = dsChuyenBay.timKiemTheoMa(maChuyen);
                            if (cb != null) {
                                ketQua.add(cb);
                            }
                        } else {
                            JOptionPane.showMessageDialog(dialog, "Vui lòng nhập mã chuyến bay!", "Lỗi",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        break;

                    case "Theo tuyến bay":
                        String diemDi = (String) cbDiemDi.getSelectedItem();
                        String diemDen = (String) cbDiemDen.getSelectedItem();
                        ketQua = dsChuyenBay.timKiemTheoTuyen(diemDi, diemDen);
                        break;

                    case "Theo ngày bay":
                        Date ngayBay = (Date) spinnerNgayBay.getValue();
                        ketQua = dsChuyenBay.timKiemTheoNgayBay(ngayBay);
                        break;

                    case "Theo khoảng giá":
                        double giaMin = txtGiaMin.getText().trim().isEmpty() ? 0
                                : Double.parseDouble(txtGiaMin.getText().trim());
                        double giaMax = txtGiaMax.getText().trim().isEmpty() ? Double.MAX_VALUE
                                : Double.parseDouble(txtGiaMax.getText().trim());
                        if (giaMin <= giaMax) {
                            ketQua = dsChuyenBay.timKiemTheoKhoangGia(giaMin, giaMax);
                        } else {
                            JOptionPane.showMessageDialog(dialog, "Giá min phải nhỏ hơn hoặc bằng giá max!", "Lỗi",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        break;

                    case "Còn chỗ trống":
                        ketQua = dsChuyenBay.getChuyenBayConCho();
                        break;

                    case "Tìm kiếm gần đúng":
                        String keyword = txtKeyword.getText().trim();
                        if (!keyword.isEmpty()) {
                            ketQua = dsChuyenBay.timKiemGanDung(keyword);
                        } else {
                            JOptionPane.showMessageDialog(dialog, "Vui lòng nhập từ khóa tìm kiếm!", "Lỗi",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        break;

                    case "Đa tiêu chí":
                        Map<String, Object> filters = new HashMap<>();

                        // Điểm đi
                        if (cbDiemDiMulti.getSelectedIndex() > 0) {
                            filters.put("diemDi", cbDiemDiMulti.getSelectedItem());
                        }

                        // Điểm đến
                        if (cbDiemDenMulti.getSelectedIndex() > 0) {
                            filters.put("diemDen", cbDiemDenMulti.getSelectedItem());
                        }

                        // Khoảng thời gian
                        filters.put("tuNgay", spinnerTuNgay.getValue());
                        filters.put("denNgay", spinnerDenNgay.getValue());

                        // Còn chỗ
                        if (chkConCho.isSelected()) {
                            filters.put("conCho", true);
                        }

                        ketQua = dsChuyenBay.timKiemChuyenBay(filters);
                        break;
                }

                // Hiển thị kết quả
                hienThiKetQuaTimKiemChuyenBay(modelKetQua, ketQua);
                lblThongKe.setText("Tìm thấy: " + ketQua.size() + " chuyến bay");

                if (ketQua.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Không tìm thấy chuyến bay nào phù hợp!", "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Giá tiền phải là số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnXoa.addActionListener(e -> {
            modelKetQua.setRowCount(0);
            lblThongKe.setText("Tìm thấy: 0 chuyến bay");

            // Xóa các trường nhập liệu
            txtMaChuyen.setText("");
            txtKeyword.setText("");
            txtGiaMin.setText("");
            txtGiaMax.setText("");
            cbDiemDi.setSelectedIndex(0);
            cbDiemDen.setSelectedIndex(0);
            spinnerNgayBay.setValue(new Date());
        });

        btnChon.addActionListener(e -> {
            int selectedRow = tableKetQua.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng chọn một chuyến bay!", "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Có thể thêm logic xử lý khi chọn chuyến bay ở đây
            String maChuyen = (String) tableKetQua.getValueAt(selectedRow, 0);
            JOptionPane.showMessageDialog(dialog, "Đã chọn chuyến bay: " + maChuyen, "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        btnDong.addActionListener(e -> dialog.dispose());

        panelButton.add(btnTimKiem);
        panelButton.add(btnXoa);
        panelButton.add(btnChon);
        panelButton.add(btnDong);

        // Thêm các panel vào dialog
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(panelTimKiem, BorderLayout.NORTH);
        mainPanel.add(panelKetQua, BorderLayout.CENTER);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(panelButton, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // Phương thức hiển thị kết quả tìm kiếm chuyến bay
    private void hienThiKetQuaTimKiemChuyenBay(DefaultTableModel model, List<ChuyenBay> danhSach) {
        model.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        for (ChuyenBay cb : danhSach) {
            Object[] row = {
                    cb.getMaChuyen(),
                    cb.getDiemDi(),
                    cb.getDiemDen(),
                    sdf.format(cb.getGioKhoiHanh()),
                    cb.getSoGheTrong() + "/" + cb.getSoGhe(),
                    String.format("%,d VND", (int) cb.getGiaCoBan()),
                    cb.getTrangThai()
            };
            model.addRow(row);
        }
    }

    private void locChuyenBay() {
        JDialog dialog = new JDialog(this, "Sắp Xếp & Lọc Chuyến Bay", true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Panel chứa các tùy chọn sắp xếp và lọc
        JPanel panelLoc = new JPanel(new GridBagLayout());
        panelLoc.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // ========== PHẦN SẮP XẾP ==========
        JPanel panelSapXep = new JPanel(new GridBagLayout());
        panelSapXep.setBorder(BorderFactory.createTitledBorder("SẮP XẾP THEO"));

        // ComboBox chọn tiêu chí sắp xếp
        JComboBox<String> cbTieuChiSapXep = new JComboBox<>(new String[] {
                "Mã chuyến bay",
                "Giá cơ bản (tăng dần)",
                "Giá cơ bản (giảm dần)",
                "Giờ khởi hành (sớm nhất)",
                "Giờ khởi hành (muộn nhất)",
                "Số ghế trống (nhiều nhất)",
                "Điểm đi (A-Z)",
                "Điểm đến (A-Z)"
        });

        // ComboBox chọn thứ tự sắp xếp (sẽ ẩn/hiện tùy theo tiêu chí)
        JComboBox<String> cbThuTuSapXep = new JComboBox<>(new String[] {
                "Tăng dần (A-Z, 0-9)",
                "Giảm dần (Z-A, 9-0)"
        });

        // Thêm vào panel sắp xếp
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelSapXep.add(new JLabel("Tiêu chí:"), gbc);

        gbc.gridx = 1;
        panelSapXep.add(cbTieuChiSapXep, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panelSapXep.add(new JLabel("Thứ tự:"), gbc);

        gbc.gridx = 1;
        panelSapXep.add(cbThuTuSapXep, gbc);

        // ========== PHẦN LỌC ==========
        JPanel panelFilter = new JPanel(new GridBagLayout());
        panelFilter.setBorder(BorderFactory.createTitledBorder("LỌC THEO ĐIỀU KIỆN"));

        // Lọc theo trạng thái
        JComboBox<String> cbTrangThai = new JComboBox<>(new String[] {
                "-- Tất cả trạng thái --",
                ChuyenBay.TRANG_THAI_CHUA_BAY,
                ChuyenBay.TRANG_THAI_DANG_BAY,
                ChuyenBay.TRANG_THAI_DA_BAY,
                ChuyenBay.TRANG_THAI_HUY
        });

        // Lọc theo điểm đi
        String[] diaDiem = { "-- Tất cả điểm đi --", "Hà Nội (HAN)", "TP.HCM (SGN)", "Đà Nẵng (DAD)", "Nha Trang (CXR)",
                "Phú Quốc (PQC)", "Huế (HUI)" };
        JComboBox<String> cbDiemDi = new JComboBox<>(diaDiem);

        // Lọc theo điểm đến
        JComboBox<String> cbDiemDen = new JComboBox<>(diaDiem);

        // Lọc theo số ghế trống
        JCheckBox chkConCho = new JCheckBox("Chỉ hiện chuyến còn chỗ trống");

        // Lọc theo khoảng giá
        JPanel panelGia = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtGiaMin = new JTextField(8);
        JTextField txtGiaMax = new JTextField(8);
        panelGia.add(new JLabel("Giá từ:"));
        panelGia.add(txtGiaMin);
        panelGia.add(new JLabel("đến:"));
        panelGia.add(txtGiaMax);
        panelGia.add(new JLabel("VND"));

        // Lọc theo máy bay
        String[] mayBay = { "-- Tất cả máy bay --", "VN-A321", "VN-B787", "VN-A350" };
        JComboBox<String> cbMaMayBay = new JComboBox<>(mayBay);

        // Thêm vào panel lọc
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelFilter.add(new JLabel("Trạng thái:"), gbc);

        gbc.gridx = 1;
        panelFilter.add(cbTrangThai, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panelFilter.add(new JLabel("Điểm đi:"), gbc);

        gbc.gridx = 1;
        panelFilter.add(cbDiemDi, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panelFilter.add(new JLabel("Điểm đến:"), gbc);

        gbc.gridx = 1;
        panelFilter.add(cbDiemDen, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panelFilter.add(new JLabel("Máy bay:"), gbc);

        gbc.gridx = 1;
        panelFilter.add(cbMaMayBay, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panelFilter.add(chkConCho, gbc);

        gbc.gridy = 5;
        panelFilter.add(panelGia, gbc);

        // ========== PHẦN HIỂN THỊ THÔNG TIN ==========
        JPanel panelThongTin = new JPanel(new BorderLayout());
        panelThongTin.setBorder(BorderFactory.createTitledBorder("THÔNG TIN LỌC & SẮP XẾP"));
        JTextArea txtThongTin = new JTextArea(4, 50);
        txtThongTin.setEditable(false);
        txtThongTin.setBackground(new Color(240, 240, 240));
        txtThongTin.setMargin(new Insets(10, 10, 10, 10));
        panelThongTin.add(new JScrollPane(txtThongTin), BorderLayout.CENTER);

        // Cập nhật thông tin khi thay đổi lựa chọn
        Runnable updateThongTin = () -> {
            String tieuChiSapXep = (String) cbTieuChiSapXep.getSelectedItem();
            String trangThai = cbTrangThai.getSelectedIndex() > 0 ? (String) cbTrangThai.getSelectedItem() : "Tất cả";
            String diemDi = cbDiemDi.getSelectedIndex() > 0 ? (String) cbDiemDi.getSelectedItem() : "Tất cả";
            String diemDen = cbDiemDen.getSelectedIndex() > 0 ? (String) cbDiemDen.getSelectedItem() : "Tất cả";
            String mamayBay = cbMaMayBay.getSelectedIndex() > 0 ? (String) cbMaMayBay.getSelectedItem() : "Tất cả";

            String thongTin = String.format(
                    "THIẾT LẬP HIỆN TẠI:\n\n" +
                            "Sắp xếp: %s\n" +
                            "Lọc theo:\n" +
                            "• Trạng thái: %s\n" +
                            "• Điểm đi: %s\n" +
                            "• Điểm đến: %s\n" +
                            "• Máy bay: %s\n" +
                            "• Chỉ còn chỗ: %s\n" +
                            "• Khoảng giá: %s - %s VND",
                    tieuChiSapXep,
                    trangThai, diemDi, diemDen, mayBay,
                    chkConCho.isSelected() ? "Có" : "Không",
                    txtGiaMin.getText().isEmpty() ? "0" : txtGiaMin.getText(),
                    txtGiaMax.getText().isEmpty() ? "Không giới hạn" : txtGiaMax.getText());
            txtThongTin.setText(thongTin);
        };

        // Thêm listeners
        cbTieuChiSapXep.addActionListener(e -> updateThongTin.run());
        cbTrangThai.addActionListener(e -> updateThongTin.run());
        cbDiemDi.addActionListener(e -> updateThongTin.run());
        cbDiemDen.addActionListener(e -> updateThongTin.run());
        cbMaMayBay.addActionListener(e -> updateThongTin.run());
        chkConCho.addActionListener(e -> updateThongTin.run());

        DocumentListener docListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                updateThongTin.run();
            }

            public void removeUpdate(DocumentEvent e) {
                updateThongTin.run();
            }

            public void changedUpdate(DocumentEvent e) {
                updateThongTin.run();
            }
        };
        txtGiaMin.getDocument().addDocumentListener(docListener);
        txtGiaMax.getDocument().addDocumentListener(docListener);

        // Gọi lần đầu
        updateThongTin.run();

        // ========== PANEL BUTTON ==========
        JPanel panelButton = new JPanel(new FlowLayout());
        JButton btnApDung = new JButton("Áp Dụng");
        JButton btnXemTruoc = new JButton("Xem Trước");
        JButton btnReset = new JButton("Đặt Lại");
        JButton btnHuy = new JButton("Hủy");

        btnApDung.setBackground(new Color(70, 130, 180));
        btnApDung.setForeground(Color.WHITE);
        btnXemTruoc.setBackground(new Color(255, 165, 0));
        btnXemTruoc.setForeground(Color.WHITE);
        btnReset.setBackground(new Color(100, 100, 100));
        btnReset.setForeground(Color.WHITE);

        btnApDung.addActionListener(e -> {
            try {
                List<ChuyenBay> danhSachLoc = applyFiltersAndSort();

                // Cập nhật danh sách chính
                quanLy.getDsChuyenBay().getDanhSach().clear();
                quanLy.getDsChuyenBay().getDanhSach().addAll(danhSachLoc);

                // Hiển thị thông báo thành công
                JOptionPane.showMessageDialog(dialog,
                        "Đã áp dụng bộ lọc và sắp xếp thành công!\n" +
                                "Số chuyến bay hiển thị: " + danhSachLoc.size(),
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);

                // Đóng dialog và cập nhật giao diện
                dialog.dispose();
                capNhatDuLieuGUI();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnXemTruoc.addActionListener(e -> {
            hienThiXemTruocLoc();
        });

        btnReset.addActionListener(e -> {
            // Reset tất cả về mặc định
            cbTieuChiSapXep.setSelectedIndex(0);
            cbTrangThai.setSelectedIndex(0);
            cbDiemDi.setSelectedIndex(0);
            cbDiemDen.setSelectedIndex(0);
            cbMaMayBay.setSelectedIndex(0);
            chkConCho.setSelected(false);
            txtGiaMin.setText("");
            txtGiaMax.setText("");

            JOptionPane.showMessageDialog(dialog,
                    "Đã đặt lại tất cả bộ lọc!",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });

        btnHuy.addActionListener(e -> dialog.dispose());

        panelButton.add(btnApDung);
        panelButton.add(btnXemTruoc);
        panelButton.add(btnReset);
        panelButton.add(btnHuy);

        // ========== SẮP XẾP LAYOUT CHÍNH ==========
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        panelLoc.add(panelSapXep, gbc);

        gbc.gridy = 1;
        panelLoc.add(panelFilter, gbc);

        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panelLoc.add(panelThongTin, gbc);

        // Thêm các panel vào dialog
        dialog.add(panelLoc, BorderLayout.CENTER);
        dialog.add(panelButton, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // Phương thức áp dụng bộ lọc và sắp xếp
    private List<ChuyenBay> applyFiltersAndSort() {
        DanhSachChuyenBay dsChuyenBay = quanLy.getDsChuyenBay();
        List<ChuyenBay> ketQua = new ArrayList<>(dsChuyenBay.getDanhSach());

        // Áp dụng các bộ lọc
        // (Ở đây cần implement logic lọc dựa trên các điều kiện đã chọn)
        // Tạm thời trả về toàn bộ danh sách
        // Có thể thêm logic lọc chi tiết ở đây

        // Áp dụng sắp xếp
        // (Cần implement logic sắp xếp dựa trên tiêu chí đã chọn)

        return ketQua;
    }

    // Phương thức hiển thị xem trước kết quả lọc
    private void hienThiXemTruocLoc() {
        JDialog dialogXemTruoc = new JDialog(this, "Xem Trước Kết Quả Lọc & Sắp Xếp", true);
        dialogXemTruoc.setSize(800, 500);
        dialogXemTruoc.setLocationRelativeTo(this);
        dialogXemTruoc.setLayout(new BorderLayout());

        // Table hiển thị kết quả xem trước
        String[] columns = { "Mã Chuyến", "Điểm Đi", "Điểm Đến", "Giờ Khởi Hành", "Ghế Trống", "Giá", "Trạng Thái" };
        DefaultTableModel modelXemTruoc = new DefaultTableModel(columns, 0);
        JTable tableXemTruoc = new JTable(modelXemTruoc);

        // Hiển thị 10 chuyến bay đầu tiên (demo)
        List<ChuyenBay> danhSachXemTruoc = quanLy.getDsChuyenBay().getDanhSach().stream()
                .limit(10)
                .collect(java.util.stream.Collectors.toList());

        // Sắp xếp demo theo mã chuyến bay
        danhSachXemTruoc.sort(java.util.Comparator.comparing(ChuyenBay::getMaChuyen));

        // Hiển thị kết quả
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        for (ChuyenBay cb : danhSachXemTruoc) {
            Object[] row = {
                    cb.getMaChuyen(),
                    cb.getDiemDi(),
                    cb.getDiemDen(),
                    sdf.format(cb.getGioKhoiHanh()),
                    cb.getSoGheTrong() + "/" + cb.getSoGhe(),
                    String.format("%,d VND", (int) cb.getGiaCoBan()),
                    cb.getTrangThai()
            };
            modelXemTruoc.addRow(row);
        }

        JScrollPane scrollXemTruoc = new JScrollPane(tableXemTruoc);

        // Panel thông tin
        JPanel panelInfo = new JPanel(new FlowLayout());
        panelInfo.add(new JLabel("Đây là kết quả xem trước (10 chuyến bay đầu tiên) - Sắp xếp theo mã chuyến bay"));

        // Panel button
        JPanel panelButton = new JPanel(new FlowLayout());
        JButton btnDong = new JButton("Đóng");
        btnDong.addActionListener(e -> dialogXemTruoc.dispose());
        panelButton.add(btnDong);

        dialogXemTruoc.add(panelInfo, BorderLayout.NORTH);
        dialogXemTruoc.add(scrollXemTruoc, BorderLayout.CENTER);
        dialogXemTruoc.add(panelButton, BorderLayout.SOUTH);
        dialogXemTruoc.setVisible(true);
    }

    private void xemChiTietChuyenBay() {
        // Kiểm tra có chuyến bay nào được chọn không
        int selectedRow = tableChuyenBay.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một chuyến bay để xem chi tiết!",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Lấy thông tin chuyến bay được chọn
        String maChuyen = (String) tableChuyenBay.getValueAt(selectedRow, 0);
        ChuyenBay cb = quanLy.getDsChuyenBay().timKiemTheoMa(maChuyen);

        if (cb == null) {
            JOptionPane.showMessageDialog(this,
                    "Không tìm thấy thông tin chuyến bay!",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Tạo dialog chi tiết
        JDialog dialog = new JDialog(this, "Chi Tiết Chuyến Bay - " + maChuyen, true);
        dialog.setSize(800, 700);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Tạo tabbed pane để phân loại thông tin
        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab 1: Thông tin chung
        tabbedPane.addTab("📋 Thông Tin Chung", taoThongTinChungPanel(cb));

        // Tab 2: Thông tin chuyến bay
        tabbedPane.addTab("✈️ Thông Tin Bay", taoThongTinBayPanel(cb));

        // Tab 3: Thống kê & Doanh thu
        tabbedPane.addTab("📊 Thống Kê", taoThongTinThongKePanel(cb));

        // Tab 4: Danh sách vé (nếu có)
        tabbedPane.addTab("🎫 Vé Đã Đặt", taoThongTinVePanel(cb));

        // Panel button
        JPanel panelButton = new JPanel(new FlowLayout());
        JButton btnIn = new JButton("🖨️ In Thông Tin");
        JButton btnDong = new JButton("Đóng");
        JButton btnCapNhat = new JButton("🔄 Cập Nhật Trạng Thái");

        btnIn.setBackground(new Color(70, 130, 180));
        btnIn.setForeground(Color.WHITE);
        btnCapNhat.setBackground(new Color(60, 179, 113));
        btnCapNhat.setForeground(Color.WHITE);

        btnIn.addActionListener(e -> {
            // Logic in thông tin
            JOptionPane.showMessageDialog(dialog,
                    "Chức năng in sẽ được triển khai sau!",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });

        btnCapNhat.addActionListener(e -> {
            // Cập nhật trạng thái chuyến bay
            cb.capNhatTrangThaiBay();
            JOptionPane.showMessageDialog(dialog,
                    "Đã cập nhật trạng thái chuyến bay!\nTrạng thái mới: " + cb.getTrangThai(),
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);

            // Refresh thông tin
            capNhatDuLieuGUI();
            dialog.dispose();
            xemChiTietChuyenBay(); // Mở lại dialog với thông tin mới
        });

        btnDong.addActionListener(e -> dialog.dispose());

        panelButton.add(btnIn);
        panelButton.add(btnCapNhat);
        panelButton.add(btnDong);

        dialog.add(tabbedPane, BorderLayout.CENTER);
        dialog.add(panelButton, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // Tab 1: Thông tin chung
    private JPanel taoThongTinChungPanel(ChuyenBay cb) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String ngayKhoiHanhStr = cb.getGioKhoiHanh() != null ? sdf.format(cb.getGioKhoiHanh()) : "N/A";
        String ngayDenStr = cb.getGioDen() != null ? sdf.format(cb.getGioDen()) : "N/A";

        // Tiêu đề
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel lblTitle = new JLabel("THÔNG TIN CHI TIẾT CHUYẾN BAY");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(new Color(70, 130, 180));
        panel.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;

        // Thông tin cơ bản
        addInfoRow2(panel, gbc, 1, "🔸 Mã Chuyến Bay:", cb.getMaChuyen());
        addInfoRow2(panel, gbc, 2, "🔸 Máy Bay:", cb.getMaMayBay());
        addInfoRow2(panel, gbc, 3, "🔸 Trạng Thái:", getTrangThaiWithIcon(cb.getTrangThai()));

        // Thông tin lộ trình
        addInfoRow2(panel, gbc, 4, "📍 Điểm Đi:", cb.getDiemDi());
        addInfoRow2(panel, gbc, 5, "🎯 Điểm Đến:", cb.getDiemDen());
        addInfoRow2(panel, gbc, 6, "📏 Khoảng Cách:",
                String.format("%.0f km", ChuyenBay.tinhKhoangCach(cb.getDiemDi(), cb.getDiemDen())));

        // Thông tin thời gian
        addInfoRow2(panel, gbc, 7, "🕒 Khởi Hành:", ngayKhoiHanhStr);
        addInfoRow2(panel, gbc, 8, "🕒 Dự Kiến Đến:", ngayDenStr);

        // Tính thời gian bay
        if (cb.getGioKhoiHanh() != null && cb.getGioDen() != null) {
            long thoiGianBay = (long) cb.tinhThoiGianBay();
            long gio = thoiGianBay / 60;
            long phut = thoiGianBay % 60;
            addInfoRow2(panel, gbc, 9, "⏱️ Thời Gian Bay:", String.format("%d giờ %d phút", gio, phut));
        }

        // Thông tin ghế
        addInfoRow2(panel, gbc, 10, "💺 Tổng Số Ghế:", String.valueOf(cb.getSoGhe()));
        addInfoRow2(panel, gbc, 11, "💺 Ghế Trống:", String.valueOf(cb.getSoGheTrong()));
        addInfoRow2(panel, gbc, 12, "📊 Tỷ Lệ Lấp Đầy:",
                String.format("%.1f%%", ((double) (cb.getSoGhe() - cb.getSoGheTrong()) / cb.getSoGhe()) * 100));

        // Thông tin giá
        addInfoRow2(panel, gbc, 13, "💰 Giá Cơ Bản:", String.format("%,d VND", (int) cb.getGiaCoBan()));

        // Thông tin bổ sung
        gbc.gridy = 14;
        gbc.gridwidth = 2;
        JTextArea txtGhiChu = new JTextArea(3, 40);
        txtGhiChu.setText("💡 THÔNG TIN BỔ SUNG:\n" +
                "• Chuyến bay " + (cb.conGheTrong() ? "vẫn còn chỗ trống" : "đã hết chỗ") + "\n" +
                "• " + getTrangThaiMoTa(cb.getTrangThai()));
        txtGhiChu.setEditable(false);
        txtGhiChu.setBackground(new Color(240, 248, 255));
        txtGhiChu.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(txtGhiChu, gbc);

        return panel;
    }

    // Tab 2: Thông tin bay
    private JPanel taoThongTinBayPanel(ChuyenBay cb) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 248, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        SimpleDateFormat sdfNgay = new SimpleDateFormat("EEEE, dd/MM/yyyy", new java.util.Locale("vi"));

        // Tiêu đề
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel lblTitle = new JLabel("THÔNG TIN HÀNH TRÌNH");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(new Color(30, 144, 255));
        panel.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;

        // Thông tin hành trình chi tiết
        addInfoRow2(panel, gbc, 1, "✈️ Sân Bay Đi:", extractSanBay(cb.getDiemDi()));
        addInfoRow2(panel, gbc, 2, "✈️ Sân Bay Đến:", extractSanBay(cb.getDiemDen()));

        addInfoRow2(panel, gbc, 3, "📅 Ngày Khởi Hành:", sdfNgay.format(cb.getGioKhoiHanh()));
        addInfoRow2(panel, gbc, 4, "📅 Ngày Đến:", sdfNgay.format(cb.getGioDen()));

        // Tính toán thời gian còn lại (nếu chưa bay)
        if (cb.getTrangThai().equals(ChuyenBay.TRANG_THAI_CHUA_BAY)) {
            long thoiGianConLai = cb.getGioKhoiHanh().getTime() - new Date().getTime();
            if (thoiGianConLai > 0) {
                long ngay = thoiGianConLai / (1000 * 60 * 60 * 24);
                long gio = (thoiGianConLai % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
                addInfoRow2(panel, gbc, 5, "⏰ Cất Cánh Sau:", String.format("%d ngày %d giờ", ngay, gio));
            }
        }

        // Thông tin kỹ thuật
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        JTextArea txtKyThuat = new JTextArea(4, 40);
        txtKyThuat.setText("🔧 THÔNG TIN KỸ THUẬT:\n" +
                "• Loại máy bay: " + getLoaiMayBay(cb.getMaMayBay()) + "\n" +
                "• Sức chứa: " + cb.getSoGhe() + " ghế\n" +
                "• Tình trạng: " + (cb.kiemTraChuyenBayHopLe() ? "Hợp lệ" : "Cần kiểm tra") + "\n" +
                "• Kiểm tra lịch bay: " + (cb.getGioKhoiHanh().after(new Date()) ? "Theo kế hoạch" : "Đã qua"));
        txtKyThuat.setEditable(false);
        txtKyThuat.setBackground(new Color(230, 240, 255));
        txtKyThuat.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(txtKyThuat, gbc);

        return panel;
    }

    // Tab 3: Thống kê
    private JPanel taoThongTinThongKePanel(ChuyenBay cb) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(255, 250, 240));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Tiêu đề
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel lblTitle = new JLabel("THỐNG KÊ & DOANH THU");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(new Color(218, 165, 32));
        panel.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;

        // Thống kê ghế
        int soGheDaDat = cb.getSoGhe() - cb.getSoGheTrong();
        double tyLeDat = ((double) soGheDaDat / cb.getSoGhe()) * 100;

        addInfoRow(panel, gbc, 1, "📈 Ghế Đã Đặt:",
                soGheDaDat + "/" + cb.getSoGhe() + " (" + String.format("%.1f", tyLeDat) + "%)");
        addInfoRow(panel, gbc, 2, "📈 Ghế Trống:",
                cb.getSoGheTrong() + "/" + cb.getSoGhe() + " (" + String.format("%.1f", 100 - tyLeDat) + "%)");

        // Doanh thu ước tính
        double doanhThuUocTinh = soGheDaDat * cb.getGiaCoBan();
        addInfoRow(panel, gbc, 3, "💰 Doanh Thu Ước Tính:", String.format("%,d VND", (int) doanhThuUocTinh));

        // Doanh thu tối đa
        double doanhThuToiDa = cb.getSoGhe() * cb.getGiaCoBan();
        addInfoRow(panel, gbc, 4, "💰 Doanh Thu Tối Đa:", String.format("%,d VND", (int) doanhThuToiDa));

        // Hiệu suất
        addInfoRow(panel, gbc, 5, "📊 Hiệu Suất:", String.format("%.1f%%", tyLeDat));

        // Biểu đồ đơn giản (text-based)
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        JTextArea txtBieuDo = new JTextArea(6, 40);

        // Tạo biểu đồ thanh đơn giản
        int barLength = 30;
        int filledLength = (int) (barLength * (tyLeDat / 100));
        String bar = "[" + "█".repeat(filledLength) + "░".repeat(barLength - filledLength) + "]";

        txtBieuDo.setText("📊 BIỂU ĐỒ LẤP ĐẦY GHẾ:\n\n" +
                bar + " " + String.format("%.1f", tyLeDat) + "%\n\n" +
                "█ Ghế đã đặt: " + soGheDaDat + " ghế\n" +
                "░ Ghế trống: " + cb.getSoGheTrong() + " ghế\n\n" +
                "Phân loại:\n" +
                "• Dưới 50%: Cần quảng cáo\n" +
                "• 50-80%: Hiệu suất tốt\n" +
                "• Trên 80%: Xuất sắc");
        txtBieuDo.setEditable(false);
        txtBieuDo.setBackground(new Color(255, 245, 230));
        txtBieuDo.setFont(new Font("Consolas", Font.PLAIN, 12));
        panel.add(txtBieuDo, gbc);

        return panel;
    }

    // Tab 4: Thông tin vé
    private JPanel taoThongTinVePanel(ChuyenBay cb) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(245, 245, 245));

        // Lấy danh sách vé của chuyến bay này
        List<VeMayBay> danhSachVe = quanLy.getDsVe().timKiemTheoChuyenBay(cb.getMaChuyen());

        if (danhSachVe.isEmpty()) {
            JLabel lblEmpty = new JLabel("Chưa có vé nào được đặt cho chuyến bay này", JLabel.CENTER);
            lblEmpty.setFont(new Font("Arial", Font.ITALIC, 14));
            lblEmpty.setForeground(Color.GRAY);
            panel.add(lblEmpty, BorderLayout.CENTER);
            return panel;
        }

        // Tạo bảng hiển thị vé
        String[] columns = { "Mã Vé", "Hành Khách", "CMND", "Loại Vé", "Số Ghế", "Giá Vé", "Trạng Thái" };
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable tableVe = new JTable(model);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        for (VeMayBay ve : danhSachVe) {
            Object[] row = {
                    ve.getMaVe(),
                    ve.getHoTenKH(),
                    ve.getCmnd(),
                    ve.loaiVe(),
                    ve.getSoGhe(),
                    String.format("%,d VND", (int) ve.getGiaVe()),
                    ve.getTrangThai()
            };
            model.addRow(row);
        }

        JScrollPane scrollPane = new JScrollPane(tableVe);

        // Thông tin tổng hợp
        JPanel panelTongHop = new JPanel(new FlowLayout());
        panelTongHop.add(new JLabel("Tổng số vé: " + danhSachVe.size() + " | "));
        panelTongHop.add(new JLabel("Tổng doanh thu: " +
                String.format("%,d VND", (int) danhSachVe.stream().mapToDouble(VeMayBay::getGiaVe).sum())));

        panel.add(new JLabel("DANH SÁCH VÉ ĐÃ ĐẶT (" + danhSachVe.size() + " vé)"), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(panelTongHop, BorderLayout.SOUTH);

        return panel;
    }

    // ========== PHƯƠNG THỨC HỖ TRỢ ==========

    private void addInfoRow2(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridx = 0;
        gbc.gridy = row;

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        lbl.setPreferredSize(new Dimension(180, 20));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(lblValue, gbc);
    }

    private String getTrangThaiMoTa(String trangThai) {
        switch (trangThai) {
            case ChuyenBay.TRANG_THAI_CHUA_BAY:
                return "Chuyến bay chưa khởi hành, có thể đặt vé";
            case ChuyenBay.TRANG_THAI_DANG_BAY:
                return "Chuyến bay đang trong hành trình";
            case ChuyenBay.TRANG_THAI_DA_BAY:
                return "Chuyến bay đã hoàn thành";
            case ChuyenBay.TRANG_THAI_HUY:
                return "Chuyến bay đã bị hủy, không thể đặt vé";
            default:
                return "";
        }
    }

    private String extractSanBay(String diaDiem) {
        // Trích xuất tên sân bay từ chuỗi "Thành phố (Mã)"
        if (diaDiem.contains("(")) {
            return diaDiem.split("\\(")[0].trim();
        }
        return diaDiem;
    }

    private String getLoaiMayBay(String maMayBay) {
        switch (maMayBay) {
            case "VN-A321":
                return "Airbus A321 (180-220 chỗ)";
            case "VN-B787":
                return "Boeing 787 Dreamliner (250-290 chỗ)";
            case "VN-A350":
                return "Airbus A350 (300-350 chỗ)";
            default:
                return "Không xác định";
        }
    }

    private void moDialogThemKhachHang() {
        JDialog dialog = new JDialog(this, "Thêm Khách Hàng Mới", true);
        dialog.setSize(500, 550);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Các component nhập liệu
        JTextField txtMaKH = new JTextField();
        JTextField txtHoTen = new JTextField();
        JTextField txtSoDT = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtCMND = new JTextField();
        JTextField txtNgaySinh = new JTextField();
        JTextField txtDiaChi = new JTextField();

        JComboBox<String> cboGioiTinh = new JComboBox<>(new String[] { "Nam", "Nữ" });
        JSpinner spinnerDiemTichLuy = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1));

        // Thêm components vào panel
        panel.add(new JLabel("Mã KH:*"));
        panel.add(txtMaKH);

        panel.add(new JLabel("Họ tên:*"));
        panel.add(txtHoTen);

        panel.add(new JLabel("Số điện thoại:*"));
        panel.add(txtSoDT);

        panel.add(new JLabel("Email:*"));
        panel.add(txtEmail);

        panel.add(new JLabel("CMND/CCCD:*"));
        panel.add(txtCMND);

        panel.add(new JLabel("Ngày sinh:"));
        panel.add(txtNgaySinh);

        panel.add(new JLabel("Giới tính:"));
        panel.add(cboGioiTinh);

        panel.add(new JLabel("Địa chỉ:"));
        panel.add(txtDiaChi);

        panel.add(new JLabel("Điểm tích lũy:"));
        panel.add(spinnerDiemTichLuy);

        // Panel hiển thị thông tin
        JPanel panelThongTin = new JPanel(new BorderLayout());
        panelThongTin.setBorder(BorderFactory.createTitledBorder("Thông tin khách hàng"));
        JTextArea txtThongTin = new JTextArea(4, 30);
        txtThongTin.setEditable(false);
        txtThongTin.setBackground(new Color(240, 240, 240));
        txtThongTin.setMargin(new Insets(10, 10, 10, 10));
        panelThongTin.add(new JScrollPane(txtThongTin), BorderLayout.CENTER);

        // Cập nhật thông tin khi nhập liệu
        javax.swing.event.DocumentListener updateInfoListener = new javax.swing.event.DocumentListener() {
            public void anyUpdate() {
                updateThongTin();
            }

            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                anyUpdate();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                anyUpdate();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                anyUpdate();
            }

            private void updateThongTin() {
                String maKH = txtMaKH.getText().trim();
                String hoTen = txtHoTen.getText().trim();
                String cmnd = txtCMND.getText().trim();
                String soDT = txtSoDT.getText().trim();
                String email = txtEmail.getText().trim();
                String gioiTinh = (String) cboGioiTinh.getSelectedItem();
                int diemTichLuy = (Integer) spinnerDiemTichLuy.getValue();

                StringBuilder info = new StringBuilder();

                if (!maKH.isEmpty()) {
                    info.append("Mã KH: ").append(maKH).append("\n");
                }
                if (!hoTen.isEmpty()) {
                    info.append("Họ tên: ").append(hoTen).append("\n");
                }
                if (!cmnd.isEmpty()) {
                    info.append("CMND: ").append(cmnd).append("\n");
                }
                if (!soDT.isEmpty()) {
                    info.append("Số ĐT: ").append(soDT).append("\n");
                }
                if (!email.isEmpty()) {
                    info.append("Email: ").append(email).append("\n");
                }
                if (gioiTinh != null && !gioiTinh.isEmpty()) {
                    info.append("Giới tính: ").append(gioiTinh).append("\n");
                }
                if (diemTichLuy > 0) {
                    info.append("Điểm tích lũy: ").append(diemTichLuy).append("\n");
                }

                if (info.length() == 0) {
                    txtThongTin.setText("Thông tin khách hàng sẽ hiển thị ở đây");
                } else {
                    txtThongTin.setText(info.toString());
                }
            }
        };

        txtMaKH.getDocument().addDocumentListener(updateInfoListener);
        txtHoTen.getDocument().addDocumentListener(updateInfoListener);
        txtCMND.getDocument().addDocumentListener(updateInfoListener);
        txtSoDT.getDocument().addDocumentListener(updateInfoListener);
        txtEmail.getDocument().addDocumentListener(updateInfoListener);

        ActionListener updateInfoActionListener = e -> {
            String maKH = txtMaKH.getText().trim();
            String hoTen = txtHoTen.getText().trim();
            String cmnd = txtCMND.getText().trim();
            String soDT = txtSoDT.getText().trim();
            String email = txtEmail.getText().trim();
            String gioiTinh = (String) cboGioiTinh.getSelectedItem();
            int diemTichLuy = (Integer) spinnerDiemTichLuy.getValue();

            StringBuilder info = new StringBuilder();

            if (!maKH.isEmpty())
                info.append("Mã KH: ").append(maKH).append("\n");
            if (!hoTen.isEmpty())
                info.append("Họ tên: ").append(hoTen).append("\n");
            if (!cmnd.isEmpty())
                info.append("CMND: ").append(cmnd).append("\n");
            if (!soDT.isEmpty())
                info.append("Số ĐT: ").append(soDT).append("\n");
            if (!email.isEmpty())
                info.append("Email: ").append(email).append("\n");
            if (gioiTinh != null && !gioiTinh.isEmpty())
                info.append("Giới tính: ").append(gioiTinh).append("\n");
            if (diemTichLuy > 0)
                info.append("Điểm tích lũy: ").append(diemTichLuy).append("\n");

            txtThongTin.setText(info.length() == 0 ? "Thông tin khách hàng sẽ hiển thị ở đây" : info.toString());
        };

        cboGioiTinh.addActionListener(updateInfoActionListener);
        spinnerDiemTichLuy.addChangeListener(e -> updateInfoActionListener.actionPerformed(null));

        // Panel button
        JPanel panelButton = new JPanel(new FlowLayout());
        JButton btnThem = new JButton("Thêm Khách Hàng");
        JButton btnHuy = new JButton("Hủy");

        btnThem.addActionListener(e -> {
            // Validate dữ liệu
            if (txtMaKH.getText().trim().isEmpty() ||
                    txtHoTen.getText().trim().isEmpty() ||
                    txtSoDT.getText().trim().isEmpty() ||
                    txtEmail.getText().trim().isEmpty() ||
                    txtCMND.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(dialog,
                        "Vui lòng nhập đầy đủ thông tin bắt buộc (*)",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // Lấy dữ liệu từ form
                String maKH = txtMaKH.getText().trim();
                String hoTen = txtHoTen.getText().trim();
                String soDT = txtSoDT.getText().trim();
                String email = txtEmail.getText().trim();
                String cmnd = txtCMND.getText().trim();
                String ngaySinhStr = txtNgaySinh.getText().trim();
                String gioiTinh = (String) cboGioiTinh.getSelectedItem();
                String diaChi = txtDiaChi.getText().trim();
                int diemTichLuy = (Integer) spinnerDiemTichLuy.getValue();

                // Parse ngày sinh
                java.util.Date ngaySinh = null;
                if (!ngaySinhStr.isEmpty()) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        sdf.setLenient(false);
                        ngaySinh = sdf.parse(ngaySinhStr);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog,
                                "Định dạng ngày sinh không hợp lệ. Vui lòng nhập theo định dạng dd/MM/yyyy",
                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                // Tạo đối tượng KhachHang
                KhachHang khachHang = new KhachHang(maKH, hoTen, soDT, email, cmnd, ngaySinh, gioiTinh, diaChi);
                khachHang.setDiemTichLuy(diemTichLuy);
                khachHang.setNgayDangKy(new java.util.Date());

                // Thêm vào danh sách
                boolean result = quanLy.getDsKhachHang().them(khachHang);

                if (result) {
                    JOptionPane.showMessageDialog(dialog,
                            "Thêm khách hàng thành công!\n\n" +
                                    "Mã KH: " + maKH + "\n" +
                                    "Họ tên: " + hoTen + "\n" +
                                    "CMND: " + cmnd + "\n" +
                                    "Số ĐT: " + soDT + "\n" +
                                    "Hạng: " + khachHang.getHangKhachHang(),
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);

                    // Đóng dialog và cập nhật giao diện
                    dialog.dispose();
                    capNhatDuLieuGUI();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Không thể thêm khách hàng!\n\n" +
                                    "Có thể mã KH hoặc CMND đã tồn tại trong hệ thống.",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Lỗi khi thêm khách hàng: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        btnHuy.addActionListener(e -> dialog.dispose());

        panelButton.add(btnThem);
        panelButton.add(btnHuy);

        // Thêm các panel vào dialog
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(panel, BorderLayout.NORTH);
        mainPanel.add(panelThongTin, BorderLayout.CENTER);
        mainPanel.add(panelButton, BorderLayout.SOUTH);

        dialog.add(mainPanel, BorderLayout.CENTER);

        // Set default button và hỗ trợ phím ESC
        dialog.getRootPane().setDefaultButton(btnThem);

        // Hiển thị dialog
        dialog.setVisible(true);
    }

    private void xemChiTietHoaDon() {
        int khachHang1 = tableChuyenBay.getSelectedRow();
        // if (khachHang1 == -1) {
        // JOptionPane.showMessageDialog(this,
        // "Vui lòng chọn một khách hàng để xem hóa đơn!",
        // "Thông báo", JOptionPane.WARNING_MESSAGE);
        // return;
        // }
        String maKH = (String) tableKhachHang.getValueAt(khachHang1, 0);
        KhachHang khachHang = quanLy.getDsKhachHang().timKiemTheoMa(maKH);

        JDialog dialog = new JDialog(this, "Chi Tiết Hóa Đơn - " + khachHang.getHoTen(), true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Lấy danh sách hóa đơn của khách hàng
        DanhSachHoaDon dsHoaDon = quanLy.getDsHoaDon();
        List<HoaDon> hoaDonCuaKH = dsHoaDon.timKiemTheoKhachHang(khachHang.getMaKH());

        if (hoaDonCuaKH.isEmpty()) {
            JOptionPane.showMessageDialog(dialog,
                    "Khách hàng " + khachHang.getHoTen() + " chưa có hóa đơn nào!",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
            return;
        }

        // Panel thông tin khách hàng
        JPanel panelThongTinKH = new JPanel(new GridLayout(0, 2, 10, 5));
        panelThongTinKH.setBorder(BorderFactory.createTitledBorder("Thông tin khách hàng"));
        panelThongTinKH.add(new JLabel("Mã KH:"));
        panelThongTinKH.add(new JLabel(khachHang.getMaKH()));
        panelThongTinKH.add(new JLabel("Họ tên:"));
        panelThongTinKH.add(new JLabel(khachHang.getHoTen()));
        panelThongTinKH.add(new JLabel("CMND:"));
        panelThongTinKH.add(new JLabel(khachHang.getCmnd()));
        panelThongTinKH.add(new JLabel("Số ĐT:"));
        panelThongTinKH.add(new JLabel(khachHang.getSoDT()));
        panelThongTinKH.add(new JLabel("Email:"));
        panelThongTinKH.add(new JLabel(khachHang.getEmail()));
        panelThongTinKH.add(new JLabel("Hạng:"));
        panelThongTinKH.add(new JLabel(khachHang.getHangKhachHang()));
        panelThongTinKH.add(new JLabel("Điểm tích lũy:"));
        panelThongTinKH.add(new JLabel(String.valueOf(khachHang.getDiemTichLuy())));

        // ComboBox chọn hóa đơn
        JPanel panelChonHoaDon = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelChonHoaDon.add(new JLabel("Chọn hóa đơn:"));
        JComboBox<String> cbHoaDon = new JComboBox<>();

        // Thêm các hóa đơn vào combobox
        for (HoaDon hd : hoaDonCuaKH) {
            String item = String.format("%s - %s - %,d VND - %s",
                    hd.getMaHoaDon(),
                    new SimpleDateFormat("dd/MM/yyyy HH:mm").format(hd.getNgayLap()),
                    (int) hd.getTongTien(),
                    hd.getTrangThai());
            cbHoaDon.addItem(item);
        }
        panelChonHoaDon.add(cbHoaDon);

        // Panel chi tiết hóa đơn
        JPanel panelChiTietHoaDon = new JPanel(new BorderLayout());
        panelChiTietHoaDon.setBorder(BorderFactory.createTitledBorder("Chi tiết hóa đơn"));

        // Table hiển thị vé máy bay
        String[] columnNames = { "Mã Vé", "Chuyến Bay", "Loại Vé", "Số Ghế", "Giá Vé", "Trạng Thái" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable tableVe = new JTable(tableModel);
        tableVe.setRowHeight(25);
        JScrollPane scrollPaneVe = new JScrollPane(tableVe);

        // Panel thông tin tổng hợp hóa đơn
        JPanel panelTongHop = new JPanel(new GridLayout(0, 2, 10, 5));
        panelTongHop.setBorder(BorderFactory.createTitledBorder("Thông tin tổng hợp"));

        JLabel lblMaHoaDon = new JLabel();
        JLabel lblNgayLap = new JLabel();
        JLabel lblTongTien = new JLabel();
        JLabel lblThueVAT = new JLabel();
        JLabel lblPhiDichVu = new JLabel();
        JLabel lblThanhTien = new JLabel();
        JLabel lblTrangThai = new JLabel();
        JLabel lblSoLuongVe = new JLabel();

        panelTongHop.add(new JLabel("Mã hóa đơn:"));
        panelTongHop.add(lblMaHoaDon);
        panelTongHop.add(new JLabel("Ngày lập:"));
        panelTongHop.add(lblNgayLap);
        panelTongHop.add(new JLabel("Tổng tiền:"));
        panelTongHop.add(lblTongTien);
        panelTongHop.add(new JLabel("Thuế VAT:"));
        panelTongHop.add(lblThueVAT);
        panelTongHop.add(new JLabel("Phí dịch vụ:"));
        panelTongHop.add(lblPhiDichVu);
        panelTongHop.add(new JLabel("Thành tiền:"));
        panelTongHop.add(lblThanhTien);
        panelTongHop.add(new JLabel("Số lượng vé:"));
        panelTongHop.add(lblSoLuongVe);
        panelTongHop.add(new JLabel("Trạng thái:"));
        panelTongHop.add(lblTrangThai);

        // Panel button
        JPanel panelButton = new JPanel(new FlowLayout());
        JButton btnInHoaDon = new JButton("In Hóa Đơn");
        JButton btnDong = new JButton("Đóng");

        // Method cập nhật chi tiết hóa đơn khi chọn
        ActionListener updateChiTietHoaDon = e -> {
            int selectedIndex = cbHoaDon.getSelectedIndex();
            if (selectedIndex >= 0) {
                HoaDon hoaDon = hoaDonCuaKH.get(selectedIndex);

                // Cập nhật thông tin tổng hợp
                lblMaHoaDon.setText(hoaDon.getMaHoaDon());
                lblNgayLap.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(hoaDon.getNgayLap()));
                lblTongTien.setText(String.format("%,d VND", (int) hoaDon.getTongTien()));
                lblThueVAT.setText(String.format("%,d VND", (int) hoaDon.getThue()));
                lblThanhTien.setText(String.format("%,d VND", (int) (hoaDon.getTongTien() + hoaDon.getThue())));
                lblTrangThai.setText(hoaDon.getTrangThai());

                // Lấy danh sách vé từ hóa đơn
                DanhSachVeMayBay dsVe = quanLy.getDsVe();
                List<VeMayBay> veTrongHoaDon = dsVe.timKiemTheoMaHoaDon(hoaDon.getMaHoaDon());

                lblSoLuongVe.setText(String.valueOf(veTrongHoaDon.size()));

                // Cập nhật table vé
                tableModel.setRowCount(0);
                for (VeMayBay ve : veTrongHoaDon) {
                    // Lấy thông tin chuyến bay
                    ChuyenBay chuyenBay = quanLy.getDsChuyenBay().timKiemTheoMa(ve.getMaChuyen());
                    String tenChuyenBay = chuyenBay != null
                            ? String.format("%s → %s", chuyenBay.getDiemDi(), chuyenBay.getDiemDen())
                            : "N/A";

                    // Xác định loại vé
                    String loaiVe = ve instanceof VeThuongGia ? "Thương gia"
                            : ve instanceof VePhoThong ? "Phổ thông" : "Tiết kiệm";

                    tableModel.addRow(new Object[] {
                            ve.getMaVe(),
                            tenChuyenBay,
                            loaiVe,
                            ve.getSoGhe(),
                            String.format("%,d VND", (int) ve.getGiaVe()),
                            ve.getTrangThai()
                    });
                }
            }
        };

        cbHoaDon.addActionListener(updateChiTietHoaDon);

        // Hiển thị chi tiết hóa đơn đầu tiên
        if (!hoaDonCuaKH.isEmpty()) {
            updateChiTietHoaDon.actionPerformed(null);
        }

        // Xử lý nút in hóa đơn
        btnInHoaDon.addActionListener(e -> {
            int selectedIndex = cbHoaDon.getSelectedIndex();
            if (selectedIndex >= 0) {
                HoaDon hoaDon = hoaDonCuaKH.get(selectedIndex);
                inHoaDon(hoaDon, khachHang);
            }
        });

        btnDong.addActionListener(e -> dialog.dispose());

        panelButton.add(btnInHoaDon);
        panelButton.add(btnDong);

        // Sắp xếp layout
        JPanel panelNorth = new JPanel(new BorderLayout());
        panelNorth.add(panelThongTinKH, BorderLayout.NORTH);
        panelNorth.add(panelChonHoaDon, BorderLayout.SOUTH);

        JPanel panelCenter = new JPanel(new BorderLayout());
        panelCenter.add(panelTongHop, BorderLayout.NORTH);
        panelCenter.add(scrollPaneVe, BorderLayout.CENTER);

        JPanel panelMain = new JPanel(new BorderLayout());
        panelMain.add(panelNorth, BorderLayout.NORTH);
        panelMain.add(panelChiTietHoaDon, BorderLayout.CENTER);
        panelChiTietHoaDon.add(panelCenter, BorderLayout.CENTER);

        dialog.add(panelMain, BorderLayout.CENTER);
        dialog.add(panelButton, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    // Method in hóa đơn (có thể phát triển thêm)
    private void inHoaDon(HoaDon hoaDon, KhachHang khachHang) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== HÓA ĐƠN BÁN VÉ MÁY BAY ===\n\n");
        sb.append("Mã hóa đơn: ").append(hoaDon.getMaHoaDon()).append("\n");
        sb.append("Ngày lập: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(hoaDon.getNgayLap()))
                .append("\n");
        sb.append("Khách hàng: ").append(khachHang.getHoTen()).append("\n");
        sb.append("CMND: ").append(khachHang.getCmnd()).append("\n");
        sb.append("Số ĐT: ").append(khachHang.getSoDT()).append("\n\n");

        // Lấy danh sách vé
        DanhSachVeMayBay dsVe = quanLy.getDsVe();
        List<VeMayBay> veTrongHoaDon = dsVe.timKiemTheoMaHoaDon(hoaDon.getMaHoaDon());

        sb.append("Chi tiết vé:\n");
        for (VeMayBay ve : veTrongHoaDon) {
            ChuyenBay chuyenBay = quanLy.getDsChuyenBay().timKiemTheoMa(ve.getMaChuyen());
            String loaiVe = ve instanceof VeThuongGia ? "Thương gia" : "Phổ thông";

            sb.append(String.format(" - %s | %s → %s | %s | %,d VND\n",
                    ve.getMaVe(),
                    chuyenBay.getDiemDi(), chuyenBay.getDiemDen(),
                    loaiVe,
                    (int) ve.getGiaVe()));
        }

        sb.append("\nTỔNG HỢP:\n");
        sb.append(String.format("Tổng tiền: %,d VND\n", (int) hoaDon.getTongTien()));
        sb.append(String.format("Thuế VAT: %,d VND\n", (int) hoaDon.getThue()));
        sb.append(String.format("THÀNH TIỀN: %,d VND\n",
                (int) (hoaDon.getTongTien() + hoaDon.getThue())));
        sb.append("\nTrạng thái: ").append(hoaDon.getTrangThai());
        sb.append("\n\nCảm ơn quý khách!");

        JTextArea textArea = new JTextArea(sb.toString(), 20, 50);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        JOptionPane.showMessageDialog(this, scrollPane, "Hóa Đơn " + hoaDon.getMaHoaDon(),
                JOptionPane.INFORMATION_MESSAGE);
    }

    // Method gọi từ table khách hàng (khi double click hoặc chọn nút xem chi tiết)
    private void xemChiTietKhachHang() {
        // Giả sử bạn có table khách hàng
        int selectedRow = tableKhachHang.getSelectedRow();
        if (selectedRow >= 0) {
            String maKH = (String) tableKhachHang.getValueAt(selectedRow, 0); // Cột mã KH
            KhachHang khachHang = quanLy.getDsKhachHang().timKiemTheoMa(maKH);
            if (khachHang != null) {
                xemChiTietHoaDon();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một khách hàng!", "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    public void onTabChanged(String tabName, int selectedIndex) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onTabChanged'");
    }
}
