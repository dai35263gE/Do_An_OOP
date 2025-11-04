/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.mycompany.QuanLyVeMayBay;

/**
 *
 * @author HP
 */
// File: QuanLyBanVeMayBay.java


import java.util.*;
import java.text.SimpleDateFormat;

public class QuanLyBanVeMayBay {
    private DanhSachVeMayBay dsVe;
    private DanhSachChuyenBay dsChuyenBay;
    private DanhSachKhachHang dsKhachHang;
    private DanhSachHoaDon dsHoaDon;
    
    private Scanner scanner;
    private SimpleDateFormat sdf;
    
    // Static properties for tracking
    private static int soLanTruyCap = 0;
    private static final String PHIEN_BAN = "1.0.0";
    
    public QuanLyBanVeMayBay() {
        this.dsVe = new DanhSachVeMayBay();
        this.dsChuyenBay = new DanhSachChuyenBay();
        this.dsKhachHang = new DanhSachKhachHang();
        this.dsHoaDon = new DanhSachHoaDon();
        this.scanner = new Scanner(System.in);
        this.sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        soLanTruyCap++;
    }
    
    // Static methods
    public static int getSoLanTruyCap() {
        return soLanTruyCap;
    }
    
    public static String getPhienBan() {
        return PHIEN_BAN;
    }
    
    public static void thongBaoPhienBan() {
        System.out.println("=== HỆ THỐNG QUẢN LÝ BÁN VÉ MÁY BAY ===");
        System.out.println("Phiên bản: " + PHIEN_BAN);
        System.out.println("Số lượt truy cập: " + soLanTruyCap);
        System.out.println("=====================================");
    }
    
    public void khoiTaoDuLieuMau() {
        System.out.println("Đang khởi tạo dữ liệu mẫu...");
        
        // Khởi tạo dữ liệu mẫu cho các danh sách
        // (Code khởi tạo dữ liệu mẫu)
        
        System.out.println("Khởi tạo dữ liệu mẫu hoàn tất!");
    }
    
    public void menuChinh() {
        int luaChon;
        do {
            System.out.println("\n=== HỆ THỐNG QUẢN LÝ BÁN VÉ MÁY BAY ===");
            System.out.println("1. Quản lý Vé máy bay");
            System.out.println("2. Quản lý Chuyến bay");
            System.out.println("3. Quản lý Khách hàng");
            System.out.println("4. Quản lý Hóa đơn");
            System.out.println("5. Thống kê & Báo cáo");
            System.out.println("6. Tìm kiếm & Lọc");
            System.out.println("7. Quản lý File");
            System.out.println("0. Thoát");
            System.out.print("Chọn chức năng: ");
            
            luaChon = scanner.nextInt();
            scanner.nextLine(); // Clear buffer
            
            switch (luaChon) {
                case 1 -> menuQuanLyVe();
                case 2 -> menuQuanLyChuyenBay();
                case 3 -> menuQuanLyKhachHang();
                case 4 -> menuQuanLyHoaDon();
                case 5 -> menuThongKe();
                case 6 -> menuTimKiem();
                case 7 -> menuQuanLyFile();
                case 0 -> thoatChuongTrinh();
                default -> System.out.println("Lựa chọn không hợp lệ!");
            }
        } while (luaChon != 0);
    }
    
    private void menuQuanLyVe() {
        // Triển khai đầy đủ menu quản lý vé
        System.out.println("\n=== QUẢN LÝ VÉ MÁY BAY ===");
        // ... chi tiết menu
    }
    
    private void menuQuanLyChuyenBay() {
        // Triển khai menu quản lý chuyến bay
    }
    
    private void menuQuanLyKhachHang() {
        // Triển khai menu quản lý khách hàng
    }
    
    private void menuQuanLyHoaDon() {
        // Triển khai menu quản lý hóa đơn
    }
    
    private void menuThongKe() {
        System.out.println("\n=== THỐNG KÊ & BÁO CÁO ===");
        System.out.println("1. Thống kê tổng quan");
        System.out.println("2. Thống kê doanh thu");
        System.out.println("3. Thống kê vé theo loại");
        System.out.println("4. Thống kê khách hàng");
        System.out.println("5. Báo cáo chuyến bay");
        System.out.println("0. Quay lại");
        
        int luaChon = scanner.nextInt();
        scanner.nextLine();
        
        switch (luaChon) {
            case 1 -> hienThiThongKeTongQuan();
            case 2 -> hienThiThongKeDoanhThu();
            case 3 -> hienThiThongKeVe();
            case 4 -> hienThiThongKeKhachHang();
            case 5 -> hienThiBaoCaoChuyenBay();
        }
    }
    
    private void menuTimKiem() {
        System.out.println("\n=== TÌM KIẾM & LỌC ===");
        System.out.println("1. Tìm kiếm vé");
        System.out.println("2. Tìm kiếm chuyến bay");
        System.out.println("3. Tìm kiếm khách hàng");
        System.out.println("4. Tìm kiếm đa tiêu chí");
        System.out.println("0. Quay lại");
        
        int luaChon = scanner.nextInt();
        scanner.nextLine();
        
        switch (luaChon) {
            case 1 -> timKiemVe();
            case 2 -> timKiemChuyenBay();
            case 3 -> timKiemKhachHang();
            case 4 -> timKiemDaTieuChi();
        }
    }
    
    private void menuQuanLyFile() {
        System.out.println("\n=== QUẢN LÝ FILE ===");
        System.out.println("1. Đọc dữ liệu từ file");
        System.out.println("2. Ghi dữ liệu ra file");
        System.out.println("3. Sao lưu dữ liệu");
        System.out.println("0. Quay lại");
        
        int luaChon = scanner.nextInt();
        scanner.nextLine();
        
        switch (luaChon) {
            case 1 -> docDuLieuTuFile();
            case 2 -> ghiDuLieuRaFile();
            case 3 -> saoLuuDuLieu();
        }
    }
    
    private void hienThiThongKeTongQuan() {
        System.out.println("\n=== THỐNG KÊ TỔNG QUAN ===");
        System.out.println("Tổng số vé: " + dsVe.demSoLuong());
        System.out.println("Tổng số chuyến bay: " + dsChuyenBay.demSoLuong());
        System.out.println("Tổng số khách hàng: " + dsKhachHang.demSoLuong());
        System.out.println("Tổng doanh thu: " + dsHoaDon.tinhTongDoanhThu());
        
        // Thống kê chi tiết
        dsVe.hienThiThongKeChiTiet();
    }
    
    private void timKiemDaTieuChi() {
        System.out.println("\n=== TÌM KIẾM ĐA TIÊU CHÍ ===");
        System.out.println("1. Tìm kiếm vé đa tiêu chí");
        System.out.println("2. Tìm kiếm chuyến bay đa tiêu chí");
        System.out.println("3. Tìm kiếm khách hàng đa tiêu chí");
        
        int luaChon = scanner.nextInt();
        scanner.nextLine();
        
        switch (luaChon) {
            case 1 -> timKiemVeDaTieuChi();
            case 2 -> timKiemChuyenBayDaTieuChi();
            case 3 -> timKiemKhachHangDaTieuChi();
        }
    }
    
    private void timKiemVeDaTieuChi() {
        Map<String, Object> filters = new HashMap<>();
        
        System.out.print("Loại vé (Enter để bỏ qua): ");
        String loaiVe = scanner.nextLine();
        if (!loaiVe.isEmpty()) filters.put("loaiVe", loaiVe);
        
        System.out.print("Trạng thái (Enter để bỏ qua): ");
        String trangThai = scanner.nextLine();
        if (!trangThai.isEmpty()) filters.put("trangThai", trangThai);
        
        // Thêm các tiêu chí khác...
        
        List<VeMayBay> ketQua = dsVe.timKiemDaTieuChi(filters);
        hienThiKetQuaTimKiem(ketQua);
    }
    
    private void hienThiKetQuaTimKiem(List<VeMayBay> ketQua) {
        if (ketQua.isEmpty()) {
            System.out.println("Không tìm thấy kết quả nào!");
            return;
        }
        
        System.out.println("=== KẾT QUẢ TÌM KIẾM ===");
        for (int i = 0; i < ketQua.size(); i++) {
            System.out.println((i + 1) + ". " + ketQua.get(i));
        }
    }
    
    private void thoatChuongTrinh() {
        System.out.println("Đang lưu dữ liệu...");
        ghiDuLieuRaFile();
        System.out.println("Cảm ơn đã sử dụng hệ thống!");
        System.out.println("Tổng số lượt truy cập: " + soLanTruyCap);
    }
    
    private void docDuLieuTuFile() {
        dsVe.docFile("data_ve.txt");
        dsChuyenBay.docFile("data_chuyenbay.txt");
        dsKhachHang.docFile("data_khachhang.txt");
    }
    
    void ghiDuLieuRaFile() {
        dsVe.ghiFile("data_ve.txt");
        dsChuyenBay.ghiFile("data_chuyenbay.txt");
        dsKhachHang.ghiFile("data_khachhang.txt");
        System.out.println("Đã ghi dữ liệu ra file!");
    }
    
    void saoLuuDuLieu() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        dsVe.ghiFile("backup/ve_" + timestamp + ".txt");
        dsChuyenBay.ghiFile("backup/chuyenbay_" + timestamp + ".txt");
        dsKhachHang.ghiFile("backup/khachhang_" + timestamp + ".txt");
        System.out.println("Sao lưu dữ liệu thành công!");
    }
    
    // Các phương thức khác...
    private void timKiemVe() { /* implementation */ }
    private void timKiemChuyenBay() { /* implementation */ }
    private void timKiemKhachHang() { /* implementation */ }
    private void hienThiThongKeDoanhThu() { /* implementation */ }
    private void hienThiThongKeVe() { /* implementation */ }
    private void hienThiThongKeKhachHang() { /* implementation */ }
    private void hienThiBaoCaoChuyenBay() { /* implementation */ }
    private void timKiemChuyenBayDaTieuChi() { /* implementation */ }
    private void timKiemKhachHangDaTieuChi() { /* implementation */ }
    
    // Getter methods for testing
    public DanhSachVeMayBay getDsVe() { return dsVe; }
    public DanhSachChuyenBay getDsChuyenBay() { return dsChuyenBay; }
    public DanhSachKhachHang getDsKhachHang() { return dsKhachHang; }
    public DanhSachHoaDon getDsHoaDon() { return dsHoaDon; }
}
