/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package model;

/**
 *
 * @author HP
 */
// File: HoaDon.java

import java.util.Date;
import java.util.UUID;

public class HoaDon {
    private String maHoaDon;
    private Date ngayLap;
    private String[] maVe;
    private String maKH;
    private String maNV;
    private double tongTien;
    private double thue;
    private double khuyenMai;
    private double thanhTien;
    private String phuongThucTT;
    private String trangThai;

    // Constants
    public static final String TT_CHUA_TT = "CHƯA_THANH_TOÁN";
    public static final String TT_DA_TT = "ĐÃ_THANH_TOÁN";
    public static final String TT_HUY = "HỦY";

    public static final String PT_TIEN_MAT = "TIỀN_MẶT";
    public static final String PT_CHUYEN_KHOAN = "CHUYỂN_KHOẢN";
    public static final String PT_THE = "THẺ_TÍN_DỤNG";
    public static final String PT_VI_DIEN_TU = "VÍ_ĐIỆN_TỬ";

    // CONSTRUCTOR CHÍNH - THÊM VALIDATION
    public HoaDon(String[] maVe, String maKH, String maNV, double tongTien,
            double thue, double khuyenMai, String phuongThucTT) {
        this.maHoaDon = generateMaHoaDon();
        this.ngayLap = new Date();
        setMaVe(maVe);
        setMaKH(maKH);
        setMaNV(maNV);
        setTongTien(tongTien);
        setThue(thue);
        setKhuyenMai(khuyenMai);
        setPhuongThucTT(phuongThucTT);

        this.thanhTien = calculateThanhTien();
        this.trangThai = TT_CHUA_TT;
    }

    // OVERLOAD CONSTRUCTOR - tự động tính thuế
    public HoaDon(String[] maVe, String maKH, String maNV, double tongTien,
            double khuyenMai, String phuongThucTT) {
        this(maVe, maKH, maNV, tongTien, tongTien * 0.08, khuyenMai, phuongThucTT);
    }

    // BUSINESS METHODS
    private String generateMaHoaDon() {
        return "HD" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private double calculateThanhTien() {
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
    }

    public void huyHoaDon() throws IllegalStateException {
        if (trangThai.equals(TT_DA_TT)) {
            throw new IllegalStateException("Không thể hủy hóa đơn đã thanh toán");
        }
        if (trangThai.equals(TT_HUY)) {
            throw new IllegalStateException("Hóa đơn đã bị hủy");
        }
        this.trangThai = TT_HUY;
    }

    public void apDungKhuyenMai(double khuyenMaiMoi) {
        if (khuyenMaiMoi < 0) {
            throw new IllegalArgumentException("Khuyến mãi không được âm");
        }
        if (khuyenMaiMoi > tongTien * 0.3) {
            throw new IllegalArgumentException("Khuyến mãi tối đa 30% tổng tiền");
        }
        this.khuyenMai = khuyenMaiMoi;
        this.thanhTien = calculateThanhTien();
    }

    public void capNhatTongTien(double tongTienMoi) {
        if (trangThai.equals(TT_DA_TT)) {
            throw new IllegalStateException("Không thể cập nhật hóa đơn đã thanh toán");
        }
        setTongTien(tongTienMoi);
        this.thanhTien = calculateThanhTien();
    }

    public boolean coTheHuy() {
        // Có thể hủy nếu chưa thanh toán và chưa quá 24h
        if (trangThai.equals(TT_DA_TT)) {
            return false;
        }

        Date now = new Date();
        long diff = now.getTime() - ngayLap.getTime();
        long hours = diff / (1000 * 60 * 60);

        return hours <= 24;
    }

    public double tinhPhanTramKhuyenMai() {
        return tongTien > 0 ? (khuyenMai / tongTien) * 100 : 0;
    }

    public void inHoaDon() {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║           HÓA ĐƠN BÁN VÉ              ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║ Mã HĐ: " + String.format("%-30s", maHoaDon) + "║");
        System.out.println("║ Ngày lập: " + String.format("%-27s", ngayLap) + "║");
        System.out.println("║ Mã vé: " + String.format("%-30s", maVe) + "║");
        System.out.println("║ Mã KH: " + String.format("%-30s", maKH) + "║");
        System.out.println("║ Mã NV: " + String.format("%-30s", maNV) + "║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║ Tổng tiền: " + String.format("%,-25.0f VND", tongTien) + "║");
        System.out.println("║ Thuế: " + String.format("%,-30.0f VND", thue) + "║");
        System.out.println("║ Khuyến mãi: " + String.format("%,-23.0f VND", khuyenMai) + "║");
        System.out.println(
                "║ " + String.format("%-38s", "(" + String.format("%.1f", tinhPhanTramKhuyenMai()) + "%)") + "║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║ Thành tiền: " + String.format("%,-23.0f VND", thanhTien) + "║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║ Phương thức TT: " + String.format("%-21s", phuongThucTT) + "║");
        System.out.println("║ Trạng thái: " + String.format("%-25s", trangThai) + "║");
        System.out.println("╚════════════════════════════════════════╝");
    }

    public String getHoaDonText() {
        return String.format(
                "HÓA ĐƠN BÁN VÉ\n" +
                        "Mã HĐ: %s\n" +
                        "Ngày lập: %s\n" +
                        "Mã vé: %s | Mã KH: %s | Mã NV: %s\n" +
                        "Tổng tiền: %,.0f VND\n" +
                        "Thuế: %,.0f VND\n" +
                        "Khuyến mãi: %,.0f VND (%.1f%%)\n" +
                        "Thành tiền: %,.0f VND\n" +
                        "Phương thức: %s | Trạng thái: %s",
                maHoaDon, ngayLap, maVe, maKH, maNV,
                tongTien, thue, khuyenMai, tinhPhanTramKhuyenMai(), thanhTien,
                phuongThucTT, trangThai);
    }

    @Override
    public String toString() {
        return String.format("HoaDon[%s: %s - %,.0f VND - %s]",
                maHoaDon, maVe, thanhTien, trangThai);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        HoaDon that = (HoaDon) obj;
        return maHoaDon != null && maHoaDon.equals(that.maHoaDon);
    }

    @Override
    public int hashCode() {
        return maHoaDon != null ? maHoaDon.hashCode() : 0;
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
            throw new IllegalArgumentException("Ngày lập không thể ở tương lai");
        }
        this.ngayLap = ngayLap;
    }

    public String[] getMaVe() {
        return maVe;
    }

    public void setMaVe(String[] maVe) {
        for (int i = 0; i < 100; i++) {
            if (maVe[i] == null || maVe[i].trim().isEmpty()) {
                throw new IllegalArgumentException("Mã vé không được để trống");
            }
            maVe[i] = maVe[i].trim().toUpperCase();
        }
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        if (maKH == null || maKH.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã KH không được để trống");
        }
        this.maKH = maKH.trim().toUpperCase();
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        if (maNV == null || maNV.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã NV không được để trống");
        }
        this.maNV = maNV.trim().toUpperCase();
    }

    public double getTongTien() {
        return tongTien;
    }

    public void setTongTien(double tongTien) {
        if (tongTien < 0) {
            throw new IllegalArgumentException("Tổng tiền không được âm");
        }
        this.tongTien = tongTien;
        this.thanhTien = calculateThanhTien();
    }

    public double getThue() {
        return thue;
    }

    public void setThue(double thue) {
        if (thue < 0) {
            throw new IllegalArgumentException("Thuế không được âm");
        }
        this.thue = thue;
        this.thanhTien = calculateThanhTien();
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
        this.thanhTien = calculateThanhTien();
    }

    public double getThanhTien() {
        return thanhTien;
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

    // Thêm phương thức tính điểm tích lũy dựa trên thành tiền
    public int tinhDiemTichLuy() {
        return (int) (thanhTien / 10000); // 1 điểm cho mỗi 10,000 VND
    }
}