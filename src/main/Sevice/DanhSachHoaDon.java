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

import model.HoaDon;
import model.KhachHang;
import model.VeMayBay;
import repository.IFileHandler;
import repository.IQuanLy;
import repository.IThongKe;
import repository.XMLUtils;

public class DanhSachHoaDon implements IQuanLy<HoaDon>, IFileHandler, IThongKe {

  private List<HoaDon> danhSach;
  private static final int MAX_SIZE = 5000;

  public DanhSachHoaDon() {
    this.danhSach = new ArrayList<>();
  }

  // ========== GETTERS ==========
  public List<HoaDon> getDanhSach() {
    return new ArrayList<>(danhSach);
  }

  public List<HoaDon> getDanhSachHoaDon() {
    return new ArrayList<>(danhSach);
  }

  // ========== IMPLEMENT IQUANLY ==========
  @Override
  public boolean them(HoaDon hoaDon) {
    if (danhSach.size() >= MAX_SIZE) {
      throw new IllegalStateException("Danh sách hóa đơn đã đầy!");
    }

    return danhSach.add(hoaDon);
  }

  @Override
  public boolean xoa(String maHoaDon) {
    HoaDon hoaDon = timKiemTheoMa(maHoaDon);
    if (hoaDon == null) {
      throw new IllegalArgumentException("Không tìm thấy hóa đơn với mã: " + maHoaDon);
    }

    // Kiểm tra nếu hóa đơn đã thanh toán thì không thể xóa
    if (hoaDon.getTrangThai().equals(HoaDon.TT_DA_TT)) {
      throw new IllegalStateException("Không thể xóa hóa đơn đã thanh toán!");
    }

    return danhSach.remove(hoaDon);
  }

  @Override
  public boolean sua(String maHoaDon, HoaDon hoaDonMoi) {
    HoaDon hoaDonCu = timKiemTheoMa(maHoaDon);
    if (hoaDonCu == null) {
      throw new IllegalArgumentException("Không tìm thấy hóa đơn với mã: " + maHoaDon);
    }

    // Kiểm tra nếu hóa đơn đã thanh toán thì không thể sửa
    if (hoaDonCu.getTrangThai().equals(HoaDon.TT_DA_TT)) {
      throw new IllegalStateException("Không thể sửa hóa đơn đã thanh toán!");
    }

    int index = danhSach.indexOf(hoaDonCu);
    danhSach.set(index, hoaDonMoi);
    return true;
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
    // Tìm theo tên khách hàng
    String keyword = ten.toLowerCase();
    return danhSach.stream()
        .filter(hd -> hd.getKhachHang().getHoTen().toLowerCase().contains(keyword))
        .collect(Collectors.toList());
  }

  @Override
  public HoaDon timKiemTheoCMND(String cmnd) {
    return danhSach.stream()
        .filter(hd -> hd.getKhachHang().getCmnd().equals(cmnd))
        .findFirst()
        .orElse(null);
  }

  @Override
  public List<HoaDon> timKiemTheoChuyenBay(String maChuyen) {
    return danhSach.stream()
        .filter(hd -> hd.getDanhSachVe().stream()
            .anyMatch(ve -> ve.getMaChuyen().equals(maChuyen)))
        .collect(Collectors.toList());
  }

  @Override
  public List<HoaDon> timKiemTheoKhoangGia(double min, double max) {
    return danhSach.stream()
        .filter(hd -> hd.getThanhTien() >= min && hd.getThanhTien() <= max)
        .collect(Collectors.toList());
  }

  @Override
  public List<HoaDon> timKiemTheoNgayBay(Date ngay) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String ngayCanTim = sdf.format(ngay);

    return danhSach.stream()
        .filter(hd -> hd.getDanhSachVe().stream()
            .anyMatch(ve -> sdf.format(ve.getNgayBay()).equals(ngayCanTim)))
        .collect(Collectors.toList());
  }

  // ========== PHƯƠNG THỨC TÌM KIẾM NÂNG CAO CHO GUI ==========
  public List<HoaDon> timKiemGanDung(String keyword) {
    if (keyword == null || keyword.trim().isEmpty()) {
      return new ArrayList<>(danhSach);
    }

    String keywordLower = keyword.toLowerCase().trim();
    return danhSach.stream()
        .filter(hd -> hd.getMaHoaDon().toLowerCase().contains(keywordLower) ||
            hd.getKhachHang().getHoTen().toLowerCase().contains(keywordLower) ||
            hd.getKhachHang().getCmnd().contains(keyword) ||
            hd.getKhachHang().getEmail().toLowerCase().contains(keywordLower) ||
            hd.getPhuongThucTT().toLowerCase().contains(keywordLower) ||
            hd.getTrangThai().toLowerCase().contains(keywordLower))
        .collect(Collectors.toList());
  }

  public List<HoaDon> timKiemDaTieuChi(Map<String, Object> filters) {
    List<HoaDon> ketQua = new ArrayList<>(danhSach);

    for (Map.Entry<String, Object> entry : filters.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();

      if (value == null || value.toString().isEmpty()) {
        continue;
      }

      switch (key) {
        case "maHoaDon":
          ketQua.removeIf(hd -> !hd.getMaHoaDon().toLowerCase().contains(value.toString().toLowerCase()));
          break;
        case "tenKhachHang":
          ketQua.removeIf(hd -> !hd.getKhachHang().getHoTen().toLowerCase().contains(value.toString().toLowerCase()));
          break;
        case "cmnd":
          ketQua.removeIf(hd -> !hd.getKhachHang().getCmnd().contains(value.toString()));
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
        case "giaMin":
          double giaMin = (double) value;
          ketQua.removeIf(hd -> hd.getThanhTien() < giaMin);
          break;
        case "giaMax":
          double giaMax = (double) value;
          ketQua.removeIf(hd -> hd.getThanhTien() > giaMax);
          break;
      }
    }

    return ketQua;
  }

  @Override
  public void hienThiTatCa() {
    if (danhSach.isEmpty()) {
      System.out.println("📭 Danh sách hóa đơn trống!");
      return;
    }

    System.out.println("====== 📋 DANH SÁCH TẤT CẢ HÓA ĐƠN (" + danhSach.size() + " hóa đơn) ======");
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    for (int i = 0; i < danhSach.size(); i++) {
      HoaDon hd = danhSach.get(i);
      System.out.printf("%d. %s - %s - %s - %s - %,.0f VND - %s\n",
          i + 1, hd.getMaHoaDon(), hd.getKhachHang().getHoTen(),
          sdf.format(hd.getNgayLap()), hd.getPhuongThucTT(),
          hd.getThanhTien(), hd.getTrangThai());
    }
  }

  @Override
  public void hienThiTheoTrangThai(String trangThai) {
    List<HoaDon> ketQua = danhSach.stream()
        .filter(hd -> hd.getTrangThai().equals(trangThai))
        .collect(Collectors.toList());

    if (ketQua.isEmpty()) {
      System.out.println("📭 Không có hóa đơn nào với trạng thái: " + trangThai);
      return;
    }

    System.out.println("====== 📋 DANH SÁCH HÓA ĐƠN " + trangThai + " (" + ketQua.size() + " hóa đơn) ======");
    for (int i = 0; i < ketQua.size(); i++) {
      HoaDon hd = ketQua.get(i);
      System.out.printf("%d. %s - %s - %,.0f VND\n",
          i + 1, hd.getMaHoaDon(), hd.getKhachHang().getHoTen(), hd.getThanhTien());
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

  @Override
  public void sapXepTheoGia() {
    danhSach.sort(Comparator.comparingDouble(HoaDon::getThanhTien));
  }

  @Override
  public void sapXepTheoNgayBay() {
    danhSach.sort(Comparator.comparing(HoaDon::getNgayLap));
  }

  // ========== SẮP XẾP NÂNG CAO CHO GUI ==========
  public void sapXepTheoNgayLap() {
    danhSach.sort(Comparator.comparing(HoaDon::getNgayLap));
  }

  public void sapXepTheoNgayLapGiamDan() {
    danhSach.sort((hd1, hd2) -> hd2.getNgayLap().compareTo(hd1.getNgayLap()));
  }

  public void sapXepTheoTenKhachHang() {
    danhSach.sort(Comparator.comparing(hd -> hd.getKhachHang().getHoTen()));
  }

  public void sapXepTheoThanhTienGiamDan() {
    danhSach.sort((hd1, hd2) -> Double.compare(hd2.getThanhTien(), hd1.getThanhTien()));
  }

  // ========== IMPLEMENT IFILEHANDLER ==========
  @Override
  public boolean docFile(String tenFile) {
    try {
      List<Map<String, String>> dataList = XMLUtils.docFileXML(tenFile);

      if (dataList == null) {
        return false;
      }

      if (dataList.isEmpty()) {
        return true;
      }

      int countSuccess = 0;
      for (Map<String, String> data : dataList) {
        try {
          HoaDon hoaDon = taoHoaDonTuDataXML(data);
          if (hoaDon == null) {
            continue;
          }

          // Kiểm tra trùng mã hóa đơn
          if (tonTai(hoaDon.getMaHoaDon())) {
            continue;
          }

          // Thêm vào danh sách
          danhSach.add(hoaDon);
          countSuccess++;

        } catch (Exception e) {
          System.out.println("❌ Lỗi xử lý hóa đơn: " + data.get("MaHoaDon") + " - " + e.getMessage());
        }
      }
      return countSuccess > 0;

    } catch (Exception e) {
      System.out.println("💥 LỖI NGHIÊM TRỌNG khi đọc file: " + e.getMessage());
      return false;
    }
  }

  private HoaDon taoHoaDonTuDataXML(Map<String, String> data) {
    try {
      // Lấy thông tin cơ bản
      String maHoaDon = data.get("MaHoaDon");
      Date ngayLap = XMLUtils.stringToDate(data.get("NgayLap"));
      double tongTien = XMLUtils.stringToDouble(data.get("TongTien"));
      double thue = XMLUtils.stringToDouble(data.get("Thue"));
      double khuyenMai = XMLUtils.stringToDouble(data.get("KhuyenMai"));
      double thanhTien = XMLUtils.stringToDouble(data.get("ThanhTien"));
      String phuongThucTT = data.get("PhuongThucTT");
      String trangThai = data.get("TrangThai");

      // Tạo khách hàng từ dữ liệu XML
      KhachHang khachHang = taoKhachHangTuData(data);
      if (khachHang == null) {
        System.out.println("❌ Không thể tạo khách hàng cho hóa đơn: " + maHoaDon);
        return null;
      }
      DanhSachVeMayBay dsv = new DanhSachVeMayBay();
      dsv.docFile("src/resources/data/3_VeMayBays.xml");
      List<VeMayBay> DSVe = taoDSVeTuData(data, dsv);

      // Tạo hóa đơn với constructor mới
      HoaDon hoaDon = new HoaDon(maHoaDon, ngayLap, khachHang, tongTien, thue, khuyenMai, phuongThucTT, trangThai,
          DSVe);

      // Đảm bảo thành tiền tính đúng
      hoaDon.setThanhTien(thanhTien);

      return hoaDon;

    } catch (Exception e) {
      System.out.println("❌ Lỗi tạo hóa đơn từ XML data: " + e.getMessage());
      e.printStackTrace();
      return null;
    }
  }

  private KhachHang taoKhachHangTuData(Map<String, String> data) {
    // Trong thực tế, cần lấy khách hàng từ DanhSachKhachHang
    // Ở đây tạo tạm một khách hàng từ dữ liệu XML
    try {
      return new KhachHang(
          data.get("MaKH"),
          data.get("HoTen"),
          data.get("SoDT"),
          data.get("Email"),
          data.get("CMND"),
          XMLUtils.stringToDate(data.get("NgaySinh")),
          data.get("GioiTinh"),
          data.get("DiaChi"),
          data.get("TenDangNhap"),
          data.get("MatKhau"));
    } catch (Exception e) {
      System.out.println("❌ Lỗi tạo khách hàng từ XML: " + e.getMessage());
      return null;
    }
  }

  private List<VeMayBay> taoDSVeTuData(Map<String, String> data, DanhSachVeMayBay danhSachVeMayBay) {
    try {
      List<VeMayBay> danhSachVe = new ArrayList<>();

      // Lấy danh sách mã vé từ XML
      String danhSachMaVe = data.get("DanhSachMaVe");
      if (danhSachMaVe == null || danhSachMaVe.trim().isEmpty()) {
        System.out.println("⚠️ Không có danh sách vé cho hóa đơn: " + data.get("MaHoaDon"));
        return danhSachVe;
      }

      // Tách các mã vé bằng dấu phẩy hoặc khoảng trắng
      String[] maVes = danhSachMaVe.split("[, ]+");

      // Truy xuất vé từ danh sách vé có sẵn
      for (String maVe : maVes) {
        String maVeTrim = maVe.trim();
        if (!maVeTrim.isEmpty()) {
          // Tìm vé trong danh sách vé máy bay
          VeMayBay ve = danhSachVeMayBay.timKiemTheoMa(maVeTrim);
          if (ve != null) {
            danhSachVe.add(ve);
          } else {
            System.out.println("Khong tim thay ve: " + maVeTrim + " trong danh sach ve");
          }
        }
      }
      return danhSachVe;

    } catch (Exception e) {
      System.out.println("❌ Lỗi tạo danh sách vé từ XML: " + e.getMessage());
      return new ArrayList<>();
    }
  }

  @Override
  public boolean ghiFile(String tenFile) {
    try {
      List<Map<String, String>> dataList = new ArrayList<>();

      for (HoaDon hd : danhSach) {
        Map<String, String> data = new HashMap<>();

        // Thông tin cơ bản
        data.put("MaHoaDon", hd.getMaHoaDon());
        data.put("NgayLap", XMLUtils.dateToString(hd.getNgayLap()));
        data.put("TongTien", String.valueOf(hd.getTongTien()));
        data.put("Thue", String.valueOf(hd.getThue()));
        data.put("KhuyenMai", String.valueOf(hd.getKhuyenMai()));
        data.put("ThanhTien", String.valueOf(hd.getThanhTien()));
        data.put("PhuongThucTT", hd.getPhuongThucTT());
        data.put("TrangThai", hd.getTrangThai());

        // Thông tin khách hàng
        KhachHang kh = hd.getKhachHang();
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

        // Thông tin vé (chỉ lưu mã vé)
        List<String> maVes = hd.getDanhSachVe().stream()
            .map(VeMayBay::getMaVe)
            .collect(Collectors.toList());
        data.put("DanhSachMaVe", String.join(",", maVes));

        dataList.add(data);
      }

      boolean result = XMLUtils.ghiFileXML(tenFile, dataList, "HoaDons");
      if (result) {
        System.out.println("✅ Ghi file XML thành công: " + danhSach.size() + " hóa đơn");
      }
      return result;

    } catch (Exception e) {
      System.out.println("❌ Lỗi ghi file XML: " + e.getMessage());
      return false;
    }
  }

  // ========== IMPLEMENT ITHONGKE ==========
  @Override
  public double tinhTongDoanhThu() {
    return danhSach.stream()
        .filter(hd -> hd.getTrangThai().equals(HoaDon.TT_DA_TT))
        .mapToDouble(HoaDon::getThanhTien)
        .sum();
  }

  @Override
  public int demSoLuongTheoLoai(String loai) {
    // Không áp dụng cho hóa đơn
    return 0;
  }

  @Override
  public double tinhDoanhThuTheoLoai(String loai) {
    // Không áp dụng cho hóa đơn
    return 0;
  }

  @Override
  public Map<String, Integer> thongKeTheoThang(int thang, int nam) {
    Map<String, Integer> thongKe = new HashMap<>();
    Calendar cal = Calendar.getInstance();

    for (HoaDon hd : danhSach) {
      cal.setTime(hd.getNgayLap());
      int hdThang = cal.get(Calendar.MONTH) + 1;
      int hdNam = cal.get(Calendar.YEAR);

      if (hdThang == thang && hdNam == nam) {
        String trangThai = hd.getTrangThai();
        thongKe.put(trangThai, thongKe.getOrDefault(trangThai, 0) + 1);
      }
    }

    return thongKe;
  }

  @Override
  public Map<String, Double> thongKeDoanhThuTheoThang(int thang, int nam) {
    Map<String, Double> thongKe = new HashMap<>();
    Calendar cal = Calendar.getInstance();

    for (HoaDon hd : danhSach) {
      if (!hd.getTrangThai().equals(HoaDon.TT_DA_TT))
        continue;

      cal.setTime(hd.getNgayLap());
      int hdThang = cal.get(Calendar.MONTH) + 1;
      int hdNam = cal.get(Calendar.YEAR);

      if (hdThang == thang && hdNam == nam) {
        String phuongThuc = hd.getPhuongThucTT();
        thongKe.put(phuongThuc, thongKe.getOrDefault(phuongThuc, 0.0) + hd.getThanhTien());
      }
    }

    return thongKe;
  }

  @Override
  public Map<String, Integer> thongKeTheoChuyenBay() {
    Map<String, Integer> thongKe = new HashMap<>();
    for (HoaDon hd : danhSach) {
      for (VeMayBay ve : hd.getDanhSachVe()) {
        String chuyenBay = ve.getMaChuyen();
        thongKe.put(chuyenBay, thongKe.getOrDefault(chuyenBay, 0) + 1);
      }
    }
    return thongKe;
  }

  @Override
  public Map<String, Double> thongKeDoanhThuTheoChuyenBay() {
    Map<String, Double> thongKe = new HashMap<>();
    for (HoaDon hd : danhSach) {
      if (hd.getTrangThai().equals(HoaDon.TT_DA_TT)) {
        for (VeMayBay ve : hd.getDanhSachVe()) {
          String chuyenBay = ve.getMaChuyen();
          thongKe.put(chuyenBay, thongKe.getOrDefault(chuyenBay, 0.0) + ve.getGiaVe());
        }
      }
    }
    return thongKe;
  }

  @Override
  public Map<String, Object> thongKeTheoKhoangNgay(Date from, Date to) {
    Map<String, Object> thongKe = new HashMap<>();
    int tongHoaDon = 0;
    double tongDoanhThu = 0;
    Map<String, Integer> theoTrangThai = new HashMap<>();
    Map<String, Double> doanhThuTheoPhuongThuc = new HashMap<>();

    for (HoaDon hd : danhSach) {
      if (hd.getNgayLap().after(from) && hd.getNgayLap().before(to)) {
        tongHoaDon++;
        if (hd.getTrangThai().equals(HoaDon.TT_DA_TT)) {
          tongDoanhThu += hd.getThanhTien();
        }

        // Thống kê theo trạng thái
        String trangThai = hd.getTrangThai();
        theoTrangThai.put(trangThai, theoTrangThai.getOrDefault(trangThai, 0) + 1);

        // Thống kê doanh thu theo phương thức
        if (hd.getTrangThai().equals(HoaDon.TT_DA_TT)) {
          String phuongThuc = hd.getPhuongThucTT();
          doanhThuTheoPhuongThuc.put(phuongThuc,
              doanhThuTheoPhuongThuc.getOrDefault(phuongThuc, 0.0) + hd.getThanhTien());
        }
      }
    }

    thongKe.put("tongHoaDon", tongHoaDon);
    thongKe.put("tongDoanhThu", tongDoanhThu);
    thongKe.put("theoTrangThai", theoTrangThai);
    thongKe.put("doanhThuTheoPhuongThuc", doanhThuTheoPhuongThuc);

    return thongKe;
  }

  @Override
  public Map<String, Integer> thongKeKhachHangThuongXuyen(int soChuyenToiThieu) {
    Map<String, Integer> thongKe = new HashMap<>();
    Map<String, Integer> demKhachHang = new HashMap<>();

    for (HoaDon hd : danhSach) {
      if (hd.getTrangThai().equals(HoaDon.TT_DA_TT)) {
        String maKH = hd.getKhachHang().getMa();
        demKhachHang.put(maKH, demKhachHang.getOrDefault(maKH, 0) + 1);
      }
    }

    // Lọc những khách hàng có số hóa đơn >= số chuyến tối thiểu
    for (Map.Entry<String, Integer> entry : demKhachHang.entrySet()) {
      if (entry.getValue() >= soChuyenToiThieu) {
        thongKe.put(entry.getKey(), entry.getValue());
      }
    }

    return thongKe;
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
      // Thống kê theo phương thức thanh toán
      Map<String, Double> doanhThuTheoPhuongThuc = new HashMap<>();
      for (HoaDon hd : danhSach) {
        if (hd.getTrangThai().equals(HoaDon.TT_DA_TT)) {
          String phuongThuc = hd.getPhuongThucTT();
          doanhThuTheoPhuongThuc.put(phuongThuc,
              doanhThuTheoPhuongThuc.getOrDefault(phuongThuc, 0.0) + hd.getThanhTien());
        }
      }

      for (Map.Entry<String, Double> entry : doanhThuTheoPhuongThuc.entrySet()) {
        tyLe.put(entry.getKey(), (entry.getValue() / tongDoanhThu) * 100);
      }
    }

    return tyLe;
  }

  // ========== PHƯƠNG THỨC NGHIỆP VỤ CHO GUI ==========
  public boolean thanhToanHoaDon(String maHoaDon) {
    HoaDon hd = timKiemTheoMa(maHoaDon);
    if (hd == null) {
      throw new IllegalArgumentException("Không tìm thấy hóa đơn với mã: " + maHoaDon);
    }

    try {
      hd.thanhToan();
      return true;
    } catch (IllegalStateException e) {
      throw new IllegalStateException("Không thể thanh toán hóa đơn: " + e.getMessage());
    }
  }

  public boolean huyHoaDon(String maHoaDon) {
    HoaDon hd = timKiemTheoMa(maHoaDon);
    if (hd == null) {
      throw new IllegalArgumentException("Không tìm thấy hóa đơn với mã: " + maHoaDon);
    }

    try {
      hd.huyHoaDon();
      return true;
    } catch (IllegalStateException e) {
      throw new IllegalStateException("Không thể hủy hóa đơn: " + e.getMessage());
    }
  }

  public boolean kiemTraCoTheHuy(String maHoaDon) {
    HoaDon hd = timKiemTheoMa(maHoaDon);
    return hd != null && hd.coTheHuy();
  }

  public void apDungKhuyenMai(String maHoaDon, double khuyenMai) {
    HoaDon hd = timKiemTheoMa(maHoaDon);
    if (hd == null) {
      throw new IllegalArgumentException("Không tìm thấy hóa đơn với mã: " + maHoaDon);
    }

    try {
      hd.apDungKhuyenMai(khuyenMai);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Không thể áp dụng khuyến mãi: " + e.getMessage());
    }
  }

  public boolean xoaVeKhoiHoaDon(String maHoaDon, VeMayBay ve) {
    HoaDon hd = timKiemTheoMa(maHoaDon);
    if (hd == null) {
      throw new IllegalArgumentException("Không tìm thấy hóa đơn với mã: " + maHoaDon);
    }

    try {
      hd.xoaVe(ve);
      return true;
    } catch (IllegalStateException e) {
      throw new IllegalStateException("Không thể xóa vé khỏi hóa đơn: " + e.getMessage());
    }
  }

  // ========== THỐNG KÊ NÂNG CAO CHO GUI ==========
  @Override
  public Map<String, Object> thongKeTongHop(Date from, Date to) {
    Map<String, Object> tongHop = new HashMap<>();

    // Lấy thống kê cơ bản
    Map<String, Object> thongKeCoBan = thongKeTheoKhoangNgay(from, to);
    tongHop.putAll(thongKeCoBan);

    // Thêm các chỉ số nâng cao
    int tongHoaDon = (int) thongKeCoBan.get("tongHoaDon");
    double tongDoanhThu = (double) thongKeCoBan.get("tongDoanhThu");
    double doanhThuTrungBinh = tongHoaDon > 0 ? tongDoanhThu / tongHoaDon : 0;

    tongHop.put("doanhThuTrungBinh", doanhThuTrungBinh);
    return tongHop;
  }

  @Override
  public List<Map<String, Object>> thongKeTopKhachHang(int limit) {
    Map<String, Double> doanhThuKhachHang = new HashMap<>();

    for (HoaDon hd : danhSach) {
      if (hd.getTrangThai().equals(HoaDon.TT_DA_TT)) {
        String maKH = hd.getKhachHang().getMa();
        doanhThuKhachHang.put(maKH, doanhThuKhachHang.getOrDefault(maKH, 0.0) + hd.getThanhTien());
      }
    }

    return doanhThuKhachHang.entrySet().stream()
        .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
        .limit(limit)
        .map(entry -> {
          Map<String, Object> info = new HashMap<>();
          info.put("maKH", entry.getKey());
          info.put("tongChiTieu", entry.getValue());
          return info;
        })
        .collect(Collectors.toList());
  }

  @Override
  public Map<String, Integer> thongKeTheoGioTrongNgay() {
    Map<String, Integer> thongKe = new HashMap<>();
    Calendar cal = Calendar.getInstance();

    for (HoaDon hd : danhSach) {
      cal.setTime(hd.getNgayLap());
      int gio = cal.get(Calendar.HOUR_OF_DAY);
      String khoangGio = String.format("%02d:00-%02d:00", gio, gio + 1);
      thongKe.put(khoangGio, thongKe.getOrDefault(khoangGio, 0) + 1);
    }

    return thongKe;
  }

  @Override
  public double tinhDoanhThuTrungBinhTheoChuyen() {
    Map<String, Double> doanhThuChuyen = thongKeDoanhThuTheoChuyenBay();
    if (doanhThuChuyen.isEmpty())
      return 0;

    double tongDoanhThu = doanhThuChuyen.values().stream().mapToDouble(Double::doubleValue).sum();
    return tongDoanhThu / doanhThuChuyen.size();
  }

  // ========== PHƯƠNG THỨC TIỆN ÍCH CHO GUI ==========
  public List<String> getDanhSachTrangThai() {
    return Arrays.asList(
        HoaDon.TT_CHUA_TT,
        HoaDon.TT_DA_TT,
        HoaDon.TT_HUY);
  }

  public List<String> getDanhSachPhuongThucTT() {
    return Arrays.asList(
        HoaDon.PT_TIEN_MAT,
        HoaDon.PT_CHUYEN_KHOAN,
        HoaDon.PT_THE,
        HoaDon.PT_VI_DIEN_TU);
  }

  public List<String> getDanhSachMaKhachHang() {
    return danhSach.stream()
        .map(hd -> hd.getKhachHang().getMa())
        .distinct()
        .sorted()
        .collect(Collectors.toList());
  }

  public Map<String, Object> thongKeTongQuan() {
    Map<String, Object> thongKe = new HashMap<>();
    thongKe.put("tongHoaDon", danhSach.size());
    thongKe.put("tongDoanhThu", tinhTongDoanhThu());
    thongKe.put("hoaDonChuaThanhToan", danhSach.stream()
        .filter(hd -> hd.getTrangThai().equals(HoaDon.TT_CHUA_TT)).count());
    thongKe.put("hoaDonDaThanhToan", danhSach.stream()
        .filter(hd -> hd.getTrangThai().equals(HoaDon.TT_DA_TT)).count());
    thongKe.put("hoaDonDaHuy", danhSach.stream()
        .filter(hd -> hd.getTrangThai().equals(HoaDon.TT_HUY)).count());

    return thongKe;
  }


  /**
   * Tìm hóa đơn (chưa thanh toán hoặc chưa hủy) có chứa mã vé cụ thể.
   * @param maVe Mã vé cần tìm.
   * @return HoaDon chứa vé đó, hoặc null.
   */
  public HoaDon timHoaDonChuaVe(String maVe) {
    for (HoaDon hd : danhSach) {
      // Chỉ tìm trong các hóa đơn còn có thể chỉnh sửa
      if (hd.getTrangThai().equals(HoaDon.TT_CHUA_TT)) {
        boolean found = hd.getDanhSachVe().stream().anyMatch(v -> v.getMaVe().equals(maVe));
        if (found) {
          return hd;
        }
      }
    }
    return null;
  }
  // public static void main(String[] args) {
  // DanhSachHoaDon ds = new DanhSachHoaDon();
  // ds.docFile("src/resources/data/4_HoaDons.xml");
  // ds.hienThiTatCa();
  // for(HoaDon hd : ds.danhSach){
  // System.out.println(hd.getDanhSachVe());
  // }
  // }
}
