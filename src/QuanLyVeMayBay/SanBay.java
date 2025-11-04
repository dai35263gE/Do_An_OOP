/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.mycompany.QuanLyVeMayBay;

/**
 *
 * @author HP
 */
// File: SanBay.java

public class SanBay {
    private String maSanBay;
    private String tenSanBay;
    private String thanhPho;
    private String quocGia;
    private String maIATA;
    private String maICAO;
    private int soDuyenBay;
    private double kinhDo;
    private double viDo;
    private String moTa;
    
    public SanBay(String maSanBay, String tenSanBay, String thanhPho, 
                  String quocGia, String maIATA, String maICAO) {
        this.maSanBay = maSanBay;
        this.tenSanBay = tenSanBay;
        this.thanhPho = thanhPho;
        this.quocGia = quocGia;
        this.maIATA = maIATA;
        this.maICAO = maICAO;
        this.soDuyenBay = 1;
    }
    
    // Getters and Setters
    public String getMaSanBay() { return maSanBay; }
    public String getTenSanBay() { return tenSanBay; }
    public String getThanhPho() { return thanhPho; }
    public String getQuocGia() { return quocGia; }
    public String getMaIATA() { return maIATA; }
    public String getMaICAO() { return maICAO; }
    public int getSoDuyenBay() { return soDuyenBay; }
    public double getKinhDo() { return kinhDo; }
    public double getViDo() { return viDo; }
    public String getMoTa() { return moTa; }
    
    public void setSoDuyenBay(int soDuyenBay) { this.soDuyenBay = soDuyenBay; }
    public void setKinhDo(double kinhDo) { this.kinhDo = kinhDo; }
    public void setViDo(double viDo) { this.viDo = viDo; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
}
