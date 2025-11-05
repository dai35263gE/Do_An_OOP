/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package model;

/**
 *
 * @author HP
 */
// File: KhachHang.java

import java.util.Date;
import java.util.regex.Pattern;

public class KhachHang {
    private String maKH;
    private String hoTen;
    private String soDT;
    private String email;
    private String cmnd;
    private Date ngaySinh;
    private String gioiTinh;
    private String diaChi;
    private String hangKhachHang;
    private int diemTichLuy;
    private Date ngayDangKy;
    
    // Constants
    public static final String HANG_BRONZE = "BRONZE";
    public static final String HANG_SILVER = "SILVER";
    public static final String HANG_GOLD = "GOLD";
    public static final String HANG_PLATINUM = "PLATINUM";
    
    // Regex patterns for validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(03|05|07|08|09)[0-9]{8}$");
    private static final Pattern CMND_PATTERN = Pattern.compile("^[0-9]{9}$|^[0-9]{12}$");
    
    public KhachHang(String maKH, String hoTen, String soDT, String email, 
                    String cmnd, Date ngaySinh, String gioiTinh, String diaChi) {
        
        // VALIDATION trong constructor
        setMaKH(maKH);
        setHoTen(hoTen);
        setSoDT(soDT);
        setEmail(email);
        setCmnd(cmnd);
        setNgaySinh(ngaySinh);
        setGioiTinh(gioiTinh);
        setDiaChi(diaChi);
        
        this.hangKhachHang = HANG_BRONZE;
        this.diemTichLuy = 0;
        this.ngayDangKy = new Date();
    }
    
    // OVERLOAD CONSTRUCTOR với hạng khách hàng (từ XML)
    public KhachHang(String maKH, String hoTen, String soDT, String email, 
                    String cmnd, Date ngaySinh, String gioiTinh, String diaChi,
                    String hangKhachHang, int diemTichLuy, Date ngayDangKy) {
        this(maKH, hoTen, soDT, email, cmnd, ngaySinh, gioiTinh, diaChi);
        setHangKhachHang(hangKhachHang);
        setDiemTichLuy(diemTichLuy);
        setNgayDangKy(ngayDangKy);
    }
    
    // BUSINESS METHODS - chỉ liên quan đến bản thân khách hàng
    public void tangDiemTichLuy(int diem) {
        if (diem < 0) {
            throw new IllegalArgumentException("Điểm tích lũy không được âm");
        }
        this.diemTichLuy += diem;
        capNhatHangKhachHang();
    }
    
    public void giamDiemTichLuy(int diem) {
        if (diem < 0) {
            throw new IllegalArgumentException("Điểm giảm không được âm");
        }
        if (diem > this.diemTichLuy) {
            throw new IllegalArgumentException("Không đủ điểm để giảm");
        }
        this.diemTichLuy -= diem;
        capNhatHangKhachHang();
    }
    
    private void capNhatHangKhachHang() {
        if (diemTichLuy >= 10000) {
            hangKhachHang = HANG_PLATINUM;
        } else if (diemTichLuy >= 5000) {
            hangKhachHang = HANG_GOLD;
        } else if (diemTichLuy >= 1000) {
            hangKhachHang = HANG_SILVER;
        } else {
            hangKhachHang = HANG_BRONZE;
        }
    }
    
    public double tinhTyLeGiamGia() {
        switch (hangKhachHang) {
            case HANG_PLATINUM: return 0.15;
            case HANG_GOLD: return 0.10;
            case HANG_SILVER: return 0.05;
            default: return 0.02;
        }
    }
    
    // VALIDATION METHODS - static để dùng chung
    public static boolean validateEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    public static boolean validatePhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }
    
    public static boolean validateCMND(String cmnd) {
        return cmnd != null && CMND_PATTERN.matcher(cmnd).matches();
    }
    
    // UTILITY METHODS - chỉ liên quan đến bản thân
    public boolean kiemTraDuTuoi(int tuoiToiThieu) {
        if (ngaySinh == null) return false;
        
        Date now = new Date();
        long diff = now.getTime() - ngaySinh.getTime();
        long age = diff / (1000L * 60 * 60 * 24 * 365);
        
        return age >= tuoiToiThieu;
    }
    
    public int tinhTuoi() {
        if (ngaySinh == null) return 0;
        
        Date now = new Date();
        long diff = now.getTime() - ngaySinh.getTime();
        return (int) (diff / (1000L * 60 * 60 * 24 * 365));
    }
    
    public long tinhSoNgayThanhVien() {
        if (ngayDangKy == null) return 0;
        
        Date now = new Date();
        long diff = now.getTime() - ngayDangKy.getTime();
        return diff / (1000L * 60 * 60 * 24);
    }
    
    @Override
    public String toString() {
        return String.format("KhachHang[%s: %s, %s, %d điểm, %s]", 
                           maKH, hoTen, soDT, diemTichLuy, hangKhachHang);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        KhachHang that = (KhachHang) obj;
        return maKH != null && maKH.equals(that.maKH);
    }
    
    @Override
    public int hashCode() {
        return maKH != null ? maKH.hashCode() : 0;
    }
    
    // Getters and Setters với VALIDATION
    public String getMaKH() { return maKH; }
    public void setMaKH(String maKH) { 
        if (maKH == null || maKH.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã KH không được để trống");
        }
        this.maKH = maKH.trim().toUpperCase();
    }
    
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { 
        if (hoTen == null || hoTen.trim().isEmpty()) {
            throw new IllegalArgumentException("Họ tên không được để trống");
        }
        this.hoTen = hoTen.trim();
    }
    
    public String getSoDT() { return soDT; }
    public void setSoDT(String soDT) { 
        if (soDT == null || soDT.trim().isEmpty()) {
            throw new IllegalArgumentException("Số ĐT không được để trống");
        }
        if (!validatePhone(soDT.trim())) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ: " + soDT);
        }
        this.soDT = soDT.trim();
    }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { 
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống");
        }
        if (!validateEmail(email.trim())) {
            throw new IllegalArgumentException("Email không hợp lệ: " + email);
        }
        this.email = email.trim().toLowerCase();
    }
    
    public String getCmnd() { return cmnd; }
    public void setCmnd(String cmnd) { 
        if (cmnd == null || cmnd.trim().isEmpty()) {
            throw new IllegalArgumentException("CMND không được để trống");
        }
        if (!validateCMND(cmnd.trim())) {
            throw new IllegalArgumentException("CMND không hợp lệ: " + cmnd);
        }
        this.cmnd = cmnd.trim();
    }
    
    public Date getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(Date ngaySinh) { 
        if (ngaySinh != null && ngaySinh.after(new Date())) {
            throw new IllegalArgumentException("Ngày sinh không thể ở tương lai");
        }
        this.ngaySinh = ngaySinh;
    }
    
    public String getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(String gioiTinh) { 
        if (gioiTinh == null || (!gioiTinh.equals("Nam") && !gioiTinh.equals("Nữ"))) {
            throw new IllegalArgumentException("Giới tính phải là 'Nam' hoặc 'Nữ'");
        }
        this.gioiTinh = gioiTinh;
    }
    
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { 
        if (diaChi == null || diaChi.trim().isEmpty()) {
            throw new IllegalArgumentException("Địa chỉ không được để trống");
        }
        this.diaChi = diaChi.trim();
    }
    
    public String getHangKhachHang() { return hangKhachHang; }
    public void setHangKhachHang(String hangKhachHang) { 
        if (!hangKhachHang.equals(HANG_BRONZE) && 
            !hangKhachHang.equals(HANG_SILVER) && 
            !hangKhachHang.equals(HANG_GOLD) && 
            !hangKhachHang.equals(HANG_PLATINUM)) {
            throw new IllegalArgumentException("Hạng khách hàng không hợp lệ");
        }
        this.hangKhachHang = hangKhachHang;
    }
    
    public int getDiemTichLuy() { return diemTichLuy; }
    public void setDiemTichLuy(int diemTichLuy) { 
        if (diemTichLuy < 0) {
            throw new IllegalArgumentException("Điểm tích lũy không được âm");
        }
        this.diemTichLuy = diemTichLuy;
        capNhatHangKhachHang();
    }
    
    public Date getNgayDangKy() { return ngayDangKy; }
    public void setNgayDangKy(Date ngayDangKy) { 
        if (ngayDangKy != null && ngayDangKy.after(new Date())) {
            throw new IllegalArgumentException("Ngày đăng ký không thể ở tương lai");
        }
        this.ngayDangKy = ngayDangKy;
    }
}