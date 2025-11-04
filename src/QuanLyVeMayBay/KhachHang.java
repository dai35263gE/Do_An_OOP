/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.mycompany.QuanLyVeMayBay;

/**
 *
 * @author HP
 */
// File: KhachHang.java

import java.util.Date;

public class KhachHang {
    private String maKH;
    private String hoTen;
    private String soDT;
    private String email;
    private String cmnd;
    private Date ngaySinh;
    private String gioiTinh;
    private String diaChi;
    private String hangKhachHang;
    private int diemTichLuy;
    private Date ngayDangKy;
    
    // Constants
    public static final String HANG_BRONZE = "BRONZE";
    public static final String HANG_SILVER = "SILVER";
    public static final String HANG_GOLD = "GOLD";
    public static final String HANG_PLATINUM = "PLATINUM";
    
    public KhachHang(String maKH, String hoTen, String soDT, String email, 
                    String cmnd, Date ngaySinh, String gioiTinh, String diaChi) {
        this.maKH = maKH;
        this.hoTen = hoTen;
        this.soDT = soDT;
        this.email = email;
        this.cmnd = cmnd;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.diaChi = diaChi;
        this.hangKhachHang = HANG_BRONZE;
        this.diemTichLuy = 0;
        this.ngayDangKy = new Date();
    }
    
    public void tangDiemTichLuy(int diem) {
        this.diemTichLuy += diem;
        capNhatHangKhachHang();
    }
    
    private void capNhatHangKhachHang() {
        if (diemTichLuy >= 10000) {
            hangKhachHang = HANG_PLATINUM;
        } else if (diemTichLuy >= 5000) {
            hangKhachHang = HANG_GOLD;
        } else if (diemTichLuy >= 1000) {
            hangKhachHang = HANG_SILVER;
        } else {
            hangKhachHang = HANG_BRONZE;
        }
    }
    
    public double tinhTyLeGiamGia() {
        switch (hangKhachHang) {
            case HANG_PLATINUM: return 0.15;
            case HANG_GOLD: return 0.10;
            case HANG_SILVER: return 0.05;
            default: return 0.02;
        }
    }
    
    // Getters and Setters
    public String getMaKH() { return maKH; }
    public void setMaKH(String maKH) { this.maKH = maKH; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public String getSoDT() { return soDT; }
    public void setSoDT(String soDT) { this.soDT = soDT; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCmnd() { return cmnd; }
    public void setCmnd(String cmnd) { this.cmnd = cmnd; }
    public Date getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(Date ngaySinh) { this.ngaySinh = ngaySinh; }
    public String getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public String getHangKhachHang() { return hangKhachHang; }
    public void setHangKhachHang(String hangKhachHang) { this.hangKhachHang = hangKhachHang; }
    public int getDiemTichLuy() { return diemTichLuy; }
    public void setDiemTichLuy(int diemTichLuy) { this.diemTichLuy = diemTichLuy; }
    public Date getNgayDangKy() { return ngayDangKy; }
    public void setNgayDangKy(Date ngayDangKy) { this.ngayDangKy = ngayDangKy; }
}
