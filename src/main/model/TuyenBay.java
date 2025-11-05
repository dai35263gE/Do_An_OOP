/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package model;

/**
 *
 * @author HP
 */
// File: TuyenBay.java

public class TuyenBay {
    private String maTuyen;
    private String sanBayDi;
    private String sanBayDen;
    private double khoangCach;
    private int thoiGianBay; // phút
    private double giaCoBan;
    private int soChuyenMoiNgay;
    private String moTa;
    
    public TuyenBay(String maTuyen, String sanBayDi, String sanBayDen, 
                    double khoangCach, int thoiGianBay, double giaCoBan) {
        this.maTuyen = maTuyen;
        this.sanBayDi = sanBayDi;
        this.sanBayDen = sanBayDen;
        this.khoangCach = khoangCach;
        this.thoiGianBay = thoiGianBay;
        this.giaCoBan = giaCoBan;
        this.soChuyenMoiNgay = 3; // Mặc định
    }
    
    public double tinhGiaTheoLoaiVe(String loaiVe) {
        switch (loaiVe) {
            case "THƯƠNG GIA": return giaCoBan * 2.5;
            case "PHỔ THÔNG": return giaCoBan * 1.2;
            case "TIẾT KIỆM": return giaCoBan * 0.8;
            default: return giaCoBan;
        }
    }
    
    // Getters and Setters
    public String getMaTuyen() { return maTuyen; }
    public String getSanBayDi() { return sanBayDi; }
    public String getSanBayDen() { return sanBayDen; }
    public double getKhoangCach() { return khoangCach; }
    public int getThoiGianBay() { return thoiGianBay; }
    public double getGiaCoBan() { return giaCoBan; }
    public int getSoChuyenMoiNgay() { return soChuyenMoiNgay; }
    public String getMoTa() { return moTa; }
    
    public void setSoChuyenMoiNgay(int soChuyenMoiNgay) { this.soChuyenMoiNgay = soChuyenMoiNgay; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
}