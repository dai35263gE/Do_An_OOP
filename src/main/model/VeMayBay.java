/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package model;

import java.util.Date;
import java.util.regex.Pattern;

public abstract class VeMayBay implements Comparable<VeMayBay> {
    protected String maKH;
    protected String maVe;
    protected Date ngayBay;
    protected double giaVe;
    protected String maChuyen;
    protected String soGhe;
    protected String trangThai;
    protected Date ngayDat;
    
    public static final String TRANG_THAI_DA_DAT = "ĐÃ_ĐẶT";
    public static final String TRANG_THAI_DA_THANH_TOAN = "ĐÃ_THANH_TOÁN";
    public static final String TRANG_THAI_DA_HUY = "ĐÃ_HỦY";
    public static final String TRANG_THAI_DA_BAY = "ĐÃ_BAY";
    
    private static final long THOI_GIAN_HUY_TOI_THIEU = 4 * 60 * 60 * 1000; // 4 tiếng tính bằng milliseconds
    //thoi gian doi se gap doi thoi gian huy ( toi thieu)

    
    // tao format mave va so ghe
    private static final Pattern MA_VE_PATTERN = Pattern.compile("^(VG|VP|VT)[0-9]{3}$");
    private static final Pattern SO_GHE_PATTERN = Pattern.compile("^[0-9]{1,2}[A-Z]$");
    
    // CONSTRUCTOR CHÍNH
    public VeMayBay(String maKH, String maVe, Date ngayBay, double giaVe, String maChuyen, String soGhe) {
        this.maKH = maKH;
        setMaVe(maVe);
        setNgayBay(ngayBay);
        setGiaVe(giaVe);
        setMaChuyen(maChuyen);
        setSoGhe(soGhe);
        this.ngayDat = new Date();
        this.trangThai = TRANG_THAI_DA_DAT;
    }

    public VeMayBay() {
    }

    // Phuong thuc Abstract 
    public abstract double tinhThue();
    public abstract String loaiVe();
    public abstract String chiTietLoaiVe();
    public abstract double tinhTongTien();
    
    public boolean coTheHuy() {
        if (ngayBay == null) return true;
        if (!trangThai.equals(TRANG_THAI_DA_BAY) && !trangThai.equals(TRANG_THAI_DA_THANH_TOAN)) {
            return false; // Chỉ có thể hủy vé đã đặt hoặc đã thanh toán
        }
        
        long thoiGianConLai = ngayBay.getTime() - System.currentTimeMillis();
        return thoiGianConLai > THOI_GIAN_HUY_TOI_THIEU;
    }
    public boolean coTheDoi() {
        if (ngayBay == null) return true;
        if (!trangThai.equals(TRANG_THAI_DA_DAT) && !trangThai.equals(TRANG_THAI_DA_THANH_TOAN)) {
            return false;
        }
        
        long thoiGianConLai = ngayBay.getTime() - System.currentTimeMillis();
        return thoiGianConLai > THOI_GIAN_HUY_TOI_THIEU * 2;
    }
    public String getThongBaoKhongTheHuy() {
        if (trangThai.equals(TRANG_THAI_DA_HUY)) {
            return "Vé đã bị hủy";
        }
        if (trangThai.equals(TRANG_THAI_DA_BAY)) {
            return "Chuyến bay đã hoàn thành";
        }
        if (ngayBay != null) {
            long thoiGianConLai = ngayBay.getTime() - System.currentTimeMillis();
            if (thoiGianConLai <= THOI_GIAN_HUY_TOI_THIEU) {
                return "Không thể hủy vé khi còn dưới 4 tiếng trước giờ bay";
            }
        }
        return null;
    }
    public void capNhatTrangThaiBay() {
        Date now = new Date();
        if (ngayBay != null && now.after(ngayBay) && 
            (trangThai.equals(TRANG_THAI_DA_THANH_TOAN))) {
            this.trangThai = TRANG_THAI_DA_BAY;
        }
    }
    public boolean daBay() {
        return TRANG_THAI_DA_BAY.equals(trangThai);
    } 
    public boolean coTheSuDung() {
        return !TRANG_THAI_DA_HUY.equals(trangThai) && !TRANG_THAI_DA_BAY.equals(trangThai);
    }
    public boolean isConTrong() {
        return TRANG_THAI_DA_HUY.equals(trangThai);
    }
    public boolean isDaDat() {
        return TRANG_THAI_DA_DAT.equals(trangThai);
    }
    public boolean isDaThanhToan() {
        return TRANG_THAI_DA_THANH_TOAN.equals(trangThai);
    }
    public boolean isDaHuy() {
        return TRANG_THAI_DA_HUY.equals(trangThai);
    }
    private boolean isTrangThaiHopLe(String trangThai) {
        return trangThai != null && 
               (
                trangThai.equals(TRANG_THAI_DA_DAT) || 
                trangThai.equals(TRANG_THAI_DA_THANH_TOAN) ||
                trangThai.equals(TRANG_THAI_DA_HUY) ||
                trangThai.equals(TRANG_THAI_DA_BAY));
    }
    public boolean kiemTraVeHopLe() {
        return validateMaVe(maVe) &&
               ngayBay != null && 
               ngayBay.after(new Date()) &&
               giaVe > 0 && 
               maChuyen != null && !maChuyen.trim().isEmpty() && 
               validateSoGhe(soGhe) && 
               isTrangThaiHopLe(trangThai);
    } 
    
    public static boolean validateMaVe(String maVe) {
        return maVe != null && MA_VE_PATTERN.matcher(maVe).matches();
    }
    public static boolean validateSoGhe(String soGhe) {
        return soGhe != null && SO_GHE_PATTERN.matcher(soGhe).matches();
    }
    
    public String getThongTinChiTiet() {
        StringBuilder sb = new StringBuilder();
        sb.append("Mã vé: ").append(maVe).append("\n");
        sb.append("Loại vé: ").append(loaiVe()).append("\n");
        sb.append("Chuyến bay: ").append(maChuyen).append("\n");
        sb.append("Ngày bay: ").append(ngayBay).append("\n");
        sb.append("Ghế: ").append(soGhe).append("\n");
        sb.append("Giá vé: ").append(String.format("%,.0f VND", giaVe)).append("\n");
        sb.append("Thuế: ").append(String.format("%,.0f VND", tinhThue())).append("\n");
        sb.append("Tổng tiền: ").append(String.format("%,.0f VND", tinhTongTien())).append("\n");
        sb.append("Chi tiết: ").append(chiTietLoaiVe()).append("\n");
        sb.append("Trạng thái: ").append(trangThai).append("\n");
        sb.append("Ngày đặt: ").append(ngayDat).append("\n");
        return sb.toString();
    }
    
    public Object[] toRowData() {
        // Phù hợp để hiển thị trong JTable
        return new Object[] {
            maVe,
            maKH,
            loaiVe(),
            maChuyen,
            ngayBay,
            soGhe,
            String.format("%,.0f VND", giaVe),
            String.format("%,.0f VND", tinhTongTien()),
            trangThai,
            ngayDat,
        };
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
    
    public String getMaVe() { return maVe; }
    public void setMaVe(String maVe) { 
        if (!validateMaVe(maVe)) {
            throw new IllegalArgumentException("Mã vé không hợp lệ. Format: VG001, VP123, VT456");
        }
        this.maVe = maVe.trim().toUpperCase();
    }
    
    public Date getNgayBay() { return ngayBay; }
    public void setNgayBay(Date ngayBay) { 
        this.ngayBay = ngayBay;
    }
    
    public double getGiaVe() { return giaVe; }
    public void setGiaVe(double giaVe) { 
        if (giaVe <= 0) {
            throw new IllegalArgumentException("Giá vé phải lớn hơn 0");
        }
        this.giaVe = giaVe;
    }
    
    public String getMaChuyen() { return maChuyen; }
    public void setMaChuyen(String maChuyen) { 
        if (maChuyen == null || maChuyen.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã chuyến bay không được để trống");
        }
        this.maChuyen = maChuyen.trim().toUpperCase();
    }
    
    public String getSoGhe() { return soGhe; }
    public void setSoGhe(String soGhe) { 
        if (soGhe == null || soGhe.trim().isEmpty()) {
            throw new IllegalArgumentException("Số ghế không được để trống");
        }
        if (!validateSoGhe(soGhe.trim())) {
            throw new IllegalArgumentException("Số ghế không hợp lệ. Format: 1A, 12B, 25C");
        }
        this.soGhe = soGhe.trim().toUpperCase();
    }
    
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { 
        if (!isTrangThaiHopLe(trangThai)) {
            throw new IllegalArgumentException("Trạng thái vé không hợp lệ");
        }
        this.trangThai = trangThai;
    }
    
    public String getmaKH() { return maKH; }
    
    public Date getNgayDat() { return ngayDat; }
    public void setNgayDat(Date ngayDat) { 
        if (ngayDat != null && ngayDat.after(new Date())) {
            throw new IllegalArgumentException("Ngày đặt không thể ở tương lai");
        }
        this.ngayDat = ngayDat;
    }
    
    
    
    public long getThoiGianConLai() {
        if (ngayBay == null) return Long.MAX_VALUE;
        return ngayBay.getTime() - System.currentTimeMillis();
    }
    
    public String getThoiGianConLaiFormatted() {
        long milliseconds = getThoiGianConLai();
        if (milliseconds == Long.MAX_VALUE) return "Không xác định";
        
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) {
            return String.format("%d ngày %d giờ", days, hours % 24);
        } else if (hours > 0) {
            return String.format("%d giờ %d phút", hours, minutes % 60);
        } else {
            return String.format("%d phút", minutes);
        }
    }
}