/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package model;

/**
 * @author HP
 */
// File: ChuyenBay.java

import java.util.Date;

public class ChuyenBay {
    private String maChuyen;
    private String diemDi;
    private String diemDen;
    private Date gioKhoiHanh;
    private Date gioDen;
    private int soGhe;
    private int soGheTrong;
    private String maMayBay;
    private double giaCoBan;
    private String trangThai;
    
    // Static properties
    private static int soChuyenBay = 0;
    public static final String TRANG_THAI_CHUA_BAY = "CHƯA BAY";
    public static final String TRANG_THAI_DA_BAY = "ĐÃ BAY";
    public static final String TRANG_THAI_HUY = "HỦY";
    public static final String TRANG_THAI_DANG_BAY = "ĐANG BAY";
    
    // CONSTRUCTOR SỬA: Thêm số ghế trống làm tham số
    public ChuyenBay(String maChuyen, String diemDi, String diemDen, Date gioKhoiHanh, 
                    Date gioDen, int soGhe, int soGheTrong, String maMayBay, double giaCoBan) {
        this.maChuyen = maChuyen;
        this.diemDi = diemDi;
        this.diemDen = diemDen;
        this.gioKhoiHanh = gioKhoiHanh;
        this.gioDen = gioDen;
        this.soGhe = soGhe;
        
        // SỬA QUAN TRỌNG: Kiểm tra tính hợp lệ của số ghế trống
        if (soGheTrong < 0 || soGheTrong > soGhe) {
            throw new IllegalArgumentException("Số ghế trống không hợp lệ: " + soGheTrong);
        }
        this.soGheTrong = soGheTrong;
        
        this.maMayBay = maMayBay;
        this.giaCoBan = giaCoBan;
        this.trangThai = TRANG_THAI_CHUA_BAY;
        soChuyenBay++;
    }
    
    // OVERLOAD CONSTRUCTOR để tương thích với code cũ
    public ChuyenBay(String maChuyen, String diemDi, String diemDen, Date gioKhoiHanh, 
                    Date gioDen, int soGhe, String maMayBay, double giaCoBan) {
        this(maChuyen, diemDi, diemDen, gioKhoiHanh, gioDen, soGhe, soGhe, maMayBay, giaCoBan);
    }
    
    // Static methods
    public static int getSoChuyenBay() {
        return soChuyenBay;
    }
    
    public static void resetSoChuyenBay() {
        soChuyenBay = 0;
    }
    
    public static double tinhKhoangCach(String diemDi, String diemDen) {
        // CẢI THIỆN: Tính khoảng cách thực tế hơn
        if (diemDi == null || diemDen == null) {
            return 0;
        }
        
        // Giả lập khoảng cách dựa trên mã sân bay
        String maSanBayDi = diemDi.replaceAll(".*\\((.*)\\).*", "$1");
        String maSanBayDen = diemDen.replaceAll(".*\\((.*)\\).*", "$1");
        
        // Khoảng cách giả định giữa các sân bay phổ biến
        java.util.Map<String, Integer> distances = new java.util.HashMap<>();
        distances.put("HAN-SGN", 1160);
        distances.put("SGN-HAN", 1160);
        distances.put("HAN-DAD", 600);
        distances.put("DAD-HAN", 600);
        distances.put("SGN-DAD", 600);
        distances.put("DAD-SGN", 600);
        distances.put("HAN-CXR", 1080);
        distances.put("SGN-CXR", 400);
        distances.put("HAN-PQC", 1200);
        distances.put("SGN-PQC", 300);
        
        String key = maSanBayDi + "-" + maSanBayDen;
        return distances.getOrDefault(key, 500); // Mặc định 500km
    }
    
    // Business methods - ĐÃ SỬA LOGIC
    public boolean conGheTrong() {
        return soGheTrong > 0 && trangThai.equals(TRANG_THAI_CHUA_BAY);
    }
    
    public boolean datGhe() throws IllegalStateException {
        if (!conGheTrong()) {
            throw new IllegalStateException("Không còn ghế trống hoặc chuyến bay không thể đặt");
        }
        soGheTrong--;
        return true;
    }
    
    public boolean huyGhe() throws IllegalStateException {
        if (soGheTrong >= soGhe) {
            throw new IllegalStateException("Số ghế trống đã đạt tối đa");
        }
        if (trangThai.equals(TRANG_THAI_DA_BAY)) {
            throw new IllegalStateException("Không thể hủy ghế trên chuyến bay đã bay");
        }
        soGheTrong++;
        return true;
    }
    
    // THÊM PHƯƠNG THỨC MỚI
    public void capNhatTrangThaiBay() {
        Date now = new Date();
        if (now.before(gioKhoiHanh)) {
            trangThai = TRANG_THAI_CHUA_BAY;
        } else if (now.after(gioKhoiHanh) && now.before(gioDen)) {
            trangThai = TRANG_THAI_DANG_BAY;
        } else if (now.after(gioDen)) {
            trangThai = TRANG_THAI_DA_BAY;
        }
    }
    
    public double tinhThoiGianBay() {
        if (gioKhoiHanh == null || gioDen == null) {
            return 0;
        }
        long diff = gioDen.getTime() - gioKhoiHanh.getTime();
        return diff / (1000.0 * 60); // Trả về phút
    }
    
    public boolean kiemTraChuyenBayHopLe() {
        Date now = new Date();
        return gioKhoiHanh != null && 
               gioDen != null && 
               gioKhoiHanh.before(gioDen) && 
               gioKhoiHanh.after(now) &&
               soGheTrong >= 0 &&
               soGheTrong <= soGhe;
    }
    
    @Override
    public String toString() {
        return String.format("ChuyenBay[%s: %s -> %s, Ghế: %d/%d, Giá: %.0f]", 
                           maChuyen, diemDi, diemDen, soGhe - soGheTrong, soGhe, giaCoBan);
    }
    
    // Getters and Setters - THÊM VALIDATION
    public String getMaChuyen() { return maChuyen; }
    public void setMaChuyen(String maChuyen) { 
        if (maChuyen == null || maChuyen.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã chuyến không được để trống");
        }
        this.maChuyen = maChuyen; 
    }
    
    public String getDiemDi() { return diemDi; }
    public void setDiemDi(String diemDi) { this.diemDi = diemDi; }
    
    public String getDiemDen() { return diemDen; }
    public void setDiemDen(String diemDen) { this.diemDen = diemDen; }
    
    public Date getGioKhoiHanh() { return gioKhoiHanh; }
    public void setGioKhoiHanh(Date gioKhoiHanh) { 
        if (gioKhoiHanh != null && gioDen != null && gioKhoiHanh.after(gioDen)) {
            throw new IllegalArgumentException("Giờ khởi hành phải trước giờ đến");
        }
        this.gioKhoiHanh = gioKhoiHanh; 
    }
    
    public Date getGioDen() { return gioDen; }
    public void setGioDen(Date gioDen) { 
        if (gioKhoiHanh != null && gioDen != null && gioDen.before(gioKhoiHanh)) {
            throw new IllegalArgumentException("Giờ đến phải sau giờ khởi hành");
        }
        this.gioDen = gioDen; 
    }
    
    public int getSoGhe() { return soGhe; }
    public void setSoGhe(int soGhe) { 
        if (soGhe <= 0) {
            throw new IllegalArgumentException("Số ghế phải lớn hơn 0");
        }
        this.soGhe = soGhe; 
    }
    
    public int getSoGheTrong() { return soGheTrong; }
    public void setSoGheTrong(int soGheTrong) { 
        if (soGheTrong < 0 || soGheTrong > soGhe) {
            throw new IllegalArgumentException("Số ghế trống phải từ 0 đến " + soGhe);
        }
        this.soGheTrong = soGheTrong; 
    }
    
    public String getMaMayBay() { return maMayBay; }
    public void setMaMayBay(String maMayBay) { this.maMayBay = maMayBay; }
    
    public double getGiaCoBan() { return giaCoBan; }
    public void setGiaCoBan(double giaCoBan) { 
        if (giaCoBan < 0) {
            throw new IllegalArgumentException("Giá cơ bản không được âm");
        }
        this.giaCoBan = giaCoBan; 
    }
    
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}