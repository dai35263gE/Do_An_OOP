/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package repository;
/**
 *
 * @author HP
 */
// File: IThongKe.java

import java.util.Date;
import java.util.Map;
import java.util.List;

public interface IThongKe {
    // Basic statistics
    double tinhTongDoanhThu();
    int demSoLuongTheoLoai(String loai);
    double tinhDoanhThuTheoLoai(String loai);
    
    // Advanced statistics
    Map<String, Integer> thongKeTheoThang(int thang, int nam);
    Map<String, Double> thongKeDoanhThuTheoThang(int thang, int nam);
    Map<String, Integer> thongKeTheoChuyenBay();
    Map<String, Double> thongKeDoanhThuTheoChuyenBay();
    
    // Date range statistics
    Map<String, Object> thongKeTheoKhoangNgay(Date from, Date to);
    Map<String, Integer> thongKeKhachHangThuongXuyen(int soChuyenToiThieu);
    
    // Revenue analysis
    double tinhTyLeDoanhThuTheoLoai();
    Map<String, Double> thongKeTyLeDoanhThu();
    
    // THÊM: Thống kê nâng cao
    Map<String, Object> thongKeTongHop(Date from, Date to);
    List<Map<String, Object>> thongKeTopKhachHang(int limit);
    Map<String, Integer> thongKeTheoGioTrongNgay();
    double tinhDoanhThuTrungBinhTheoChuyen();
}