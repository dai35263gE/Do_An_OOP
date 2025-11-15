package Main.dialogs;

import java.awt.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import Main.MainGUI;
import Main.utils.GUIUtils;
import Main.utils.ValidatorUtils;
import Sevice.QuanLyBanVeMayBay;
import model.ChuyenBay;

@SuppressWarnings("unchecked")
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
    try {
      System.out.println("Đang mở dialog thêm chuyến bay...");
      JDialog dialog = new JDialog(mainGUI, "Thêm Chuyến Bay Mới", true);
      dialog.setSize(600, 700);
      dialog.setLocationRelativeTo(mainGUI);
      dialog.setLayout(new BorderLayout(10, 10));
      dialog.getContentPane().setBackground(new Color(245, 245, 245));

      // Header
      JPanel headerPanel = new JPanel(new BorderLayout());
      headerPanel.setBackground(new Color(70, 130, 180));
      headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

      JLabel lblTitle = new JLabel("THÊM CHUYẾN BAY MỚI");
      lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
      lblTitle.setForeground(Color.WHITE);
      headerPanel.add(lblTitle, BorderLayout.WEST);

      JLabel lblSubTitle = new JLabel("Điền đầy đủ thông tin bên dưới");
      lblSubTitle.setFont(new Font("Arial", Font.PLAIN, 12));
      lblSubTitle.setForeground(new Color(200, 220, 240));
      headerPanel.add(lblSubTitle, BorderLayout.EAST);

      // Main content panel
      JPanel mainContent = new JPanel(new BorderLayout(10, 10));
      mainContent.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
      mainContent.setBackground(Color.WHITE);

      JPanel formPanel = new JPanel(new GridBagLayout());
      formPanel.setBackground(Color.WHITE);
      formPanel.setBorder(BorderFactory.createTitledBorder(
          BorderFactory.createLineBorder(new Color(200, 220, 240), 1),
          "THÔNG TIN CHUYẾN BAY",
          TitledBorder.LEFT,
          TitledBorder.TOP,
          new Font("Arial", Font.BOLD, 12),
          new Color(70, 130, 180)));

      GridBagConstraints gbc = new GridBagConstraints();
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.insets = new Insets(8, 8, 8, 8);
      gbc.gridx = 0;
      gbc.gridy = 0;

      // ComboBox cho điểm đi và điểm đến
      String[] diaDiem = { "Hà Nội (HAN)", "TP.HCM (SGN)", "Đà Nẵng (DAD)", "Nha Trang (CXR)", "Phú Quốc (PQC)",
          "Huế (HUI)" };
      JComboBox<String> cbDiemDi = createStyledComboBox(diaDiem);
      JComboBox<String> cbDiemDen = createStyledComboBox(diaDiem);
      cbDiemDen.setSelectedIndex(1); // Mặc định chọn điểm đến khác điểm đi

      // Spinner cho giờ khởi hành và giờ đến
      JSpinner spinnerGioKhoiHanh = createTimeSpinner();
      JSpinner spinnerGioDen = createTimeSpinner();

      // Đặt giờ mặc định
      setDefaultTimes(spinnerGioKhoiHanh, spinnerGioDen);

      JSpinner spinnerSoGhe = GUIUtils.createNumberSpinner(150, 50, 500, 10);

      // ComboBox cho mã máy bay
      String[] mayBay = { "VN-A321", "VN-B787", "VN-A350", "VN-A320", "VN-B777" };
      JComboBox<String> cbMaMayBay = createStyledComboBox(mayBay);

      JSpinner spinnerGiaCoBan = GUIUtils.createNumberSpinner(1500000.0, 500000.0, 50000000.0, 100000.0);
      stylePriceSpinner(spinnerGiaCoBan);

      // Tự động tạo mã chuyến bay
      int soChuyenBayHienTai = quanLy.getDsChuyenBay().demSoLuong();
      String maChuyenTuDong = "CB" + String.format("%03d", soChuyenBayHienTai + 1);
      JTextField txtMaChuyen = createStyledTextField(maChuyenTuDong, false);

      // Thêm components vào panel với label có icon
      addFormRowWithIcon(formPanel, gbc, "Mã chuyến bay:", txtMaChuyen);
      addFormRowWithIcon(formPanel, gbc, "Điểm đi:*", cbDiemDi);
      addFormRowWithIcon(formPanel, gbc, "Điểm đến:*", cbDiemDen);
      addFormRowWithIcon(formPanel, gbc, "Giờ khởi hành:*", spinnerGioKhoiHanh);
      addFormRowWithIcon(formPanel, gbc, "Giờ đến:*", spinnerGioDen);
      addFormRowWithIcon(formPanel, gbc, "Số ghế:*", spinnerSoGhe);
      addFormRowWithIcon(formPanel, gbc, "Mã máy bay:*", cbMaMayBay);
      addFormRowWithIcon(formPanel, gbc, "Giá cơ bản:*", spinnerGiaCoBan);

      // Panel hiển thị thông tin
      JPanel panelThongTin = new JPanel(new BorderLayout());
      panelThongTin.setBorder(BorderFactory.createTitledBorder(
          BorderFactory.createLineBorder(new Color(60, 179, 113), 1),
          "THÔNG TIN CHUYẾN BAY",
          TitledBorder.LEFT,
          TitledBorder.TOP,
          new Font("Arial", Font.BOLD, 12),
          new Color(60, 179, 113)));
      panelThongTin.setBackground(Color.WHITE);

      JTextArea txtThongTin = new JTextArea(8, 40);
      txtThongTin.setEditable(false);
      txtThongTin.setBackground(new Color(240, 248, 255));
      txtThongTin.setForeground(new Color(70, 130, 180));
      txtThongTin.setFont(new Font("Consolas", Font.PLAIN, 12));
      txtThongTin.setMargin(new Insets(15, 15, 15, 15));
      txtThongTin.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      panelThongTin.add(new JScrollPane(txtThongTin), BorderLayout.CENTER);

      // Cập nhật thông tin khi thay đổi dữ liệu
      Runnable updateChuyenBayInfo = () -> {
        updateChuyenBayInfo(txtMaChuyen, cbDiemDi, cbDiemDen, spinnerGioKhoiHanh,
            spinnerGioDen, spinnerSoGhe, cbMaMayBay, spinnerGiaCoBan, txtThongTin);
      };

      // Thêm listeners
      addChuyenBayListeners(cbDiemDi, cbDiemDen, spinnerGioKhoiHanh, spinnerGioDen, spinnerSoGhe, cbMaMayBay,
          spinnerGiaCoBan, updateChuyenBayInfo);

      // Gọi lần đầu
      updateChuyenBayInfo.run();

      // Panel button
      JPanel panelButton = createButtonPanel(dialog, txtMaChuyen, cbDiemDi, cbDiemDen,
          spinnerGioKhoiHanh, spinnerGioDen, spinnerSoGhe,
          cbMaMayBay, spinnerGiaCoBan, updateChuyenBayInfo);

      // Sắp xếp layout
      mainContent.add(formPanel, BorderLayout.NORTH);
      mainContent.add(panelThongTin, BorderLayout.CENTER);

      dialog.add(headerPanel, BorderLayout.NORTH);
      dialog.add(mainContent, BorderLayout.CENTER);
      dialog.add(panelButton, BorderLayout.SOUTH);

      dialog.setVisible(true);
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Lỗi khi mở dialog thêm chuyến bay: " + e.getMessage());
      JOptionPane.showMessageDialog(mainGUI, "Không thể mở dialog thêm chuyến bay!\nLỗi: " + e.getMessage(),
          "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
  }

  // ========== PHƯƠNG THỨC HỖ TRỢ ==========

  private JTextField createStyledTextField(String text, boolean editable) {
    JTextField textField = new JTextField(text, 20);
    textField.setEditable(editable);
    textField.setBackground(editable ? Color.WHITE : new Color(240, 240, 240));
    textField.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(200, 200, 200)),
        BorderFactory.createEmptyBorder(5, 8, 5, 8)));
    textField.setFont(new Font("Arial", Font.PLAIN, 12));
    return textField;
  }

  private JComboBox<String> createStyledComboBox(String[] items) {
    JComboBox<String> comboBox = new JComboBox<>(items);
    comboBox.setBackground(Color.WHITE);
    comboBox.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(200, 200, 200)),
        BorderFactory.createEmptyBorder(0,0,0,0)));
    comboBox.setFont(new Font("Arial", Font.PLAIN, 12));
    return comboBox;
  }

  private JSpinner createTimeSpinner() {
    JSpinner spinner = new JSpinner(new SpinnerDateModel());
    JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "dd/MM/yyyy HH:mm");
    spinner.setEditor(editor);
    spinner.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(200, 200, 200)),
        BorderFactory.createEmptyBorder(5, 8, 5, 8)));
    return spinner;
  }

  private void setDefaultTimes(JSpinner spinnerKhoiHanh, JSpinner spinnerDen) {
    Calendar calKhoiHanh = Calendar.getInstance();
    calKhoiHanh.add(Calendar.DAY_OF_MONTH, 1);
    calKhoiHanh.set(Calendar.HOUR_OF_DAY, 6);
    calKhoiHanh.set(Calendar.MINUTE, 0);
    spinnerKhoiHanh.setValue(calKhoiHanh.getTime());

    Calendar calDen = (Calendar) calKhoiHanh.clone();
    calDen.add(Calendar.HOUR_OF_DAY, 2);
    spinnerDen.setValue(calDen.getTime());
  }

  private void stylePriceSpinner(JSpinner spinner) {
    JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinner, "#,##0 VND");
    spinner.setEditor(editor);
  }

  private void addFormRowWithIcon(JPanel panel, GridBagConstraints gbc, String label, JComponent component) {
    JLabel lbl = new JLabel(label);
    lbl.setFont(new Font("Arial", Font.BOLD, 12));
    lbl.setForeground(new Color(60, 60, 60));

    gbc.gridx = 0;
    gbc.weightx = 0.3;
    panel.add(lbl, gbc);

    gbc.gridx = 1;
    gbc.weightx = 0.7;
    panel.add(component, gbc);

    gbc.gridy++;
  }

  private void updateChuyenBayInfo(JTextField txtMaChuyen, JComboBox<String> cbDiemDi,
      JComboBox<String> cbDiemDen, JSpinner spinnerGioKhoiHanh,
      JSpinner spinnerGioDen, JSpinner spinnerSoGhe,
      JComboBox<String> cbMaMayBay, JSpinner spinnerGiaCoBan,
      JTextArea txtThongTin) {
    try {
      String maChuyen = txtMaChuyen.getText().trim();
      String diemDi = (String) cbDiemDi.getSelectedItem();
      String diemDen = (String) cbDiemDen.getSelectedItem();
      Date gioKhoiHanh = (Date) spinnerGioKhoiHanh.getValue();
      Date gioDen = (Date) spinnerGioDen.getValue();
      int soGhe = ((Double) spinnerSoGhe.getValue()).intValue();
      String maMayBay = (String) cbMaMayBay.getSelectedItem();
      double giaCoBan = (Double) spinnerGiaCoBan.getValue();

      SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
      long thoiGianBay = (gioDen.getTime() - gioKhoiHanh.getTime()) / (1000 * 60);

      // Tính khoảng cách giả lập dựa trên điểm đi và điểm đến
      double khoangCach = tinhKhoangCach(diemDi, diemDen);

      String info = String.format(
          "🔹 Mã chuyến: %s\n" +
              "Lộ trình: %s → %s\n" +
              "Khởi hành: %s\n" +
              "Đến nơi: %s\n" +
              "Thời gian bay: %d phút\n" +
              "Số ghế: %d\n" +
              "Máy bay: %s\n" +
              "Giá cơ bản: %s VND\n" +
              "Khoảng cách: %.0f km\n" +
              "Trạng thái: %s",
          maChuyen,
          diemDi, diemDen,
          sdf.format(gioKhoiHanh),
          sdf.format(gioDen),
          thoiGianBay,
          soGhe,
          maMayBay,
          String.format("%,.0f", giaCoBan),
          khoangCach,
          "CHƯA BAY");

      txtThongTin.setText(info);
    } catch (Exception ex) {
      txtThongTin.setText("🔄 Đang cập nhật thông tin...");
    }
  }

  private double tinhKhoangCach(String diemDi, String diemDen) {
    // Giả lập khoảng cách giữa các sân bay
    Map<String, Map<String, Double>> distances = new HashMap<>();

    // Khoảng cách từ Hà Nội
    Map<String, Double> hanDistances = new HashMap<>();
    hanDistances.put("TP.HCM (SGN)", 1160.0);
    hanDistances.put("Đà Nẵng (DAD)", 600.0);
    hanDistances.put("Nha Trang (CXR)", 1080.0);
    hanDistances.put("Phú Quốc (PQC)", 1200.0);
    hanDistances.put("Huế (HUI)", 500.0);
    distances.put("Hà Nội (HAN)", hanDistances);

    // Khoảng cách từ TP.HCM
    Map<String, Double> sgnDistances = new HashMap<>();
    sgnDistances.put("Hà Nội (HAN)", 1160.0);
    sgnDistances.put("Đà Nẵng (DAD)", 600.0);
    sgnDistances.put("Nha Trang (CXR)", 350.0);
    sgnDistances.put("Phú Quốc (PQC)", 300.0);
    sgnDistances.put("Huế (HUI)", 700.0);
    distances.put("TP.HCM (SGN)", sgnDistances);

    Double distance = distances.getOrDefault(diemDi, new HashMap<>()).get(diemDen);
    return distance != null ? distance : 500.0; // Mặc định 500km
  }

  private void addChuyenBayListeners(JComboBox<String> cbDiemDi, JComboBox<String> cbDiemDen,
      JSpinner spinnerGioKhoiHanh, JSpinner spinnerGioDen,
      JSpinner spinnerSoGhe, JComboBox<String> cbMaMayBay,
      JSpinner spinnerGiaCoBan, Runnable updateAction) {
    cbDiemDi.addActionListener(e -> updateAction.run());
    cbDiemDen.addActionListener(e -> updateAction.run());
    cbMaMayBay.addActionListener(e -> updateAction.run());
    spinnerGioKhoiHanh.addChangeListener(e -> updateAction.run());
    spinnerGioDen.addChangeListener(e -> updateAction.run());
    spinnerSoGhe.addChangeListener(e -> updateAction.run());
    spinnerGiaCoBan.addChangeListener(e -> updateAction.run());
  }

  private JPanel createButtonPanel(JDialog dialog, JTextField txtMaChuyen,
      JComboBox<String> cbDiemDi, JComboBox<String> cbDiemDen,
      JSpinner spinnerGioKhoiHanh, JSpinner spinnerGioDen,
      JSpinner spinnerSoGhe, JComboBox<String> cbMaMayBay,
      JSpinner spinnerGiaCoBan, Runnable updateAction) {
    JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
    panelButton.setBackground(Color.WHITE);
    panelButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JButton btnThem = createStyledButton("Thêm Chuyến Bay", new Color(60, 179, 113));
    JButton btnHuy = createStyledButton("Hủy", new Color(220, 53, 69));
    JButton btnLamMoi = createStyledButton("Làm Mới", new Color(255, 165, 0));

    btnThem.addActionListener(e -> {
      if (!validateThemChuyenBay(dialog, cbDiemDi, cbDiemDen, spinnerGioKhoiHanh, spinnerGioDen)) {
        return;
      }
      handleThemChuyenBay(dialog, txtMaChuyen, cbDiemDi, cbDiemDen, spinnerGioKhoiHanh,
          spinnerGioDen, spinnerSoGhe, cbMaMayBay, spinnerGiaCoBan);
    });

    btnLamMoi.addActionListener(e -> {
      resetFormThemChuyenBay(txtMaChuyen, cbDiemDi, cbDiemDen, spinnerGioKhoiHanh,
          spinnerGioDen, spinnerSoGhe, cbMaMayBay, spinnerGiaCoBan);
      ValidatorUtils.showSuccessDialog(dialog, "Đã làm mới form với mã chuyến bay mới!");
    });

    btnHuy.addActionListener(e -> dialog.dispose());

    panelButton.add(btnThem);
    panelButton.add(btnLamMoi);
    panelButton.add(btnHuy);

    return panelButton;
  }

  private JButton createStyledButton(String text, Color backgroundColor) {
    JButton button = new JButton(text);
    button.setBackground(backgroundColor);
    button.setForeground(Color.WHITE);
    button.setFont(new Font("Arial", Font.BOLD, 12));
    button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    button.setFocusPainted(false);
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));

    // Hiệu ứng hover
    button.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        button.setBackground(backgroundColor.darker());
      }

      public void mouseExited(java.awt.event.MouseEvent evt) {
        button.setBackground(backgroundColor);
      }
    });

    return button;
  }

  private void handleThemChuyenBay(JDialog dialog, JTextField txtMaChuyen,
      JComboBox<String> cbDiemDi, JComboBox<String> cbDiemDen,
      JSpinner spinnerGioKhoiHanh, JSpinner spinnerGioDen,
      JSpinner spinnerSoGhe, JComboBox<String> cbMaMayBay,
      JSpinner spinnerGiaCoBan) {
    try {
      String maChuyen = txtMaChuyen.getText().trim();
      String diemDi = (String) cbDiemDi.getSelectedItem();
      String diemDen = (String) cbDiemDen.getSelectedItem();
      Date gioKhoiHanh = (Date) spinnerGioKhoiHanh.getValue();
      Date gioDen = (Date) spinnerGioDen.getValue();
      int soGhe = ((Double) spinnerSoGhe.getValue()).intValue();
      String maMayBay = (String) cbMaMayBay.getSelectedItem();
      double giaCoBan = (Double) spinnerGiaCoBan.getValue();

      // Kiểm tra mã chuyến bay đã tồn tại chưa
      if (quanLy.getDsChuyenBay().timKiemTheoMa(maChuyen) != null) {
        ValidatorUtils.showErrorDialog(dialog, "❌ Mã chuyến bay đã tồn tại!");
        return;
      }

      // Tạo chuyến bay mới
      // soGhe is now computed from maMayBay suffix, so pass soGhe as initial soGheTrong
      ChuyenBay chuyenBayMoi = new ChuyenBay(
          maChuyen, diemDi, diemDen, gioKhoiHanh, gioDen,
          soGhe, maMayBay, giaCoBan);

      // Thêm vào danh sách
      quanLy.themChuyenBay(chuyenBayMoi);

      // Hiển thị thông báo thành công
      String message = String.format(
          "✅ Thêm chuyến bay thành công!\n\n" +
              "🔹 Mã chuyến: %s\n" +
              "🛫 Lộ trình: %s → %s\n" +
              "⏰ Khởi hành: %s\n" +
              "💺 Số ghế: %d\n" +
              "✈️ Máy bay: %s\n" +
              "💰 Giá cơ bản: %s VND",
          maChuyen, diemDi, diemDen,
          new SimpleDateFormat("dd/MM/yyyy HH:mm").format(gioKhoiHanh),
          soGhe,
          maMayBay,
          String.format("%,.0f", giaCoBan));

      ValidatorUtils.showSuccessDialog(dialog, message);


      quanLy.ghiDuLieuRaFile();

      // Đóng dialog và cập nhật giao diện
      dialog.dispose();
      mainGUI.capNhatDuLieuGUI();

    } catch (Exception ex) {
      ValidatorUtils.showErrorDialog(dialog, "❌ Lỗi: " + ex.getMessage());
    }
  }

  private void resetFormThemChuyenBay(JTextField txtMaChuyen, JComboBox<String> cbDiemDi,
      JComboBox<String> cbDiemDen, JSpinner spinnerGioKhoiHanh,
      JSpinner spinnerGioDen, JSpinner spinnerSoGhe,
      JComboBox<String> cbMaMayBay, JSpinner spinnerGiaCoBan) {
    // Tạo mã chuyến bay mới
    int soChuyenBayMoi = quanLy.getDsChuyenBay().demSoLuong();
    String maChuyenMoi = "CB" + String.format("%03d", soChuyenBayMoi + 1);
    txtMaChuyen.setText(maChuyenMoi);

    // Reset các combobox
    cbDiemDi.setSelectedIndex(0);
    cbDiemDen.setSelectedIndex(1);

    // Reset thời gian
    setDefaultTimes(spinnerGioKhoiHanh, spinnerGioDen);

    // Reset các giá trị khác
    spinnerSoGhe.setValue(150);
    cbMaMayBay.setSelectedIndex(0);
    // Không reset giá cơ bản để giữ giá trị người dùng đã nhập
    // spinnerGiaCoBan.setValue(1500000.0);
  }

  private boolean validateThemChuyenBay(JDialog dialog, JComboBox<String> cbDiemDi,
      JComboBox<String> cbDiemDen, JSpinner spinnerGioKhoiHanh,
      JSpinner spinnerGioDen) {
    String diemDi = (String) cbDiemDi.getSelectedItem();
    String diemDen = (String) cbDiemDen.getSelectedItem();

    if (diemDi.equals(diemDen)) {
      ValidatorUtils.showErrorDialog(dialog, "❌ Điểm đi và điểm đến không được trùng nhau!");
      return false;
    }

    Date gioKhoiHanh = (Date) spinnerGioKhoiHanh.getValue();
    Date gioDen = (Date) spinnerGioDen.getValue();

    if (!ValidatorUtils.isValidTimeRange(gioKhoiHanh, gioDen)) {
      ValidatorUtils.showErrorDialog(dialog, "❌ Giờ khởi hành phải trước giờ đến!");
      return false;
    }

    if (!ValidatorUtils.isFutureTime(gioKhoiHanh)) {
      ValidatorUtils.showErrorDialog(dialog, "❌ Giờ khởi hành phải trong tương lai!");
      return false;
    }

    return true;
  }

  // ========== DIALOG SỬA CHUYẾN BAY ==========
  public void moDialogSuaChuyenBay() {
    int selectedRow = tableChuyenBay.getSelectedRow();
    if (selectedRow == -1) {
      ValidatorUtils.showWarningDialog(mainGUI, "⚠️ Vui lòng chọn một chuyến bay để sửa!");
      return;
    }

    String maChuyen = (String) tableChuyenBay.getValueAt(selectedRow, 0);
    ChuyenBay cbCanSua = quanLy.getDsChuyenBay().timKiemTheoMa(maChuyen);

    if (cbCanSua == null) {
      ValidatorUtils.showErrorDialog(mainGUI, "❌ Không tìm thấy thông tin chuyến bay!");
      return;
    }

    JDialog dialog = new JDialog(mainGUI, "Sửa Thông Tin Chuyến Bay - " + maChuyen, true);
    dialog.setSize(700, 800);
    dialog.setLocationRelativeTo(mainGUI);
    dialog.setLayout(new BorderLayout(10, 10));
    dialog.getContentPane().setBackground(new Color(245, 245, 245));

    // Header
    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setBackground(new Color(70, 130, 180));
    headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

    JLabel lblTitle = new JLabel("✏️ SỬA THÔNG TIN CHUYẾN BAY");
    lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
    lblTitle.setForeground(Color.WHITE);
    headerPanel.add(lblTitle, BorderLayout.WEST);

    JLabel lblSubTitle = new JLabel("Mã: " + maChuyen);
    lblSubTitle.setFont(new Font("Arial", Font.PLAIN, 12));
    lblSubTitle.setForeground(new Color(200, 220, 240));
    headerPanel.add(lblSubTitle, BorderLayout.EAST);

    // Main content
    JPanel mainContent = new JPanel(new BorderLayout(10, 10));
    mainContent.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    mainContent.setBackground(Color.WHITE);

    // Panel form sửa
    JPanel formPanel = createSuaChuyenBayForm(cbCanSua);

    // Panel thông tin cập nhật
    JPanel panelThongTinCapNhat = createInfoPanel("THÔNG TIN CẬP NHẬT",
        "", new Color(60, 179, 113));

    // Panel button
    JPanel panelButton = createSuaChuyenBayButtonPanel(dialog, cbCanSua, formPanel, panelThongTinCapNhat);

    // Sắp xếp layout
    JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
    centerPanel.add(formPanel, BorderLayout.CENTER);
    centerPanel.add(panelThongTinCapNhat, BorderLayout.SOUTH);
    mainContent.add(centerPanel, BorderLayout.CENTER);

    dialog.add(headerPanel, BorderLayout.NORTH);
    dialog.add(mainContent, BorderLayout.CENTER);
    dialog.add(panelButton, BorderLayout.SOUTH);

    dialog.setVisible(true);
  }

  private JPanel createInfoPanel(String title, String content, Color color) {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(color, 1),
        title,
        TitledBorder.LEFT,
        TitledBorder.TOP,
        new Font("Arial", Font.BOLD, 12),
        color));
    panel.setBackground(Color.WHITE);

    JTextArea textArea = new JTextArea(content);
    textArea.setEditable(false);
    textArea.setBackground(new Color(240, 248, 255));
    textArea.setForeground(color);
    textArea.setFont(new Font("Consolas", Font.PLAIN, 11));
    textArea.setMargin(new Insets(10, 10, 10, 10));
    textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
    return panel;
  }

  private JPanel createSuaChuyenBayForm(ChuyenBay cbCanSua) {
    JPanel formPanel = new JPanel(new GridBagLayout());
    formPanel.setBackground(Color.WHITE);
    formPanel.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(new Color(255, 165, 0), 1),
        " THÔNG TIN CHỈNH SỬA",
        TitledBorder.LEFT,
        TitledBorder.TOP,
        new Font("Arial", Font.BOLD, 12),
        new Color(255, 165, 0)));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(8, 8, 8, 8);
    gbc.gridx = 0;
    gbc.gridy = 0;

    // Mã chuyến bay (không thể sửa)
    JTextField txtMaChuyen = createStyledTextField(cbCanSua.getMaChuyen(), false);

    // Điểm đi và điểm đến
    String[] diaDiem = { "Hà Nội (HAN)", "TP.HCM (SGN)", "Đà Nẵng (DAD)", "Nha Trang (CXR)", "Phú Quốc (PQC)",
        "Huế (HUI)" };
    JComboBox<String> cbDiemDi = createStyledComboBox(diaDiem);
    JComboBox<String> cbDiemDen = createStyledComboBox(diaDiem);

    // Chọn điểm đi và điểm đến hiện tại
    setComboBoxToValue(cbDiemDi, cbCanSua.getDiemDi());
    setComboBoxToValue(cbDiemDen, cbCanSua.getDiemDen());

    // Thời gian
    JSpinner spinnerGioKhoiHanh = createTimeSpinner();
    JSpinner spinnerGioDen = createTimeSpinner();
    spinnerGioKhoiHanh.setValue(cbCanSua.getGioKhoiHanh());
    spinnerGioDen.setValue(cbCanSua.getGioDen());

    // Số ghế (now computed from aircraft code)
    JLabel labelSoGhe = new JLabel(String.valueOf(cbCanSua.getSoGheToiDa()));
    JSpinner spinnerSoGheTrong = GUIUtils.createNumberSpinner(cbCanSua.getSoGheTrong(), 0, cbCanSua.getSoGheToiDa(), 1);

    // Máy bay
    String[] mayBay = { "VN-A321", "VN-B787", "VN-A350", "VN-A320", "VN-B777" };
    JComboBox<String> cbMaMayBay = createStyledComboBox(mayBay);
    setComboBoxToValue(cbMaMayBay, cbCanSua.getMaMayBay());

    // Giá
    JSpinner spinnerGiaCoBan = GUIUtils.createNumberSpinner(cbCanSua.getGiaCoBan(), 500000.0, 50000000.0, 100000.0);
    stylePriceSpinner(spinnerGiaCoBan);

    // Trạng thái
    JComboBox<String> cbTrangThai = createStyledComboBox(new String[] {
        ChuyenBay.TRANG_THAI_CHUA_BAY,
        ChuyenBay.TRANG_THAI_DANG_BAY,
        ChuyenBay.TRANG_THAI_DA_BAY,
        ChuyenBay.TRANG_THAI_HUY
    });
    cbTrangThai.setSelectedItem(cbCanSua.getTrangThai());

    // Thêm components vào form
    addFormRowWithIcon(formPanel, gbc, "Mã chuyến bay:", txtMaChuyen);
    addFormRowWithIcon(formPanel, gbc, "Điểm đi:*", cbDiemDi);
    addFormRowWithIcon(formPanel, gbc, "Điểm đến:*", cbDiemDen);
    addFormRowWithIcon(formPanel, gbc, "Giờ khởi hành:*", spinnerGioKhoiHanh);
    addFormRowWithIcon(formPanel, gbc, "Giờ đến:*", spinnerGioDen);
    addFormRowWithIcon(formPanel, gbc, "Tổng số ghế:*", labelSoGhe);  // Read-only, computed from aircraft code
    addFormRowWithIcon(formPanel, gbc, "Số ghế trống:*", spinnerSoGheTrong);
    addFormRowWithIcon(formPanel, gbc, "Mã máy bay:*", cbMaMayBay);
    addFormRowWithIcon(formPanel, gbc, "Giá cơ bản:*", spinnerGiaCoBan);
    addFormRowWithIcon(formPanel, gbc, "Trạng thái:*", cbTrangThai);

    // Lưu references để sử dụng sau
    formPanel.putClientProperty("components", new HashMap<String, Object>() {
      {
        put("txtMaChuyen", txtMaChuyen);
        put("cbDiemDi", cbDiemDi);
        put("cbDiemDen", cbDiemDen);
        put("spinnerGioKhoiHanh", spinnerGioKhoiHanh);
        put("spinnerGioDen", spinnerGioDen);
        put("labelSoGhe", labelSoGhe);  // Read-only label
        put("spinnerSoGheTrong", spinnerSoGheTrong);
        put("cbMaMayBay", cbMaMayBay);
        put("spinnerGiaCoBan", spinnerGiaCoBan);
        put("cbTrangThai", cbTrangThai);
      }
    });

    return formPanel;
  }

  private JPanel createSuaChuyenBayButtonPanel(JDialog dialog, ChuyenBay cbCanSua,
      JPanel formPanel, JPanel panelThongTinCapNhat) {
    JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
    panelButton.setBackground(Color.WHITE);
    panelButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JButton btnLuu = createStyledButton("Lưu Thay Đổi", new Color(60, 179, 113));
    JButton btnHuy = createStyledButton("Hủy", new Color(220, 53, 69));
    JButton btnKhoiPhuc = createStyledButton("Khôi Phục Mặc Định", new Color(255, 165, 0));

    btnLuu.addActionListener(e -> {
      handleSuaChuyenBay(dialog, cbCanSua, formPanel, panelThongTinCapNhat);
    });

    btnKhoiPhuc.addActionListener(e -> {
      khoiPhucGiaTriBanDau(formPanel, cbCanSua);
      ValidatorUtils.showSuccessDialog(dialog, " Đã khôi phục thông tin ban đầu!");
    });

    btnHuy.addActionListener(e -> dialog.dispose());

    panelButton.add(btnLuu);
    panelButton.add(btnKhoiPhuc);
    panelButton.add(btnHuy);

    return panelButton;
  }

  private void handleSuaChuyenBay(JDialog dialog, ChuyenBay cbCanSua,
      JPanel formPanel, JPanel panelThongTinCapNhat) {
    try {
      Map<String, Object> components = (Map<String, Object>) formPanel.getClientProperty("components");

      JComboBox<String> cbDiemDi = (JComboBox<String>) components.get("cbDiemDi");
      JComboBox<String> cbDiemDen = (JComboBox<String>) components.get("cbDiemDen");
      JSpinner spinnerGioKhoiHanh = (JSpinner) components.get("spinnerGioKhoiHanh");
      JSpinner spinnerGioDen = (JSpinner) components.get("spinnerGioDen");
      JSpinner spinnerSoGhe = (JSpinner) components.get("spinnerSoGhe");
      JSpinner spinnerSoGheTrong = (JSpinner) components.get("spinnerSoGheTrong");
      JComboBox<String> cbMaMayBay = (JComboBox<String>) components.get("cbMaMayBay");
      JSpinner spinnerGiaCoBan = (JSpinner) components.get("spinnerGiaCoBan");
      JComboBox<String> cbTrangThai = (JComboBox<String>) components.get("cbTrangThai");

      // Validate dữ liệu
      if (!validateSuaChuyenBay(dialog, cbDiemDi, cbDiemDen, spinnerGioKhoiHanh,
          spinnerGioDen, spinnerSoGhe, spinnerSoGheTrong)) {
        return;
      }

      // Lấy thông tin từ form
      String diemDi = (String) cbDiemDi.getSelectedItem();
      String diemDen = (String) cbDiemDen.getSelectedItem();
      Date gioKhoiHanh = (Date) spinnerGioKhoiHanh.getValue();
      Date gioDen = (Date) spinnerGioDen.getValue();
      double soGhe = (Double) spinnerSoGhe.getValue();
      double soGheTrong = (Double) spinnerSoGheTrong.getValue();
      String maMayBay = (String) cbMaMayBay.getSelectedItem();
      double giaCoBan = (Double) spinnerGiaCoBan.getValue();
      String trangThai = (String) cbTrangThai.getSelectedItem();

      // Tạo đối tượng chuyến bay mới với thông tin cập nhật
      // soGhe is now computed from maMayBay suffix, so only pass soGheTrong
      ChuyenBay chuyenBayMoi = new ChuyenBay(
          cbCanSua.getMaChuyen(), // Giữ nguyên mã chuyến
          diemDi, diemDen, gioKhoiHanh, gioDen,
          (int) soGheTrong, maMayBay, giaCoBan);
      chuyenBayMoi.setTrangThai(trangThai);

      // Cập nhật chuyến bay qua service layer
      if (!quanLy.suaChuyenBay(cbCanSua.getMaChuyen(), chuyenBayMoi)) {
        ValidatorUtils.showErrorDialog(dialog, "Không thể cập nhật chuyến bay!");
        return;
      }

      // Hiển thị thông báo thành công
      String message = String.format(
          "Cập nhật chuyến bay thành công!\n\n" +
              "Mã chuyến: %s\n" +
              "Lộ trình: %s → %s\n" +
              "Khởi hành: %s\n" +
              "Số ghế: %.0f/%.0f\n" +
              "Giá cơ bản: %s VND\n" +
              "Trạng thái: %s",
          cbCanSua.getMaChuyen(),
          diemDi, diemDen,
          new SimpleDateFormat("dd/MM/yyyy HH:mm").format(gioKhoiHanh),
          soGhe - soGheTrong, soGhe,
          String.format("%,.0f", giaCoBan),
          trangThai);

      ValidatorUtils.showSuccessDialog(dialog, message);

      quanLy.ghiDuLieuRaFile();

      // Đóng dialog và cập nhật giao diện
      dialog.dispose();
      mainGUI.capNhatDuLieuGUI();

    } catch (Exception ex) {
      ValidatorUtils.showErrorDialog(dialog, " Lỗi: " + ex.getMessage());
      ex.printStackTrace();
    }
  }

  private boolean validateSuaChuyenBay(JDialog dialog, JComboBox<String> cbDiemDi,
      JComboBox<String> cbDiemDen, JSpinner spinnerGioKhoiHanh,
      JSpinner spinnerGioDen, JSpinner spinnerSoGhe,
      JSpinner spinnerSoGheTrong) {
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

    double soGhe = (Double) spinnerSoGhe.getValue();
    double soGheTrong = (Double) spinnerSoGheTrong.getValue();

    if (soGheTrong > soGhe) {
      ValidatorUtils.showErrorDialog(dialog, "Số ghế trống không được lớn hơn tổng số ghế!");
      return false;
    }

    if (soGheTrong < 0) {
      ValidatorUtils.showErrorDialog(dialog, " Số ghế trống không được âm!");
      return false;
    }

    return true;
  }

  private void khoiPhucGiaTriBanDau(JPanel formPanel, ChuyenBay cbCanSua) {
    Map<String, Object> components = (Map<String, Object>) formPanel.getClientProperty("components");

    JComboBox<String> cbDiemDi = (JComboBox<String>) components.get("cbDiemDi");
    JComboBox<String> cbDiemDen = (JComboBox<String>) components.get("cbDiemDen");
    JSpinner spinnerGioKhoiHanh = (JSpinner) components.get("spinnerGioKhoiHanh");
    JSpinner spinnerGioDen = (JSpinner) components.get("spinnerGioDen");
    JLabel labelSoGhe = (JLabel) components.get("labelSoGhe");  // Read-only
    JSpinner spinnerSoGheTrong = (JSpinner) components.get("spinnerSoGheTrong");
    JComboBox<String> cbMaMayBay = (JComboBox<String>) components.get("cbMaMayBay");
    JSpinner spinnerGiaCoBan = (JSpinner) components.get("spinnerGiaCoBan");
    JComboBox<String> cbTrangThai = (JComboBox<String>) components.get("cbTrangThai");

    // Khôi phục về giá trị ban đầu
    setComboBoxToValue(cbDiemDi, cbCanSua.getDiemDi());
    setComboBoxToValue(cbDiemDen, cbCanSua.getDiemDen());
    spinnerGioKhoiHanh.setValue(cbCanSua.getGioKhoiHanh());
    spinnerGioDen.setValue(cbCanSua.getGioDen());
    labelSoGhe.setText(String.valueOf(cbCanSua.getSoGheToiDa()));
    spinnerSoGheTrong.setValue(cbCanSua.getSoGheTrong());
    setComboBoxToValue(cbMaMayBay, cbCanSua.getMaMayBay());
    spinnerGiaCoBan.setValue(cbCanSua.getGiaCoBan());
    cbTrangThai.setSelectedItem(cbCanSua.getTrangThai());
  }

  private void setComboBoxToValue(JComboBox<String> comboBox, String value) {
    for (int i = 0; i < comboBox.getItemCount(); i++) {
      if (comboBox.getItemAt(i).equals(value)) {
        comboBox.setSelectedIndex(i);
        return;
      }
    }
    // Nếu không tìm thấy, chọn item đầu tiên
    if (comboBox.getItemCount() > 0) {
      comboBox.setSelectedIndex(0);
    }
  }

  // ========== XÓA CHUYẾN BAY ==========
  public void xoaChuyenBay() {
    int selectedRow = tableChuyenBay.getSelectedRow();
    if (selectedRow == -1) {
      ValidatorUtils.showWarningDialog(mainGUI, "⚠️ Vui lòng chọn một chuyến bay để xóa!");
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

    // Kiểm tra trạng thái chuyến bay
    if (!trangThai.equals(ChuyenBay.TRANG_THAI_HUY)) {
      ValidatorUtils.showErrorDialog(mainGUI,
          "Chỉ có thể xóa chuyến bay có trạng thái HỦY!\n" +
              "📊 Trạng thái hiện tại: " + trangThai + "\n\n" +
              "💡 Vui lòng chuyển trạng thái chuyến bay sang HỦY trước khi xóa.");
      return;
    }

    // Hiển thị dialog xác nhận với icon và styling
    JPanel messagePanel = new JPanel(new BorderLayout(10, 10));
    messagePanel.setBackground(Color.WHITE);

    JLabel iconLabel = new JLabel("⚠️", JLabel.CENTER);
    iconLabel.setFont(new Font("Arial", Font.BOLD, 24));
    iconLabel.setForeground(new Color(255, 193, 7));

    JTextArea messageArea = new JTextArea(
        "Bạn có chắc chắn muốn xóa chuyến bay này?\n\n" +
            "Mã chuyến: " + maChuyen + "\n" +
            "Lộ trình: " + diemDi + " → " + diemDen + "\n" +
            "Trạng thái: " + trangThai + "\n\n" +
            "Thao tác này không thể hoàn tác!");
    messageArea.setEditable(false);
    messageArea.setBackground(Color.WHITE);
    messageArea.setFont(new Font("Arial", Font.PLAIN, 12));
    messageArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    messagePanel.add(iconLabel, BorderLayout.WEST);
    messagePanel.add(messageArea, BorderLayout.CENTER);

    int confirm = JOptionPane.showConfirmDialog(mainGUI,
        messagePanel,
        "Xác nhận xóa chuyến bay",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE);

    if (confirm == JOptionPane.YES_OPTION) {
      try {
        boolean xoaThanhCong = quanLy.xoaChuyenBay(maChuyen);

        if (xoaThanhCong) {
          ValidatorUtils.showSuccessDialog(mainGUI,
              "Xóa chuyến bay thành công!\n\n" +
                  "Mã chuyến: " + maChuyen + "\n" +
                  "Lộ trình: " + diemDi + " → " + diemDen);

          quanLy.ghiDuLieuRaFile();

          mainGUI.capNhatDuLieuGUI();
        } else {
          ValidatorUtils.showErrorDialog(mainGUI, " Không thể xóa chuyến bay!");
        }

      } catch (Exception ex) {
        ValidatorUtils.showErrorDialog(mainGUI, " Lỗi khi xóa chuyến bay: " + ex.getMessage());
      }
    }
  }
  // ========== DIALOG TÌM KIẾM CHUYẾN BAY ==========
public void moDialogTimKiemChuyenBay() {
    try {
        System.out.println("Đang mở dialog tìm kiếm chuyến bay...");
        JDialog dialog = new JDialog(mainGUI, "Tìm Kiếm Chuyến Bay", true);
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(mainGUI);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getContentPane().setBackground(new Color(245, 245, 245));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("TÌM KIẾM CHUYẾN BAY");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JLabel lblSubTitle = new JLabel("Nhập thông tin tìm kiếm bên dưới");
        lblSubTitle.setFont(new Font("Arial", Font.PLAIN, 12));
        lblSubTitle.setForeground(new Color(200, 220, 240));
        headerPanel.add(lblSubTitle, BorderLayout.EAST);

        // Main content panel
        JPanel mainContent = new JPanel(new BorderLayout(10, 10));

        mainContent.setBackground(Color.WHITE);

        // Panel tìm kiếm cơ bản
        JPanel panelTimKiemCoBan = createTimKiemCoBanPanel();
        
        // Panel tìm kiếm nâng cao
        JPanel panelTimKiemNangCao = createTimKiemNangCaoPanel();


        // Tabbed pane để chuyển đổi giữa tìm kiếm cơ bản và nâng cao
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 12));
        
        tabbedPane.addTab(" Tìm Kiếm Cơ Bản", panelTimKiemCoBan);
        tabbedPane.addTab("Tìm Kiếm Nâng Cao", panelTimKiemNangCao);
        
        // Panel button
        JPanel panelButton = createTimKiemButtonPanel(dialog, tabbedPane);

        // Sắp xếp layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(tabbedPane, BorderLayout.CENTER);
        
        mainContent.add(topPanel, BorderLayout.NORTH);

        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(mainContent, BorderLayout.CENTER);
        dialog.add(panelButton, BorderLayout.SOUTH);

        dialog.setVisible(true);
    } catch (Exception e) {
        e.printStackTrace();
        System.err.println("Lỗi khi mở dialog tìm kiếm chuyến bay: " + e.getMessage());
        JOptionPane.showMessageDialog(mainGUI, "Không thể mở dialog tìm kiếm chuyến bay!\nLỗi: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}

private JPanel createTimKiemCoBanPanel() {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBackground(Color.WHITE);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(8, 8, 8, 8);
    gbc.gridx = 0;
    gbc.gridy = 0;

    // Tìm kiếm theo từ khóa
    JTextField txtTimKiem = new JTextField(20);
    txtTimKiem.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
    txtTimKiem.setFont(new Font("Arial", Font.PLAIN, 12));

    // ComboBox điểm đi và điểm đến
    String[] diaDiem = { "Tất cả", "Hà Nội (HAN)", "TP.HCM (SGN)", "Đà Nẵng (DAD)", "Nha Trang (CXR)", "Phú Quốc (PQC)", "Huế (HUI)" };
    JComboBox<String> cbDiemDi = createStyledComboBox(diaDiem);
    JComboBox<String> cbDiemDen = createStyledComboBox(diaDiem);

    // ComboBox trạng thái
    String[] trangThai = { "Tất cả", ChuyenBay.TRANG_THAI_CHUA_BAY, ChuyenBay.TRANG_THAI_DANG_BAY, 
                          ChuyenBay.TRANG_THAI_DA_BAY, ChuyenBay.TRANG_THAI_HUY };
    JComboBox<String> cbTrangThai = createStyledComboBox(trangThai);

    // Checkbox chỉ hiển thị chuyến bay còn chỗ
    JCheckBox chkConCho = new JCheckBox("Chỉ hiển thị chuyến bay còn chỗ trống");
    chkConCho.setBackground(Color.WHITE);
    chkConCho.setFont(new Font("Arial", Font.PLAIN, 12));

    // Thêm components
    addFormRowWithIcon(panel, gbc, "Từ khóa tìm kiếm:", txtTimKiem);
    addFormRowWithIcon(panel, gbc, "Điểm đi:", cbDiemDi);
    addFormRowWithIcon(panel, gbc, "Điểm đến:", cbDiemDen);
    addFormRowWithIcon(panel, gbc, "Trạng thái:", cbTrangThai);
    
    gbc.gridx = 0;
    gbc.gridwidth = 2;
    panel.add(chkConCho, gbc);
    gbc.gridwidth = 1;

    // Lưu references
    panel.putClientProperty("components", new HashMap<String, Object>() {
        {
            put("txtTimKiem", txtTimKiem);
            put("cbDiemDi", cbDiemDi);
            put("cbDiemDen", cbDiemDen);
            put("cbTrangThai", cbTrangThai);
            put("chkConCho", chkConCho);
        }
    });

    return panel;
}

private JPanel createTimKiemNangCaoPanel() {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBackground(Color.WHITE);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(8, 8, 8, 8);
    gbc.gridx = 0;
    gbc.gridy = 0;

    // Mã máy bay
    String[] mayBay = { "Tất cả", "VN-A321", "VN-B787", "VN-A350", "VN-A320", "VN-B777" };
    JComboBox<String> cbMaMayBay = createStyledComboBox(mayBay);

    // Khoảng giá
    JSpinner spinnerGiaMin = GUIUtils.createNumberSpinner(0.0, 0.0, 50000000.0, 100000.0);
    JSpinner spinnerGiaMax = GUIUtils.createNumberSpinner(50000000.0, 0.0, 50000000.0, 100000.0);
    stylePriceSpinner(spinnerGiaMin);
    stylePriceSpinner(spinnerGiaMax);

    // Khoảng thời gian
    JSpinner spinnerTuNgay = createTimeSpinner();
    JSpinner spinnerDenNgay = createTimeSpinner();
    
    // Đặt thời gian mặc định (7 ngày tới)
    Calendar cal = Calendar.getInstance();
    spinnerTuNgay.setValue(cal.getTime());
    cal.add(Calendar.DAY_OF_MONTH, 7);
    spinnerDenNgay.setValue(cal.getTime());

    // Số ghế trống tối thiểu
    JSpinner spinnerGheTrongMin = GUIUtils.createNumberSpinner(0, 0, 500, 1);

    // Thêm components
    addFormRowWithIcon(panel, gbc, "Mã máy bay:", cbMaMayBay);
    addFormRowWithIcon(panel, gbc, "Giá tối thiểu (VND):", spinnerGiaMin);
    addFormRowWithIcon(panel, gbc, "Giá tối đa (VND):", spinnerGiaMax);
    addFormRowWithIcon(panel, gbc, "Từ ngày:", spinnerTuNgay);
    addFormRowWithIcon(panel, gbc, "Đến ngày:", spinnerDenNgay);
    addFormRowWithIcon(panel, gbc, "Số ghế trống tối thiểu:", spinnerGheTrongMin);

    // Lưu references
    panel.putClientProperty("components", new HashMap<String, Object>() {
        {
            put("cbMaMayBay", cbMaMayBay);
            put("spinnerGiaMin", spinnerGiaMin);
            put("spinnerGiaMax", spinnerGiaMax);
            put("spinnerTuNgay", spinnerTuNgay);
            put("spinnerDenNgay", spinnerDenNgay);
            put("spinnerGheTrongMin", spinnerGheTrongMin);
        }
    });

    return panel;
}

private JPanel createTimKiemButtonPanel(JDialog dialog, JTabbedPane tabbedPane) {
    JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
    panelButton.setBackground(Color.WHITE);
    panelButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JButton btnTimKiem = createStyledButton("Tìm Kiếm", new Color(70, 130, 180));
    JButton btnXoaHet = createStyledButton("Xóa Hết", new Color(220, 53, 69));
    JButton btnDong = createStyledButton("Đóng", new Color(108, 117, 125));

    btnTimKiem.addActionListener(e -> {
        handleTimKiemChuyenBay(tabbedPane);
    });

    btnXoaHet.addActionListener(e -> {
        resetTimKiemForm(tabbedPane);
    });

    btnDong.addActionListener(e -> dialog.dispose());

    panelButton.add(btnTimKiem);
    panelButton.add(btnXoaHet);
    panelButton.add(btnDong);

    return panelButton;
}

private void handleTimKiemChuyenBay(JTabbedPane tabbedPane) {
    try {
        Map<String, Object> filters = new HashMap<>();
        
        // Lấy tab hiện tại
        int selectedTab = tabbedPane.getSelectedIndex();
        JPanel currentPanel = (JPanel) tabbedPane.getComponentAt(selectedTab);
        Map<String, Object> components = (Map<String, Object>) currentPanel.getClientProperty("components");

        if (selectedTab == 0) { // Tab tìm kiếm cơ bản
            String keyword = ((JTextField) components.get("txtTimKiem")).getText().trim();
            String diemDi = (String) ((JComboBox<String>) components.get("cbDiemDi")).getSelectedItem();
            String diemDen = (String) ((JComboBox<String>) components.get("cbDiemDen")).getSelectedItem();
            String trangThai = (String) ((JComboBox<String>) components.get("cbTrangThai")).getSelectedItem();
            boolean conCho = ((JCheckBox) components.get("chkConCho")).isSelected();

            if (!keyword.isEmpty()) {
                filters.put("keyword", keyword);
            }
            if (!diemDi.equals("Tất cả")) {
                filters.put("diemDi", diemDi);
            }
            if (!diemDen.equals("Tất cả")) {
                filters.put("diemDen", diemDen);
            }
            if (!trangThai.equals("Tất cả")) {
                filters.put("trangThai", trangThai);
            }
            if (conCho) {
                filters.put("conCho", true);
            }
        } else { // Tab tìm kiếm nâng cao
            String maMayBay = (String) ((JComboBox<String>) components.get("cbMaMayBay")).getSelectedItem();
            double giaMin = (Double) ((JSpinner) components.get("spinnerGiaMin")).getValue();
            double giaMax = (Double) ((JSpinner) components.get("spinnerGiaMax")).getValue();
            Date tuNgay = (Date) ((JSpinner) components.get("spinnerTuNgay")).getValue();
            Date denNgay = (Date) ((JSpinner) components.get("spinnerDenNgay")).getValue();
            int gheTrongMin = ((Double) ((JSpinner) components.get("spinnerGheTrongMin")).getValue()).intValue();

            if (!maMayBay.equals("Tất cả")) {
                filters.put("maMayBay", maMayBay);
            }
            if (giaMin > 0) {
                filters.put("giaMin", giaMin);
            }
            if (giaMax < 50000000.0) {
                filters.put("giaMax", giaMax);
            }
            filters.put("tuNgay", tuNgay);
            filters.put("denNgay", denNgay);
            if (gheTrongMin > 0) {
                filters.put("gheTrongMin", gheTrongMin);
            }
        }

        // Thực hiện tìm kiếm
        List<ChuyenBay> ketQua = quanLy.getDsChuyenBay().timKiemChuyenBay(filters);
        
        // Hiển thị kết quả lên table chính
        hienThiKetQuaLenTableChinh(ketQua);
        
        // Đóng dialog
        Window dialog = SwingUtilities.windowForComponent(tabbedPane);
        if (dialog != null) {
            dialog.dispose();
        }

        // Hiển thị thông báo
        String message = String.format("Tìm thấy %d chuyến bay phù hợp", ketQua.size());
        ValidatorUtils.showSuccessDialog(mainGUI, message);

    } catch (Exception e) {
        ValidatorUtils.showErrorDialog(mainGUI, " Lỗi khi tìm kiếm: " + e.getMessage());
        e.printStackTrace();
    }
}

private void hienThiKetQuaLenTableChinh(List<ChuyenBay> ketQua) {
    // Lấy table model từ table chính
    javax.swing.table.DefaultTableModel tableModel = (javax.swing.table.DefaultTableModel) tableChuyenBay.getModel();
    
    // Xóa dữ liệu cũ
    tableModel.setRowCount(0);

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    // Thêm dữ liệu mới từ kết quả tìm kiếm
    for (ChuyenBay cb : ketQua) {
        Object[] row = {
            cb.getMaChuyen(),
            cb.getDiemDi(),
            cb.getDiemDen(),
            sdf.format(cb.getGioKhoiHanh()),
            sdf.format(cb.getGioDen()),
            String.format("%d/%d", cb.getSoGheToiDa() - cb.getSoGheTrong(), cb.getSoGheToiDa()),
            cb.getTrangThai(),
            String.format("%,.0f VND", cb.getGiaCoBan()),
            cb.getMaMayBay()
        };
        tableModel.addRow(row);
    }
}



private void resetTimKiemForm(JTabbedPane tabbedPane) {
    // Reset cả hai tab
    for (int i = 0; i < tabbedPane.getTabCount(); i++) {
        JPanel panel = (JPanel) tabbedPane.getComponentAt(i);
        Map<String, Object> components = (Map<String, Object>) panel.getClientProperty("components");
        
        for (Object component : components.values()) {
            if (component instanceof JTextField) {
                ((JTextField) component).setText("");
            } else if (component instanceof JComboBox) {
                ((JComboBox<?>) component).setSelectedIndex(0);
            } else if (component instanceof JSpinner) {
                if (i == 0) {
                    // Tab cơ bản - không có spinner cần reset
                } else {
                    // Tab nâng cao
                    if (component == components.get("spinnerGiaMin")) {
                        ((JSpinner) component).setValue(0.0);
                    } else if (component == components.get("spinnerGiaMax")) {
                        ((JSpinner) component).setValue(50000000.0);
                    } else if (component == components.get("spinnerGheTrongMin")) {
                        ((JSpinner) component).setValue(0);
                    } else if (component == components.get("spinnerTuNgay")) {
                        ((JSpinner) component).setValue(new Date());
                    } else if (component == components.get("spinnerDenNgay")) {
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.DAY_OF_MONTH, 7);
                        ((JSpinner) component).setValue(cal.getTime());
                    }
                }
            } else if (component instanceof JCheckBox) {
                ((JCheckBox) component).setSelected(false);
            }
        }
    }
    ValidatorUtils.showSuccessDialog(mainGUI, "Đã xóa hết điều kiện tìm kiếm!");
}

}
