/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package model;

import java.util.Date;
import java.util.regex.Pattern;

/**
 *
 * @author HP
 */
// File: NguoiDung.java - CLASS CHA
public abstract class NguoiDung {
    // CÁC THUỘC TÍNH CHUNG
    protected String ma;
    protected String hoTen;
    protected String soDT;
    protected String email;
    protected String cmnd;
    protected Date ngaySinh;
    protected String gioiTinh;
    protected String diaChi;
    protected Date ngayTao;
    
    // Regex patterns for validation
    protected static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    protected static final Pattern PHONE_PATTERN = Pattern.compile("^(03|05|07|08|09)[0-9]{8}$");
    protected static final Pattern CMND_PATTERN = Pattern.compile("^[0-9]{9}$|^[0-9]{12}$");
    
    // CONSTRUCTOR
    public NguoiDung(String ma, String hoTen, String soDT, String email, 
                     String cmnd, Date ngaySinh, String gioiTinh, String diaChi) {
        setMa(ma);
        setHoTen(hoTen);
        setSoDT(soDT);
        setEmail(email);
        setCmnd(cmnd);
        setNgaySinh(ngaySinh);
        setGioiTinh(gioiTinh);
        setDiaChi(diaChi);
        this.ngayTao = new Date();
    }
    
    // ABSTRACT METHODS
    public abstract String loaiNguoiDung();
    
    // VALIDATION METHODS
    public static boolean validateEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    public static boolean validatePhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }
    
    public static boolean validateCMND(String cmnd) {
        return cmnd != null && CMND_PATTERN.matcher(cmnd).matches();
    }
    
    // UTILITY METHODS
    public int tinhTuoi() {
        if (ngaySinh == null) return 0;
        Date now = new Date();
        long diff = now.getTime() - ngaySinh.getTime();
        return (int) (diff / (1000L * 60 * 60 * 24 * 365));
    }
    
    @Override
    public String toString() {
        return String.format("%s[%s: %s, %s]", loaiNguoiDung(), ma, hoTen, soDT);
    }
    
    // GETTERS AND SETTERS
    public String getMa() { return ma; }
    public void setMa(String ma) { 
        if (ma == null || ma.trim().isEmpty()) throw new IllegalArgumentException("Mã không được để trống");
        this.ma = ma.trim().toUpperCase();
    }
    
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { 
        if (hoTen == null || hoTen.trim().isEmpty()) throw new IllegalArgumentException("Họ tên không được để trống");
        this.hoTen = hoTen.trim();
    }
    
    public String getSoDT() { return soDT; }
    public void setSoDT(String soDT) { 
        if (soDT == null || soDT.trim().isEmpty()) throw new IllegalArgumentException("Số ĐT không được để trống");
        if (!validatePhone(soDT.trim())) throw new IllegalArgumentException("Số điện thoại không hợp lệ");
        this.soDT = soDT.trim();
    }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { 
        if (email == null || email.trim().isEmpty()) throw new IllegalArgumentException("Email không được để trống");
        if (!validateEmail(email.trim())) throw new IllegalArgumentException("Email không hợp lệ");
        this.email = email.trim().toLowerCase();
    }
    
    public String getCmnd() { return cmnd; }
    public void setCmnd(String cmnd) { 
        if (cmnd == null || cmnd.trim().isEmpty()) throw new IllegalArgumentException("CMND không được để trống");
        if (!validateCMND(cmnd.trim())) throw new IllegalArgumentException("CMND không hợp lệ");
        this.cmnd = cmnd.trim();
    }
    
    public Date getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(Date ngaySinh) { 
        if (ngaySinh != null && ngaySinh.after(new Date())) throw new IllegalArgumentException("Ngày sinh không thể ở tương lai");
        this.ngaySinh = ngaySinh;
    }
    
    public String getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(String gioiTinh) { 
        if (gioiTinh == null || (!gioiTinh.equals("Nam") && !gioiTinh.equals("Nữ"))) throw new IllegalArgumentException("Giới tính phải là 'Nam' hoặc 'Nữ'");
        this.gioiTinh = gioiTinh;
    }
    
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { 
        if (diaChi == null || diaChi.trim().isEmpty()) throw new IllegalArgumentException("Địa chỉ không được để trống");
        this.diaChi = diaChi.trim();
    }
    
    public Date getNgayTao() { return ngayTao; }
    public void setNgayTao(Date ngayTao) { 
        if (ngayTao != null && ngayTao.after(new Date())) throw new IllegalArgumentException("Ngày tạo không thể ở tương lai");
        this.ngayTao = ngayTao;
    }
}