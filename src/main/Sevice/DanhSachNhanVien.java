package Sevice;

import model.NhanVien;
import repository.IFileHandler;
import repository.IQuanLy;

import java.util.*;

public class DanhSachNhanVien implements IFileHandler {
    private List<NhanVien> danhSach;
    
    public DanhSachNhanVien() {
        this.danhSach = new ArrayList<>();
    }
    
    // Implement IQuanLy methods tương tự như DanhSachVeMayBay
    // Implement IFileHandler methods để đọc/ghi XML nhân viên
    
    // Các phương thức đặc thù cho nhân viên
    public List<NhanVien> timKiemTheoChucVu(String chucVu) {
        return danhSach.stream()
                      .filter(nv -> nv.getChucVu().equals(chucVu))
                      .toList();
    }

    @Override
    public boolean docFile(String tenFile) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'docFile'");
    }

    @Override
    public boolean ghiFile(String tenFile) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'ghiFile'");
    }
}