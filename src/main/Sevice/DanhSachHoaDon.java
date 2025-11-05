package Sevice;

import java.util.*;
import java.text.SimpleDateFormat;

import repository.IFileHandler;
import repository.IQuanLy;
import model.HoaDon;
import repository.XMLUtils;

public class DanhSachHoaDon implements IQuanLy<HoaDon>, IFileHandler {
    private ArrayList<HoaDon> danhSach;
    
    public DanhSachHoaDon() {
        this.danhSach = new ArrayList<>();
    }
    
    // ========== IMPLEMENT IQUANLY ==========
    @Override
    public boolean them(HoaDon hoaDon) {
        if (tonTai(hoaDon.getMaHoaDon())) {
            System.out.println("Mã hóa đơn đã tồn tại!");
            return false;
        }
        danhSach.add(hoaDon);
        System.out.println("Thêm hóa đơn thành công!");
        return true;
    }
    
    @Override
    public boolean xoa(String maHoaDon) {
        for (Iterator<HoaDon> iterator = danhSach.iterator(); iterator.hasNext();) {
            HoaDon hd = iterator.next();
            if (hd.getMaHoaDon().equals(maHoaDon)) {
                if (hd.getTrangThai().equals(HoaDon.TT_DA_TT)) {
                    System.out.println("Không thể xóa hóa đơn đã thanh toán!");
                    return false;
                }
                iterator.remove();
                System.out.println("Xóa hóa đơn thành công!");
                return true;
            }
        }
        System.out.println("Không tìm thấy hóa đơn với mã: " + maHoaDon);
        return false;
    }
    
    @Override
    public boolean sua(String maHoaDon, HoaDon hoaDonMoi) {
        for (int i = 0; i < danhSach.size(); i++) {
            if (danhSach.get(i).getMaHoaDon().equals(maHoaDon)) {
                if (danhSach.get(i).getTrangThai().equals(HoaDon.TT_DA_TT)) {
                    System.out.println("Không thể sửa hóa đơn đã thanh toán!");
                    return false;
                }
                danhSach.set(i, hoaDonMoi);
                System.out.println("Cập nhật hóa đơn thành công!");
                return true;
            }
        }
        System.out.println("Không tìm thấy hóa đơn với mã: " + maHoaDon);
        return false;
    }
    
    @Override
    public HoaDon timKiemTheoMa(String maHoaDon) {
        return danhSach.stream()
                      .filter(hd -> hd.getMaHoaDon().equals(maHoaDon))
                      .findFirst()
                      .orElse(null);
    }
    
    @Override
    public List<HoaDon> timKiemTheoTen(String ten) {
        // Không áp dụng cho hóa đơn
        return new ArrayList<>();
    }
    
    @Override
    public HoaDon timKiemTheoCMND(String cmnd) {
        // Không áp dụng trực tiếp
        return danhSach.get(0);
    }
    
    @Override
    public List<HoaDon> timKiemTheoChuyenBay(String maChuyen) {
        // Không áp dụng trực tiếp
        return new ArrayList<>();
    }
    
    @Override
    public List<HoaDon> timKiemTheoKhoangGia(double min, double max) {
        return danhSach.stream()
                      .filter(hd -> hd.getThanhTien() >= min && hd.getThanhTien() <= max)
                      .toList();
    }
    
    @Override
    public List<HoaDon> timKiemTheoNgayBay(Date ngay) {
        // Không áp dụng
        return new ArrayList<>();
    }
    
    // PHƯƠNG THỨC MỚI: Tìm kiếm theo mã vé
    public HoaDon timKiemTheoMaVe(String maVe) {
        return danhSach.stream()
                      .filter(hd -> hd.getMaVe().equals(maVe))
                      .findFirst()
                      .orElse(null);
    }
    
    // PHƯƠNG THỨC MỚI: Tìm kiếm theo khách hàng
    public List<HoaDon> timKiemTheoKhachHang(String maKH) {
        return danhSach.stream()
                      .filter(hd -> hd.getMaKH().equals(maKH))
                      .toList();
    }
    
    // PHƯƠNG THỨC MỚI: Tìm kiếm theo nhân viên
    public List<HoaDon> timKiemTheoNhanVien(String maNV) {
        return danhSach.stream()
                      .filter(hd -> hd.getMaNV().equals(maNV))
                      .toList();
    }
    
    // PHƯƠNG THỨC MỚI: Tìm kiếm theo ngày lập
    public List<HoaDon> timKiemTheoNgayLap(Date ngay) {
        List<HoaDon> ketQua = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String ngayCanTim = sdf.format(ngay);
        
        for (HoaDon hd : danhSach) {
            String ngayHD = sdf.format(hd.getNgayLap());
            if (ngayHD.equals(ngayCanTim)) {
                ketQua.add(hd);
            }
        }
        return ketQua;
    }
    
    // PHƯƠNG THỨC MỚI: Tìm kiếm theo trạng thái
    public List<HoaDon> timKiemTheoTrangThai(String trangThai) {
        return danhSach.stream()
                      .filter(hd -> hd.getTrangThai().equals(trangThai))
                      .toList();
    }
    
    // TÌM KIẾM ĐA TIÊU CHÍ
    public List<HoaDon> timKiemHoaDon(Map<String, Object> filters) {
        List<HoaDon> ketQua = new ArrayList<>(danhSach);
        
        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            switch (key) {
                case "maKH":
                    ketQua.removeIf(hd -> !hd.getMaKH().equals(value));
                    break;
                case "maNV":
                    ketQua.removeIf(hd -> !hd.getMaNV().equals(value));
                    break;
                case "maVe":
                    ketQua.removeIf(hd -> !hd.getMaVe().equals(value));
                    break;
                case "trangThai":
                    ketQua.removeIf(hd -> !hd.getTrangThai().equals(value));
                    break;
                case "phuongThucTT":
                    ketQua.removeIf(hd -> !hd.getPhuongThucTT().equals(value));
                    break;
                case "tuNgay":
                    Date tuNgay = (Date) value;
                    ketQua.removeIf(hd -> hd.getNgayLap().before(tuNgay));
                    break;
                case "denNgay":
                    Date denNgay = (Date) value;
                    ketQua.removeIf(hd -> hd.getNgayLap().after(denNgay));
                    break;
                case "thanhTienMin":
                    double thanhTienMin = (double) value;
                    ketQua.removeIf(hd -> hd.getThanhTien() < thanhTienMin);
                    break;
                case "thanhTienMax":
                    double thanhTienMax = (double) value;
                    ketQua.removeIf(hd -> hd.getThanhTien() > thanhTienMax);
                    break;
            }
        }
        
        return ketQua;
    }
    
    @Override
    public void hienThiTatCa() {
        if (danhSach.isEmpty()) {
            System.out.println("Danh sách hóa đơn trống!");
            return;
        }
        
        System.out.println("===== DANH SÁCH TẤT CẢ HÓA ĐƠN =====");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        for (int i = 0; i < danhSach.size(); i++) {
            HoaDon hd = danhSach.get(i);
            System.out.printf("%d. %s - Vé: %s - KH: %s - NV: %s - %,.0f VND - %s%n",
                    i + 1, hd.getMaHoaDon(), hd.getMaVe(), hd.getMaKH(), 
                    hd.getMaNV(), hd.getThanhTien(), hd.getTrangThai());
        }
    }
    
    @Override
    public void hienThiTheoTrangThai(String trangThai) {
        List<HoaDon> ketQua = timKiemTheoTrangThai(trangThai);
        
        if (ketQua.isEmpty()) {
            System.out.println("Không có hóa đơn nào với trạng thái: " + trangThai);
            return;
        }
        
        System.out.println("===== DANH SÁCH HÓA ĐƠN " + trangThai + " =====");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        for (int i = 0; i < ketQua.size(); i++) {
            HoaDon hd = ketQua.get(i);
            System.out.printf("%d. %s - Vé: %s - %,.0f VND - %s%n",
                    i + 1, hd.getMaHoaDon(), hd.getMaVe(), hd.getThanhTien(), 
                    sdf.format(hd.getNgayLap()));
        }
    }
    
    @Override
    public int demSoLuong() {
        return danhSach.size();
    }
    
    @Override
    public boolean tonTai(String ma) {
        return danhSach.stream().anyMatch(hd -> hd.getMaHoaDon().equals(ma));
    }
    
    @Override
    public void sapXepTheoMa() {
        danhSach.sort(Comparator.comparing(HoaDon::getMaHoaDon));
    }
    
    public void sapXepTheoNgayLap() {
        danhSach.sort(Comparator.comparing(HoaDon::getNgayLap));
    }
    
    public void sapXepTheoThanhTien() {
        danhSach.sort(Comparator.comparingDouble(HoaDon::getThanhTien));
    }
    
    public void sapXepTheoThanhTienGiamDan() {
        danhSach.sort((hd1, hd2) -> Double.compare(hd2.getThanhTien(), hd1.getThanhTien()));
    }
    
    @Override
    public void sapXepTheoGia() {
        // Áp dụng sắp xếp theo thành tiền
        sapXepTheoThanhTien();
    }
    
    @Override
    public void sapXepTheoNgayBay() {
        // Không áp dụng
        System.out.println("Không áp dụng sắp xếp theo ngày bay cho hóa đơn");
    }
    
    // SỬA: Thêm phương thức phân trang
    public List<HoaDon> phanTrang(int trang, int kichThuocTrang) {
        int batDau = (trang - 1) * kichThuocTrang;
        int ketThuc = Math.min(batDau + kichThuocTrang, danhSach.size());
        
        if (batDau >= danhSach.size()) {
            return new ArrayList<>();
        }
        
        return danhSach.subList(batDau, ketThuc);
    }
    
    // SỬA: Thêm phương thức tìm kiếm gần đúng
    public List<HoaDon> timKiemGanDung(String keyword) {
        List<HoaDon> ketQua = new ArrayList<>();
        String keywordLower = keyword.toLowerCase();
        
        for (HoaDon hd : danhSach) {
            if (hd.getMaHoaDon().toLowerCase().contains(keywordLower) ||
                hd.getMaVe().toLowerCase().contains(keywordLower) ||
                hd.getMaKH().toLowerCase().contains(keywordLower) ||
                hd.getMaNV().toLowerCase().contains(keywordLower) ||
                hd.getTrangThai().toLowerCase().contains(keywordLower) ||
                hd.getPhuongThucTT().toLowerCase().contains(keywordLower)) {
                ketQua.add(hd);
            }
        }
        return ketQua;
    }
    
    // ========== IMPLEMENT IFILEHANDLER ==========
    @Override
    public boolean docFile(String tenFile) {
        return docFileXML1(tenFile);
    }
    
    // SỬA: Phương thức đọc file XML
    private boolean docFileXML1(String tenFile) {
        try {
            List<Map<String, String>> dataList = XMLUtils.docFileXML(tenFile);
            
            if (dataList == null || dataList.isEmpty()) {
                System.out.println("❌ Khong co du lieu trong file");
                return false;
            }
            
            int count = 0;
            for (Map<String, String> data : dataList) {
                try {
                    // Kiểm tra dữ liệu bắt buộc
                    if (data.get("MaHoaDon") == null || data.get("MaHoaDon").isEmpty()) {
                        continue;
                    }
                    
                    // Tạo đối tượng HoaDon từ dữ liệu XML
                    HoaDon hd = new HoaDon(
                            data.get("MaVe"),
                            data.get("MaKH"),
                            data.get("MaNV"),
                            XMLUtils.stringToDouble(data.get("TongTien")),
                            XMLUtils.stringToDouble(data.get("Thue")),
                            XMLUtils.stringToDouble(data.get("KhuyenMai")),
                            data.get("PhuongThucTT")
                    );
                    
                    // Cập nhật các thuộc tính bổ sung
                    hd.setNgayLap(XMLUtils.stringToDate(data.get("NgayLap")));
                    hd.setTrangThai(data.get("TrangThai"));
                    
                    // Thêm vào danh sách (kiểm tra trùng trước khi thêm)
                    if (!tonTai(hd.getMaHoaDon())) {
                        danhSach.add(hd);
                        count++;
                    } else {
                    }
                    
                } catch (Exception e) {
                    System.out.println("❌ Lỗi tạo HoaDon từ XML: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            return count > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean ghiFile(String tenFile) {
        return ghiFileXML(tenFile);
    }
    
    // SỬA: Phương thức ghi file XML
    private boolean ghiFileXML(String tenFile) {
        try {
            List<Map<String, String>> dataList = new ArrayList<>();
            
            for (HoaDon hd : danhSach) {
                Map<String, String> data = new HashMap<>();
                data.put("MaHoaDon", hd.getMaHoaDon());
                data.put("NgayLap", XMLUtils.dateToString(hd.getNgayLap()));
                data.put("MaVe", hd.getMaVe());
                data.put("MaKH", hd.getMaKH());
                data.put("MaNV", hd.getMaNV());
                data.put("TongTien", String.valueOf(hd.getTongTien()));
                data.put("Thue", String.valueOf(hd.getThue()));
                data.put("KhuyenMai", String.valueOf(hd.getKhuyenMai()));
                data.put("ThanhTien", String.valueOf(hd.getThanhTien()));
                data.put("PhuongThucTT", hd.getPhuongThucTT());
                data.put("TrangThai", hd.getTrangThai());
                
                dataList.add(data);
            }
            
            return ghiFileXML(tenFile, dataList, "HoaDons");
            
        } catch (Exception e) {
            System.out.println("❌ Lỗi ghi file XML: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // ========== PHƯƠNG THỨC NGHIỆP VỤ ==========
    public double tinhTongDoanhThu() {
        return danhSach.stream()
                      .filter(hd -> hd.getTrangThai().equals(HoaDon.TT_DA_TT))
                      .mapToDouble(HoaDon::getThanhTien)
                      .sum();
    }
    
    public double tinhDoanhThuTheoThang(int thang, int nam) {
        Calendar cal = Calendar.getInstance();
        return danhSach.stream()
                      .filter(hd -> {
                          cal.setTime(hd.getNgayLap());
                          int hdThang = cal.get(Calendar.MONTH) + 1;
                          int hdNam = cal.get(Calendar.YEAR);
                          return hd.getTrangThai().equals(HoaDon.TT_DA_TT) && 
                                 hdThang == thang && hdNam == nam;
                      })
                      .mapToDouble(HoaDon::getThanhTien)
                      .sum();
    }
    
    public Map<String, Double> thongKeDoanhThuTheoThang(int nam) {
        Map<String, Double> thongKe = new HashMap<>();
        Calendar cal = Calendar.getInstance();
        
        for (HoaDon hd : danhSach) {
            if (hd.getTrangThai().equals(HoaDon.TT_DA_TT)) {
                cal.setTime(hd.getNgayLap());
                int thang = cal.get(Calendar.MONTH) + 1;
                int hdNam = cal.get(Calendar.YEAR);
                
                if (hdNam == nam) {
                    String key = "Tháng " + thang;
                    thongKe.put(key, thongKe.getOrDefault(key, 0.0) + hd.getThanhTien());
                }
            }
        }
        
        return thongKe;
    }
    
    public Map<String, Integer> thongKeTheoPhuongThucTT() {
        Map<String, Integer> thongKe = new HashMap<>();
        for (HoaDon hd : danhSach) {
            String phuongThuc = hd.getPhuongThucTT();
            thongKe.put(phuongThuc, thongKe.getOrDefault(phuongThuc, 0) + 1);
        }
        return thongKe;
    }
    
    public Map<String, Integer> thongKeTheoTrangThai() {
        Map<String, Integer> thongKe = new HashMap<>();
        for (HoaDon hd : danhSach) {
            String trangThai = hd.getTrangThai();
            thongKe.put(trangThai, thongKe.getOrDefault(trangThai, 0) + 1);
        }
        return thongKe;
    }
    
    public List<HoaDon> getHoaDonChuaThanhToan() {
        return danhSach.stream()
                      .filter(hd -> hd.getTrangThai().equals(HoaDon.TT_CHUA_TT))
                      .toList();
    }
    
    public boolean thanhToanHoaDon(String maHoaDon) {
        HoaDon hd = timKiemTheoMa(maHoaDon);
        if (hd != null) {
            try {
                hd.thanhToan();
                System.out.println("✅ Thanh toán hóa đơn thành công: " + maHoaDon);
                return true;
            } catch (IllegalStateException e) {
                System.out.println("❌ Lỗi thanh toán: " + e.getMessage());
                return false;
            }
        }
        System.out.println("❌ Không tìm thấy hóa đơn: " + maHoaDon);
        return false;
    }
    
    public boolean huyHoaDon(String maHoaDon) {
        HoaDon hd = timKiemTheoMa(maHoaDon);
        if (hd != null) {
            try {
                hd.huyHoaDon();
                System.out.println("✅ Hủy hóa đơn thành công: " + maHoaDon);
                return true;
            } catch (IllegalStateException e) {
                System.out.println("❌ Lỗi hủy hóa đơn: " + e.getMessage());
                return false;
            }
        }
        System.out.println("❌ Không tìm thấy hóa đơn: " + maHoaDon);
        return false;
    }
    
    public List<HoaDon> getDanhSach() {
        return new ArrayList<>(danhSach);
    }
    
    // Phương thức tiện ích
    public void xoaTatCa() {
        danhSach.clear();
        System.out.println("✅ Đã xóa tất cả hóa đơn!");
    }
    
    public void hienThiThongKe() {
        System.out.println("===== THỐNG KÊ HÓA ĐƠN =====");
        System.out.println("Tổng số hóa đơn: " + demSoLuong());
        System.out.println("Tổng doanh thu: " + String.format("%,.0f VND", tinhTongDoanhThu()));
        
        Map<String, Integer> thongKeTrangThai = thongKeTheoTrangThai();
        System.out.println("📊 Phân bố trạng thái:");
        for (Map.Entry<String, Integer> entry : thongKeTrangThai.entrySet()) {
            System.out.printf("   - %s: %d hóa đơn%n", entry.getKey(), entry.getValue());
        }
        
        Map<String, Integer> thongKePhuongThuc = thongKeTheoPhuongThucTT();
        System.out.println("💳 Phân bố phương thức thanh toán:");
        for (Map.Entry<String, Integer> entry : thongKePhuongThuc.entrySet()) {
            System.out.printf("   - %s: %d hóa đơn%n", entry.getKey(), entry.getValue());
        }
        
        List<HoaDon> chuaThanhToan = getHoaDonChuaThanhToan();
        System.out.println("⏳ Hóa đơn chưa thanh toán: " + chuaThanhToan.size());
    }
}