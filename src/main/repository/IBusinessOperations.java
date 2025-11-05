package repository;

import java.util.Date;
import java.util.List;

public interface IBusinessOperations {
    // Quản lý vé
    boolean datVe(String maChuyen, String maKH, String loaiVe, String soGhe);
    boolean huyVe(String maVe);
    boolean doiVe(String maVeCu, String maChuyenMoi);
    
    // Quản lý chuyến bay
    boolean capNhatTrangThaiChuyenBay(String maChuyen, String trangThai);
    boolean kiemTraGheTrong(String maChuyen, String soGhe);
    List<String> danhSachGheTrong(String maChuyen);
    
    // Quản lý khách hàng
    boolean tangDiemTichLuy(String maKH, int diem);
    boolean giamDiemTichLuy(String maKH, int diem);
    String kiemTraHangKhachHang(String maKH);
    
    // Báo cáo và cảnh báo
    List<String> danhSachChuyenBaySapKhoiHanh(int gioToiThieu);
    List<String> danhSachVeCanThanhToan();
    boolean guiCanhBaoBaoTriMayBay();
}
