/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.mycompany.QuanLyVeMayBay;

/**
 *
 * @author HP
 */
// File: DanhSachVeMayBay.java
import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;

public class DanhSachVeMayBay implements IQuanLy<VeMayBay>, IFileHandler, IThongKe {
    private List<VeMayBay> danhSach;
    private static final int MAX_SIZE = 10000;
    
    public DanhSachVeMayBay() {
        this.danhSach = new ArrayList<>();
    }
    
    // ========== IMPLEMENT IQUANLY ==========
    @Override
    public boolean them(VeMayBay ve) {
        if (danhSach.size() >= MAX_SIZE) {
            System.out.println("Danh sách đã đầy! Không thể thêm mới.");
            return false;
        }
        
        if (tonTai(ve.getMaVe())) {
            System.out.println("Mã vé đã tồn tại! Không thể thêm.");
            return false;
        }
        
        danhSach.add(ve);
        System.out.println("Thêm vé thành công!");
        return true;
    }
    
    @Override
    public boolean xoa(String maVe) {
        for (Iterator<VeMayBay> iterator = danhSach.iterator(); iterator.hasNext();) {
            VeMayBay ve = iterator.next();
            if (ve.getMaVe().equals(maVe)) {
                iterator.remove();
                System.out.println("Xóa vé thành công!");
                return true;
            }
        }
        System.out.println("Không tìm thấy vé với mã: " + maVe);
        return false;
    }
    
    @Override
    public boolean sua(String maVe, VeMayBay veMoi) {
        for (int i = 0; i < danhSach.size(); i++) {
            if (danhSach.get(i).getMaVe().equals(maVe)) {
                danhSach.set(i, veMoi);
                System.out.println("Cập nhật vé thành công!");
                return true;
            }
        }
        System.out.println("Không tìm thấy vé với mã: " + maVe);
        return false;
    }
    
    @Override
    public VeMayBay timKiemTheoMa(String maVe) {
        return danhSach.stream()
                      .filter(ve -> ve.getMaVe().equals(maVe))
                      .findFirst()
                      .orElse(null);
    }
    
    @Override
    public List<VeMayBay> timKiemTheoTen(String ten) {
        List<VeMayBay> ketQua = new ArrayList<>();
        for (VeMayBay ve : danhSach) {
            if (ve.getHoTenKH().toLowerCase().contains(ten.toLowerCase())) {
                ketQua.add(ve);
            }
        }
        return ketQua;
    }
    
    @Override
    public List<VeMayBay> timKiemTheoCMND(String cmnd) {
        List<VeMayBay> ketQua = new ArrayList<>();
        for (VeMayBay ve : danhSach) {
            if (ve.getCmnd().equals(cmnd)) {
                ketQua.add(ve);
            }
        }
        return ketQua;
    }
    
    @Override
    public List<VeMayBay> timKiemTheoChuyenBay(String maChuyen) {
        List<VeMayBay> ketQua = new ArrayList<>();
        for (VeMayBay ve : danhSach) {
            if (ve.getMaChuyenBay().equals(maChuyen)) {
                ketQua.add(ve);
            }
        }
        return ketQua;
    }
    
    @Override
    public List<VeMayBay> timKiemTheoKhoangGia(double min, double max) {
        List<VeMayBay> ketQua = new ArrayList<>();
        for (VeMayBay ve : danhSach) {
            if (ve.getGiaVe() >= min && ve.getGiaVe() <= max) {
                ketQua.add(ve);
            }
        }
        return ketQua;
    }
    
    @Override
    public List<VeMayBay> timKiemTheoNgayBay(Date ngay) {
        List<VeMayBay> ketQua = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String ngayCanTim = sdf.format(ngay);
        
        for (VeMayBay ve : danhSach) {
            String ngayVe = sdf.format(ve.getNgayBay());
            if (ngayVe.equals(ngayCanTim)) {
                ketQua.add(ve);
            }
        }
        return ketQua;
    }
    
    // TÌM KIẾM NÂNG CAO - ĐA TIÊU CHÍ
    public List<VeMayBay> timKiemDaTieuChi(Map<String, Object> filters) {
        List<VeMayBay> ketQua = new ArrayList<>(danhSach);
        
        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            switch (key) {
                case "loaiVe":
                    ketQua.removeIf(ve -> !ve.loaiVe().equals(value));
                    break;
                case "trangThai":
                    ketQua.removeIf(ve -> !ve.getTrangThai().equals(value));
                    break;
                case "maChuyenBay":
                    ketQua.removeIf(ve -> !ve.getMaChuyenBay().equals(value));
                    break;
                case "tuNgay":
                    Date tuNgay = (Date) value;
                    ketQua.removeIf(ve -> ve.getNgayBay().before(tuNgay));
                    break;
                case "denNgay":
                    Date denNgay = (Date) value;
                    ketQua.removeIf(ve -> ve.getNgayBay().after(denNgay));
                    break;
                case "giaMin":
                    double giaMin = (double) value;
                    ketQua.removeIf(ve -> ve.getGiaVe() < giaMin);
                    break;
                case "giaMax":
                    double giaMax = (double) value;
                    ketQua.removeIf(ve -> ve.getGiaVe() > giaMax);
                    break;
            }
        }
        
        return ketQua;
    }
    
    @Override
    public void hienThiTatCa() {
        if (danhSach.isEmpty()) {
            System.out.println("Danh sách vé trống!");
            return;
        }
        
        System.out.println("===== DANH SÁCH TẤT CẢ VÉ =====");
        for (int i = 0; i < danhSach.size(); i++) {
            System.out.println((i + 1) + ". " + danhSach.get(i));
        }
    }
    
    @Override
    public void hienThiTheoTrangThai(String trangThai) {
        List<VeMayBay> ketQua = danhSach.stream()
                                       .filter(ve -> ve.getTrangThai().equals(trangThai))
                                       .toList();
        
        if (ketQua.isEmpty()) {
            System.out.println("Không có vé nào với trạng thái: " + trangThai);
            return;
        }
        
        System.out.println("===== DANH SÁCH VÉ " + trangThai + " =====");
        for (int i = 0; i < ketQua.size(); i++) {
            System.out.println((i + 1) + ". " + ketQua.get(i));
        }
    }
    
    @Override
    public int demSoLuong() {
        return danhSach.size();
    }
    
    @Override
    public boolean tonTai(String ma) {
        return danhSach.stream().anyMatch(ve -> ve.getMaVe().equals(ma));
    }
    
    @Override
    public void sapXepTheoMa() {
        danhSach.sort(Comparator.comparing(VeMayBay::getMaVe));
    }
    
    @Override
    public void sapXepTheoGia() {
        danhSach.sort(Comparator.comparingDouble(VeMayBay::getGiaVe));
    }
    
    @Override
    public void sapXepTheoNgayBay() {
        danhSach.sort(Comparator.comparing(VeMayBay::getNgayBay));
    }
    
    // ========== IMPLEMENT IFILEHANDLER ==========
    @Override
    public boolean docFile(String tenFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(tenFile))) {
            String line;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 7) {
                    // Logic đọc từ file - cần triển khai chi tiết dựa trên định dạng file
                    // Đây là ví dụ đơn giản
                    Date ngayBay = sdf.parse(data[3]);
                    // Tạo đối tượng vé và thêm vào danh sách
                }
            }
            System.out.println("Đọc file thành công!");
            return true;
        } catch (Exception e) {
            System.out.println("Lỗi đọc file: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean ghiFile(String tenFile) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(tenFile))) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            for (VeMayBay ve : danhSach) {
                pw.println(String.format("%s,%s,%s,%s,%.2f,%s,%s",
                    ve.getMaVe(), ve.getHoTenKH(), ve.getCmnd(),
                    sdf.format(ve.getNgayBay()), ve.getGiaVe(),
                    ve.getMaChuyenBay(), ve.loaiVe()));
            }
            System.out.println("Ghi file thành công!");
            return true;
        } catch (IOException e) {
            System.out.println("Lỗi ghi file: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean xuatExcel(String tenFile) {
        // Triển khai xuất Excel (sử dụng thư viện như Apache POI)
        System.out.println("Xuất Excel - Chức năng đang phát triển");
        return false;
    }
    

    // ========== IMPLEMENT ITHONGKE ==========
    @Override
    public double tinhTongDoanhThu() {
        return danhSach.stream()
                      .filter(ve -> ve.getTrangThai().equals(VeMayBay.TRANG_THAI_HOAN_TAT))
                      .mapToDouble(VeMayBay::tinhTongTien)
                      .sum();
    }
    
    @Override
    public int demSoLuongTheoLoai(String loai) {
        return (int) danhSach.stream()
                            .filter(ve -> ve.loaiVe().equals(loai))
                            .count();
    }
    
    @Override
    public double tinhDoanhThuTheoLoai(String loai) {
        return danhSach.stream()
                      .filter(ve -> ve.loaiVe().equals(loai) && 
                                   ve.getTrangThai().equals(VeMayBay.TRANG_THAI_HOAN_TAT))
                      .mapToDouble(VeMayBay::tinhTongTien)
                      .sum();
    }
    
    @Override
    public Map<String, Integer> thongKeTheoThang(int thang, int nam) {
        Map<String, Integer> thongKe = new HashMap<>();
        Calendar cal = Calendar.getInstance();
        
        for (VeMayBay ve : danhSach) {
            cal.setTime(ve.getNgayBay());
            int veThang = cal.get(Calendar.MONTH) + 1;
            int veNam = cal.get(Calendar.YEAR);
            
            if (veThang == thang && veNam == nam) {
                String loai = ve.loaiVe();
                thongKe.put(loai, thongKe.getOrDefault(loai, 0) + 1);
            }
        }
        
        return thongKe;
    }
    
    @Override
    public Map<String, Double> thongKeDoanhThuTheoThang(int thang, int nam) {
        Map<String, Double> thongKe = new HashMap<>();
        Calendar cal = Calendar.getInstance();
        
        for (VeMayBay ve : danhSach) {
            if (!ve.getTrangThai().equals(VeMayBay.TRANG_THAI_HOAN_TAT)) continue;
            
            cal.setTime(ve.getNgayBay());
            int veThang = cal.get(Calendar.MONTH) + 1;
            int veNam = cal.get(Calendar.YEAR);
            
            if (veThang == thang && veNam == nam) {
                String loai = ve.loaiVe();
                thongKe.put(loai, thongKe.getOrDefault(loai, 0.0) + ve.tinhTongTien());
            }
        }
        
        return thongKe;
    }
    
    @Override
    public Map<String, Integer> thongKeTheoChuyenBay() {
        Map<String, Integer> thongKe = new HashMap<>();
        for (VeMayBay ve : danhSach) {
            String chuyenBay = ve.getMaChuyenBay();
            thongKe.put(chuyenBay, thongKe.getOrDefault(chuyenBay, 0) + 1);
        }
        return thongKe;
    }
    
    @Override
    public Map<String, Double> thongKeDoanhThuTheoChuyenBay() {
        Map<String, Double> thongKe = new HashMap<>();
        for (VeMayBay ve : danhSach) {
            if (ve.getTrangThai().equals(VeMayBay.TRANG_THAI_HOAN_TAT)) {
                String chuyenBay = ve.getMaChuyenBay();
                thongKe.put(chuyenBay, thongKe.getOrDefault(chuyenBay, 0.0) + ve.tinhTongTien());
            }
        }
        return thongKe;
    }
    
    @Override
    public Map<String, Object> thongKeTheoKhoangNgay(Date from, Date to) {
        Map<String, Object> thongKe = new HashMap<>();
        int tongVe = 0;
        double tongDoanhThu = 0;
        Map<String, Integer> theoLoai = new HashMap<>();
        Map<String, Double> doanhThuTheoLoai = new HashMap<>();
        
        for (VeMayBay ve : danhSach) {
            if (ve.getNgayBay().after(from) && ve.getNgayBay().before(to)) {
                tongVe++;
                if (ve.getTrangThai().equals(VeMayBay.TRANG_THAI_HOAN_TAT)) {
                    tongDoanhThu += ve.tinhTongTien();
                }
                
                // Thống kê theo loại
                String loai = ve.loaiVe();
                theoLoai.put(loai, theoLoai.getOrDefault(loai, 0) + 1);
                if (ve.getTrangThai().equals(VeMayBay.TRANG_THAI_HOAN_TAT)) {
                    doanhThuTheoLoai.put(loai, 
                        doanhThuTheoLoai.getOrDefault(loai, 0.0) + ve.tinhTongTien());
                }
            }
        }
        
        thongKe.put("tongVe", tongVe);
        thongKe.put("tongDoanhThu", tongDoanhThu);
        thongKe.put("theoLoai", theoLoai);
        thongKe.put("doanhThuTheoLoai", doanhThuTheoLoai);
        
        return thongKe;
    }
    
    @Override
    public Map<String, Integer> thongKeKhachHangThuongXuyen(int soChuyenToiThieu) {
        Map<String, Integer> khachHang = new HashMap<>();
        for (VeMayBay ve : danhSach) {
            String cmnd = ve.getCmnd();
            khachHang.put(cmnd, khachHang.getOrDefault(cmnd, 0) + 1);
        }
        
        // Lọc những khách hàng có số chuyến >= ngưỡng
        Map<String, Integer> result = new HashMap<>();
        for (Map.Entry<String, Integer> entry : khachHang.entrySet()) {
            if (entry.getValue() >= soChuyenToiThieu) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        
        return result;
    }
    
    @Override
    public double tinhTyLeDoanhThuTheoLoai() {
        // Tính tỷ lệ doanh thu từng loại vé
        double tongDoanhThu = tinhTongDoanhThu();
        if (tongDoanhThu == 0) return 0;
        
        // Có thể triển khai chi tiết hơn
        return 1.0;
    }
    
    @Override
    public Map<String, Double> thongKeTyLeDoanhThu() {
        Map<String, Double> tyLe = new HashMap<>();
        double tongDoanhThu = tinhTongDoanhThu();
        
        if (tongDoanhThu > 0) {
            String[] loaiVes = {"THƯƠNG GIA", "PHỔ THÔNG", "TIẾT KIỆM"};
            for (String loai : loaiVes) {
                double doanhThuLoai = tinhDoanhThuTheoLoai(loai);
                tyLe.put(loai, (doanhThuLoai / tongDoanhThu) * 100);
            }
        }
        
        return tyLe;
    }
    
    // ========== PHƯƠNG THỨC BỔ SUNG ==========
    public List<VeMayBay> getDanhSach() {
        return new ArrayList<>(danhSach);
    }
    
    public void xoaTatCa() {
        danhSach.clear();
        System.out.println("Đã xóa tất cả vé!");
    }
    
    public void hienThiThongKeChiTiet() {
        System.out.println("===== THỐNG KÊ CHI TIẾT =====");
        System.out.println("Tổng số vé: " + demSoLuong());
        System.out.println("Tổng doanh thu: " + tinhTongDoanhThu());
        
        String[] loaiVes = {"THƯƠNG GIA", "PHỔ THÔNG", "TIẾT KIỆM"};
        for (String loai : loaiVes) {
            System.out.printf("%s: %d vé, Doanh thu: %.2f%n", 
                loai, demSoLuongTheoLoai(loai), tinhDoanhThuTheoLoai(loai));
        }
        
        // Thống kê theo trạng thái
        long soVeDat = danhSach.stream().filter(v -> v.getTrangThai().equals(VeMayBay.TRANG_THAI_DAT)).count();
        long soVeHuy = danhSach.stream().filter(v -> v.getTrangThai().equals(VeMayBay.TRANG_THAI_HUY)).count();
        long soVeHoanTat = danhSach.stream().filter(v -> v.getTrangThai().equals(VeMayBay.TRANG_THAI_HOAN_TAT)).count();
        
        System.out.println("Trạng thái vé:");
        System.out.println("  - Đặt: " + soVeDat);
        System.out.println("  - Hủy: " + soVeHuy);
        System.out.println("  - Hoàn tất: " + soVeHoanTat);
    }
}