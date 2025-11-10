package Sevice;

import model.*;
import java.util.*;

public class QuanLyBanVeMayBay {
    private DanhSachVeMayBay dsVe;
    private DanhSachChuyenBay dsChuyenBay;
    private DanhSachKhachHang dsKhachHang;
    private DanhSachHoaDon dsHoaDon;
    
    // Static properties for tracking
    private static int soLanTruyCap = 0;
    private static final String PHIEN_BAN = "1.0.0";
    
    public QuanLyBanVeMayBay() {
        this.dsVe = new DanhSachVeMayBay();
        this.dsChuyenBay = new DanhSachChuyenBay();
        this.dsKhachHang = new DanhSachKhachHang();
        this.dsHoaDon = new DanhSachHoaDon();
        soLanTruyCap++;
    }
    
    public static int getSoLanTruyCap() {
        return soLanTruyCap;
    }
    public static String getPhienBan() {
        return PHIEN_BAN;
    }
    
    
    public Map<String, Object> thongKeTongQuan() {
        Map<String, Object> thongKe = new HashMap<>();
        
        thongKe.put("tongVe", dsVe.demSoLuong());
        thongKe.put("tongChuyenBay", dsChuyenBay.demSoLuong());
        thongKe.put("tongKhachHang", dsKhachHang.demSoLuong());
        thongKe.put("tongDoanhThu", dsHoaDon.tinhTongDoanhThu());
        thongKe.put("veThuongGia", dsVe.demSoLuongTheoLoai("VeThuongGia"));
        thongKe.put("vePhoThong", dsVe.demSoLuongTheoLoai("VePhoThong"));
        thongKe.put("veTietKiem", dsVe.demSoLuongTheoLoai("VeTietKiem"));
        int tongGhe = 0;
        int gheTrong = 0;
        for (ChuyenBay cb : dsChuyenBay.getDanhSach()) {
            tongGhe += cb.getSoGhe();
            gheTrong += cb.getSoGheTrong();
        }
        double tiLeLapDay = tongGhe > 0 ? (1 - (double)gheTrong / tongGhe) * 100 : 0;
        thongKe.put("tiLeLapDay", tiLeLapDay);
        
        return thongKe;
    }
    
    public Map<String, Double> thongKeDoanhThu() {
        Map<String, Double> doanhThu = new HashMap<>();
        
        doanhThu.put("thuongGia", dsVe.tinhDoanhThuTheoLoai("VeThuongGia"));
        doanhThu.put("phoThong", dsVe.tinhDoanhThuTheoLoai("VePhoThong"));
        doanhThu.put("tietKiem", dsVe.tinhDoanhThuTheoLoai("VeTietKiem"));
        doanhThu.put("tongCong", dsHoaDon.tinhTongDoanhThu());
        
        return doanhThu;
    }
    
    // Phương thức tìm kiếm cho GUI
    public List<VeMayBay> timKiemVe(Map<String, Object> filters) {
        return dsVe.timKiemDaTieuChi(filters);
    }
    
    public List<ChuyenBay> timKiemChuyenBay(Map<String, Object> filters) {
        return dsChuyenBay.timKiemChuyenBay(filters);
    }
    
    public List<KhachHang> timKiemKhachHang(Map<String, Object> filters) {
        return dsKhachHang.timKiemKhachHang(filters);
    }
    
    // Phương thức thêm/xóa/sửa
    public boolean themVe(VeMayBay ve) {
        return dsVe.them(ve);
    }
    
    public boolean xoaVe(String maVe) {
        return dsVe.xoa(maVe);
    }
    
    public boolean suaVe(String maVe, VeMayBay ve) {
        return dsVe.sua(maVe, ve);
    }
    
    public boolean themChuyenBay(ChuyenBay chuyenBay) {
        return dsChuyenBay.them(chuyenBay);
    }
    
    public boolean xoaChuyenBay(String maChuyen) {
        return dsChuyenBay.xoa(maChuyen);
    }
    
    public boolean themKhachHang(KhachHang khachHang) {
        return dsKhachHang.them(khachHang);
    }
    
    public boolean xoaKhachHang(String maKH) {
        return dsKhachHang.xoa(maKH);
    }
    
    public boolean taoHoaDon(HoaDon hoaDon) {
        return dsHoaDon.them(hoaDon);
    }
    
    public void docDuLieuTuFile() {
        try {
            dsVe.docFile("src/resources/data/3_VeMayBays.xml");
            dsChuyenBay.docFile("src/resources/data/1_ChuyenBays.xml");
            dsKhachHang.docFile("src/resources/data/2_KhachHangs.xml");
            dsHoaDon.docFile("src/resources/data/4_HoaDons.xml");
        } catch (Exception e) {
            System.out.println("Lỗi khi đọc dữ liệu từ file: " + e.getMessage());
        }
    }
    
    public void ghiDuLieuRaFile() {
        try {
            dsChuyenBay.ghiFile("src/resources/data/1_ChuyenBays.xml");
            dsKhachHang.ghiFile("src/resources/data/2_KhachHangs.xml");
            dsHoaDon.ghiFile("src/resources/data/4_HoaDons.xml");
            System.out.println("Đã ghi dữ liệu ra file!");
        } catch (Exception e) {
            System.out.println("Lỗi khi ghi dữ liệu ra file: " + e.getMessage());
        }
    }
    
    // Getter methods for GUI
    public DanhSachVeMayBay getDsVe() { return dsVe; }
    public DanhSachChuyenBay getDsChuyenBay() { return dsChuyenBay; }
    public DanhSachKhachHang getDsKhachHang() { return dsKhachHang; }
    public DanhSachHoaDon getDsHoaDon() { return dsHoaDon; }
    
    public static void main(String[] args) {
        QuanLyBanVeMayBay quanly = new QuanLyBanVeMayBay();
        quanly.docDuLieuTuFile();
        
        for (String key : quanly.thongKeTongQuan().keySet()) {
            String value = quanly.thongKeTongQuan().get(key).toString();
            System.out.println(key + " : " + value);
        }
    }
}