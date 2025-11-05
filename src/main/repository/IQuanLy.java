/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package repository;

/**
 *
 * @author HP
 */
// File: IQuanLy.java

import java.util.Date;
import java.util.List;

public interface IQuanLy<T> {
    // CRUD operations
    boolean them(T obj);
    boolean xoa(String ma);
    boolean sua(String ma, T obj);
    T timKiemTheoMa(String ma);
    
    // Advanced search operations
    List<T> timKiemTheoTen(String ten);
    T timKiemTheoCMND(String cmnd);
    List<T> timKiemTheoChuyenBay(String maChuyen);
    List<T> timKiemTheoKhoangGia(double min, double max);
    List<T> timKiemTheoNgayBay(Date ngay);
    
    // Display operations
    void hienThiTatCa();
    void hienThiTheoTrangThai(String trangThai);
    
    // Utility operations
    int demSoLuong();
    boolean tonTai(String ma);
    void sapXepTheoMa();
    void sapXepTheoGia();
    void sapXepTheoNgayBay();
}
