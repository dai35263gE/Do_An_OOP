/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.mycompany.QuanLyVeMayBay;

/**
 *
 * @author HP
 */
// File: HoaDon.java

import java.util.Date;
import java.util.UUID;

public class HoaDon {
    private String maHoaDon;
    private Date ngayLap;
    private String maVe;
    private String maKH;
    private String maNV;
    private double tongTien;
    private double thue;
    private double khuyenMai;
    private double thanhTien;
    private String phuongThucTT;
    private String trangThai;
    
    // Constants
    public static final String TT_CHUA_TT = "CHƯA_THANH_TOÁN";
    public static final String TT_DA_TT = "ĐÃ_THANH_TOÁN";
    public static final String TT_HUY = "HỦY";
    
    public HoaDon(String maVe, String maKH, String maNV, double tongTien, 
                  double thue, double khuyenMai, String phuongThucTT) {
        this.maHoaDon = "HD" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.ngayLap = new Date();
        this.maVe = maVe;
        this.maKH = maKH;
        this.maNV = maNV;
        this.tongTien = tongTien;
        this.thue = thue;
        this.khuyenMai = khuyenMai;
        this.thanhTien = tongTien + thue - khuyenMai;
        this.phuongThucTT = phuongThucTT;
        this.trangThai = TT_CHUA_TT;
    }
    
    public void thanhToan() {
        this.trangThai = TT_DA_TT;
    }
    
    public void huyHoaDon() {
        this.trangThai = TT_HUY;
    }
    
    public void inHoaDon() {
        System.out.println("=== HÓA ĐƠN BÁN VÉ ===");
        System.out.println("Mã HĐ: " + maHoaDon);
        System.out.println("Ngày lập: " + ngayLap);
        System.out.println("Mã vé: " + maVe);
        System.out.println("Mã KH: " + maKH);
        System.out.println("Mã NV: " + maNV);
        System.out.println("Tổng tiền: " + tongTien);
        System.out.println("Thuế: " + thue);
        System.out.println("Khuyến mãi: " + khuyenMai);
        System.out.println("Thành tiền: " + thanhTien);
        System.out.println("Phương thức TT: " + phuongThucTT);
        System.out.println("Trạng thái: " + trangThai);
    }
    
    // Getters and Setters
    public String getMaHoaDon() { return maHoaDon; }
    public Date getNgayLap() { return ngayLap; }
    public String getMaVe() { return maVe; }
    public String getMaKH() { return maKH; }
    public String getMaNV() { return maNV; }
    public double getTongTien() { return tongTien; }
    public double getThue() { return thue; }
    public double getKhuyenMai() { return khuyenMai; }
    public double getThanhTien() { return thanhTien; }
    public String getPhuongThucTT() { return phuongThucTT; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}
