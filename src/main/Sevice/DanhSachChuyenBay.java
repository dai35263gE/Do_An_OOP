package Sevice;

import java.util.*;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;

import model.ChuyenBay;
import repository.IFileHandler;
import repository.IQuanLy;
import repository.XMLUtils;

public class DanhSachChuyenBay implements IQuanLy<ChuyenBay>, IFileHandler {

    private ArrayList<ChuyenBay> danhSach;
    private static final int MAX_SIZE = 1000;

    public DanhSachChuyenBay() {
        danhSach = new ArrayList<>();
    }

    public ArrayList<ChuyenBay> getDanhSachChuyenBay() {
        return new ArrayList<>(danhSach);
    }
    public List<ChuyenBay> getDanhSach() {
        return new ArrayList<>(danhSach);
    }

    @Override
    public boolean them(ChuyenBay chuyenBay) {
        if (danhSach.size() >= MAX_SIZE) {
            throw new IllegalStateException("Danh sách chuyến bay đã đầy!");
        }

        if (tonTai(chuyenBay.getMaChuyen())) {
            throw new IllegalArgumentException("Mã chuyến bay đã tồn tại: " + chuyenBay.getMaChuyen());
        }

        if (kiemTraTrungLich(chuyenBay)) {
            throw new IllegalArgumentException("Trùng lịch bay với chuyến khác!");
        }

        return danhSach.add(chuyenBay);
    }
    @Override
    public boolean xoa(String maChuyen) {
        ChuyenBay chuyenBay = timKiemTheoMa(maChuyen);
        if (chuyenBay == null) {
            throw new IllegalArgumentException("Không tìm thấy chuyến bay với mã: " + maChuyen);
        }

        if (chuyenBay.getSoGheTrong() != chuyenBay.getSoGhe()) {
            throw new IllegalStateException("Không thể xóa! Đã có vé được đặt cho chuyến này.");
        }

        return danhSach.remove(chuyenBay);
    }
    @Override
    public boolean sua(String maChuyen, ChuyenBay chuyenBayMoi) {
        ChuyenBay chuyenBayCu = timKiemTheoMa(maChuyen);
        if (chuyenBayCu == null) {
            throw new IllegalArgumentException("Không tìm thấy chuyến bay với mã: " + maChuyen);
        }

        if (!maChuyen.equals(chuyenBayMoi.getMaChuyen()) && tonTai(chuyenBayMoi.getMaChuyen())) {
            throw new IllegalArgumentException("Mã chuyến bay mới đã tồn tại!");
        }

        if (kiemTraTrungLich(chuyenBayMoi, maChuyen)) {
            throw new IllegalArgumentException("Trùng lịch bay với chuyến khác!");
        }

        int index = danhSach.indexOf(chuyenBayCu);
        danhSach.set(index, chuyenBayMoi);
        return true;
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
        String keyword = ten.toLowerCase();
        return danhSach.stream()
                .filter(cb -> cb.getDiemDi().toLowerCase().contains(keyword) ||
                             cb.getDiemDen().toLowerCase().contains(keyword))
                .collect(Collectors.toList());
    }

    @Override
    public List<ChuyenBay> timKiemTheoNgayBay(Date ngay) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String ngayCanTim = sdf.format(ngay);

        return danhSach.stream()
                .filter(cb -> sdf.format(cb.getGioKhoiHanh()).equals(ngayCanTim))
                .collect(Collectors.toList());
    }

    @Override
    public List<ChuyenBay> timKiemTheoKhoangGia(double min, double max) {
        return danhSach.stream()
                .filter(cb -> cb.getGiaCoBan() >= min && cb.getGiaCoBan() <= max)
                .collect(Collectors.toList());
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
                .collect(Collectors.toList());

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

    // ========== PHƯƠNG THỨC TÌM KIẾM NÂNG CAO CHO GUI ==========
    public List<ChuyenBay> timKiemTheoTuyen(String diemDi, String diemDen) {
        return danhSach.stream()
                .filter(cb -> cb.getDiemDi().equalsIgnoreCase(diemDi) &&
                             cb.getDiemDen().equalsIgnoreCase(diemDen))
                .collect(Collectors.toList());
    }

    public List<ChuyenBay> timKiemGanDung(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>(danhSach);
        }
        
        String keywordLower = keyword.toLowerCase().trim();
        
        return danhSach.stream()
                .filter(cb -> cb.getMaChuyen().toLowerCase().contains(keywordLower) ||
                             cb.getDiemDi().toLowerCase().contains(keywordLower) ||
                             cb.getDiemDen().toLowerCase().contains(keywordLower) ||
                             cb.getMaMayBay().toLowerCase().contains(keywordLower) ||
                             cb.getTrangThai().toLowerCase().contains(keywordLower))
                .collect(Collectors.toList());
    }

    public List<ChuyenBay> timKiemChuyenBay(Map<String, Object> filters) {
        List<ChuyenBay> ketQua = new ArrayList<>(danhSach);

        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value == null || value.toString().isEmpty()) {
                continue;
            }

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
                case "trangThai":
                    ketQua.removeIf(cb -> !cb.getTrangThai().equals(value));
                    break;
            }
        }

        return ketQua;
    }
    @Override
    public boolean docFile(String tenFile) {
        try {
            List<Map<String, String>> dataList = XMLUtils.docFileXML(tenFile);

            if (dataList.isEmpty()) {
                System.out.println("Khong co du lieu trong file");
                return false;
            }

            int count = 0;
            for (Map<String, String> data : dataList) {
                try {
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

                    cb.setTrangThai(data.get("TrangThai"));

                    if (!tonTai(cb.getMaChuyen())) {
                        danhSach.add(cb);
                        count++;
                    }

                } catch (Exception e) {
                    System.err.println("Loi tao chuyen bay: " + e.getMessage());
                }
            }
            
            System.out.println("Da tai " + count + " chuyen bay tu file XML");
            return true;

        } catch (Exception e) {
            System.err.println("Loi doc file: " + e.getMessage());
            return false;
        }
    }

    @Override

    public boolean ghiFile(String tenFile) {
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

            return XMLUtils.ghiFileXML(tenFile, dataList, "ChuyenBays");

        } catch (Exception e) {
            System.err.println("Loi ghi file XML: " + e.getMessage());
            return false;
        }
    }

    // ========== PHƯƠNG THỨC NGHIỆP VỤ CHO GUI ==========
    private boolean kiemTraTrungLich(ChuyenBay chuyenBayMoi) {
        return kiemTraTrungLich(chuyenBayMoi, null);
    }
    private boolean kiemTraTrungLich(ChuyenBay chuyenBayMoi, String maLoaiTru) {
        for (ChuyenBay cb : danhSach) {
            if (maLoaiTru != null && cb.getMaChuyen().equals(maLoaiTru)) {
                continue;
            }

            if (cb.getMaMayBay().equals(chuyenBayMoi.getMaMayBay())) {
                long thoiGianTrung = Math.abs(cb.getGioKhoiHanh().getTime() - chuyenBayMoi.getGioKhoiHanh().getTime());
                if (thoiGianTrung < 4 * 60 * 60 * 1000) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<ChuyenBay> getChuyenBayConCho() {
        return danhSach.stream().filter(ChuyenBay::conGheTrong).collect(Collectors.toList());
    }

    public boolean datGheChuyenBay(String maChuyen) {
        ChuyenBay cb = timKiemTheoMa(maChuyen);
        return cb != null && cb.datGhe();
    }

    public boolean huyGheChuyenBay(String maChuyen) {
        ChuyenBay cb = timKiemTheoMa(maChuyen);
        return cb != null && cb.huyGhe();
    }

    // ========== THỐNG KÊ CHO GUI ==========
    public Map<String, Integer> thongKeChuyenBayTheoTuyen() {
        return danhSach.stream()
                .collect(Collectors.groupingBy(
                    cb -> cb.getDiemDi() + " - " + cb.getDiemDen(),
                    Collectors.summingInt(cb -> 1)
                ));
    }

    public Map<String, Double> thongKeDoanhThuUocTinh() {
        return danhSach.stream()
                .collect(Collectors.groupingBy(
                    cb -> cb.getDiemDi() + " - " + cb.getDiemDen(),
                    Collectors.summingDouble(cb -> 
                        cb.getGiaCoBan() * (cb.getSoGhe() - cb.getSoGheTrong())
                    )
                ));
    }

    public Map<String, Integer> thongKeTheoTrangThai() {
        return danhSach.stream()
                .collect(Collectors.groupingBy(
                    ChuyenBay::getTrangThai,
                    Collectors.summingInt(cb -> 1)
                ));
    }

    public Map<String, Object> thongKeTongQuan() {
        Map<String, Object> thongKe = new HashMap<>();
        thongKe.put("tongChuyenBay", danhSach.size());
        thongKe.put("chuyenBayConCho", getChuyenBayConCho().size());
        thongKe.put("tongGheTrong", danhSach.stream().mapToInt(ChuyenBay::getSoGheTrong).sum());
        thongKe.put("tongGhe", danhSach.stream().mapToInt(ChuyenBay::getSoGhe).sum());
        thongKe.put("tyLeGheTrong", String.format("%.1f%%", 
            (double) thongKe.get("tongGheTrong") / (int) thongKe.get("tongGhe") * 100));
        
        return thongKe;
    }

    // ========== PHƯƠNG THỨC TIỆN ÍCH CHO GUI ==========
    public List<String> getDanhSachDiemDi() {
        return danhSach.stream().map(ChuyenBay::getDiemDi).distinct().sorted()
.collect(Collectors.toList());
    }

    public List<String> getDanhSachDiemDen() {
        return danhSach.stream().map(ChuyenBay::getDiemDen).distinct().sorted().collect(Collectors.toList());
    }

    public List<String> getDanhSachMaMayBay() {
        return danhSach.stream().map(ChuyenBay::getMaMayBay).distinct().sorted().collect(Collectors.toList());
    }

    public List<String> getDanhSachTrangThai() {
        return danhSach.stream().map(ChuyenBay::getTrangThai).distinct().sorted().collect(Collectors.toList());
    }

    // ========== PHƯƠNG THỨC KHÔNG ÁP DỤNG ==========
    @Override
    public ChuyenBay timKiemTheoCMND(String cmnd) {
        throw new UnsupportedOperationException("Phương thức không áp dụng cho chuyến bay");
    }

    @Override
    public List<ChuyenBay> timKiemTheoChuyenBay(String maChuyen) {
        ChuyenBay cb = timKiemTheoMa(maChuyen);
        return cb != null ? Arrays.asList(cb) : new ArrayList<>();
    }

     // ========== MAIN METHOD FOR TESTING ==========
     public static void main(String[] args) {
        DanhSachChuyenBay ds = new DanhSachChuyenBay();
         ds.docFile("src/resources/data/1_ChuyenBays.xml");
        ds.hienThiTatCa();
     }
}