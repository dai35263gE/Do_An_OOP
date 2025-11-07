package Main.dialogs;

import Main.MainGUI;
import javax.swing.*;
import Main.utils.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;

import model.*;
import Sevice.QuanLyBanVeMayBay;

public class ChuyenBayDialogs {
    private MainGUI mainGUI;
    private QuanLyBanVeMayBay quanLy;
    private JTable tableChuyenBay;

    public ChuyenBayDialogs(MainGUI mainGUI, QuanLyBanVeMayBay quanLy, JTable tableChuyenBay) {
        this.mainGUI = mainGUI;
        this.quanLy = quanLy;
        this.tableChuyenBay = tableChuyenBay;
    }

    // ========== DIALOG THÊM CHUYẾN BAY ==========
    public void moDialogThemChuyenBay() {
        JDialog dialog = new JDialog(mainGUI, "Thêm Chuyến Bay Mới", true);
        dialog.setSize(600, 650);
        dialog.setLocationRelativeTo(mainGUI);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Tự động tạo mã chuyến bay
        int soChuyenBayHienTai = quanLy.getDsChuyenBay().demSoLuong();
        String maChuyenTuDong = "CB" + String.format("%03d", soChuyenBayHienTai + 1);
        JTextField txtMaChuyen = new JTextField(maChuyenTuDong, 20);
        txtMaChuyen.setEditable(false);
        txtMaChuyen.setBackground(new Color(240, 240, 240));
        
        // ComboBox cho điểm đi và điểm đến
        String[] diaDiem = {"Hà Nội (HAN)", "TP.HCM (SGN)", "Đà Nẵng (DAD)", "Nha Trang (CXR)", "Phú Quốc (PQC)", "Huế (HUI)"};
        JComboBox<String> cbDiemDi = new JComboBox<>(diaDiem);
        JComboBox<String> cbDiemDen = new JComboBox<>(diaDiem);
        
        // Spinner cho giờ khởi hành và giờ đến
        JSpinner spinnerGioKhoiHanh = GUIUtils.createDateSpinner();
        JSpinner spinnerGioDen = GUIUtils.createDateSpinner();
        
        // Đặt giờ khởi hành mặc định là ngày mai 6:00 sáng
        Calendar calKhoiHanh = Calendar.getInstance();
        calKhoiHanh.add(Calendar.DAY_OF_MONTH, 1);
        calKhoiHanh.set(Calendar.HOUR_OF_DAY, 6);
        calKhoiHanh.set(Calendar.MINUTE, 0);
        spinnerGioKhoiHanh.setValue(calKhoiHanh.getTime());
        
        // Đặt giờ đến mặc định là ngày mai 8:00 sáng (sau 2 giờ)
        Calendar calDen = Calendar.getInstance();
        calDen.add(Calendar.DAY_OF_MONTH, 1);
        calDen.set(Calendar.HOUR_OF_DAY, 8);
        calDen.set(Calendar.MINUTE, 0);
        spinnerGioDen.setValue(calDen.getTime());
        
        JSpinner spinnerSoGhe = GUIUtils.createNumberSpinner(150, 50, 500, 10);
        
        // ComboBox cho mã máy bay
        String[] mayBay = {"VN-A321", "VN-B787", "VN-A350"};
        JComboBox<String> cbMaMayBay = new JComboBox<>(mayBay);
        
        JSpinner spinnerGiaCoBan = GUIUtils.createNumberSpinner(1500000.0, 500000.0, 50000000.0, 100000.0);
        
        // Định dạng spinner giá
        JSpinner.NumberEditor editorGia = new JSpinner.NumberEditor(spinnerGiaCoBan, "#,##0 VND");
        spinnerGiaCoBan.setEditor(editorGia);

        // Thêm components vào panel
        GUIUtils.addFormRow(panel, gbc, "Mã chuyến bay:", txtMaChuyen);
        GUIUtils.addFormRow(panel, gbc, "Điểm đi:*", cbDiemDi);
        GUIUtils.addFormRow(panel, gbc, "Điểm đến:*", cbDiemDen);
        GUIUtils.addFormRow(panel, gbc, "Giờ khởi hành:*", spinnerGioKhoiHanh);
        GUIUtils.addFormRow(panel, gbc, "Giờ đến:*", spinnerGioDen);
        GUIUtils.addFormRow(panel, gbc, "Số ghế:*", spinnerSoGhe);
        GUIUtils.addFormRow(panel, gbc, "Mã máy bay:*", cbMaMayBay);
        GUIUtils.addFormRow(panel, gbc, "Giá cơ bản:*", spinnerGiaCoBan);

        // Panel hiển thị thông tin
        JPanel panelThongTin = new JPanel(new BorderLayout());
        panelThongTin.setBorder(BorderFactory.createTitledBorder("THÔNG TIN CHUYẾN BAY"));
        JTextArea txtThongTin = new JTextArea(6, 40);
        txtThongTin.setEditable(false);
        txtThongTin.setBackground(new Color(240, 240, 240));
        txtThongTin.setMargin(new Insets(10, 10, 10, 10));
        panelThongTin.add(new JScrollPane(txtThongTin), BorderLayout.CENTER);

        // Cập nhật thông tin khi thay đổi dữ liệu
        Runnable updateChuyenBayInfo = () -> {
            try {
                String maChuyen = txtMaChuyen.getText().trim();
                String diemDi = (String) cbDiemDi.getSelectedItem();
                String diemDen = (String) cbDiemDen.getSelectedItem();
                Date gioKhoiHanh = (Date) spinnerGioKhoiHanh.getValue();
                Date gioDen = (Date) spinnerGioDen.getValue();
                int soGhe = (Integer) spinnerSoGhe.getValue();
                String maMayBay = (String) cbMaMayBay.getSelectedItem();
                double giaCoBan = (Double) spinnerGiaCoBan.getValue();
                
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                long thoiGianBay = (gioDen.getTime() - gioKhoiHanh.getTime()) / (1000 * 60); // phút
                
                String info = String.format(
                    "Mã chuyến: %s\n" +
                    "Lộ trình: %s → %s\n" +
                    "Khởi hành: %s\n" +
                    "Đến: %s\n" +
                    "Thời gian bay: %d phút\n" +
                    "Số ghế: %d\n" +
                    "Máy bay: %s\n" +
                    "Giá cơ bản: %s VND\n" +
                    "Khoảng cách: %.0f km",
                    maChuyen,
                    diemDi,
                    diemDen,
                    sdf.format(gioKhoiHanh),
                    sdf.format(gioDen),
                    thoiGianBay,
                    soGhe,
                    maMayBay,
                    String.format("%,.0f", giaCoBan),
                    10000000
                );
                
                txtThongTin.setText(info);
            } catch (Exception ex) {
                txtThongTin.setText("Đang cập nhật thông tin...");
            }
        };

        // Thêm listeners
        cbDiemDi.addActionListener(e -> updateChuyenBayInfo.run());
        cbDiemDen.addActionListener(e -> updateChuyenBayInfo.run());
        cbMaMayBay.addActionListener(e -> updateChuyenBayInfo.run());
        spinnerGioKhoiHanh.addChangeListener(e -> updateChuyenBayInfo.run());
        spinnerGioDen.addChangeListener(e -> updateChuyenBayInfo.run());
        spinnerSoGhe.addChangeListener(e -> updateChuyenBayInfo.run());
        spinnerGiaCoBan.addChangeListener(e -> updateChuyenBayInfo.run());

        // Gọi lần đầu
        updateChuyenBayInfo.run();

        // Panel button
        JPanel panelButton = new JPanel(new FlowLayout());
        JButton btnThem = new JButton("Thêm Chuyến Bay");
        JButton btnHuy = new JButton("Hủy");
        JButton btnLamMoi = new JButton("Làm Mới");

        btnThem.setBackground(new Color(70, 130, 180));
        btnThem.setForeground(Color.WHITE);
        btnLamMoi.setBackground(new Color(255, 165, 0));
        btnLamMoi.setForeground(Color.WHITE);

        btnThem.addActionListener(e -> {
            if (!validateThemChuyenBay(dialog, cbDiemDi, cbDiemDen, spinnerGioKhoiHanh, spinnerGioDen)) {
                return;
            }

            try {
                // Lấy thông tin từ form
                String maChuyen = txtMaChuyen.getText().trim();
                String diemDi = (String) cbDiemDi.getSelectedItem();
                String diemDen = (String) cbDiemDen.getSelectedItem();
                Date gioKhoiHanh = (Date) spinnerGioKhoiHanh.getValue();
                Date gioDen = (Date) spinnerGioDen.getValue();
                int soGhe = (Integer) spinnerSoGhe.getValue();
                String maMayBay = (String) cbMaMayBay.getSelectedItem();
                double giaCoBan = (Double) spinnerGiaCoBan.getValue();

                // Kiểm tra mã chuyến bay đã tồn tại chưa
                if (quanLy.getDsChuyenBay().timKiemTheoMa(maChuyen) != null) {
                    ValidatorUtils.showErrorDialog(dialog, "Mã chuyến bay đã tồn tại!");
                    return;
                }

                // Tạo chuyến bay mới
                ChuyenBay chuyenBayMoi = new ChuyenBay(
                    maChuyen, diemDi, diemDen, gioKhoiHanh, gioDen,
                    soGhe, soGhe, maMayBay, giaCoBan
                );

                // Thêm vào danh sách
                quanLy.themChuyenBay(chuyenBayMoi);

                // Hiển thị thông báo thành công
                String message = String.format(
                    "Thêm chuyến bay thành công!\n\n" +
                    "Mã chuyến: %s\n" +
                    "Lộ trình: %s → %s\n" +
                    "Khởi hành: %s\n" +
                    "Số ghế: %d\n" +
                    "Máy bay: %s\n" +
                    "Giá cơ bản: %s VND",
                    maChuyen, diemDi, diemDen,
                    new SimpleDateFormat("dd/MM/yyyy HH:mm").format(gioKhoiHanh),
                    soGhe,
                    maMayBay,
                    String.format("%,.0f", giaCoBan)
                );

                ValidatorUtils.showSuccessDialog(dialog, message);

                // Đóng dialog và cập nhật giao diện
                dialog.dispose();
                mainGUI.capNhatDuLieuGUI();

            } catch (Exception ex) {
                ValidatorUtils.showErrorDialog(dialog, "Lỗi: " + ex.getMessage());
            }
        });

        btnLamMoi.addActionListener(e -> {
            // Tạo mã chuyến bay mới
            int soChuyenBayMoi = quanLy.getDsChuyenBay().demSoLuong();
            String maChuyenMoi = "CB" + String.format("%03d", soChuyenBayMoi + 1);
            txtMaChuyen.setText(maChuyenMoi);
            
            // Reset các combobox
            cbDiemDi.setSelectedIndex(0);
            cbDiemDen.setSelectedIndex(1); // Chọn điểm đến khác mặc định
            
            // Reset thời gian
            Calendar calNow = Calendar.getInstance();
            calNow.add(Calendar.DAY_OF_MONTH, 1);
            calNow.set(Calendar.HOUR_OF_DAY, 6);
            calNow.set(Calendar.MINUTE, 0);
            spinnerGioKhoiHanh.setValue(calNow.getTime());
            
            calNow.add(Calendar.HOUR, 2);
            spinnerGioDen.setValue(calNow.getTime());
            
            // Reset các giá trị khác
            spinnerSoGhe.setValue(150);
            cbMaMayBay.setSelectedIndex(0);
            spinnerGiaCoBan.setValue(1500000.0);
            
            ValidatorUtils.showSuccessDialog(dialog, "Đã làm mới form với mã chuyến bay mới!");
        });

        btnHuy.addActionListener(e -> dialog.dispose());

        panelButton.add(btnThem);
        panelButton.add(btnLamMoi);
        panelButton.add(btnHuy);

        // Thêm các panel vào dialog
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(panel, BorderLayout.NORTH);
        mainPanel.add(panelThongTin, BorderLayout.CENTER);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(panelButton, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private boolean validateThemChuyenBay(JDialog dialog, JComboBox<String> cbDiemDi, 
                                         JComboBox<String> cbDiemDen, JSpinner spinnerGioKhoiHanh, 
                                         JSpinner spinnerGioDen) {
        String diemDi = (String) cbDiemDi.getSelectedItem();
        String diemDen = (String) cbDiemDen.getSelectedItem();
        
        if (diemDi.equals(diemDen)) {
            ValidatorUtils.showErrorDialog(dialog, "Điểm đi và điểm đến không được trùng nhau!");
            return false;
        }

        Date gioKhoiHanh = (Date) spinnerGioKhoiHanh.getValue();
        Date gioDen = (Date) spinnerGioDen.getValue();

        if (!ValidatorUtils.isValidTimeRange(gioKhoiHanh, gioDen)) {
            ValidatorUtils.showErrorDialog(dialog, "Giờ khởi hành phải trước giờ đến!");
            return false;
        }

        if (!ValidatorUtils.isFutureTime(gioKhoiHanh)) {
            ValidatorUtils.showErrorDialog(dialog, "Giờ khởi hành phải trong tương lai!");
            return false;
        }

        return true;
    }

    // ========== DIALOG SỬA CHUYẾN BAY ==========
    public void moDialogSuaChuyenBay() {
        int selectedRow = tableChuyenBay.getSelectedRow();
        if (selectedRow == -1) {
            ValidatorUtils.showWarningDialog(mainGUI, "Vui lòng chọn một chuyến bay để sửa!");
            return;
        }

        String maChuyen = (String) tableChuyenBay.getValueAt(selectedRow, 0);
        ChuyenBay cbCanSua = quanLy.getDsChuyenBay().timKiemTheoMa(maChuyen);

        if (cbCanSua == null) {
            ValidatorUtils.showErrorDialog(mainGUI, "Không tìm thấy thông tin chuyến bay!");
            return;
        }

        JDialog dialog = new JDialog(mainGUI, "Sửa Thông Tin Chuyến Bay - " + maChuyen, true);
        dialog.setSize(600, 700);
        dialog.setLocationRelativeTo(mainGUI);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Panel hiển thị thông tin hiện tại
        JPanel panelThongTinHienTai = new JPanel(new BorderLayout());
        panelThongTinHienTai.setBorder(BorderFactory.createTitledBorder("THÔNG TIN HIỆN TẠI"));
        JTextArea txtThongTinHienTai = new JTextArea(6, 40);
        txtThongTinHienTai.setEditable(false);
        txtThongTinHienTai.setBackground(new Color(245, 245, 245));
        txtThongTinHienTai.setForeground(new Color(70, 130, 180));
        txtThongTinHienTai.setFont(new Font("Arial", Font.BOLD, 12));
        txtThongTinHienTai.setMargin(new Insets(10, 10, 10, 10));

        // Hiển thị thông tin chuyến bay hiện tại
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String thongTinHienTai = String.format(
            "Mã chuyến: %s\n" +
            "Lộ trình: %s → %s\n" +
            "Khởi hành: %s\n" +
            "Đến: %s\n" +
            "Số ghế: %d/%d\n" +
            "Giá cơ bản: %,d VND\n" +
            "Trạng thái: %s",
            cbCanSua.getMaChuyen(),
            cbCanSua.getDiemDi(),
            cbCanSua.getDiemDen(),
            sdf.format(cbCanSua.getGioKhoiHanh()),
            sdf.format(cbCanSua.getGioDen()),
            cbCanSua.getSoGheTrong(),
            cbCanSua.getSoGhe(),
            (int) cbCanSua.getGiaCoBan(),
            cbCanSua.getTrangThai()
        );
        txtThongTinHienTai.setText(thongTinHienTai);
        panelThongTinHienTai.add(new JScrollPane(txtThongTinHienTai), BorderLayout.CENTER);

        // Các component nhập liệu để sửa
        JTextField txtMaChuyen = new JTextField(cbCanSua.getMaChuyen());
        txtMaChuyen.setEditable(false);
        txtMaChuyen.setBackground(new Color(240, 240, 240));
        
        JTextField txtDiemDi = new JTextField(cbCanSua.getDiemDi());
        JTextField txtDiemDen = new JTextField(cbCanSua.getDiemDen());
        
        // Spinner cho giờ khởi hành và giờ đến
        JSpinner spinnerGioKhoiHanh = new JSpinner(new SpinnerDateModel(cbCanSua.getGioKhoiHanh(), null, null, Calendar.HOUR_OF_DAY));
        JSpinner.DateEditor editorKhoiHanh = new JSpinner.DateEditor(spinnerGioKhoiHanh, "dd/MM/yyyy HH:mm");
        spinnerGioKhoiHanh.setEditor(editorKhoiHanh);
        
        JSpinner spinnerGioDen = new JSpinner(new SpinnerDateModel(cbCanSua.getGioDen(), null, null, Calendar.HOUR_OF_DAY));
        JSpinner.DateEditor editorDen = new JSpinner.DateEditor(spinnerGioDen, "dd/MM/yyyy HH:mm");
        spinnerGioDen.setEditor(editorDen);
        
        JSpinner spinnerSoGhe = GUIUtils.createNumberSpinner(cbCanSua.getSoGhe(), 50, 500, 10);
        JSpinner spinnerSoGheTrong = GUIUtils.createNumberSpinner(cbCanSua.getSoGheTrong(), 0, cbCanSua.getSoGhe(), 1);
        JTextField txtMaMayBay = new JTextField(cbCanSua.getMaMayBay());
        JSpinner spinnerGiaCoBan = GUIUtils.createNumberSpinner(cbCanSua.getGiaCoBan(), 500000.0, 50000000.0, 100000.0);
        
        // Định dạng spinner giá
        JSpinner.NumberEditor editorGia = new JSpinner.NumberEditor(spinnerGiaCoBan, "#,##0 VND");
        spinnerGiaCoBan.setEditor(editorGia);

        // ComboBox trạng thái
        JComboBox<String> cbTrangThai = new JComboBox<>(new String[] {
            ChuyenBay.TRANG_THAI_CHUA_BAY,
            ChuyenBay.TRANG_THAI_DANG_BAY,
            ChuyenBay.TRANG_THAI_DA_BAY,
            ChuyenBay.TRANG_THAI_HUY
        });
        cbTrangThai.setSelectedItem(cbCanSua.getTrangThai());

        // Thêm components vào panel
        GUIUtils.addFormRow(panel, gbc, "Mã chuyến bay:", txtMaChuyen);
        GUIUtils.addFormRow(panel, gbc, "Điểm đi:*", txtDiemDi);
        GUIUtils.addFormRow(panel, gbc, "Điểm đến:*", txtDiemDen);
        GUIUtils.addFormRow(panel, gbc, "Giờ khởi hành:*", spinnerGioKhoiHanh);
        GUIUtils.addFormRow(panel, gbc, "Giờ đến:*", spinnerGioDen);
        GUIUtils.addFormRow(panel, gbc, "Tổng số ghế:*", spinnerSoGhe);
        GUIUtils.addFormRow(panel, gbc, "Số ghế trống:*", spinnerSoGheTrong);
        GUIUtils.addFormRow(panel, gbc, "Mã máy bay:*", txtMaMayBay);
        GUIUtils.addFormRow(panel, gbc, "Giá cơ bản:*", spinnerGiaCoBan);
        GUIUtils.addFormRow(panel, gbc, "Trạng thái:*", cbTrangThai);

        // Panel hiển thị thông tin cập nhật
        JPanel panelThongTinCapNhat = new JPanel(new BorderLayout());
        panelThongTinCapNhat.setBorder(BorderFactory.createTitledBorder("THÔNG TIN CẬP NHẬT"));
        JTextArea txtThongTinCapNhat = new JTextArea(6, 40);
        txtThongTinCapNhat.setEditable(false);
        txtThongTinCapNhat.setBackground(new Color(240, 248, 255));
        txtThongTinCapNhat.setMargin(new Insets(10, 10, 10, 10));
        panelThongTinCapNhat.add(new JScrollPane(txtThongTinCapNhat), BorderLayout.CENTER);

        // Cập nhật thông tin khi thay đổi dữ liệu
        Runnable updateThongTinCapNhat = () -> {
            try {
                String diemDi = txtDiemDi.getText().trim();
                String diemDen = txtDiemDen.getText().trim();
                Date gioKhoiHanh = (Date) spinnerGioKhoiHanh.getValue();
                Date gioDen = (Date) spinnerGioDen.getValue();
                int soGhe = (Integer) spinnerSoGhe.getValue();
                int soGheTrong = (Integer) spinnerSoGheTrong.getValue();
                double giaCoBan = (Double) spinnerGiaCoBan.getValue();
                String trangThai = (String) cbTrangThai.getSelectedItem();
                
                long thoiGianBay = (gioDen.getTime() - gioKhoiHanh.getTime()) / (1000 * 60); // phút
                
                String info = String.format(
                    "THÔNG TIN CẬP NHẬT:\n\n" +
                    "Lộ trình: %s → %s\n" +
                    "Khởi hành: %s\n" +
                    "Đến: %s\n" +
                    "Thời gian bay: %d phút\n" +
                    "Ghế: %d/%d (%.1f%% lấp đầy)\n" +
                    "Giá cơ bản: %s VND\n" +
                    "Trạng thái: %s",
                    diemDi.isEmpty() ? "?" : diemDi,
                    diemDen.isEmpty() ? "?" : diemDen,
                    sdf.format(gioKhoiHanh),
                    sdf.format(gioDen),
                    thoiGianBay,
                    soGhe - soGheTrong, soGhe,
                    ((double)(soGhe - soGheTrong) / soGhe) * 100,
                    String.format("%,.0f", giaCoBan),
                    trangThai
                );
                
                txtThongTinCapNhat.setText(info);
            } catch (Exception ex) {
                txtThongTinCapNhat.setText("Đang cập nhật thông tin...");
            }
        };

        // Thêm listener cho các component
        DocumentListener docListener = new DocumentListener() {
            public void anyUpdate() { updateThongTinCapNhat.run(); }
            public void insertUpdate(DocumentEvent e) { anyUpdate(); }
            public void removeUpdate(DocumentEvent e) { anyUpdate(); }
            public void changedUpdate(DocumentEvent e) { anyUpdate(); }
        };
        
        txtDiemDi.getDocument().addDocumentListener(docListener);
        txtDiemDen.getDocument().addDocumentListener(docListener);
        txtMaMayBay.getDocument().addDocumentListener(docListener);
        
        spinnerGioKhoiHanh.addChangeListener(e -> updateThongTinCapNhat.run());
        spinnerGioDen.addChangeListener(e -> updateThongTinCapNhat.run());
        spinnerSoGhe.addChangeListener(e -> {
            int soGheMoi = (Integer) spinnerSoGhe.getValue();
            int soGheTrongHienTai = (Integer) spinnerSoGheTrong.getValue();
            
            if (soGheTrongHienTai > soGheMoi) {
                spinnerSoGheTrong.setValue(soGheMoi);
            }
            spinnerSoGheTrong.setModel(new SpinnerNumberModel(
                Math.min(soGheTrongHienTai, soGheMoi), 0, soGheMoi, 1
            ));
            updateThongTinCapNhat.run();
        });
        spinnerSoGheTrong.addChangeListener(e -> updateThongTinCapNhat.run());
        spinnerGiaCoBan.addChangeListener(e -> updateThongTinCapNhat.run());
        cbTrangThai.addActionListener(e -> updateThongTinCapNhat.run());

        // Gọi lần đầu
        updateThongTinCapNhat.run();

        // Panel button
        JPanel panelButton = new JPanel(new FlowLayout());
        JButton btnLuu = new JButton("Lưu Thay Đổi");
        JButton btnHuy = new JButton("Hủy");
        JButton btnKhoiPhuc = new JButton("Khôi Phục Mặc Định");

        btnLuu.setBackground(new Color(70, 130, 180));
        btnLuu.setForeground(Color.WHITE);
        btnKhoiPhuc.setBackground(new Color(255, 165, 0));
        btnKhoiPhuc.setForeground(Color.WHITE);

        btnLuu.addActionListener(e -> {
            if (!validateSuaChuyenBay(dialog, txtDiemDi, txtDiemDen, txtMaMayBay, 
                                    spinnerGioKhoiHanh, spinnerGioDen, spinnerSoGhe, spinnerSoGheTrong)) {
                return;
            }

            try {
                // Lấy thông tin từ form
                String diemDi = txtDiemDi.getText().trim();
                String diemDen = txtDiemDen.getText().trim();
                Date gioKhoiHanh = (Date) spinnerGioKhoiHanh.getValue();
                Date gioDen = (Date) spinnerGioDen.getValue();
                int soGhe = (Integer) spinnerSoGhe.getValue();
                int soGheTrong = (Integer) spinnerSoGheTrong.getValue();
                String maMayBay = txtMaMayBay.getText().trim();
                double giaCoBan = (Double) spinnerGiaCoBan.getValue();
                String trangThai = (String) cbTrangThai.getSelectedItem();

                // Cập nhật thông tin chuyến bay
                cbCanSua.setDiemDi(diemDi);
                cbCanSua.setDiemDen(diemDen);
                cbCanSua.setGioKhoiHanh(gioKhoiHanh);
                cbCanSua.setGioDen(gioDen);
                cbCanSua.setSoGhe(soGhe);
                cbCanSua.setSoGheTrong(soGheTrong);
                cbCanSua.setMaMayBay(maMayBay);
                cbCanSua.setGiaCoBan(giaCoBan);
                cbCanSua.setTrangThai(trangThai);

                // Hiển thị thông báo thành công
                String message = String.format(
                    "Cập nhật chuyến bay thành công!\n\n" +
                    "Mã chuyến: %s\n" +
                    "Lộ trình: %s → %s\n" +
                    "Khởi hành: %s\n" +
                    "Số ghế: %d/%d\n" +
                    "Giá cơ bản: %s VND\n" +
                    "Trạng thái: %s",
                    cbCanSua.getMaChuyen(),
                    diemDi, diemDen,
                    sdf.format(gioKhoiHanh),
                    soGhe - soGheTrong, soGhe,
                    String.format("%,.0f", giaCoBan),
                    trangThai
                );

                ValidatorUtils.showSuccessDialog(dialog, message);

                // Đóng dialog và cập nhật giao diện
                dialog.dispose();
                mainGUI.capNhatDuLieuGUI();

            } catch (Exception ex) {
                ValidatorUtils.showErrorDialog(dialog, "Lỗi: " + ex.getMessage());
            }
        });

        btnKhoiPhuc.addActionListener(e -> {
            // Khôi phục về giá trị ban đầu
            txtDiemDi.setText(cbCanSua.getDiemDi());
            txtDiemDen.setText(cbCanSua.getDiemDen());
            spinnerGioKhoiHanh.setValue(cbCanSua.getGioKhoiHanh());
            spinnerGioDen.setValue(cbCanSua.getGioDen());
            spinnerSoGhe.setValue(cbCanSua.getSoGhe());
            spinnerSoGheTrong.setValue(cbCanSua.getSoGheTrong());
            txtMaMayBay.setText(cbCanSua.getMaMayBay());
            spinnerGiaCoBan.setValue(cbCanSua.getGiaCoBan());
            cbTrangThai.setSelectedItem(cbCanSua.getTrangThai());

            ValidatorUtils.showSuccessDialog(dialog, "Đã khôi phục thông tin ban đầu!");
        });

        btnHuy.addActionListener(e -> dialog.dispose());

        panelButton.add(btnLuu);
        panelButton.add(btnKhoiPhuc);
        panelButton.add(btnHuy);

        // Thêm các panel vào dialog
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(panelThongTinHienTai, BorderLayout.NORTH);
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.add(panelThongTinCapNhat, BorderLayout.SOUTH);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(panelButton, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private boolean validateSuaChuyenBay(JDialog dialog, JTextField txtDiemDi, JTextField txtDiemDen,
                                       JTextField txtMaMayBay, JSpinner spinnerGioKhoiHanh,
                                       JSpinner spinnerGioDen, JSpinner spinnerSoGhe, JSpinner spinnerSoGheTrong) {
        // Validate required fields
        if (!ValidatorUtils.validateRequiredFields(dialog, txtDiemDi, txtDiemDen, txtMaMayBay)) {
            return false;
        }

        Date gioKhoiHanh = (Date) spinnerGioKhoiHanh.getValue();
        Date gioDen = (Date) spinnerGioDen.getValue();

        if (!ValidatorUtils.isValidTimeRange(gioKhoiHanh, gioDen)) {
            ValidatorUtils.showErrorDialog(dialog, "Giờ khởi hành phải trước giờ đến!");
            return false;
        }

        int soGhe = (Integer) spinnerSoGhe.getValue();
        int soGheTrong = (Integer) spinnerSoGheTrong.getValue();

        if (soGheTrong > soGhe) {
            ValidatorUtils.showErrorDialog(dialog, "Số ghế trống không được lớn hơn tổng số ghế!");
            return false;
        }

        return true;
    }

    // ========== DIALOG XÓA CHUYẾN BAY ==========
    public void xoaChuyenBay() {
        int selectedRow = tableChuyenBay.getSelectedRow();
        if (selectedRow == -1) {
            ValidatorUtils.showWarningDialog(mainGUI, "Vui lòng chọn một chuyến bay để xóa!");
            return;
        }

        String maChuyen = (String) tableChuyenBay.getValueAt(selectedRow, 0);
        String diemDi = (String) tableChuyenBay.getValueAt(selectedRow, 1);
        String diemDen = (String) tableChuyenBay.getValueAt(selectedRow, 2);
        String trangThai = (String) tableChuyenBay.getValueAt(selectedRow, 6);
        
        ChuyenBay cbCanXoa = quanLy.getDsChuyenBay().timKiemTheoMa(maChuyen);

        if (cbCanXoa == null) {
            ValidatorUtils.showErrorDialog(mainGUI, "Không tìm thấy thông tin chuyến bay!");
            return;
        }

        // Kiểm tra trạng thái chuyến bay - CHỈ cho phép xóa nếu trạng thái là HỦY
        if (!trangThai.equals(ChuyenBay.TRANG_THAI_HUY)) {
            ValidatorUtils.showErrorDialog(mainGUI,
                "Chỉ có thể xóa chuyến bay có trạng thái HỦY!\n" +
                "Trạng thái hiện tại: " + trangThai + "\n\n" +
                "Vui lòng chuyển trạng thái chuyến bay sang HỦY trước khi xóa.");
            return;
        }

        // Hiển thị dialog xác nhận
        int confirm = JOptionPane.showConfirmDialog(mainGUI,
            "Bạn có chắc chắn muốn xóa chuyến bay này?\n\n" +
            "Mã chuyến: " + maChuyen + "\n" +
            "Lộ trình: " + diemDi + " → " + diemDen + "\n" +
            "Trạng thái: " + trangThai + "\n\n" +
            "Thao tác này không thể hoàn tác!",
            "Xác nhận xóa chuyến bay",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean xoaThanhCong = quanLy.xoaChuyenBay(maChuyen);
                
                if (xoaThanhCong) {
                    ValidatorUtils.showSuccessDialog(mainGUI, 
                        "Xóa chuyến bay thành công!\n\n" +
                        "Mã chuyến: " + maChuyen + "\n" +
                        "Lộ trình: " + diemDi + " → " + diemDen);
                    
                    mainGUI.capNhatDuLieuGUI();
                } else {
                    ValidatorUtils.showErrorDialog(mainGUI, "Không thể xóa chuyến bay!");
                }
                
            } catch (Exception ex) {
                ValidatorUtils.showErrorDialog(mainGUI, "Lỗi khi xóa chuyến bay: " + ex.getMessage());
            }
        }
    }
}