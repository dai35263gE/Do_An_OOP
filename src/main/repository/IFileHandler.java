package repository;

import java.util.List;
import java.util.Map;

public interface IFileHandler {
    boolean docFile(String tenFile);
    boolean ghiFile(String tenFile);
    
    // Thêm phương thức mới để xử lý XML
    default List<Map<String, String>> docFileXML(String tenFile) {
        return XMLUtils.docFileXML(tenFile);
    }
    
    default boolean ghiFileXML(String tenFile, List<Map<String, String>> dataList, String rootElementName) {
        return XMLUtils.ghiFileXML(tenFile, dataList, rootElementName);
    }
}