package repository;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class XMLUtils {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd");

    // ========== ĐỌC FILE XML ==========
    public static List<Map<String, String>> docFileXML(String tenFile) {
        List<Map<String, String>> danhSach = new ArrayList<>();
        try {
            File file = new File(tenFile);
            if (!file.exists()) {
                System.out.println("File không tồn tại: " + tenFile);
                return danhSach;
            }

            String fixedXmlContent = fixDuplicateXmlDeclaration(file);
            
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            
            Document doc = builder.parse(new ByteArrayInputStream(fixedXmlContent.getBytes()));
            doc.getDocumentElement().normalize();

            String rootElementName = doc.getDocumentElement().getNodeName();
            
            switch (rootElementName) {
                case "ChuyenBays":
                    return docChuyenBays(doc);
                case "KhachHangs":
                    return docKhachHangs(doc);
                case "VeMayBays":
                    return docVeMayBays(doc);
                case "HoaDons":
                    return docHoaDons(doc);
                default:
                    System.out.println("Không hỗ trợ định dạng XML: " + rootElementName);
            }

        } catch (Exception e) {
            System.out.println("Lỗi đọc file XML: " + tenFile);
            e.printStackTrace();
        }

        return danhSach;
    }

    // ========== ĐỌC CHUYẾN BAY ==========
    private static List<Map<String, String>> docChuyenBays(Document doc) {
        List<Map<String, String>> danhSach = new ArrayList<>();
        NodeList nodeList = doc.getElementsByTagName("ChuyenBay");
        
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                Map<String, String> data = new HashMap<>();

                String[] tags = {
                    "MaChuyen", "DiemDi", "DiemDen", "GioKhoiHanh", 
                    "GioDen", "SoGhe", "MaMayBay", "GiaCoBan", "TrangThai"
                };
                
                for (String tag : tags) {
                    data.put(tag, getElementValue(element, tag));
                }
                
                danhSach.add(data);
            }
        }
        System.out.println("Đọc " + danhSach.size() + " chuyến bay từ XML");
        return danhSach;
    }

    // ========== ĐỌC KHÁCH HÀNG ==========
    private static List<Map<String, String>> docKhachHangs(Document doc) {
        List<Map<String, String>> danhSach = new ArrayList<>();
        NodeList nodeList = doc.getElementsByTagName("KhachHang");
        
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                Map<String, String> data = new HashMap<>();

                String[] tags = {
                    "MaKH", "HoTen", "SoDT", "Email", "CMND", 
                    "NgaySinh", "GioiTinh", "DiaChi", "TenDangNhap", "MatKhau",
                    "HangKhachHang", "DiemTichLuy", "NgayDangKy"
                };
                
                for (String tag : tags) {
                    data.put(tag, getElementValue(element, tag));
                }
                
                danhSach.add(data);
            }
        }
        System.out.println("Đọc " + danhSach.size() + " khách hàng từ XML");
        return danhSach;
    }

    // ========== ĐỌC VÉ MÁY BAY ==========
    private static List<Map<String, String>> docVeMayBays(Document doc) {
        List<Map<String, String>> danhSach = new ArrayList<>();
        
        // Đọc Vé Thương Gia
        NodeList veThuongGiaList = doc.getElementsByTagName("VeThuongGia");
        for (int i = 0; i < veThuongGiaList.getLength(); i++) {
            danhSach.add(docVeMayBay(veThuongGiaList.item(i), "VeThuongGia"));
        }
        
        // Đọc Vé Phổ Thông
        NodeList vePhoThongList = doc.getElementsByTagName("VePhoThong");
        for (int i = 0; i < vePhoThongList.getLength(); i++) {
            danhSach.add(docVeMayBay(vePhoThongList.item(i), "VePhoThong"));
        }
        
        // Đọc Vé Tiết Kiệm
        NodeList veTietKiemList = doc.getElementsByTagName("VeTietKiem");
        for (int i = 0; i < veTietKiemList.getLength(); i++) {
            danhSach.add(docVeMayBay(veTietKiemList.item(i), "VeTietKiem"));
        }
        
        System.out.println("Đọc " + danhSach.size() + " vé máy bay từ XML");
        return danhSach;
    }

    private static Map<String, String> docVeMayBay(Node node, String loaiVe) {
        Map<String, String> data = new HashMap<>();
        data.put("LoaiVe", loaiVe);
        
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            
            // Thông tin chung của tất cả vé
            String[] commonTags = {
                "MaVe","MaKhachHang", "NgayBay", "GiaVe", "MaChuyen", "SoGhe", 
                "TrangThai", "NgayDat"
            };
            
            for (String tag : commonTags) {
                data.put(tag, getElementValue(element, tag));
            }
            
            // Thông tin riêng cho từng loại vé
            switch (loaiVe) {
                case "VeThuongGia":
                    String[] thuongGiaTags = {
                        "DichVuDacBiet", "PhuThu", "SoKgHanhLyMienPhi", 
                        "PhongChoVIP", "LoaiDoUong"
                    };
                    for (String tag : thuongGiaTags) {
                        data.put(tag, getElementValue(element, tag));
                    }
                    break;
                    
                case "VePhoThong":
                    String[] phoThongTags = {
                        "HanhLyXachTay", "SoKgHanhLyKyGui", 
                        "PhiHanhLy", "LoaiGhe", "DoAn"
                    };
                    for (String tag : phoThongTags) {
                        data.put(tag, getElementValue(element, tag));
                    }
                    break;
                    
                case "VeTietKiem":
                    String[] tietKiemTags = {
                        "SoGioDatTruoc", "TyLeGiam", "HoanDoi", 
                        "PhiHoanDoi", "DieuKienGia"
                    };
                    for (String tag : tietKiemTags) {
                        data.put(tag, getElementValue(element, tag));
                    }
                    break;
            }
        }
        
        return data;
    }

    // ========== ĐỌC HÓA ĐƠN ==========
   private static List<Map<String, String>> docHoaDons(Document doc) {
    List<Map<String, String>> danhSach = new ArrayList<>();
    NodeList nodeList = doc.getElementsByTagName("HoaDon");
    
    for (int i = 0; i < nodeList.getLength(); i++) {
        Node node = nodeList.item(i);
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            Map<String, String> data = new HashMap<>();

            // Thông tin cơ bản hóa đơn
            String[] hoaDonTags = {
                "MaHoaDon", "NgayLap", "TongTien", "Thue", "KhuyenMai", 
                "ThanhTien", "PhuongThucTT", "TrangThai"
            };
            
            for (String tag : hoaDonTags) {
                data.put(tag, getElementValue(element, tag));
            }
            
            // Thông tin khách hàng
            String[] khachHangTags = {
                "MaKH", "HoTen", "SoDT", "Email", "CMND", 
                "NgaySinh", "GioiTinh", "DiaChi", "TenDangNhap", "MatKhau"
            };
            
            for (String tag : khachHangTags) {
                data.put(tag, getElementValue(element, tag));
            }
            
            // Thông tin vé (chỉ lưu mã vé)
            data.put("DanhSachMaVe", getElementValue(element, "DanhSachMaVe"));
            
            danhSach.add(data);
        }
    }
    System.out.println("Đọc " + danhSach.size() + " hóa đơn từ XML");
    return danhSach;
}

    // ========== GHI FILE XML ==========
    public static boolean ghiFileXML(String tenFile, List<Map<String, String>> dataList, String rootElementName) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element rootElement = doc.createElement(rootElementName);
            doc.appendChild(rootElement);

            String childElementName = getChildElementName(rootElementName);
            
            for (Map<String, String> data : dataList) {
                Element itemElement = doc.createElement(childElementName);
                
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    if (!shouldSkipField(entry.getKey(), rootElementName)) {
                        Element fieldElement = doc.createElement(entry.getKey());
                        fieldElement.appendChild(doc.createTextNode(entry.getValue() != null ? entry.getValue() : ""));
                        itemElement.appendChild(fieldElement);
                    }
                }
                
                rootElement.appendChild(itemElement);
            }

            // Ghi file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(tenFile));
            transformer.transform(source, result);

            System.out.println("Ghi file XML thành công: " + tenFile);
            return true;

        } catch (Exception e) {
            System.out.println("Lỗi ghi file XML: " + tenFile);
            e.printStackTrace();
            return false;
        }
    }

    // ========== GHI THEO ĐỐI TƯỢNG CỤ THỂ ==========
    
    // Ghi danh sách chuyến bay
    public static boolean ghiChuyenBays(String tenFile, List<Map<String, String>> chuyenBays) {
        return ghiFileXML(tenFile, chuyenBays, "ChuyenBays");
    }

    // Ghi danh sách khách hàng
    public static boolean ghiKhachHangs(String tenFile, List<Map<String, String>> khachHangs) {
        return ghiFileXML(tenFile, khachHangs, "KhachHangs");
    }

    // Ghi danh sách vé máy bay
    public static boolean ghiVeMayBays(String tenFile, List<Map<String, String>> veMayBays) {
        return ghiFileXML(tenFile, veMayBays, "VeMayBays");
    }

    // Ghi danh sách hóa đơn
    public static boolean ghiHoaDons(String tenFile, List<Map<String, String>> hoaDons) {
        return ghiFileXML(tenFile, hoaDons, "HoaDons");
    }

    // ========== PHƯƠNG THỨC HỖ TRỢ ==========
    
    private static String getChildElementName(String rootElementName) {
        switch (rootElementName) {
            case "ChuyenBays": return "ChuyenBay";
            case "KhachHangs": return "KhachHang";
            case "VeMayBays": return "VeMayBay";
            case "HoaDons": return "HoaDon";
            default: return "Item";
        }
    }

    private static boolean shouldSkipField(String fieldName, String rootElementName) {
        // Không ghi trường LoaiVe vào XML (chỉ dùng để phân biệt khi đọc)
        if ("LoaiVe".equals(fieldName)) {
            return true;
        }
        
        // Đối với vé máy bay, chỉ ghi các trường phù hợp với loại vé
        if ("VeMayBays".equals(rootElementName)) {
            // Xác định loại vé dựa trên dữ liệu (cần xử lý đặc biệt)
            // Xử lý này sẽ được thực hiện ở lớp gọi
        }
        
        return false;
    }

    private static String getElementValue(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            return node.getTextContent().trim();
        }
        return "";
    }

    private static String fixDuplicateXmlDeclaration(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder content = new StringBuilder();
        String line;
        boolean firstDeclaration = true;
        
        while ((line = reader.readLine()) != null) {
            if (line.trim().startsWith("<?xml")) {
                if (firstDeclaration) {
                    content.append(line).append("\n");
                    firstDeclaration = false;
                }
            } else {
                content.append(line).append("\n");
            }
        }
        reader.close();
        
        return content.toString();
    }

    // ========== PHƯƠNG THỨC CHUYỂN ĐỔI ==========
    public static Date stringToDate(String dateString) {
        try {
            if (dateString == null || dateString.trim().isEmpty()) {
                return null;
            }
            if (dateString.contains(" ")) {
                return dateFormat.parse(dateString);
            } else {
                return dateOnlyFormat.parse(dateString);
            }
        } catch (Exception e) {
            System.out.println("Lỗi chuyển đổi ngày: " + dateString);
            return null;
        }
    }

    public static String dateToString(Date date) {
        if (date == null) return "";
        return dateFormat.format(date);
    }

    public static String dateToDateOnlyString(Date date) {
        if (date == null) return "";
        return dateOnlyFormat.format(date);
    }

    public static double stringToDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return 0.0;
        }
    }

    public static int stringToInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean stringToBoolean(String value) {
        return "true".equalsIgnoreCase(value) || "1".equals(value);
    }

    // ========== PHƯƠNG THỨC TẠO DỮ LIỆU MẪU ==========
    public static Map<String, String> taoChuyenBayData(String maChuyen, String diemDi, String diemDen, 
                                                     Date gioKhoiHanh, Date gioDen, int soGhe, 
                                                     String maMayBay, double giaCoBan, String trangThai) {
        Map<String, String> data = new HashMap<>();
        data.put("MaChuyen", maChuyen);
        data.put("DiemDi", diemDi);
        data.put("DiemDen", diemDen);
        data.put("GioKhoiHanh", dateToString(gioKhoiHanh));
        data.put("GioDen", dateToString(gioDen));
        data.put("SoGhe", String.valueOf(soGhe));
        data.put("MaMayBay", maMayBay);
        data.put("GiaCoBan", String.valueOf(giaCoBan));
        data.put("TrangThai", trangThai);
        return data;
    }

    public static Map<String, String> taoKhachHangData(String maKH, String hoTen, String soDT, String email,
                                                     String cmnd, Date ngaySinh, String gioiTinh, String diaChi,
                                                     String tenDangNhap, String matKhau, String hangKhachHang,
                                                     int diemTichLuy, Date ngayDangKy) {
        Map<String, String> data = new HashMap<>();
        data.put("MaKH", maKH);
        data.put("HoTen", hoTen);
        data.put("SoDT", soDT);
        data.put("Email", email);
        data.put("CMND", cmnd);
        data.put("NgaySinh", dateToDateOnlyString(ngaySinh));
        data.put("GioiTinh", gioiTinh);
        data.put("DiaChi", diaChi);
        data.put("TenDangNhap", tenDangNhap);
        data.put("MatKhau", matKhau);
        data.put("HangKhachHang", hangKhachHang);
        data.put("DiemTichLuy", String.valueOf(diemTichLuy));
        data.put("NgayDangKy", dateToDateOnlyString(ngayDangKy));
        return data;
    }
}