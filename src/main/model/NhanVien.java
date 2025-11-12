package model;

import java.util.Date;

// Nhân viên cũng là một Người Dùng
public class NhanVien extends NguoiDung {

    private String chucVu; // Ví dụ: "ADMIN", "QUAN_LY", "NHAN_VIEN"

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_STAFF = "NHAN_VIEN";

    public NhanVien(String ma, String hoTen, String soDT, String email, String cmnd,
                    Date ngaySinh, String gioiTinh, String diaChi,
                    String tenDangNhap, String matKhau, String chucVu) {
        // Gọi constructor của lớp cha NguoiDung
        super(ma, hoTen, soDT, email, cmnd, ngaySinh, gioiTinh, diaChi, tenDangNhap, matKhau);
        this.chucVu = chucVu;
    }

    public String getChucVu() {
        return chucVu;
    }

    public void setChucVu(String chucVu) {
        this.chucVu = chucVu;
    }

    /**
     * Kiểm tra quyền hạn của nhân viên.
     * Admin (Quản trị viên) có thể làm mọi thứ.
     */
    @Override
    public boolean coTheThucHienChucNang(String chucNang) {
        if (this.chucVu.equals(ROLE_ADMIN)) {
            return true; // Admin có mọi quyền
        }

        // Có thể thêm các quyền chi tiết cho NHAN_VIEN sau
        if (this.chucVu.equals(ROLE_STAFF)) {
            switch (chucNang) {
                case "XEM_DU_LIEU":
                case "SUA_KHACH_HANG":
                    return true;
                default:
                    return false; // Nhân viên thường không được xóa
            }
        }
        return false;
    }
}