/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package Sevice;

/**
 *
 * @author HP
 */
// File: DanhSachChuyenBay.java
import java.util.List;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import model.ChuyenBay;
import repository.IFileHandler;
import repository.IQuanLy;
import repository.XMLUtils;

public class DanhSachChuyenBay implements IQuanLy<ChuyenBay>, IFileHandler {

    private ArrayList<ChuyenBay> danhSach;
    private static final int MAX_SIZE = 1000;

    public ArrayList<ChuyenBay> getDanhSachChuyenBay() {
        return this.danhSach;
    }

    public DanhSachChuyenBay() {
        danhSach = new ArrayList<>();
    }

    // ========== IMPLEMENT IQUANLY ==========
    @Override
    public boolean them(ChuyenBay chuyenBay) {
        if (danhSach.size() >= MAX_SIZE) {
            System.out.println("Danh sách chuyến bay đã đầy!");
            return false;
        }

        if (tonTai(chuyenBay.getMaChuyen())) {
            System.out.println("Mã chuyến bay đã tồn tại!");
            return false;
        }

        // Kiểm tra trùng lịch bay
        if (kiemTraTrungLich(chuyenBay)) {
            System.out.println("Trùng lịch bay với chuyến khác!");
            return false;
        }

        danhSach.add(chuyenBay);
        System.out.println("Thêm chuyến bay thành công!");
        return true;
    }

    @Override
    public boolean xoa(String maChuyen) {
        for (Iterator<ChuyenBay> iterator = danhSach.iterator(); iterator.hasNext();) {
            ChuyenBay cb = iterator.next();
            if (cb.getMaChuyen().equals(maChuyen)) {
                // Kiểm tra nếu đã có vé đặt cho chuyến này
                if (cb.getSoGheTrong() != cb.getSoGhe()) {
                    System.out.println("Không thể xóa! Đã có vé được đặt cho chuyến này.");
                    return false;
                }
                iterator.remove();
                System.out.println("Xóa chuyến bay thành công!");
                return true;
            }
        }
        System.out.println("Không tìm thấy chuyến bay với mã: " + maChuyen);
        return false;
    }

    @Override
    public boolean sua(String maChuyen, ChuyenBay chuyenBayMoi) {
        for (int i = 0; i < danhSach.size(); i++) {
            if (danhSach.get(i).getMaChuyen().equals(maChuyen)) {
                // Kiểm tra trùng lịch (trừ chính nó)
                if (kiemTraTrungLich(chuyenBayMoi, maChuyen)) {
                    System.out.println("Trùng lịch bay với chuyến khác!");
                    return false;
                }
                danhSach.set(i, chuyenBayMoi);
                System.out.println("Cập nhật chuyến bay thành công!");
                return true;
            }
        }
        System.out.println("Không tìm thấy chuyến bay với mã: " + maChuyen);
        return false;
    }

    @Override
    public ChuyenBay timKiemTheoMa(String maChuyen) {
        return danhSach.stream()
                .filter(cb -> cb.getMaChuyen().equals(maChuyen))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<ChuyenBay> timKiemTheoTen(String ten) {
        // Tìm theo điểm đi/đến
        List<ChuyenBay> ketQua = new ArrayList<>();
        for (ChuyenBay cb : danhSach) {
            if (cb.getDiemDi().toLowerCase().contains(ten.toLowerCase())
                    || cb.getDiemDen().toLowerCase().contains(ten.toLowerCase())) {
                ketQua.add(cb);
            }
        }
        return ketQua;
    }

    // PHƯƠNG THỨC MỚI: Tìm kiếm theo tuyến bay
    public List<ChuyenBay> timKiemTheoTuyen(String diemDi, String diemDen) {
        List<ChuyenBay> ketQua = new ArrayList<>();
        for (ChuyenBay cb : danhSach) {
            if (cb.getDiemDi().equalsIgnoreCase(diemDi)
                    && cb.getDiemDen().equalsIgnoreCase(diemDen)) {
                ketQua.add(cb);
            }
        }
        return ketQua;
    }

    @Override
    public List<ChuyenBay> timKiemTheoNgayBay(Date ngay) {
        List<ChuyenBay> ketQua = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String ngayCanTim = sdf.format(ngay);

        for (ChuyenBay cb : danhSach) {
            String ngayChuyen = sdf.format(cb.getGioKhoiHanh());
            if (ngayChuyen.equals(ngayCanTim)) {
                ketQua.add(cb);
            }
        }
        return ketQua;
    }

    // TÌM KIẾM NÂNG CAO - ĐA TIÊU CHÍ
    public List<ChuyenBay> timKiemChuyenBay(Map<String, Object> filters) {
        List<ChuyenBay> ketQua = new ArrayList<>(danhSach);

        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            switch (key) {
                case "diemDi":
                    ketQua.removeIf(cb -> !cb.getDiemDi().equalsIgnoreCase(value.toString()));
                    break;
                case "diemDen":
                    ketQua.removeIf(cb -> !cb.getDiemDen().equalsIgnoreCase(value.toString()));
                    break;
                case "tuNgay":
                    Date tuNgay = (Date) value;
                    ketQua.removeIf(cb -> cb.getGioKhoiHanh().before(tuNgay));
                    break;
                case "denNgay":
                    Date denNgay = (Date) value;
                    ketQua.removeIf(cb -> cb.getGioKhoiHanh().after(denNgay));
                    break;
                case "conCho":
                    boolean conCho = (boolean) value;
                    if (conCho) {
                        ketQua.removeIf(cb -> !cb.conGheTrong());
                    }
                    break;
                case "maMayBay":
                    ketQua.removeIf(cb -> !cb.getMaMayBay().equals(value));
                    break;
                case "giaMin":
                    double giaMin = (double) value;
                    ketQua.removeIf(cb -> cb.getGiaCoBan() < giaMin);
                    break;
                case "giaMax":
                    double giaMax = (double) value;
                    ketQua.removeIf(cb -> cb.getGiaCoBan() > giaMax);
                    break;
            }
        }

        return ketQua;
    }

    @Override
    public void hienThiTatCa() {
        if (danhSach.isEmpty()) {
            System.out.println("Danh sách chuyến bay trống!");
            return;
        }

        System.out.println("===== DANH SÁCH TẤT CẢ CHUYẾN BAY =====");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        for (int i = 0; i < danhSach.size(); i++) {
            ChuyenBay cb = danhSach.get(i);
            System.out.printf("%d. %s: %s → %s | %s | Ghế: %d/%d | Giá: %,.0f VND | %s%n",
                    i + 1, cb.getMaChuyen(), cb.getDiemDi(), cb.getDiemDen(),
                    sdf.format(cb.getGioKhoiHanh()), cb.getSoGheTrong(), cb.getSoGhe(),
                    cb.getGiaCoBan(), cb.getTrangThai());
        }
    }

    @Override
    public void hienThiTheoTrangThai(String trangThai) {
        List<ChuyenBay> ketQua = danhSach.stream()
                .filter(cb -> cb.getTrangThai().equals(trangThai))
                .toList();

        if (ketQua.isEmpty()) {
            System.out.println("Không có chuyến bay nào với trạng thái: " + trangThai);
            return;
        }

        System.out.println("===== DANH SÁCH CHUYẾN BAY " + trangThai + " =====");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        for (int i = 0; i < ketQua.size(); i++) {
            ChuyenBay cb = ketQua.get(i);
            System.out.printf("%d. %s: %s → %s | %s | Ghế: %d/%d%n",
                    i + 1, cb.getMaChuyen(), cb.getDiemDi(), cb.getDiemDen(),
                    sdf.format(cb.getGioKhoiHanh()), cb.getSoGheTrong(), cb.getSoGhe());
        }
    }

    @Override
    public int demSoLuong() {
        return danhSach.size();
    }

    @Override
    public boolean tonTai(String ma) {
        return danhSach.stream().anyMatch(cb -> cb.getMaChuyen().equals(ma));
    }

    @Override
    public void sapXepTheoMa() {
        danhSach.sort(Comparator.comparing(ChuyenBay::getMaChuyen));
    }

    @Override
    public void sapXepTheoGia() {
        danhSach.sort(Comparator.comparingDouble(ChuyenBay::getGiaCoBan));
    }

    @Override
    public void sapXepTheoNgayBay() {
        danhSach.sort(Comparator.comparing(ChuyenBay::getGioKhoiHanh));
    }

    // SỬA: Thêm phương thức phân trang
    public List<ChuyenBay> phanTrang(int trang, int kichThuocTrang) {
        int batDau = (trang - 1) * kichThuocTrang;
        int ketThuc = Math.min(batDau + kichThuocTrang, danhSach.size());
        
        if (batDau >= danhSach.size()) {
            return new ArrayList<>();
        }
        
        return danhSach.subList(batDau, ketThuc);
    }

    // SỬA: Thêm phương thức tìm kiếm gần đúng
    public List<ChuyenBay> timKiemGanDung(String keyword) {
        List<ChuyenBay> ketQua = new ArrayList<>();
        String keywordLower = keyword.toLowerCase();
        
        for (ChuyenBay cb : danhSach) {
            if (cb.getMaChuyen().toLowerCase().contains(keywordLower) ||
                cb.getDiemDi().toLowerCase().contains(keywordLower) ||
                cb.getDiemDen().toLowerCase().contains(keywordLower) ||
                cb.getMaMayBay().toLowerCase().contains(keywordLower) ||
                cb.getTrangThai().toLowerCase().contains(keywordLower)) {
                ketQua.add(cb);
            }
        }
        return ketQua;
    }

    // ========== IMPLEMENT IFILEHANDLER ==========
    @Override
    public boolean docFile(String tenFile) {
        return docFileXML1(tenFile);
    }

    // SỬA: Sửa phương thức đọc file XML
    private boolean docFileXML1(String tenFile) {
        try {
            List<Map<String, String>> dataList = docFileXML(tenFile);

            if (dataList.isEmpty()) {
                System.out.println("Không có dữ liệu trong file XML");
                return false;
            }

            int count = 0;
            for (Map<String, String> data : dataList) {
                try {
                    // Tạo đối tượng ChuyenBay từ dữ liệu XML
                    ChuyenBay cb = new ChuyenBay(
                            data.get("MaChuyen"),
                            data.get("DiemDi"),
                            data.get("DiemDen"),
                            XMLUtils.stringToDate(data.get("GioKhoiHanh")),
                            XMLUtils.stringToDate(data.get("GioDen")),
                            XMLUtils.stringToInt(data.get("SoGhe")),
                            XMLUtils.stringToInt(data.get("SoGheTrong")),
                            data.get("MaMayBay"),
                            XMLUtils.stringToDouble(data.get("GiaCoBan"))
                    );

                    // Cập nhật trạng thái
                    cb.setTrangThai(data.get("TrangThai"));

                    // Thêm vào danh sách (kiểm tra trùng trước khi thêm)
                    if (!tonTai(cb.getMaChuyen())) {
                        danhSach.add(cb);
                        count++;
                    } else {
                        System.out.println("Bỏ qua chuyến bay trùng mã: " + cb.getMaChuyen());
                    }

                } catch (Exception e) {
                    System.out.println("Lỗi tạo ChuyenBay từ XML: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            System.out.println("Đã đọc thành công " + count + " chuyến bay từ file XML.");
            return true;

        } catch (Exception e) {
            System.out.println("Lỗi đọc file XML: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean ghiFile(String tenFile) {
        return ghiFileXML(tenFile);
    }

    // SỬA: Sửa phương thức ghi file XML
    private boolean ghiFileXML(String tenFile) {
        try {
            List<Map<String, String>> dataList = new ArrayList<>();

            for (ChuyenBay cb : danhSach) {
                Map<String, String> data = new HashMap<>();
                data.put("MaChuyen", cb.getMaChuyen());
                data.put("DiemDi", cb.getDiemDi());
                data.put("DiemDen", cb.getDiemDen());
                data.put("GioKhoiHanh", XMLUtils.dateToString(cb.getGioKhoiHanh()));
                data.put("GioDen", XMLUtils.dateToString(cb.getGioDen()));
                data.put("SoGhe", String.valueOf(cb.getSoGhe()));
                data.put("SoGheTrong", String.valueOf(cb.getSoGheTrong()));
                data.put("MaMayBay", cb.getMaMayBay());
                data.put("GiaCoBan", String.valueOf((int) cb.getGiaCoBan()));
                data.put("TrangThai", cb.getTrangThai());

                dataList.add(data);
            }

            return ghiFileXML(tenFile, dataList, "ChuyenBays");

        } catch (Exception e) {
            System.out.println("Lỗi ghi file XML: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ========== PHƯƠNG THỨC NGHIỆP VỤ ==========
    private boolean kiemTraTrungLich(ChuyenBay chuyenBayMoi) {
        return kiemTraTrungLich(chuyenBayMoi, null);
    }

    private boolean kiemTraTrungLich(ChuyenBay chuyenBayMoi, String maLoaiTru) {
        for (ChuyenBay cb : danhSach) {
            // Bỏ qua chuyến bay đang chỉnh sửa
            if (maLoaiTru != null && cb.getMaChuyen().equals(maLoaiTru)) {
                continue;
            }

            // Kiểm tra trùng máy bay và thời gian
            if (cb.getMaMayBay().equals(chuyenBayMoi.getMaMayBay())) {
                long thoiGianTrung = Math.abs(cb.getGioKhoiHanh().getTime()
                        - chuyenBayMoi.getGioKhoiHanh().getTime());
                // Nếu cùng máy bay và cách nhau dưới 4 tiếng -> trùng
                if (thoiGianTrung < 4 * 60 * 60 * 1000) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<ChuyenBay> getChuyenBayConCho() {
        return danhSach.stream()
                .filter(ChuyenBay::conGheTrong)
                .toList();
    }

    public boolean datGheChuyenBay(String maChuyen) {
        ChuyenBay cb = timKiemTheoMa(maChuyen);
        if (cb != null && cb.conGheTrong()) {
            return cb.datGhe();
        }
        return false;
    }

    public boolean huyGheChuyenBay(String maChuyen) {
        ChuyenBay cb = timKiemTheoMa(maChuyen);
        if (cb != null) {
            return cb.huyGhe();
        }
        return false;
    }

    // SỬA: Cập nhật trạng thái chuyến bay tự động
    public void capNhatTrangThaiChuyenBay() {
        Date now = new Date();
        for (ChuyenBay cb : danhSach) {
            cb.capNhatTrangThaiBay(); // Sử dụng phương thức từ class ChuyenBay
        }
    }

    public Map<String, Integer> thongKeChuyenBayTheoTuyen() {
        Map<String, Integer> thongKe = new HashMap<>();
        for (ChuyenBay cb : danhSach) {
            String tuyen = cb.getDiemDi() + " - " + cb.getDiemDen();
            thongKe.put(tuyen, thongKe.getOrDefault(tuyen, 0) + 1);
        }
        return thongKe;
    }

    // SỬA: Thêm phương thức thống kê doanh thu ước tính
    public Map<String, Double> thongKeDoanhThuUocTinh() {
        Map<String, Double> thongKe = new HashMap<>();
        for (ChuyenBay cb : danhSach) {
            String tuyen = cb.getDiemDi() + " - " + cb.getDiemDen();
            double doanhThuUocTinh = cb.getGiaCoBan() * (cb.getSoGhe() - cb.getSoGheTrong());
            thongKe.put(tuyen, thongKe.getOrDefault(tuyen, 0.0) + doanhThuUocTinh);
        }
        return thongKe;
    }

    public List<ChuyenBay> getDanhSach() {
        return new ArrayList<>(danhSach);
    }

    // SỬA: Sửa các phương thức không áp dụng
    @Override
    public List<ChuyenBay> timKiemTheoCMND(String cmnd) {
        // Không áp dụng cho chuyến bay
        System.out.println("Phương thức không áp dụng cho chuyến bay");
        return new ArrayList<>();
    }

    @Override
    public List<ChuyenBay> timKiemTheoChuyenBay(String maChuyen) {
        // Trả về chuyến bay với mã cụ thể
        ChuyenBay cb = timKiemTheoMa(maChuyen);
        List<ChuyenBay> ketQua = new ArrayList<>();
        if (cb != null) {
            ketQua.add(cb);
        }
        return ketQua;
    }

    @Override
    public List<ChuyenBay> timKiemTheoKhoangGia(double min, double max) {
        return danhSach.stream()
                .filter(cb -> cb.getGiaCoBan() >= min && cb.getGiaCoBan() <= max)
                .toList();
    }
public static void main(String[] args) {
    DanhSachChuyenBay ds = new DanhSachChuyenBay();
    ds.docFileXML1("src/resources/data/1_ChuyenBays.xml");
    ds.hienThiTatCa();
}
}