/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.mycompany.QuanLyVeMayBay;

/**
 *
 * @author HP
 */
// File: DanhSachKhachHang.java
import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;

public class DanhSachKhachHang implements IQuanLy<KhachHang>, IFileHandler {

    private List<KhachHang> danhSach;
    private static final int MAX_SIZE = 5000;

    public DanhSachKhachHang() {
        this.danhSach = new ArrayList<>();
    }

    // ========== IMPLEMENT IQUANLY ==========
    @Override
    public boolean them(KhachHang khachHang) {
        if (danhSach.size() >= MAX_SIZE) {
            System.out.println("Danh sách khách hàng đã đầy!");
            return false;
        }

        danhSach.add(khachHang);
        System.out.println("Thêm khách hàng thành công!");
        return true;
    }

    @Override
    public boolean xoa(String maKH) {
        for (Iterator<KhachHang> iterator = danhSach.iterator(); iterator.hasNext();) {
            KhachHang kh = iterator.next();
            if (kh.getMaKH().equals(maKH)) {
                iterator.remove();
                System.out.println("Xóa khách hàng thành công!");
                return true;
            }
        }
        System.out.println("Không tìm thấy khách hàng với mã: " + maKH);
        return false;
    }

    @Override
    public boolean sua(String maKH, KhachHang khachHangMoi) {
        for (int i = 0; i < danhSach.size(); i++) {
            if (danhSach.get(i).getMaKH().equals(maKH)) {
                // Kiểm tra CMND trùng (trừ chính nó)
                if (tonTaiCMND(khachHangMoi.getCmnd(), maKH)) {
                    System.out.println("CMND đã tồn tại!");
                    return false;
                }
                danhSach.set(i, khachHangMoi);
                System.out.println("Cập nhật khách hàng thành công!");
                return true;
            }
        }
        System.out.println("Không tìm thấy khách hàng với mã: " + maKH);
        return false;
    }

    @Override
    public KhachHang timKiemTheoMa(String maKH) {
        for (KhachHang kh : danhSach) {
            if (kh.getMaKH().equals(maKH)) {
                return kh;
            }
        }
        return null;
    }

    @Override
    public List<KhachHang> timKiemTheoTen(String ten) {
        List<KhachHang> ketQua = new ArrayList<>();
        for (KhachHang kh : danhSach) {
            if (kh.getHoTen().toLowerCase().contains(ten.toLowerCase())) {
                ketQua.add(kh);
            }
        }
        return ketQua;
    }

    @Override
    public List<KhachHang> timKiemTheoChuyenBay(String maChuyen) {
        // Phương thức này không áp dụng cho khách hàng
        // Có thể trả về danh sách rỗng hoặc triển khai nếu có liên kết với vé
        return new ArrayList<>();
    }

    public List<KhachHang> timKiemTheoHang(String hang) {
        List<KhachHang> ketQua = new ArrayList<>();
        for (KhachHang kh : danhSach) {
            if (kh.getHangKhachHang().equals(hang)) {
                ketQua.add(kh);
            }
        }
        return ketQua;
    }

    public List<KhachHang> timKiemTheoDiaChi(String diaChi) {
        List<KhachHang> ketQua = new ArrayList<>();
        for (KhachHang kh : danhSach) {
            if (kh.getDiaChi().toLowerCase().contains(diaChi.toLowerCase())) {
                ketQua.add(kh);
            }
        }
        return ketQua;
    }

    @Override
    public List<KhachHang> timKiemTheoKhoangGia(double min, double max) {
        // Không áp dụng cho khách hàng
        return new ArrayList<>();
    }

    @Override
    public List<KhachHang> timKiemTheoNgayBay(Date ngay) {
        // Không áp dụng cho khách hàng
        return new ArrayList<>();
    }

    // TÌM KIẾM ĐA TIÊU CHÍ
    public List<KhachHang> timKiemKhachHang(Map<String, Object> filters) {
        List<KhachHang> ketQua = new ArrayList<>(danhSach);

        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            switch (key) {
                case "hoTen":
                    ketQua.removeIf(kh -> !kh.getHoTen().toLowerCase().contains(value.toString().toLowerCase()));
                    break;
                case "cmnd":
                    ketQua.removeIf(kh -> !kh.getCmnd().contains(value.toString()));
                    break;
                case "hang":
                    ketQua.removeIf(kh -> !kh.getHangKhachHang().equals(value));
                    break;
                case "gioiTinh":
                    ketQua.removeIf(kh -> !kh.getGioiTinh().equals(value));
                    break;
                case "diaChi":
                    ketQua.removeIf(kh -> !kh.getDiaChi().toLowerCase().contains(value.toString().toLowerCase()));
                    break;
                case "tuNgayDangKy":
                    Date tuNgay = (Date) value;
                    ketQua.removeIf(kh -> kh.getNgayDangKy().before(tuNgay));
                    break;
                case "denNgayDangKy":
                    Date denNgay = (Date) value;
                    ketQua.removeIf(kh -> kh.getNgayDangKy().after(denNgay));
                    break;
            }
        }

        return ketQua;
    }

    @Override
    public void hienThiTatCa() {
        if (danhSach.isEmpty()) {
            System.out.println("Danh sách khách hàng trống!");
            return;
        }

        System.out.println("===== DANH SÁCH TẤT CẢ KHÁCH HÀNG =====");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (int i = 0; i < danhSach.size(); i++) {
            KhachHang kh = danhSach.get(i);
            System.out.printf("%d. %s - %s - %s - %s - Điểm: %d - Hạng: %s%n",
                    i + 1, kh.getMaKH(), kh.getHoTen(), kh.getCmnd(),
                    kh.getSoDT(), kh.getDiemTichLuy(), kh.getHangKhachHang());
        }
    }

    @Override
    public void hienThiTheoTrangThai(String trangThai) {
        // Không áp dụng trạng thái cho khách hàng
        hienThiTatCa();
    }

    @Override
    public int demSoLuong() {
        return danhSach.size();
    }

    @Override
    public boolean tonTai(String ma) {
        return timKiemTheoMa(ma) != null;
    }

    private boolean tonTaiCMND(String cmnd, String maLoaiTru) {
        for (KhachHang kh : danhSach) {
            if (!kh.getMaKH().equals(maLoaiTru) && kh.getCmnd().equals(cmnd)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void sapXepTheoMa() {
        Collections.sort(danhSach, new Comparator<KhachHang>() {
            @Override
            public int compare(KhachHang kh1, KhachHang kh2) {
                return kh1.getMaKH().compareTo(kh2.getMaKH());
            }
        });
    }

    public void sapXepTheoHoTen() {
        Collections.sort(danhSach, new Comparator<KhachHang>() {
            @Override
            public int compare(KhachHang kh1, KhachHang kh2) {
                return kh1.getHoTen().compareTo(kh2.getHoTen());
            }
        });
    }

    public void sapXepTheoDiem() {
        Collections.sort(danhSach, new Comparator<KhachHang>() {
            @Override
            public int compare(KhachHang kh1, KhachHang kh2) {
                return Integer.compare(kh2.getDiemTichLuy(), kh1.getDiemTichLuy()); // Giảm dần
            }
        });
    }

    @Override
    public void sapXepTheoGia() {
        // Không áp dụng
    }

    @Override
    public void sapXepTheoNgayBay() {
        // Không áp dụng
    }

    // ========== IMPLEMENT IFILEHANDLER ==========
    @Override
    public boolean docFile(String tenFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(tenFile))) {
            String line;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 8) {
                    try {
                        Date ngaySinh = sdf.parse(data[5]);

                        KhachHang kh = new KhachHang(
                                data[0], data[1], data[2], data[3], data[4],
                                ngaySinh, data[6], data[7]
                        );

                        if (data.length > 8) {
                            kh.setHangKhachHang(data[8]);
                        }
                        if (data.length > 9) {
                            try {
                                kh.setDiemTichLuy(Integer.parseInt(data[9]));
                            } catch (NumberFormatException e) {
                                kh.setDiemTichLuy(0);
                            }
                        }

                        danhSach.add(kh);
                    } catch (Exception e) {
                        System.out.println("Lỗi xử lý dòng: " + line + " - " + e.getMessage());
                    }
                }
            }
            System.out.println("Đọc file khách hàng thành công! Tổng: " + danhSach.size() + " khách hàng");
            return true;
        } catch (Exception e) {
            System.out.println("Lỗi đọc file khách hàng: " + tenFile + " - " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean ghiFile(String tenFile) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(tenFile))) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            for (KhachHang kh : danhSach) {
                pw.println(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%d",
                        kh.getMaKH(), kh.getHoTen(), kh.getSoDT(), kh.getEmail(),
                        kh.getCmnd(), sdf.format(kh.getNgaySinh()), kh.getGioiTinh(),
                        kh.getDiaChi(), kh.getHangKhachHang(), kh.getDiemTichLuy()));
            }
            System.out.println("Ghi file khách hàng thành công! Tổng: " + danhSach.size() + " khách hàng");
            return true;
        } catch (IOException e) {
            System.out.println("Lỗi ghi file khách hàng: " + tenFile + " - " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean xuatExcel(String tenFile) {
        System.out.println("Xuất Excel khách hàng - Chức năng đang phát triển");
        return false;
    }


    // ========== PHƯƠNG THỨC NGHIỆP VỤ ==========
    public void tangDiemKhachHang(String maKH, int diem) {
        KhachHang kh = timKiemTheoMa(maKH);
        if (kh != null) {
            kh.tangDiemTichLuy(diem);
            System.out.printf("Đã thêm %d điểm cho KH %s. Tổng điểm: %d%n",
                    diem, kh.getHoTen(), kh.getDiemTichLuy());
        } else {
            System.out.println("Không tìm thấy khách hàng với mã: " + maKH);
        }
    }

    public void capNhatHangKhachHang() {
        for (KhachHang kh : danhSach) {
            kh.tangDiemTichLuy(0); // Chỉ để cập nhật hạng
        }
        System.out.println("Đã cập nhật hạng cho tất cả khách hàng");
    }

    public Map<String, Integer> thongKeTheoHang() {
        Map<String, Integer> thongKe = new HashMap<>();
        for (KhachHang kh : danhSach) {
            String hang = kh.getHangKhachHang();
            thongKe.put(hang, thongKe.getOrDefault(hang, 0) + 1);
        }
        return thongKe;
    }

    public Map<String, Integer> thongKeTheoGioiTinh() {
        Map<String, Integer> thongKe = new HashMap<>();
        for (KhachHang kh : danhSach) {
            String gioiTinh = kh.getGioiTinh();
            thongKe.put(gioiTinh, thongKe.getOrDefault(gioiTinh, 0) + 1);
        }
        return thongKe;
    }

    public List<KhachHang> getKhachHangVip() {
        List<KhachHang> vipList = new ArrayList<>();
        for (KhachHang kh : danhSach) {
            if (kh.getHangKhachHang().equals(KhachHang.HANG_GOLD)
                    || kh.getHangKhachHang().equals(KhachHang.HANG_PLATINUM)) {
                vipList.add(kh);
            }
        }
        return vipList;
    }

    public List<KhachHang> getDanhSach() {
        return new ArrayList<>(danhSach);
    }

    // Phương thức tiện ích
    public void xoaTatCa() {
        danhSach.clear();
        System.out.println("Đã xóa tất cả khách hàng!");
    }

    public void hienThiThongKe() {
        System.out.println("===== THỐNG KÊ KHÁCH HÀNG =====");
        System.out.println("Tổng số khách hàng: " + demSoLuong());

        Map<String, Integer> thongKeHang = thongKeTheoHang();
        System.out.println("Phân bố hạng:");
        for (Map.Entry<String, Integer> entry : thongKeHang.entrySet()) {
            System.out.printf("  - %s: %d khách hàng%n", entry.getKey(), entry.getValue());
        }

        Map<String, Integer> thongKeGioiTinh = thongKeTheoGioiTinh();
        System.out.println("Phân bố giới tính:");
        for (Map.Entry<String, Integer> entry : thongKeGioiTinh.entrySet()) {
            System.out.printf("  - %s: %d khách hàng%n", entry.getKey(), entry.getValue());
        }

        List<KhachHang> vipList = getKhachHangVip();
        System.out.println("Khách hàng VIP: " + vipList.size());
    }

    @Override
    public List<KhachHang> timKiemTheoCMND(String cmnd) {
        List<KhachHang> ketQua = new ArrayList<>();
        for (KhachHang kh : danhSach) {
            if (kh.getCmnd().equals(cmnd)) {
                ketQua.add(kh);
            }
        }
        return ketQua;
    }

    
}
