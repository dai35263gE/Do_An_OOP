package Sevice;

import model.HoaDon;
import model.KhachHang;
import model.VeMayBay;
import model.VePhoThong;

import java.util.*;
import java.text.SimpleDateFormat;

public class test {
    public static void main(String[] args) {
        System.out.println("🚀 BẮT ĐẦU TEST DANH SÁCH HÓA ĐƠN\n");
        
        // Tạo đối tượng danh sách hóa đơn
        DanhSachHoaDon danhSachHoaDon = new DanhSachHoaDon();
        
        // Test 1: Thêm hóa đơn
        testThemHoaDon(danhSachHoaDon);
        
        // Test 2: Đọc file XML
        testDocFileXML(danhSachHoaDon);
        
        // Test 3: Tìm kiếm hóa đơn
        testTimKiemHoaDon(danhSachHoaDon);
        
        // Test 4: Thống kê
        testThongKe(danhSachHoaDon);
        
        // Test 5: Ghi file XML
        testGhiFileXML(danhSachHoaDon);
        
        // Test 6: Nghiệp vụ hóa đơn
        testNghiepVuHoaDon(danhSachHoaDon);
        
        System.out.println("\n✅ KẾT THÚC TEST DANH SÁCH HÓA ĐƠN");
    }
    
    // TEST 1: Thêm hóa đơn
    private static void testThemHoaDon(DanhSachHoaDon danhSachHoaDon) {
        System.out.println("📋 TEST 1: THÊM HÓA ĐƠN");
        System.out.println("=" .repeat(50));
        
        try {
            // Tạo khách hàng mẫu
            KhachHang kh1 = new KhachHang("KH001", "Nguyễn Văn An", "0912345678", 
                                        "nguyenvana@email.com", "001123456789", 
                                        new Date(), "Nam", "Hà Nội", "nguyenvana", "pass123");
            
            KhachHang kh2 = new KhachHang("KH002", "Trần Thị Bình", "0923456789", 
                                        "tranthib@email.com", "001234567890", 
                                        new Date(), "Nữ", "TP.HCM", "tranthib", "pass456");
            
            // Tạo vé mẫu
            List<VeMayBay> danhSachVe1 = new ArrayList<>();
            VeMayBay ve1 = new VePhoThong();
            ve1.setMaVe("VE001");
            ve1.setGiaVe(1500000);
            ve1.setMaChuyen("CB001");
            danhSachVe1.add(ve1);
            
            List<VeMayBay> danhSachVe2 = new ArrayList<>();
            VeMayBay ve2 = new VePhoThong();
            ve2.setMaVe("VE002");
            ve2.setGiaVe(2000000);
            ve2.setMaChuyen("CB002");
            danhSachVe2.add(ve2);
            
            VeMayBay ve3 = new VePhoThong();
            ve3.setMaVe("VE003");
            ve3.setGiaVe(1800000);
            ve3.setMaChuyen("CB002");
            danhSachVe2.add(ve3);
            
            // Tạo hóa đơn
            HoaDon hd1 = new HoaDon(kh1, danhSachVe1, 100000, HoaDon.PT_CHUYEN_KHOAN);
            HoaDon hd2 = new HoaDon(kh2, danhSachVe2, 150000, HoaDon.PT_THE);
            
            // Thêm hóa đơn vào danh sách
            danhSachHoaDon.them(hd1);
            danhSachHoaDon.them(hd2);
            
            System.out.println("✅ Đã thêm 2 hóa đơn thành công");
            System.out.println("📊 Tổng số hóa đơn: " + danhSachHoaDon.demSoLuong());
            
            // Hiển thị tất cả hóa đơn
            danhSachHoaDon.hienThiTatCa();
            
        } catch (Exception e) {
            System.out.println("❌ Lỗi khi thêm hóa đơn: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }
    
    // TEST 2: Đọc file XML
    private static void testDocFileXML(DanhSachHoaDon danhSachHoaDon) {
        System.out.println("📋 TEST 2: ĐỌC FILE XML");
        System.out.println("=" .repeat(50));
        
        try {
            // Đọc file XML (thay đổi đường dẫn cho phù hợp)
            String filePath = "data/hoadon.xml";
            boolean ketQua = danhSachHoaDon.docFile(filePath);
            
            if (ketQua) {
                System.out.println("✅ Đọc file XML thành công");
                System.out.println("📊 Tổng số hóa đơn sau khi đọc: " + danhSachHoaDon.demSoLuong());
                
                // Hiển thị 5 hóa đơn đầu tiên
                List<HoaDon> hoaDonTrangDau = danhSachHoaDon.phanTrang(1, 5);
                System.out.println("\n📄 5 HÓA ĐƠN ĐẦU TIÊN:");
                for (int i = 0; i < hoaDonTrangDau.size(); i++) {
                    HoaDon hd = hoaDonTrangDau.get(i);
                    System.out.printf("%d. %s - %s - %,.0f VND - %s\n", 
                            i + 1, hd.getMaHoaDon(), hd.getKhachHang().getHoTen(),
                            hd.getThanhTien(), hd.getTrangThai());
                }
            } else {
                System.out.println("❌ Đọc file XML thất bại");
                System.out.println("💡 Tạo file XML mẫu...");
                taoFileXMLMau();
            }
            
        } catch (Exception e) {
            System.out.println("❌ Lỗi khi đọc file XML: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }
    
    // TEST 3: Tìm kiếm hóa đơn
    private static void testTimKiemHoaDon(DanhSachHoaDon danhSachHoaDon) {
        System.out.println("📋 TEST 3: TÌM KIẾM HÓA ĐƠN");
        System.out.println("=" .repeat(50));
        
        try {
            // Tìm kiếm gần đúng
            System.out.println("🔍 Tìm kiếm theo từ khóa 'An':");
            List<HoaDon> ketQuaTimKiem = danhSachHoaDon.timKiemGanDung("An");
            System.out.println("📊 Tìm thấy " + ketQuaTimKiem.size() + " kết quả");
            for (HoaDon hd : ketQuaTimKiem) {
                System.out.printf("   - %s - %s - %,.0f VND\n", 
                        hd.getMaHoaDon(), hd.getKhachHang().getHoTen(), hd.getThanhTien());
            }
            
            // Tìm kiếm theo khoảng giá
            System.out.println("\n🔍 Tìm kiếm theo khoảng giá 1,000,000 - 2,000,000 VND:");
            List<HoaDon> ketQuaGia = danhSachHoaDon.timKiemTheoKhoangGia(1000000, 2000000);
            System.out.println("📊 Tìm thấy " + ketQuaGia.size() + " kết quả");
            
            // Tìm kiếm theo trạng thái
            System.out.println("\n🔍 Tìm kiếm hóa đơn đã thanh toán:");
            danhSachHoaDon.hienThiTheoTrangThai(HoaDon.TT_DA_TT);
            
        } catch (Exception e) {
            System.out.println("❌ Lỗi khi tìm kiếm hóa đơn: " + e.getMessage());
        }
        System.out.println();
    }
    
    // TEST 4: Thống kê
    private static void testThongKe(DanhSachHoaDon danhSachHoaDon) {
        System.out.println("📋 TEST 4: THỐNG KÊ");
        System.out.println("=" .repeat(50));
        
        try {
            // Thống kê tổng quan
            Map<String, Object> thongKeTongQuan = danhSachHoaDon.thongKeTongQuan();
            System.out.println("📊 THỐNG KÊ TỔNG QUAN:");
            System.out.println("   - Tổng số hóa đơn: " + thongKeTongQuan.get("tongHoaDon"));
            System.out.println("   - Tổng doanh thu: " + String.format("%,.0f", thongKeTongQuan.get("tongDoanhThu")) + " VND");
            System.out.println("   - Hóa đơn chưa thanh toán: " + thongKeTongQuan.get("hoaDonChuaThanhToan"));
            System.out.println("   - Hóa đơn đã thanh toán: " + thongKeTongQuan.get("hoaDonDaThanhToan"));
            System.out.println("   - Hóa đơn đã hủy: " + thongKeTongQuan.get("hoaDonDaHuy"));
            
            // Thống kê doanh thu
            double tongDoanhThu = danhSachHoaDon.tinhTongDoanhThu();
            System.out.println("\n💰 TỔNG DOANH THU: " + String.format("%,.0f", tongDoanhThu) + " VND");
            
            // Thống kê theo phương thức thanh toán
            Map<String, Double> tyLeDoanhThu = danhSachHoaDon.thongKeTyLeDoanhThu();
            System.out.println("\n💳 THỐNG KÊ THEO PHƯƠNG THỨC THANH TOÁN:");
            for (Map.Entry<String, Double> entry : tyLeDoanhThu.entrySet()) {
                System.out.printf("   - %s: %.1f%%\n", entry.getKey(), entry.getValue());
            }
            
            // Thống kê top khách hàng
            System.out.println("\n🏆 TOP 3 KHÁCH HÀNG:");
            List<Map<String, Object>> topKhachHang = danhSachHoaDon.thongKeTopKhachHang(3);
            for (Map<String, Object> kh : topKhachHang) {
                System.out.printf("   - %s: %,.0f VND\n", 
                        kh.get("maKH"), kh.get("tongChiTieu"));
            }
            
        } catch (Exception e) {
            System.out.println("❌ Lỗi khi thống kê: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }
    
    // TEST 5: Ghi file XML
    private static void testGhiFileXML(DanhSachHoaDon danhSachHoaDon) {
        System.out.println("📋 TEST 5: GHI FILE XML");
        System.out.println("=" .repeat(50));
        
        try {
            // Ghi file XML
            String filePath = "data/hoadon_output.xml";
            boolean ketQua = danhSachHoaDon.ghiFile(filePath);
            
            if (ketQua) {
                System.out.println("✅ Ghi file XML thành công: " + filePath);
                System.out.println("📊 Đã ghi " + danhSachHoaDon.demSoLuong() + " hóa đơn");
            } else {
                System.out.println("❌ Ghi file XML thất bại");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Lỗi khi ghi file XML: " + e.getMessage());
        }
        System.out.println();
    }
    
    // TEST 6: Nghiệp vụ hóa đơn
    private static void testNghiepVuHoaDon(DanhSachHoaDon danhSachHoaDon) {
        System.out.println("📋 TEST 6: NGHIỆP VỤ HÓA ĐƠN");
        System.out.println("=" .repeat(50));
        
        try {
            // Tìm một hóa đơn chưa thanh toán để test
            HoaDon hdChuaThanhToan = null;
            for (HoaDon hd : danhSachHoaDon.getDanhSach()) {
                if (hd.getTrangThai().equals(HoaDon.TT_CHUA_TT)) {
                    hdChuaThanhToan = hd;
                    break;
                }
            }
            
            if (hdChuaThanhToan != null) {
                System.out.println("🧪 TEST THANH TOÁN HÓA ĐƠN:");
                System.out.println("   Hóa đơn trước khi thanh toán: " + hdChuaThanhToan.getMaHoaDon() + " - " + hdChuaThanhToan.getTrangThai());
                
                // Thanh toán hóa đơn
                danhSachHoaDon.thanhToanHoaDon(hdChuaThanhToan.getMaHoaDon());
                System.out.println("   Hóa đơn sau khi thanh toán: " + hdChuaThanhToan.getMaHoaDon() + " - " + hdChuaThanhToan.getTrangThai());
                
                // Test áp dụng khuyến mãi
                System.out.println("\n🧪 TEST ÁP DỤNG KHUYẾN MÃI:");
                double khuyenMaiCu = hdChuaThanhToan.getKhuyenMai();
                System.out.println("   Khuyến mãi trước: " + String.format("%,.0f", khuyenMaiCu) + " VND");
                
                danhSachHoaDon.apDungKhuyenMai(hdChuaThanhToan.getMaHoaDon(), 200000);
                System.out.println("   Khuyến mãi sau: " + String.format("%,.0f", hdChuaThanhToan.getKhuyenMai()) + " VND");
                System.out.println("   Thành tiền: " + String.format("%,.0f", hdChuaThanhToan.getThanhTien()) + " VND");
                
            } else {
                System.out.println("ℹ️ Không tìm thấy hóa đơn chưa thanh toán để test");
            }
            
            // Test sắp xếp
            System.out.println("\n🧪 TEST SẮP XẾP:");
            System.out.println("   Sắp xếp theo ngày lập (mới nhất):");
            danhSachHoaDon.sapXepTheoNgayLapGiamDan();
            
            List<HoaDon> hoaDonSapXep = danhSachHoaDon.phanTrang(1, 3);
            for (HoaDon hd : hoaDonSapXep) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                System.out.printf("   - %s: %s - %,.0f VND\n", 
                        hd.getMaHoaDon(), sdf.format(hd.getNgayLap()), hd.getThanhTien());
            }
            
        } catch (Exception e) {
            System.out.println("❌ Lỗi khi test nghiệp vụ: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }
    
    // Tạo file XML mẫu nếu file không tồn tại
    private static void taoFileXMLMau() {
        System.out.println("📝 Tạo file XML mẫu với 10 hóa đơn...");
        
        // Code để tạo file XML mẫu
        // Bạn có thể sử dụng code từ câu trả lời trước để tạo file hoadon.xml
        System.out.println("💡 Hãy đảm bảo file 'data/hoadon.xml' tồn tại với dữ liệu mẫu");
    }
    
    // Phương thức tiện ích để hiển thị danh sách hóa đơn
    private static void hienThiDanhSachHoaDon(List<HoaDon> danhSach) {
        if (danhSach.isEmpty()) {
            System.out.println("📭 Danh sách trống");
            return;
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        System.out.println("\n📄 DANH SÁCH HÓA ĐƠN (" + danhSach.size() + " hóa đơn):");
        System.out.println("-".repeat(80));
        System.out.printf("%-10s %-20s %-15s %-12s %-15s\n", 
                "Mã HĐ", "Khách hàng", "Ngày lập", "Thành tiền", "Trạng thái");
        System.out.println("-".repeat(80));
        
        for (HoaDon hd : danhSach) {
            System.out.printf("%-10s %-20s %-15s %-12s %-15s\n",
                    hd.getMaHoaDon(),
                    hd.getKhachHang().getHoTen().substring(0, Math.min(20, hd.getKhachHang().getHoTen().length())),
                    sdf.format(hd.getNgayLap()).substring(0, 10),
                    String.format("%,.0f", hd.getThanhTien()),
                    hd.getTrangThai());
        }
        System.out.println("-".repeat(80));
    }
}