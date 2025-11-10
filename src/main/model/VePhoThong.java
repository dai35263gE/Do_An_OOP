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
    private boolean hanhLyXachTay;  // Tối đa 7kg
    private int soKgHanhLyKyGui;
    private String loaiGhe; // Cửa sổ/ lối đi /giữa
    private boolean doAn; 

    public static final int SO_KG_MIEN_PHI = 10;//kg

    public static double hsg = 1.2;
    
    public VePhoThong(String maKH, String maVe, Date ngayBay, double giaVe, String maChuyen, String soGhe,boolean hanhLyXachTay, int soKgHanhLyKyGui, String loaiGhe, boolean doAn) {
        super(maKH, maVe, ngayBay, giaVe, maChuyen, soGhe);
        this.hanhLyXachTay = hanhLyXachTay;
        this.soKgHanhLyKyGui = soKgHanhLyKyGui;
        this.loaiGhe = loaiGhe;
        this.doAn = doAn;
    }
    public VePhoThong(String maKH, String maVe, Date ngayBay, double giaVe, String maChuyen, String soGhe,boolean hanhLyXachTay, int soKgHanhLyKyGui, String loaiGhe, boolean doAn, String  trangThai) {
        super(maKH, maVe, ngayBay, giaVe, maChuyen, soGhe);
        this.hanhLyXachTay = hanhLyXachTay;
        this.soKgHanhLyKyGui = soKgHanhLyKyGui;
        this.loaiGhe = loaiGhe;
        this.doAn = doAn;
        this.trangThai = trangThai;
    }
    
    @Override
    public double tinhThue() {
        return tinhTongTien()*VeMayBay.thue;
    }
    
    @Override
    public String loaiVe() {
        return "VePhoThong";
    }
    
    
    @Override
    public double tinhTongTien() {
        return (giaVe*hsg + getPhiHanhLy());
    }
    
    // Getters and Setters
    public boolean isHanhLyXachTay() { return hanhLyXachTay; }
    public void setHanhLyXachTay(boolean hanhLyXachTay) { this.hanhLyXachTay = hanhLyXachTay; }
    public int getSoKgHanhLyKyGui() { return soKgHanhLyKyGui; }
    public void setSoKgHanhLyKyGui(int soKgHanhLyKyGui) { this.soKgHanhLyKyGui = soKgHanhLyKyGui; }
    public double getPhiHanhLy() { return (soKgHanhLyKyGui - VePhoThong.SO_KG_MIEN_PHI) > 0 ? (soKgHanhLyKyGui - VePhoThong.SO_KG_MIEN_PHI)*VeMayBay.PHI_HANH_LY : 0; }
    public String getLoaiGhe() { return loaiGhe; }
    public void setLoaiGhe(String loaiGhe) { this.loaiGhe = loaiGhe; }
    public boolean isDoAn() { return doAn; }
    public void setDoAn(boolean doAn) { this.doAn = doAn; }
}