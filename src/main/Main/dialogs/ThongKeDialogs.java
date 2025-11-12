package Main.dialogs;

import Main.MainGUI;
import javax.swing.*;
import Main.utils.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import Main.utils.BarChartPanel;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;

import model.*;
import Sevice.*;

public class ThongKeDialogs {
    private QuanLyBanVeMayBay quanLy;
    private MainGUI mainGUI;

    public ThongKeDialogs(QuanLyBanVeMayBay quanLy, MainGUI mainGUI) {
        this.quanLy = quanLy;
        this.mainGUI = mainGUI;
    }

    public void hienThiThongKe(String loai, JTextArea textArea) {
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
                sb.append("- Thương gia: ").append(thongKe.get("veThuongGia")).append(" vé\n");
                sb.append("- Phổ thông: ").append(thongKe.get("vePhoThong")).append(" vé\n");
                sb.append("- Tiết kiệm: ").append(thongKe.get("veTietKiem")).append(" vé\n");
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

            case "Vé theo loại":
                hienThiThongKeVeTheoLoai(sb);
                break;

            case "Khách hàng":
                hienThiThongKeKhachHang(sb);
                break;

            case "Chuyến bay":
                hienThiThongKeChuyenBay(sb);
                break;

            case "Làm mới":
                mainGUI.capNhatThongKeTrangChu();
                return;

            case "Thống kê nâng cao":
                hienThiThongKeNangCao();
                return;
        }

        textArea.setText(sb.toString());
    }

    private void hienThiThongKeVeTheoLoai(StringBuilder sb) {
        DanhSachVeMayBay dsVe = quanLy.getDsVe();
        if (dsVe == null || dsVe.getDanhSach() == null) {
            sb.append("Không có dữ liệu vé để thống kê");
            return;
        }

        sb.append("=== THỐNG KÊ VÉ THEO LOẠI ===\n\n");

        // Thống kê theo loại vé
        long veThuongGia = dsVe.getDanhSach().stream().filter(ve -> ve.loaiVe().equals("VeThuongGia")).count();
        
        long vePhoThong = dsVe.getDanhSach().stream().filter(ve -> ve.loaiVe().equals("VePhoThong")).count();
        
        long veTietKiem = dsVe.getDanhSach().stream().filter(ve -> ve.loaiVe().equals("VeTietKiem")).count();

        long tongVe = dsVe.getDanhSach().size();

        sb.append("Tổng số vé: ").append(tongVe).append("\n\n");
        sb.append("Phân bố theo loại vé:\n");
        sb.append("- Thương gia: ").append(veThuongGia).append(" vé (")
          .append(String.format("%.1f%%", (veThuongGia * 100.0 / tongVe))).append(")\n");
        sb.append("- Phổ thông: ").append(vePhoThong).append(" vé (")
          .append(String.format("%.1f%%", (vePhoThong * 100.0 / tongVe))).append(")\n");
        sb.append("- Tiết kiệm: ").append(veTietKiem).append(" vé (")
          .append(String.format("%.1f%%", (veTietKiem * 100.0 / tongVe))).append(")\n\n");

        // Thống kê theo trạng thái
        Map<String, Long> thongKeTrangThai = dsVe.getDanhSach().stream()
                .collect(Collectors.groupingBy(VeMayBay::getTrangThai, Collectors.counting()));

        sb.append("Phân bố theo trạng thái:\n");
        thongKeTrangThai.forEach((trangThai, soLuong) -> {
            sb.append("- ").append(trangThai).append(": ").append(soLuong).append(" vé (")
              .append(String.format("%.1f%%", (soLuong * 100.0 / tongVe))).append(")\n");
        });
    }

    private void hienThiThongKeKhachHang(StringBuilder sb) {
        DanhSachKhachHang dsKhachHang = quanLy.getDsKhachHang();
        if (dsKhachHang == null || dsKhachHang.getDanhSach() == null) {
            sb.append("Không có dữ liệu khách hàng để thống kê");
            return;
        }

        sb.append("=== THỐNG KÊ KHÁCH HÀNG ===\n\n");

        List<KhachHang> danhSachKH = dsKhachHang.getDanhSach();
        int tongKH = danhSachKH.size();

        sb.append("Tổng số khách hàng: ").append(tongKH).append("\n\n");

        // Thống kê theo hạng khách hàng
        Map<String, Long> thongKeHang = danhSachKH.stream()
                .collect(Collectors.groupingBy(KhachHang::getHangKhachHang, Collectors.counting()));

        sb.append("Phân bố theo hạng:\n");
        thongKeHang.forEach((hang, soLuong) -> {
            sb.append("- ").append(hang).append(": ").append(soLuong).append(" KH (")
              .append(String.format("%.1f%%", (soLuong * 100.0 / tongKH))).append(")\n");
        });

        // Top 10 khách hàng có điểm tích lũy cao nhất
        sb.append("\nTop 10 khách hàng có điểm tích lũy cao nhất:\n");
        danhSachKH.stream()
                .sorted((kh1, kh2) -> Integer.compare(kh2.getDiemTichLuy(), kh1.getDiemTichLuy()))
                .limit(10)
                .forEach(kh -> {
                    sb.append("- ").append(kh.getHoTen())
                      .append(" (").append(kh.getMa()).append("): ")
                      .append(String.format("%,d", kh.getDiemTichLuy())).append(" điểm\n");
                });

        // Thống kê theo năm đăng ký
        Map<Integer, Long> thongKeTheoNam = danhSachKH.stream()
                .filter(kh -> kh.getNgayDangKy() != null)
                .collect(Collectors.groupingBy(
                    kh -> {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(kh.getNgayDangKy());
                        return cal.get(Calendar.YEAR);
                    },
                    Collectors.counting()
                ));

        sb.append("\nThống kê theo năm đăng ký:\n");
        thongKeTheoNam.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    sb.append("- Năm ").append(entry.getKey()).append(": ")
                      .append(entry.getValue()).append(" KH\n");
                });
    }

    private void hienThiThongKeChuyenBay(StringBuilder sb) {
        DanhSachChuyenBay dsChuyenBay = quanLy.getDsChuyenBay();
        if (dsChuyenBay == null || dsChuyenBay.getDanhSach() == null) {
            sb.append("Không có dữ liệu chuyến bay để thống kê");
            return;
        }

        sb.append("=== THỐNG KÊ CHUYẾN BAY ===\n\n");

        List<ChuyenBay> danhSachCB = dsChuyenBay.getDanhSach();
        int tongCB = danhSachCB.size();

        sb.append("Tổng số chuyến bay: ").append(tongCB).append("\n\n");

        // Thống kê theo trạng thái
        Map<String, Long> thongKeTrangThai = danhSachCB.stream()
                .collect(Collectors.groupingBy(ChuyenBay::getTrangThai, Collectors.counting()));

        sb.append("Phân bố theo trạng thái:\n");
        thongKeTrangThai.forEach((trangThai, soLuong) -> {
            sb.append("- ").append(trangThai).append(": ").append(soLuong).append(" chuyến (")
              .append(String.format("%.1f%%", (soLuong * 100.0 / tongCB))).append(")\n");
        });

        // Thống kê theo điểm đi
        Map<String, Long> thongKeDiemDi = danhSachCB.stream()
                .collect(Collectors.groupingBy(ChuyenBay::getDiemDi, Collectors.counting()));

        sb.append("\nThống kê theo điểm đi:\n");
        thongKeDiemDi.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(entry -> {
                    sb.append("- ").append(entry.getKey()).append(": ")
                      .append(entry.getValue()).append(" chuyến\n");
                });

        // Thống kê theo điểm đến
        Map<String, Long> thongKeDiemDen = danhSachCB.stream()
                .collect(Collectors.groupingBy(ChuyenBay::getDiemDen, Collectors.counting()));

        sb.append("\nThống kê theo điểm đến:\n");
        thongKeDiemDen.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(entry -> {
                    sb.append("- ").append(entry.getKey()).append(": ")
                      .append(entry.getValue()).append(" chuyến\n");
                });

        // Tỷ lệ lấp đầy trung bình
        double tyLeLapDayTB = danhSachCB.stream()
                .mapToDouble(cb -> ((double) (cb.getSoGhe() - cb.getSoGheTrong()) / cb.getSoGhe()) * 100)
                .average()
                .orElse(0.0);

        sb.append("\nTỷ lệ lấp đầy trung bình: ").append(String.format("%.1f%%", tyLeLapDayTB)).append("\n");

        // Top 5 chuyến bay có tỷ lệ lấp đầy cao nhất
        sb.append("\nTop 5 chuyến bay có tỷ lệ lấp đầy cao nhất:\n");
        danhSachCB.stream()
                .sorted((cb1, cb2) -> {
                    double tyLe1 = ((double) (cb1.getSoGhe() - cb1.getSoGheTrong()) / cb1.getSoGhe()) * 100;
                    double tyLe2 = ((double) (cb2.getSoGhe() - cb2.getSoGheTrong()) / cb2.getSoGhe()) * 100;
                    return Double.compare(tyLe2, tyLe1);
                })
                .limit(5)
                .forEach(cb -> {
                    double tyLe = ((double) (cb.getSoGhe() - cb.getSoGheTrong()) / cb.getSoGhe()) * 100;
                    sb.append("- ").append(cb.getMaChuyen()).append(" (")
                      .append(cb.getDiemDi()).append(" → ").append(cb.getDiemDen()).append("): ")
                      .append(String.format("%.1f%%", tyLe)).append("\n");
                });
    }

    public void hienThiThongKeNangCao() {
        JDialog dialog = new JDialog(mainGUI, "Thống Kê Nâng Cao", true);
        dialog.setSize(900, 700);
        dialog.setLocationRelativeTo(mainGUI);
        dialog.setLayout(new BorderLayout());

        // Tabbed pane cho các loại thống kê
        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab 1: Thống kê doanh thu theo tháng
        tabbedPane.addTab("📊 Doanh Thu Theo Tháng", taoTabDoanhThuTheoThang());

        // Tab 2: Thống kê tuyến bay phổ biến
        tabbedPane.addTab("✈️ Tuyến Bay Phổ Biến", taoTabTuyenBayPhoBien());

        // Tab 3: Thống kê khách hàng thân thiết
        tabbedPane.addTab("⭐ Khách Hàng Thân Thiết", taoTabKhachHangThanThiet());

        // Tab 4: Thống kê hiệu suất
        tabbedPane.addTab("📈 Hiệu Suất Kinh Doanh", taoTabHieuSuatKinhDoanh());

        // Panel button
        JPanel panelButton = new JPanel(new FlowLayout());
        JButton btnInBaoCao = new JButton("In Báo Cáo");
        JButton btnXuatExcel = new JButton("Xuất Excel");
        JButton btnDong = new JButton("Đóng");

        btnInBaoCao.addActionListener(e -> {
            JOptionPane.showMessageDialog(dialog, 
                "Chức năng in báo cáo sẽ được triển khai sau!", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });

        btnXuatExcel.addActionListener(e -> {
            JOptionPane.showMessageDialog(dialog, 
                "Chức năng xuất Excel sẽ được triển khai sau!", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });

        btnDong.addActionListener(e -> dialog.dispose());

        panelButton.add(btnInBaoCao);
        panelButton.add(btnXuatExcel);
        panelButton.add(btnDong);

        dialog.add(tabbedPane, BorderLayout.CENTER);
        dialog.add(panelButton, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JPanel taoTabDoanhThuTheoThang() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 1. Lấy dữ liệu
        Map<String, Double> doanhThuTheoThang = tinhDoanhThuTheoThang();

        if (doanhThuTheoThang.isEmpty()) {
            panel.add(new JLabel("Không có dữ liệu doanh thu để vẽ biểu đồ.", JLabel.CENTER));
            return panel;
        }

        // 2. Tạo biểu đồ bằng BarChartPanel (đã sắp xếp)
        JScrollPane chartScrollPane = BarChartPanel.createChartScrollPane(
                doanhThuTheoThang,
                "DOANH THU THEO THÁNG (VND)",
                800 // Chiều rộng tối thiểu
        );

        // 3. (Tùy chọn) Thêm thông tin tổng hợp
        double tongDoanhThu = doanhThuTheoThang.values().stream().mapToDouble(Double::doubleValue).sum();
        JPanel panelTongHop = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelTongHop.setBorder(BorderFactory.createTitledBorder("Tổng hợp"));
        panelTongHop.add(new JLabel("Tổng doanh thu: " + String.format("%,.0f VND", tongDoanhThu)));

        panel.add(chartScrollPane, BorderLayout.CENTER);
        panel.add(panelTongHop, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel taoTabTuyenBayPhoBien() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 1. Lấy dữ liệu (Map<String, Long>)
        Map<String, Long> tuyenBayPhoBien = thongKeTuyenBayPhoBien();

        if (tuyenBayPhoBien.isEmpty()) {
            panel.add(new JLabel("Không có dữ liệu tuyến bay để vẽ biểu đồ.", JLabel.CENTER));
            return panel;
        }

        // 2. Chuyển đổi Map<String, Long> sang Map<String, Double>
        Map<String, Double> dataDouble = tuyenBayPhoBien.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> (double) entry.getValue(),
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        // 3. Tạo biểu đồ (đã sắp xếp)
        JScrollPane chartScrollPane = BarChartPanel.createChartScrollPane(
                dataDouble,
                "THỐNG KÊ TUYẾN BAY PHỔ BIẾN (Số vé bán)",
                800 // Chiều rộng tối thiểu
        );

        panel.add(chartScrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel taoTabKhachHangThanThiet() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Lấy top khách hàng thân thiết
        List<KhachHang> topKhachHang = quanLy.getDsKhachHang().getDanhSach().stream()
                .sorted((kh1, kh2) -> Integer.compare(kh2.getDiemTichLuy(), kh1.getDiemTichLuy()))
                .limit(10)
                .collect(Collectors.toList());

        // Tạo bảng hiển thị
        String[] columns = {"Hạng", "Mã KH", "Họ Tên", "Điểm Tích Lũy", "Hạng KH", "Số Vé Đã Mua"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (int i = 0; i < topKhachHang.size(); i++) {
            KhachHang kh = topKhachHang.get(i);
            int soVeDaMua = demSoVeCuaKhachHang(kh.getMa());
            
            model.addRow(new Object[]{
                i + 1,
                kh.getMa(),
                kh.getHoTen(),
                String.format("%,d", kh.getDiemTichLuy()),
                kh.getHangKhachHang(),
                soVeDaMua + " vé"
            });
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        panel.add(new JLabel("TOP 10 KHÁCH HÀNG THÂN THIẾT"), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel taoTabHieuSuatKinhDoanh() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 20, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Tính các chỉ số hiệu suất
        double tyLeLapDayTB = tinhTyLeLapDayTrungBinh();
        double doanhThuTB = tinhDoanhThuTrungBinh();
        double tyLeHuyVe = tinhTyLeHuyVe();
        double tyLeKhachHangQuayLai = tinhTyLeKhachHangQuayLai();

        // Tạo các panel chỉ số
        panel.add(taoChiSoCard("Tỷ Lệ Lấp Đầy TB", String.format("%.1f%%", tyLeLapDayTB), "💺"));
        panel.add(taoChiSoCard("Doanh Thu TB/Chuyến", String.format("%,.0f VND", doanhThuTB), "💰"));
        panel.add(taoChiSoCard("Tỷ Lệ Hủy Vé", String.format("%.1f%%", tyLeHuyVe), "❌"));
        panel.add(taoChiSoCard("KH Quay Lại", String.format("%.1f%%", tyLeKhachHangQuayLai), "🔄"));

        return panel;
    }

    private JPanel taoChiSoCard(String title, String value, String icon) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(70, 130, 180));
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        card.setPreferredSize(new Dimension(200, 100));

        JLabel lblValue = new JLabel("<html><center>" + icon + "<br>" + value + "</center></html>", JLabel.CENTER);
        lblValue.setFont(new Font("Arial", Font.BOLD, 16));
        lblValue.setForeground(Color.WHITE);

        JLabel lblTitle = new JLabel(title, JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 12));
        lblTitle.setForeground(Color.WHITE);

        card.add(lblValue, BorderLayout.CENTER);
        card.add(lblTitle, BorderLayout.SOUTH);

        return card;
    }

    // Các phương thức tính toán dữ liệu thống kê
    private Map<String, Double> tinhDoanhThuTheoThang() {
        Map<String, Double> doanhThuTheoThang = new HashMap<>();
        
        DanhSachVeMayBay dsVe = quanLy.getDsVe();
        if (dsVe != null && dsVe.getDanhSach() != null) {
            for (VeMayBay ve : dsVe.getDanhSach()) {
                if (ve.getNgayBay() != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
                    String thangNam = sdf.format(ve.getNgayBay());
                    doanhThuTheoThang.merge(thangNam, ve.getGiaVe(), Double::sum);
                }
            }
        }
        
        return doanhThuTheoThang;
    }

    private Map<String, Long> thongKeTuyenBayPhoBien() {
        Map<String, Long> tuyenBayPhoBien = new HashMap<>();
        
        DanhSachVeMayBay dsVe = quanLy.getDsVe();
        DanhSachChuyenBay dsChuyenBay = quanLy.getDsChuyenBay();
        
        if (dsVe != null && dsVe.getDanhSach() != null && dsChuyenBay != null) {
            for (VeMayBay ve : dsVe.getDanhSach()) {
                ChuyenBay cb = dsChuyenBay.timKiemTheoMa(ve.getMaChuyen());
                if (cb != null) {
                    String tuyenBay = cb.getDiemDi() + " → " + cb.getDiemDen();
                    tuyenBayPhoBien.merge(tuyenBay, 1L, Long::sum);
                }
            }
        }
        
        return tuyenBayPhoBien;
    }

    private int demSoVeCuaKhachHang(String maKH) {
        // DanhSachVeMayBay dsVe = quanLy.getDsVe();
        // if (dsVe != null && dsVe.getDanhSach() != null) {
        //     return (int) dsVe.getDanhSach().stream().filter(ve -> maKH.equals(ve.getHoaDon().getKhachHang().getMa())).count();
        // }
        return 5;
    }

    private double tinhTyLeLapDayTrungBinh() {
        DanhSachChuyenBay dsChuyenBay = quanLy.getDsChuyenBay();
        if (dsChuyenBay != null && dsChuyenBay.getDanhSach() != null) {
            return dsChuyenBay.getDanhSach().stream()
                    .mapToDouble(cb -> ((double) (cb.getSoGhe() - cb.getSoGheTrong()) / cb.getSoGhe()) * 100)
                    .average()
                    .orElse(0.0);
        }
        return 0.0;
    }

    private double tinhDoanhThuTrungBinh() {
        DanhSachChuyenBay dsChuyenBay = quanLy.getDsChuyenBay();
        if (dsChuyenBay != null && dsChuyenBay.getDanhSach() != null) {
            return dsChuyenBay.getDanhSach().stream()
                    .mapToDouble(cb -> {
                        int soVeDaBan = cb.getSoGhe() - cb.getSoGheTrong();
                        return soVeDaBan * cb.getGiaCoBan();
                    })
                    .average()
                    .orElse(0.0);
        }
        return 0.0;
    }

    private double tinhTyLeHuyVe() {
        DanhSachVeMayBay dsVe = quanLy.getDsVe();
        if (dsVe != null && dsVe.getDanhSach() != null) {
            long tongVe = dsVe.getDanhSach().size();
            long veDaHuy = dsVe.getDanhSach().stream()
                    .filter(ve -> "HỦY".equals(ve.getTrangThai()))
                    .count();
            return (veDaHuy * 100.0) / tongVe;
        }
        return 0.0;
    }

    private double tinhTyLeKhachHangQuayLai() {
        // Giả sử khách hàng quay lại là khách hàng có từ 2 vé trở lên
        DanhSachKhachHang dsKH = quanLy.getDsKhachHang();
        DanhSachVeMayBay dsVe = quanLy.getDsVe();
        
        if (dsKH != null && dsVe != null) {
            long tongKH = dsKH.getDanhSach().size();
            long khQuayLai = dsKH.getDanhSach().stream()
                    .filter(kh -> demSoVeCuaKhachHang(kh.getMa()) >= 2)
                    .count();
            return (khQuayLai * 100.0) / tongKH;
        }
        return 0.0;
    }
}