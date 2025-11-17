/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Calendar;

/**
 *
 * @author HP
 */
// File: KhachHang.java
public class KhachHang extends NguoiDung {
    private String hangKhachHang;
    private int diemTichLuy;
    private Date ngayDangKy;
    private List<HoaDon> lichSuHoaDon;
    private List<VeMayBay> veDaDat;
    
    // Constants
    public static final String HANG_BRONZE = "BRONZE";  // 
    public static final String HANG_SILVER = "SILVER";  // hoa don tren 1,000,000
    public static final String HANG_GOLD = "GOLD"; // hoa don tren 5,000,000
    public static final String HANG_PLATINUM = "PLATINUM"; // hoa don tren 10,000,000
    

    // Monthly spending thresholds (VND) for tiers — reset/reevaluated every month
    private static final double THRESHOLD_SILVER = 2_000_000.0;
    private static final double THRESHOLD_GOLD = 5_000_000.0;
    private static final double THRESHOLD_PLATINUM = 10_000_000.0;
    

    public KhachHang(String ma, String hoTen, String soDT, String email, String cmnd, Date ngaySinh, String gioiTinh, String diaChi, String matKhau) {
        super(ma, hoTen, soDT, email, cmnd, ngaySinh, gioiTinh, diaChi, matKhau);
        this.hangKhachHang = HANG_BRONZE;
        this.diemTichLuy = 0;
        this.ngayDangKy = new Date();
        this.lichSuHoaDon = new ArrayList<>();
        this.veDaDat = new ArrayList<>();
    }
    
    // OVERLOAD CONSTRUCTOR - từ file
    public KhachHang(String ma, String hoTen, String soDT, String email, String cmnd, Date ngaySinh, String gioiTinh, String diaChi, String matKhau, String hangKhachHang, int diemTichLuy, Date ngayDangKy) {
        super(ma, hoTen, soDT, email, cmnd, ngaySinh, gioiTinh, diaChi, matKhau);
        setHangKhachHang(hangKhachHang);
        setDiemTichLuy(diemTichLuy);
        setNgayDangKy(ngayDangKy);
        this.lichSuHoaDon = new ArrayList<>();
        this.veDaDat = new ArrayList<>();
    }
    
    @Override
    public boolean coTheThucHienChucNang(String chucNang) {
        switch (chucNang) {
            case "DAT_VE":
            case "HUY_VE":
            case "XEM_HOA_DON":
            case "XEM_VE_DA_DAT":
            case "TRA_CUU_CHUYEN_BAY":
            case "CHINH_SUA_THONG_TIN":
            case "DOI_MAT_KHAU":
                return true;
            default:
                return false;
        }
    }
    
    public boolean datVe(VeMayBay ve) {
        if (!coTheThucHienChucNang("DAT_VE")) {
            return false;
        }
        
        try {
            if (!ve.trangThai.equals(VeMayBay.TRANG_THAI_DA_DAT)) {
            throw new IllegalStateException("Vé không thể đặt. Trạng thái hiện tại: " + ve.getTrangThai());
        }
        else {
            ve.setTrangThai(VeMayBay.TRANG_THAI_DA_DAT);
            ve.ngayDat = new Date();
        }
            veDaDat.add(ve);
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }
    
    public boolean huyVe(VeMayBay ve) {
        if (!coTheThucHienChucNang("HUY_VE")) {
            return false;
        }
        
        if (!veDaDat.contains(ve)) {
            return false; // Vé không thuộc về khách hàng này
        }
        
        try {
            if (!ve.coTheHuy()) {
            String thongBao = ve.getThongBaoKhongTheHuy();
            throw new IllegalStateException(thongBao != null ? thongBao : "Không thể hủy vé");
        }
        else {ve.setTrangThai(VeMayBay.TRANG_THAI_DA_HUY);}
            veDaDat.remove(ve);
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }
    
    public String kiemTraKhaNangHuyVe(VeMayBay ve) {
        if (!veDaDat.contains(ve)) {
            return "Vé không thuộc về khách hàng này";
        }
        
        return ve.getThongBaoKhongTheHuy();
    }
    
    public void themHoaDon(HoaDon hoaDon) {
        if (hoaDon != null && !lichSuHoaDon.contains(hoaDon)) {
            lichSuHoaDon.add(hoaDon);
            
            // Cộng điểm tích lũy từ hóa đơn
            int diemThuong = hoaDon.tinhDiemTichLuy();
            tangDiemTichLuy(diemThuong);
        }
    }
    
    public void tangDiemTichLuy(int diem) {
        if (diem < 0) {
            throw new IllegalArgumentException("Điểm tích lũy không được âm");
        }
        this.diemTichLuy += diem;
        capNhatHangKhachHang();
    }
    
    public boolean suDungDiemTichLuy(int diem) {
        if (diem < 0) {
            throw new IllegalArgumentException("Điểm sử dụng không được âm");
        }
        if (diem > this.diemTichLuy) {
            return false; // Không đủ điểm
        }
        this.diemTichLuy -= diem;
        capNhatHangKhachHang();
        return true;
    }
    
    private void capNhatHangKhachHang() {
        // New policy: determine tier based on total paid spending in the current month.
        double tongThang = getTongChiTieuThang();
        if (tongThang > THRESHOLD_PLATINUM) {
            hangKhachHang = HANG_PLATINUM;
        } else if (tongThang > THRESHOLD_GOLD) {
            hangKhachHang = HANG_GOLD;
        } else if (tongThang > THRESHOLD_SILVER) {
            hangKhachHang = HANG_SILVER;
        } else {
            hangKhachHang = HANG_BRONZE;
        }
    }

    // Public wrapper to allow external callers (GUI/services) to force re-evaluation
    public void capNhatHangTheoThang() {
        capNhatHangKhachHang();
    }

    /**
     * Tính tổng chi tiêu (đã thanh toán) của khách hàng trong tháng hiện tại.
     * Dùng để đánh hạng theo yêu cầu: reset mỗi tháng.
     */
    public double getTongChiTieuThang() {
        if (lichSuHoaDon == null || lichSuHoaDon.isEmpty()) return 0.0;
        Calendar now = Calendar.getInstance();
        int yearNow = now.get(Calendar.YEAR);
        int monthNow = now.get(Calendar.MONTH); // 0-based

        double tong = 0.0;
        for (HoaDon hd : lichSuHoaDon) {
            if (hd == null) continue;
            if (!hd.getTrangThai().equals(HoaDon.TT_DA_TT)) continue; // chỉ tính hóa đơn đã thanh toán
            Date ngay = hd.getNgayLap();
            if (ngay == null) continue;
            Calendar c = Calendar.getInstance();
            c.setTime(ngay);
            if (c.get(Calendar.YEAR) == yearNow && c.get(Calendar.MONTH) == monthNow) {
                tong += hd.getThanhTien();
            }
        }
        return tong;
    }
    
    public double tinhTyLeGiamGia() {
        switch (hangKhachHang) {
            case HANG_PLATINUM: return 0.15;
            case HANG_GOLD: return 0.10;
            case HANG_SILVER: return 0.05;
            default: return 0;
        }
    }
    
    public double tinhMucGiamGia(double tongTien) {
        return tongTien * tinhTyLeGiamGia();
    }
    
    // TRA CỨU VÀ XEM THÔNG TIN
    public List<HoaDon> getLichSuHoaDon() {
        return lichSuHoaDon;
    }
    
    public List<VeMayBay> getVeDaDat() {
        List<VeMayBay> veCuaToi = new ArrayList<>();
        for(HoaDon hd : this.lichSuHoaDon){
            veCuaToi.addAll(hd.getDanhSachVe());
        }
        return veCuaToi;
    }
    
    public List<VeMayBay> getVeChuaBay() {
        List<VeMayBay> result = new ArrayList<>();
        for (VeMayBay ve : veDaDat) {
            if (!ve.daBay() && ve.coTheSuDung()) {
                result.add(ve);
            }
        }
        return result;
    }
    
    public List<VeMayBay> getVeDaBay() {
        List<VeMayBay> result = new ArrayList<>();
        for (VeMayBay ve : veDaDat) {
            if (ve.daBay()) {
                result.add(ve);
            }
        }
        return result;
    }
    
    public List<VeMayBay> getVeCoTheHuy() {
        List<VeMayBay> result = new ArrayList<>();
        for (VeMayBay ve : veDaDat) {
            if (ve.coTheHuy()) {
                result.add(ve);
            }
        }
        return result;
    }
    
    public HoaDon timHoaDonTheoMa(String maHoaDon) {
        for (HoaDon hd : lichSuHoaDon) {
            if (hd.getMaHoaDon().equals(maHoaDon)) {
                return hd;
            }
        }
        return null;
    }
    
    public VeMayBay timVeTheoMa(String maVe) {
        for (VeMayBay ve : veDaDat) {
            if (ve.getMaVe().equals(maVe)) {
                return ve;
            }
        }
        return null;
    }
    
    // CHỈNH SỬA THÔNG TIN CÁ NHÂN
    public boolean capNhatThongTinCaNhan(String hoTenMoi, String soDTMoi, String emailMoi, 
                                        String diaChiMoi, String gioiTinhMoi) {
        if (!coTheThucHienChucNang("CHINH_SUA_THONG_TIN")) {
            return false;
        }
        
        try {
            if (hoTenMoi != null && !hoTenMoi.trim().isEmpty()) {
                setHoTen(hoTenMoi);
            }
            if (soDTMoi != null && !soDTMoi.trim().isEmpty()) {
                setSoDT(soDTMoi);
            }
            if (emailMoi != null && !emailMoi.trim().isEmpty()) {
                setEmail(emailMoi);
            }
            if (diaChiMoi != null && !diaChiMoi.trim().isEmpty()) {
                setDiaChi(diaChiMoi);
            }
            if (gioiTinhMoi != null && !gioiTinhMoi.trim().isEmpty()) {
                setGioiTinh(gioiTinhMoi);
            }
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    // THỐNG KÊ CÁ NHÂN
    public int getTongSoChuyenBay() {
        return veDaDat.size();
    }
    
    public int getSoChuyenBayDaBay() {
        return getVeDaBay().size();
    }
    
    public int getSoChuyenBaySapBay() {
        return getVeChuaBay().size();
    }
    
    public double getTongChiTieu() {
        double tong = 0;
        for (HoaDon hd : lichSuHoaDon) {
            if (hd.getTrangThai().equals(HoaDon.TT_DA_TT)) {
                tong += hd.getThanhTien();
            }
        }
        return tong;
    }
    
    public long getSoNgayThanhVien() {
        if (ngayDangKy == null) return 0;
        
        Date now = new Date();
        long diff = now.getTime() - ngayDangKy.getTime();
        return diff / (1000L * 60 * 60 * 24);
    }
    
    // UTILITY METHODS CHO GUI
    public Object[] toRowData() {
        return new Object[] {
            getMa(),
            getHoTen(),
            getSoDT(),
            getEmail(),
            hangKhachHang,
            diemTichLuy,
            String.format("%,.0f VND", getTongChiTieu()),
            getSoNgayThanhVien() + " ngày"
        };
    }
    
    public String getThongTinThanhVien() {
        return String.format(
            "Hạng: %s\nĐiểm tích lũy: %d\nTỷ lệ giảm giá: %.1f%%\nTổng chi tiêu tháng này: %,.0f VND\nNgày đăng ký: %s\nSố ngày thành viên: %d",
            hangKhachHang, diemTichLuy, tinhTyLeGiamGia() * 100, getTongChiTieuThang(), ngayDangKy, getSoNgayThanhVien()
        );
    }
    
    public String getThongKeCaNhan() {
        return String.format(
            "Tổng số chuyến bay: %d\nĐã bay: %d\nSắp bay: %d\nTổng chi tiêu: %,.0f VND",
            getTongSoChuyenBay(), getSoChuyenBayDaBay(), getSoChuyenBaySapBay(), getTongChiTieu()
        );
    }
    // GETTERS AND SETTERS
    public String getHangKhachHang() {
         return hangKhachHang; 
    }
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
    }
    
    public Date getNgayDangKy() { return ngayDangKy; }
    public void setNgayDangKy(Date ngayDangKy) { 
        if (ngayDangKy != null && ngayDangKy.after(new Date())) {
            throw new IllegalArgumentException("Ngày đăng ký không thể ở tương lai");
        }
        this.ngayDangKy = ngayDangKy;
    }
    
    // Additional utility methods for GUI
    public boolean isHangBronze() { return HANG_BRONZE.equals(hangKhachHang); }
    public boolean isHangSilver() { return HANG_SILVER.equals(hangKhachHang); }
    public boolean isHangGold() { return HANG_GOLD.equals(hangKhachHang); }
    public boolean isHangPlatinum() { return HANG_PLATINUM.equals(hangKhachHang); }
    
    public String getHangKhachHangText() {
        switch (hangKhachHang) {
            case HANG_PLATINUM: return "Bạch Kim";
            case HANG_GOLD: return "Vàng";
            case HANG_SILVER: return "Bạc";
            default: return "Đồng";
        }
    }
}