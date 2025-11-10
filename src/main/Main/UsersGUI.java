package Main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import Sevice.DanhSachChuyenBay;
import Sevice.DanhSachHoaDon;
import Sevice.DanhSachKhachHang;
import Sevice.DanhSachVeMayBay;
import Sevice.QuanLyBanVeMayBay;
import model.ChuyenBay;
import model.HoaDon;
import model.KhachHang;
import model.VeMayBay;
import model.VePhoThong;
import model.VeThuongGia;
import model.VeTietKiem;

public class UsersGUI extends JFrame {
  private QuanLyBanVeMayBay quanLy;
  private KhachHang khachHangDangNhap;
  private DanhSachChuyenBay dsChuyenBay;
  private DanhSachVeMayBay dsVe;
  private DanhSachHoaDon dsHoaDon;
  private DanhSachKhachHang dsKhachHang;

  // Components
  private JTabbedPane tabbedPane;
  private JLabel lblWelcome;

  // Tab Đặt vé
  private JComboBox<String> cbDiemDi, cbDiemDen, cbChuyenBay;
  private JSpinner spinnerNgayDi;
  private JButton btnTimChuyen, btnDatVe, btnXemTatCa;
  private JTable tableChuyenBay;
  private DefaultTableModel modelChuyenBay;

  // Tab Vé của tôi
  private JTable tableVeCuaToi;
  private DefaultTableModel modelVeCuaToi;
  private JButton btnXemHoaDon, btnHuyVe, btnXemChiTietVe;

  // Tab Lịch sử
  private JTable tableLichSu;
  private DefaultTableModel modelLichSu;

  // Tab Thông tin
  private JTextField txtHoTen, txtEmail, txtSoDT, txtDiaChi, txtCmnd, txtNgaySinh;
  private JComboBox<String> cbGioiTinh;
  private JButton btnCapNhatThongTin;
  private JLabel lblDiemTichLuy, lblHangKhachHang;

  public UsersGUI(QuanLyBanVeMayBay quanLy) {

    this.quanLy = quanLy;
    quanLy.docDuLieuTuFile();
    this.dsChuyenBay = quanLy.getDsChuyenBay();
    this.dsVe = quanLy.getDsVe();
    this.dsHoaDon = quanLy.getDsHoaDon();
    this.dsKhachHang = quanLy.getDsKhachHang();
    initComponents();
    setupLayout();
    setupEvents();
  }

  public boolean dangNhap(String maKH, String matKhau) {
    khachHangDangNhap = dsKhachHang.timKiemTheoMa(maKH);
    if (khachHangDangNhap.dangNhap(maKH, matKhau)) {
      lblWelcome.setText("Xin chào, " + khachHangDangNhap.getHoTen() + "! - Hạng: "
          + khachHangDangNhap.getHangKhachHangText());
      capNhatThongTinCaNhan();
      taiVeCuaToi();
      taiLichSu();
      return true;
    }
    return false;
  }

  private void initComponents() {
    setTitle("Hệ Thống Vé Máy Bay - Khách Hàng");
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setSize(1000, 700);
    setLocationRelativeTo(null);

    // Welcome label
    lblWelcome = new JLabel("Vui lòng đăng nhập", JLabel.CENTER);
    lblWelcome.setFont(new Font("Arial", Font.BOLD, 16));
    lblWelcome.setForeground(Color.BLUE);

    // Tabbed pane
    tabbedPane = new JTabbedPane();

    // Tab Đặt vé
    JPanel panelDatVe = createTabDatVe();
    tabbedPane.addTab("Đặt Vé", panelDatVe);

    // Tab Vé của tôi
    JPanel panelVeCuaToi = createTabVeCuaToi();
    tabbedPane.addTab("Vé Của Tôi", panelVeCuaToi);

    // Tab Lịch sử
    JPanel panelLichSu = createTabLichSu();
    tabbedPane.addTab("Lịch Sử", panelLichSu);

    // Tab Thông tin
    JPanel panelThongTin = createTabThongTin();
    tabbedPane.addTab("Thông Tin", panelThongTin);
  }

  private JPanel createTabDatVe() {
    JPanel panel = new JPanel(new BorderLayout(10, 10)); // tab
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // nguyên khung

    JPanel panelTimKiem = new JPanel(new GridLayout(2, 4, 10, 10)); // Tiêu đề
    panelTimKiem.setBorder(BorderFactory.createTitledBorder("Tìm kiếm chuyến bay phù hợp"));

    // Tạo list điểm đi điểm đến
    List<String> diemDiList = new ArrayList<>(
        Arrays.asList("Hà Nội (HAN)", "Đà Nẵng (DAD)", "TP.HCM (SGN)", "Nha Trang (CXR)", "Phú Quốc (PQC)"));
    List<String> diemDenList = new ArrayList<>(
        Arrays.asList("Hà Nội (HAN)", "Đà Nẵng (DAD)", "TP.HCM (SGN)", "Nha Trang (CXR)", "Phú Quốc (PQC)"));
    // Thêm điểm đi điểm đến vào list
    cbDiemDi = new JComboBox<>(diemDiList.toArray(new String[0]));
    cbDiemDen = new JComboBox<>(diemDenList.toArray(new String[0]));

    // Thêm Chọn ngày đi
    SpinnerDateModel modelNgayDi = new SpinnerDateModel();
    spinnerNgayDi = new JSpinner(modelNgayDi); // <-- KHỞI TẠO spinnerNgayDi

    JSpinner.DateEditor editorNgayDi = new JSpinner.DateEditor(spinnerNgayDi, "📅 dd/MM/yyyy");
    spinnerNgayDi.setEditor(editorNgayDi);
    spinnerNgayDi.setValue(new Date());

    btnTimChuyen = new JButton("Tìm Chuyến Bay");
    panelTimKiem.add(new JLabel("Điểm đi:"));
    panelTimKiem.add(cbDiemDi);
    panelTimKiem.add(new JLabel("Điểm đến:"));
    panelTimKiem.add(cbDiemDen);
    panelTimKiem.add(new JLabel("Ngày đi (dd/MM/yyyy):"));
    panelTimKiem.add(spinnerNgayDi);
    panelTimKiem.add(new JLabel(""));
    panelTimKiem.add(btnTimChuyen);

    // Table chuyến bay
    String[] columns = { "Mã CB", "Điểm đi", "Điểm đến", "Giờ đi", "Giờ đến", "Ghế trống", "Giá cơ bản",
        "Trạng thái" };
    modelChuyenBay = new DefaultTableModel(columns, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    tableChuyenBay = new JTable(modelChuyenBay);
    tableChuyenBay.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    JScrollPane scrollChuyenBay = new JScrollPane(tableChuyenBay);
    modelChuyenBay.setRowCount(0);
    for (ChuyenBay cb : quanLy.getDsChuyenBay().getDanhSach()) {
      if (cb.getTrangThai().equals(ChuyenBay.TRANG_THAI_CHUA_BAY)) {
        modelChuyenBay.addRow(cb.toRowData());
      }
    }

    // Panel đặt vé
    JPanel panelDatVe = new JPanel(new FlowLayout());
    cbChuyenBay = new JComboBox<>();
    btnDatVe = new JButton("Đặt Vé");

    btnXemTatCa = new JButton("Xem tất cả");

    panelDatVe.add(new JLabel("Chọn chuyến bay:"));
    panelDatVe.add(cbChuyenBay);
    panelDatVe.add(btnDatVe);
    panelDatVe.add(btnXemTatCa);

    panel.add(panelTimKiem, BorderLayout.NORTH);
    panel.add(scrollChuyenBay, BorderLayout.CENTER);
    panel.add(panelDatVe, BorderLayout.SOUTH);

    return panel;
  }

  private JPanel createTabVeCuaToi() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    String[] columns = { "Mã Vé", "Chuyến Bay", "Loại Vé", "Số Ghế", "Giá Vé", "Ngày Đặt", "Trạng Thái" };
    modelVeCuaToi = new DefaultTableModel(columns, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    tableVeCuaToi = new JTable(modelVeCuaToi);
    JScrollPane scrollVeCuaToi = new JScrollPane(tableVeCuaToi);

    // Panel button
    JPanel panelButton = new JPanel(new FlowLayout());
    btnXemChiTietVe = new JButton("Xem Chi Tiết Vé");
    btnXemHoaDon = new JButton("Xem Hóa Đơn");
    btnHuyVe = new JButton("Hủy Vé");

    panelButton.add(btnXemChiTietVe);
    panelButton.add(btnXemHoaDon);
    panelButton.add(btnHuyVe);

    panel.add(new JLabel("Vé đang có:"), BorderLayout.NORTH);
    panel.add(scrollVeCuaToi, BorderLayout.CENTER);
    panel.add(panelButton, BorderLayout.SOUTH);

    return panel;
  }

  private JPanel createTabLichSu() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    String[] columns = { "Mã Hóa Đơn", "Mã Khách Hàng", "Ngày Lập", "DS Vé", "Tổng Tiền", "Thuế", "Thành Tiền",
        "Trạng Thái", "PP Thanh Toán" };
    modelLichSu = new DefaultTableModel(columns, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    tableLichSu = new JTable(modelLichSu);
    JScrollPane scrollLichSu = new JScrollPane(tableLichSu);

    // Panel button cho lịch sử
    JPanel panelButton = new JPanel(new FlowLayout());
    JButton btnXemChiTietHD = new JButton("Xem Chi Tiết HĐ");
    JButton btnThanhToanHD = new JButton("Thanh Toán HĐ");
    JButton btnHuyHD = new JButton("Hủy Hóa Đơn");

    panelButton.add(btnXemChiTietHD);
    panelButton.add(btnThanhToanHD);
    panelButton.add(btnHuyHD);

    // Thêm sự kiện cho các nút
    btnXemChiTietHD.addActionListener(e -> xemChiTietHoaDon());
    btnThanhToanHD.addActionListener(e -> thanhToanHoaDon());
    btnHuyHD.addActionListener(e -> huyHoaDon());

    panel.add(new JLabel("Lịch sử đặt vé:"), BorderLayout.NORTH);
    panel.add(scrollLichSu, BorderLayout.CENTER);
    panel.add(panelButton, BorderLayout.SOUTH);

    return panel;
}

// ========== PHƯƠNG THỨC XEM CHI TIẾT HÓA ĐƠN ==========
private void xemChiTietHoaDon() {
    int row = tableLichSu.getSelectedRow();
    if (row < 0) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn một hóa đơn!", "Thông báo", JOptionPane.WARNING_MESSAGE);
        return;
    }

    String maHoaDon = (String) modelLichSu.getValueAt(row, 0);
    HoaDon hoaDon = dsHoaDon.timKiemTheoMa(maHoaDon);

    if (hoaDon != null) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== CHI TIẾT HÓA ĐƠN ===\n\n");
        sb.append("Mã hóa đơn: ").append(hoaDon.getMaHoaDon()).append("\n");
        sb.append("Ngày lập: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(hoaDon.getNgayLap())).append("\n");
        sb.append("Khách hàng: ").append(hoaDon.getKhachHang().getHoTen()).append("\n");
        sb.append("Số điện thoại: ").append(hoaDon.getKhachHang().getSoDT()).append("\n");
        sb.append("Email: ").append(hoaDon.getKhachHang().getEmail()).append("\n\n");
        
        sb.append("=== DANH SÁCH VÉ ===\n");
        int stt = 1;
        for (VeMayBay ve : hoaDon.getDanhSachVe()) {
            ChuyenBay cb = dsChuyenBay.timKiemTheoMa(ve.getMaChuyen());
            sb.append(stt++).append(". ").append(ve.getMaVe())
              .append(" - ").append(cb != null ? cb.getDiemDi() + " → " + cb.getDiemDen() : "N/A")
              .append(" - ").append(ve.getSoGhe())
              .append(" - ").append(String.format("%,d VND", (int) ve.getGiaVe()))
              .append(" - ").append(chuyenTrangThaiSangText(ve.getTrangThai())).append("\n");
        }
        
        sb.append("\n=== THÔNG TIN THANH TOÁN ===\n");
        sb.append("Tổng tiền: ").append(String.format("%,d VND", (int) hoaDon.getTongTien())).append("\n");
        sb.append("Giảm giá: ").append(String.format("%,d VND", (int) hoaDon.getKhuyenMai())).append("\n");
        sb.append("Thuế/VAT: ").append(String.format("%,d VND", (int) hoaDon.getThue())).append("\n");
        sb.append("Thành tiền: ").append(String.format("%,d VND", (int) hoaDon.getThanhTien())).append("\n");
        sb.append("Phương thức TT: ").append(chuyenPhuongThucTTSangText(hoaDon.getPhuongThucTT())).append("\n");
        sb.append("Trạng thái: ").append(chuyenTrangThaiSangText1(hoaDon.getTrangThai())).append("\n");

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Chi Tiết Hóa Đơn " + maHoaDon, 
            JOptionPane.INFORMATION_MESSAGE);
    }
}

// ========== PHƯƠNG THỨC THANH TOÁN HÓA ĐƠN ==========
private void thanhToanHoaDon() {
    int row = tableLichSu.getSelectedRow();
    if (row < 0) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn một hóa đơn!", "Thông báo", JOptionPane.WARNING_MESSAGE);
        return;
    }

    String maHoaDon = (String) modelLichSu.getValueAt(row, 0);
    HoaDon hoaDon = dsHoaDon.timKiemTheoMa(maHoaDon);

    if (hoaDon == null) {
        JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Kiểm tra trạng thái hóa đơn
    if (hoaDon.getTrangThai().equals(HoaDon.TT_DA_TT)) {
        JOptionPane.showMessageDialog(this, "Hóa đơn đã được thanh toán!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    if (hoaDon.getTrangThai().equals(HoaDon.TT_HUY)) {
        JOptionPane.showMessageDialog(this, "Hóa đơn đã bị hủy, không thể thanh toán!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Hiển thị dialog chọn phương thức thanh toán
    String[] ptOptions = { "Tiền mặt", "Chuyển khoản", "Thẻ tín dụng", "Ví điện tử" };
    String phuongThucTT = (String) JOptionPane.showInputDialog(
            this,
            "Chọn phương thức thanh toán cho hóa đơn " + maHoaDon,
            "Phương Thức Thanh Toán",
            JOptionPane.QUESTION_MESSAGE,
            null,
            ptOptions,
            ptOptions[0]);

    if (phuongThucTT == null) {
        return; // Hủy
    }

    // Xác nhận thanh toán
    int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn thanh toán hóa đơn " + maHoaDon + "?\n" +
                    "Phương thức thanh toán: " + phuongThucTT + "\n" +
                    "Số tiền: " + String.format("%,d VND", (int) hoaDon.getThanhTien()),
            "Xác Nhận Thanh Toán",
            JOptionPane.YES_NO_OPTION);

    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }

    try {
        // Thực hiện thanh toán
        hoaDon.setTrangThai(HoaDon.TT_DA_TT);
        hoaDon.setPhuongThucTT(chuyenPhuongThucTextSangMa(phuongThucTT));
        hoaDon.setNgayLap(new Date());

        // Cập nhật trạng thái các vé trong hóa đơn
        for (VeMayBay ve : hoaDon.getDanhSachVe()) {
            ve.setTrangThai(VeMayBay.TRANG_THAI_DA_THANH_TOAN);
        }

        // Lưu dữ liệu
        quanLy.ghiDuLieuRaFile();

        // Cập nhật giao diện
        taiLichSu();
        taiVeCuaToi();

        JOptionPane.showMessageDialog(this, 
            "Thanh toán hóa đơn thành công!\nMã hóa đơn: " + maHoaDon, 
            "Thành Công", 
            JOptionPane.INFORMATION_MESSAGE);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, 
            "Lỗi khi thanh toán hóa đơn: " + e.getMessage(), 
            "Lỗi", 
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}

// ========== PHƯƠNG THỨC HỦY HÓA ĐƠN ==========
private void huyHoaDon() {
    int row = tableLichSu.getSelectedRow();
    if (row < 0) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn một hóa đơn!", "Thông báo", JOptionPane.WARNING_MESSAGE);
        return;
    }

    String maHoaDon = (String) modelLichSu.getValueAt(row, 0);
    HoaDon hoaDon = dsHoaDon.timKiemTheoMa(maHoaDon);

    if (hoaDon == null) {
        JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Kiểm tra trạng thái hóa đơn
    if (hoaDon.getTrangThai().equals(HoaDon.TT_HUY)) {
        JOptionPane.showMessageDialog(this, "Hóa đơn đã bị hủy!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    if (hoaDon.getTrangThai().equals(HoaDon.TT_DA_TT)) {
        JOptionPane.showMessageDialog(this, "Hóa đơn đã thanh toán, không thể hủy!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Hiển thị thông tin vé sẽ bị hủy
    StringBuilder veInfo = new StringBuilder();
    veInfo.append("Các vé sau sẽ bị hủy:\n\n");
    for (VeMayBay ve : hoaDon.getDanhSachVe()) {
        ChuyenBay cb = dsChuyenBay.timKiemTheoMa(ve.getMaChuyen());
        veInfo.append("• ").append(ve.getMaVe())
               .append(" - ").append(cb != null ? cb.getDiemDi() + " → " + cb.getDiemDen() : "N/A")
               .append(" - ").append(ve.getSoGhe()).append("\n");
    }

    // Xác nhận hủy
    int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn HỦY hóa đơn " + maHoaDon + "?\n\n" +
                    veInfo.toString() + "\n" +
                    "Lưu ý: Tất cả vé trong hóa đơn sẽ chuyển sang trạng thái HỦY!",
            "Xác Nhận Hủy Hóa Đơn",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }

    try {
        // Thực hiện hủy hóa đơn
        hoaDon.setTrangThai(HoaDon.TT_HUY);

        // Cập nhật trạng thái các vé trong hóa đơn thành HỦY
        for (VeMayBay ve : hoaDon.getDanhSachVe()) {
            ve.setTrangThai(VeMayBay.TRANG_THAI_DA_HUY);
            
            // Cập nhật số ghế trống của chuyến bay
            ChuyenBay cb = dsChuyenBay.timKiemTheoMa(ve.getMaChuyen());
            if (cb != null) {
                cb.setSoGheTrong(cb.getSoGheTrong() + 1);
            }
        }

        // Lưu dữ liệu
        quanLy.ghiDuLieuRaFile();

        // Cập nhật giao diện
        taiLichSu();
        taiVeCuaToi();

        JOptionPane.showMessageDialog(this, 
            "Hủy hóa đơn thành công!\nMã hóa đơn: " + maHoaDon + "\nTất cả vé đã được hủy.", 
            "Thành Công", 
            JOptionPane.INFORMATION_MESSAGE);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, 
            "Lỗi khi hủy hóa đơn: " + e.getMessage(), 
            "Lỗi", 
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}

// ========== PHƯƠNG THỨC HỖ TRỢ CHUYỂN ĐỔI PHƯƠNG THỨC THANH TOÁN ==========
private String chuyenPhuongThucTextSangMa(String phuongThucText) {
    switch (phuongThucText) {
        case "Tiền mặt":
            return HoaDon.PT_TIEN_MAT;
        case "Chuyển khoản":
            return HoaDon.PT_CHUYEN_KHOAN;
        case "Thẻ tín dụng":
            return HoaDon.PT_THE;
        case "Ví điện tử":
            return HoaDon.PT_VI_DIEN_TU;
        default:
            return HoaDon.PT_CHUYEN_KHOAN;
    }
}

  private JPanel createTabThongTin() {
    JPanel panel = new JPanel(new BorderLayout(15, 15));
    panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
    panel.setBackground(new Color(248, 250, 252));

    // === PANEL THÔNG TIN CÁ NHÂN ===
    JPanel panelThongTin = new JPanel(new GridBagLayout());
    panelThongTin.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
        "THÔNG TIN CÁ NHÂN",
        javax.swing.border.TitledBorder.CENTER,
        javax.swing.border.TitledBorder.TOP,
        new Font("Segoe UI", Font.BOLD, 14),
        new Color(70, 130, 180)));
    panelThongTin.setBackground(Color.WHITE);
    panelThongTin.setBorder(BorderFactory.createCompoundBorder(
        panelThongTin.getBorder(),
        BorderFactory.createEmptyBorder(20, 20, 20, 20)));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(8, 10, 8, 10);
    gbc.weightx = 1.0;

    // Khởi tạo components với style
    txtHoTen = createStyledTextField();
    txtEmail = createStyledTextField();
    txtSoDT = createStyledTextField();
    txtDiaChi = createStyledTextField();
    txtCmnd = createStyledTextField();
    txtNgaySinh = createStyledTextField();

    String[] gioiTinhOptions = { "Nam", "Nữ", "Khác" };
    cbGioiTinh = createStyledComboBox(gioiTinhOptions);

    btnCapNhatThongTin = createStyledButton("Cập Nhật Thông Tin", new Color(70, 130, 180));

    // Row 1: Họ tên và Email
    gbc.gridx = 0;
    gbc.gridy = 0;
    panelThongTin.add(createStyledLabel("Họ tên:"), gbc);

    gbc.gridx = 1;
    panelThongTin.add(txtHoTen, gbc);

    gbc.gridx = 2;
    panelThongTin.add(createStyledLabel("Email:"), gbc);

    gbc.gridx = 3;
    panelThongTin.add(txtEmail, gbc);

    // Row 2: Số điện thoại và Địa chỉ
    gbc.gridx = 0;
    gbc.gridy = 1;
    panelThongTin.add(createStyledLabel("Số điện thoại:"), gbc);

    gbc.gridx = 1;
    panelThongTin.add(txtSoDT, gbc);

    gbc.gridx = 2;
    panelThongTin.add(createStyledLabel("Địa chỉ:"), gbc);

    gbc.gridx = 3;
    panelThongTin.add(txtDiaChi, gbc);

    // Row 3: Giới tính và CCCD
    gbc.gridx = 0;
    gbc.gridy = 2;
    panelThongTin.add(createStyledLabel("Giới tính:"), gbc);

    gbc.gridx = 1;
    panelThongTin.add(cbGioiTinh, gbc);

    gbc.gridx = 2;
    panelThongTin.add(createStyledLabel(" CCCD:"), gbc);

    gbc.gridx = 3;
    panelThongTin.add(txtCmnd, gbc);

    // Row 4: Ngày sinh và Nút cập nhật
    gbc.gridx = 0;
    gbc.gridy = 3;
    panelThongTin.add(createStyledLabel("Ngày Sinh:"), gbc);

    gbc.gridx = 1;
    panelThongTin.add(txtNgaySinh, gbc);

    gbc.gridx = 2;
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.CENTER;
    panelThongTin.add(btnCapNhatThongTin, gbc);

    // === PANEL THÔNG TIN THÀNH VIÊN ===
    JPanel panelThanhVien = new JPanel(new GridBagLayout());
    panelThanhVien.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(new Color(60, 179, 113), 2),
        " THÔNG TIN THÀNH VIÊN",
        javax.swing.border.TitledBorder.CENTER,
        javax.swing.border.TitledBorder.TOP,
        new Font("Segoe UI", Font.BOLD, 14),
        new Color(60, 179, 113)));
    panelThanhVien.setBackground(Color.WHITE);
    panelThanhVien.setBorder(BorderFactory.createCompoundBorder(
        panelThanhVien.getBorder(),
        BorderFactory.createEmptyBorder(15, 15, 15, 15)));

    lblHangKhachHang = createInfoLabel("Hạng: Chưa đăng nhập");
    lblDiemTichLuy = createInfoLabel("Điểm tích lũy: 0");

    GridBagConstraints gbc2 = new GridBagConstraints();
    gbc2.insets = new Insets(10, 15, 10, 15);
    gbc2.fill = GridBagConstraints.HORIZONTAL;

    gbc2.gridx = 0;
    gbc2.gridy = 0;
    panelThanhVien.add(createStyledLabel(" Hạng khách hàng:"), gbc2);

    gbc2.gridx = 1;
    panelThanhVien.add(lblHangKhachHang, gbc2);

    gbc2.gridx = 0;
    gbc2.gridy = 1;
    panelThanhVien.add(createStyledLabel(" Điểm tích lũy:"), gbc2);

    gbc2.gridx = 1;
    panelThanhVien.add(lblDiemTichLuy, gbc2);

    // === ADD TO MAIN PANEL ===
    panel.add(panelThongTin, BorderLayout.CENTER);
    panel.add(panelThanhVien, BorderLayout.SOUTH);

    return panel;
  }

  // ========== CÁC PHƯƠNG THỨC HỖ TRỢ STYLE ==========

  private JTextField createStyledTextField() {
    JTextField txt = new JTextField();
    txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    txt.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(200, 200, 200)),
        BorderFactory.createEmptyBorder(10, 12, 10, 12)));
    txt.setBackground(new Color(252, 252, 252));
    txt.setPreferredSize(new Dimension(200, 40));

    // Hiệu ứng focus
    txt.addFocusListener(new FocusAdapter() {
      @Override
      public void focusGained(FocusEvent e) {
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        txt.setBackground(new Color(255, 255, 255));
      }

      @Override
      public void focusLost(FocusEvent e) {
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        txt.setBackground(new Color(252, 252, 252));
      }
    });

    return txt;
  }

  private <T> JComboBox<T> createStyledComboBox(T[] items) {
    JComboBox<T> cb = new JComboBox<>(items);
    cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    cb.setBackground(Color.WHITE);
    cb.setPreferredSize(new Dimension(200, 40));
    cb.setRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
          boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
            cellHasFocus);
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return label;
      }
    });
    return cb;
  }

  private JButton createStyledButton(String text, Color color) {
    JButton btn = new JButton(text);
    btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
    btn.setBackground(color);
    btn.setForeground(Color.WHITE);
    btn.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(color.darker()),
        BorderFactory.createEmptyBorder(12, 25, 12, 25)));
    btn.setFocusPainted(false);
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

    // Hiệu ứng hover
    btn.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        btn.setBackground(color.brighter());
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.brighter()),
            BorderFactory.createEmptyBorder(12, 25, 12, 25)));
      }

      public void mouseExited(java.awt.event.MouseEvent evt) {
        btn.setBackground(color);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker()),
            BorderFactory.createEmptyBorder(12, 25, 12, 25)));
      }
    });

    return btn;
  }

  private JLabel createStyledLabel(String text) {
    JLabel lbl = new JLabel(text);
    lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
    lbl.setForeground(new Color(60, 60, 60));
    return lbl;
  }

  private JLabel createInfoLabel(String text) {
    JLabel lbl = new JLabel(text);
    lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
    lbl.setForeground(new Color(70, 130, 180));
    lbl.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(220, 220, 220)),
        BorderFactory.createEmptyBorder(8, 15, 8, 15)));
    lbl.setOpaque(true);
    lbl.setBackground(new Color(245, 245, 245));
    return lbl;
  }

  private void setupLayout() {
    setLayout(new BorderLayout());
    add(lblWelcome, BorderLayout.NORTH);
    add(tabbedPane, BorderLayout.CENTER);
  }

  private void setupEvents() {
    // Tab Đặt vé
    btnTimChuyen.addActionListener(e -> timChuyenBay());
    btnDatVe.addActionListener(e -> datVe());
    btnXemTatCa.addActionListener(e -> xemTatCa());

    // Tab Vé của tôi
    btnXemChiTietVe.addActionListener(e -> xemChiTietVe());
    btnXemHoaDon.addActionListener(e -> xemHoaDon());
    btnHuyVe.addActionListener(e -> huyVe());

    // Tab Thông tin
    btnCapNhatThongTin.addActionListener(e -> capNhatThongTin());

    // Double click để chọn chuyến bay
    tableChuyenBay.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          int row = tableChuyenBay.getSelectedRow();
          if (row >= 0) {
            String maChuyen = (String) modelChuyenBay.getValueAt(row, 0);
            cbChuyenBay.removeAllItems();
            cbChuyenBay.addItem(maChuyen);
          }
        }
      }
    });
  }

  private void timChuyenBay() {
    modelChuyenBay.setRowCount(0);

    String diemDi = (String) cbDiemDi.getSelectedItem();
    String diemDen = (String) cbDiemDen.getSelectedItem();

    // Lấy giá trị từ JSpinner
    Date ngayDiDate = (Date) spinnerNgayDi.getValue();
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    // Sử dụng phương thức có sẵn từ DanhSachChuyenBay
    List<ChuyenBay> ketQua = dsChuyenBay.timKiemTheoTuyen(diemDi, diemDen);

    // Lọc theo ngày
    if (ngayDiDate != null) {
      try {
        // Reset lại danh sách kết quả
        List<ChuyenBay> ketQuaLocNgay = new ArrayList<>();

        for (ChuyenBay cb : ketQua) {
          // So sánh ngày (bỏ qua giờ phút)
          Date ngayChuyenBay = cb.getGioKhoiHanh();
          if (ngayChuyenBay != null) {
            String ngayCBStr = sdf.format(ngayChuyenBay);
            String ngayTimStr = sdf.format(ngayDiDate);
            if (ngayCBStr.equals(ngayTimStr)) {
              ketQuaLocNgay.add(cb);
            }
          }
        }
        ketQua = ketQuaLocNgay;
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Lỗi xử lý ngày tháng: " + ex.getMessage(), "Lỗi",
            JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
        return;
      }
    }

    // Lọc theo trạng thái và ghế trống
    for (ChuyenBay cb : ketQua) {
      if (cb.getSoGheTrong() > 0) {
        modelChuyenBay.addRow(cb.toRowData());
      }
    }
  }

  private void datVe() {
    // Kiểm tra đăng nhập
    if (!kiemTraDangNhap())
      return;

    // Kiểm tra chọn chuyến bay
    if (!kiemTraChonChuyenBay())
      return;

    // Lấy thông tin chuyến bay
    String maChuyen = (String) cbChuyenBay.getSelectedItem();
    ChuyenBay chuyenBay = dsChuyenBay.timKiemTheoMa(maChuyen);

    // Kiểm tra chuyến bay khả dụng
    if (!kiemTraChuyenBayKhaDung(chuyenBay))
      return;

    // Hiển thị dialog đặt vé
    VeMayBay ve = hienThiDialogDatVe(chuyenBay);
    if (ve == null)
      return;

    // xác nhận
    if (xuLyDatVe(ve, chuyenBay)) {
      hienThiThongBaoThanhCong(ve, chuyenBay);
      capNhatDuLieuSauKhiDatVe();
    }
  }

  // ========== CÁC PHƯƠNG THỨC HỖ TRỢ ==========

  private boolean kiemTraDangNhap() {
    if (khachHangDangNhap == null) {
      JOptionPane.showMessageDialog(this, "Vui lòng đăng nhập!", "Lỗi", JOptionPane.ERROR_MESSAGE);
      return false;
    }
    return true;
  }

  private boolean kiemTraChonChuyenBay() {
    if (cbChuyenBay.getSelectedItem() == null) {
      JOptionPane.showMessageDialog(this, "Vui lòng chọn chuyến bay!", "Lỗi", JOptionPane.ERROR_MESSAGE);
      return false;
    }
    return true;
  }

  private boolean kiemTraChuyenBayKhaDung(ChuyenBay chuyenBay) {
    if (chuyenBay == null) {
      JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin chuyến bay!", "Lỗi",
          JOptionPane.ERROR_MESSAGE);
      return false;
    }

    if (chuyenBay.getSoGheTrong() <= 0) {
      JOptionPane.showMessageDialog(this, "Chuyến bay đã hết chỗ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
      return false;
    }

    if (!chuyenBay.getTrangThai().equals(ChuyenBay.TRANG_THAI_CHUA_BAY)) {
      JOptionPane.showMessageDialog(this, "Chuyến bay không khả dụng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
      return false;
    }

    return true;
  }

  private boolean xuLyDatVe(VeMayBay ve, ChuyenBay chuyenBay) {
    try {
      double giamGia = khachHangDangNhap.tinhMucGiamGia(ve.getGiaVe());
      double giaVeSauGiam = ve.getGiaVe() - giamGia;
      ve.setGiaVe(giaVeSauGiam);
      if (!hienThiThongTinVeXacNhan(ve, chuyenBay, giamGia)) {
        return false;
      }
      if (!dsVe.them(ve)) {
        JOptionPane.showMessageDialog(this, "Lỗi khi thêm vé vào hệ thống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return false;
      }

      chuyenBay.setSoGheTrong(chuyenBay.getSoGheTrong() - 1);

      // Tạo hóa đơn
      if (!taoHoaDon(ve, giamGia)) {
        JOptionPane.showMessageDialog(this, "Lỗi khi tạo hóa đơn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return false;
      }

      capNhatDiemTichLuy(giaVeSauGiam);

      // Lưu dữ liệu ra file sau khi đặt vé thành công
      quanLy.ghiDuLieuRaFile();

      return true;

    } catch (Exception e) {
      JOptionPane.showMessageDialog(this,
          "Lỗi trong quá trình đặt vé: " + e.getMessage(),
          "Lỗi hệ thống",
          JOptionPane.ERROR_MESSAGE);
      e.printStackTrace();
      return false;
    }
  }

  private boolean taoHoaDon(VeMayBay ve, double giamGia) {
    try {
      List<VeMayBay> dsVeHoaDon = new ArrayList<>();
      dsVeHoaDon.add(ve);
      String maHoaDon = "HD" + String.format("%03d", quanLy.getDsHoaDon().getDanhSach().size());
      HoaDon hoaDon = new HoaDon(maHoaDon, khachHangDangNhap, dsVeHoaDon, giamGia, HoaDon.PT_CHUYEN_KHOAN);
      dsHoaDon.them(hoaDon);
      return true;
    } catch (Exception e) {
      System.err.println("Lỗi khi tạo hóa đơn: " + e.getMessage());
      return false;
    }
  }

  private void capNhatDiemTichLuy(double giaVeSauGiam) {
    try {
      int diemThuong = (int) (giaVeSauGiam / 100000); // 1 điểm cho mỗi 100,000 VND
      khachHangDangNhap.tangDiemTichLuy(diemThuong);
    } catch (Exception e) {
      System.err.println("Lỗi khi cập nhật điểm tích lũy: " + e.getMessage());
    }
  }

  private void hienThiThongBaoThanhCong(VeMayBay ve, ChuyenBay chuyenBay) {
    double giamGia = khachHangDangNhap.tinhMucGiamGia(ve.getGiaVe());
    int diemThuong = (int) ((ve.getGiaVe() - giamGia) / 100000);

    String message = String.format(
        "Đặt vé thành công!\n\n" +
            "📋 Thông tin vé:\n" +
            "• Mã vé: %s\n" +
            "• Chuyến bay: %s → %s\n" +
            "• Loại vé: %s\n" +
            "• Số ghế: %s\n" +
            "• Giá vé: %s VND\n" +
            "• Giảm giá: %s VND\n" +
            "• Điểm tích lũy nhận được: %d điểm\n\n" +
            "Cảm ơn bạn đã sử dụng dịch vụ!",
        ve.getMaVe(),
        chuyenBay.getDiemDi(),
        chuyenBay.getDiemDen(),
        getTenLoaiVe(ve),
        ve.getSoGhe(),
        String.format("%,d", (int) ve.getGiaVe()),
        String.format("%,d", (int) giamGia),
        diemThuong);

    JOptionPane.showMessageDialog(this, message, "Đặt Vé Thành Công", JOptionPane.INFORMATION_MESSAGE);
  }

  private void capNhatDuLieuSauKhiDatVe() {
    // Cập nhật giao diện
    taiVeCuaToi();
    taiLichSu();
    capNhatThongTinCaNhan();

    // Làm mới danh sách chuyến bay
    timChuyenBay();
  }

  private boolean hienThiThongTinVeXacNhan(VeMayBay ve, ChuyenBay chuyenBay, double giamGia) {
    String message = String.format(
        "XÁC NHẬN THÔNG TIN VÉ\n\n" +
            "Chuyến bay: %s → %s\n" +
            "Loại vé: %s\n" +
            "Số ghế: %s\n" +
            "Giá gốc: %s VND\n" +
            "Giảm giá: %s VND\n" +
            "Thành tiền: %s VND\n\n" +
            "Bạn có chắc chắn đặt vé này?",
        chuyenBay.getDiemDi(),
        chuyenBay.getDiemDen(),
        getTenLoaiVe(ve),
        ve.getSoGhe(),
        String.format("%,d", (int) (ve.getGiaVe() + giamGia)),
        String.format("%,d", (int) giamGia),
        String.format("%,d", (int) ve.getGiaVe()));

    int result = JOptionPane.showConfirmDialog(
        this,
        message,
        "Xác Nhận Đặt Vé",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE);

    return result == JOptionPane.YES_OPTION;
  }

  private VeMayBay hienThiDialogDatVe(ChuyenBay chuyenBay) {
    JDialog dialog = new JDialog(this, "Đặt Vé Máy Bay", true);
    dialog.setSize(700, 700);
    dialog.setLocationRelativeTo(this);
    dialog.setLayout(new BorderLayout(10, 10));

    JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

    // ========== PANEL THÔNG TIN CHUYẾN BAY ==========
    JPanel panelChuyenBay = new JPanel(new GridLayout(0, 2, 10, 5));
    panelChuyenBay.setBorder(BorderFactory.createTitledBorder("Thông tin chuyến bay"));

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    panelChuyenBay.add(new JLabel("Mã chuyến bay:"));
    panelChuyenBay.add(new JLabel(chuyenBay.getMaChuyen()));
    panelChuyenBay.add(new JLabel("Tuyến bay:"));
    panelChuyenBay.add(new JLabel(chuyenBay.getDiemDi() + " → " + chuyenBay.getDiemDen()));
    panelChuyenBay.add(new JLabel("Giờ khởi hành:"));
    panelChuyenBay.add(new JLabel(sdf.format(chuyenBay.getGioKhoiHanh())));
    panelChuyenBay.add(new JLabel("Giờ đến:"));
    panelChuyenBay.add(new JLabel(sdf.format(chuyenBay.getGioDen())));
    panelChuyenBay.add(new JLabel("Ghế trống:"));
    panelChuyenBay.add(new JLabel(String.valueOf(chuyenBay.getSoGheTrong())));

    // ========== PANEL LỰA CHỌN VÉ ==========
    JPanel panelLuaChon = new JPanel(new GridBagLayout());
    panelLuaChon.setBorder(BorderFactory.createTitledBorder("Lựa chọn vé"));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 5, 5, 5);

    // Loại vé
    gbc.gridx = 0;
    gbc.gridy = 0;
    panelLuaChon.add(new JLabel("Loại vé:"), gbc);
    JComboBox<String> cbLoaiVe = new JComboBox<>(new String[] {
        "THƯƠNG GIA", "PHỔ THÔNG", "TIẾT KIỆM"
    });
    gbc.gridx = 1;
    panelLuaChon.add(cbLoaiVe, gbc);

    // Loại ghế (sẽ thay đổi theo loại vé)
    gbc.gridx = 0;
    gbc.gridy = 1;
    panelLuaChon.add(new JLabel("Loại ghế:"), gbc);
    JComboBox<String> cbLoaiGhe = new JComboBox<>();
    gbc.gridx = 1;
    panelLuaChon.add(cbLoaiGhe, gbc);

    // Dịch vụ 1 (sẽ thay đổi theo loại vé)
    gbc.gridx = 0;
    gbc.gridy = 2;
    JLabel lblDichVu1 = new JLabel("Dịch vụ:");
    panelLuaChon.add(lblDichVu1, gbc);
    JComboBox<String> cbDichVu1 = new JComboBox<>();
    gbc.gridx = 1;
    panelLuaChon.add(cbDichVu1, gbc);

    // Dịch vụ 2 (sẽ thay đổi theo loại vé)
    gbc.gridx = 0;
    gbc.gridy = 3;
    JLabel lblDichVu2 = new JLabel("Dịch vụ bổ sung:");
    panelLuaChon.add(lblDichVu2, gbc);
    JComboBox<String> cbDichVu2 = new JComboBox<>();
    gbc.gridx = 1;
    panelLuaChon.add(cbDichVu2, gbc);

    // Hành lý
    gbc.gridx = 0;
    gbc.gridy = 4;
    panelLuaChon.add(new JLabel("Hành lý:"), gbc);
    JComboBox<String> cbHanhLy = new JComboBox<>();
    gbc.gridx = 1;
    panelLuaChon.add(cbHanhLy, gbc);

    // ========== PANEL THÔNG TIN GIÁ ==========
    JPanel panelGia = new JPanel(new GridLayout(0, 2, 10, 5));
    panelGia.setBorder(BorderFactory.createTitledBorder("Thông tin giá"));

    JLabel lblGiaCoBan = new JLabel(String.format("%,d VND", (int) chuyenBay.getGiaCoBan()));
    JLabel lblHeSoGia = new JLabel("1.0");
    JLabel lblPhiDichVu = new JLabel("0 VND");
    JLabel lblPhiHanhLy = new JLabel("0 VND");
    JLabel lblGiamGia = new JLabel("0 VND");

    panelGia.add(new JLabel("Giá cơ bản:"));
    panelGia.add(lblGiaCoBan);
    panelGia.add(new JLabel("Hệ số loại vé:"));
    panelGia.add(lblHeSoGia);
    panelGia.add(new JLabel("Phụ thu:"));
    panelGia.add(lblPhiDichVu);
    panelGia.add(new JLabel("Phí hành lý:"));
    panelGia.add(lblPhiHanhLy);
    panelGia.add(new JLabel("Tổng thành tiền:"));
    JLabel lblTongThanhTien = new JLabel(String.format("%,d VND", (int) chuyenBay.getGiaCoBan()));
    lblTongThanhTien.setFont(new Font("Arial", Font.BOLD, 14));
    lblTongThanhTien.setForeground(Color.RED);
    panelGia.add(lblTongThanhTien);

    // ========== CẬP NHẬT LỰA CHỌN THEO LOẠI VÉ ==========
    ActionListener capNhatLuaChon = e -> {
      String loaiVe = (String) cbLoaiVe.getSelectedItem();
      cbLoaiGhe.removeAllItems();
      cbDichVu1.removeAllItems();
      cbDichVu2.removeAllItems();
      cbHanhLy.removeAllItems();

      switch (loaiVe) {
        case "THƯƠNG GIA":
          lblDichVu1.setText("Dịch vụ đặc biệt:");
          lblDichVu2.setText("Dịch vụ ăn uống:");
          cbLoaiGhe.addItem("Giường nằm");
          cbDichVu1.addItem("Phòng chờ VIP, Ưu tiên lên máy bay, Xe đưa đón");
          cbDichVu1.addItem("Phòng chờ VIP, Hỗ trợ check-in riêng");
          cbDichVu1.addItem("Phòng chờ VIP, Ghế ngả hoàn toàn");
          cbDichVu2.addItem("Rượu vang cao cấp");
          cbDichVu2.addItem("Champagne");
          cbDichVu2.addItem("Cocktail đặc biệt");
          cbHanhLy.addItem("20kg miễn phí");
          break;

        case "PHỔ THÔNG":
          lblDichVu1.setText("Vị trí ghế:");
          lblDichVu2.setText("Dịch vụ ăn uống:");
          cbLoaiGhe.addItem("Ghế ngồi");
          cbDichVu1.addItem("Cửa sổ");
          cbDichVu1.addItem("Lối đi");
          cbDichVu1.addItem("Giữa");
          cbDichVu2.addItem("Không ăn uống");
          cbDichVu2.addItem("Set ăn phổ thông");
          cbHanhLy.addItem("7kg xách tay");
          cbHanhLy.addItem("15kg ký gửi");
          cbHanhLy.addItem("20kg ký gửi");
          cbHanhLy.addItem("25kg ký gửi");
          break;

        case "TIẾT KIỆM":
          lblDichVu1.setText("Loại vé TK:");
          lblDichVu2.setText("Dịch vụ:");
          cbLoaiGhe.addItem("Ghế ngồi");
          cbDichVu1.addItem("TK không hoàn hủy");
          cbDichVu2.addItem("Không dịch vụ");
          cbHanhLy.addItem("Không hành lý");
          cbHanhLy.addItem("Có xách tay (<= 7kg)");
          break;
      }
    };

    cbLoaiVe.addActionListener(capNhatLuaChon);

    // ========== CẬP NHẬT GIÁ ==========
    Runnable capNhatGia = () -> {
      double giaCoBan = chuyenBay.getGiaCoBan();
      String loaiVe = (String) cbLoaiVe.getSelectedItem();

      double heSoGia = 1.0;
      double phuThu = 0;
      double phiHanhLy = 0;

      // Tính hệ số giá theo loại vé
      switch (loaiVe) {
        case "THƯƠNG GIA":
          heSoGia = 2.0;
          phuThu = 500000;
          String loaiGheTG = (String) cbLoaiGhe.getSelectedItem();
          if ("Giường nằm".equals(loaiGheTG))
            heSoGia += 0.5;
          if ("Suite".equals(loaiGheTG))
            heSoGia += 1.0;
          // Thêm phí hành lý
          String hanhLyTG = (String) cbHanhLy.getSelectedItem();
          if ("30kg (thêm 200,000 VND)".equals(hanhLyTG))
            phiHanhLy = 200000;
          if ("40kg (thêm 400,000 VND)".equals(hanhLyTG))
            phiHanhLy = 400000;
          break;

        case "PHỔ THÔNG":
          heSoGia = 1.2;
          // Thêm phí hành lý
          String hanhLyPT = (String) cbHanhLy.getSelectedItem();
          if ("15kg ký gửi".equals(hanhLyPT))
            phiHanhLy = 200000;
          if ("20kg ký gửi".equals(hanhLyPT))
            phiHanhLy = 300000;
          if ("25kg ký gửi".equals(hanhLyPT))
            phiHanhLy = 400000;
          // Thêm phí ăn uống
          String anUongPT = (String) cbDichVu2.getSelectedItem();
          if ("Có".equals(anUongPT))
            phuThu = 150000;
          if ("Không".equals(anUongPT))
            phuThu = 250000;
          break;

        case "TIẾT KIỆM":
          heSoGia = 0.9;
          phuThu = 100000;
          // Điều chỉnh theo loại vé TK
          String loaiVeTK = (String) cbLoaiGhe.getSelectedItem();
          if ("Tiết kiệm linh hoạt".equals(loaiVeTK)) {
            heSoGia = 0.85;
            phuThu = 150000;
          } else if ("Tiết kiệm siêu rẻ".equals(loaiVeTK)) {
            heSoGia = 0.8;
            phuThu = 200000;
          }
          // Thêm phí hành lý
          String hanhLyTK = (String) cbHanhLy.getSelectedItem();
          if ("7kg xách tay".equals(hanhLyTK))
            phiHanhLy = 50;
          if ("10kg ký gửi".equals(hanhLyTK))
            phiHanhLy = 100000;
          break;
      }

      double tongGiaTruocGiam = giaCoBan * heSoGia + phuThu + phiHanhLy;
      double giamGia = khachHangDangNhap.tinhMucGiamGia(tongGiaTruocGiam);
      double thanhTien = tongGiaTruocGiam - giamGia;

      // Cập nhật hiển thị
      lblHeSoGia.setText(String.format("%.1f", heSoGia));
      lblPhiDichVu.setText(String.format("%,d VND", (int) phuThu));
      lblPhiHanhLy.setText(String.format("%,d VND", (int) phiHanhLy));
      lblGiamGia.setText(String.format("%,d VND", (int) giamGia));
      lblTongThanhTien.setText(String.format("%,d VND", (int) thanhTien));
    };

    // Thêm listener cho tất cả combobox
    ActionListener capNhatGiaListener = e -> capNhatGia.run();
    cbLoaiGhe.addActionListener(capNhatGiaListener);
    cbDichVu1.addActionListener(capNhatGiaListener);
    cbDichVu2.addActionListener(capNhatGiaListener);
    cbHanhLy.addActionListener(capNhatGiaListener);

    // ========== PANEL BUTTON ==========
    JPanel panelButton = new JPanel(new FlowLayout());
    JButton btnDatVe = new JButton("Đặt Vé");
    JButton btnHuy = new JButton("Hủy");

    final VeMayBay[] veResult = { null };

    btnDatVe.addActionListener(e -> {
      String loaiVe = (String) cbLoaiVe.getSelectedItem();

      String soGhe = "12A";
      double tongGia = Double.parseDouble(lblTongThanhTien.getText().replaceAll("[^0-9]", ""));

      // Tạo vé theo loại
      switch (loaiVe) {
        case "THƯƠNG GIA":
          String maVe = "VG" + String.format("%03d", quanLy.getDsVe().demSoLuong());
          String dichVuGiaiTri = (String) cbDichVu1.getSelectedItem();
          String dichVuAnUong = (String) cbDichVu2.getSelectedItem();
          double phiDichVuTG = Double.parseDouble(lblPhiDichVu.getText().replaceAll("[^0-9]", ""));
          veResult[0] = new VeThuongGia(
              khachHangDangNhap.getMa(), maVe, new Date(), tongGia,
              chuyenBay.getMaChuyen(), soGhe, dichVuGiaiTri,
              phiDichVuTG, true, 20, dichVuAnUong);
          break;

        case "PHỔ THÔNG":
          String maVe1 = "VP" + String.format("%03d", quanLy.getDsVe().demSoLuong());
          String viTriGhe = (String) cbDichVu1.getSelectedItem();
          boolean coAnUong = !"Không ăn uống".equals(cbDichVu2.getSelectedItem());
          veResult[0] = new VePhoThong(
              khachHangDangNhap.getMa(), maVe1, new Date(), tongGia,
              chuyenBay.getMaChuyen(), soGhe, coAnUong,
              7, viTriGhe, true);
          break;

        case "TIẾT KIỆM":
          String maVe2 = "VT" + String.format("%03d", quanLy.getDsVe().demSoLuong());
          veResult[0] = new VeTietKiem(
              khachHangDangNhap.getMa(), maVe2, new Date(), tongGia,
              chuyenBay.getMaChuyen(), soGhe, true);
          break;
      }

      dialog.dispose();
    });

    btnHuy.addActionListener(e -> {
      dialog.dispose();
    });

    panelButton.add(btnDatVe);
    panelButton.add(btnHuy);

    // ========== SẮP XẾP LAYOUT ==========
    JPanel panelContent = new JPanel(new GridLayout(3, 1, 10, 10));
    panelContent.add(panelChuyenBay);
    panelContent.add(panelLuaChon);
    panelContent.add(panelGia);

    mainPanel.add(panelContent, BorderLayout.CENTER);
    mainPanel.add(panelButton, BorderLayout.SOUTH);

    dialog.add(mainPanel);

    // Khởi tạo giá trị mặc định
    capNhatLuaChon.actionPerformed(null);

    dialog.setVisible(true);
    return veResult[0];
  }

  private String generateSoGhe(String loaiVe, int soGheTrong) {
    String prefix = "";
    switch (loaiVe) {
      case "THƯƠNG GIA":
        prefix = "VG0";
        break;
      case "PHỔ THÔNG":
        prefix = "VP0";
        break;
      case "TIẾT KIỆM":
        prefix = "VT0";
        break;
    }
    return prefix + "67";
  }

  private String getTenLoaiVe(VeMayBay ve) {
    if (ve instanceof VeThuongGia)
      return "Thương gia";
    if (ve instanceof VePhoThong)
      return "Phổ thông";
    if (ve instanceof VeTietKiem)
      return "Tiết kiệm";
    return "Không xác định";
  }

  private void xemTatCa() {
    // Xóa dữ liệu cũ
    modelChuyenBay.setRowCount(0);

    List<ChuyenBay> tatCaChuyenBay = quanLy.getDsChuyenBay().getDanhSach();

    if (tatCaChuyenBay == null || tatCaChuyenBay.isEmpty()) {
      JOptionPane.showMessageDialog(this,
          "Không có chuyến bay nào trong hệ thống!",
          "Thông báo",
          JOptionPane.INFORMATION_MESSAGE);
      return;
    }

    // Thống kê
    int tongSo = tatCaChuyenBay.size();
    int conGhe = 0;
    int chuaBay = 0;

    // Thêm dữ liệu và thống kê
    for (ChuyenBay cb : tatCaChuyenBay) {
      if (cb.getTrangThai().equals(ChuyenBay.TRANG_THAI_CHUA_BAY)) {
        modelChuyenBay.addRow(cb.toRowData());

        // Thống kê
        if (cb.getSoGheTrong() > 0)
          conGhe++;
        chuaBay++;
      }
    }
    // Hiển thị thống kê
    String thongKe = String.format(
        "THỐNG KÊ CHUYẾN BAY:\n" +
            "• Tổng số: %d chuyến\n" +
            "• Còn ghế trống: %d chuyến\n" +
            "• Chưa khởi hành: %d chuyến\n" +
            tongSo,
        conGhe, chuaBay);

    JOptionPane.showMessageDialog(this, thongKe, "Đã hiển thị tất cả chuyến bay", JOptionPane.INFORMATION_MESSAGE);
  }

  private void taiVeCuaToi() {
    try {
      modelVeCuaToi.setRowCount(0);
      // Đọc dữ liệu mới nhất
      dsHoaDon.docFile("src/resources/data/4_HoaDons.xml");
      // Cập nhật lịch sử hóa đơn
      capNhatLichSuHoaDon();
      // Lấy và hiển thị vé
      List<VeMayBay> veCuaToi = khachHangDangNhap.getVeDaDat();
      hienThiVeTrongBang(veCuaToi);

    } catch (Exception e) {
      JOptionPane.showMessageDialog(this,
          "Lỗi khi tải dữ liệu vé: " + e.getMessage(),
          "Lỗi",
          JOptionPane.ERROR_MESSAGE);
      e.printStackTrace();
    }
  }

  private void capNhatLichSuHoaDon() {
    if (khachHangDangNhap == null)
      return;

    List<HoaDon> lichSuMoi = new ArrayList<>();

    for (HoaDon hd : dsHoaDon.getDanhSach()) {
      if (hd.getKhachHang() != null && hd.getKhachHang().getMa() != null
          && hd.getKhachHang().getMa().equals(khachHangDangNhap.getMa())) {
        lichSuMoi.add(hd);
      }
    }

    // Cập nhật lịch sử
    khachHangDangNhap.getLichSuHoaDon().clear();
    khachHangDangNhap.getLichSuHoaDon().addAll(lichSuMoi);
  }

  private void hienThiVeTrongBang(List<VeMayBay> danhSachVe) {
    if (danhSachVe == null || danhSachVe.isEmpty()) {
      modelVeCuaToi.addRow(new Object[] {
          "", "Không có vé nào", "", "", "", "", ""
      });
      return;
    }

    int count = 0;
    for (VeMayBay ve : danhSachVe) {
      // Bỏ qua vé đã hủy
      if (ve.getTrangThai().equals(VeMayBay.TRANG_THAI_DA_HUY)) {
        continue;
      }

      ChuyenBay cb = dsChuyenBay.timKiemTheoMa(ve.getMaChuyen());
      String tenChuyen = (cb != null) ? cb.getDiemDi() + " → " + cb.getDiemDen() : "N/A";

      String loaiVe = xacDinhLoaiVe(ve);
      String trangThai = chuyenTrangThaiSangText(ve.getTrangThai());

      modelVeCuaToi.addRow(new Object[] {
          ve.getMaVe(),
          tenChuyen,
          loaiVe,
          ve.getSoGhe(),
          String.format("%,d VND", (int) ve.getGiaVe()),
          new SimpleDateFormat("dd/MM/yyyy").format(ve.getNgayDat()),
          trangThai
      });
      count++;
    }

    // Thông báo kết quả
    if (count == 0) {
      JOptionPane.showMessageDialog(this,
          "Bạn không có vé nào để hiển thị!",
          "Thông báo",
          JOptionPane.INFORMATION_MESSAGE);
    }
  }

  private String xacDinhLoaiVe(VeMayBay ve) {
    if (ve instanceof VeThuongGia)
      return "Thương gia";
    if (ve instanceof VeTietKiem)
      return "Tiết kiệm";
    return "Phổ thông";
  }

  private String chuyenTrangThaiSangText(String trangThai) {
    switch (trangThai) {
      case VeMayBay.TRANG_THAI_DA_DAT:
        return "Đã đặt";
      case VeMayBay.TRANG_THAI_DA_THANH_TOAN:
        return "Đã thanh toán";
      case VeMayBay.TRANG_THAI_DA_BAY:
        return "Đã bay";
      case VeMayBay.TRANG_THAI_DA_HUY:
        return "Đã hủy";
      default:
        return trangThai;
    }
  }

  private void taiLichSu() {
    if (khachHangDangNhap == null)
      return;

    modelLichSu.setRowCount(0);
    List<HoaDon> lichSu = khachHangDangNhap.getLichSuHoaDon();

    for (HoaDon hd : lichSu) {
      // Tạo thông tin vé chi tiết hơn
      String thongTinVe = taoThongTinVeChiTiet(hd.getDanhSachVe());
      String trangThai = chuyenTrangThaiSangText1(hd.getTrangThai());
      String phuongThucTT = chuyenPhuongThucTTSangText(hd.getPhuongThucTT());

      modelLichSu.addRow(new Object[] {
          hd.getMaHoaDon(),
          hd.getKhachHang().getMa(), // Hiển thị tên thay vì mã
          new SimpleDateFormat("dd/MM/yyyy HH:mm").format(hd.getNgayLap()),
          thongTinVe,
          String.format("%,d VND", (int) hd.getTongTien()),
          String.format("%,d VND", (int) hd.getThue()),
          String.format("%,d VND", (int) hd.getThanhTien()),
          trangThai,
          phuongThucTT
      });
    }
  }

  // Các phương thức hỗ trợ
  private String taoThongTinVeChiTiet(List<VeMayBay> danhSachVe) {
    StringBuilder sb = new StringBuilder();
    sb.append("<html>");
    for (int i = 0; i < danhSachVe.size(); i++) {
      VeMayBay ve = danhSachVe.get(i);
      if (i > 0)
        sb.append(", ");
      sb.append(String.format("%s",
          ve.getMaVe()));
    }
    sb.append("</html>");
    return sb.toString();
  }

  private String chuyenTrangThaiSangText1(String trangThai) {
    switch (trangThai) {
      case HoaDon.TT_CHUA_TT:
        return "Chưa thanh toán";
      case HoaDon.TT_DA_TT:
        return "Đã thanh toán";
      case HoaDon.TT_HUY:
        return "Đã hủy";
      default:
        return trangThai;
    }
  }

  private String chuyenPhuongThucTTSangText(String phuongThuc) {
    switch (phuongThuc) {
      case HoaDon.PT_TIEN_MAT:
        return "Tiền mặt";
      case HoaDon.PT_CHUYEN_KHOAN:
        return "Chuyển khoản";
      case HoaDon.PT_THE:
        return "Thẻ tín dụng";
      case HoaDon.PT_VI_DIEN_TU:
        return "Ví điện tử";
      default:
        return phuongThuc;
    }
  }

  private void xemChiTietVe() {
    int row = tableVeCuaToi.getSelectedRow();
    if (row < 0) {
      JOptionPane.showMessageDialog(this, "Vui lòng chọn một vé!", "Thông báo", JOptionPane.WARNING_MESSAGE);
      return;
    }

    String maVe = (String) modelVeCuaToi.getValueAt(row, 0);
    VeMayBay ve = dsVe.timKiemTheoMa(maVe);

    if (ve != null) {
      ChuyenBay cb = dsChuyenBay.timKiemTheoMa(ve.getMaChuyen());

      StringBuilder sb = new StringBuilder();
      sb.append("=== CHI TIẾT VÉ ===\n\n");
      sb.append("Mã vé: ").append(ve.getMaVe()).append("\n");
      sb.append("Ma khach hang: ").append(ve.getmaKH()).append("\n");
      sb.append("Chuyến bay: ").append(cb != null ? cb.getDiemDi() + " → " + cb.getDiemDen() : "N/A")
          .append("\n");
      sb.append("Số ghế: ").append(ve.getSoGhe()).append("\n");
      sb.append("Loại vé: ").append(ve instanceof VeThuongGia ? "Thương gia"
          : (ve.loaiVe().equals("VePhoThong") ? "Phổ Thông" : "Tiết Kiệm")).append("\n");
      sb.append("Giá vé: ").append(String.format("%,d VND", (int) ve.getGiaVe())).append("\n");
      sb.append("Ngày đặt: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(ve.getNgayDat()))
          .append("\n");
      sb.append("Trạng thái: ").append(ve.getTrangThai()).append("\n\n");

      JTextArea textArea = new JTextArea(sb.toString());
      textArea.setEditable(false);
      JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Chi Tiết Vé",
          JOptionPane.INFORMATION_MESSAGE);
    }
  }

  private void xemHoaDon() {
    int row = tableVeCuaToi.getSelectedRow();
    if (row < 0) {
      JOptionPane.showMessageDialog(this, "Vui lòng chọn một vé!", "Thông báo", JOptionPane.WARNING_MESSAGE);
      return;
    }

    String maVe = (String) modelVeCuaToi.getValueAt(row, 0);

    // Tìm hóa đơn tương ứng
    List<HoaDon> hoaDonList = khachHangDangNhap.getLichSuHoaDon();
    for (HoaDon hd : hoaDonList) {
      // Hiển thị thông tin hóa đơn
      StringBuilder sb = new StringBuilder();
      sb.append("=== HÓA ĐƠN ===\n\n");
      sb.append("Mã hóa đơn: ").append(hd.getMaHoaDon()).append("\n");
      sb.append("Ngày lập: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(hd.getNgayLap()))
          .append("\n");
      sb.append("Khách hàng: ").append(khachHangDangNhap.getHoTen()).append("\n");
      sb.append("Tổng tiền: ").append(String.format("%,d VND", (int) hd.getTongTien())).append("\n");
      sb.append("Giảm giá: ").append(String.format("%,d VND", (int) hd.getKhuyenMai())).append("\n");
      sb.append("Phí dịch vụ: ").append(String.format("%,d VND", (int) hd.getThue())).append("\n");
      sb.append("Thành tiền: ").append(String.format("%,d VND", (int) hd.getThanhTien())).append("\n");
      sb.append("Trạng thái: ").append(hd.getTrangThai()).append("\n");

      JTextArea textArea = new JTextArea(sb.toString());
      textArea.setEditable(false);
      JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Hóa Đơn", JOptionPane.INFORMATION_MESSAGE);
      break;
    }
  }

  private void huyVe() {
    int row = tableVeCuaToi.getSelectedRow();
    if (row < 0) {
      JOptionPane.showMessageDialog(this, "Vui lòng chọn một vé!", "Thông báo", JOptionPane.WARNING_MESSAGE);
      return;
    }

    String maVe = (String) modelVeCuaToi.getValueAt(row, 0);
    VeMayBay ve = dsVe.timKiemTheoMa(maVe);

    if (ve == null) {
      JOptionPane.showMessageDialog(this, "Không tìm thấy vé!", "Lỗi", JOptionPane.ERROR_MESSAGE);
      return;
    }

    // Kiểm tra khả năng hủy vé
    String khaNangHuy = khachHangDangNhap.kiemTraKhaNangHuyVe(ve);
    if (!khaNangHuy.equals("Có thể hủy")) {
      JOptionPane.showMessageDialog(this,
          "Không thể hủy vé:\n" + khaNangHuy,
          "Không thể hủy", JOptionPane.WARNING_MESSAGE);
      return;
    }

    int confirm = JOptionPane.showConfirmDialog(this,
        "Bạn có chắc chắn muốn hủy vé " + maVe + "?",
        "Xác nhận hủy vé",
        JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
      if (khachHangDangNhap.huyVe(ve)) {
        // Cập nhật số ghế trống của chuyến bay
        ChuyenBay cb = dsChuyenBay.timKiemTheoMa(ve.getMaChuyen());
        if (cb != null) {
          cb.setSoGheTrong(cb.getSoGheTrong() + 1);
        }

        JOptionPane.showMessageDialog(this, "Đã hủy vé thành công!", "Thành công",
            JOptionPane.INFORMATION_MESSAGE);
        taiVeCuaToi();
        taiLichSu();
      } else {
        JOptionPane.showMessageDialog(this, "Hủy vé thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private void capNhatThongTinCaNhan() {
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    if (khachHangDangNhap != null) {
      txtHoTen.setText(khachHangDangNhap.getHoTen());
      txtEmail.setText(khachHangDangNhap.getEmail());
      txtSoDT.setText(khachHangDangNhap.getSoDT());
      txtDiaChi.setText(khachHangDangNhap.getDiaChi());
      txtCmnd.setText(khachHangDangNhap.getCmnd());
      txtNgaySinh.setText(sdf.format(khachHangDangNhap.getNgaySinh()));

      // Set giới tính
      String gioiTinh = khachHangDangNhap.getGioiTinh();
      if (gioiTinh != null) {
        cbGioiTinh.setSelectedItem(gioiTinh);
      }

      // Cập nhật thông tin thành viên
      lblHangKhachHang.setText("Hạng: " + khachHangDangNhap.getHangKhachHangText());
      lblDiemTichLuy.setText("Điểm tích lũy: " + khachHangDangNhap.getDiemTichLuy());
    }
  }

  private void capNhatThongTin() {
    if (khachHangDangNhap == null) {
      JOptionPane.showMessageDialog(this,
          "Không tìm thấy thông tin khách hàng! Vui lòng đăng nhập lại.",
          "Lỗi", JOptionPane.ERROR_MESSAGE);
      return;
    }

    try {
      // Lấy dữ liệu từ các trường nhập liệu
      String hoTen = txtHoTen.getText().trim();
      String email = txtEmail.getText().trim();
      String soDT = txtSoDT.getText().trim();
      String diaChi = txtDiaChi.getText().trim();
      String gioiTinh = (String) cbGioiTinh.getSelectedItem();
      String cccd = txtCmnd.getText().trim();
      String ngaySinhStr = txtNgaySinh.getText().trim();

      // Validate dữ liệu đầu vào
      StringBuilder loi = new StringBuilder();

      if (hoTen.isEmpty())
        loi.append("• Họ tên không được để trống\n");
      if (email.isEmpty())
        loi.append("• Email không được để trống\n");
      if (soDT.isEmpty())
        loi.append("• Số điện thoại không được để trống\n");
      if (diaChi.isEmpty())
        loi.append("• Địa chỉ không được để trống\n");
      if (cccd.isEmpty())
        loi.append("• CCCD không được để trống\n");
      if (ngaySinhStr.isEmpty())
        loi.append("• Ngày sinh không được để trống\n");

      if (loi.length() > 0) {
        JOptionPane.showMessageDialog(this,
            "Vui lòng sửa các lỗi sau:\n" + loi.toString(),
            "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        return;
      }

      // Parse ngày sinh
      SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
      sdf.setLenient(false);
      Date ngaySinh = sdf.parse(ngaySinhStr);

      // Cập nhật thông tin
      if (khachHangDangNhap.capNhatThongTinCaNhan(hoTen, soDT, email, diaChi, gioiTinh)) {
        khachHangDangNhap.setCmnd(cccd);
        khachHangDangNhap.setNgaySinh(ngaySinh);
        khachHangDangNhap.setHoTen(hoTen);
        khachHangDangNhap.setDiaChi(diaChi);
        khachHangDangNhap.setGioiTinh(gioiTinh);
        khachHangDangNhap.setSoDT(soDT);
        khachHangDangNhap.setEmail(email);
        int index = -1;
        for (int i = 0; i < quanLy.getDsKhachHang().getDanhSach().size(); i++) {
          if (quanLy.getDsKhachHang().getDanhSach().get(i).getMa().equals(khachHangDangNhap.getMa())) {
            index = i;
            break;
          }
        }
        quanLy.getDsKhachHang().getDanhSach().set(index, khachHangDangNhap);
        quanLy.ghiDuLieuRaFile();
        JOptionPane.showMessageDialog(this,
            "Cập nhật thông tin thành công!",
            "Thành công",
            JOptionPane.INFORMATION_MESSAGE);

        // Cập nhật lại hiển thị
        capNhatThongTinCaNhan();
        lblWelcome.setText("Xin chào, " + khachHangDangNhap.getHoTen() + "! - Hạng: "
            + khachHangDangNhap.getHangKhachHangText());

      } else {
        JOptionPane.showMessageDialog(this,
            "Cập nhật thông tin thất bại!",
            "Lỗi",
            JOptionPane.ERROR_MESSAGE);
      }

    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this,
          "Có lỗi xảy ra khi cập nhật thông tin:\n" + ex.getMessage(),
          "Lỗi hệ thống",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  public static boolean showDangNhap(QuanLyBanVeMayBay quanLy) {
    JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JTextField txtMaKH = new JTextField(15);
    JPasswordField txtMatKhau = new JPasswordField(15);

    panel.add(new JLabel("Mã khách hàng:"));
    panel.add(txtMaKH);
    panel.add(new JLabel("Mật khẩu:"));
    panel.add(txtMatKhau);
    panel.add(new JLabel(""));
    panel.add(new JLabel(""));

    int result = JOptionPane.showConfirmDialog(null, panel, "Đăng nhập hệ thống",
        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
      String maKH = txtMaKH.getText().trim();
      String matKhau = new String(txtMatKhau.getPassword());

      if (maKH.isEmpty() || matKhau.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin!", "Lỗi",
            JOptionPane.ERROR_MESSAGE);
        return false;
      }

      UsersGUI usersGUI = new UsersGUI(quanLy);
      if (usersGUI.dangNhap(maKH, matKhau)) {
        usersGUI.setVisible(true);
        return true;
      } else {
        JOptionPane.showMessageDialog(null,
            "Mã khách hàng hoặc mật khẩu không đúng!\nVui lòng kiểm tra lại.",
            "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
      }
    }
    return false;
  }

  public static void main(String[] args) {
    QuanLyBanVeMayBay quanLy = new QuanLyBanVeMayBay();
    quanLy.docDuLieuTuFile();
    SwingUtilities.invokeLater(() -> {
      UsersGUI.showDangNhap(quanLy);
    });
  }
}
