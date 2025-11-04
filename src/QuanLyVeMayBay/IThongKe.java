/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.mycompany.QuanLyVeMayBay;

/**
 *
 * @author HP
 */
// File: IThongKe.java


import java.util.Date;
import java.util.Map;

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
}
