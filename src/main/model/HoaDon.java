/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package model;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * HoaDon - invoice model. Amount-related fields are computed on demand:
 * - tongTien = sum of ticket prices
 * - khuyenMai = computed from `KhachHang.tinhMucGiamGia(tongTien)`
 * - thue = TAX_RATE * tongTien (TAX_RATE = 0.05)
 * - thanhTien = tongTien - khuyenMai + thue
 */
public class HoaDon {
    private String maHoaDon;
    private Date ngayLap;
    private static final double TAX_RATE = 0.05; // fixed tax rate
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
    public static final String PT_NONE = "CHƯA";

    // Constructors
    public HoaDon(String maHoaDon, KhachHang khachHang, List<VeMayBay> danhSachVe, String phuongThucTT) {
        this.maHoaDon = maHoaDon;
        this.ngayLap = new Date();
        this.khachHang = khachHang;
        this.danhSachVe = (danhSachVe == null) ? new ArrayList<>() : danhSachVe;
        this.trangThai = TT_CHUA_TT;
        this.phuongThucTT = (phuongThucTT == null) ? PT_NONE : phuongThucTT;
    }

    // Backwards-compatible constructor: old callers that passed a khuyenMai value
    // will still work but the provided khuyenMai is ignored (discount is calculated from customer tier).
    public HoaDon(String maHoaDon, KhachHang khachHang, List<VeMayBay> danhSachVe, double ignoredKhuyenMai, String phuongThucTT) {
        this(maHoaDon, khachHang, danhSachVe, phuongThucTT);
    }

    public HoaDon(VeMayBay vmb) {
        this.maHoaDon = "HD999";
        this.ngayLap = new Date();
        this.danhSachVe = new ArrayList<>();
        this.danhSachVe.add(vmb);
        this.trangThai = TT_CHUA_TT;
        this.phuongThucTT = PT_NONE;
    }

    // Constructor used when loading from persistence
    public HoaDon(String maHoaDon, Date ngayLap, KhachHang khachHang, String phuongThucTT, String trangThai, List<VeMayBay> DSVe) {
        this.maHoaDon = maHoaDon;
        this.ngayLap = (ngayLap == null) ? new Date() : ngayLap;
        this.khachHang = khachHang;
        this.danhSachVe = (DSVe == null) ? new ArrayList<>() : DSVe;
        this.phuongThucTT = (phuongThucTT == null) ? PT_NONE : phuongThucTT;
        this.trangThai = (trangThai == null) ? TT_CHUA_TT : trangThai;
    }

    // BUSINESS METHODS

    // Total of ticket base prices
    public double tinhTongTien() {
        double tong = 0;
        if (danhSachVe == null) return 0;
        for (VeMayBay ve : danhSachVe) {
            tong += ve.getGiaVe();
        }
        return tong;
    }

    // Tax = TAX_RATE * total
    public double tinhThue() {
        return getTongTien() * TAX_RATE;
    }

    // Discount (khuyến mãi) computed from customer tier
    public double getKhuyenMai() {
        if (khachHang == null) return 0;
        return khachHang.tinhMucGiamGia(getTongTien());
    }

    // Final amount = total - discount + tax
    public double tinhThanhTien() {
        return getTongTien() - getKhuyenMai() + getThue();
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
            ve.setTrangThai(VeMayBay.TRANG_THAI_DA_THANH_TOAN);
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

        // Cập nhật trạng thái vé
        for (VeMayBay ve : danhSachVe) {
            ve.setTrangThai("CÓ_THỂ_ĐẶT");
        }
    }

    // Manual application of discount is not supported — discount depends on customer tier
    public void apDungKhuyenMai(double khuyenMaiMoi) {
        throw new UnsupportedOperationException("Khuyến mãi được tính tự động theo hạng khách hàng");
    }

    public boolean coTheHuy() {
        if (danhSachVe == null) return true;
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
        if (ve == null) return;
        danhSachVe.remove(ve);
    }

    public int tinhDiemTichLuy() {
        return (int) (getThanhTien() / 10000); // 1 điểm cho mỗi 10,000 VND
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

    // Derived getters for amounts (keep names to avoid changes elsewhere)
    public double getTongTien() {
        return tinhTongTien();
    }

    public double getThue() {
        return tinhThue();
    }

    public double getThanhTien() {
        return tinhThanhTien();
    }

    public String getPhuongThucTT() {
        return phuongThucTT;
    }

    public void setPhuongThucTT(String phuongThucTT) {
        if (phuongThucTT == null) throw new IllegalArgumentException("Phương thức thanh toán không được null");
        if (!phuongThucTT.equals(PT_TIEN_MAT) &&
                !phuongThucTT.equals(PT_CHUYEN_KHOAN) &&
                !phuongThucTT.equals(PT_THE) &&
                !phuongThucTT.equals(PT_VI_DIEN_TU) &&
                !phuongThucTT.equals(PT_NONE)) {
            throw new IllegalArgumentException("Phương thức thanh toán không hợp lệ");
        }
        if(this.getTrangThai().equals(TT_CHUA_TT) || this.getTrangThai().equals(TT_HUY)) this.phuongThucTT = PT_NONE;
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
                    "Nam", "Địa chỉ mặc định", "password");
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
        return (danhSachVe == null) ? 0 : danhSachVe.size();
    }

}
 