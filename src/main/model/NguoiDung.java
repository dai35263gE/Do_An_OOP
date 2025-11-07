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
    protected String tenDangNhap;
    protected String matKhau;
    protected boolean trangThaiDangNhap;
    protected Date lastLogin;
    
    // Constants
    public static final String GT_NAM = "Nam";
    public static final String GT_NU = "Nữ";
    
    // Regex patterns for validation
    protected static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    protected static final Pattern PHONE_PATTERN = Pattern.compile("^(03|05|07|08|09)[0-9]{8}$");
    protected static final Pattern CMND_PATTERN = Pattern.compile("^[0-9]{9}$|^[0-9]{12}$");
    protected static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{4,20}$");
    protected static final Pattern PASSWORD_PATTERN = Pattern.compile("^.{6,}$");
    
    // CONSTRUCTOR
    public NguoiDung(String ma, String hoTen, String soDT, String email, 
                     String cmnd, Date ngaySinh, String gioiTinh, String diaChi,
                     String tenDangNhap, String matKhau) {
        setMa(ma);
        setHoTen(hoTen);
        setSoDT(soDT);
        setEmail(email);
        setCmnd(cmnd);
        setNgaySinh(ngaySinh);
        setGioiTinh(gioiTinh);
        setDiaChi(diaChi);
        setTenDangNhap(tenDangNhap);
        setMatKhau(matKhau);
        this.ngayTao = new Date();
        this.trangThaiDangNhap = false;
        this.lastLogin = null;
    }
    
    // ABSTRACT METHODS - để các lớp con triển khai
    public abstract String getVaiTro();
    public abstract String getPhanQuyen();
    public abstract boolean coTheThucHienChucNang(String chucNang);
    
    // AUTHENTICATION METHODS
    public boolean dangNhap(String tenDangNhap, String matKhau) {
        if (this.tenDangNhap.equals(tenDangNhap) && this.matKhau.equals(matKhau)) {
            this.trangThaiDangNhap = true;
            this.lastLogin = new Date();
            return true;
        }
        return false;
    }
    
    public void dangXuat() {
        this.trangThaiDangNhap = false;
    }
    
    public boolean doiMatKhau(String matKhauCu, String matKhauMoi) {
        if (this.matKhau.equals(matKhauCu) && validatePassword(matKhauMoi)) {
            setMatKhau(matKhauMoi);
            return true;
        }
        return false;
    }
    
    public void resetMatKhau(String matKhauMoi) {
        if (validatePassword(matKhauMoi)) {
            setMatKhau(matKhauMoi);
        } else {
            throw new IllegalArgumentException("Mật khẩu mới không hợp lệ");
        }
    }
    
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
    
    public static boolean validateUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }
    
    public static boolean validatePassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }
    
    // UTILITY METHODS cho GUI
    public int tinhTuoi() {
        if (ngaySinh == null) return 0;
        Date now = new Date();
        long diff = now.getTime() - ngaySinh.getTime();
        return (int) (diff / (1000L * 60 * 60 * 24 * 365));
    }
    
    public String getThongTinCoBan() {
        return String.format("%s - %s - %s", ma, hoTen, getVaiTro());
    }
    
    public String getThongTinDayDu() {
        return String.format(
            "Mã: %s\nHọ tên: %s\nSĐT: %s\nEmail: %s\nCMND: %s\nNgày sinh: %s\nGiới tính: %s\nĐịa chỉ: %s\nVai trò: %s\nTrạng thái: %s",
            ma, hoTen, soDT, email, cmnd, ngaySinh, gioiTinh, diaChi, getVaiTro(),
            trangThaiDangNhap ? "Đang đăng nhập" : "Chưa đăng nhập"
        );
    }
    
    public Object[] toRowData() {
        // Phù hợp để hiển thị trong JTable
        return new Object[] {
            ma,
            hoTen,
            soDT,
            email,
            cmnd,
            ngaySinh,
            gioiTinh,
            diaChi,
            getVaiTro(),
            trangThaiDangNhap ? "Đang hoạt động" : "Không hoạt động",
            lastLogin
        };
    }
    
    public boolean isDuThongTin() {
        return ma != null && !ma.trim().isEmpty() &&
               hoTen != null && !hoTen.trim().isEmpty() &&
               soDT != null && !soDT.trim().isEmpty() &&
               email != null && !email.trim().isEmpty() &&
               cmnd != null && !cmnd.trim().isEmpty() &&
               ngaySinh != null &&
               gioiTinh != null && !gioiTinh.trim().isEmpty() &&
               diaChi != null && !diaChi.trim().isEmpty() &&
               tenDangNhap != null && !tenDangNhap.trim().isEmpty() &&
               matKhau != null && !matKhau.trim().isEmpty();
    }
    
    // GETTERS AND SETTERS
    public String getMa() { return ma; }
    public void setMa(String ma) { 
        if (ma == null || ma.trim().isEmpty()) 
            throw new IllegalArgumentException("Mã không được để trống");
        this.ma = ma.trim().toUpperCase();
    }
    
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { 
        if (hoTen == null || hoTen.trim().isEmpty()) 
            throw new IllegalArgumentException("Họ tên không được để trống");
        this.hoTen = hoTen.trim();
    }
    
    public String getSoDT() { return soDT; }
    public void setSoDT(String soDT) { 
        if (soDT == null || soDT.trim().isEmpty()) 
            throw new IllegalArgumentException("Số ĐT không được để trống");
        if (!validatePhone(soDT.trim())) 
            throw new IllegalArgumentException("Số điện thoại không hợp lệ");
        this.soDT = soDT.trim();
    }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { 
        if (email == null || email.trim().isEmpty()) 
            throw new IllegalArgumentException("Email không được để trống");
        if (!validateEmail(email.trim())) 
            throw new IllegalArgumentException("Email không hợp lệ");
        this.email = email.trim().toLowerCase();
    }
    
    public String getCmnd() { return cmnd; }
    public void setCmnd(String cmnd) { 
        if (cmnd == null || cmnd.trim().isEmpty()) 
            throw new IllegalArgumentException("CMND không được để trống");
        if (!validateCMND(cmnd.trim())) 
            throw new IllegalArgumentException("CMND không hợp lệ");
        this.cmnd = cmnd.trim();
    }
    
    public Date getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(Date ngaySinh) { 
        if (ngaySinh != null && ngaySinh.after(new Date())) 
            throw new IllegalArgumentException("Ngày sinh không thể ở tương lai");
        this.ngaySinh = ngaySinh;
    }
    
    public String getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(String gioiTinh) { 
        if (gioiTinh == null || (!gioiTinh.equals(GT_NAM) && !gioiTinh.equals(GT_NU))) 
            throw new IllegalArgumentException("Giới tính phải là 'Nam' hoặc 'Nữ'");
        this.gioiTinh = gioiTinh;
    }
    
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { 
        if (diaChi == null || diaChi.trim().isEmpty()) 
            throw new IllegalArgumentException("Địa chỉ không được để trống");
        this.diaChi = diaChi.trim();
    }
    
    public Date getNgayTao() { return ngayTao; }
    public void setNgayTao(Date ngayTao) { 
        if (ngayTao != null && ngayTao.after(new Date())) 
            throw new IllegalArgumentException("Ngày tạo không thể ở tương lai");
        this.ngayTao = ngayTao;
    }
    
    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { 
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) 
            throw new IllegalArgumentException("Tên đăng nhập không được để trống");
        if (!validateUsername(tenDangNhap.trim())) 
            throw new IllegalArgumentException("Tên đăng nhập phải từ 4-20 ký tự và chỉ chứa chữ, số, dấu gạch dưới");
        this.tenDangNhap = tenDangNhap.trim();
    }
    
    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { 
        if (matKhau == null || matKhau.trim().isEmpty()) 
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        if (!validatePassword(matKhau.trim())) 
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự");
        this.matKhau = matKhau.trim();
    }
    
    public boolean isTrangThaiDangNhap() { return trangThaiDangNhap; }
    
    public Date getLastLogin() { return lastLogin; }
    
    // Additional utility methods for GUI
    public boolean isNam() {
        return GT_NAM.equals(gioiTinh);
    }
    
    public boolean isNu() {
        return GT_NU.equals(gioiTinh);
    }
    
    public boolean isDangHoatDong() {
        return trangThaiDangNhap;
    }
    
    public String getTrangThaiText() {
        return trangThaiDangNhap ? "Đang hoạt động" : "Không hoạt động";
    }
    
    @Override
    public String toString() {
        return getThongTinCoBan();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        NguoiDung that = (NguoiDung) obj;
        return ma != null && ma.equals(that.ma);
    }
    
    @Override
    public int hashCode() {
        return ma != null ? ma.hashCode() : 0;
    }
}