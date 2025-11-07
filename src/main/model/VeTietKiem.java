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
    private int soGioDatTruoc;
    private double tyLeGiam;
    private boolean hoanDoi;
    private double phiHoanDoi;
    private String dieuKienGia;
    
    public VeTietKiem(String maKH,String maVe,Date ngayBay, double giaVe, String maChuyenBay, String soGhe,int soGioDatTruoc, double tyLeGiam, boolean hoanDoi,double phiHoanDoi, String dieuKienGia) {
        super(maKH,maVe, ngayBay, giaVe, maChuyenBay, soGhe);
        this.soGioDatTruoc = soGioDatTruoc;
        this.tyLeGiam = tyLeGiam;
        this.hoanDoi = hoanDoi;
        this.phiHoanDoi = phiHoanDoi;
        this.dieuKienGia = dieuKienGia;
    }
    
    @Override
    public double tinhThue() {
        double giaSauGiam = giaVe * (1 - tyLeGiam);
        return giaSauGiam * 0.05; // Thuế 5% trên giá sau giảm
    }
    
    @Override
    public String loaiVe() {
        return "VeTietKiem";
    }
    
    @Override
    public String chiTietLoaiVe() {
        return String.format("Đặt trước: %d giờ, Giảm: %.1f%%, Hoàn đổi: %b, Điều kiện: %s",
                           soGioDatTruoc, tyLeGiam * 100, hoanDoi, dieuKienGia);
    }
    
    @Override
    public double tinhTongTien() {
        double giaSauGiam = giaVe * (1 - tyLeGiam);
        return giaSauGiam + tinhThue();
    }
    
    // Getters and Setters
    public int getSoGioDatTruoc() { return soGioDatTruoc; }
    public void setSoGioDatTruoc(int soGioDatTruoc) { this.soGioDatTruoc = soGioDatTruoc; }
    public double getTyLeGiam() { return tyLeGiam; }
    public void setTyLeGiam(double tyLeGiam) { this.tyLeGiam = tyLeGiam; }
    public boolean isHoanDoi() { return hoanDoi; }
    public void setHoanDoi(boolean hoanDoi) { this.hoanDoi = hoanDoi; }
    public double getPhiHoanDoi() { return phiHoanDoi; }
    public void setPhiHoanDoi(double phiHoanDoi) { this.phiHoanDoi = phiHoanDoi; }
    public String getDieuKienGia() { return dieuKienGia; }
    public void setDieuKienGia(String dieuKienGia) { this.dieuKienGia = dieuKienGia; }
}
