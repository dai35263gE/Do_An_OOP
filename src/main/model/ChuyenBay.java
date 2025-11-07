package model;

import java.util.*;
import java.text.SimpleDateFormat;

public class ChuyenBay {
    private String maChuyen;
    private String diemDi;
    private String diemDen;
    private Date gioKhoiHanh;
    private Date gioDen;
    private int soGhe;
    private int soGheTrong;
    private String maMayBay;
    private double giaCoBan;
    private String trangThai;
    private List<VeMayBay> danhSachVe;
    
    public static final String TRANG_THAI_CHUA_BAY = "CHUA_BAY";
    public static final String TRANG_THAI_DANG_BAY = "DANG_BAY";
    public static final String TRANG_THAI_DA_BAY = "DA_BAY";
    public static final String TRANG_THAI_HUY = "HUY";
    public static final String TRANG_THAI_DA_DAT_HET = "DA_DAT_HET";
    
    public ChuyenBay(String maChuyen, String diemDi, String diemDen, Date gioKhoiHanh, Date gioDen, int soGhe, int soGheTrong, String maMayBay, double giaCoBan) {
        setMaChuyen(maChuyen);
        setDiemDi(diemDi);
        setDiemDen(diemDen);
        setGioKhoiHanh(gioKhoiHanh);
        setGioDen(gioDen);
        setSoGhe(soGhe);
        setSoGheTrong(soGheTrong);
        setMaMayBay(maMayBay);
        setGiaCoBan(giaCoBan);
        this.trangThai = TRANG_THAI_CHUA_BAY;
        this.danhSachVe = new ArrayList<>();
        capNhatTrangThai();
    }
    
    // BUSINESS METHODS - Tối ưu cho GUI
    public boolean datGhe() {
        if (!conGheTrong()) {
            return false;
        }
        soGheTrong--;
        capNhatTrangThai();
        return true;
    }
    
    public boolean huyGhe() {
        if (soGheTrong >= soGhe) {
            return false;
        }
        soGheTrong++;
        capNhatTrangThai();
        return true;
    }
    
    public boolean themVe(VeMayBay ve) {
        if (ve == null || !datGhe()) {
            return false;
        }
        
        danhSachVe.add(ve);
        return true;
    }
    
    public boolean xoaVe(VeMayBay ve) {
        if (ve == null || !danhSachVe.contains(ve)) {
            return false;
        }
        
        if (!huyGhe()) {
            return false;
        }
        
        danhSachVe.remove(ve);
        return true;
    }
    
    public boolean kiemTraGheDaDat(String soGhe) {
        return danhSachVe.stream()
                .anyMatch(ve -> ve.getSoGhe().equals(soGhe) && !ve.isDaHuy());
    }
    
    public List<String> getDanhSachGheTrong() {
        List<String> gheTrong = new ArrayList<>();
        int soHang = (int) Math.ceil(soGhe / 4.0);
        for (int i = 1; i <= soHang; i++) {
            for (char c = 'A'; c <= 'D'; c++) {
                if (gheTrong.size() >= soGheTrong) break;
                String soGhe = i + String.valueOf(c);
                if (!kiemTraGheDaDat(soGhe)) {
                    gheTrong.add(soGhe);
                }
            }
        }
        return gheTrong;
    }
    
    public List<String> getDanhSachGheDaDat() {
        List<String> gheDaDat = new ArrayList<>();
        for (VeMayBay ve : danhSachVe) {
            if (!ve.isDaHuy()) {
                gheDaDat.add(ve.getSoGhe());
            }
        }
        return gheDaDat;
    }
    
    public int getSoGheTrong() {
        return soGheTrong;
    }
    
    public void setSoGheTrong(int soGheTrong) {
        if (soGheTrong < 0 || soGheTrong > soGhe) {
            throw new IllegalArgumentException("Số ghế trống không hợp lệ");
        }
        this.soGheTrong = soGheTrong;
    }
    
    public int getSoGheDaDat() {
        return soGhe - soGheTrong;
    }
    
    public boolean conGheTrong() {
        return soGheTrong > 0;
    }
    
    public boolean coTheDatVe() {
        return conGheTrong() && 
               (trangThai.equals(TRANG_THAI_CHUA_BAY) || trangThai.equals(TRANG_THAI_DA_DAT_HET));
    }
    
    public boolean coTheHuyVe() {
        if (gioKhoiHanh == null) return true;
        
        long thoiGianConLai = gioKhoiHanh.getTime() - System.currentTimeMillis();
        return thoiGianConLai > (4 * 60 * 60 * 1000); // 4 tiếng
    }
    
    // Cập nhật trạng thái tự động - tối ưu hóa
    public void capNhatTrangThai() {
    }
    
    public void capNhatTrangThaiBay() {
        capNhatTrangThai();
    }
    
    // Tìm kiếm và lọc vé - tối ưu với Stream API
    public VeMayBay timVeTheoMa(String maVe) {
        return danhSachVe.stream()
                .filter(ve -> ve.getMaVe().equals(maVe))
                .findFirst()
                .orElse(null);
    }
    
    public List<VeMayBay> timVeTheoTrangThai(String trangThai) {
        return danhSachVe.stream()
                .filter(ve -> ve.getTrangThai().equals(trangThai))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    public List<VeMayBay> getVeConHieuLuc() {
        return danhSachVe.stream()
                .filter(VeMayBay::coTheSuDung)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    // Tính toán thông tin
    public double tinhThoiGianBay() {
        if (gioKhoiHanh == null || gioDen == null) {
            return 0;
        }
        long diff = gioDen.getTime() - gioKhoiHanh.getTime();
        return diff / (1000.0 * 60); // Trả về phút
    }
    
    public String getThoiGianBayFormatted() {
        double phut = tinhThoiGianBay();
        int gio = (int) (phut / 60);
        int phutConLai = (int) (phut % 60);
        return String.format("%dh %02dm", gio, phutConLai);
    }
    
    public double tinhDoanhThu() {
        return danhSachVe.stream()
                .filter(VeMayBay::isDaThanhToan)
                .mapToDouble(VeMayBay::tinhTongTien)
                .sum();
    }
    
    public boolean kiemTraChuyenBayHopLe() {
        Date now = new Date();
        return gioKhoiHanh != null && 
               gioDen != null && 
               gioKhoiHanh.before(gioDen) && 
               gioKhoiHanh.after(now) &&
               soGhe > 0 &&
               giaCoBan > 0;
    }
    
    // Utility methods cho GUI - CẢI TIẾN QUAN TRỌNG
    public Object[] toRowData() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return new Object[] {
            maChuyen,
            diemDi,
            diemDen,
            dateFormat.format(gioKhoiHanh),
            dateFormat.format(gioDen),
            getSoGheDaDat() + "/" + soGhe,
            String.format("%,d VND", (int)giaCoBan),
            getTrangThaiHienThi(),
            getThoiGianBayFormatted(),
            String.format("%,d VND", (int)tinhDoanhThu()),
            getTyLeDatFormatted()
        };
    }
    
    public Object[] toRowDataTimKiem() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return new Object[] {
            maChuyen,
            diemDi + " → " + diemDen,
            dateFormat.format(gioKhoiHanh),
            soGheTrong + " ghế trống",
            String.format("%,d VND", (int)giaCoBan),
            getTrangThaiHienThi()
        };
    }
    
    public String getThongTinChiTiet() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return String.format(
            "Mã chuyến: %s\nĐiểm đi: %s\nĐiểm đến: %s\nGiờ khởi hành: %s\nGiờ đến: %s\n" +
            "Số ghế: %d/%d (%.1f%%)\nMáy bay: %s\nGiá cơ bản: %s\nTrạng thái: %s\nThời gian bay: %s\nDoanh thu: %s",
            maChuyen, diemDi, diemDen, dateFormat.format(gioKhoiHanh), dateFormat.format(gioDen), 
            getSoGheDaDat(), soGhe, getTyLeDat(), maMayBay, 
            String.format("%,d VND", (int)giaCoBan), getTrangThaiHienThi(), 
            getThoiGianBayFormatted(), String.format("%,d VND", (int)tinhDoanhThu())
        );
    }
    
    public String getThongTinTimKiem() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return String.format("%s - %s → %s - %s - %d ghế trống - %s", 
            maChuyen, diemDi, diemDen, dateFormat.format(gioKhoiHanh), 
            soGheTrong, String.format("%,d VND", (int)giaCoBan));
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ChuyenBay that = (ChuyenBay) obj;
        return maChuyen != null && maChuyen.equals(that.maChuyen);
    }
    
    @Override
    public int hashCode() {
        return maChuyen != null ? maChuyen.hashCode() : 0;
    }
    
    // GETTERS AND SETTERS với VALIDATION
    public String getMaChuyen() { return maChuyen; }
    public void setMaChuyen(String maChuyen) { 
        if (maChuyen == null || maChuyen.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã chuyến không được để trống");
        }
        this.maChuyen = maChuyen.trim().toUpperCase(); 
    }
    
    public String getDiemDi() { return diemDi; }
    public void setDiemDi(String diemDi) { 
        if (diemDi == null || diemDi.trim().isEmpty()) {
            throw new IllegalArgumentException("Điểm đi không được để trống");
        }
        this.diemDi = diemDi.trim(); 
    }
    
    public String getDiemDen() { return diemDen; }
    public void setDiemDen(String diemDen) { 
        if (diemDen == null || diemDen.trim().isEmpty()) {
            throw new IllegalArgumentException("Điểm đến không được để trống");
        }
        this.diemDen = diemDen.trim(); 
    }
    
    public Date getGioKhoiHanh() { return gioKhoiHanh; }
    public void setGioKhoiHanh(Date gioKhoiHanh) { 
        if (gioKhoiHanh != null && gioDen != null && gioKhoiHanh.after(gioDen)) {
            throw new IllegalArgumentException("Giờ khởi hành phải trước giờ đến");
        }
        this.gioKhoiHanh = gioKhoiHanh; 
    }
    
    public Date getGioDen() { return gioDen; }
    public void setGioDen(Date gioDen) { 
        if (gioKhoiHanh != null && gioDen != null && gioDen.before(gioKhoiHanh)) {
            throw new IllegalArgumentException("Giờ đến phải sau giờ khởi hành");
        }
        this.gioDen = gioDen; 
    }
    
    public int getSoGhe() { return soGhe; }
    public void setSoGhe(int soGhe) { 
        if (soGhe <= 0) {
            throw new IllegalArgumentException("Số ghế phải lớn hơn 0");
        }
        this.soGhe = soGhe; 
    }
    
    public String getMaMayBay() { return maMayBay; }
    public void setMaMayBay(String maMayBay) { 
        if (maMayBay == null || maMayBay.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã máy bay không được để trống");
        }
        this.maMayBay = maMayBay.trim().toUpperCase(); 
    }
    
    public double getGiaCoBan() { return giaCoBan; }
    public void setGiaCoBan(double giaCoBan) { 
        if (giaCoBan < 0) {
            throw new IllegalArgumentException("Giá cơ bản không được âm");
        }
        this.giaCoBan = giaCoBan; 
    }
    
    public String getTrangThai() { 
        capNhatTrangThai();
        return trangThai; 
    }
    
    public String getTrangThaiHienThi() {
        switch (getTrangThai()) {
            case TRANG_THAI_CHUA_BAY: return "Chưa bay";
            case TRANG_THAI_DANG_BAY: return "Đang bay";
            case TRANG_THAI_DA_BAY: return "Đã bay";
            case TRANG_THAI_HUY: return "Đã hủy";
            case TRANG_THAI_DA_DAT_HET: return "Hết chỗ";
            default: return "Không xác định";
        }
    }
    
    public void setTrangThai(String trangThai) { 
        this.trangThai = trangThai; 
    }
    
    public List<VeMayBay> getDanhSachVe() { 
        return new ArrayList<>(danhSachVe); 
    }
    
    // Additional utility methods for GUI
    public boolean isChuaBay() { return TRANG_THAI_CHUA_BAY.equals(getTrangThai()); }
    public boolean isDangBay() { return TRANG_THAI_DANG_BAY.equals(getTrangThai()); }
    public boolean isDaBay() { return TRANG_THAI_DA_BAY.equals(getTrangThai()); }
    public boolean isHuy() { return TRANG_THAI_HUY.equals(getTrangThai()); }
    public boolean isDaDatHet() { return TRANG_THAI_DA_DAT_HET.equals(getTrangThai()); }
    
    public double getTyLeDat() {
        return soGhe > 0 ? (double) getSoGheDaDat() / soGhe * 100 : 0;
    }
    
    public String getTyLeDatFormatted() {
        return String.format("%.1f%%", getTyLeDat());
    }
    
    public long getThoiGianConLai() {
        if (gioKhoiHanh == null) return Long.MAX_VALUE;
        return gioKhoiHanh.getTime() - System.currentTimeMillis();
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
        } else if (minutes > 0) {
            return String.format("%d phút", minutes);
        } else {
            return "Sắp cất cánh";
        }
    }
    
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("maChuyen", maChuyen);
        map.put("diemDi", diemDi);
        map.put("diemDen", diemDen);
        map.put("gioKhoiHanh", gioKhoiHanh);
        map.put("gioDen", gioDen);
        map.put("soGhe", soGhe);
        map.put("soGheTrong", soGheTrong);
        map.put("maMayBay", maMayBay);
        map.put("giaCoBan", giaCoBan);
        map.put("trangThai", trangThai);
        map.put("trangThaiHienThi", getTrangThaiHienThi());
        map.put("thoiGianBay", getThoiGianBayFormatted());
        map.put("doanhThu", tinhDoanhThu());
        map.put("tyLeDat", getTyLeDat());
        return map;
    }
}