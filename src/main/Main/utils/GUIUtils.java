package Main.utils;

import javax.swing.*;
import java.awt.*;

public class GUIUtils {
    
    // Phương thức tạo màu theo type
    public static Color getColorByType(String type) {
        switch (type) {
            case "primary":
                return new Color(70, 130, 180);
            case "success":
                return new Color(60, 179, 113);
            case "info":
                return new Color(30, 144, 255);
            case "warning":
                return new Color(255, 165, 0);
            default:
                return new Color(70, 130, 180);
        }
    }
    
    // Phương thức tạo button action với icon và tooltip
    public static JButton createActionButton(String text, String icon, String tooltip) {
        JButton button = new JButton("<html><center>" + icon + "<br>" + text + "</center></html>");
        button.setFont(new Font("Arial", Font.PLAIN, 15));
        button.setToolTipText(tooltip);
        button.setPreferredSize(new Dimension(120, 80));
        return button;
    }
    
    // Phương thức thêm dòng form với GridBagLayout
    public static void addFormRow(JPanel panel, GridBagConstraints gbc, String label, JComponent component) {
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(label), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(component, gbc);
        
        gbc.gridy++;
    }
    
    // Phương thức thêm dòng thông tin với GridBagLayout (1 cột)
    public static void addInfoRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        lbl.setPreferredSize(new Dimension(150, 20));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(lblValue, gbc);
    }
    
    // Phương thức thêm dòng thông tin với GridBagLayout (2 cột)
    public static void addInfoRow2(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridx = 0;
        gbc.gridy = row;
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        lbl.setPreferredSize(new Dimension(180, 20));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(lblValue, gbc);
    }
    
    // Phương thức hiển thị icon trạng thái
    public static String getTrangThaiWithIcon(String trangThai) {
        switch (trangThai) {
            case "ĐẶT": return "✅ " + trangThai;
            case "HOÀN TẤT": return "🎫 " + trangThai;
            case "HỦY": return "❌ " + trangThai;
            case "ĐÃ BAY": return "✈️ " + trangThai;
            default: return trangThai;
        }
    }
    
    // Phương thức tạo spinner cho ngày
    public static JSpinner createDateSpinner() {
        JSpinner spinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "dd/MM/yyyy HH:mm");
        spinner.setEditor(editor);
        return spinner;
    }
    
    // Phương thức tạo spinner cho số
    public static JSpinner createNumberSpinner(double value, double min, double max, double step) {
        return new JSpinner(new SpinnerNumberModel(value, min, max, step));
    }
}