/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package model;

/**
 *
 * @author HP
 */
// File: VePhoThong.java

import java.util.Date;

public class VePhoThong extends VeMayBay {
    private boolean hanhLyXachTay;
    private int soKgHanhLyKyGui;
    private double phiHanhLy;
    private String loaiGhe; // Cửa sổ, lối đi
    private boolean doAn;
    
    public VePhoThong(String maVe, String hoTenKH, String cmnd, Date ngayBay, 
                     double giaVe, String maChuyenBay, String soGhe,
                     boolean hanhLyXachTay, int soKgHanhLyKyGui, 
                     double phiHanhLy, String loaiGhe, boolean doAn) {
        super(maVe, hoTenKH, cmnd, ngayBay, giaVe, maChuyenBay, soGhe);
        this.hanhLyXachTay = hanhLyXachTay;
        this.soKgHanhLyKyGui = soKgHanhLyKyGui;
        this.phiHanhLy = phiHanhLy;
        this.loaiGhe = loaiGhe;
        this.doAn = doAn;
    }
    
    @Override
    public double tinhThue() {
        double thue = giaVe * 0.08; // VAT 8%
        if (soKgHanhLyKyGui > 0) {
            thue += phiHanhLy * 0.1; // Thuế hành lý 10%
        }
        return thue;
    }
    
    @Override
    public String loaiVe() {
        return "VePhoThong";
    }
    
    @Override
    public String chiTietLoaiVe() {
        return String.format("Hành lý xách tay: %b, Ký gửi: %dkg, Loại ghế: %s, Đồ ăn: %b",
                           hanhLyXachTay, soKgHanhLyKyGui, loaiGhe, doAn);
    }
    
    @Override
    public double tinhTongTien() {
        return giaVe + phiHanhLy + tinhThue();
    }
    
    // Getters and Setters
    public boolean isHanhLyXachTay() { return hanhLyXachTay; }
    public void setHanhLyXachTay(boolean hanhLyXachTay) { this.hanhLyXachTay = hanhLyXachTay; }
    public int getSoKgHanhLyKyGui() { return soKgHanhLyKyGui; }
    public void setSoKgHanhLyKyGui(int soKgHanhLyKyGui) { this.soKgHanhLyKyGui = soKgHanhLyKyGui; }
    public double getPhiHanhLy() { return phiHanhLy; }
    public void setPhiHanhLy(double phiHanhLy) { this.phiHanhLy = phiHanhLy; }
    public String getLoaiGhe() { return loaiGhe; }
    public void setLoaiGhe(String loaiGhe) { this.loaiGhe = loaiGhe; }
    public boolean isDoAn() { return doAn; }
    public void setDoAn(boolean doAn) { this.doAn = doAn; }
}