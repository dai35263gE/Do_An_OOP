/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.mycompany.QuanLyVeMayBay;

/**
 *
 * @author HP
 */
// File: XMLHandler.java


import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class XMLHandler {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    // ========== ĐỌC DỮ LIỆU TỪ XML ==========
    
    public static List<Map<String, String>> docFileXML(String tenFile) {
        List<Map<String, String>> danhSach = new ArrayList<>();
        try {
            File file = new File(tenFile);
            if (!file.exists()) {
                System.out.println("File không tồn tại: " + tenFile);
                return danhSach;
            }
            
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            doc.getDocumentElement().normalize();
            
            String rootElement = doc.getDocumentElement().getNodeName();
            NodeList nodeList = doc.getElementsByTagName(rootElement.substring(0, rootElement.length() - 1));
            
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    Map<String, String> data = new HashMap<>();
                    
                    NodeList childNodes = element.getChildNodes();
                    for (int j = 0; j < childNodes.getLength(); j++) {
                        Node childNode = childNodes.item(j);
                        if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                            String tagName = childNode.getNodeName();
                            String textContent = childNode.getTextContent();
                            data.put(tagName, textContent);
                        }
                    }
                    danhSach.add(data);
                }
            }
            
            System.out.println("Đoc file XML thanh cong: " + tenFile + " (" + danhSach.size() + " ban ghi)");
            
        } catch (Exception e) {
            System.out.println("Loi đoc file XML: " + tenFile + " - " + e.getMessage());
        }
        
        return danhSach;
    }
    
    // ========== GHI DỮ LIỆU RA XML ==========
    
    public static <T> boolean ghiFileXML(String tenFile, List<T> danhSach, String rootElementName) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            
            // Tạo root element
            Element rootElement = doc.createElement(rootElementName);
            doc.appendChild(rootElement);
            
            // Thêm các phần tử con
            for (T obj : danhSach) {
                Element itemElement = taoElementTuObject(doc, obj);
                if (itemElement != null) {
                    rootElement.appendChild(itemElement);
                }
            }
            
            // Ghi file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(tenFile));
            transformer.transform(source, result);
            
            System.out.println("Ghi file XML thành công: " + tenFile);
            return true;
            
        } catch (Exception e) {
            System.out.println("Lỗi ghi file XML: " + tenFile + " - " + e.getMessage());
            return false;
        }
    }
    
    private static Element taoElementTuObject(Document doc, Object obj) {
        // Triển khai tạo element từ object
        // Đây là phương thức generic, cần override cho từng loại object
        return null;
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