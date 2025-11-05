/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package model;
/**
 *
 * @author HP
 */
// File: VeThuongGia.java

import java.util.Date;

public class VeThuongGia extends VeMayBay {
    private String dichVuDacBiet;
    private double phuThu;
    private int soKgHanhLyMienPhi;
    private boolean phongChoVIP;
    private String loaiDoUong;
    
    public VeThuongGia(String maVe, String hoTenKH, String cmnd, Date ngayBay, 
                      double giaVe, String maChuyenBay, String soGhe,
                      String dichVuDacBiet, double phuThu, int soKgHanhLyMienPhi,
                      boolean phongChoVIP, String loaiDoUong) {
        super(maVe, hoTenKH, cmnd, ngayBay, giaVe, maChuyenBay, soGhe);
        this.dichVuDacBiet = dichVuDacBiet;
        this.phuThu = phuThu;
        this.soKgHanhLyMienPhi = soKgHanhLyMienPhi;
        this.phongChoVIP = phongChoVIP;
        this.loaiDoUong = loaiDoUong;
    }
    
    @Override
    public double tinhThue() {
        return (giaVe * 0.1) + (phuThu * 0.05) + 200000; // VAT 10% + thuế dịch vụ
    }
    
    @Override
    public String loaiVe() {
        return "VeThuongGia";
    }
    
    @Override
    public String chiTietLoaiVe() {
        return String.format("Dịch vụ: %s, Phụ thu: %.2f, Hành lý: %dkg, VIP: %b",
                           dichVuDacBiet, phuThu, soKgHanhLyMienPhi, phongChoVIP);
    }
    
    @Override
    public double tinhTongTien() {
        return giaVe + phuThu + tinhThue();
    }
    
    // Getters and Setters
    public String getDichVuDacBiet() { return dichVuDacBiet; }
    public void setDichVuDacBiet(String dichVuDacBiet) { this.dichVuDacBiet = dichVuDacBiet; }
    public double getPhuThu() { return phuThu; }
    public void setPhuThu(double phuThu) { this.phuThu = phuThu; }
    public int getSoKgHanhLyMienPhi() { return soKgHanhLyMienPhi; }
    public void setSoKgHanhLyMienPhi(int soKgHanhLyMienPhi) { this.soKgHanhLyMienPhi = soKgHanhLyMienPhi; }
    public boolean isPhongChoVIP() { return phongChoVIP; }
    public void setPhongChoVIP(boolean phongChoVIP) { this.phongChoVIP = phongChoVIP; }
    public String getLoaiDoUong() { return loaiDoUong; }
    public void setLoaiDoUong(String loaiDoUong) { this.loaiDoUong = loaiDoUong; }
}
