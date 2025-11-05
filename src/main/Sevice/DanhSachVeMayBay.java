/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package Sevice;

/**
 *
 * @author HP
 */
// File: DanhSachVeMayBay.java
import java.util.*;

import model.VeMayBay;
import model.VeThuongGia;
import model.VePhoThong;
import model.VeTietKiem;
import repository.IFileHandler;
import repository.IQuanLy;
import repository.IThongKe;
import repository.XMLUtils;
import java.text.SimpleDateFormat;

public class DanhSachVeMayBay implements IQuanLy<VeMayBay>, IFileHandler, IThongKe {
    private List<VeMayBay> danhSach;
    private static final int MAX_SIZE = 10000;
    
    public DanhSachVeMayBay() {
        this.danhSach = new ArrayList<>();
    }
    
    // ========== IMPLEMENT IQUANLY ==========
    @Override
    public boolean them(VeMayBay ve) {
        if (danhSach.size() >= MAX_SIZE) {
            System.out.println("Danh sách vé đã đầy! Không thể thêm mới.");
            return false;
        }
        
        if (tonTai(ve.getMaVe())) {
            System.out.println("Mã vé '" + ve.getMaVe() + "' đã tồn tại! Không thể thêm.");
            return false;
        }
        
        if (!ve.kiemTraVeHopLe()) {
            System.out.println("Thông tin vé không hợp lệ! Không thể thêm.");
            return false;
        }
        
        danhSach.add(ve);
        System.out.println("✅ Thêm vé '" + ve.getMaVe() + "' thành công!");
        return true;
    }
    
    @Override
    public boolean xoa(String maVe) {
        VeMayBay ve = timKiemTheoMa(maVe);
        if (ve == null) {
            System.out.println("❌ Không tìm thấy vé với mã: " + maVe);
            return false;
        }
        
        // Kiểm tra nếu vé đã hoàn tất thì không thể xóa
        if (ve.getTrangThai().equals(VeMayBay.TRANG_THAI_HOAN_TAT)) {
            System.out.println("❌ Không thể xóa vé đã hoàn tất!");
            return false;
        }
        
        if (danhSach.remove(ve)) {
            System.out.println("✅ Xóa vé '" + maVe + "' thành công!");
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean sua(String maVe, VeMayBay veMoi) {
        for (int i = 0; i < danhSach.size(); i++) {
            if (danhSach.get(i).getMaVe().equals(maVe)) {
                // Kiểm tra nếu vé đã hoàn tất thì chỉ cho sửa một số thông tin
                VeMayBay veCu = danhSach.get(i);
                if (veCu.getTrangThai().equals(VeMayBay.TRANG_THAI_HOAN_TAT)) {
                    System.out.println("⚠️ Vé đã hoàn tất, chỉ có thể cập nhật một số thông tin!");
                    // Cho phép cập nhật thông tin liên lạc, nhưng không cho thay đổi chuyến bay, giá
                    veCu.setHoTenKH(veMoi.getHoTenKH());
                    veCu.setCmnd(veMoi.getCmnd());
                } else {
                    danhSach.set(i, veMoi);
                }
                System.out.println("✅ Cập nhật vé '" + maVe + "' thành công!");
                return true;
            }
        }
        System.out.println("❌ Không tìm thấy vé với mã: " + maVe);
        return false;
    }
    
    @Override
    public VeMayBay timKiemTheoMa(String maVe) {
        return danhSach.stream()
                      .filter(ve -> ve.getMaVe().equalsIgnoreCase(maVe))
                      .findFirst()
                      .orElse(null);
    }
    
    @Override
    public List<VeMayBay> timKiemTheoTen(String ten) {
        return danhSach.stream()
                      .filter(ve -> ve.getHoTenKH().toLowerCase().contains(ten.toLowerCase()))
                      .toList();
    }
    
    @Override
    public VeMayBay timKiemTheoCMND(String cmnd) {
        return danhSach.stream().filter(ve -> ve.getCmnd().equals(cmnd)).findFirst().get();
    }
    
    @Override
    public List<VeMayBay> timKiemTheoChuyenBay(String maChuyen) {
        return danhSach.stream()
                      .filter(ve -> ve.getMaChuyen().equalsIgnoreCase(maChuyen))
                      .toList();
    }
    
    @Override
    public List<VeMayBay> timKiemTheoKhoangGia(double min, double max) {
        return danhSach.stream().filter(ve -> ve.getGiaVe() >= min && ve.getGiaVe() <= max).toList();
    }
    
    @Override
    public List<VeMayBay> timKiemTheoNgayBay(Date ngay) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String ngayCanTim = sdf.format(ngay);
        
        return danhSach.stream()
                      .filter(ve -> sdf.format(ve.getNgayBay()).equals(ngayCanTim))
                      .toList();
    }
    
    // TÌM KIẾM NÂNG CAO - ĐA TIÊU CHÍ
    public List<VeMayBay> timKiemDaTieuChi(Map<String, Object> filters) {
        List<VeMayBay> ketQua = new ArrayList<>(danhSach);
        
        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            switch (key) {
                case "loaiVe":
                    ketQua.removeIf(ve -> !ve.loaiVe().equals(value));
                    break;
                case "trangThai":
                    ketQua.removeIf(ve -> !ve.getTrangThai().equals(value));
                    break;
                case "maChuyen":
                    ketQua.removeIf(ve -> !ve.getMaChuyen().equals(value));
                    break;
                case "tuNgay":
                    Date tuNgay = (Date) value;
                    ketQua.removeIf(ve -> ve.getNgayBay().before(tuNgay));
                    break;
                case "denNgay":
                    Date denNgay = (Date) value;
                    ketQua.removeIf(ve -> ve.getNgayBay().after(denNgay));
                    break;
                case "giaMin":
                    double giaMin = (double) value;
                    ketQua.removeIf(ve -> ve.getGiaVe() < giaMin);
                    break;
                case "giaMax":
                    double giaMax = (double) value;
                    ketQua.removeIf(ve -> ve.getGiaVe() > giaMax);
                    break;
                case "maKH":
                    ketQua.removeIf(ve -> !ve.getMaKH().equals(value));
                    break;
            }
        }
        
        return ketQua;
    }
    
    @Override
    public void hienThiTatCa() {
        if (danhSach.isEmpty()) {
            System.out.println("📭 Danh sách vé trống!");
            return;
        }
        
        System.out.println("====== 📋 DANH SÁCH TẤT CẢ VÉ (" + danhSach.size() + " vé) ======");
        for (int i = 0; i < danhSach.size(); i++) {
            VeMayBay ve = danhSach.get(i);
            System.out.printf("%d. %s - %s - %s - %s - %,.0f VND - %s\n",
                i + 1, ve.getMaVe(), ve.getHoTenKH(), ve.loaiVe(),
                new SimpleDateFormat("dd/MM/yyyy HH:mm").format(ve.getNgayBay()),
                ve.getGiaVe(), ve.getTrangThai());
        }
    }
    
    @Override
    public void hienThiTheoTrangThai(String trangThai) {
        List<VeMayBay> ketQua = danhSach.stream()
                                       .filter(ve -> ve.getTrangThai().equals(trangThai))
                                       .toList();
        
        if (ketQua.isEmpty()) {
            System.out.println("📭 Không có vé nào với trạng thái: " + trangThai);
            return;
        }
        
        System.out.println("====== 📋 DANH SÁCH VÉ " + trangThai + " (" + ketQua.size() + " vé) ======");
        for (int i = 0; i < ketQua.size(); i++) {
            VeMayBay ve = ketQua.get(i);
            System.out.printf("%d. %s - %s - %s - %,.0f VND\n",
                i + 1, ve.getMaVe(), ve.getHoTenKH(), ve.loaiVe(), ve.getGiaVe());
        }
    }
    
    @Override
    public int demSoLuong() {
        return danhSach.size();
    }
    
    @Override
    public boolean tonTai(String ma) {
        return danhSach.stream().anyMatch(ve -> ve.getMaVe().equalsIgnoreCase(ma));
    }
    
    @Override
    public void sapXepTheoMa() {
        danhSach.sort(Comparator.comparing(VeMayBay::getMaVe));
        System.out.println("✅ Đã sắp xếp theo mã vé");
    }
    
    @Override
    public void sapXepTheoGia() {
        danhSach.sort(Comparator.comparingDouble(VeMayBay::getGiaVe));
        System.out.println("✅ Đã sắp xếp theo giá vé");
    }
    
    @Override
    public void sapXepTheoNgayBay() {
        danhSach.sort(Comparator.comparing(VeMayBay::getNgayBay));
        System.out.println("✅ Đã sắp xếp theo ngày bay");
    }
    
    // ========== IMPLEMENT IFILEHANDLER - CẢI THIỆN VỚI XML ==========
    @Override
public boolean docFile(String tenFile) {
    return docFileXML1(tenFile);
}

// PHƯƠNG THỨC ĐỌC FILE XML - ĐÃ SỬA
boolean docFileXML1(String tenFile) {
    try {
        System.out.println("🔄 Bắt đầu đọc file vé máy bay: " + tenFile);
        
        List<Map<String, String>> dataList = XMLUtils.docFileXML(tenFile);
        
        if (dataList == null || dataList.isEmpty()) {
            System.out.println("❌ Không có dữ liệu trong file XML hoặc file không tồn tại");
            return false;
        }
        
        System.out.println("📖 Tìm thấy " + dataList.size() + " bản ghi trong file XML");
        
        int countSuccess = 0;
        int countError = 0;
        int countDuplicate = 0;
        
        for (Map<String, String> data : dataList) {
            try {
                
                VeMayBay ve = taoVeTuDataXML(data);
                if (ve == null) {
                    System.out.println("❌ Không thể tạo vé từ dữ liệu: " + data.get("MaVe"));
                    countError++;
                    continue;
                }
                
                // KIỂM TRA TRÙNG MÃ VÉ
                if (tonTai(ve.getMaVe())) {
                    System.out.println("⚠️ Bỏ qua vé trùng mã: " + ve.getMaVe());
                    countDuplicate++;
                    continue;
                }
                
                // THÊM VÀO DANH SÁCH
                danhSach.add(ve);
                countSuccess++;
                System.out.println("✅ Đã thêm vé: " + ve.getMaVe() + " - " + ve.loaiVe());
                
            } catch (Exception e) {
                System.out.println("❌ Lỗi xử lý vé: " + data.get("MaVe") + " - " + e.getMessage());
                countError++;
            }
        }
        
        // THỐNG KÊ KẾT QUẢ
        System.out.println("\n🎉 KẾT QUẢ ĐỌC FILE:");
        System.out.println("✅ Thành công: " + countSuccess + " vé");
        System.out.println("❌ Lỗi: " + countError + " vé");
        System.out.println("⚠️ Trùng: " + countDuplicate + " vé");
        System.out.println("📊 Tổng trong danh sách: " + danhSach.size() + " vé");
        
        return countSuccess > 0;
        
    } catch (Exception e) {
        System.out.println("💥 LỖI NGHIÊM TRỌNG khi đọc file: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}
    
    // Phương thức tạo vé từ dữ liệu XML
    private VeMayBay taoVeTuDataXML(Map<String, String> data) {
        try {
            String loaiVe = data.get("LoaiVe");
            String maVe = data.get("MaVe");
            String maKH = data.get("MaKH");
            String hoTenKH = data.get("HoTenKH");
            String cmnd = data.get("CMND");
            Date ngayBay = XMLUtils.stringToDate(data.get("NgayBay"));
            double giaVe = XMLUtils.stringToDouble(data.get("GiaVe"));
            String maChuyen = data.get("MaChuyen");
            String soGhe = data.get("SoGhe");
            String trangThai = data.get("TrangThai");
            
            // Tạo vé theo loại
            switch (loaiVe) {
                case "VeThuongGia":
                    return new VeThuongGia(
                        maVe,maKH, hoTenKH, cmnd, ngayBay, giaVe, maChuyen, soGhe,trangThai,
                        data.get("DichVuDacBiet"),
                        XMLUtils.stringToDouble(data.get("PhuThu")),
                        XMLUtils.stringToInt(data.get("SoKgHanhLyMienPhi")),
                        XMLUtils.stringToBoolean(data.get("PhongChoVIP")),
                        data.get("LoaiDoUong")
                    );
                    
                case "VePhoThong":
                    return new VePhoThong(
                        maVe,maKH, hoTenKH, cmnd, ngayBay, giaVe, maChuyen, soGhe,trangThai,
                        XMLUtils.stringToBoolean(data.get("HanhLyXachTay")),
                        XMLUtils.stringToInt(data.get("SoKgHanhLyKyGui")),
                        XMLUtils.stringToDouble(data.get("PhiHanhLy")),
                        data.get("LoaiGhe"),
                        XMLUtils.stringToBoolean(data.get("DoAn"))
                    );
                    
                case "VeTietKiem":
                    return new VeTietKiem(
                        maVe,maKH, hoTenKH, cmnd, ngayBay, giaVe, maChuyen, soGhe,trangThai,
                        XMLUtils.stringToInt(data.get("SoGioDatTruoc")),
                        XMLUtils.stringToDouble(data.get("TyLeGiam")),
                        XMLUtils.stringToBoolean(data.get("HoanDoi")),
                        XMLUtils.stringToDouble(data.get("PhiHoanDoi")),
                        data.get("DieuKienGia")
                    );
                    
                default:
                    System.out.println("❌ Loại vé không hợp lệ: " + loaiVe);
                    return null;
            }
        } catch (Exception e) {
            System.out.println("❌ Loi tao ve tu XML data: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public boolean ghiFile(String tenFile) {
        try {
            List<Map<String, String>> dataList = new ArrayList<>();
            
            for (VeMayBay ve : danhSach) {
                Map<String, String> data = new HashMap<>();
                
                // Thông tin chung
                data.put("MaVe", ve.getMaVe());
                data.put("MaKH", ve.getMaKH());
                data.put("HoTenKH", ve.getHoTenKH());
                data.put("CMND", ve.getCmnd());
                data.put("NgayBay", XMLUtils.dateToString(ve.getNgayBay()));
                data.put("GiaVe", String.valueOf(ve.getGiaVe()));
                data.put("MaChuyen", ve.getMaChuyen());
                data.put("SoGhe", ve.getSoGhe());
                data.put("TrangThai", ve.getTrangThai());
                data.put("NgayDat", XMLUtils.dateToString(ve.getNgayDat()));
                
                // Thông tin riêng theo loại vé
                if (ve instanceof VeThuongGia vtg) {
                    data.put("LoaiVe", "VeThuongGia");
                    data.put("DichVuDacBiet", vtg.getDichVuDacBiet());
                    data.put("PhuThu", String.valueOf(vtg.getPhuThu()));
                    data.put("SoKgHanhLyMienPhi", String.valueOf(vtg.getSoKgHanhLyMienPhi()));
                    data.put("PhongChoVIP", String.valueOf(vtg.isPhongChoVIP()));
                    data.put("LoaiDoUong", vtg.getLoaiDoUong());
                } 
                else if (ve instanceof VePhoThong vpt) {
                    data.put("LoaiVe", "VePhoThong");
                    data.put("HanhLyXachTay", String.valueOf(vpt.isHanhLyXachTay()));
                    data.put("SoKgHanhLyKyGui", String.valueOf(vpt.getSoKgHanhLyKyGui()));
                    data.put("PhiHanhLy", String.valueOf(vpt.getPhiHanhLy()));
                    data.put("LoaiGhe", vpt.getLoaiGhe());
                    data.put("DoAn", String.valueOf(vpt.isDoAn()));
                }
                else if (ve instanceof VeTietKiem vtk) {
                    data.put("LoaiVe", "VeTietKiem");
                    data.put("SoGioDatTruoc", String.valueOf(vtk.getSoGioDatTruoc()));
                    data.put("TyLeGiam", String.valueOf(vtk.getTyLeGiam()));
                    data.put("HoanDoi", String.valueOf(vtk.isHoanDoi()));
                    data.put("PhiHoanDoi", String.valueOf(vtk.getPhiHoanDoi()));
                    data.put("DieuKienGia", vtk.getDieuKienGia());
                }
                
                dataList.add(data);
            }
            
            boolean result = XMLUtils.ghiFileXML(tenFile, dataList, "VeMayBays");
            if (result) {
                System.out.println("✅ Ghi file XML thành công: " + danhSach.size() + " vé");
            }
            return result;
            
        } catch (Exception e) {
            System.out.println("❌ Lỗi ghi file XML: " + e.getMessage());
            return false;
        }
    }
    
    // ========== IMPLEMENT ITHONGKE - CẢI THIỆN ==========
    @Override
    public double tinhTongDoanhThu() {
        return danhSach.stream().filter(ve -> ve.getTrangThai().equals("HOAN TAT")).mapToDouble(VeMayBay::getGiaVe).sum();
    }
    
    @Override
    public int demSoLuongTheoLoai(String loai) {
        return (int) danhSach.stream()
                            .filter(ve -> ve.loaiVe().equals(loai))
                            .count();
    }
    
    @Override
    public double tinhDoanhThuTheoLoai(String loai) {
        return danhSach.stream().filter(ve -> ve.loaiVe().equals(loai) && 
                                   ve.getTrangThai().equals(VeMayBay.TRANG_THAI_HOAN_TAT))
                      .mapToDouble(VeMayBay::tinhTongTien)
                      .sum();
    }
    
    @Override
    public Map<String, Integer> thongKeTheoThang(int thang, int nam) {
        Map<String, Integer> thongKe = new HashMap<>();
        Calendar cal = Calendar.getInstance();
        
        for (VeMayBay ve : danhSach) {
            cal.setTime(ve.getNgayBay());
            int veThang = cal.get(Calendar.MONTH) + 1;
            int veNam = cal.get(Calendar.YEAR);
            
            if (veThang == thang && veNam == nam) {
                String loai = ve.loaiVe();
                thongKe.put(loai, thongKe.getOrDefault(loai, 0) + 1);
            }
        }
        
        return thongKe;
    }
    
    @Override
    public Map<String, Double> thongKeDoanhThuTheoThang(int thang, int nam) {
        Map<String, Double> thongKe = new HashMap<>();
        Calendar cal = Calendar.getInstance();
        
        for (VeMayBay ve : danhSach) {
            if (!ve.getTrangThai().equals(VeMayBay.TRANG_THAI_HOAN_TAT)) continue;
            
            cal.setTime(ve.getNgayBay());
            int veThang = cal.get(Calendar.MONTH) + 1;
            int veNam = cal.get(Calendar.YEAR);
            
            if (veThang == thang && veNam == nam) {
                String loai = ve.loaiVe();
                thongKe.put(loai, thongKe.getOrDefault(loai, 0.0) + ve.tinhTongTien());
            }
        }
        
        return thongKe;
    }
    
    @Override
    public Map<String, Integer> thongKeTheoChuyenBay() {
        Map<String, Integer> thongKe = new HashMap<>();
        for (VeMayBay ve : danhSach) {
            String chuyenBay = ve.getMaChuyen();
            thongKe.put(chuyenBay, thongKe.getOrDefault(chuyenBay, 0) + 1);
        }
        return thongKe;
    }
    
    @Override
    public Map<String, Double> thongKeDoanhThuTheoChuyenBay() {
        Map<String, Double> thongKe = new HashMap<>();
        for (VeMayBay ve : danhSach) {
            if (ve.getTrangThai().equals(VeMayBay.TRANG_THAI_HOAN_TAT)) {
                String chuyenBay = ve.getMaChuyen();
                thongKe.put(chuyenBay, thongKe.getOrDefault(chuyenBay, 0.0) + ve.tinhTongTien());
            }
        }
        return thongKe;
    }
    
    @Override
    public Map<String, Object> thongKeTheoKhoangNgay(Date from, Date to) {
        Map<String, Object> thongKe = new HashMap<>();
        int tongVe = 0;
        double tongDoanhThu = 0;
        Map<String, Integer> theoLoai = new HashMap<>();
        Map<String, Double> doanhThuTheoLoai = new HashMap<>();
        
        for (VeMayBay ve : danhSach) {
            if (ve.getNgayBay().after(from) && ve.getNgayBay().before(to)) {
                tongVe++;
                if (ve.getTrangThai().equals(VeMayBay.TRANG_THAI_HOAN_TAT)) {
                    tongDoanhThu += ve.tinhTongTien();
                }
                
                // Thống kê theo loại
                String loai = ve.loaiVe();
                theoLoai.put(loai, theoLoai.getOrDefault(loai, 0) + 1);
                if (ve.getTrangThai().equals(VeMayBay.TRANG_THAI_HOAN_TAT)) {
                    doanhThuTheoLoai.put(loai, 
                        doanhThuTheoLoai.getOrDefault(loai, 0.0) + ve.tinhTongTien());
                }
            }
        }
        
        thongKe.put("tongVe", tongVe);
        thongKe.put("tongDoanhThu", tongDoanhThu);
        thongKe.put("theoLoai", theoLoai);
        thongKe.put("doanhThuTheoLoai", doanhThuTheoLoai);
        
        return thongKe;
    }
    
    @Override
    public Map<String, Integer> thongKeKhachHangThuongXuyen(int soChuyenToiThieu) {
        Map<String, Integer> khachHang = new HashMap<>();
        for (VeMayBay ve : danhSach) {
            if (ve.getTrangThai().equals(VeMayBay.TRANG_THAI_HOAN_TAT)) {
                String cmnd = ve.getCmnd();
                khachHang.put(cmnd, khachHang.getOrDefault(cmnd, 0) + 1);
            }
        }
        
        // Lọc những khách hàng có số chuyến >= ngưỡng
        return khachHang.entrySet().stream()
                       .filter(entry -> entry.getValue() >= soChuyenToiThieu)
                       .collect(HashMap::new, 
                               (m, e) -> m.put(e.getKey(), e.getValue()), 
                               Map::putAll);
    }
    
    @Override
    public double tinhTyLeDoanhThuTheoLoai() {
        // Đây là phương thức tổng quát, chi tiết được triển khai trong thongKeTyLeDoanhThu
        return 1.0;
    }
    
    @Override
    public Map<String, Double> thongKeTyLeDoanhThu() {
        Map<String, Double> tyLe = new HashMap<>();
        double tongDoanhThu = tinhTongDoanhThu();
        
        if (tongDoanhThu > 0) {
            String[] loaiVes = {"THƯƠNG GIA", "PHỔ THÔNG", "TIẾT KIỆM"};
            for (String loai : loaiVes) {
                double doanhThuLoai = tinhDoanhThuTheoLoai(loai);
                tyLe.put(loai, (doanhThuLoai / tongDoanhThu) * 100);
            }
        }
        
        return tyLe;
    }
    
    // ========== PHƯƠNG THỨC BỔ SUNG NÂNG CAO ==========
    public List<VeMayBay> getDanhSach() {
        return new ArrayList<>(danhSach);
    }
    
    public void xoaTatCa() {
        danhSach.clear();
        System.out.println("✅ Đã xóa tất cả vé!");
    }
    
    // Hủy vé với kiểm tra điều kiện
    public boolean huyVe(String maVe) {
        VeMayBay ve = timKiemTheoMa(maVe);
        if (ve == null) {
            System.out.println("❌ Không tìm thấy vé với mã: " + maVe);
            return false;
        }
        
        try {
            ve.huyVe();
            System.out.println("✅ Hủy vé '" + maVe + "' thành công!");
            return true;
        } catch (IllegalStateException e) {
            System.out.println("❌ Không thể hủy vé: " + e.getMessage());
            return false;
        }
    }
    
    // Hoàn tất vé (sau khi thanh toán)
    public boolean hoanTatVe(String maVe) {
        VeMayBay ve = timKiemTheoMa(maVe);
        if (ve == null) {
            System.out.println("❌ Không tìm thấy vé với mã: " + maVe);
            return false;
        }
        
        ve.hoanTatVe();
        System.out.println("✅ Hoàn tất vé '" + maVe + "' thành công!");
        return true;
    }
    
    // Kiểm tra vé có thể hủy
    public boolean kiemTraCoTheHuy(String maVe) {
        VeMayBay ve = timKiemTheoMa(maVe);
        return ve != null && ve.coTheHuy();
    }
    
    // Kiểm tra vé có thể đổi
    public boolean kiemTraCoTheDoi(String maVe) {
        VeMayBay ve = timKiemTheoMa(maVe);
        return ve != null && ve.coTheDoi();
    }
    
    // Cập nhật trạng thái bay cho tất cả vé
    public void capNhatTrangThaiBay() {
        int count = 0;
        for (VeMayBay ve : danhSach) {
            ve.capNhatTrangThaiBay();
            count++;
        }
        System.out.println("✅ Đã cập nhật trạng thái bay cho " + count + " vé");
    }
    
    // Hiển thị thống kê chi tiết
    public void hienThiThongKeChiTiet() {
        System.out.println("====== 📊 THỐNG KÊ CHI TIẾT HỆ THỐNG VÉ ======");
        System.out.println("📈 Tổng số vé: " + demSoLuong());
        System.out.println("💰 Tổng doanh thu: " + String.format("%,.0f VND", tinhTongDoanhThu()));
        
        // Thống kê theo loại vé
        System.out.println("\n📋 THỐNG KÊ THEO LOẠI VÉ:");
        String[] loaiVes = {"THƯƠNG GIA", "PHỔ THÔNG", "TIẾT KIỆM"};
        for (String loai : loaiVes) {
            int soLuong = demSoLuongTheoLoai(loai);
            double doanhThu = tinhDoanhThuTheoLoai(loai);
            System.out.printf("   %s: %d vé (%.1f%%), Doanh thu: %,.0f VND\n", 
                loai, soLuong, 
                (double) soLuong / demSoLuong() * 100,
                doanhThu);
        }
        
        // Thống kê theo trạng thái
        System.out.println("\n📊 THỐNG KÊ THEO TRẠNG THÁI:");
        long soVeDat = danhSach.stream().filter(v -> v.getTrangThai().equals(VeMayBay.TRANG_THAI_DAT)).count();
        long soVeHuy = danhSach.stream().filter(v -> v.getTrangThai().equals(VeMayBay.TRANG_THAI_HUY)).count();
        long soVeHoanTat = danhSach.stream().filter(v -> v.getTrangThai().equals(VeMayBay.TRANG_THAI_HOAN_TAT)).count();
        long soVeDaBay = danhSach.stream().filter(v -> v.getTrangThai().equals(VeMayBay.TRANG_THAI_DA_BAY)).count();
        
        System.out.printf("   🟡 Đặt: %d vé (%.1f%%)\n", soVeDat, (double) soVeDat / demSoLuong() * 100);
        System.out.printf("   🔴 Hủy: %d vé (%.1f%%)\n", soVeHuy, (double) soVeHuy / demSoLuong() * 100);
        System.out.printf("   🟢 Hoàn tất: %d vé (%.1f%%)\n", soVeHoanTat, (double) soVeHoanTat / demSoLuong() * 100);
        System.out.printf("   🟣 Đã bay: %d vé (%.1f%%)\n", soVeDaBay, (double) soVeDaBay / demSoLuong() * 100);
        
        // Tỷ lệ doanh thu theo loại
        System.out.println("\n💹 TỶ LỆ DOANH THU THEO LOẠI VÉ:");
        Map<String, Double> tyLeDoanhThu = thongKeTyLeDoanhThu();
        for (Map.Entry<String, Double> entry : tyLeDoanhThu.entrySet()) {
            System.out.printf("   %s: %.1f%%\n", entry.getKey(), entry.getValue());
        }
    }
    
    // ========== IMPLEMENT CÁC PHƯƠNG THỨC THỐNG KÊ NÂNG CAO ==========
    @Override
    public Map<String, Object> thongKeTongHop(Date from, Date to) {
        Map<String, Object> tongHop = new HashMap<>();
        
        // Lấy thống kê cơ bản
        Map<String, Object> thongKeCoBan = thongKeTheoKhoangNgay(from, to);
        tongHop.putAll(thongKeCoBan);
        
        // Thêm các chỉ số nâng cao
        double doanhThuTrungBinh = (int)thongKeCoBan.get("tongVe") > 0 ? 
            (double)thongKeCoBan.get("tongDoanhThu") / (int)thongKeCoBan.get("tongVe") : 0;
        tongHop.put("doanhThuTrungBinh", doanhThuTrungBinh);
        
        // Tỷ lệ hoàn thành
        long tongVeTrongKhoang = danhSach.stream()
            .filter(ve -> ve.getNgayBay().after(from) && ve.getNgayBay().before(to))
            .count();
        long veHoanTatTrongKhoang = danhSach.stream()
            .filter(ve -> ve.getNgayBay().after(from) && ve.getNgayBay().before(to) &&
                         ve.getTrangThai().equals(VeMayBay.TRANG_THAI_HOAN_TAT))
            .count();
        double tyLeHoanThanh = tongVeTrongKhoang > 0 ? 
            (double) veHoanTatTrongKhoang / tongVeTrongKhoang * 100 : 0;
        tongHop.put("tyLeHoanThanh", tyLeHoanThanh);
        
        return tongHop;
    }
    
    @Override
    public List<Map<String, Object>> thongKeTopKhachHang(int limit) {
        Map<String, Map<String, Object>> khachHangData = new HashMap<>();
        
        for (VeMayBay ve : danhSach) {
            if (ve.getTrangThai().equals(VeMayBay.TRANG_THAI_HOAN_TAT)) {
                String cmnd = ve.getCmnd();
                Map<String, Object> data = khachHangData.getOrDefault(cmnd, new HashMap<>());
                
                data.put("cmnd", cmnd);
                data.put("hoTen", ve.getHoTenKH());
                data.put("maKH", ve.getMaKH());
                data.put("soVe", (int)data.getOrDefault("soVe", 0) + 1);
                data.put("tongChiTieu", (double)data.getOrDefault("tongChiTieu", 0.0) + ve.tinhTongTien());
                
                khachHangData.put(cmnd, data);
            }
        }
        
        return khachHangData.values().stream()
                           .sorted((a, b) -> Double.compare((double)b.get("tongChiTieu"), (double)a.get("tongChiTieu")))
                           .limit(limit)
                           .toList();
    }
    
    @Override
    public Map<String, Integer> thongKeTheoGioTrongNgay() {
        Map<String, Integer> thongKe = new HashMap<>();
        Calendar cal = Calendar.getInstance();
        
        for (VeMayBay ve : danhSach) {
            cal.setTime(ve.getNgayBay());
            int gio = cal.get(Calendar.HOUR_OF_DAY);
            String khoangGio = String.format("%02d:00-%02d:00", gio, gio + 1);
            thongKe.put(khoangGio, thongKe.getOrDefault(khoangGio, 0) + 1);
        }
        
        return thongKe;
    }
    
    @Override
    public double tinhDoanhThuTrungBinhTheoChuyen() {
        Map<String, Double> doanhThuChuyen = thongKeDoanhThuTheoChuyenBay();
        if (doanhThuChuyen.isEmpty()) return 0;
        
        double tongDoanhThu = doanhThuChuyen.values().stream().mapToDouble(Double::doubleValue).sum();
        return tongDoanhThu / doanhThuChuyen.size();
    }
    
    public static void main(String[] args) {
        DanhSachVeMayBay ds = new DanhSachVeMayBay();
        ds.docFileXML1("src/resources/data/3_VeMayBays.xml");
        ds.hienThiTatCa();
        System.out.print((long)ds.tinhTongDoanhThu());
        
    }
}