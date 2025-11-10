/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package model;
/**
 *
 * @author HP
 */
// File: VeTietKiem.java

import java.util.Date;

public class VeTietKiem extends VeMayBay {
    private boolean hanhLyXachTay; // tối đa 7kg

    public static double hsg = 1.0;
    
    public VeTietKiem(String maKH,String maVe,Date ngayBay, double giaVe, String maChuyenBay, String soGhe,boolean hanhLyXachTay) {
        super(maKH,maVe, ngayBay, giaVe, maChuyenBay, soGhe);
        this.hanhLyXachTay = hanhLyXachTay; 
    }

    public VeTietKiem(String maKH,String maVe,Date ngayBay, double giaVe, String maChuyenBay, String soGhe,boolean hanhLyXachTay, String trangThai) {
        super(maKH,maVe, ngayBay, giaVe, maChuyenBay, soGhe);
        this.trangThai = trangThai;
    }
    
    @Override
    public double tinhThue() {
        return tinhTongTien() * VeMayBay.thue; 
    }
    
    @Override
    public String loaiVe() {
        return "VeTietKiem";
    }
    
    @Override
    public double tinhTongTien() {
        return giaVe*hsg;
    }
    
    // Getters and Setters
    public boolean hanhLyXachTay() { return this.hanhLyXachTay; }
}
