/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package model;

import java.util.Date;
import java.util.regex.Pattern;

public abstract class VeMayBay implements Comparable<VeMayBay> {
    protected String maVe;
    protected String maHD;
    protected String maKH;
    protected String hoTenKH;
    protected String cmnd;
    protected Date ngayBay;
    protected double giaVe;
    protected String maChuyen;
    protected String soGhe;
    protected String trangThai;
    protected Date ngayDat;
    
    // Constants - Sửa thành enum để type-safe
    public static final String TRANG_THAI_DAT = "DAT";
    public static final String TRANG_THAI_HUY = "HUY";
    public static final String TRANG_THAI_HOAN_TAT = "HOAN TAT";
    public static final String TRANG_THAI_DA_BAY = "DA BAY";
    
    // Thêm hằng số cho validation
    private static final int THOI_GIAN_HUY_TOI_THIEU = 24;
    private static final int THOI_GIAN_DOI_TOI_THIEU = 48; 
    
    // Regex patterns for validation
    private static final Pattern MA_VE_PATTERN = Pattern.compile("^(VG|VP|VT)[0-9]{3}$");
    private static final Pattern SO_GHE_PATTERN = Pattern.compile("^[0-9]{1,2}[A-Z]$");
    private static final Pattern CMND_PATTERN = Pattern.compile("^[0-9]{12}$");
    
    // CONSTRUCTOR CHÍNH
    public VeMayBay(String maVe, String maKH, String hoTenKH, String cmnd, Date ngayBay, double giaVe, String maChuyen,  String soGhe,String trangThai) {
        setMaVe(maVe);
        setMaKH(maKH);
        setHoTenKH(hoTenKH);
        setCmnd(cmnd);
        setNgayBay(ngayBay);
        setGiaVe(giaVe);
        setMaChuyen(maChuyen);
        setTrangThai(trangThai); // Đặt trạng thái trước khi setSoGhe
        setSoGhe(soGhe);
        this.ngayDat = new Date();
    }
    

    
    // Abstract methods
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
        System.out.println("Ngày bay: " + (ngayBay != null ? ngayBay : "N/A"));
        System.out.println("Ghế: " + soGhe);
        System.out.println("Giá vé: " + String.format("%,.0f VND", giaVe));
        System.out.println("Thuế: " + String.format("%,.0f VND", tinhThue()));
        System.out.println("Tổng tiền: " + String.format("%,.0f VND", tinhTongTien()));
        System.out.println("Chi tiết: " + chiTietLoaiVe());
        System.out.println("Trạng thái: " + trangThai);
        System.out.println("Ngày đặt: " + (ngayDat != null ? ngayDat : "N/A"));
    }
    
    // BUSINESS METHODS - CẢI THIỆN
    public boolean coTheHuy() {
        if (ngayBay == null) return true;
        if (!trangThai.equals(TRANG_THAI_DAT) && !trangThai.equals(TRANG_THAI_HOAN_TAT)) {
            return false; // Chỉ có thể hủy vé đang đặt hoặc hoàn tất
        }
        
        long diff = ngayBay.getTime() - System.currentTimeMillis();
        long hours = diff / (1000 * 60 * 60);
        
        return hours > THOI_GIAN_HUY_TOI_THIEU;
    }
    
    public boolean coTheDoi() {
        if (ngayBay == null) return true;
        if (!trangThai.equals(TRANG_THAI_DAT) && !trangThai.equals(TRANG_THAI_HOAN_TAT)) {
            return false;
        }
        
        long diff = ngayBay.getTime() - System.currentTimeMillis();
        long hours = diff / (1000 * 60 * 60);
        
        return hours > THOI_GIAN_DOI_TOI_THIEU;
    }
    
    public void huyVe() throws IllegalStateException {
        if (!coTheHuy()) {
            throw new IllegalStateException("Da qua thoi gian huy ve.");
        }
        this.trangThai = TRANG_THAI_HUY;
    }
    
    public void hoanTatVe() {
        if (trangThai.equals(TRANG_THAI_HUY) ||  trangThai.equals(TRANG_THAI_DA_BAY)) {
            throw new IllegalStateException("Khong the hoan tat.");
        }
        this.trangThai = TRANG_THAI_HOAN_TAT;
    }
    
    public void capNhatTrangThaiBay() {
        Date now = new Date();
        if (ngayBay != null && now.after(ngayBay) && trangThai.equals(TRANG_THAI_HOAN_TAT)) {
            this.trangThai = TRANG_THAI_DA_BAY;
        }
    }
    
    // VALIDATION METHODS - BỔ SUNG
    public static boolean validateMaVe(String maVe) {
        return maVe != null && MA_VE_PATTERN.matcher(maVe).matches();
    }
    
    public static boolean validateSoGhe(String soGhe) {
        return soGhe != null && SO_GHE_PATTERN.matcher(soGhe).matches();
    }
    
    public static boolean validateCmnd(String cmnd) {
        return cmnd != null && CMND_PATTERN.matcher(cmnd).matches();
    }
    private boolean isTrangThaiHopLe(String trangThai) {
        return trangThai != null && 
               (trangThai.equals(TRANG_THAI_DAT) || 
                trangThai.equals(TRANG_THAI_HUY) || 
                trangThai.equals(TRANG_THAI_HOAN_TAT) ||
                trangThai.equals(TRANG_THAI_DA_BAY));
    }
    
    public boolean kiemTraVeHopLe() {
        return validateMaVe(maVe) &&
            hoTenKH != null && !hoTenKH.trim().isEmpty() && validateCmnd(cmnd) && ngayBay != null && ngayBay.after(new Date()) &&
            giaVe > 0 && maChuyen != null && !maChuyen.trim().isEmpty() && validateSoGhe(soGhe) && isTrangThaiHopLe(trangThai);
    }
    @Override
    public int compareTo(VeMayBay other) {
        if (this.ngayBay == null && other.ngayBay == null) {
            return this.maVe.compareTo(other.maVe);
        }
        if (this.ngayBay == null) return -1;
        if (other.ngayBay == null) return 1;
        return this.ngayBay.compareTo(other.ngayBay);
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
    
    // Getters and Setters với VALIDATION cải tiến
    public String getMaVe() { return maVe; }
    public void setMaVe(String maVe) { 
        if (!validateMaVe(maVe.trim())) {
            throw new IllegalArgumentException("Ma ve khong hop le. Format: VG001, VP001, VT001");
        }
        this.maVe = maVe.trim().toUpperCase();
    }
    
    public String getMaKH() { return maKH; }
    public void setMaKH(String maKH) { 
        this.maKH = maKH != null ? maKH.trim().toUpperCase() : null;
    }
    
    public String getHoTenKH() { return hoTenKH; }
    public void setHoTenKH(String hoTenKH) { 
        this.hoTenKH = hoTenKH.trim();
    }
    
    public String getCmnd() { return cmnd; }
    public void setCmnd(String cmnd) { 
        if (cmnd == null || cmnd.trim().isEmpty()) {
            throw new IllegalArgumentException("CMND khong duoc de trong");
        }
        if (!validateCmnd(cmnd.trim())) {
            throw new IllegalArgumentException("CMND khong hop le.");
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
            throw new IllegalArgumentException("Gia ve phai lon hon 0");
        }
        this.giaVe = giaVe;
    }
    
    public String getMaChuyen() { return maChuyen; }
    public void setMaChuyen(String maChuyen) { 
        if (maChuyen == null || maChuyen.trim().isEmpty()) {
            throw new IllegalArgumentException("Ma chuyen bay khong duoc de trong");
        }
        this.maChuyen = maChuyen.trim().toUpperCase();
    }
    
    public String getSoGhe() { return soGhe; }
    public void setSoGhe(String soGhe) { 
        if (soGhe == null || soGhe.trim().isEmpty()) {
            throw new IllegalArgumentException("So ghe khong duoc de trong");
        }
        if (!validateSoGhe(soGhe.trim())) {
            throw new IllegalArgumentException("So ghe khong hop le. Format: 1A, 12B, 25C");
        }
        this.soGhe = soGhe.trim().toUpperCase();
    }
    
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { 
        if (!isTrangThaiHopLe(trangThai)) {
            throw new IllegalArgumentException("Trang thai ve khong hop le, phai la: " + 
                TRANG_THAI_DAT + ", " + TRANG_THAI_HUY + ", " + 
                TRANG_THAI_HOAN_TAT + ", " + TRANG_THAI_DA_BAY);
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
    public boolean daBay() {
        return TRANG_THAI_DA_BAY.equals(trangThai);
    }
    
    public boolean coTheSuDung() {
        return !TRANG_THAI_HUY.equals(trangThai) && !TRANG_THAI_DA_BAY.equals(trangThai);
    }
    public String getMaHoaDon() { return maHD; }
    public void setMaHoaDon(String maHD) { 
        this.maHD = maHD;
    }
}