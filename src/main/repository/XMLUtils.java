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

    // ========== CẬP NHẬT PHƯƠNG THỨC docFileXML ==========
public static List<Map<String, String>> docFileXML(String tenFile) {
    List<Map<String, String>> danhSach = new ArrayList<>();
    try {
        File file = new File(tenFile);
        if (!file.exists()) {
            System.out.println("File không tồn tại: " + tenFile);
            return danhSach;
        }

        // Sửa lỗi file có 2 dòng khai báo XML
        String fixedXmlContent = fixDuplicateXmlDeclaration(file);
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        
        Document doc = builder.parse(new ByteArrayInputStream(fixedXmlContent.getBytes()));
        doc.getDocumentElement().normalize();

        String rootElementName = doc.getDocumentElement().getNodeName();
        
        // Xác định loại dữ liệu dựa trên root element - CẬP NHẬT THÊM HoaDons
        if (rootElementName.equals("ChuyenBays")) {
            return docChuyenBays(doc);
        } else if (rootElementName.equals("KhachHangs")) {
            return docKhachHangs(doc);
        } else if (rootElementName.equals("VeMayBays")) {
            return docVeMayBays(doc);
        } else if (rootElementName.equals("HoaDons")) { // THÊM DÒNG NÀY
            return docHoaDons(doc);
        } else if (rootElementName.equals("NhanViens")) { // THÊM LUÔN NHÂN VIÊN
            return docNhanViens(doc);
        } else {
            System.out.println("Không hỗ trợ định dạng XML: " + rootElementName);
        }

    } catch (Exception e) {
        System.out.println("Lỗi đọc file XML: " + tenFile);
        e.printStackTrace();
    }

    return danhSach;
}
    // Đọc dữ liệu ChuyenBays
    private static List<Map<String, String>> docChuyenBays(Document doc) {
        List<Map<String, String>> danhSach = new ArrayList<>();
        NodeList nodeList = doc.getElementsByTagName("ChuyenBay");
        
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                Map<String, String> data = new HashMap<>();

                String[] tags = {"MaChuyen", "DiemDi", "DiemDen", "GioKhoiHanh", 
                               "GioDen", "SoGhe", "SoGheTrong", "MaMayBay", 
                               "GiaCoBan", "TrangThai"};
                
                for (String tag : tags) {
                    data.put(tag, getElementValue(element, tag));
                }
                
                danhSach.add(data);
            }
        }
        System.out.println("Đọc " + danhSach.size() + " chuyến bay từ XML");
        return danhSach;
    }

    // Đọc dữ liệu KhachHangs
    private static List<Map<String, String>> docKhachHangs(Document doc) {
        List<Map<String, String>> danhSach = new ArrayList<>();
        NodeList nodeList = doc.getElementsByTagName("KhachHang");
        
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                Map<String, String> data = new HashMap<>();

                String[] tags = {"MaKH", "HoTen", "SoDT", "Email", "CMND", 
                               "NgaySinh", "GioiTinh", "DiaChi", 
                               "HangKhachHang", "DiemTichLuy", "NgayDangKy"};
                
                for (String tag : tags) {
                    data.put(tag, getElementValue(element, tag));
                }
                
                danhSach.add(data);
            }
        }
        System.out.println("Đọc " + danhSach.size() + " khách hàng từ XML");
        return danhSach;
    }

    // Đọc dữ liệu VeMayBays (hỗ trợ nhiều loại vé)
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

    // Đọc thông tin chung cho các loại vé
    private static Map<String, String> docVeMayBay(Node node, String loaiVe) {
        Map<String, String> data = new HashMap<>();
        data.put("LoaiVe", loaiVe);
        
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            
            // Thông tin chung của tất cả vé
            String[] commonTags = {"MaVe", "MaKH","HoTenKH", "CMND", "NgayBay", "GiaVe", 
                                 "MaChuyen", "SoGhe", "TrangThai", "NgayDat"};
            
            for (String tag : commonTags) {
                data.put(tag, getElementValue(element, tag));
            }
            
            // Thông tin riêng cho từng loại vé
            switch (loaiVe) {
                case "VeThuongGia":
                    String[] thuongGiaTags = {"DichVuDacBiet", "PhuThu", "SoKgHanhLyMienPhi", 
                                            "PhongChoVIP", "LoaiDoUong"};
                    for (String tag : thuongGiaTags) {
                        data.put(tag, getElementValue(element, tag));
                    }
                    break;
                    
                case "VePhoThong":
                    String[] phoThongTags = {"HanhLyXachTay", "SoKgHanhLyKyGui", 
                                           "PhiHanhLy", "LoaiGhe", "DoAn"};
                    for (String tag : phoThongTags) {
                        data.put(tag, getElementValue(element, tag));
                    }
                    break;
                    
                case "VeTietKiem":
                    String[] tietKiemTags = {"SoGioDatTruoc", "TyLeGiam", "HoanDoi", 
                                           "PhiHoanDoi", "DieuKienGia"};
                    for (String tag : tietKiemTags) {
                        data.put(tag, getElementValue(element, tag));
                    }
                    break;
            }
        }
        
        return data;
    }
    private static List<Map<String, String>> docHoaDons(Document doc) {
    List<Map<String, String>> danhSach = new ArrayList<>();
    NodeList nodeList = doc.getElementsByTagName("HoaDon");
    
    for (int i = 0; i < nodeList.getLength(); i++) {
        Node node = nodeList.item(i);
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            Map<String, String> data = new HashMap<>();

            String[] tags = {
                "MaHoaDon", "NgayLap", "MaVe", "MaKH", "MaNV", 
                "TongTien", "Thue", "KhuyenMai", "ThanhTien", 
                "PhuongThucTT", "TrangThai"
            };
            
            for (String tag : tags) {
                data.put(tag, getElementValue(element, tag));
            }
            
            danhSach.add(data);
        }
    }
    System.out.println("Đọc " + danhSach.size() + " hóa đơn từ XML");
    return danhSach;
}



// ========== ĐỌC DỮ LIỆU NHÂN VIÊN ========== (Bổ sung thêm)
private static List<Map<String, String>> docNhanViens(Document doc) {
    List<Map<String, String>> danhSach = new ArrayList<>();
    NodeList nodeList = doc.getElementsByTagName("NhanVien");
    
    for (int i = 0; i < nodeList.getLength(); i++) {
        Node node = nodeList.item(i);
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            Map<String, String> data = new HashMap<>();

            String[] tags = {
                "MaNV", "HoTen", "SoDT", "Email", "CMND", 
                "NgaySinh", "GioiTinh", "DiaChi", 
                "ChucVu", "LuongCoBan", "NgayVaoLam", "TrangThai"
            };
            
            for (String tag : tags) {
                data.put(tag, getElementValue(element, tag));
            }
            
            danhSach.add(data);
        }
    }
    System.out.println("Đọc " + danhSach.size() + " nhân viên từ XML");
    return danhSach;
}

// ========== CẬP NHẬT PHƯƠNG THỨC getChildElementName ==========
private static String getChildElementName(String rootElementName) {
    switch (rootElementName) {
        case "ChuyenBays": return "ChuyenBay";
        case "KhachHangs": return "KhachHang";
        case "VeMayBays": return "VeMayBay";
        case "HoaDons": return "HoaDon"; // THÊM DÒNG NÀY
        case "NhanViens": return "NhanVien"; // THÊM DÒNG NÀY
        default: return "Item";
    }
}

    // Lấy giá trị của một element
    private static String getElementValue(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            return node.getTextContent().trim();
        }
        return "";
    }

    // Sửa lỗi duplicate XML declaration
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
                // Bỏ qua các dòng khai báo XML tiếp theo
            } else {
                content.append(line).append("\n");
            }
        }
        reader.close();
        
        return content.toString();
    }

    // ========== GHI DỮ LIỆU RA XML ==========
    public static boolean ghiFileXML(String tenFile, List<Map<String, String>> dataList, String rootElementName) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            // Tạo root element
            Element rootElement = doc.createElement(rootElementName);
            doc.appendChild(rootElement);

            // Xác định loại element con dựa trên root element
            String childElementName = getChildElementName(rootElementName);
            
            // Thêm các phần tử con
            for (Map<String, String> data : dataList) {
                Element itemElement = doc.createElement(childElementName);
                
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    // Không ghi LoaiVe vào XML (chỉ dùng để phân biệt khi đọc)
                    if (!"LoaiVe".equals(entry.getKey())) {
                        Element fieldElement = doc.createElement(entry.getKey());
                        fieldElement.appendChild(doc.createTextNode(entry.getValue()));
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



    // ========== PHƯƠNG THỨC CHUYỂN ĐỔI ==========
    public static Date stringToDate(String dateString) {
        try {
            if (dateString.contains(" ")) {
                return dateFormat.parse(dateString);
            } else {
                return dateOnlyFormat.parse(dateString);
            }
        } catch (Exception e) {
            System.out.println("Lỗi chuyển đổi ngày: " + dateString);
            return new Date();
        }
    }

    public static String dateToString(Date date) {
        return dateFormat.format(date);
    }

    public static String dateToDateOnlyString(Date date) {
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
}