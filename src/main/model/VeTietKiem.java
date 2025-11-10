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
    
    public VeTietKiem(String maKH,String maVe,Date ngayBay, double giaVe, String maChuyenBay, String soGhe,boolean hanhLyXachTay) {
        super(maKH,maVe, ngayBay, giaVe, maChuyenBay, soGhe);
        this.hanhLyXachTay = hanhLyXachTay; 
    }
    
    @Override
    public double tinhThue() {
        double giaSauGiam = giaVe * (1 - 0.1);
        return giaSauGiam * 0.05; // Thuế 5% trên giá sau giảm
    }
    
    @Override
    public String loaiVe() {
        return "VeTietKiem";
    }
    
    @Override
    public String chiTietLoaiVe() {
        return String.format("Hành lý xách tay: %s",
                           hanhLyXachTay);
    }
    
    @Override
    public double tinhTongTien() {
        double giaSauGiam = giaVe * (1 - 0.1);
        return giaSauGiam + tinhThue();
    }
    
    // Getters and Setters
    public boolean hanhLyXachTay() { return this.hanhLyXachTay; }
}
