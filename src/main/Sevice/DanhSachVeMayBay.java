package Sevice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import model.VeMayBay;
import model.VePhoThong;
import model.VeThuongGia;
import model.VeTietKiem;
import repository.IFileHandler;
import repository.IQuanLy;
import repository.IThongKe;
import repository.XMLUtils;

public class DanhSachVeMayBay implements IQuanLy<VeMayBay>, IFileHandler, IThongKe {
  private List<VeMayBay> danhSach;
  private static final int MAX_SIZE = 10000;

  public DanhSachVeMayBay() {
    this.danhSach = new ArrayList<>();
  }

  // ========== GETTERS ==========
  public List<VeMayBay> getDanhSach() {
    return new ArrayList<>(danhSach);
  }

  // ========== IMPLEMENT IQUANLY ==========
  @Override
  public boolean them(VeMayBay ve) {
    if (danhSach.size() >= MAX_SIZE) {
      throw new IllegalStateException("Danh sách vé đã đầy!");
    }

    if (tonTai(ve.getMaVe())) {
      throw new IllegalArgumentException("Mã vé '" + ve.getMaVe() + "' đã tồn tại!");
    }
    return danhSach.add(ve);
  }

  @Override
  public boolean xoa(String maVe) {
    VeMayBay ve = timKiemTheoMa(maVe);
    if (ve == null) {
      throw new IllegalArgumentException("Không tìm thấy vé với mã: " + maVe);
    }

    // Kiểm tra nếu vé đã thanh toán thì không thể xóa
    if (ve.isDaThanhToan()) {
      throw new IllegalStateException("Không thể xóa vé đã thanh toán!");
    }

    return danhSach.remove(ve);
  }

  @Override
  public boolean sua(String maVe, VeMayBay veMoi) {
    VeMayBay veCu = timKiemTheoMa(maVe);
    if (veCu == null) {
      throw new IllegalArgumentException("Không tìm thấy vé với mã: " + maVe);
    }

    // Kiểm tra nếu vé đã thanh toán thì chỉ cho sửa một số thông tin
    if (veCu.isDaThanhToan()) {
      throw new IllegalStateException("Không thể sửa vé đã thanh toán!");
    }

    int index = danhSach.indexOf(veCu);
    danhSach.set(index, veMoi);
    return true;
  }

  @Override
  public VeMayBay timKiemTheoMa(String maVe) {
    return danhSach.stream().filter(ve -> ve.getMaVe().equalsIgnoreCase(maVe)).findFirst().orElse(null);
  }

  @Override
  public List<VeMayBay> timKiemTheoTen(String ten) {
    // Class VeMayBay của bạn không có thuộc tính tên, nên trả về danh sách rỗng
    return new ArrayList<>();
  }

  @Override
  public VeMayBay timKiemTheoCMND(String cmnd) {
    // Class VeMayBay của bạn không có thuộc tính CMND
    return null;
  }

  @Override
  public List<VeMayBay> timKiemTheoChuyenBay(String maChuyen) {
    return danhSach.stream()
        .filter(ve -> ve.getMaChuyen().equalsIgnoreCase(maChuyen))
        .collect(Collectors.toList());
  }

  @Override
  public List<VeMayBay> timKiemTheoKhoangGia(double min, double max) {
    return danhSach.stream()
        .filter(ve -> ve.tinhTongTien() >= min && ve.tinhTongTien() <= max)
        .collect(Collectors.toList());
  }

  @Override
  public List<VeMayBay> timKiemTheoNgayBay(Date ngay) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String ngayCanTim = sdf.format(ngay);

    return danhSach.stream()
        .filter(ve -> sdf.format(ve.getNgayBay()).equals(ngayCanTim))
        .collect(Collectors.toList());
  }

  // ========== TÌM KIẾM NÂNG CAO CHO GUI ==========
  public List<VeMayBay> timKiemDaTieuChi(Map<String, Object> filters) {
    List<VeMayBay> ketQua = new ArrayList<>(danhSach);

    for (Map.Entry<String, Object> entry : filters.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();

      if (value == null || value.toString().isEmpty()) {
        continue;
      }

      switch (key) {
        case "loaiVe":
          ketQua.removeIf(ve -> !ve.loaiVe().equals(value));
          break;
        case "maKH":
          ketQua.removeIf(ve -> !ve.getmaKH().equals(value));
          break;
        case "maVe":
          ketQua.removeIf(ve -> !ve.getMaVe().equals(value));
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
          ketQua.removeIf(ve -> ve.tinhTongTien() < giaMin);
          break;
        case "giaMax":
          double giaMax = (double) value;
          ketQua.removeIf(ve -> ve.tinhTongTien() > giaMax);
          break;
        case "soGhe":
          ketQua.removeIf(ve -> !ve.getSoGhe().contains(value.toString()));
          break;
      }
    }

    return ketQua;
  }

  public List<VeMayBay> timKiemGanDung(String keyword) {
    if (keyword == null || keyword.trim().isEmpty()) {
      return new ArrayList<>(danhSach);
    }

    String keywordLower = keyword.toLowerCase().trim();
    return danhSach.stream().filter(ve -> ve.getMaVe().toLowerCase().contains(keywordLower) ||
        ve.getMaChuyen().toLowerCase().contains(keywordLower) ||
        ve.getSoGhe().toLowerCase().contains(keywordLower) ||
        ve.loaiVe().toLowerCase().contains(keywordLower) ||
        ve.getTrangThai().toLowerCase().contains(keywordLower))
                      .collect(Collectors.toList());
    }
    
    @Override
    public void hienThiTatCa() {
        if (danhSach.isEmpty()) {
            System.out.println("📭 Danh sách vé trống!");
            return;
        }
        
        System.out.println("====== 📋 DANH SÁCH TẤT CẢ VÉ (" + danhSach.size() + " vé) ======");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        for (int i = 0; i < danhSach.size(); i++) {
            VeMayBay ve = danhSach.get(i);
            System.out.printf("%d. %s - %s - %s - %s - %,.0f VND - %s\n",
                i + 1, ve.getMaVe(), ve.loaiVe(), ve.getMaChuyen(),
                sdf.format(ve.getNgayBay()),
                ve.tinhTongTien(), ve.getTrangThai());
        }
    }
    
    @Override
    public void hienThiTheoTrangThai(String trangThai) {
        List<VeMayBay> ketQua = danhSach.stream()
                                       .filter(ve -> ve.getTrangThai().equals(trangThai))
                                       .collect(Collectors.toList());
        
        if (ketQua.isEmpty()) {
            System.out.println("📭 Không có vé nào với trạng thái: " + trangThai);
            return;
        }
        
        System.out.println("====== 📋 DANH SÁCH VÉ " + trangThai + " (" + ketQua.size() + " vé) ======");
        for (int i = 0; i < ketQua.size(); i++) {
            VeMayBay ve = ketQua.get(i);
            System.out.printf("%d. %s - %s - %s - %,.0f VND\n",
                i + 1, ve.getMaVe(), ve.loaiVe(), ve.getMaChuyen(), ve.tinhTongTien());
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
    }
    
    @Override
    public void sapXepTheoGia() {
        danhSach.sort(Comparator.comparingDouble(VeMayBay::tinhTongTien));
    }
    
    @Override
    public void sapXepTheoNgayBay() {
        danhSach.sort(Comparator.comparing(VeMayBay::getNgayBay));
    }
    
    // ========== IMPLEMENT IFILEHANDLER ==========
    @Override
    public boolean docFile(String tenFile) {
        try {
            List<Map<String, String>> dataList = XMLUtils.docFileXML(tenFile);
            
            if (dataList == null || dataList.isEmpty()) {
                System.out.println("Khong co du lieu hoac file khong ton tai");
                return false;
            }
            
            int countSuccess = 0;
            for (Map<String, String> data : dataList) {
                try {
                    VeMayBay ve = taoVeTuDataXML(data);
                    if (ve == null) {
                        continue;
                    }
                    
                    // KIỂM TRA TRÙNG MÃ VÉ
                    if (tonTai(ve.getMaVe())) {
                        continue;
                    }
                    
                    // THÊM VÀO DANH SÁCH
                    danhSach.add(ve);
                    countSuccess++;
                    
                } catch (Exception e) {
                    System.out.println("Loi xu ly ve: " + data.get("MaVe") + " - " + e.getMessage());
                }
            }
            return countSuccess > 0;
            
        } catch (Exception e) {
            System.out.println("Loi nghiem trong khi doc file file: " + e.getMessage());
            return false;
        }
    }
    // Phương thức tạo vé từ dữ liệu XML - PHÙ HỢP VỚI 3 LOẠI VÉ CỦA BẠN
    private VeMayBay taoVeTuDataXML(Map<String, String> data) {
        try {
            String loaiVe = data.get("LoaiVe");
            String maKH = data.get("MaKhachHang");
            String maVe = data.get("MaVe");
            Date ngayBay = XMLUtils.stringToDate(data.get("NgayBay"));
            double giaVe = XMLUtils.stringToDouble(data.get("GiaVe"));
            String maChuyen = data.get("MaChuyen");
            String soGhe = data.get("SoGhe");
            String trangThai = data.get("TrangThai");
            
            // Tạo vé theo loại - PHÙ HỢP VỚI CONSTRUCTOR CỦA BẠN
            switch (loaiVe) {
                case "VeThuongGia":
                    return new VeThuongGia(
                        maKH, maVe, ngayBay, giaVe, maChuyen, soGhe,
                        data.get("DichVuDacBiet"),
                        XMLUtils.stringToDouble(data.get("PhuThu")),
                        XMLUtils.stringToBoolean(data.get("PhongChoVIP")),XMLUtils.stringToInt(data.get("SoKgHanhLyKyGui")),
                        data.get("LoaiDoUong")
                    );
                    
                case "VePhoThong":
                    return new VePhoThong(
                        maKH,maVe, ngayBay, giaVe, maChuyen, soGhe,
                        XMLUtils.stringToBoolean(data.get("HanhLyXachTay")),
                        XMLUtils.stringToInt(data.get("SoKgHanhLyKyGui")),
                        data.get("LoaiGhe"),
                        XMLUtils.stringToBoolean(data.get("DoAn"))
                    );
                    
                case "VeTietKiem":
                    return new VeTietKiem(
                        maKH,maVe, ngayBay, giaVe, maChuyen, soGhe,
                        XMLUtils.stringToBoolean(data.get("HanhLyXachTay"))
                    );
                    
                default:
                    System.out.println("Loai ve khong hop le: " + loaiVe);
                    return null;
            }
        } catch (Exception e) {
            System.out.println("Loi tao ve tu XML data: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public boolean ghiFile(String tenFile) {
        try {
            List<Map<String, String>> dataList = new ArrayList<>();
            
            for (VeMayBay ve : danhSach) {
                Map<String, String> data = new HashMap<>();
                
                // Thông tin chung theo class VeMayBay
                data.put("MaVe", ve.getMaVe());
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
                    data.put("SoKgHanhLyKyGui", String.valueOf(vtg.getSoKgHanhLyKiGui()));
                    data.put("PhongChoVIP", String.valueOf(vtg.isPhongChoVIP()));
                    data.put("LoaiDoUong", vtg.getLoaiDoUong());
                } 
                else if (ve instanceof VePhoThong vpt) {
                    data.put("LoaiVe", "VePhoThong");
                    data.put("HanhLyXachTay", String.valueOf(vpt.isHanhLyXachTay()));
                    data.put("SoKgHanhLyKyGui", String.valueOf(vpt.getSoKgHanhLyKyGui()));
                    data.put("LoaiGhe", vpt.getLoaiGhe());
                    data.put("DoAn", String.valueOf(vpt.isDoAn()));
                }
                else if (ve instanceof VeTietKiem vtk) {
                    data.put("LoaiVe", "VeTietKiem");
                    data.put("HanhLyXachTay", String.valueOf(vtk.hanhLyXachTay()));
                }
                
                dataList.add(data);
            }
            
            boolean result = XMLUtils.ghiFileXML(tenFile, dataList, "VeMayBays");
            if (result) {
                System.out.println("Ghi file thanh cong: " + danhSach.size() + " ve");
            }
            return result;
            
        } catch (Exception e) {
            System.out.println("Loi ghi fileL: " + e.getMessage());
            return false;
        }
    }
    
    // ========== IMPLEMENT ITHONGKE ==========
    @Override
    public double tinhTongDoanhThu() {
        return danhSach.stream()
                      .filter(ve -> ve.isDaThanhToan())
                      .mapToDouble(VeMayBay::tinhTongTien)
                      .sum();
    }
    
    @Override
    public int demSoLuongTheoLoai(String loai) {
        return (int) danhSach.stream()
                            .filter(ve -> ve.loaiVe().equals(loai))
                            .count();
    }
    
    @Override
    public double tinhDoanhThuTheoLoai(String loai) {
        return danhSach.stream().filter(ve -> ve.loaiVe().equals(loai)).mapToDouble(VeMayBay::tinhTongTien).sum();
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
            if (!ve.isDaThanhToan()) continue;
            
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
            if (ve.isDaThanhToan()) {
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
                if (ve.isDaThanhToan()) {
                    tongDoanhThu += ve.tinhTongTien();
                }
                
                // Thống kê theo loại
                String loai = ve.loaiVe();
                theoLoai.put(loai, theoLoai.getOrDefault(loai, 0) + 1);
                if (ve.isDaThanhToan()) {
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
        // Class VeMayBay của bạn không có thông tin khách hàng
        return new HashMap<>();
    }
    
    @Override
    public double tinhTyLeDoanhThuTheoLoai() {
        return 1.0;
    }
    
    @Override
    public Map<String, Double> thongKeTyLeDoanhThu() {
        Map<String, Double> tyLe = new HashMap<>();
        double tongDoanhThu = tinhTongDoanhThu();
        
        if (tongDoanhThu > 0) {
            String[] loaiVes = {"VeThuongGia", "VePhoThong", "VeTietKiem"};
            for (String loai : loaiVes) {
                double doanhThuLoai = tinhDoanhThuTheoLoai(loai);
                tyLe.put(loai, (doanhThuLoai / tongDoanhThu) * 100);
            }
        }
        
        return tyLe;
    }
    
    // ========== PHƯƠNG THỨC BỔ SUNG CHO GUI ==========
    
    public boolean huyVe(String maVe) {
        VeMayBay ve = timKiemTheoMa(maVe);
        if (ve == null) {
            throw new IllegalArgumentException("Không tìm thấy vé với mã: " + maVe);
        }
        
        try {
            if (!ve.coTheHuy()) {
            String thongBao = ve.getThongBaoKhongTheHuy();
            throw new IllegalStateException(thongBao != null ? thongBao : "Không thể hủy vé");
        }
        else {ve.setTrangThai(VeMayBay.TRANG_THAI_DA_HUY);}
            return true;
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Không thể hủy vé: " + e.getMessage());
        }
    }
    
    public boolean kiemTraCoTheHuy(String maVe) {
        VeMayBay ve = timKiemTheoMa(maVe);
        return ve != null && ve.coTheHuy();
    }
    
    public boolean kiemTraCoTheDoi(String maVe) {
        VeMayBay ve = timKiemTheoMa(maVe);
        return ve != null && ve.coTheDoi();
    }
    
    public void capNhatTrangThaiBay() {
        for (VeMayBay ve : danhSach) {
            ve.capNhatTrangThaiBay();
        }
    }
    
    public int demSoLuongTheoChuyenBay(String maChuyen) {
        if (maChuyen == null) return 0;
        
        return (int) danhSach.stream()
                           .filter(ve -> maChuyen.equals(ve.getMaChuyen()))
                           .count();
    }
    
    // ========== THỐNG KÊ NÂNG CAO CHO GUI ==========
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
        
        return tongHop;
    }
    
    @Override
    public List<Map<String, Object>> thongKeTopKhachHang(int limit) {
        // Class VeMayBay của bạn không có thông tin khách hàng
        return new ArrayList<>();
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
    
    // ========== PHƯƠNG THỨC TIỆN ÍCH CHO GUI ==========
    public List<String> getDanhSachLoaiVe() {
        return Arrays.asList("VeThuongGia", "VePhoThong", "VeTietKiem");
    }
    
    public List<String> getDanhSachTrangThai() {
        return Arrays.asList(
            VeMayBay.TRANG_THAI_DA_DAT,
            VeMayBay.TRANG_THAI_DA_THANH_TOAN,
            VeMayBay.TRANG_THAI_DA_HUY,
            VeMayBay.TRANG_THAI_DA_BAY
        );
    }
    
    public List<String> getDanhSachMaChuyen() {
        return danhSach.stream()
                      .map(VeMayBay::getMaChuyen)
                      .distinct()
                      .sorted()
                      .collect(Collectors.toList());
    }
    
    public Map<String, Object> thongKeTongQuan() {
        Map<String, Object> thongKe = new HashMap<>();
        thongKe.put("tongVe", danhSach.size());
        thongKe.put("tongDoanhThu", tinhTongDoanhThu());
        thongKe.put("veConTrong", danhSach.stream().filter(VeMayBay::isConTrong).count());
        thongKe.put("veDaDat", danhSach.stream().filter(VeMayBay::isDaDat).count());
        thongKe.put("veDaThanhToan", danhSach.stream().filter(VeMayBay::isDaThanhToan).count());
        thongKe.put("veDaHuy", danhSach.stream().filter(VeMayBay::isDaHuy).count());
        thongKe.put("veDaBay", danhSach.stream().filter(VeMayBay::daBay).count());
        
        return thongKe;
    }
    
    // ========== MAIN METHOD FOR TESTING ==========
    // public static void main(String[] args) {
    //     DanhSachVeMayBay ds = new DanhSachVeMayBay();
    //     ds.docFile("src/resources/data/3_VeMayBays.xml");
    //     ds.hienThiTatCa();
    //     System.out.println("Tổng doanh thu: " + (long)ds.tinhTongDoanhThu());
    // }
}
