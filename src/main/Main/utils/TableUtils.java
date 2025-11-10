package Main.utils;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import Sevice.DanhSachChuyenBay;
import Sevice.DanhSachHoaDon;
import Sevice.DanhSachKhachHang;
import Sevice.DanhSachVeMayBay;
import Sevice.QuanLyBanVeMayBay;
import model.ChuyenBay;
import model.HoaDon;
import model.KhachHang;
import model.VeMayBay;

public class TableUtils {
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

  // Cập nhật table vé
  public static void capNhatTableVe(JTable tableVe, QuanLyBanVeMayBay quanLy) {
    DefaultTableModel model = (DefaultTableModel) tableVe.getModel();
    model.setRowCount(0);

    if (quanLy == null)
      return;

    DanhSachVeMayBay dsVe = quanLy.getDsVe();
    DanhSachKhachHang dsKH = quanLy.getDsKhachHang();

    if (dsVe != null && dsVe.getDanhSach() != null && dsKH != null) {
      for (VeMayBay ve : dsVe.getDanhSach()) {
        KhachHang kh = dsKH.timKiemTheoMa(ve.getmaKH());
        Object[] row = {
            ve.getMaVe(),
            ve.getmaKH(),
            kh != null ? kh.getHoTen() : "N/A",
            kh != null ? kh.getCmnd() : "N/A",
            ve.getMaChuyen(),
            ve.getSoGhe(),
            ve.getNgayBay() != null ? DATE_FORMAT.format(ve.getNgayBay()) : "N/A",
            ve.loaiVe(),
            String.format("%,.0f VND", ve.getGiaVe()),
            ve.getTrangThai()
        };
        model.addRow(row);
      }
    }
  }

  // Cập nhật table chuyến bay
  public static void capNhatTableChuyenBay(JTable tableChuyenBay, QuanLyBanVeMayBay quanLy) {
    DefaultTableModel model = (DefaultTableModel) tableChuyenBay.getModel();
    model.setRowCount(0);

    if (quanLy == null)
      return;

    DanhSachChuyenBay dsChuyenBay = quanLy.getDsChuyenBay();
    if (dsChuyenBay != null && dsChuyenBay.getDanhSach() != null) {
      for (ChuyenBay cb : dsChuyenBay.getDanhSach()) {
        Object[] row = {
            cb.getMaChuyen(),
            cb.getDiemDi(),
            cb.getDiemDen(),
            cb.getGioKhoiHanh() != null ? DATE_FORMAT.format(cb.getGioKhoiHanh()) : "N/A",
            cb.getSoGheTrong() + "/" + cb.getSoGhe(),
            String.format("%,.0f VND", cb.getGiaCoBan()),
            cb.getTrangThai()
        };
        model.addRow(row);
      }
    }
  }

  // Cập nhật table khách hàng
  public static void capNhatTableKhachHang(JTable tableKhachHang, QuanLyBanVeMayBay quanLy) {
    DefaultTableModel model = (DefaultTableModel) tableKhachHang.getModel();
    model.setRowCount(0);

    if (quanLy == null)
      return;

    DanhSachKhachHang dsKhachHang = quanLy.getDsKhachHang();
    if (dsKhachHang != null && dsKhachHang.getDanhSach() != null) {
      for (KhachHang kh : dsKhachHang.getDanhSach()) {
        Object[] row = {
            kh.getMa(),
            kh.getHoTen(),
            kh.getSoDT(),
            kh.getEmail(),
            kh.getCmnd(),
            kh.getHangKhachHang(),
            String.format("%,d", kh.getDiemTichLuy())
        };
        model.addRow(row);
      }
    }
  }

  // Cập nhật table hóa đơn
  public static void capNhatTableHoaDon(JTable tableHoaDon, QuanLyBanVeMayBay quanLy) {
    DefaultTableModel model = (DefaultTableModel) tableHoaDon.getModel();
    model.setRowCount(0);

    if (quanLy == null)
      return;
    quanLy.docDuLieuTuFile();
    DanhSachHoaDon dsHoaDon = quanLy.getDsHoaDon();
    DanhSachKhachHang dsKH = quanLy.getDsKhachHang();

    if (dsHoaDon != null && dsHoaDon.getDanhSach() != null && dsKH != null) {
      for (HoaDon hd : dsHoaDon.getDanhSach()) {
        KhachHang kh = dsKH.timKiemTheoMa(hd.getKhachHang().getMa());
        Object[] row = {
            hd.getMaHoaDon(),
            hd.getKhachHang().getMa(),
            kh != null ? kh.getHoTen() : "N/A",
            hd.getNgayLap() != null ? DATE_FORMAT.format(hd.getNgayLap()) : "N/A",
            String.format("%,.0f VND", hd.getThanhTien()),
            hd.getTrangThai(),
            hd.getPhuongThucTT()
        };
        model.addRow(row);
      }
    }
  }

  // Hiển thị kết quả tìm kiếm vé
  public static void hienThiKetQuaTimKiemVe(DefaultTableModel model, List<VeMayBay> danhSach,
      QuanLyBanVeMayBay quanLy) {
    model.setRowCount(0);
    if (danhSach == null || quanLy == null)
      return;

    DanhSachKhachHang dsKH = quanLy.getDsKhachHang();
    for (VeMayBay ve : danhSach) {
      KhachHang kh = dsKH != null ? dsKH.timKiemTheoMa(ve.getmaKH()) : null;
      Object[] row = {
          ve.getMaVe(),
          ve.getmaKH(),
          kh != null ? kh.getHoTen() : "N/A",
          kh != null ? kh.getCmnd() : "N/A",
          ve.getMaChuyen(),
          ve.getSoGhe(),
          ve.getNgayBay() != null ? DATE_FORMAT.format(ve.getNgayBay()) : "N/A",
          ve.loaiVe(),
          String.format("%,.0f VND", ve.getGiaVe()),
          ve.getTrangThai()
      };
      model.addRow(row);
    }
  }

  // Hiển thị kết quả tìm kiếm chuyến bay
  public static void hienThiKetQuaTimKiemChuyenBay(DefaultTableModel model, List<ChuyenBay> danhSach) {
    model.setRowCount(0);
    if (danhSach == null)
      return;

    for (ChuyenBay cb : danhSach) {
      Object[] row = {
          cb.getMaChuyen(),
          cb.getDiemDi(),
          cb.getDiemDen(),
          cb.getGioKhoiHanh() != null ? DATE_FORMAT.format(cb.getGioKhoiHanh()) : "N/A",
          cb.getSoGheTrong() + "/" + cb.getSoGhe(),
          String.format("%,.0f VND", cb.getGiaCoBan()),
          cb.getTrangThai()
      };
      model.addRow(row);
    }
  }

  // Tạo model table vé
  public static DefaultTableModel createVeTableModel() {
    String[] columns = { "Mã vé", "Mã KH", "Hành khách", "CMND", "Chuyến bay", "Số ghế", "Giờ khởi hành", "Loại vé",
        "Giá vé", "Trạng thái" };
    return new DefaultTableModel(columns, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
  }

  // Tạo model table chuyến bay
  public static DefaultTableModel createChuyenBayTableModel() {
    String[] columns = { "Mã chuyến", "Điểm đi", "Điểm đến", "Giờ khởi hành", "Ghế trống", "Giá cơ bản", "Trạng thái" };
    return new DefaultTableModel(columns, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
  }

  // Tạo model table khách hàng
  public static DefaultTableModel createKhachHangTableModel() {
    String[] columns = { "Mã KH", "Họ tên", "SĐT", "Email", "CMND", "Hạng", "Điểm tích lũy" };
    return new DefaultTableModel(columns, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
  }

  // Tạo model table hóa đơn
  public static DefaultTableModel createHoaDonTableModel() {
    String[] columns = { "Mã HD", "Mã KH", "Họ tên", "Ngày lập", "Tổng tiền", "Trạng thái", "Phương thức TT" };
    return new DefaultTableModel(columns, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
  }
}
