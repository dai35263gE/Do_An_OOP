/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.mycompany.QuanLyVeMayBay;

/**
 *`
 * @author HP
 */
// File: ChuyenBay.java

import java.util.Date;

public class ChuyenBay {
    private String maChuyen;
    private String diemDi;
    private String diemDen;
    private Date gioKhoiHanh;
    private Date gioDen;
    private int soGhe;
    private int soGheTrong;
    private String maMayBay;
    private double giaCoBan;
    private String trangThai;
    
    // Static properties
    private static int soChuyenBay = 0;
    public static final String TRANG_THAI_CHUA_BAY = "CHƯA BAY";
    public static final String TRANG_THAI_DA_BAY = "ĐÃ BAY";
    public static final String TRANG_THAI_HUY = "HỦY";
    
    public ChuyenBay(String maChuyen, String diemDi, String diemDen, Date gioKhoiHanh, Date gioDen, int soGhe, String maMayBay, double giaCoBan) {
        this.maChuyen = maChuyen;
        this.diemDi = diemDi;
        this.diemDen = diemDen;
        this.gioKhoiHanh = gioKhoiHanh;
        this.gioDen = gioDen;
        this.soGhe = soGhe;
        this.soGheTrong = soGhe;
        this.maMayBay = maMayBay;
        this.giaCoBan = giaCoBan;
        this.trangThai = TRANG_THAI_CHUA_BAY;
        soChuyenBay++;
    }
    
    // Static methods
    public static int getSoChuyenBay() {
        return soChuyenBay;
    }
    
    public static void resetSoChuyenBay() {
        soChuyenBay = 0;
    }
    
    public static double tinhKhoangCach(String diemDi, String diemDen) {
        // Giả lập tính khoảng cách
        return Math.abs(diemDi.hashCode() - diemDen.hashCode()) % 5000 + 100;
    }
    
    // Business methods
    public boolean conGheTrong() {
        return soGheTrong > 0;
    }
    
    public boolean datGhe() {
        if (conGheTrong()) {
            soGheTrong--;
            return true;
        }
        return false;
    }
    
    public boolean huyGhe() {
        if (soGheTrong < soGhe) {
            soGheTrong++;
            return true;
        }
        return false;
    }
    
    // Getters and Setters
    public String getMaChuyen() { return maChuyen; }
    public void setMaChuyen(String maChuyen) { this.maChuyen = maChuyen; }
    public String getDiemDi() { return diemDi; }
    public void setDiemDi(String diemDi) { this.diemDi = diemDi; }
    public String getDiemDen() { return diemDen; }
    public void setDiemDen(String diemDen) { this.diemDen = diemDen; }
    public Date getGioKhoiHanh() { return gioKhoiHanh; }
    public void setGioKhoiHanh(Date gioKhoiHanh) { this.gioKhoiHanh = gioKhoiHanh; }
    public Date getGioDen() { return gioDen; }
    public void setGioDen(Date gioDen) { this.gioDen = gioDen; }
    public int getSoGhe() { return soGhe; }
    public void setSoGhe(int soGhe) { this.soGhe = soGhe; }
    public int getSoGheTrong() { return soGheTrong; }
    public void setSoGheTrong(int soGheTrong) { this.soGheTrong = soGheTrong; }
    public String getMaMayBay() { return maMayBay; }
    public void setMaMayBay(String maMayBay) { this.maMayBay = maMayBay; }
    public double getGiaCoBan() { return giaCoBan; }
    public void setGiaCoBan(double giaCoBan) { this.giaCoBan = giaCoBan; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}
