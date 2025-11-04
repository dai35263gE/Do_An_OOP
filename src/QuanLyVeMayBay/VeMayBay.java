/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.mycompany.QuanLyVeMayBay;

/**
 *
 * @author HP
 */
// File: VeMayBay.java

import java.util.Date;

public abstract class VeMayBay implements Comparable<VeMayBay> {
    protected String maVe;
    protected String hoTenKH;
    protected String cmnd;
    protected Date ngayBay;
    protected double giaVe;
    protected String maChuyenBay;
    protected String soGhe;
    protected String trangThai;
    protected Date ngayDat;
    protected String maKhuyenMai;
    
    // Constants
    public static final String TRANG_THAI_DAT = "ĐẶT";
    public static final String TRANG_THAI_HUY = "HỦY";
    public static final String TRANG_THAI_HOAN_TAT = "HOÀN TẤT";
    
    public VeMayBay(String maVe, String hoTenKH, String cmnd, Date ngayBay, 
                   double giaVe, String maChuyenBay, String soGhe) {
        this.maVe = maVe;
        this.hoTenKH = hoTenKH;
        this.cmnd = cmnd;
        this.ngayBay = ngayBay;
        this.giaVe = giaVe;
        this.maChuyenBay = maChuyenBay;
        this.soGhe = soGhe;
        this.trangThai = TRANG_THAI_DAT;
        this.ngayDat = new Date();
    }
    
    // Abstract methods - ĐA HÌNH
    public abstract double tinhThue();
    public abstract String loaiVe();
    public abstract String chiTietLoaiVe();
    public abstract double tinhTongTien();
    
    // Template method pattern
    public final void inThongTinVe() {
        System.out.println("=== THÔNG TIN VÉ ===");
        System.out.println("Mã vé: " + maVe);
        System.out.println("Loại vé: " + loaiVe());
        System.out.println("Hành khách: " + hoTenKH);
        System.out.println("CMND: " + cmnd);
        System.out.println("Ngày bay: " + ngayBay);
        System.out.println("Ghế: " + soGhe);
        System.out.println("Giá vé: " + giaVe);
        System.out.println("Thuế: " + tinhThue());
        System.out.println("Tổng tiền: " + tinhTongTien());
        System.out.println("Chi tiết: " + chiTietLoaiVe());
    }
    
    @Override
    public int compareTo(VeMayBay other) {
        return this.maVe.compareTo(other.maVe);
    }
    
    // Getters and Setters
    public String getMaVe() { return maVe; }
    public void setMaVe(String maVe) { this.maVe = maVe; }
    public String getHoTenKH() { return hoTenKH; }
    public void setHoTenKH(String hoTenKH) { this.hoTenKH = hoTenKH; }
    public String getCmnd() { return cmnd; }
    public void setCmnd(String cmnd) { this.cmnd = cmnd; }
    public Date getNgayBay() { return ngayBay; }
    public void setNgayBay(Date ngayBay) { this.ngayBay = ngayBay; }
    public double getGiaVe() { return giaVe; }
    public void setGiaVe(double giaVe) { this.giaVe = giaVe; }
    public String getMaChuyenBay() { return maChuyenBay; }
    public void setMaChuyenBay(String maChuyenBay) { this.maChuyenBay = maChuyenBay; }
    public String getSoGhe() { return soGhe; }
    public void setSoGhe(String soGhe) { this.soGhe = soGhe; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public Date getNgayDat() { return ngayDat; }
    public void setNgayDat(Date ngayDat) { this.ngayDat = ngayDat; }
    public String getMaKhuyenMai() { return maKhuyenMai; }
    public void setMaKhuyenMai(String maKhuyenMai) { this.maKhuyenMai = maKhuyenMai; }
}
