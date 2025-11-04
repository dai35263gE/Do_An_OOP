/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.mycompany.QuanLyVeMayBay;

/**
 *
 * @author HP
 */
// File: MayBay.java

import java.util.Date;

public class MayBay {
    private String maMayBay;
    private String tenMayBay;
    private String hangSanXuat;
    private int soCho;
    private int namSanXuat;
    private Date ngayBaoTri;
    private String trangThai;
    private double tocDoToiDa;
    private double tamBay;
    
    public static final String TT_SAN_SANG = "SẴN_SÀNG";
    public static final String TT_BAO_TRI = "BẢO_TRÌ";
    public static final String TT_HONG = "HỎNG";
    
    public MayBay(String maMayBay, String tenMayBay, String hangSanXuat, 
                  int soCho, int namSanXuat, double tocDoToiDa, double tamBay) {
        this.maMayBay = maMayBay;
        this.tenMayBay = tenMayBay;
        this.hangSanXuat = hangSanXuat;
        this.soCho = soCho;
        this.namSanXuat = namSanXuat;
        this.tocDoToiDa = tocDoToiDa;
        this.tamBay = tamBay;
        this.trangThai = TT_SAN_SANG;
        this.ngayBaoTri = new Date();
    }
    
    public boolean kiemTraBaoTri() {
        Date now = new Date();
        long diff = now.getTime() - ngayBaoTri.getTime();
        long days = diff / (24 * 60 * 60 * 1000);
        return days > 180; // Cần bảo trì sau 6 tháng
    }
    
    // Getters and Setters
    public String getMaMayBay() { return maMayBay; }
    public String getTenMayBay() { return tenMayBay; }
    public String getHangSanXuat() { return hangSanXuat; }
    public int getSoCho() { return soCho; }
    public int getNamSanXuat() { return namSanXuat; }
    public Date getNgayBaoTri() { return ngayBaoTri; }
    public String getTrangThai() { return trangThai; }
    public double getTocDoToiDa() { return tocDoToiDa; }
    public double getTamBay() { return tamBay; }
    
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public void setNgayBaoTri(Date ngayBaoTri) { this.ngayBaoTri = ngayBaoTri; }
}