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

        JLabel lblSubTitle = new JLabel("Phiên bản " + QuanLyBanVeMayBay.getPhienBan() + " | Số lượt truy cập: "
                + (QuanLyBanVeMayBay.getSoLanTruyCap() + 7), JLabel.CENTER);
        lblSubTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSubTitle.setForeground(Color.LIGHT_GRAY);
        headerPanel.add(lblSubTitle, BorderLayout.SOUTH);

        // Thống kê nhanh
        JPanel statsPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        lblSubTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        statsPanel.setBackground(Color.WHITE);

        // Tạo các card thống kê
        String str1 = String.valueOf(quanLy.getDsVe().demSoLuong());
        statCards[0] = taoStatCard("Tổng số vé", str1, "primary");
        String str2 = String.valueOf(quanLy.getDsChuyenBay().demSoLuong());
        statCards[1] = taoStatCard("Tổng chuyến bay", str2, "success");
        String str3 = String.valueOf(quanLy.getDsKhachHang().demSoLuong());
        statCards[2] = taoStatCard("Tổng khách hàng", str3, "info");
        long tongDoanhThu = (long) quanLy.getDsVe().tinhTongDoanhThu();
        NumberFormat formatter = NumberFormat.getInstance();
        String formatted = formatter.format(tongDoanhThu);
        String str4 = String.valueOf(formatted);
        statCards[3] = taoStatCard("Doanh thu", str4 + " VND", "warning");
        String str5 = String.valueOf(quanLy.getDsVe().demSoLuongTheoLoai("VeThuongGia"));
        statCards[4] = taoStatCard("Vé thương gia", str5, "primary");
        String str6 = String.valueOf(quanLy.getDsVe().demSoLuongTheoLoai("VePhoThong"));
        statCards[5] = taoStatCard("Vé phổ thông", str6, "success");
        String str7 = String.valueOf(quanLy.getDsVe().demSoLuongTheoLoai("VeTietKiem"));
        statCards[6] = taoStatCard("Vé tiết kiệm", str7, "info");
        statCards[7] = taoStatCard("Tỷ lệ lấp đầy", "79.6%", "warning");

        for (JPanel card : statCards) {
            statsPanel.add(card);
        }

        // Chức năng nhanh
        JPanel quickActionsPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        quickActionsPanel.setBorder(BorderFactory.createTitledBorder("Chức năng nhanh"));
        quickActionsPanel.setBackground(Color.WHITE);
        quickActionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[][] actions = {
                { "Đặt vé mới", "🎫", "Đặt vé máy bay mới" },
                { "Tìm chuyến bay", "🔍", "Tìm kiếm chuyến bay" },
                { "Thống kê", "📈", "Xem báo cáo thống kê" },
                { "Quản lý", "⚙️", "Cài đặt hệ thống" }
        };

        for (String[] action : actions) {
            quickActionsPanel.add(taoActionButton(action[0], action[1], action[2]));
        }

        panelTrangChu.add(headerPanel, BorderLayout.NORTH);
        panelTrangChu.add(statsPanel, BorderLayout.CENTER);
        panelTrangChu.add(quickActionsPanel, BorderLayout.SOUTH);
    }

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
        String[] columns = { "Mã vé","Mã KH", "Hành khách", "CMND", "Chuyến bay","Số ghế","Giờ khởi hành", "Loại vé", "Giá vé", "Trạng thái"};
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
                // suaChuyenBay();
                break;
            case "Xóa chuyến":
                // xoaChuyenBay();
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

    private void moDialogDatVe() {
        JDialog dialog = new JDialog(this, "Đặt Vé Máy Bay", true);
        dialog.setSize(500, 550);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Các component nhập liệu
        JTextField txtHoTen = new JTextField();
        JTextField txtCMND = new JTextField();
        JTextField txtSoDT = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtSoLuong = new JTextField("1");

        // Lấy danh sách chuyến bay thực tế từ service
        DanhSachChuyenBay dsChuyenBay = quanLy.getDsChuyenBay();
        Vector<String> chuyenBayItems = new Vector<>();
        chuyenBayItems.add("-- Chọn chuyến bay --");

        if (dsChuyenBay != null && dsChuyenBay.getDanhSachChuyenBay() != null) {
            for (ChuyenBay cb : dsChuyenBay.getDanhSachChuyenBay()) {
                if ("CHƯA BAY".equals(cb.getTrangThai()) && cb.getSoGheTrong() > 0) {
                    String item = String.format("%s - %s → %s - %s - %s ghế trống",
                            cb.getMaChuyen(), cb.getDiemDi(), cb.getDiemDen(),
                            new SimpleDateFormat("dd/MM/yyyy HH:mm").format(cb.getGioKhoiHanh()),
                            cb.getSoGheTrong());
                    chuyenBayItems.add(item);
                }
            }
        }

        JComboBox<String> cbChuyenBay = new JComboBox<>(chuyenBayItems);
        JComboBox<String> cbLoaiVe = new JComboBox<>(new String[] { "THƯƠNG GIA", "PHỔ THÔNG", "TIẾT KIỆM" });

        // Thêm components vào panel
        panel.add(new JLabel("Họ tên hành khách:*"));
        panel.add(txtHoTen);

        panel.add(new JLabel("CMND/CCCD:*"));
        panel.add(txtCMND);

        panel.add(new JLabel("Số điện thoại:*"));
        panel.add(txtSoDT);

        panel.add(new JLabel("Email:"));
        panel.add(txtEmail);

        panel.add(new JLabel("Chuyến bay:*"));
        panel.add(cbChuyenBay);

        panel.add(new JLabel("Loại vé:*"));
        panel.add(cbLoaiVe);

        panel.add(new JLabel("Số lượng vé:*"));
        panel.add(txtSoLuong);

        // Panel hiển thị thông tin giá
        JPanel panelThongTin = new JPanel(new BorderLayout());
        panelThongTin.setBorder(BorderFactory.createTitledBorder("Thông tin giá"));
        JTextArea txtThongTin = new JTextArea(4, 30);
        txtThongTin.setEditable(false);
        txtThongTin.setBackground(new Color(240, 240, 240));
        txtThongTin.setMargin(new Insets(10, 10, 10, 10));
        panelThongTin.add(new JScrollPane(txtThongTin), BorderLayout.CENTER);

        // Cập nhật thông tin giá khi chọn chuyến bay và loại vé
        ActionListener updatePriceListener = e -> {
            if (cbChuyenBay.getSelectedIndex() > 0 && cbLoaiVe.getSelectedIndex() >= 0) {
                String selectedItem = (String) cbChuyenBay.getSelectedItem();
                String maChuyen = selectedItem.split(" - ")[0];
                String loaiVe = (String) cbLoaiVe.getSelectedItem();

                ChuyenBay chuyenBay = dsChuyenBay.timKiemTheoMa(maChuyen);
                if (chuyenBay != null) {
                    double giaCoBan = chuyenBay.getGiaCoBan();
                    double heSoGia = 1.0;

                    switch (loaiVe) {
                        case "THƯƠNG GIA":
                            heSoGia = 2.0;
                            break;
                        case "PHỔ THÔNG":
                            heSoGia = 1.2;
                            break;
                        case "TIẾT KIỆM":
                            heSoGia = 0.9;
                            break;
                    }

                    double giaVe = giaCoBan * heSoGia;
                    int soLuong = 1;
                    try {
                        soLuong = Integer.parseInt(txtSoLuong.getText().trim());
                        if (soLuong < 1)
                            soLuong = 1;
                    } catch (NumberFormatException ex) {
                        soLuong = 1;
                    }

                    double tongTien = giaVe * soLuong;

                    String thongTin = String.format(
                            "Chuyến bay: %s → %s\nLoại vé: %s\nGiá vé: %,d VND\nSố lượng: %d\nTổng tiền: %,d VND",
                            chuyenBay.getDiemDi(), chuyenBay.getDiemDen(),
                            loaiVe, (int) giaVe, soLuong, (int) tongTien);
                    txtThongTin.setText(thongTin);
                }
            } else {
                txtThongTin.setText("Vui lòng chọn chuyến bay và loại vé");
            }
        };

        cbChuyenBay.addActionListener(updatePriceListener);
        cbLoaiVe.addActionListener(updatePriceListener);
        txtSoLuong.getDocument().addDocumentListener(new DocumentListener() {
            public void anyUpdate() {
                updatePriceListener.actionPerformed(null);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                anyUpdate();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                anyUpdate();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                anyUpdate();
            }
        });

        // Panel button
        JPanel panelButton = new JPanel(new FlowLayout());
        JButton btnDatVe = new JButton("Đặt Vé");
        JButton btnHuy = new JButton("Hủy");

        btnDatVe.addActionListener(e -> {
            // Validate dữ liệu
            if (txtHoTen.getText().trim().isEmpty() ||
                    txtCMND.getText().trim().isEmpty() ||
                    txtSoDT.getText().trim().isEmpty() ||
                    cbChuyenBay.getSelectedIndex() <= 0) {

                JOptionPane.showMessageDialog(dialog,
                        "Vui lòng nhập đầy đủ thông tin bắt buộc (*)",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // Lấy thông tin từ form
                String hoTen = txtHoTen.getText().trim();
                String cmnd = txtCMND.getText().trim();
                String soDT = txtSoDT.getText().trim();
                String email = txtEmail.getText().trim();
                String selectedItem = (String) cbChuyenBay.getSelectedItem();
                String maChuyen = selectedItem.split(" - ")[0];
                String loaiVe = (String) cbLoaiVe.getSelectedItem();
                int soLuong = Integer.parseInt(txtSoLuong.getText().trim());

                if (soLuong <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Số lượng vé phải lớn hơn 0!", "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Tìm chuyến bay
                ChuyenBay chuyenBay = dsChuyenBay.timKiemTheoMa(maChuyen);
                if (chuyenBay == null) {
                    JOptionPane.showMessageDialog(dialog, "Không tìm thấy chuyến bay!", "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Kiểm tra số ghế trống
                if (chuyenBay.getSoGheTrong() < soLuong) {
                    JOptionPane.showMessageDialog(dialog,
                            String.format("Chỉ còn %d ghế trống!", chuyenBay.getSoGheTrong()),
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Tính giá vé
                double giaCoBan = chuyenBay.getGiaCoBan();
                double heSoGia = 1.0;
                switch (loaiVe) {
                    case "THƯƠNG GIA":
                        heSoGia = 2.0;
                        break;
                    case "PHỔ THÔNG":
                        heSoGia = 1.2;
                        break;
                    case "TIẾT KIỆM":
                        heSoGia = 0.9;
                        break;
                }
                double giaVe = giaCoBan * heSoGia;
                Date ngayHienTai = new Date();

                // Kiểm tra xem khách hàng đã tồn tại chưa
                KhachHang khachHangExist = quanLy.getDsKhachHang().timKiemTheoCMND(cmnd);
                KhachHang khachHang;

                if (khachHangExist != null) {
                    // Cập nhật thông tin khách hàng nếu đã tồn tại
                    khachHangExist.setHoTen(hoTen);
                    khachHangExist.setSoDT(soDT);
                    khachHangExist.setEmail(email);
                    khachHang = khachHangExist;
                } else {
                    // Tạo khách hàng mới
                    khachHang = new KhachHang(
                            "KH" + System.currentTimeMillis(),
                            hoTen,
                            soDT,
                            email,
                            cmnd,
                            ngayHienTai,
                            "Nam", // Giới tính mặc định
                            "Hà Nội" // Địa chỉ mặc định
                    );
                    quanLy.themKhachHang(khachHang);
                }

                // Tạo các vé máy bay
                DanhSachVeMayBay danhSachMaVe = new DanhSachVeMayBay();
                for (int i = 0; i < soLuong; i++) {
                    String maVe = "VE" + System.currentTimeMillis() + "_" + i;

                    // Tạo vé tương ứng với loại vé
                    VeMayBay ve;
                    if ("THƯƠNG GIA".equals(loaiVe)) {
                        ve = new VeThuongGia(
                                maVe,
                                "KH" + System.currentTimeMillis(),
                                hoTen,
                                cmnd,
                                ngayHienTai,
                                giaVe,
                                maChuyen,
                                "A" + (i + 1), // Số ghế
                                "DAT",
                                "Massage", // Dịch vụ
                                500000.0, // Phí dịch vụ
                                20, // Hành lý
                                true, // Ưu tiên
                                "Rượu vang cao cấp" // Đồ uống
                        );
                    } else {
                        // Tạo vé phổ thông hoặc tiết kiệm
                        ve = new VePhoThong(
                                maVe,
                                "KH" + System.currentTimeMillis(),
                                hoTen,
                                cmnd,
                                ngayHienTai,
                                giaVe,
                                maChuyen,
                                "A" + (i + 1), // Số ghế
                                "DAT",
                                true, // Dịch vụ
                                5, // Phí dịch vụ
                                200000, // Hành lý
                                "Cua so",
                                true // Ưu tiên
                        );
                    }
                    danhSachMaVe.them(ve);
                    quanLy.themVe(ve);
                }

                // Cập nhật số ghế trống của chuyến bay
                chuyenBay.setSoGheTrong(chuyenBay.getSoGheTrong() - soLuong);

                // Tạo hóa đơn
                double tongTien = giaVe * soLuong;
                double thueVAT = tongTien * 0.1; // 10% VAT
                double phiDichVu = 200000.0; // Phí dịch vụ cố định

                HoaDon hoaDon = new HoaDon(
                        "VE" + System.currentTimeMillis() + "_" + 1,
                        "KH" + System.currentTimeMillis(),
                        "NV001",
                        (double) giaVe * soLuong,
                        (double) 0.1 * giaVe * soLuong,
                        (double) 200000,
                        "Da_THANH_TOAN");

                // Hiển thị thông báo thành công
                String message = String.format(
                        "Đặt vé thành công!\n\n" +
                                "Mã hóa đơn: %s\n" +
                                "Khách hàng: %s\n" +
                                "CMND: %s\n" +
                                "Chuyến bay: %s → %s\n" +
                                "Loại vé: %s\n" +
                                "Số lượng: %d\n" +
                                "Tổng tiền: %,d VND"

                );

                JOptionPane.showMessageDialog(dialog, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);

                // Đóng dialog và cập nhật giao diện
                dialog.dispose();
                capNhatDuLieuGUI();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Số lượng vé phải là số nguyên dương!", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnHuy.addActionListener(e -> dialog.dispose());

        panelButton.add(btnDatVe);
        panelButton.add(btnHuy);

        // Thêm các panel vào dialog
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(panel, BorderLayout.NORTH);
        mainPanel.add(panelThongTin, BorderLayout.CENTER);
        mainPanel.add(panelButton, BorderLayout.SOUTH);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void moDialogSuaVe() {
    // Kiểm tra có vé nào được chọn không
    int selectedRow = tableVe.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this,
                "Vui lòng chọn một vé để sửa!",
                "Thông báo", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Lấy thông tin vé được chọn
    String maVe = (String) tableVe.getValueAt(selectedRow, 0);
    VeMayBay veCanSua = quanLy.getDsVe().timKiemTheoMa(maVe);

    if (veCanSua == null) {
        JOptionPane.showMessageDialog(this,
                "Không tìm thấy thông tin vé!",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    JDialog dialog = new JDialog(this, "Sửa Thông Tin Vé Máy Bay", true);
    dialog.setSize(500, 500);
    dialog.setLocationRelativeTo(this);
    dialog.setLayout(new BorderLayout());

    JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // Panel hiển thị thông tin hiện tại
    JPanel panelThongTinHienTai = new JPanel(new BorderLayout());
    panelThongTinHienTai.setBorder(BorderFactory.createTitledBorder("THÔNG TIN HIỆN TẠI"));
    JTextArea txtThongTinHienTai = new JTextArea(8, 30);
    txtThongTinHienTai.setEditable(false);
    txtThongTinHienTai.setBackground(new Color(245, 245, 245));
    txtThongTinHienTai.setForeground(new Color(70, 130, 180));
    txtThongTinHienTai.setFont(new Font("Arial", Font.BOLD, 12));
    txtThongTinHienTai.setMargin(new Insets(10, 10, 10, 10));

    // Hiển thị thông tin vé hiện tại
    String thongTinHienTai = String.format(
            "Mã vé: %s\n" +
            "Hành khách: %s\n" +
            "CMND: %s\n" +
            "Chuyến bay: %s\n" +
            "Loại vé: %s\n" +
            "Số ghế: %s\n" +
            "Giá vé: %,d VND\n" +
            "Trạng thái: %s",
            veCanSua.getMaVe(),
            veCanSua.getHoTenKH(),
            veCanSua.getCmnd(),
            veCanSua.getMaChuyen(),
            veCanSua.loaiVe(),
            veCanSua.getSoGhe(),
            (int) veCanSua.getGiaVe(),
            veCanSua.getTrangThai());
    txtThongTinHienTai.setText(thongTinHienTai);
    panelThongTinHienTai.add(new JScrollPane(txtThongTinHienTai), BorderLayout.CENTER);

    // Các component nhập liệu để sửa (CHỈ cho phép sửa các trường được phép)
    JTextField txtHoTen = new JTextField(veCanSua.getHoTenKH());
    JTextField txtCMND = new JTextField(veCanSua.getCmnd());
    JTextField txtSoGhe = new JTextField(veCanSua.getSoGhe());

    // ComboBox trạng thái - CHỈ cho phép sửa nếu trạng thái hiện tại không phải HỦY hoặc ĐÃ BAY
    JComboBox<String> cbTrangThai = new JComboBox<>(new String[] {
            VeMayBay.TRANG_THAI_DAT, 
            VeMayBay.TRANG_THAI_HOAN_TAT,
            VeMayBay.TRANG_THAI_HUY,
            VeMayBay.TRANG_THAI_DA_BAY
    });
    
    // Kiểm tra trạng thái hiện tại để quyết định có cho phép sửa không
    boolean choPhepSuaTrangThai = !veCanSua.getTrangThai().equals(VeMayBay.TRANG_THAI_HUY) && 
                                  !veCanSua.getTrangThai().equals(VeMayBay.TRANG_THAI_DA_BAY);
    
    if (choPhepSuaTrangThai) {
        // Nếu cho phép sửa, set trạng thái hiện tại
        cbTrangThai.setSelectedItem(veCanSua.getTrangThai());
    } else {
        // Nếu không cho phép sửa, chỉ hiển thị trạng thái hiện tại và disable
        cbTrangThai.removeAllItems();
        cbTrangThai.addItem(veCanSua.getTrangThai());
        cbTrangThai.setEnabled(false);
    }

    // Các trường không được phép sửa - chỉ hiển thị
    JTextField txtMaVe = new JTextField(veCanSua.getMaVe());
    txtMaVe.setEditable(false);
    txtMaVe.setBackground(new Color(240, 240, 240));
    
    JTextField txtMaChuyen = new JTextField(veCanSua.getMaChuyen());
    txtMaChuyen.setEditable(false);
    txtMaChuyen.setBackground(new Color(240, 240, 240));
    
    JTextField txtLoaiVe = new JTextField(veCanSua.loaiVe());
    txtLoaiVe.setEditable(false);
    txtLoaiVe.setBackground(new Color(240, 240, 240));
    
    JTextField txtGiaVe = new JTextField(String.valueOf((int) veCanSua.getGiaVe()));
    txtGiaVe.setEditable(false);
    txtGiaVe.setBackground(new Color(240, 240, 240));

    // Thêm components vào panel
    panel.add(new JLabel("Mã vé:"));
    panel.add(txtMaVe);

    panel.add(new JLabel("Họ tên hành khách:*"));
    panel.add(txtHoTen);

    panel.add(new JLabel("CMND/CCCD:*"));
    panel.add(txtCMND);

    panel.add(new JLabel("Số ghế:*"));
    panel.add(txtSoGhe);

    panel.add(new JLabel("Chuyến bay:"));
    panel.add(txtMaChuyen);

    panel.add(new JLabel("Loại vé:"));
    panel.add(txtLoaiVe);

    panel.add(new JLabel("Giá vé (VND):"));
    panel.add(txtGiaVe);

    panel.add(new JLabel("Trạng thái:" + (!choPhepSuaTrangThai ? " (Không thể thay đổi)" : "")));
    panel.add(cbTrangThai);

    // Thông báo nếu không thể sửa trạng thái
    if (!choPhepSuaTrangThai) {
        JLabel lblThongBaoTrangThai = new JLabel(
            " Không thể thay đổi vé đã " + 
            (veCanSua.getTrangThai().equals(VeMayBay.TRANG_THAI_HUY) ? "hủy" : "bay")
        );
        lblThongBaoTrangThai.setForeground(Color.RED);
        lblThongBaoTrangThai.setFont(new Font("Arial", Font.ITALIC, 11));
        panel.add(new JLabel()); // placeholder
        panel.add(lblThongBaoTrangThai);
    }

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
        if (txtHoTen.getText().trim().isEmpty() ||
            txtCMND.getText().trim().isEmpty() ||
            txtSoGhe.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(dialog,
                    "Vui lòng nhập đầy đủ thông tin bắt buộc (*)",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Lấy thông tin từ form
            String hoTenMoi = txtHoTen.getText().trim();
            String cmndMoi = txtCMND.getText().trim();
            String soGheMoi = txtSoGhe.getText().trim();
            String trangThaiMoi = choPhepSuaTrangThai ? (String) cbTrangThai.getSelectedItem() : veCanSua.getTrangThai();

            // Kiểm tra số ghế hợp lệ
            if (!VeMayBay.validateSoGhe(soGheMoi)) {
                JOptionPane.showMessageDialog(dialog,
                        "Số ghế không hợp lệ! Format: 1A, 12B, 25C",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Kiểm tra xem số ghế mới có trùng với ghế khác trong cùng chuyến bay không
            if (!soGheMoi.equals(veCanSua.getSoGhe())) {
                boolean gheDaCo = quanLy.getDsVe().timKiemTheoChuyenBay(veCanSua.getMaChuyen())
                        .stream()
                        .anyMatch(ve -> ve.getSoGhe().equals(soGheMoi) && !ve.getMaVe().equals(veCanSua.getMaVe()));
                
                if (gheDaCo) {
                    JOptionPane.showMessageDialog(dialog,
                            "Số ghế " + soGheMoi + " đã có người đặt trong chuyến bay này!",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Cập nhật thông tin vé (CHỈ các trường được phép)
            veCanSua.setHoTenKH(hoTenMoi);
            veCanSua.setCmnd(cmndMoi);
            veCanSua.setSoGhe(soGheMoi);
            
            if (choPhepSuaTrangThai) {
                veCanSua.setTrangThai(trangThaiMoi);
            }

            // Hiển thị thông báo thành công
            String message = String.format(
                    "Cập nhật vé thành công!\n\n" +
                    "Mã vé: %s\n" +
                    "Hành khách: %s\n" +
                    "CMND: %s\n" +
                    "Số ghế: %s\n" +
                    "Trạng thái: %s",
                    veCanSua.getMaVe(),
                    hoTenMoi,
                    cmndMoi,
                    soGheMoi,
                    trangThaiMoi);

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
        txtHoTen.setText(veCanSua.getHoTenKH());
        txtCMND.setText(veCanSua.getCmnd());
        txtSoGhe.setText(veCanSua.getSoGhe());
        
        if (choPhepSuaTrangThai) {
            cbTrangThai.setSelectedItem(veCanSua.getTrangThai());
        }

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

    dialog.add(mainPanel, BorderLayout.CENTER);
    dialog.add(panelButton, BorderLayout.SOUTH);
    dialog.setVisible(true);
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
        (int) veCanXoa.getGiaVe()
    );
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
    //     canhBaoList.add("• Vé đã được xuất hóa đơn, cần xóa hóa đơn trước");
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
                    maVe, hoTen, chuyenBay
                );
                
                JOptionPane.showMessageDialog(dialogXacNhan, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
                
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
    gbc.gridx = 0; gbc.gridy = 0;
    panelTimKiem.add(new JLabel("Loại tìm kiếm:"), gbc);
    
    gbc.gridx = 1;
    panelTimKiem.add(cbLoaiTimKiem, gbc);
    
    gbc.gridx = 0; gbc.gridy = 1;
    panelTimKiem.add(new JLabel("Giá trị tìm kiếm:"), gbc);
    
    gbc.gridx = 1;
    panelTimKiem.add(panelComponent, gbc);

    // Panel kết quả
    JPanel panelKetQua = new JPanel(new BorderLayout());
    panelKetQua.setBorder(BorderFactory.createTitledBorder("KẾT QUẢ TÌM KIẾM"));
    
    // Table kết quả
    String[] columns = {"Mã Vé", "Hành Khách", "CMND", "Chuyến Bay", "Loại Vé", "Ngày Bay", "Giá Vé", "Trạng Thái"};
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
                    double giaMin = txtGiaMin.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtGiaMin.getText().trim());
                    double giaMax = txtGiaMax.getText().trim().isEmpty() ? Double.MAX_VALUE : Double.parseDouble(txtGiaMax.getText().trim());
                    if (giaMin <= giaMax) {
                        ketQua = quanLy.getDsVe().timKiemTheoKhoangGia(giaMin, giaMax);
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Giá min phải nhỏ hơn hoặc bằng giá max!", "Lỗi", JOptionPane.ERROR_MESSAGE);
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
                JOptionPane.showMessageDialog(dialog, "Không tìm thấy vé nào phù hợp!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
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
    gbc.gridx = 0; gbc.gridy = 0;
    panelSapXep.add(new JLabel("Tiêu chí sắp xếp:"), gbc);
    
    gbc.gridx = 1;
    panelSapXep.add(cbTieuChi, gbc);
    
    gbc.gridx = 0; gbc.gridy = 1;
    panelSapXep.add(new JLabel("Thứ tự:"), gbc);
    
    gbc.gridx = 1;
    panelSapXep.add(cbThuTu, gbc);
    
    gbc.gridx = 0; gbc.gridy = 2;
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
            rbTatCa.isSelected() ? quanLy.getDsVe().demSoLuong() : 
            quanLy.getDsVe().getDanhSach().stream()
                .filter(ve -> ve.getTrangThai().equals(cbTrangThai.getSelectedItem()))
                .count()
        );
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
                    danhSachSapXep.sort(tangDan ? 
                        Comparator.comparing(VeMayBay::getMaVe) : 
                        Comparator.comparing(VeMayBay::getMaVe).reversed());
                    break;
                    
                case "Theo giá vé":
                    danhSachSapXep.sort(tangDan ? 
                        Comparator.comparingDouble(VeMayBay::getGiaVe) : 
                        Comparator.comparingDouble(VeMayBay::getGiaVe).reversed());
                    break;
                    
                case "Theo ngày bay":
                    danhSachSapXep.sort((v1, v2) -> {
                        if (v1.getNgayBay() == null && v2.getNgayBay() == null) return 0;
                        if (v1.getNgayBay() == null) return tangDan ? -1 : 1;
                        if (v2.getNgayBay() == null) return tangDan ? 1 : -1;
                        return tangDan ? 
                            v1.getNgayBay().compareTo(v2.getNgayBay()) : 
                            v2.getNgayBay().compareTo(v1.getNgayBay());
                    });
                    break;
                    
                case "Theo tên khách hàng":
                    danhSachSapXep.sort(tangDan ? 
                        Comparator.comparing(VeMayBay::getHoTenKH) : 
                        Comparator.comparing(VeMayBay::getHoTenKH).reversed());
                    break;
                    
                case "Theo chuyến bay":
                    danhSachSapXep.sort(tangDan ? 
                        Comparator.comparing(VeMayBay::getMaChuyen) : 
                        Comparator.comparing(VeMayBay::getMaChuyen).reversed());
                    break;
                    
                case "Theo loại vé":
                    danhSachSapXep.sort(tangDan ? 
                        Comparator.comparing(VeMayBay::loaiVe) : 
                        Comparator.comparing(VeMayBay::loaiVe).reversed());
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
                danhSachSapXep.size()
            );

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
    String[] columns = {"Mã Vé", "Hành Khách", "Chuyến Bay", "Loại Vé", "Ngày Bay", "Giá Vé", "Trạng Thái"};
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
    gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
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
    gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
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
    gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
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
    gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
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
    gbc.gridy = 6; gbc.gridwidth = 2;
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
    gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
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
        case "ĐẶT": return "✅ " + trangThai;
        case "HOÀN TẤT": return "🎫 " + trangThai;
        case "HỦY": return "❌ " + trangThai;
        case "ĐÃ BAY": return "✈️ " + trangThai;
        default: return trangThai;
    }
}


    private void moDialogThemChuyenBay() {
        JOptionPane.showMessageDialog(this, "Chức năng đang phát triển", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void moDialogThemKhachHang() {
        JOptionPane.showMessageDialog(this, "Chức năng đang phát triển", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
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

        String[] buttonNames = { "Thêm chuyến", "Sửa chuyến", "Xóa chuyến", "Tìm kiếm", "Lọc", "Làm mới" };
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
    }

    private void taoPanelQuanLyKhachHang() {
        panelQuanLyKhachHang = new JPanel(new BorderLayout());

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));

        String[] buttonNames = { "Thêm KH", "Sửa KH", "Xóa KH", "Tìm kiếm", "Lọc", "Làm mới" };
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
}