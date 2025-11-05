/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package Sevice;

/**
 *
 * @author HP
 */
// File: DanhSachKhachHang.java
import java.util.*;
import model.KhachHang;
import repository.IFileHandler;
import repository.IQuanLy;
import repository.XMLUtils;

import java.io.*;
import java.text.SimpleDateFormat;

public class DanhSachKhachHang implements IQuanLy<KhachHang>, IFileHandler {

    private List<KhachHang> danhSach;
    private static final int MAX_SIZE = 5000;

    public DanhSachKhachHang() {
        this.danhSach = new ArrayList<>();
    }

    // ========== IMPLEMENT IQUANLY ==========
    @Override
    public boolean them(KhachHang khachHang) {
        if (danhSach.size() >= MAX_SIZE) {
            System.out.println("Danh sách khách hàng đã đầy!");
            return false;
        }

        if (tonTai(khachHang.getMaKH())) {
            System.out.println("Mã khách hàng đã tồn tại!");
            return false;
        }

        // Kiểm tra CMND trùng
        if (tonTaiCMND(khachHang.getCmnd(), null)) {
            System.out.println("CMND đã tồn tại!");
            return false;
        }

        danhSach.add(khachHang);
        System.out.println("Thêm khách hàng thành công!");
        return true;
    }

    @Override
    public boolean xoa(String maKH) {
        for (Iterator<KhachHang> iterator = danhSach.iterator(); iterator.hasNext();) {
            KhachHang kh = iterator.next();
            if (kh.getMaKH().equals(maKH)) {
                iterator.remove();
                System.out.println("Xóa khách hàng thành công!");
                return true;
            }
        }
        System.out.println("Không tìm thấy khách hàng với mã: " + maKH);
        return false;
    }

    @Override
    public boolean sua(String maKH, KhachHang khachHangMoi) {
        for (int i = 0; i < danhSach.size(); i++) {
            if (danhSach.get(i).getMaKH().equals(maKH)) {
                // Kiểm tra CMND trùng (trừ chính nó)
                if (tonTaiCMND(khachHangMoi.getCmnd(), maKH)) {
                    System.out.println("CMND đã tồn tại!");
                    return false;
                }
                danhSach.set(i, khachHangMoi);
                System.out.println("Cập nhật khách hàng thành công!");
                return true;
            }
        }
        System.out.println("Không tìm thấy khách hàng với mã: " + maKH);
        return false;
    }

    @Override
    public KhachHang timKiemTheoMa(String maKH) {
        return danhSach.stream()
                .filter(kh -> kh.getMaKH().equals(maKH))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<KhachHang> timKiemTheoTen(String ten) {
        List<KhachHang> ketQua = new ArrayList<>();
        for (KhachHang kh : danhSach) {
            if (kh.getHoTen().toLowerCase().contains(ten.toLowerCase())) {
                ketQua.add(kh);
            }
        }
        return ketQua;
    }

    @Override
    public KhachHang timKiemTheoCMND(String cmnd) {
        for (KhachHang kh : danhSach) {
            if (kh.getCmnd().equals(cmnd)) {
                return kh;
            }
        }
        return null;
    }

    // PHƯƠNG THỨC MỚI: Tìm kiếm theo email
    public List<KhachHang> timKiemTheoEmail(String email) {
        List<KhachHang> ketQua = new ArrayList<>();
        for (KhachHang kh : danhSach) {
            if (kh.getEmail().toLowerCase().contains(email.toLowerCase())) {
                ketQua.add(kh);
            }
        }
        return ketQua;
    }

    // PHƯƠNG THỨC MỚI: Tìm kiếm theo số điện thoại
    public List<KhachHang> timKiemTheoSoDT(String soDT) {
        List<KhachHang> ketQua = new ArrayList<>();
        for (KhachHang kh : danhSach) {
            if (kh.getSoDT().contains(soDT)) {
                ketQua.add(kh);
            }
        }
        return ketQua;
    }

    public List<KhachHang> timKiemTheoHang(String hang) {
        List<KhachHang> ketQua = new ArrayList<>();
        for (KhachHang kh : danhSach) {
            if (kh.getHangKhachHang().equals(hang)) {
                ketQua.add(kh);
            }
        }
        return ketQua;
    }

    public List<KhachHang> timKiemTheoDiaChi(String diaChi) {
        List<KhachHang> ketQua = new ArrayList<>();
        for (KhachHang kh : danhSach) {
            if (kh.getDiaChi().toLowerCase().contains(diaChi.toLowerCase())) {
                ketQua.add(kh);
            }
        }
        return ketQua;
    }

    // SỬA: Các phương thức không áp dụng trả về danh sách rỗng
    @Override
    public List<KhachHang> timKiemTheoChuyenBay(String maChuyen) {
        // Không áp dụng cho khách hàng
        return new ArrayList<>();
    }

    @Override
    public List<KhachHang> timKiemTheoKhoangGia(double min, double max) {
        // Không áp dụng cho khách hàng
        return new ArrayList<>();
    }

    @Override
    public List<KhachHang> timKiemTheoNgayBay(Date ngay) {
        // Không áp dụng cho khách hàng
        return new ArrayList<>();
    }

    // TÌM KIẾM ĐA TIÊU CHÍ
    public List<KhachHang> timKiemKhachHang(Map<String, Object> filters) {
        List<KhachHang> ketQua = new ArrayList<>(danhSach);

        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            switch (key) {
                case "hoTen":
                    ketQua.removeIf(kh -> !kh.getHoTen().toLowerCase().contains(value.toString().toLowerCase()));
                    break;
                case "cmnd":
                    ketQua.removeIf(kh -> !kh.getCmnd().contains(value.toString()));
                    break;
                case "email":
                    ketQua.removeIf(kh -> !kh.getEmail().toLowerCase().contains(value.toString().toLowerCase()));
                    break;
                case "soDT":
                    ketQua.removeIf(kh -> !kh.getSoDT().contains(value.toString()));
                    break;
                case "hang":
                    ketQua.removeIf(kh -> !kh.getHangKhachHang().equals(value));
                    break;
                case "gioiTinh":
                    ketQua.removeIf(kh -> !kh.getGioiTinh().equals(value));
                    break;
                case "diaChi":
                    ketQua.removeIf(kh -> !kh.getDiaChi().toLowerCase().contains(value.toString().toLowerCase()));
                    break;
                case "tuNgayDangKy":
                    Date tuNgay = (Date) value;
                    ketQua.removeIf(kh -> kh.getNgayDangKy().before(tuNgay));
                    break;
                case "denNgayDangKy":
                    Date denNgay = (Date) value;
                    ketQua.removeIf(kh -> kh.getNgayDangKy().after(denNgay));
                    break;
                case "diemMin":
                    int diemMin = (int) value;
                    ketQua.removeIf(kh -> kh.getDiemTichLuy() < diemMin);
                    break;
                case "diemMax":
                    int diemMax = (int) value;
                    ketQua.removeIf(kh -> kh.getDiemTichLuy() > diemMax);
                    break;
            }
        }

        return ketQua;
    }

    @Override
    public void hienThiTatCa() {
        if (danhSach.isEmpty()) {
            System.out.println("Danh sách khách hàng trống!");
            return;
        }

        System.out.println("===== DANH SÁCH TẤT CẢ KHÁCH HÀNG =====");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (int i = 0; i < danhSach.size(); i++) {
            KhachHang kh = danhSach.get(i);
            System.out.printf("%d. %s - %s - %s - %s - Điểm: %d - Hạng: %s%n",
                    i + 1, kh.getMaKH(), kh.getHoTen(), kh.getCmnd(),
                    kh.getSoDT(), kh.getDiemTichLuy(), kh.getHangKhachHang());
        }
    }

    @Override
    public void hienThiTheoTrangThai(String trangThai) {
        // Không áp dụng trạng thái cho khách hàng
        System.out.println("Khách hàng không có trạng thái, hiển thị tất cả:");
        hienThiTatCa();
    }

    @Override
    public int demSoLuong() {
        return danhSach.size();
    }

    @Override
    public boolean tonTai(String ma) {
        return danhSach.stream().anyMatch(kh -> kh.getMaKH().equals(ma));
    }

    private boolean tonTaiCMND(String cmnd, String maLoaiTru) {
        for (KhachHang kh : danhSach) {
            if ((maLoaiTru == null || !kh.getMaKH().equals(maLoaiTru)) && kh.getCmnd().equals(cmnd)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void sapXepTheoMa() {
        danhSach.sort(Comparator.comparing(KhachHang::getMaKH));
    }

    public void sapXepTheoHoTen() {
        danhSach.sort(Comparator.comparing(KhachHang::getHoTen));
    }

    public void sapXepTheoDiem() {
        danhSach.sort((kh1, kh2) -> Integer.compare(kh2.getDiemTichLuy(), kh1.getDiemTichLuy())); // Giảm dần
    }

    public void sapXepTheoNgayDangKy() {
        danhSach.sort(Comparator.comparing(KhachHang::getNgayDangKy));
    }

    // SỬA: Các phương thức không áp dụng
    @Override
    public void sapXepTheoGia() {
        // Không áp dụng
        System.out.println("Không áp dụng sắp xếp theo giá cho khách hàng");
    }

    @Override
    public void sapXepTheoNgayBay() {
        // Không áp dụng
        System.out.println("Không áp dụng sắp xếp theo ngày bay cho khách hàng");
    }

    // SỬA: Thêm phương thức phân trang
    public List<KhachHang> phanTrang(int trang, int kichThuocTrang) {
        int batDau = (trang - 1) * kichThuocTrang;
        int ketThuc = Math.min(batDau + kichThuocTrang, danhSach.size());
        
        if (batDau >= danhSach.size()) {
            return new ArrayList<>();
        }
        
        return danhSach.subList(batDau, ketThuc);
    }

    // SỬA: Thêm phương thức tìm kiếm gần đúng
    public List<KhachHang> timKiemGanDung(String keyword) {
        List<KhachHang> ketQua = new ArrayList<>();
        String keywordLower = keyword.toLowerCase();
        
        for (KhachHang kh : danhSach) {
            if (kh.getMaKH().toLowerCase().contains(keywordLower) ||
                kh.getHoTen().toLowerCase().contains(keywordLower) ||
                kh.getCmnd().contains(keyword) ||
                kh.getEmail().toLowerCase().contains(keywordLower) ||
                kh.getSoDT().contains(keyword) ||
                kh.getDiaChi().toLowerCase().contains(keywordLower) ||
                kh.getHangKhachHang().toLowerCase().contains(keywordLower)) {
                ketQua.add(kh);
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
            System.out.println("🔄 Bắt đầu đọc file khách hàng: " + tenFile);
            
            List<Map<String, String>> dataList = XMLUtils.docFileXML(tenFile);

            if (dataList == null || dataList.isEmpty()) {
                System.out.println("❌ Không có dữ liệu trong file XML");
                return false;
            }

            int count = 0;
            for (Map<String, String> data : dataList) {
                try {
                    // Kiểm tra dữ liệu bắt buộc
                    if (data.get("MaKH") == null || data.get("MaKH").isEmpty()) {
                        System.out.println("⚠️ Bỏ qua dòng thiếu mã khách hàng");
                        continue;
                    }

                    // Tạo đối tượng KhachHang từ dữ liệu XML
                    KhachHang kh = new KhachHang(
                            data.get("MaKH"),
                            data.get("HoTen"),
                            data.get("SoDT"),
                            data.get("Email"),
                            data.get("CMND"),
                            XMLUtils.stringToDate(data.get("NgaySinh")),
                            data.get("GioiTinh"),
                            data.get("DiaChi")
                    );

                    // Cập nhật các thuộc tính bổ sung nếu có
                    if (data.containsKey("HangKhachHang") && data.get("HangKhachHang") != null) {
                        kh.setHangKhachHang(data.get("HangKhachHang"));
                    }
                    
                    if (data.containsKey("DiemTichLuy") && data.get("DiemTichLuy") != null) {
                        kh.setDiemTichLuy(XMLUtils.stringToInt(data.get("DiemTichLuy")));
                    }
                    
                    if (data.containsKey("NgayDangKy") && data.get("NgayDangKy") != null) {
                        kh.setNgayDangKy(XMLUtils.stringToDate(data.get("NgayDangKy")));
                    }

                    // Thêm vào danh sách (kiểm tra trùng trước khi thêm)
                    if (!tonTai(kh.getMaKH())) {
                        danhSach.add(kh);
                        count++;
                        System.out.println("✅ Đã thêm khách hàng: " + kh.getMaKH());
                    } else {
                        System.out.println("⚠️ Bỏ qua khách hàng trùng mã: " + kh.getMaKH());
                    }

                } catch (Exception e) {
                    System.out.println("❌ Lỗi tạo KhachHang từ XML: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            System.out.println("🎉 Đã đọc thành công " + count + " khách hàng từ file XML.");
            return count > 0;

        } catch (Exception e) {
            System.out.println("💥 Lỗi đọc file XML: " + e.getMessage());
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

            for (KhachHang kh : danhSach) {
                Map<String, String> data = new HashMap<>();
                data.put("MaKH", kh.getMaKH());
                data.put("HoTen", kh.getHoTen());
                data.put("SoDT", kh.getSoDT());
                data.put("Email", kh.getEmail());
                data.put("CMND", kh.getCmnd());
                data.put("NgaySinh", XMLUtils.dateToDateOnlyString(kh.getNgaySinh()));
                data.put("GioiTinh", kh.getGioiTinh());
                data.put("DiaChi", kh.getDiaChi());
                data.put("HangKhachHang", kh.getHangKhachHang());
                data.put("DiemTichLuy", String.valueOf(kh.getDiemTichLuy()));
                data.put("NgayDangKy", XMLUtils.dateToString(kh.getNgayDangKy()));

                dataList.add(data);
            }

            return ghiFileXML(tenFile, dataList, "KhachHangs");

        } catch (Exception e) {
            System.out.println("❌ Lỗi ghi file XML: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ========== PHƯƠNG THỨC NGHIỆP VỤ ==========
    public void tangDiemKhachHang(String maKH, int diem) {
        KhachHang kh = timKiemTheoMa(maKH);
        if (kh != null) {
            kh.tangDiemTichLuy(diem);
            System.out.printf("✅ Đã thêm %d điểm cho KH %s. Tổng điểm: %d%n",
                    diem, kh.getHoTen(), kh.getDiemTichLuy());
        } else {
            System.out.println("❌ Không tìm thấy khách hàng với mã: " + maKH);
        }
    }

    public void giamDiemKhachHang(String maKH, int diem) {
        KhachHang kh = timKiemTheoMa(maKH);
        if (kh != null) {
            try {
                kh.giamDiemTichLuy(diem);
                System.out.printf("✅ Đã giảm %d điểm của KH %s. Tổng điểm: %d%n",
                        diem, kh.getHoTen(), kh.getDiemTichLuy());
            } catch (IllegalArgumentException e) {
                System.out.println("❌ Lỗi: " + e.getMessage());
            }
        } else {
            System.out.println("❌ Không tìm thấy khách hàng với mã: " + maKH);
        }
    }

    public void capNhatHangKhachHang() {
        int count = 0;
        for (KhachHang kh : danhSach) {
            String hangCu = kh.getHangKhachHang();
            kh.tangDiemTichLuy(0); // Chỉ để cập nhật hạng
            if (!hangCu.equals(kh.getHangKhachHang())) {
                count++;
                System.out.printf("🔄 %s: %s → %s%n", kh.getMaKH(), hangCu, kh.getHangKhachHang());
            }
        }
        System.out.println("✅ Đã cập nhật hạng cho " + count + " khách hàng");
    }

    public Map<String, Integer> thongKeTheoHang() {
        Map<String, Integer> thongKe = new HashMap<>();
        for (KhachHang kh : danhSach) {
            String hang = kh.getHangKhachHang();
            thongKe.put(hang, thongKe.getOrDefault(hang, 0) + 1);
        }
        return thongKe;
    }

    public Map<String, Integer> thongKeTheoGioiTinh() {
        Map<String, Integer> thongKe = new HashMap<>();
        for (KhachHang kh : danhSach) {
            String gioiTinh = kh.getGioiTinh();
            thongKe.put(gioiTinh, thongKe.getOrDefault(gioiTinh, 0) + 1);
        }
        return thongKe;
    }

    public Map<String, Integer> thongKeTheoTuoi() {
        Map<String, Integer> thongKe = new HashMap<>();
        for (KhachHang kh : danhSach) {
            int tuoi = kh.tinhTuoi();
            String nhomTuoi;
            if (tuoi < 18) nhomTuoi = "Dưới 18";
            else if (tuoi < 25) nhomTuoi = "18-24";
            else if (tuoi < 35) nhomTuoi = "25-34";
            else if (tuoi < 45) nhomTuoi = "35-44";
            else if (tuoi < 60) nhomTuoi = "45-59";
            else nhomTuoi = "Trên 60";
            
            thongKe.put(nhomTuoi, thongKe.getOrDefault(nhomTuoi, 0) + 1);
        }
        return thongKe;
    }

    public List<KhachHang> getKhachHangVip() {
        List<KhachHang> vipList = new ArrayList<>();
        for (KhachHang kh : danhSach) {
            if (kh.getHangKhachHang().equals(KhachHang.HANG_GOLD)
                    || kh.getHangKhachHang().equals(KhachHang.HANG_PLATINUM)) {
                vipList.add(kh);
            }
        }
        return vipList;
    }

    public List<KhachHang> getKhachHangCoDiemCao(int soDiemToiThieu) {
        return danhSach.stream()
                .filter(kh -> kh.getDiemTichLuy() >= soDiemToiThieu)
                .toList();
    }

    public List<KhachHang> getDanhSach() {
        return new ArrayList<>(danhSach);
    }

    // Phương thức tiện ích
    public void xoaTatCa() {
        danhSach.clear();
        System.out.println("✅ Đã xóa tất cả khách hàng!");
    }

    public void hienThiThongKe() {
        System.out.println("===== THỐNG KÊ KHÁCH HÀNG =====");
        System.out.println("Tổng số khách hàng: " + demSoLuong());

        Map<String, Integer> thongKeHang = thongKeTheoHang();
        System.out.println("📊 Phân bố hạng:");
        for (Map.Entry<String, Integer> entry : thongKeHang.entrySet()) {
            System.out.printf("   - %s: %d khách hàng%n", entry.getKey(), entry.getValue());
        }

        Map<String, Integer> thongKeGioiTinh = thongKeTheoGioiTinh();
        System.out.println("👥 Phân bố giới tính:");
        for (Map.Entry<String, Integer> entry : thongKeGioiTinh.entrySet()) {
            System.out.printf("   - %s: %d khách hàng%n", entry.getKey(), entry.getValue());
        }

        Map<String, Integer> thongKeTuoi = thongKeTheoTuoi();
        System.out.println("🎂 Phân bố tuổi:");
        for (Map.Entry<String, Integer> entry : thongKeTuoi.entrySet()) {
            System.out.printf("   - %s: %d khách hàng%n", entry.getKey(), entry.getValue());
        }

        List<KhachHang> vipList = getKhachHangVip();
        System.out.println("⭐ Khách hàng VIP: " + vipList.size());
    }
    public static void main(String[] args) {
        DanhSachKhachHang ds = new DanhSachKhachHang();
        ds.docFileXML1("src/resources/data/2_KhachHangs.xml");
        ds.hienThiTatCa();
    }
}