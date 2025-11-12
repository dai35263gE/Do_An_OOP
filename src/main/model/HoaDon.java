/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package model;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

/**
 *
 * @author HP
 */
// File: HoaDon.java
public class HoaDon {
    private String maHoaDon;
    private Date ngayLap;
    private double tongTien;
    private double thue;
    private double khuyenMai;
    private double thanhTien;
    private String phuongThucTT;
    private String trangThai;
    private KhachHang khachHang;
    private List<VeMayBay> danhSachVe;

    // Constants
    public static final String TT_CHUA_TT = "CHƯA_THANH_TOÁN";
    public static final String TT_DA_TT = "ĐÃ_THANH_TOÁN";
    public static final String TT_HUY = "HỦY";

    public static final String PT_TIEN_MAT = "TIỀN_MẶT";
    public static final String PT_CHUYEN_KHOAN = "CHUYỂN_KHOẢN";
    public static final String PT_THE = "THẺ_TÍN_DỤNG";
    public static final String PT_VI_DIEN_TU = "VÍ_ĐIỆN_TỬ";

    // CONSTRUCTOR CHÍNH
    public HoaDon(String maHoaDon, KhachHang khachHang, List<VeMayBay> danhSachVe, double khuyenMai, String phuongThucTT) {
        this.maHoaDon = maHoaDon;
        this.ngayLap = new Date();
        this.khachHang = khachHang;
        this.danhSachVe = danhSachVe;
        setTongTien(tinhTongTien());
        setThue(tinhThue());
        setKhuyenMai(khuyenMai);
        setPhuongThucTT(phuongThucTT);

        this.thanhTien = tinhThanhTien();
        this.trangThai = TT_CHUA_TT;
    }

    // OVERLOAD CONSTRUCTOR - không có khuyến mãi
    public HoaDon(String maHoaDon, KhachHang khachHang, List<VeMayBay> danhSachVe, String phuongThucTT) {
        this(maHoaDon, khachHang, danhSachVe, 0, phuongThucTT);
    }

    public HoaDon(VeMayBay vmb) {
        this.maHoaDon = "HD000";
        this.ngayLap = new Date();
        this.danhSachVe = new ArrayList<>();
        danhSachVe.add(vmb);
        this.trangThai = TT_CHUA_TT;
    }

    public HoaDon(String maHoaDon, Date ngayLap, KhachHang khachHang, double tongTien, double thue, double khuyenMai,
            String phuongThucTT, String trangThai, List<VeMayBay> DSVe) {
        this.maHoaDon = maHoaDon;
        this.ngayLap = ngayLap;
        this.khachHang = khachHang;
        this.danhSachVe = new ArrayList<>();
        this.tongTien = tongTien;
        this.thue = thue;
        this.khuyenMai = khuyenMai;
        this.phuongThucTT = phuongThucTT;
        this.trangThai = trangThai;
        this.thanhTien = tinhThanhTien();
        this.danhSachVe = DSVe;
    }

    // BUSINESS METHODS

    public double tinhTongTien() {
        double tong = 0;
        for (VeMayBay ve : danhSachVe) {
            tong += ve.tinhTongTien();
        }
        return tong;
    }

    public double tinhThue() {
        return tongTien * 0.05; // Thuế 8%
    }

    public double tinhThanhTien() {
        return tongTien + thue - khuyenMai;
    }

    public void thanhToan() throws IllegalStateException {
        if (trangThai.equals(TT_DA_TT)) {
            throw new IllegalStateException("Hóa đơn đã được thanh toán");
        }
        if (trangThai.equals(TT_HUY)) {
            throw new IllegalStateException("Không thể thanh toán hóa đơn đã hủy");
        }
        this.trangThai = TT_DA_TT;

        // Cập nhật trạng thái vé sau khi thanh toán
        for (VeMayBay ve : danhSachVe) {
            ve.setTrangThai("ĐÃ_THANH_TOÁN");
        }
    }

    public void huyHoaDon() throws IllegalStateException {
        if (trangThai.equals(TT_DA_TT)) {
            throw new IllegalStateException("Không thể hủy hóa đơn đã thanh toán");
        }
        if (trangThai.equals(TT_HUY)) {
            throw new IllegalStateException("Hóa đơn đã bị hủy");
        }

        // Kiểm tra điều kiện hủy
        if (!coTheHuy()) {
            throw new IllegalStateException("Không thể hủy hóa đơn vì có vé đã quá thời gian hủy");
        }

        this.trangThai = TT_HUY;

        // Cập nhật trạng thái vé và xóa liên kết với hóa đơn
        for (VeMayBay ve : danhSachVe) {
            ve.setTrangThai("CÓ_THỂ_ĐẶT");
        }
    }

    public void apDungKhuyenMai(double khuyenMaiMoi) {
        if (khuyenMaiMoi < 0) {
            throw new IllegalArgumentException("Khuyến mãi không được âm");
        }
        if (khuyenMaiMoi > tongTien * 0.3) {
            throw new IllegalArgumentException("Khuyến mãi tối đa 30% tổng tiền");
        }
        this.khuyenMai = khuyenMaiMoi;
        this.thanhTien = tinhThanhTien();
    }

    public boolean coTheHuy() {
        for (VeMayBay ve : danhSachVe) {
            if (!ve.coTheHuy()) {
                return false;
            }
        }
        return true;
    }

    public void xoaVe(VeMayBay ve) {
        if (trangThai.equals(TT_DA_TT)) {
            throw new IllegalStateException("Không thể xóa vé khỏi hóa đơn đã thanh toán");
        }

        if ((ve.getTrangThai().equals(VeMayBay.TRANG_THAI_DA_DAT) || ve.getTrangThai().equals(VeMayBay.TRANG_THAI_DA_DAT))  && danhSachVe.remove(ve)) {
            this.tongTien = tinhTongTien();
            this.thue = tinhThue();
            this.thanhTien = tinhThanhTien();
        }
    }

    public int tinhDiemTichLuy() {
        return (int) (thanhTien / 10000); // 1 điểm cho mỗi 10,000 VND
    }

    // Getters and Setters với VALIDATION
    public String getMaHoaDon() {
        return maHoaDon;
    }

    public Date getNgayLap() {
        return ngayLap;
    }
    public void setNgayLap(Date ngayLap) {
        if (ngayLap != null && ngayLap.after(new Date())) {
            throw new IllegalArgumentException("Ngay lap khong the o tuong lai");
        }
        this.ngayLap = ngayLap;
    }

    public double getTongTien() {
        return tongTien;
    }
    public void setTongTien(double tongTien) {
        if (tongTien < 0) {
            throw new IllegalArgumentException("Tổng tiền không được âm");
        }
        this.tongTien = tongTien;
    }

    public double getThue() {
        return thue;
    }
    public void setThue(double thue) {
        if (thue < 0) {
            throw new IllegalArgumentException("Thuế không được âm");
        }
        this.thue = thue;
        this.thanhTien = tinhThanhTien();
    }

    public double getKhuyenMai() {
        return khuyenMai;
    }
    public void setKhuyenMai(double khuyenMai) {
        if (khuyenMai < 0) {
            throw new IllegalArgumentException("Khuyến mãi không được âm");
        }
        if (khuyenMai > tongTien * 0.3) {
            throw new IllegalArgumentException("Khuyến mãi tối đa 30% tổng tiền");
        }
        this.khuyenMai = khuyenMai;
        this.thanhTien = tinhThanhTien();
    }

    public double getThanhTien() {
        return thanhTien;
    }
    public void setThanhTien(double thanhTien) {
        this.thanhTien = thanhTien;
    }


    public String getPhuongThucTT() {
        return phuongThucTT;
    }
    public void setPhuongThucTT(String phuongThucTT) {
        if (!phuongThucTT.equals(PT_TIEN_MAT) &&
                !phuongThucTT.equals(PT_CHUYEN_KHOAN) &&
                !phuongThucTT.equals(PT_THE) &&
                !phuongThucTT.equals(PT_VI_DIEN_TU)) {
            throw new IllegalArgumentException("Phương thức thanh toán không hợp lệ");
        }
        this.phuongThucTT = phuongThucTT;
    }

    public String getTrangThai() {
        return trangThai;
    }
    public void setTrangThai(String trangThai) {
        if (!trangThai.equals(TT_CHUA_TT) &&
                !trangThai.equals(TT_DA_TT) &&
                !trangThai.equals(TT_HUY)) {
            throw new IllegalArgumentException("Trạng thái hóa đơn không hợp lệ");
        }
        this.trangThai = trangThai;
    }

    public KhachHang getKhachHang() {
        if (khachHang == null) {
            // Trả về khách hàng mặc định nếu null
            return new KhachHang("KH000", "Khách hàng mặc định", "0000000000",
                    "default@email.com", "000000000000", new Date(),
                    "Nam", "Địa chỉ mặc định", "default", "password");
        }
        return khachHang;
    }
    public void setKhachHang(KhachHang khachHang) {
        if (khachHang == null) {
            throw new IllegalArgumentException("Khách hàng không được null");
        }
        this.khachHang = khachHang;
    }

    public List<VeMayBay> getDanhSachVe() {
        return new ArrayList<>(danhSachVe);
    }
    public int getSoLuongVe() {
        return danhSachVe.size();
    }

    
}