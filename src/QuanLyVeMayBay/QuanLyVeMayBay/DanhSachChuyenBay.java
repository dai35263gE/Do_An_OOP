/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.mycompany.QuanLyVeMayBay;

/**
 *
 * @author HP
 */
// File: DanhSachChuyenBay.java
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DanhSachChuyenBay implements IQuanLy<ChuyenBay>, IFileHandler {
    private List<ChuyenBay> danhSach;
    private static final int MAX_SIZE = 1000;
    
    public DanhSachChuyenBay() {
        this.danhSach = new ArrayList<>();
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
            if (cb.getDiemDi().toLowerCase().contains(ten.toLowerCase()) ||
                cb.getDiemDen().toLowerCase().contains(ten.toLowerCase())) {
                ketQua.add(cb);
            }
        }
        return ketQua;
    }
    
    public List<ChuyenBay> timKiemTheoTuyen(String diemDi, String diemDen) {
        List<ChuyenBay> ketQua = new ArrayList<>();
        for (ChuyenBay cb : danhSach) {
            if (cb.getDiemDi().equalsIgnoreCase(diemDi) && 
                cb.getDiemDen().equalsIgnoreCase(diemDen)) {
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
    
    // TÌM KIẾM NÂNG CAO
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
            System.out.printf("%d. %s: %s → %s | %s | Ghế: %d/%d | Giá: %.2f%n",
                i + 1, cb.getMaChuyen(), cb.getDiemDi(), cb.getDiemDen(),
                sdf.format(cb.getGioKhoiHanh()), cb.getSoGheTrong(), cb.getSoGhe(),
                cb.getGiaCoBan());
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
            System.out.printf("%d. %s: %s → %s | %s%n",
                i + 1, cb.getMaChuyen(), cb.getDiemDi(), cb.getDiemDen(),
                sdf.format(cb.getGioKhoiHanh()));
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
    
    // ========== IMPLEMENT IFILEHANDLER ==========
    @Override
    public boolean docFile(String tenFile) {
        if (tenFile.endsWith(".xml")) {
            return docFileXML(tenFile);
        } else {
            return docFileText(tenFile);
        }
    }
    
    private boolean docFileXML(String tenFile) {
        List<Map<String, String>> dataList = XMLHandler.docFileXML(tenFile);
        
        for (Map<String, String> data : dataList) {
            try {
                ChuyenBay cb = new ChuyenBay(
                    data.get("MaChuyen"),
                    data.get("DiemDi"),
                    data.get("DiemDen"),
                    XMLHandler.stringToDate(data.get("GioKhoiHanh")),
                    XMLHandler.stringToDate(data.get("GioDen")),
                    XMLHandler.stringToInt(data.get("SoGhe")),
                    data.get("MaMayBay"),
                    XMLHandler.stringToDouble(data.get("GiaCoBan"))
                );
                
                // Cập nhật các thuộc tính bổ sung
                cb.setSoGheTrong(XMLHandler.stringToInt(data.get("SoGheTrong")));
                cb.setTrangThai(data.get("TrangThai"));
                
                danhSach.add(cb);
                
            } catch (Exception e) {
                System.out.println("Lỗi tạo ChuyenBay từ XML: " + e.getMessage());
            }
        }
        
        return !dataList.isEmpty();
    }
    
    @Override
    public boolean ghiFile(String tenFile) {
        if (tenFile.endsWith(".xml")) {
            return ghiFileXML(tenFile);
        } else {
            return ghiFileText(tenFile);
        }
    }
    
    private boolean ghiFileXML(String tenFile) {
        List<Map<String, String>> dataList = new ArrayList<>();
        
        for (ChuyenBay cb : danhSach) {
            Map<String, String> data = new HashMap<>();
            data.put("MaChuyen", cb.getMaChuyen());
            data.put("DiemDi", cb.getDiemDi());
            data.put("DiemDen", cb.getDiemDen());
            data.put("GioKhoiHanh", XMLHandler.dateToString(cb.getGioKhoiHanh()));
            data.put("GioDen", XMLHandler.dateToString(cb.getGioDen()));
            data.put("SoGhe", String.valueOf(cb.getSoGhe()));
            data.put("SoGheTrong", String.valueOf(cb.getSoGheTrong()));
            data.put("MaMayBay", cb.getMaMayBay());
            data.put("GiaCoBan", String.valueOf(cb.getGiaCoBan()));
            data.put("TrangThai", cb.getTrangThai());
            
            dataList.add(data);
        }
        
        // Sử dụng XMLHandler để ghi
        return XMLHandler.ghiFileXML(tenFile, dataList, "ChuyenBays");
    }
    
    @Override
    public boolean xuatExcel(String tenFile) {
        System.out.println("Xuất Excel chuyến bay - Chức năng đang phát triển");
        return false;
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
                long thoiGianTrung = Math.abs(cb.getGioKhoiHanh().getTime() - 
                                            chuyenBayMoi.getGioKhoiHanh().getTime());
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
    
    public void capNhatTrangThaiChuyenBay() {
        Date now = new Date();
        for (ChuyenBay cb : danhSach) {
            if (cb.getGioKhoiHanh().before(now) && 
                cb.getTrangThai().equals(ChuyenBay.TRANG_THAI_CHUA_BAY)) {
                cb.setTrangThai(ChuyenBay.TRANG_THAI_DA_BAY);
            }
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
    
    public List<ChuyenBay> getDanhSach() {
        return new ArrayList<>(danhSach);
    }

    @Override
    public List<ChuyenBay> timKiemTheoCMND(String cmnd) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<ChuyenBay> timKiemTheoChuyenBay(String maChuyen) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<ChuyenBay> timKiemTheoKhoangGia(double min, double max) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private boolean ghiFileText(String tenFile) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private boolean docFileText(String tenFile) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
