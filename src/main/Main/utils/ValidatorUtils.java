package Main.utils;

import javax.swing.*;

import java.awt.*;
import java.awt.Dimension;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ValidatorUtils {
    
    
    // Validate ngày theo định dạng dd/MM/yyyy
    public static boolean isValidDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
    
    // Parse date từ string
    public static Date parseDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }
    
    // Validate số tiền
    public static boolean isValidAmount(String amount) {
        try {
            double value = Double.parseDouble(amount);
            return value >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    // Kiểm tra trường bắt buộc
    public static boolean validateRequiredFields(JDialog dialog, JTextField... fields) {
        for (JTextField field : fields) {
            if (field.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Vui lòng nhập đầy đủ thông tin bắt buộc!",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                field.requestFocus();
                return false;
            }
        }
        return true;
    }
    
    // Kiểm tra thời gian hợp lệ (thời gian bắt đầu phải trước thời gian kết thúc)
    public static boolean isValidTimeRange(Date startTime, Date endTime) {
        return startTime.before(endTime);
    }
    
    // Kiểm tra thời gian trong tương lai
    public static boolean isFutureTime(Date time) {
        return time.after(new Date());
    }
    
    // Hiển thị thông báo lỗi
    public static void showErrorDialog(JComponent parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
    
    // Hiển thị thông báo thành công
    public static void showSuccessDialog(JComponent parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Hiển thị thông báo cảnh báo
    public static void showWarningDialog(JComponent parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Cảnh báo", JOptionPane.WARNING_MESSAGE);
    }

    public static void showErrorDialog(Component parent, String message) {
        showErrorDialog(parent, "Lỗi", message);
    }

    public static void showErrorDialog(Component parent, String title, String message) {
        JTextArea textArea = createStyledTextArea(message, new Color(220, 53, 69)); // Màu đỏ
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 150));
        
        JOptionPane.showMessageDialog(parent, scrollPane, title, 
                                    JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Hiển thị dialog cảnh báo với icon và màu sắc phù hợp
     */
    public static void showWarningDialog(Component parent, String message) {
        showWarningDialog(parent, "Cảnh báo", message);
    }

    public static void showWarningDialog(Component parent, String title, String message) {
        JTextArea textArea = createStyledTextArea(message, new Color(255, 193, 7)); // Màu vàng
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 150));
        
        JOptionPane.showMessageDialog(parent, scrollPane, title, 
                                    JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Hiển thị dialog thông tin thành công
     */
    public static void showSuccessDialog(Component parent, String message) {
        showSuccessDialog(parent, "Thành công", message);
    }

    public static void showSuccessDialog(Component parent, String title, String message) {
        JTextArea textArea = createStyledTextArea(message, new Color(40, 167, 69)); // Màu xanh lá
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 150));
        
        JOptionPane.showMessageDialog(parent, scrollPane, title, 
                                    JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Hiển thị dialog xác nhận với tùy chọn Yes/No
     */
    public static boolean showConfirmDialog(Component parent, String message) {
        return showConfirmDialog(parent, "Xác nhận", message);
    }

    public static boolean showConfirmDialog(Component parent, String title, String message) {
        JTextArea textArea = createStyledTextArea(message, new Color(0, 123, 255)); // Màu xanh dương
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 150));
        
        int result = JOptionPane.showConfirmDialog(parent, scrollPane, title,
                                                  JOptionPane.YES_NO_OPTION,
                                                  JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }

    /**
     * Hiển thị dialog xác nhận với tùy chọn Yes/No/Cancel
     */
    public static int showConfirmDialogWithCancel(Component parent, String message) {
        return showConfirmDialogWithCancel(parent, "Xác nhận", message);
    }

    public static int showConfirmDialogWithCancel(Component parent, String title, String message) {
        JTextArea textArea = createStyledTextArea(message, new Color(0, 123, 255));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 150));
        
        return JOptionPane.showConfirmDialog(parent, scrollPane, title,
                                           JOptionPane.YES_NO_CANCEL_OPTION,
                                           JOptionPane.QUESTION_MESSAGE);
    }

    /**
     * Tạo JTextArea được style cho dialog
     */
    private static JTextArea createStyledTextArea(String message, Color textColor) {
        JTextArea textArea = new JTextArea(message);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(new Color(248, 249, 250)); // Màu nền sáng
        textArea.setForeground(textColor);
        textArea.setFont(new Font("Arial", Font.PLAIN, 13));
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        textArea.setMargin(new Insets(10, 10, 10, 10));
        return textArea;
    }

    /**
     * Hiển thị dialog lỗi với chi tiết exception
     */
    public static void showExceptionDialog(Component parent, String message, Exception ex) {
        String detailedMessage = message + "\n\nChi tiết lỗi:\n" + ex.getMessage();
        
        JTextArea textArea = new JTextArea(detailedMessage);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(new Color(255, 245, 245)); // Màu nền đỏ nhạt
        textArea.setForeground(new Color(220, 53, 69));
        textArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 200));
        
        JOptionPane.showMessageDialog(parent, scrollPane, "Lỗi Hệ Thống", 
                                    JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Hiển thị dialog với tùy chỉnh nút
     */
    public static int showCustomOptionDialog(Component parent, String message, String[] options, String defaultOption) {
        JTextArea textArea = createStyledTextArea(message, new Color(33, 37, 41));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 150));
        
        return JOptionPane.showOptionDialog(parent, scrollPane, "Tùy chọn",
                                          JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                                          null, options, defaultOption);
    }

    /**
     * Hiển thị dialog input với validation
     */
    public static String showInputDialog(Component parent, String message) {
        return showInputDialog(parent, "Nhập liệu", message);
    }

    public static String showInputDialog(Component parent, String title, String message) {
        JTextArea messageArea = createStyledTextArea(message, new Color(33, 37, 41));
        JScrollPane messageScroll = new JScrollPane(messageArea);
        messageScroll.setPreferredSize(new Dimension(400, 100));
        
        JTextField inputField = new JTextField(30);
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(messageScroll, BorderLayout.NORTH);
        panel.add(new JLabel("Giá trị:"), BorderLayout.WEST);
        panel.add(inputField, BorderLayout.CENTER);
        
        int result = JOptionPane.showConfirmDialog(parent, panel, title,
                                                 JOptionPane.OK_CANCEL_OPTION,
                                                 JOptionPane.QUESTION_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            return inputField.getText().trim();
        }
        return null;
    }

    /**
     * Hiển thị dialog với thời gian tự động đóng
     */
    public static void showAutoCloseDialog(Component parent, String message, int delayMillis) {
        JTextArea textArea = createStyledTextArea(message, new Color(40, 167, 69));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(300, 100));
        
        JDialog dialog = new JDialog();
        dialog.setTitle("Thông báo");
        dialog.setModal(false);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.add(scrollPane);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        
        // Tự động đóng sau delay
        Timer timer = new Timer(delayMillis, e -> dialog.dispose());
        timer.setRepeats(false);
        timer.start();
    }

    // ========== PHƯƠNG THỨC VALIDATION BỔ SUNG ==========

    /**
     * Kiểm tra và hiển thị lỗi nếu validation thất bại
     */
    public static boolean validateAndShowError(Component parent, boolean condition, String errorMessage) {
        if (!condition) {
            showErrorDialog(parent, errorMessage);
            return false;
        }
        return true;
    }

    /**
     * Kiểm tra chuỗi không rỗng và hiển thị lỗi nếu cần
     */
    public static boolean validateNotEmpty(Component parent, String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            showErrorDialog(parent, fieldName + " không được để trống!");
            return false;
        }
        return true;
    }

    /**
     * Kiểm tra số hợp lệ và hiển thị lỗi nếu cần
     */
    public static boolean validateNumber(Component parent, String value, String fieldName) {
        if (!isValidAmount(value)) {
            showErrorDialog(parent, fieldName + " phải là số hợp lệ!");
            return false;
        }
        return true;
    }

    /**
     * Kiểm tra email và hiển thị lỗi nếu cần
     */
    public static boolean validateEmail(Component parent, String email) {
        if (email == null || email.trim().isEmpty()) {
            showErrorDialog(parent, "Email không được để trống!");
            return false;
        }
        return true;
    }

    /**
     * Kiểm tra số điện thoại và hiển thị lỗi nếu cần
     */
    public static boolean validatePhone(Component parent, String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            showErrorDialog(parent, "Số điện thoại không được để trống!");
            return false;
        }
        return true;
    }

    /**
     * Kiểm tra CMND và hiển thị lỗi nếu cần
     */
    public static boolean validateCMND(Component parent, String cmnd) {
        if (cmnd == null || cmnd.trim().isEmpty()) {
            showErrorDialog(parent, "CMND/CCCD không được để trống!");
            return false;
        }
        return true;
    }

    /**
     * Kiểm tra số ghế và hiển thị lỗi nếu cần
     */
    public static boolean validateSoGhe(Component parent, String soGhe) {
        if (soGhe == null || soGhe.trim().isEmpty()) {
            showErrorDialog(parent, "Số ghế không được để trống!");
            return false;
        }
        return true;
    }
}
