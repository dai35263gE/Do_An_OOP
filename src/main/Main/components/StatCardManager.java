package Main.components;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

import Sevice.QuanLyBanVeMayBay;

public class StatCardManager {
    private JPanel[] statCards;
    private JPanel statsPanel;
    private QuanLyBanVeMayBay quanLy;

    public StatCardManager(QuanLyBanVeMayBay quanLy) {
        this.quanLy = quanLy;
        this.statCards = new JPanel[8];
        this.statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        taoStatCards();
    }

    public JPanel getStatsPanel() {
        return statsPanel;
    }

    private void taoStatCards() {
        // Tạo 8 thẻ thống kê
        String[] titles = {
            "Tổng số vé", "Tổng chuyến bay", "Tổng khách hàng", "Tổng doanh thu",
            "Vé thương gia", "Vé phổ thông", "Vé tiết kiệm", "Tỷ lệ lấp đầy"
        };

        String[] values = {"0", "0", "0", "0 VND", "0", "0", "0", "0%"};

        String[] types = {
            "primary", "success", "info", "warning",
            "primary", "success", "info", "warning"
        };

        for (int i = 0; i < 8; i++) {
            statCards[i] = taoStatCard(titles[i], values[i], types[i]);
            statsPanel.add(statCards[i]);
        }
    }

    public JPanel taoStatCard(String title, String value, String type) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(getColorByType(type));
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        card.setPreferredSize(new Dimension(180, 100));

        JLabel lblValue = new JLabel(value, JLabel.CENTER);
        lblValue.setFont(new Font("Arial", Font.BOLD, 24));
        lblValue.setForeground(Color.WHITE);
        lblValue.setName("value"); // Đặt tên để dễ tìm

        JLabel lblTitle = new JLabel("<html><center>" + title + "</center></html>", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        lblTitle.setForeground(Color.WHITE);

        JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
        contentPanel.setOpaque(false);
        contentPanel.add(lblValue, BorderLayout.CENTER);
        contentPanel.add(lblTitle, BorderLayout.SOUTH);

        card.add(contentPanel, BorderLayout.CENTER);
        
        // Thêm hiệu ứng hover
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            }
        });

        return card;
    }

    public void updateStatCard(int index, String newValue) {
        if (index < 0 || index >= statCards.length) {
            return;
        }

        JPanel card = statCards[index];
        Component[] components = card.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                Component[] subComps = ((JPanel) comp).getComponents();
                for (Component subComp : subComps) {
                    if (subComp instanceof JLabel && "value".equals(subComp.getName())) {
                        ((JLabel) subComp).setText(newValue);
                        
                        // Thêm hiệu ứng cập nhật
                        subComp.setForeground(Color.YELLOW);
                        Timer timer = new Timer(500, e -> {
                            subComp.setForeground(Color.WHITE);
                        });
                        timer.setRepeats(false);
                        timer.start();
                        
                        return;
                    }
                }
            }
        }
    }

    public void capNhatThongKeTrangChu() {
        Map<String, Object> thongKe = quanLy.thongKeTongQuan();

        updateStatCard(0, String.valueOf(thongKe.get("TongVe")));
        updateStatCard(1, String.valueOf(thongKe.get("TongChuyenBay")));
        updateStatCard(2, String.valueOf(thongKe.get("TongKhachHang")));
        updateStatCard(3, String.format("%,.0f VND", thongKe.get("TongDoanhThu")));
        updateStatCard(4, String.valueOf(thongKe.get("VeThuongGia")));
        updateStatCard(5, String.valueOf(thongKe.get("VePhoThong")));
        updateStatCard(6, String.valueOf(thongKe.get("VeTietKiem")));
        updateStatCard(7, String.format("%.1f%%", thongKe.get("tiLeLapDay")));
    }

    public void capNhatThongKeTheoLoai(String loai) {
        Map<String, Object> thongKe = quanLy.thongKeTongQuan();
        
        switch (loai) {
            case "ve":
                updateStatCard(0, String.valueOf(thongKe.get("TongVe")));
                updateStatCard(4, String.valueOf(thongKe.get("VeThuongGia")));
                updateStatCard(5, String.valueOf(thongKe.get("VePhoThong")));
                updateStatCard(6, String.valueOf(thongKe.get("VeTietKiem")));
                break;
            case "chuyenbay":
                updateStatCard(1, String.valueOf(thongKe.get("TongChuyenBay")));
                updateStatCard(7, String.format("%.1f%%", thongKe.get("tiLeLapDay")));
                break;
            case "doanhthu":
                updateStatCard(3, String.format("%,.0f VND", thongKe.get("TongDoanhThu")));
                break;
            default:
                capNhatThongKeTrangChu();
        }
    }

    public void resetStatCards() {
        String[] defaultValues = {"0", "0", "0", "0 VND", "0", "0", "0", "0%"};
        for (int i = 0; i < statCards.length; i++) {
            updateStatCard(i, defaultValues[i]);
        }
    }

    public void setStatCardVisible(int index, boolean visible) {
        if (index >= 0 && index < statCards.length) {
            statCards[index].setVisible(visible);
        }
    }

    public void setAllStatCardsVisible(boolean visible) {
        for (JPanel card : statCards) {
            card.setVisible(visible);
        }
    }

    public void addStatCardClickListener(int index, Runnable action) {
        if (index >= 0 && index < statCards.length) {
            statCards[index].addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    action.run();
                }
            });
        }
    }

    public JPanel createCustomStatCard(String title, String value, String type, 
                                     int width, int height, String icon) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(getColorByType(type));
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        card.setPreferredSize(new Dimension(width, height));

        JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
        contentPanel.setOpaque(false);

        // Thêm icon nếu có
        if (icon != null && !icon.isEmpty()) {
            JLabel lblIcon = new JLabel(icon, JLabel.CENTER);
            lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
            lblIcon.setForeground(Color.WHITE);
            contentPanel.add(lblIcon, BorderLayout.NORTH);
        }

        JLabel lblValue = new JLabel(value, JLabel.CENTER);
        lblValue.setFont(new Font("Arial", Font.BOLD, 20));
        lblValue.setForeground(Color.WHITE);

        JLabel lblTitle = new JLabel("<html><center>" + title + "</center></html>", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 12));
        lblTitle.setForeground(Color.WHITE);

        JPanel textPanel = new JPanel(new BorderLayout(0, 5));
        textPanel.setOpaque(false);
        textPanel.add(lblValue, BorderLayout.CENTER);
        textPanel.add(lblTitle, BorderLayout.SOUTH);

        if (icon != null && !icon.isEmpty()) {
            contentPanel.add(textPanel, BorderLayout.CENTER);
        } else {
            contentPanel.add(textPanel, BorderLayout.CENTER);
        }

        card.add(contentPanel, BorderLayout.CENTER);
        return card;
    }

    public Map<String, Object> getCurrentStatistics() {
        return quanLy.thongKeTongQuan();
    }

    public void refreshStatistics() {
        capNhatThongKeTrangChu();
    }

    public void setStatCardToolTip(int index, String tooltip) {
        if (index >= 0 && index < statCards.length) {
            statCards[index].setToolTipText(tooltip);
        }
    }

    public void setAllStatCardsToolTips(String[] tooltips) {
        if (tooltips.length == statCards.length) {
            for (int i = 0; i < statCards.length; i++) {
                statCards[i].setToolTipText(tooltips[i]);
            }
        }
    }

    private Color getColorByType(String type) {
        switch (type) {
            case "primary":
                return new Color(70, 130, 180);   // Steel Blue
            case "success":
                return new Color(60, 179, 113);   // Medium Sea Green
            case "info":
                return new Color(30, 144, 255);   // Dodger Blue
            case "warning":
                return new Color(255, 165, 0);    // Orange
            case "danger":
                return new Color(220, 53, 69);    // Red
            case "secondary":
                return new Color(108, 117, 125);  // Gray
            case "dark":
                return new Color(52, 58, 64);     // Dark Gray
            default:
                return new Color(70, 130, 180);   // Mặc định
        }
    }

    // Phương thức để lấy thông tin chi tiết cho tooltip
    public String[] getDefaultToolTips() {
        return new String[] {
            "Tổng số vé đã được đặt trong hệ thống",
            "Tổng số chuyến bay hiện có",
            "Tổng số khách hàng đã đăng ký",
            "Tổng doanh thu từ tất cả vé đã bán",
            "Số vé hạng thương gia đã bán",
            "Số vé hạng phổ thông đã bán", 
            "Số vé hạng tiết kiệm đã bán",
            "Tỷ lệ ghế đã được đặt trên tổng số ghế"
        };
    }

    // Phương thức khởi tạo với tooltip mặc định
    public void initializeWithDefaultToolTips() {
        String[] tooltips = getDefaultToolTips();
        setAllStatCardsToolTips(tooltips);
    }
}