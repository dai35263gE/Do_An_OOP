/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package model;

import java.util.Date;

/**
 *
 * @author HP
 */
// File: NhanVien.java - NHÂN VIÊN BÁN VÉ
public class NhanVien extends NguoiDung {
    private String tenDangNhap;
    private String matKhau;
    private String chucVu;
    private double luong;
    private Date ngayBatDau;
    private String trangThai;
    
    public static final String TRANG_THAI_DANG_LAM = "ĐANG_LÀM";
    public static final String TRANG_THAI_NGHI_VIEC = "NGHỈ_VIỆC";
    
    public NhanVien(String maNV, String hoTen, String soDT, String email, 
                   String cmnd, Date ngaySinh, String gioiTinh, String diaChi,
                   String tenDangNhap, String matKhau, double luong) {
        super(maNV, hoTen, soDT, email, cmnd, ngaySinh, gioiTinh, diaChi);
        setTenDangNhap(tenDangNhap);
        setMatKhau(matKhau);
        setLuong(luong);
        this.chucVu = "NHÂN_VIÊN_BÁN_VÉ";
        this.ngayBatDau = new Date();
        this.trangThai = TRANG_THAI_DANG_LAM;
    }
    
    @Override
    public String loaiNguoiDung() {
        return "NHAN_VIEN";
    }
    
    public boolean dangNhap(String tenDangNhap, String matKhau) {
        return this.tenDangNhap.equals(tenDangNhap) && this.matKhau.equals(matKhau) && trangThai.equals(TRANG_THAI_DANG_LAM);
    }
    
    public void doiMatKhau(String matKhauCu, String matKhauMoi) {
        if (!this.matKhau.equals(matKhauCu)) throw new IllegalArgumentException("Mật khẩu cũ không đúng");
        setMatKhau(matKhauMoi);
    }
    
    public void nghiViec() {
        this.trangThai = TRANG_THAI_NGHI_VIEC;
    }
    
    // GETTERS AND SETTERS
    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { 
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) throw new IllegalArgumentException("Tên đăng nhập không được để trống");
        this.tenDangNhap = tenDangNhap.trim();
    }
    
    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { 
        if (matKhau == null || matKhau.trim().isEmpty()) throw new IllegalArgumentException("Mật khẩu không được để trống");
        if (matKhau.length() < 6) throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự");
        this.matKhau = matKhau.trim();
    }
    
    public String getChucVu() { return chucVu; }
    
    public double getLuong() { return luong; }
    public void setLuong(double luong) { 
        if (luong < 0) throw new IllegalArgumentException("Lương không được âm");
        this.luong = luong;
    }
    
    public Date getNgayBatDau() { return ngayBatDau; }
    public void setNgayBatDau(Date ngayBatDau) { 
        if (ngayBatDau != null && ngayBatDau.after(new Date())) throw new IllegalArgumentException("Ngày bắt đầu không thể ở tương lai");
        this.ngayBatDau = ngayBatDau;
    }
    
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { 
        if (!trangThai.equals(TRANG_THAI_DANG_LAM) && !trangThai.equals(TRANG_THAI_NGHI_VIEC)) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ");
        }
        this.trangThai = trangThai;
    }
}