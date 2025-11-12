package Sevice;

import java.util.ArrayList; // Thêm import
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.ChuyenBay;
import model.HoaDon;
import model.KhachHang;
import model.NhanVien; // Thêm import
import model.VeMayBay;

public class QuanLyBanVeMayBay {
  private DanhSachVeMayBay dsVe;
  private DanhSachChuyenBay dsChuyenBay;
  private DanhSachKhachHang dsKhachHang;
  private DanhSachHoaDon dsHoaDon;
  private List<NhanVien> dsNhanVien; // THÊM MỚI: Danh sách nhân viên

  // Static properties for tracking
  private static int soLanTruyCap = 0;
  private static final String PHIEN_BAN = "1.0.0";

  public QuanLyBanVeMayBay() {
    this.dsVe = new DanhSachVeMayBay();
    this.dsChuyenBay = new DanhSachChuyenBay();
    this.dsKhachHang = new DanhSachKhachHang();
    this.dsHoaDon = new DanhSachHoaDon();
    this.dsNhanVien = new ArrayList<>(); // THÊM MỚI: Khởi tạo danh sách
    soLanTruyCap++;

    // THÊM MỚI: Tạo tài khoản Admin mẫu
    // (Trong dự án thực tế, bạn sẽ dùng DanhSachNhanVien.docFile("5_NhanVien.xml"))
    taoNhanVienMau();
  }

  // THÊM MỚI: Hàm tạo tài khoản mẫu
  private void taoNhanVienMau() {
    try {
      NhanVien admin = new NhanVien("admin", "Long (Admin)", "0987654321", "admin@airline.com",
              "000000000001", new java.util.Date(), "Nam", "Hanoi",
              "admin", "123", NhanVien.ROLE_ADMIN);
      this.dsNhanVien.add(admin);
    } catch (Exception e) {
      System.err.println("Lỗi tạo nhân viên mẫu: " + e.getMessage());
    }
  }

  // THÊM MỚI: Hàm kiểm tra đăng nhập Admin
  public NhanVien dangNhapAdmin(String tenDangNhap, String matKhau) {
    for (NhanVien nv : dsNhanVien) {
      // Kiểm tra tên đăng nhập và mật khẩu
      if (nv.getTenDangNhap().equals(tenDangNhap) && nv.getMatKhau().equals(matKhau)) {
        return nv; // Trả về nhân viên nếu đăng nhập thành công
      }
    }
    return null; // Trả về null nếu thất bại
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
    double tiLeLapDay = tongGhe > 0 ? (1 - (double) gheTrong / tongGhe) * 100 : 0;
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

  public boolean suaChuyenBay(String maChuyen, ChuyenBay chuyenBayMoi) {
    return dsChuyenBay.sua(maChuyen, chuyenBayMoi);
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
      // 1. Đọc tất cả dữ liệu thô từ file XML
      dsChuyenBay.docFile("src/resources/data/1_ChuyenBays.xml");
      dsKhachHang.docFile("src/resources/data/2_KhachHangs.xml");
      dsVe.docFile("src/resources/data/3_VeMayBays.xml");
      dsHoaDon.docFile("src/resources/data/4_HoaDons.xml");
      // (Trong tương lai bạn sẽ thêm: dsNhanVien.docFile("src/resources/data/5_NhanVien.xml");)

      // === BẮT ĐẦU CODE LIÊN KẾT DỮ LIỆU ===

      // 2. Liên kết Vé vào ChuyenBay (Sửa lỗi Chọn Ghế)
      for (VeMayBay ve : dsVe.getDanhSach()) {
        ChuyenBay cb = dsChuyenBay.timKiemTheoMa(ve.getMaChuyen());
        if (cb != null) {
          // SỬA LỖI: Gọi hàm ganVeKhiTai() mới mà chúng ta vừa tạo
          cb.ganVeKhiTai(ve);
        }
      }

      // 3. Liên kết Hóa đơn và Vé vào KhachHang (Sửa lỗi Lịch sử mua vé)
      for (KhachHang kh : dsKhachHang.getDanhSach()) {
        kh.getLichSuHoaDon().clear();
        kh.getVeDaDat().clear();
      }

      for (HoaDon hd : dsHoaDon.getDanhSach()) {
        String maKH = hd.getKhachHang().getMa();
        KhachHang kh = dsKhachHang.timKiemTheoMa(maKH);

        if (kh != null) {
          kh.themHoaDon(hd);
        }

        // Thay thế các vé tạm trong Hóa đơn bằng vé thật từ dsVe
        List<VeMayBay> veThucTe = new ArrayList<>();
        for (VeMayBay veTrongHD : hd.getDanhSachVe()) {
          VeMayBay ve = dsVe.timKiemTheoMa(veTrongHD.getMaVe());
          if (ve != null) {
            veThucTe.add(ve);
          }
        }
        hd.getDanhSachVe().clear();
        hd.getDanhSachVe().addAll(veThucTe);
      }

      System.out.println(">>> Đã liên kết dữ liệu ChuyenBay, KhachHang và HoaDon thành công.");
      // === KẾT THÚC CODE LIÊN KẾT DỮ LIỆU ===

    } catch (Exception e) {
      System.out.println("Lỗi nghiêm trọng khi đọc và liên kết dữ liệu từ file: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public void ghiDuLieuRaFile() {
    try {
      dsVe.ghiFile("src/resources/data/3_VeMayBays.xml");
      dsChuyenBay.ghiFile("src/resources/data/1_ChuyenBays.xml");
      dsKhachHang.ghiFile("src/resources/data/2_KhachHangs.xml");
      dsHoaDon.ghiFile("src/resources/data/4_HoaDons.xml");
      // (Trong tương lai bạn sẽ thêm: dsNhanVien.ghiFile("src/resources/data/5_NhanVien.xml");)
      System.out.println("Đã ghi dữ liệu ra file!");
    } catch (Exception e) {
      System.out.println("Lỗi khi ghi dữ liệu ra file: " + e.getMessage());
    }
  }

  // Getter methods for GUI
  public DanhSachVeMayBay getDsVe() {
    return dsVe;
  }

  public DanhSachChuyenBay getDsChuyenBay() {
    return dsChuyenBay;
  }

  public DanhSachKhachHang getDsKhachHang() {
    return dsKhachHang;
  }

  public DanhSachHoaDon getDsHoaDon() {
    return dsHoaDon;
  }

  // THÊM MỚI: Getter cho DS Nhân Viên
  public List<NhanVien> getDsNhanVien() {
    return dsNhanVien;
  }


  public static void main(String[] args) {
    QuanLyBanVeMayBay quanly = new QuanLyBanVeMayBay();
    quanly.docDuLieuTuFile();

    for (String key : quanly.thongKeTongQuan().keySet()) {
      String value = quanly.thongKeDoanhThu().get(key).toString();
      System.out.println(key + " : " + value);
    }
  }
}