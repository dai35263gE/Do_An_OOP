/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package model;

/**
 *
 * @author HP
 */
// File: VeMayBay.java

import java.util.Date;
import java.util.regex.Pattern;

public abstract class VeMayBay implements Comparable<VeMayBay> {
    protected String maVe;
    protected String maKH;          // THÊM: Liên kết với khách hàng
    protected String hoTenKH;
    protected String cmnd;
    protected Date ngayBay;
    protected double giaVe;
    protected String maChuyen;
    protected String soGhe;
    protected String trangThai;
    protected Date ngayDat;
    protected String maKhuyenMai;
    
    // Constants
    public static final String TRANG_THAI_DAT = "DAT";
    public static final String TRANG_THAI_HUY = "HUY";
    public static final String TRANG_THAI_HOAN_TAT = "HOAN TAT";
    public static final String TRANG_THAI_DA_BAY = "DA BAY";
    
    // Regex patterns for validation
    private static final Pattern MA_VE_PATTERN = Pattern.compile("^(VG|VP|VT)[0-9]{3}$");
    private static final Pattern SO_GHE_PATTERN = Pattern.compile("^[0-9]{1,2}[A-Z]$");
    
    // CONSTRUCTOR CHÍNH - THÊM maKH
    public VeMayBay(String maVe, String maKH, String hoTenKH, String cmnd, Date ngayBay, 
                   double giaVe, String maChuyen, String soGhe) {
        setMaVe(maVe);
        setMaKH(maKH);
        setHoTenKH(hoTenKH);
        setCmnd(cmnd);
        setNgayBay(ngayBay);
        setGiaVe(giaVe);
        setmaChuyen(maChuyen);
        setSoGhe(soGhe);
        
        this.trangThai = TRANG_THAI_HOAN_TAT;
        this.ngayDat = new Date();
    }
    
    // OVERLOAD CONSTRUCTOR để tương thích code cũ
    public VeMayBay(String maVe, String hoTenKH, String cmnd, Date ngayBay, 
                   double giaVe, String maChuyen, String soGhe) {
        this(maVe, null, hoTenKH, cmnd, ngayBay, giaVe, maChuyen, soGhe);
    }
    
    // Abstract methods - ĐA HÌNH
    public abstract double tinhThue();
    public abstract String loaiVe();
    public abstract String chiTietLoaiVe();
    public abstract double tinhTongTien();
    
    // Template method pattern - CẢI THIỆN
    public final void inThongTinVe() {
        System.out.println("=== THÔNG TIN VÉ MÁY BAY ===");
        System.out.println("Mã vé: " + maVe);
        System.out.println("Mã KH: " + (maKH != null ? maKH : "N/A"));
        System.out.println("Loại vé: " + loaiVe());
        System.out.println("Hành khách: " + hoTenKH);
        System.out.println("CMND: " + cmnd);
        System.out.println("Chuyến bay: " + maChuyen);
        System.out.println("Ngày bay: " + ngayBay);
        System.out.println("Ghế: " + soGhe);
        System.out.println("Giá vé: " + String.format("%,.0f VND", giaVe));
        System.out.println("Thuế: " + String.format("%,.0f VND", tinhThue()));
        System.out.println("Tổng tiền: " + String.format("%,.0f VND", tinhTongTien()));
        System.out.println("Chi tiết: " + chiTietLoaiVe());
        System.out.println("Trạng thái: " + trangThai);
        System.out.println("Ngày đặt: " + ngayDat);
    }
    
    // BUSINESS METHODS
    public boolean coTheHuy() {
        Date now = new Date();
        if (ngayBay == null) return true;
        
        long diff = ngayBay.getTime() - now.getTime();
        long hours = diff / (1000 * 60 * 60);
        
        return hours > 24; // Có thể hủy trước 24h
    }
    
    public boolean coTheDoi() {
        Date now = new Date();
        if (ngayBay == null) return true;
        
        long diff = ngayBay.getTime() - now.getTime();
        long hours = diff / (1000 * 60 * 60);
        
        return hours > 48; // Có thể đổi trước 48h
    }
    
    public void huyVe() throws IllegalStateException {
        if (!coTheHuy()) {
            throw new IllegalStateException("Không thể hủy vé trước giờ bay 24h");
        }
        if (trangThai.equals(TRANG_THAI_HOAN_TAT)) {
            throw new IllegalStateException("Vé đã hoàn tất, không thể hủy");
        }
        this.trangThai = TRANG_THAI_HUY;
    }
    
    public void hoanTatVe() {
        this.trangThai = TRANG_THAI_HOAN_TAT;
    }
    
    public void capNhatTrangThaiBay() {
        Date now = new Date();
        if (ngayBay != null && now.after(ngayBay) && trangThai.equals(TRANG_THAI_HOAN_TAT)) {
            this.trangThai = TRANG_THAI_DA_BAY;
        }
    }
    
    // VALIDATION METHODS
    public static boolean validateMaVe(String maVe) {
        return maVe != null && MA_VE_PATTERN.matcher(maVe).matches();
    }
    
    public static boolean validateSoGhe(String soGhe) {
        return soGhe != null && SO_GHE_PATTERN.matcher(soGhe).matches();
    }
    
    public boolean kiemTraVeHopLe() {
        return maVe != null && !maVe.trim().isEmpty() &&
               hoTenKH != null && !hoTenKH.trim().isEmpty() &&
               cmnd != null && !cmnd.trim().isEmpty() &&
               ngayBay != null && ngayBay.after(new Date()) &&
               giaVe > 0 &&
               maChuyen != null && !maChuyen.trim().isEmpty() &&
               soGhe != null && !soGhe.trim().isEmpty();
    }
    
    @Override
    public int compareTo(VeMayBay other) {
        if (this.ngayBay == null || other.ngayBay == null) {
            return this.maVe.compareTo(other.maVe);
        }
        return this.ngayBay.compareTo(other.ngayBay);
    }
    
    @Override
    public String toString() {
        return String.format("VeMayBay[%s: %s - %s - %s]", 
                           maVe, hoTenKH, maChuyen, trangThai);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        VeMayBay that = (VeMayBay) obj;
        return maVe != null && maVe.equals(that.maVe);
    }
    
    @Override
    public int hashCode() {
        return maVe != null ? maVe.hashCode() : 0;
    }
    
    // Getters and Setters với VALIDATION
    public String getMaVe() { return maVe; }
    public void setMaVe(String maVe) { 
        if (maVe == null || maVe.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã vé không được để trống");
        }
        if (!validateMaVe(maVe.trim())) {
            throw new IllegalArgumentException("Mã vé không hợp lệ. Format: VG001, VP001, VT001");
        }
        this.maVe = maVe.trim().toUpperCase();
    }
    
    public String getMaKH() { return maKH; }                    // THÊM
    public void setMaKH(String maKH) { 
        if (maKH != null && maKH.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã KH không được để trống");
        }
        this.maKH = maKH != null ? maKH.trim().toUpperCase() : null;
    }
    
    public String getHoTenKH() { return hoTenKH; }
    public void setHoTenKH(String hoTenKH) { 
        if (hoTenKH == null || hoTenKH.trim().isEmpty()) {
            throw new IllegalArgumentException("Họ tên KH không được để trống");
        }
        this.hoTenKH = hoTenKH.trim();
    }
    
    public String getCmnd() { return cmnd; }
    public void setCmnd(String cmnd) { 
        if (cmnd == null || cmnd.trim().isEmpty()) {
            throw new IllegalArgumentException("CMND không được để trống");
        }
        this.cmnd = cmnd.trim();
    }
    
    public Date getNgayBay() { return ngayBay; }
    public void setNgayBay(Date ngayBay) { 
        this.ngayBay = ngayBay;
    }
    
    public double getGiaVe() { return giaVe; }
    public void setGiaVe(double giaVe) { 
        if (giaVe <= 0) {
            throw new IllegalArgumentException("Giá vé phải lớn hơn 0");
        }
        this.giaVe = giaVe;
    }
    
    public String getmaChuyen() { return maChuyen; }
    public void setmaChuyen(String maChuyen) { 
        if (maChuyen == null || maChuyen.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã chuyến bay không được để trống");
        }
        this.maChuyen = maChuyen.trim().toUpperCase();
    }
    
    public String getSoGhe() { return soGhe; }
    public void setSoGhe(String soGhe) { 
        if (soGhe == null || soGhe.trim().isEmpty()) {
            throw new IllegalArgumentException("Số ghế không được để trống");
        }
        if (!validateSoGhe(soGhe.trim())) {
            throw new IllegalArgumentException("Số ghế không hợp lệ. Format: 1A, 12B, 25C");
        }
        this.soGhe = soGhe.trim().toUpperCase();
    }
    
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { 
        if (!trangThai.equals(TRANG_THAI_DAT) && 
            !trangThai.equals(TRANG_THAI_HUY) && 
            !trangThai.equals(TRANG_THAI_HOAN_TAT) &&
            !trangThai.equals(TRANG_THAI_DA_BAY)) {
            throw new IllegalArgumentException("Trạng thái vé không hợp lệ");
        }
        this.trangThai = trangThai;
    }
    
    public Date getNgayDat() { return ngayDat; }
    public void setNgayDat(Date ngayDat) { 
        if (ngayDat != null && ngayDat.after(new Date())) {
            throw new IllegalArgumentException("Ngày đặt không thể ở tương lai");
        }
        this.ngayDat = ngayDat;
    }
    
    public String getMaKhuyenMai() { return maKhuyenMai; }
    public void setMaKhuyenMai(String maKhuyenMai) { 
        this.maKhuyenMai = maKhuyenMai;
    }
}