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
    private double SoKgHanhLyKyGui;
    private boolean phongChoVIP;
    private String loaiDoUong;

    // Hành lý
    public static final int SO_KG_MIEN_PHI = 20;
    public static final int PHI_HANH_LY_THEM = 15000; // 15k/kg cho phần hành lý vượt quá 20kg
    
    // Gói hành lý: 25kg, 30kg, 35kg, 40kg
    public static final double GÓI_25KG = 25;
    public static final double GÓI_30KG = 30;
    public static final double GÓI_35KG = 35;
    public static final double GÓI_40KG = 40;

    public static double hsg = 2.0;
    
    public VeThuongGia(String maKH,String maVe, Date ngayBay, double giaVe, String maChuyen, String soGhe,String dichVuDacBiet, double phuThu,boolean phongChoVIP,double SoKgHanhLyKyGui, String loaiDoUong) {
        super( maKH,maVe, ngayBay, giaVe, maChuyen, soGhe);
        this.dichVuDacBiet = dichVuDacBiet;
        this.phuThu = phuThu;
        this.phongChoVIP = phongChoVIP;
        this.SoKgHanhLyKyGui = SoKgHanhLyKyGui;
        this.loaiDoUong = loaiDoUong;
    }
    public VeThuongGia(String maKH,String maVe, Date ngayBay, double giaVe, String maChuyen, String soGhe,String dichVuDacBiet, double phuThu,boolean phongChoVIP,double SoKgHanhLyKyGui, String loaiDoUong, String trangThai) {
        super( maKH,maVe, ngayBay, giaVe, maChuyen, soGhe);
        this.dichVuDacBiet = dichVuDacBiet;
        this.phuThu = phuThu;
        this.phongChoVIP = phongChoVIP;
        this.SoKgHanhLyKyGui = SoKgHanhLyKyGui;
        this.loaiDoUong = loaiDoUong;
        this.trangThai = trangThai;
    }
    
    @Override
    public double tinhThue() {
        return tinhTongTien()*VeMayBay.thue; // VAT 10% + thuế dịch vụ
    }
    
    @Override
    public String loaiVe() {
        return "VeThuongGia";
    }
    
    @Override
    public double tinhTongTien() {
        return (giaVe*hsg + phuThu+getPhiHanhLy());
    }
    
    // Getters and Setters
    public String getDichVuDacBiet() { return dichVuDacBiet; }
    public void setDichVuDacBiet(String dichVuDacBiet) { this.dichVuDacBiet = dichVuDacBiet; }
    public double getSoKgHanhLyKiGui() { return this.SoKgHanhLyKyGui; }
    public void setSoKgHanhLyKyGui(double dichVuDaSoKgHanhLyKyGui) { this.SoKgHanhLyKyGui = dichVuDaSoKgHanhLyKyGui; }
    public double getPhuThu() { return phuThu; }
    public void setPhuThu(double phuThu) { this.phuThu = phuThu; }
    public boolean isPhongChoVIP() { return phongChoVIP; }
    public void setPhongChoVIP(boolean phongChoVIP) { this.phongChoVIP = phongChoVIP; }
    public String getLoaiDoUong() { return loaiDoUong; }
    public void setLoaiDoUong(String loaiDoUong) { this.loaiDoUong = loaiDoUong; }
    public double getPhiHanhLy() {
        if (SoKgHanhLyKyGui <= SO_KG_MIEN_PHI) {
            return 0; // Miễn phí cho 20kg đầu
        }
        // Tính từng kg vượt quá 20kg, mỗi kg = 15k
        double kgVuotQua = SoKgHanhLyKyGui - SO_KG_MIEN_PHI;
        return kgVuotQua * PHI_HANH_LY_THEM;
    }
}
