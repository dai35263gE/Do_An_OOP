package Sevice;

import java.util.*;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;

import model.KhachHang;
import repository.IFileHandler;
import repository.IQuanLy;
import repository.XMLUtils;

public class DanhSachKhachHang implements IQuanLy<KhachHang>, IFileHandler {

    private List<KhachHang> danhSach;
    private static final int MAX_SIZE = 5000;

    public DanhSachKhachHang() {
        this.danhSach = new ArrayList<>();
    }

    // ========== GETTERS ==========
    public List<KhachHang> getDanhSach() {
        return new ArrayList<>(danhSach);
    }

    // ========== IMPLEMENT IQUANLY ==========
    @Override
    public boolean them(KhachHang khachHang) {
        if (danhSach.size() >= MAX_SIZE) {
            throw new IllegalStateException("Danh sách khách hàng đã đầy!");
        }

        if (tonTai(khachHang.getMa())) {
            throw new IllegalArgumentException("Mã khách hàng đã tồn tại: " + khachHang.getMa());
        }

        if (tonTaiCMND(khachHang.getCmnd(), null)) {
            throw new IllegalArgumentException("CMND đã tồn tại: " + khachHang.getCmnd());
        }

        if (tonTaiEmail(khachHang.getEmail(), null)) {
            throw new IllegalArgumentException("Email đã tồn tại: " + khachHang.getEmail());
        }

        return danhSach.add(khachHang);
    }

    @Override
    public boolean xoa(String maKH) {
        KhachHang khachHang = timKiemTheoMa(maKH);
        if (khachHang == null) {
            throw new IllegalArgumentException("Không tìm thấy khách hàng với mã: " + maKH);
        }

        // Kiểm tra nếu khách hàng có vé đang hoạt động
        if (coVeDangHoatDong(khachHang)) {
            throw new IllegalStateException("Không thể xóa khách hàng đang có vé đặt hoặc chưa bay!");
        }

        return danhSach.remove(khachHang);
    }

    @Override
    public boolean sua(String maKH, KhachHang khachHangMoi) {
        KhachHang khachHangCu = timKiemTheoMa(maKH);
        if (khachHangCu == null) {
            throw new IllegalArgumentException("Không tìm thấy khách hàng với mã: " + maKH);
        }

        if (!maKH.equals(khachHangMoi.getMa()) && tonTai(khachHangMoi.getMa())) {
            throw new IllegalArgumentException("Mã khách hàng mới đã tồn tại!");
        }

        if (tonTaiCMND(khachHangMoi.getCmnd(), maKH)) {
            throw new IllegalArgumentException("CMND đã tồn tại!");
        }

        if (tonTaiEmail(khachHangMoi.getEmail(), maKH)) {
            throw new IllegalArgumentException("Email đã tồn tại!");
        }

        int index = danhSach.indexOf(khachHangCu);
        danhSach.set(index, khachHangMoi);
        return true;
    }

    @Override
    public KhachHang timKiemTheoMa(String maKH) {
        return danhSach.stream()
                .filter(kh -> kh.getMa().equals(maKH))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<KhachHang> timKiemTheoTen(String ten) {
        String keyword = ten.toLowerCase();
        return danhSach.stream()
                .filter(kh -> kh.getHoTen().toLowerCase().contains(keyword))
                .collect(Collectors.toList());
    }

    @Override
    public KhachHang timKiemTheoCMND(String cmnd) {
        return danhSach.stream()
                .filter(kh -> kh.getCmnd().equals(cmnd))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<KhachHang> timKiemTheoNgayBay(Date ngay) {
        // Không áp dụng cho khách hàng
        return new ArrayList<>();
    }

    @Override
    public List<KhachHang> timKiemTheoKhoangGia(double min, double max) {
        // Không áp dụng cho khách hàng
        return new ArrayList<>();
    }

    @Override
    public List<KhachHang> timKiemTheoChuyenBay(String maChuyen) {
        // Không áp dụng cho khách hàng
        return new ArrayList<>();
    }

    @Override
    public void hienThiTatCa() {
        if (danhSach.isEmpty()) {
            System.out.println("Danh sách khách hàng trống!");
            return;
        }

        System.out.println("===== DANH SÁCH TẤT CẢ KHÁCH HÀNG =====");
        for (int i = 0; i < danhSach.size(); i++) {
            KhachHang kh = danhSach.get(i);
            System.out.printf("%d. %s - %s - %s - %s - Điểm: %d - Hạng: %s%n",
                    i + 1, kh.getMa(), kh.getHoTen(), kh.getCmnd(),
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
        return danhSach.stream().anyMatch(kh -> kh.getMa().equals(ma));
    }

    @Override
    public void sapXepTheoMa() {
        danhSach.sort(Comparator.comparing(KhachHang::getMa));
    }

    @Override
    public void sapXepTheoGia() {
        // Không áp dụng cho khách hàng
        System.out.println("Không áp dụng sắp xếp theo giá cho khách hàng");
    }

    @Override
    public void sapXepTheoNgayBay() {
        // Không áp dụng cho khách hàng
        System.out.println("Không áp dụng sắp xếp theo ngày bay cho khách hàng");
    }

    // ========== PHƯƠNG THỨC TÌM KIẾM NÂNG CAO ==========
    public List<KhachHang> timKiemTheoEmail(String email) {
        String keyword = email.toLowerCase();
        return danhSach.stream()
                .filter(kh -> kh.getEmail().toLowerCase().contains(keyword))
                .collect(Collectors.toList());
    }

    public List<KhachHang> timKiemTheoSoDT(String soDT) {
        return danhSach.stream()
                .filter(kh -> kh.getSoDT().contains(soDT))
                .collect(Collectors.toList());
    }

    public List<KhachHang> timKiemTheoHang(String hang) {
        return danhSach.stream()
                .filter(kh -> kh.getHangKhachHang().equals(hang))
                .collect(Collectors.toList());
    }

    public List<KhachHang> timKiemTheoDiaChi(String diaChi) {
        String keyword = diaChi.toLowerCase();
        return danhSach.stream()
                .filter(kh -> kh.getDiaChi().toLowerCase().contains(keyword))
                .collect(Collectors.toList());
    }

    public List<KhachHang> timKiemGanDung(String keyword) {
        String keywordLower = keyword.toLowerCase();
        
        return danhSach.stream()
                .filter(kh -> kh.getMa().toLowerCase().contains(keywordLower) ||
                             kh.getHoTen().toLowerCase().contains(keywordLower) ||
                             kh.getCmnd().contains(keyword) ||
                             kh.getEmail().toLowerCase().contains(keywordLower) ||
                             kh.getSoDT().contains(keyword) ||
                             kh.getDiaChi().toLowerCase().contains(keywordLower) ||
                             kh.getHangKhachHang().toLowerCase().contains(keywordLower))
                .collect(Collectors.toList());
    }

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

    // ========== PHÂN TRANG ==========
    public List<KhachHang> phanTrang(int trang, int kichThuocTrang) {
        int batDau = (trang - 1) * kichThuocTrang;
        int ketThuc = Math.min(batDau + kichThuocTrang, danhSach.size());
        
        if (batDau >= danhSach.size() || batDau < 0) {
            return new ArrayList<>();
        }
        
        return new ArrayList<>(danhSach.subList(batDau, ketThuc));
    }

    public int getTongSoTrang(int kichThuocTrang) {
        return (int) Math.ceil((double) danhSach.size() / kichThuocTrang);
    }

    // ========== SẮP XẾP NÂNG CAO ==========
    public void sapXepTheoHoTen() {
        danhSach.sort(Comparator.comparing(KhachHang::getHoTen));
    }

    public void sapXepTheoDiem() {
        danhSach.sort((kh1, kh2) -> Integer.compare(kh2.getDiemTichLuy(), kh1.getDiemTichLuy()));
    }

    public void sapXepTheoNgayDangKy() {
        danhSach.sort(Comparator.comparing(KhachHang::getNgayDangKy));
    }

    public void sapXepTheoNgaySinh() {
        danhSach.sort(Comparator.comparing(KhachHang::getNgaySinh));
    }

    // ========== IMPLEMENT IFILEHANDLER ==========
    @Override
    public boolean docFile(String tenFile) {
        try {
            List<Map<String, String>> dataList = XMLUtils.docFileXML(tenFile);

            if (dataList == null || dataList.isEmpty()) {
                System.out.println("Không có dữ liệu trong file XML");
                return false;
            }

            int count = 0;
            for (Map<String, String> data : dataList) {
                try {
                    // Kiểm tra dữ liệu bắt buộc
                    if (data.get("MaKH") == null || data.get("MaKH").isEmpty()) {
                        System.out.println("Bỏ qua dòng thiếu mã khách hàng");
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
                            data.get("DiaChi"),
                            data.get("TenDangNhap"),
                            data.get("MatKhau")
                    );

                    // Cập nhật các thuộc tính bổ sung
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
                    if (!tonTai(kh.getMa())) {
                        danhSach.add(kh);
                        count++;
                    }

                } catch (Exception e) {
                    System.err.println("Lỗi tạo KhachHang từ XML: " + e.getMessage());
                }
            }

            System.out.println("Da tai " + count + " khach hang tu file XML");
            return count > 0;

        } catch (Exception e) {
            System.err.println("Lỗi đọc file XML: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean ghiFile(String tenFile) {
        try {
            List<Map<String, String>> dataList = new ArrayList<>();

            for (KhachHang kh : danhSach) {
                Map<String, String> data = new HashMap<>();
                data.put("MaKH", kh.getMa());
                data.put("HoTen", kh.getHoTen());
                data.put("SoDT", kh.getSoDT());
                data.put("Email", kh.getEmail());
                data.put("CMND", kh.getCmnd());
                data.put("NgaySinh", XMLUtils.dateToString(kh.getNgaySinh()));
                data.put("GioiTinh", kh.getGioiTinh());
                data.put("DiaChi", kh.getDiaChi());
                data.put("TenDangNhap", kh.getTenDangNhap());
                data.put("MatKhau", kh.getMatKhau());
                data.put("HangKhachHang", kh.getHangKhachHang());
                data.put("DiemTichLuy", String.valueOf(kh.getDiemTichLuy()));
                data.put("NgayDangKy", XMLUtils.dateToString(kh.getNgayDangKy()));

                dataList.add(data);
            }

            return XMLUtils.ghiFileXML(tenFile, dataList, "KhachHangs");

        } catch (Exception e) {
            System.err.println("Lỗi ghi file XML: " + e.getMessage());
            return false;
        }
    }

    // ========== PHƯƠNG THỨC NGHIỆP VỤ ==========
    private boolean tonTaiCMND(String cmnd, String maLoaiTru) {
        return danhSach.stream()
                .anyMatch(kh -> (maLoaiTru == null || !kh.getMa().equals(maLoaiTru)) && 
                               kh.getCmnd().equals(cmnd));
    }

    private boolean tonTaiEmail(String email, String maLoaiTru) {
        return danhSach.stream()
                .anyMatch(kh -> (maLoaiTru == null || !kh.getMa().equals(maLoaiTru)) && 
                               kh.getEmail().equalsIgnoreCase(email));
    }

    private boolean coVeDangHoatDong(KhachHang khachHang) {
        // Giả sử có phương thức kiểm tra vé đang hoạt động
        // Trong thực tế, cần tích hợp với service quản lý vé
        return false; // Tạm thời trả về false
    }

    public void tangDiemKhachHang(String maKH, int diem) {
        KhachHang kh = timKiemTheoMa(maKH);
        if (kh != null) {
            kh.tangDiemTichLuy(diem);
        } else {
            throw new IllegalArgumentException("Không tìm thấy khách hàng với mã: " + maKH);
        }
    }

    public boolean suDungDiemKhachHang(String maKH, int diem) {
        KhachHang kh = timKiemTheoMa(maKH);
        return kh != null && kh.suDungDiemTichLuy(diem);
    }

    public void capNhatHangKhachHang() {
        for (KhachHang kh : danhSach) {
            String hangCu = kh.getHangKhachHang();
            kh.tangDiemTichLuy(0); // Chỉ để cập nhật hạng
            if (!hangCu.equals(kh.getHangKhachHang())) {
                System.out.printf("Cập nhật hạng: %s: %s → %s%n", 
                    kh.getMa(), hangCu, kh.getHangKhachHang());
            }
        }
    }

    // ========== THỐNG KÊ ==========
    public Map<String, Integer> thongKeTheoHang() {
        return danhSach.stream()
                .collect(Collectors.groupingBy(
                    KhachHang::getHangKhachHang,
                    Collectors.summingInt(kh -> 1)
                ));
    }

    public Map<String, Integer> thongKeTheoGioiTinh() {
        return danhSach.stream()
                .collect(Collectors.groupingBy(
                    KhachHang::getGioiTinh,
                    Collectors.summingInt(kh -> 1)
                ));
    }

    public Map<String, Integer> thongKeTheoTuoi() {
        return danhSach.stream()
                .collect(Collectors.groupingBy(
                    kh -> {
                        int tuoi = kh.tinhTuoi();
                        if (tuoi < 18) return "Dưới 18";
                        else if (tuoi < 25) return "18-24";
                        else if (tuoi < 35) return "25-34";
                        else if (tuoi < 45) return "35-44";
                        else if (tuoi < 60) return "45-59";
                        else return "Trên 60";
                    },
                    Collectors.summingInt(kh -> 1)
                ));
    }

    public Map<String, Integer> thongKeTheoThanhPho() {
        return danhSach.stream()
                .collect(Collectors.groupingBy(
                    kh -> {
                        String diaChi = kh.getDiaChi().toLowerCase();
                        if (diaChi.contains("hà nội")) return "Hà Nội";
                        else if (diaChi.contains("tp.hcm") || diaChi.contains("hồ chí minh")) return "TP.HCM";
                        else if (diaChi.contains("đà nẵng")) return "Đà Nẵng";
                        else if (diaChi.contains("cần thơ")) return "Cần Thơ";
                        else if (diaChi.contains("hải phòng")) return "Hải Phòng";
                        else return "Tỉnh khác";
                    },
                    Collectors.summingInt(kh -> 1)
                ));
    }

    public List<KhachHang> getKhachHangVip() {
        return danhSach.stream()
                .filter(kh -> kh.isHangGold() || kh.isHangPlatinum())
                .collect(Collectors.toList());
    }

    public List<KhachHang> getKhachHangCoDiemCao(int soDiemToiThieu) {
        return danhSach.stream()
                .filter(kh -> kh.getDiemTichLuy() >= soDiemToiThieu)
                .collect(Collectors.toList());
    }

    public KhachHang getKhachHangCoDiemCaoNhat() {
        return danhSach.stream()
                .max(Comparator.comparingInt(KhachHang::getDiemTichLuy))
                .orElse(null);
    }

    public double getTyLeKhachHangVip() {
        long soKhachHangVip = danhSach.stream()
                .filter(kh -> kh.isHangGold() || kh.isHangPlatinum())
                .count();
        return danhSach.isEmpty() ? 0 : (double) soKhachHangVip / danhSach.size() * 100;
    }

    // ========== UTILITY METHODS ==========
    public void xoaTatCa() {
        danhSach.clear();
    }

    public boolean isEmpty() {
        return danhSach.isEmpty();
    }

    public void hienThiThongKe() {
        System.out.println("===== THỐNG KÊ KHÁCH HÀNG =====");
        System.out.println("Tổng số khách hàng: " + demSoLuong());

        Map<String, Integer> thongKeHang = thongKeTheoHang();
        System.out.println("Phân bố hạng:");
        thongKeHang.forEach((hang, soLuong) -> 
            System.out.printf("   - %s: %d khách hàng%n", hang, soLuong));

        Map<String, Integer> thongKeGioiTinh = thongKeTheoGioiTinh();
        System.out.println("Phân bố giới tính:");
        thongKeGioiTinh.forEach((gioiTinh, soLuong) -> 
            System.out.printf("   - %s: %d khách hàng%n", gioiTinh, soLuong));

        System.out.printf("Tỷ lệ khách hàng VIP: %.1f%%%n", getTyLeKhachHangVip());
        
        KhachHang khachHangDiemCaoNhat = getKhachHangCoDiemCaoNhat();
        if (khachHangDiemCaoNhat != null) {
            System.out.printf("Khách hàng có điểm cao nhất: %s - %d điểm%n", 
                khachHangDiemCaoNhat.getHoTen(), khachHangDiemCaoNhat.getDiemTichLuy());
        }
    }

    // // ========== MAIN METHOD FOR TESTING ==========
    // public static void main(String[] args) {
    //     DanhSachKhachHang ds = new DanhSachKhachHang();
    //     ds.docFile("src/resources/data/2_KhachHangs.xml");
    //     ds.hienThiTatCa();
    //     ds.hienThiThongKe();
        
    //     // Test phân trang
    //     System.out.println("\n=== TEST PHÂN TRANG ===");
    //     List<KhachHang> trang1 = ds.phanTrang(1, 5);
    //     trang1.forEach(kh -> System.out.println(kh.getMa() + " - " + kh.getHoTen()));
    // }
}