/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.mycompany.QuanLyVeMayBay;

/**
 *
 * @author HP
 */
// File: DanhSachHoaDon.java

import java.util.*;

public class DanhSachHoaDon implements IQuanLy<HoaDon>, IFileHandler {
    private ArrayList<HoaDon> danhSach;
    
    public DanhSachHoaDon() {
        this.danhSach = new ArrayList<>();
    }
    
    // Triển khai tương tự như các DanhSach khác
    // Do giới hạn độ dài, chỉ hiển thị phương thức quan trọng
    
    @Override
    public boolean them(HoaDon hoaDon) {
        danhSach.add(hoaDon);
        System.out.println("Thêm hóa đơn thành công!");
        return true;
    }
    
    public HoaDon timKiemTheoMaVe(String maVe) {
        return danhSach.stream()
                      .filter(hd -> hd.getMaVe().equals(maVe))
                      .findFirst()
                      .orElse(null);
    }
    
    public List<HoaDon> timKiemTheoKhachHang(String maKH) {
        return danhSach.stream()
                      .filter(hd -> hd.getMaKH().equals(maKH))
                      .toList();
    }
    
    public List<HoaDon> timKiemTheoNhanVien(String maNV) {
        return danhSach.stream()
                      .filter(hd -> hd.getMaNV().equals(maNV))
                      .toList();
    }
    
    public double tinhTongDoanhThu() {
        return danhSach.stream()
                      .filter(hd -> hd.getTrangThai().equals(HoaDon.TT_DA_TT))
                      .mapToDouble(HoaDon::getThanhTien)
                      .sum();
    }
    
    public Map<String, Double> thongKeDoanhThuTheoThang(int nam) {
        Map<String, Double> thongKe = new HashMap<>();
        Calendar cal = Calendar.getInstance();
        
        for (HoaDon hd : danhSach) {
            if (hd.getTrangThai().equals(HoaDon.TT_DA_TT)) {
                cal.setTime(hd.getNgayLap());
                int thang = cal.get(Calendar.MONTH) + 1;
                int hdNam = cal.get(Calendar.YEAR);
                
                if (hdNam == nam) {
                    String key = "Tháng " + thang;
                    thongKe.put(key, thongKe.getOrDefault(key, 0.0) + hd.getThanhTien());
                }
            }
        }
        
        return thongKe;
    }
    
    // Các phương thức khác triển khai tương tự...
    
    @Override
    public List<HoaDon> timKiemTheoCMND(String cmnd) {
        return new ArrayList<>();
    }
    
    @Override
    public List<HoaDon> timKiemTheoKhoangGia(double min, double max) {
        return danhSach.stream()
                      .filter(hd -> hd.getThanhTien() >= min && hd.getThanhTien() <= max)
                      .toList();
    }
    
    @Override
    public List<HoaDon> timKiemTheoNgayBay(Date ngay) {
        // Không áp dụng
        return new ArrayList<>();
    }

    @Override
    public boolean xoa(String ma) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean sua(String ma, HoaDon obj) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public HoaDon timKiemTheoMa(String ma) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<HoaDon> timKiemTheoTen(String ten) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<HoaDon> timKiemTheoChuyenBay(String maChuyen) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void hienThiTatCa() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void hienThiTheoTrangThai(String trangThai) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int demSoLuong() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean tonTai(String ma) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void sapXepTheoMa() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void sapXepTheoGia() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void sapXepTheoNgayBay() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean docFile(String tenFile) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean ghiFile(String tenFile) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean xuatExcel(String tenFile) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}