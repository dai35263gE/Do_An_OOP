package Main.utils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Lớp tiện ích để vẽ biểu đồ cột đơn giản bằng Java Swing.
 * Sử dụng bằng cách:
 * Map<String, Double> data = ...;
 * BarChartPanel chart = new BarChartPanel(data, "Tiêu đề biểu đồ");
 * panelCuaBan.add(chart);
 */
public class BarChartPanel extends JPanel {

    private Map<String, Double> data;
    private String title;
    private Color barColor = new Color(70, 130, 180); // Màu xanh dương
    private Color labelColor = new Color(50, 50, 50); // Màu chữ
    private int padding = 30;
    private int labelPadding = 20;

    /**
     * @param data Map chứa dữ liệu (Label, Value)
     * @param title Tiêu đề của biểu đồ
     */
    public BarChartPanel(Map<String, Double> data, String title) {
        this.data = data;
        this.title = title;
        setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));
        setBackground(Color.WHITE);
    }

    /**
     * Ghi đè phương thức paintComponent để vẽ biểu đồ
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (data == null || data.isEmpty()) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Lấy kích thước của panel
        int width = getWidth();
        int height = getHeight();

        // Vẽ tiêu đề
        FontMetrics titleMetrics = g2.getFontMetrics(g2.getFont().deriveFont(Font.BOLD, 16f));
        int titleWidth = titleMetrics.stringWidth(title);
        g2.setColor(labelColor);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 16f));
        g2.drawString(title, (width - titleWidth) / 2, padding);

        // Chiều cao và chiều rộng của khu vực vẽ biểu đồ
        int chartHeight = height - 2 * padding - labelPadding - titleMetrics.getHeight();
        int chartWidth = width - 2 * padding;

        // Tìm giá trị lớn nhất để chia tỷ lệ
        double maxValue = Collections.max(data.values());
        if (maxValue == 0) maxValue = 1; // Tránh chia cho 0

        // Tính độ rộng của mỗi cột (bao gồm cả khoảng cách)
        int barWidth = chartWidth / data.size();
        int barGap = (int) (barWidth * 0.2); // 20% khoảng cách
        int actualBarWidth = barWidth - barGap;

        // Vị trí bắt đầu vẽ (từ dưới lên)
        int chartY = height - padding - labelPadding;

        // Vẽ từng cột
        int x = padding;
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 10f));
        FontMetrics labelMetrics = g2.getFontMetrics();

        for (Map.Entry<String, Double> entry : data.entrySet()) {
            String label = entry.getKey();
            double value = entry.getValue();

            // Tính chiều cao cột
            int barHeight = (int) ((value / maxValue) * chartHeight);
            int y = chartY - barHeight;

            // Vẽ cột
            g2.setColor(barColor);
            g2.fillRect(x + (barGap / 2), y, actualBarWidth, barHeight);

            // Vẽ giá trị trên đầu cột
            g2.setColor(labelColor);
            String valueStr = String.format("%,.0f", value);
            int valueWidth = labelMetrics.stringWidth(valueStr);
            g2.drawString(valueStr, x + (barWidth - valueWidth) / 2, y - 5);

            // Vẽ nhãn (label) bên dưới cột
            int labelWidth = labelMetrics.stringWidth(label);
            if (labelWidth > barWidth && label.length() > 10) { // Sửa logic cắt chuỗi
                // Nếu nhãn quá dài, cắt bớt
                label = label.substring(0, Math.min(label.length(), 10)) + "...";
                labelWidth = labelMetrics.stringWidth(label);
            }
            g2.drawString(label, x + (barWidth - labelWidth) / 2, chartY + labelPadding);

            x += barWidth;
        }
    }

    /**
     * Phương thức tiện ích để tạo một JScrollPane chứa BarChartPanel,
     * vì JScrollPane không hoạt động tốt với việc vẽ tùy chỉnh nếu không có
     * sự gợi ý về kích thước.
     */
    public static JScrollPane createChartScrollPane(Map<String, Double> data, String title, int preferredWidth) {
        // Sắp xếp dữ liệu (ví dụ: giảm dần)
        Map<String, Double> sortedData = data.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        BarChartPanel chartPanel = new BarChartPanel(sortedData, title);

        // Gợi ý kích thước cho JScrollPane
        int preferredHeight = 400; // Chiều cao cố định
        // Chiều rộng động dựa trên số lượng cột, nhưng tối thiểu là preferredWidth
        int dynamicWidth = Math.max(preferredWidth, sortedData.size() * 80); // 80px mỗi cột

        chartPanel.setPreferredSize(new Dimension(dynamicWidth, preferredHeight));

        JScrollPane scrollPane = new JScrollPane(chartPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }
}