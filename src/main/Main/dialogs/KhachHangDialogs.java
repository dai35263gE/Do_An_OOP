package Main.dialogs;

import Main.MainGUI;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import model.*;
import Sevice.*;

public class KhachHangDialogs {
    private QuanLyBanVeMayBay quanLy;
    private MainGUI mainGUI;
    private TableRowSorter<DefaultTableModel> sorter;

    public KhachHangDialogs(QuanLyBanVeMayBay quanLy, MainGUI mainGUI) {
        this.quanLy = quanLy;
        this.mainGUI = mainGUI;
        // Khởi tạo sorter cho bảng khách hàng
        this.sorter = new TableRowSorter<>((DefaultTableModel) mainGUI.getTableKhachHang().getModel());
        mainGUI.getTableKhachHang().setRowSorter(sorter);
    }

    // ========== DIALOG TÌM KIẾM VÀ LỌC ==========
    public void moDialogTimKiemLoc() {
        JDialog dialog = new JDialog(mainGUI, "Tìm Kiếm & Lọc Khách Hàng", true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(mainGUI);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Panel tìm kiếm
        JPanel panelTimKiem = new JPanel(new GridLayout(0, 2, 10, 10));
        panelTimKiem.setBorder(BorderFactory.createTitledBorder("Tìm kiếm theo từ khóa"));

        JTextField txtTimKiem = new JTextField();
        JComboBox<String> cboLoaiTimKiem = new JComboBox<>(new String[]{
            "Tất cả", "Mã KH", "Họ tên", "Số điện thoại", "Email", "CMND", "Địa chỉ"
        });

        panelTimKiem.add(new JLabel("Loại tìm kiếm:"));
        panelTimKiem.add(cboLoaiTimKiem);
        panelTimKiem.add(new JLabel("Từ khóa:"));
        panelTimKiem.add(txtTimKiem);

        // Panel lọc
        JPanel panelLoc = new JPanel(new GridLayout(0, 2, 10, 10));
        panelLoc.setBorder(BorderFactory.createTitledBorder("Lọc theo tiêu chí"));

        JComboBox<String> cboHangKhachHang = new JComboBox<>(new String[]{
            "Tất cả", "Đồng", "Bạc", "Vàng", "Bạch Kim", "Kim Cương"
        });

        JSpinner spinnerDiemTu = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 100));
        JSpinner spinnerDiemDen = new JSpinner(new SpinnerNumberModel(5000, 0, 10000, 100));

        JComboBox<String> cboGioiTinh = new JComboBox<>(new String[]{"Tất cả", "Nam", "Nữ"});

        panelLoc.add(new JLabel("Hạng khách hàng:"));
        panelLoc.add(cboHangKhachHang);
        panelLoc.add(new JLabel("Điểm tích lũy từ:"));
        panelLoc.add(spinnerDiemTu);
        panelLoc.add(new JLabel("Điểm tích lũy đến:"));
        panelLoc.add(spinnerDiemDen);
        panelLoc.add(new JLabel("Giới tính:"));
        panelLoc.add(cboGioiTinh);

        // Panel thống kê
        JPanel panelThongKe = new JPanel(new GridLayout(0, 2, 10, 5));
        panelThongKe.setBorder(BorderFactory.createTitledBorder("Thống kê kết quả"));

        JLabel lblTongSo = new JLabel("0");
        JLabel lblSoKetQua = new JLabel("0");
        JLabel lblHangCaoNhat = new JLabel("Không có");
        JLabel lblDiemTrungBinh = new JLabel("0");

        panelThongKe.add(new JLabel("Tổng số KH:"));
        panelThongKe.add(lblTongSo);
        panelThongKe.add(new JLabel("Số kết quả:"));
        panelThongKe.add(lblSoKetQua);
        panelThongKe.add(new JLabel("Hạng cao nhất:"));
        panelThongKe.add(lblHangCaoNhat);
        panelThongKe.add(new JLabel("Điểm trung bình:"));
        panelThongKe.add(lblDiemTrungBinh);

        // Panel button
        JPanel panelButton = new JPanel(new FlowLayout());
        JButton btnTimKiem = new JButton("Tìm Kiếm");
        JButton btnLoc = new JButton("Lọc");
        JButton btnXoaLoc = new JButton("Xóa Bộ Lọc");
        JButton btnDong = new JButton("Đóng");

        // Cập nhật thống kê ban đầu
        capNhatThongKe(lblTongSo, lblSoKetQua, lblHangCaoNhat, lblDiemTrungBinh);

        // Sự kiện tìm kiếm
        btnTimKiem.addActionListener(e -> {
            String tuKhoa = txtTimKiem.getText().trim();
            String loaiTimKiem = (String) cboLoaiTimKiem.getSelectedItem();
            
            if (tuKhoa.isEmpty()) {
                xoaLoc();
                return;
            }

            try {
                RowFilter<DefaultTableModel, Object> rf = null;
                switch (loaiTimKiem) {
                    case "Tất cả":
                        rf = RowFilter.regexFilter("(?i)" + tuKhoa);
                        break;
                    case "Mã KH":
                        rf = RowFilter.regexFilter("(?i)" + tuKhoa, 0);
                        break;
                    case "Họ tên":
                        rf = RowFilter.regexFilter("(?i)" + tuKhoa, 1);
                        break;
                    case "Số điện thoại":
                        rf = RowFilter.regexFilter("(?i)" + tuKhoa, 2);
                        break;
                    case "Email":
                        rf = RowFilter.regexFilter("(?i)" + tuKhoa, 3);
                        break;
                    case "CMND":
                        rf = RowFilter.regexFilter("(?i)" + tuKhoa, 4);
                        break;
                    case "Địa chỉ":
                        rf = RowFilter.regexFilter("(?i)" + tuKhoa, 6);
                        break;
                }
                
                sorter.setRowFilter(rf);
                capNhatThongKe(lblTongSo, lblSoKetQua, lblHangCaoNhat, lblDiemTrungBinh);
                
            } catch (PatternSyntaxException ex) {
                JOptionPane.showMessageDialog(dialog, "Từ khóa tìm kiếm không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Sự kiện lọc
        btnLoc.addActionListener(e -> {
            String hang = (String) cboHangKhachHang.getSelectedItem();
            int diemTu = (Integer) spinnerDiemTu.getValue();
            int diemDen = (Integer) spinnerDiemDen.getValue();
            String gioiTinh = (String) cboGioiTinh.getSelectedItem();

            List<RowFilter<DefaultTableModel, Object>> filters = new java.util.ArrayList<>();

            // Lọc theo hạng khách hàng
            if (!hang.equals("Tất cả")) {
                filters.add(RowFilter.regexFilter("(?i)" + hang, 5)); // Cột 5 là hạng
            }

            // Lọc theo điểm tích lũy
            filters.add(new RowFilter<DefaultTableModel, Object>() {
                public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                    try {
                        String diemStr = entry.getStringValue(6); // Cột 6 là điểm tích lũy
                        int diem = Integer.parseInt(diemStr.replaceAll("[^0-9]", ""));
                        return diem >= diemTu && diem <= diemDen;
                    } catch (Exception ex) {
                        return false;
                    }
                }
            });

            // Lọc theo giới tính - lọc từ đối tượng KhachHang
            if (!gioiTinh.equals("Tất cả")) {
                filters.add(new RowFilter<DefaultTableModel, Object>() {
                    public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                        try {
                            String maKH = entry.getStringValue(0); // Cột 0 là mã KH
                            KhachHang kh = quanLy.getDsKhachHang().timKiemTheoMa(maKH);
                            return kh != null && kh.getGioiTinh() != null && kh.getGioiTinh().equals(gioiTinh);
                        } catch (Exception ex) {
                            return false;
                        }
                    }
                });
            }

            if (!filters.isEmpty()) {
                sorter.setRowFilter(RowFilter.andFilter(filters));
                capNhatThongKe(lblTongSo, lblSoKetQua, lblHangCaoNhat, lblDiemTrungBinh);
            }
        });

        // Sự kiện xóa bộ lọc
        btnXoaLoc.addActionListener(e -> {
            xoaLoc();
            capNhatThongKe(lblTongSo, lblSoKetQua, lblHangCaoNhat, lblDiemTrungBinh);
        });

        btnDong.addActionListener(e -> dialog.dispose());

        panelButton.add(btnTimKiem);
        panelButton.add(btnLoc);
        panelButton.add(btnXoaLoc);
        panelButton.add(btnDong);

        // Thêm các panel vào dialog
        JPanel panelContent = new JPanel(new GridLayout(3, 1, 10, 10));
        panelContent.add(panelTimKiem);
        panelContent.add(panelLoc);
        panelContent.add(panelThongKe);

        mainPanel.add(panelContent, BorderLayout.CENTER);
        mainPanel.add(panelButton, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void capNhatThongKe(JLabel lblTongSo, JLabel lblSoKetQua, JLabel lblHangCaoNhat, JLabel lblDiemTrungBinh) {
        DefaultTableModel model = (DefaultTableModel) mainGUI.getTableKhachHang().getModel();
        int tongSo = model.getRowCount();
        int soKetQua = mainGUI.getTableKhachHang().getRowCount();
        
        lblTongSo.setText(String.valueOf(tongSo));
        lblSoKetQua.setText(String.valueOf(soKetQua));
        
        // Tính hạng cao nhất và điểm trung bình
        if (soKetQua > 0) {
            int tongDiem = 0;
            String hangCaoNhat = "Đồng";
            int diemCaoNhat = 0;
            
            for (int i = 0; i < soKetQua; i++) {
                try {
                    int row = mainGUI.getTableKhachHang().convertRowIndexToModel(i);
                    String diemStr = (String) model.getValueAt(row, 5);
                    String hang = (String) model.getValueAt(row, 7);
                    int diem = Integer.parseInt(diemStr.replaceAll("[^0-9]", ""));
                    
                    tongDiem += diem;
                    
                    // Xác định hạng cao nhất
                    int thuTuHang = getThuTuHang(hang);
                    int thuTuCaoNhat = getThuTuHang(hangCaoNhat);
                    if (thuTuHang > thuTuCaoNhat || (thuTuHang == thuTuCaoNhat && diem > diemCaoNhat)) {
                        hangCaoNhat = hang;
                        diemCaoNhat = diem;
                    }
                } catch (Exception e) {
                    // Bỏ qua lỗi
                }
            }
            
            lblHangCaoNhat.setText(hangCaoNhat);
            lblDiemTrungBinh.setText(String.format("%.1f", (double) tongDiem / soKetQua));
        } else {
            lblHangCaoNhat.setText("Không có");
            lblDiemTrungBinh.setText("0");
        }
    }

    private int getThuTuHang(String hang) {
        switch (hang) {
            case "Kim Cương": return 5;
            case "Bạch Kim": return 4;
            case "Vàng": return 3;
            case "Bạc": return 2;
            case "Đồng": return 1;
            default: return 0;
        }
    }

    private void xoaLoc() {
        sorter.setRowFilter(null);
    }

    // ========== DIALOG XEM CHI TIẾT KHÁCH HÀNG ==========
    public void xemChiTietKhachHang() {
        int selectedRow = mainGUI.getTableKhachHang().getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(mainGUI, "Vui lòng chọn một khách hàng!", "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maKH = (String) mainGUI.getTableKhachHang().getValueAt(selectedRow, 0);
        KhachHang khachHang = quanLy.getDsKhachHang().timKiemTheoMa(maKH);
        
        if (khachHang == null) {
            JOptionPane.showMessageDialog(mainGUI, "Không tìm thấy thông tin khách hàng!", "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(mainGUI, "Chi Tiết Khách Hàng - " + khachHang.getHoTen(), true);
        dialog.setSize(600, 750);
        dialog.setLocationRelativeTo(mainGUI);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Panel thông tin cơ bản
        JPanel panelThongTin = new JPanel(new GridLayout(0, 2, 10, 10));
        panelThongTin.setBorder(BorderFactory.createTitledBorder("Thông tin cá nhân"));

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        panelThongTin.add(new JLabel("Mã KH:"));
        panelThongTin.add(new JLabel(khachHang.getMa()));
        panelThongTin.add(new JLabel("Họ tên:"));
        panelThongTin.add(new JLabel(khachHang.getHoTen()));
        panelThongTin.add(new JLabel("CMND/CCCD:"));
        panelThongTin.add(new JLabel(khachHang.getCmnd()));
        panelThongTin.add(new JLabel("Số điện thoại:"));
        panelThongTin.add(new JLabel(khachHang.getSoDT()));
        panelThongTin.add(new JLabel("Email:"));
        panelThongTin.add(new JLabel(khachHang.getEmail()));
        panelThongTin.add(new JLabel("Ngày sinh:"));
        panelThongTin.add(new JLabel(khachHang.getNgaySinh() != null ? sdf.format(khachHang.getNgaySinh()) : "N/A"));
        panelThongTin.add(new JLabel("Giới tính:"));
        panelThongTin.add(new JLabel(khachHang.getGioiTinh()));
        panelThongTin.add(new JLabel("Địa chỉ:"));
        panelThongTin.add(new JLabel(khachHang.getDiaChi() != null ? khachHang.getDiaChi() : "N/A"));

        // Panel thông tin thành viên
        JPanel panelThanhVien = new JPanel(new GridLayout(0, 2, 10, 10));
        panelThanhVien.setBorder(BorderFactory.createTitledBorder("Thông tin thành viên"));

        panelThanhVien.add(new JLabel("Hạng khách hàng:"));
        panelThanhVien.add(new JLabel(khachHang.getHangKhachHang()));
        panelThanhVien.add(new JLabel("Điểm tích lũy:"));
        panelThanhVien.add(new JLabel(String.valueOf(khachHang.getDiemTichLuy())));
        panelThanhVien.add(new JLabel("Ngày đăng ký:"));
        panelThanhVien.add(new JLabel(khachHang.getNgayDangKy() != null ? sdf.format(khachHang.getNgayDangKy()) : "N/A"));
        panelThanhVien.add(new JLabel("Mức giảm giá:"));
        panelThanhVien.add(new JLabel(String.format("%.1f%%", khachHang.tinhMucGiamGia(100000) / 100000 * 100)));

        // Panel thống kê
        JPanel panelThongKe = new JPanel(new GridLayout(0, 2, 10, 5));
        panelThongKe.setBorder(BorderFactory.createTitledBorder("Thống kê đặt vé"));
        List<HoaDon> hoaDonList = khachHang.getLichSuHoaDon();
        for(HoaDon hd : quanLy.getDsHoaDon().getDanhSach()){
            if(hd.getKhachHang().getMa().equals(khachHang.getMa())) hoaDonList.add(hd);
        }
        
        int tongHoaDon = hoaDonList.size();
        int tongVe = 0;
        double tongTien = 0;

        for (HoaDon hd : hoaDonList) {
            tongVe += hd.getDanhSachVe().size();
            tongTien += hd.getTongTien();
        }

        panelThongKe.add(new JLabel("Tổng số hóa đơn:"));
        panelThongKe.add(new JLabel(String.valueOf(tongHoaDon)));
        panelThongKe.add(new JLabel("Tổng số vé đã đặt:"));
        panelThongKe.add(new JLabel(String.valueOf(tongVe)));
        panelThongKe.add(new JLabel("Tổng chi tiêu:"));
        panelThongKe.add(new JLabel(String.format("%,d VND", (int) tongTien)));
        panelThongKe.add(new JLabel("Hóa đơn gần nhất:"));
        panelThongKe.add(new JLabel(tongHoaDon > 0 ? 
            sdf.format(hoaDonList.get(tongHoaDon - 1).getNgayLap()) : "N/A"));

        // Panel button
        JPanel panelButton = new JPanel(new FlowLayout());
        JButton btnXemHoaDon = new JButton("Xem Hóa Đơn");
        JButton btnSua = new JButton("Sửa Thông Tin");
        JButton btnDong = new JButton("Đóng");

        btnXemHoaDon.addActionListener(e -> {
            dialog.dispose();
            xemChiTietHoaDon();
        });

        btnSua.addActionListener(e -> {
            dialog.dispose();
            suaKhachHang();
        });

        btnDong.addActionListener(e -> dialog.dispose());

        panelButton.add(btnXemHoaDon);
        panelButton.add(btnSua);
        panelButton.add(btnDong);

        // Sắp xếp layout
        JPanel panelInfoContainer = new JPanel(new GridLayout(3, 1, 10, 10));
        panelInfoContainer.add(panelThongTin);
        panelInfoContainer.add(panelThanhVien);
        panelInfoContainer.add(panelThongKe);

        mainPanel.add(panelInfoContainer, BorderLayout.CENTER);
        mainPanel.add(panelButton, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    // ========== DIALOG SỬA KHÁCH HÀNG ==========
    public void suaKhachHang() {
        int selectedRow = mainGUI.getTableKhachHang().getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(mainGUI, "Vui lòng chọn một khách hàng!", "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maKH = (String) mainGUI.getTableKhachHang().getValueAt(selectedRow, 0);
        KhachHang khachHang = quanLy.getDsKhachHang().timKiemTheoMa(maKH);
        
        if (khachHang == null) {
            JOptionPane.showMessageDialog(mainGUI, "Không tìm thấy thông tin khách hàng!", "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(mainGUI, "Sửa Thông Tin Khách Hàng - " + khachHang.getHoTen(), true);
        dialog.setSize(500, 550);
        dialog.setLocationRelativeTo(mainGUI);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Các component nhập liệu
        JTextField txtMaKH = new JTextField(khachHang.getMa());
        txtMaKH.setEditable(false); // Không cho sửa mã KH
        txtMaKH.setBackground(new Color(240, 240, 240));
        
        JTextField txtHoTen = new JTextField(khachHang.getHoTen());
        JTextField txtSoDT = new JTextField(khachHang.getSoDT());
        JTextField txtEmail = new JTextField(khachHang.getEmail());
        JTextField txtCMND = new JTextField(khachHang.getCmnd());
        txtCMND.setEditable(false); // Không cho sửa CMND
        txtCMND.setBackground(new Color(240, 240, 240));
        
        JTextField txtNgaySinh = new JTextField(khachHang.getNgaySinh() != null ? 
            new SimpleDateFormat("dd/MM/yyyy").format(khachHang.getNgaySinh()) : "");
        JTextField txtDiaChi = new JTextField(khachHang.getDiaChi() != null ? khachHang.getDiaChi() : "");
        
        JComboBox<String> cboGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});
        cboGioiTinh.setSelectedItem(khachHang.getGioiTinh());
        
        JSpinner spinnerDiemTichLuy = new JSpinner(new SpinnerNumberModel(khachHang.getDiemTichLuy(), 0, 10000, 1));

        // Thêm components vào panel
        panel.add(new JLabel("Mã KH:"));
        panel.add(txtMaKH);
        panel.add(new JLabel("Họ tên:*"));
        panel.add(txtHoTen);
        panel.add(new JLabel("Số điện thoại:*"));
        panel.add(txtSoDT);
        panel.add(new JLabel("Email:*"));
        panel.add(txtEmail);
        panel.add(new JLabel("CMND/CCCD:"));
        panel.add(txtCMND);
        panel.add(new JLabel("Ngày sinh (dd/MM/yyyy):"));
        panel.add(txtNgaySinh);
        panel.add(new JLabel("Giới tính:"));
        panel.add(cboGioiTinh);
        panel.add(new JLabel("Địa chỉ:"));
        panel.add(txtDiaChi);
        panel.add(new JLabel("Điểm tích lũy:"));
        panel.add(spinnerDiemTichLuy);

        // Panel button
        JPanel panelButton = new JPanel(new FlowLayout());
        JButton btnLuu = new JButton("Lưu Thay Đổi");
        JButton btnHuy = new JButton("Hủy");

        btnLuu.addActionListener(e -> {
            // Validate dữ liệu
            if (txtHoTen.getText().trim().isEmpty() ||
                txtSoDT.getText().trim().isEmpty() ||
                txtEmail.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(dialog,
                        "Vui lòng nhập đầy đủ thông tin bắt buộc (*)",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // Lấy dữ liệu từ form
                String hoTen = txtHoTen.getText().trim();
                String soDT = txtSoDT.getText().trim();
                String email = txtEmail.getText().trim();
                String ngaySinhStr = txtNgaySinh.getText().trim();
                String gioiTinh = (String) cboGioiTinh.getSelectedItem();
                String diaChi = txtDiaChi.getText().trim();
                int diemTichLuy = (Integer) spinnerDiemTichLuy.getValue();

                // Parse ngày sinh
                java.util.Date ngaySinh = null;
                if (!ngaySinhStr.isEmpty()) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        sdf.setLenient(false);
                        ngaySinh = sdf.parse(ngaySinhStr);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog, 
                            "Định dạng ngày sinh không hợp lệ. Vui lòng nhập theo định dạng dd/MM/yyyy", 
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                // Cập nhật thông tin khách hàng
                khachHang.setHoTen(hoTen);
                khachHang.setSoDT(soDT);
                khachHang.setEmail(email);
                khachHang.setGioiTinh(gioiTinh);
                khachHang.setDiaChi(diaChi);
                khachHang.setDiemTichLuy(diemTichLuy);
                if (ngaySinh != null) {
                    khachHang.setNgaySinh(ngaySinh);
                }

                // Lưu thay đổi
                int index = -1;
                for(KhachHang kh : quanLy.getDsKhachHang().getDanhSach()){
                    index++;
                    if(kh.getCmnd().equals(khachHang.getCmnd()))
                    break;
                }
                quanLy.getDsKhachHang().getDanhSach().set(index, khachHang);
                
                if (index > 0) {
                    // Ghi dữ liệu vào file XML
                    boolean ghiFileThanCong = quanLy.getDsKhachHang().ghiFile("src/resources/data/2_KhachHangs.xml");
                    
                    if (ghiFileThanCong) {
                        JOptionPane.showMessageDialog(dialog, 
                            "Cập nhật thông tin khách hàng thành công!", 
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        
                        // Đóng dialog và cập nhật giao diện
                        dialog.dispose();
                        mainGUI.capNhatDuLieuGUI();
                    } else {
                        JOptionPane.showMessageDialog(dialog, 
                            "Lỗi ghi file dữ liệu!", 
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(dialog, 
                        "Không thể cập nhật thông tin khách hàng!", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Lỗi khi cập nhật thông tin: " + ex.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        btnHuy.addActionListener(e -> dialog.dispose());

        panelButton.add(btnLuu);
        panelButton.add(btnHuy);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(panelButton, BorderLayout.SOUTH);
        
        dialog.getRootPane().setDefaultButton(btnLuu);
        dialog.setVisible(true);
    }

    // ========== DIALOG XÓA KHÁCH HÀNG ==========
    public void xoaKhachHang() {
        int selectedRow = mainGUI.getTableKhachHang().getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(mainGUI, "Vui lòng chọn một khách hàng!", "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maKH = (String) mainGUI.getTableKhachHang().getValueAt(selectedRow, 0);
        String tenKH = (String) mainGUI.getTableKhachHang().getValueAt(selectedRow, 1);
        KhachHang khachHang = quanLy.getDsKhachHang().timKiemTheoMa(maKH);
        
        if (khachHang == null) {
            JOptionPane.showMessageDialog(mainGUI, "Không tìm thấy thông tin khách hàng!", "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Kiểm tra xem khách hàng có hóa đơn không
        List<HoaDon> hoaDonList = khachHang.getLichSuHoaDon();
        boolean coHoaDon = !hoaDonList.isEmpty();

        StringBuilder message = new StringBuilder();
        message.append("Bạn có chắc chắn muốn xóa khách hàng này?\n\n");
        message.append("Mã KH: ").append(maKH).append("\n");
        message.append("Họ tên: ").append(tenKH).append("\n\n");

        if (coHoaDon) {
            message.append("⚠️ CẢNH BÁO: Khách hàng này có ").append(hoaDonList.size())
                   .append(" hóa đơn trong hệ thống.\n")
                   .append("Việc xóa có thể ảnh hưởng đến thống kê và lịch sử giao dịch.\n\n");
        }

        message.append("Thao tác này không thể hoàn tác!");

        int option = JOptionPane.showConfirmDialog(mainGUI, 
            message.toString(), 
            "Xác Nhận Xóa Khách Hàng", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE);

        if (option == JOptionPane.YES_OPTION) {
            try {
                boolean result = quanLy.getDsKhachHang().xoa(maKH);
                
                if (result) {
                    JOptionPane.showMessageDialog(mainGUI, 
                        "Đã xóa khách hàng thành công!", 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    mainGUI.capNhatDuLieuGUI();
                } else {
                    JOptionPane.showMessageDialog(mainGUI, 
                        "Không thể xóa khách hàng!\nCó thể khách hàng đang có giao dịch trong hệ thống.", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainGUI, 
                    "Lỗi khi xóa khách hàng: " + ex.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ========== DIALOG THÊM KHÁCH HÀNG MỚI ==========
    public void moDialogThemKhachHang() {
        JDialog dialog = new JDialog(mainGUI, "Thêm Khách Hàng Mới", true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(mainGUI);
        dialog.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel lblTitle = new JLabel("👤 THÊM KHÁCH HÀNG MỚI");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin khách hàng"));

        // Tạo mã KH tự động
        int soKHHienTai = quanLy.getDsKhachHang().demSoLuong();
        String maKHTuDong = "KH" + String.format("%03d", soKHHienTai + 1);

        JTextField txtMaKH = new JTextField(maKHTuDong);
        txtMaKH.setEditable(false);
        txtMaKH.setBackground(new Color(240, 240, 240));
        
        JTextField txtHoTen = new JTextField();
        JTextField txtCMND = new JTextField();
        JTextField txtSoDT = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtNgaySinh = new JTextField();
        JComboBox<String> cboGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});
        JTextField txtDiaChi = new JTextField();

        // Thêm components vào form
        formPanel.add(new JLabel("Mã KH:"));
        formPanel.add(txtMaKH);
        formPanel.add(new JLabel("Họ tên:*"));
        formPanel.add(txtHoTen);
        formPanel.add(new JLabel("CMND/CCCD:*"));
        formPanel.add(txtCMND);
        formPanel.add(new JLabel("Số điện thoại:*"));
        formPanel.add(txtSoDT);
        formPanel.add(new JLabel("Email:*"));
        formPanel.add(txtEmail);
        formPanel.add(new JLabel("Ngày sinh (dd/MM/yyyy):"));
        formPanel.add(txtNgaySinh);
        formPanel.add(new JLabel("Giới tính:"));
        formPanel.add(cboGioiTinh);
        formPanel.add(new JLabel("Địa chỉ:"));
        formPanel.add(txtDiaChi);

        // Panel button
        JPanel panelButton = new JPanel(new FlowLayout());
        JButton btnThem = new JButton("Thêm Khách Hàng");
        JButton btnHuy = new JButton("Hủy");
        JButton btnLamMoi = new JButton("Làm Mới");

        // Style buttons
        btnThem.setBackground(new Color(60, 179, 113));
        btnThem.setForeground(Color.WHITE);
        btnLamMoi.setBackground(new Color(255, 165, 0));
        btnLamMoi.setForeground(Color.WHITE);

        btnThem.addActionListener(e -> {
            if (!validateThemKhachHang(dialog, txtHoTen, txtCMND, txtSoDT, txtEmail)) {
                return;
            }

            try {
                // Lấy dữ liệu từ form
                String hoTen = txtHoTen.getText().trim();
                String cmnd = txtCMND.getText().trim();
                String soDT = txtSoDT.getText().trim();
                String email = txtEmail.getText().trim();
                String ngaySinhStr = txtNgaySinh.getText().trim();
                String gioiTinh = (String) cboGioiTinh.getSelectedItem();
                String diaChi = txtDiaChi.getText().trim();

                // Parse ngày sinh
                java.util.Date ngaySinh = null;
                if (!ngaySinhStr.isEmpty()) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        sdf.setLenient(false);
                        ngaySinh = sdf.parse(ngaySinhStr);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog, 
                            "Định dạng ngày sinh không hợp lệ!", 
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                // Kiểm tra CMND đã tồn tại chưa
                if (quanLy.getDsKhachHang().timKiemTheoCMND(cmnd) != null) {
                    JOptionPane.showMessageDialog(dialog, 
                        "CMND/CCCD đã tồn tại trong hệ thống!", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Tạo khách hàng mới
                KhachHang khachHangMoi = new KhachHang(
                    maKHTuDong, hoTen,  soDT, email,cmnd,
                     ngaySinh,gioiTinh, diaChi, "0000"
                );

                // Thêm vào danh sách
                boolean result = quanLy.getDsKhachHang().them(khachHangMoi);
                
                if (result) {
                    // Ghi dữ liệu vào file XML
                    boolean ghiFileThanCong = quanLy.getDsKhachHang().ghiFile("src/resources/data/2_KhachHangs.xml");
                    
                    if (ghiFileThanCong) {
                        JOptionPane.showMessageDialog(dialog, 
                            "Thêm khách hàng thành công!\n\n" +
                            "Mã KH: " + maKHTuDong + "\n" +
                            "Họ tên: " + hoTen + "\n" +
                            "CMND: " + cmnd, 
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        
                        // Đóng dialog và cập nhật giao diện
                        dialog.dispose();
                        mainGUI.capNhatDuLieuGUI();
                    } else {
                        JOptionPane.showMessageDialog(dialog, 
                            "Lỗi ghi file dữ liệu!", 
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(dialog, 
                        "Không thể thêm khách hàng!", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Lỗi khi thêm khách hàng: " + ex.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        btnLamMoi.addActionListener(e -> {
            // Tạo mã KH mới
            int soKHMoi = quanLy.getDsKhachHang().demSoLuong();
            String maKHMoi = "KH" + String.format("%04d", soKHMoi + 1);
            txtMaKH.setText(maKHMoi);
            
            // Reset các field
            txtHoTen.setText("");
            txtCMND.setText("");
            txtSoDT.setText("");
            txtEmail.setText("");
            txtNgaySinh.setText("");
            cboGioiTinh.setSelectedIndex(0);
            txtDiaChi.setText("");
            
            JOptionPane.showMessageDialog(dialog, "Đã làm mới form!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });

        btnHuy.addActionListener(e -> dialog.dispose());

        panelButton.add(btnThem);
        panelButton.add(btnLamMoi);
        panelButton.add(btnHuy);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(panelButton, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private boolean validateThemKhachHang(JDialog dialog, JTextField txtHoTen, JTextField txtCMND, 
                                         JTextField txtSoDT, JTextField txtEmail) {
        if (txtHoTen.getText().trim().isEmpty() ||
            txtCMND.getText().trim().isEmpty() ||
            txtSoDT.getText().trim().isEmpty() ||
            txtEmail.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(dialog,
                    "Vui lòng nhập đầy đủ thông tin bắt buộc (*)",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    // ========== DIALOG XEM CHI TIẾT HÓA ĐƠN ==========
    public void xemChiTietHoaDon() {
        int selectedRow = mainGUI.getTableKhachHang().getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(mainGUI, "Vui lòng chọn một khách hàng!", "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maKH = (String) mainGUI.getTableKhachHang().getValueAt(selectedRow, 0);
        KhachHang khachHang = quanLy.getDsKhachHang().timKiemTheoMa(maKH);
        
        if (khachHang == null) {
            JOptionPane.showMessageDialog(mainGUI, "Không tìm thấy thông tin khách hàng!", "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<HoaDon> hoaDonList = khachHang.getLichSuHoaDon();
        if (hoaDonList.isEmpty()) {
            JOptionPane.showMessageDialog(mainGUI, 
                "Khách hàng này chưa có hóa đơn nào!", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(mainGUI, "Lịch Sử Hóa Đơn - " + khachHang.getHoTen(), true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(mainGUI);
        dialog.setLayout(new BorderLayout());

        // Tạo bảng hiển thị hóa đơn
        String[] columns = {"Mã HĐ", "Ngày Lập", "Số Vé", "Tổng Tiền", "Trạng Thái", "Phương Thức TT"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (HoaDon hd : hoaDonList) {
            model.addRow(new Object[]{
                hd.getMaHoaDon(),
                new SimpleDateFormat("dd/MM/yyyy HH:mm").format(hd.getNgayLap()),
                hd.getDanhSachVe().size(),
                String.format("%,d VND", (int) hd.getTongTien()),
                hd.getTrangThai(),
                hd.getPhuongThucTT()
            });
        }

        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);

        // Panel button
        JPanel panelButton = new JPanel(new FlowLayout());
        JButton btnXemChiTiet = new JButton("Xem Chi Tiết Hóa Đơn");
        JButton btnInHoaDon = new JButton("In Hóa Đơn");
        JButton btnDong = new JButton("Đóng");

        btnXemChiTiet.addActionListener(e -> {
            int selectedHoaDonRow = table.getSelectedRow();
            if (selectedHoaDonRow < 0) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng chọn một hóa đơn!", "Thông báo", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            String maHD = (String) table.getValueAt(selectedHoaDonRow, 0);
            HoaDon hoaDon = timHoaDonTheoMa(hoaDonList, maHD);
            
            if (hoaDon != null) {
                hienThiChiTietHoaDon(hoaDon, khachHang);
            }
        });

        btnInHoaDon.addActionListener(e -> {
            int selectedHoaDonRow = table.getSelectedRow();
            if (selectedHoaDonRow < 0) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng chọn một hóa đơn!", "Thông báo", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            String maHD = (String) table.getValueAt(selectedHoaDonRow, 0);
            HoaDon hoaDon = timHoaDonTheoMa(hoaDonList, maHD);
            
            if (hoaDon != null) {
                inHoaDon(hoaDon, khachHang);
            }
        });

        btnDong.addActionListener(e -> dialog.dispose());

        panelButton.add(btnXemChiTiet);
        panelButton.add(btnInHoaDon);
        panelButton.add(btnDong);

        dialog.add(new JLabel("LỊCH SỬ HÓA ĐƠN - " + khachHang.getHoTen(), JLabel.CENTER), BorderLayout.NORTH);
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(panelButton, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private HoaDon timHoaDonTheoMa(List<HoaDon> hoaDonList, String maHD) {
        for (HoaDon hd : hoaDonList) {
            if (hd.getMaHoaDon().equals(maHD)) {
                return hd;
            }
        }
        return null;
    }

    private void hienThiChiTietHoaDon(HoaDon hoaDon, KhachHang khachHang) {
        JDialog dialog = new JDialog(mainGUI, "Chi Tiết Hóa Đơn - " + hoaDon.getMaHoaDon(), true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(mainGUI);
        dialog.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Thông tin hóa đơn
        JPanel panelThongTin = new JPanel(new GridLayout(0, 2, 10, 5));
        panelThongTin.setBorder(BorderFactory.createTitledBorder("Thông tin hóa đơn"));

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        panelThongTin.add(new JLabel("Mã hóa đơn:"));
        panelThongTin.add(new JLabel(hoaDon.getMaHoaDon()));
        panelThongTin.add(new JLabel("Ngày lập:"));
        panelThongTin.add(new JLabel(sdf.format(hoaDon.getNgayLap())));
        panelThongTin.add(new JLabel("Khách hàng:"));
        panelThongTin.add(new JLabel(khachHang.getHoTen()));
        panelThongTin.add(new JLabel("Số vé:"));
        panelThongTin.add(new JLabel(String.valueOf(hoaDon.getDanhSachVe().size())));
        panelThongTin.add(new JLabel("Tổng tiền:"));
        panelThongTin.add(new JLabel(String.format("%,d VND", (int) hoaDon.getTongTien())));
        panelThongTin.add(new JLabel("Trạng thái:"));
        panelThongTin.add(new JLabel(hoaDon.getTrangThai()));
        panelThongTin.add(new JLabel("Phương thức TT:"));
        panelThongTin.add(new JLabel(hoaDon.getPhuongThucTT()));

        // Danh sách vé
        JPanel panelVe = new JPanel(new BorderLayout());
        panelVe.setBorder(BorderFactory.createTitledBorder("Danh sách vé"));

        String[] columnsVe = {"Mã Vé", "Chuyến Bay", "Số Ghế", "Loại Vé", "Giá Vé"};
        DefaultTableModel modelVe = new DefaultTableModel(columnsVe, 0);

        for (VeMayBay ve : hoaDon.getDanhSachVe()) {
            ChuyenBay cb = quanLy.getDsChuyenBay().timKiemTheoMa(ve.getMaChuyen());
            String chuyenBay = cb != null ? cb.getDiemDi() + " → " + cb.getDiemDen() : "N/A";
            
            modelVe.addRow(new Object[]{
                ve.getMaVe(),
                chuyenBay,
                ve.getSoGhe(),
                ve.getClass().getSimpleName().replace("Ve", ""),
                String.format("%,d VND", (int) ve.getGiaVe())
            });
        }

        JTable tableVe = new JTable(modelVe);
        JScrollPane scrollPaneVe = new JScrollPane(tableVe);
        panelVe.add(scrollPaneVe, BorderLayout.CENTER);

        // Panel button
        JPanel panelButton = new JPanel(new FlowLayout());
        JButton btnDong = new JButton("Đóng");
        JButton btnIn = new JButton("In Hóa Đơn");

        btnIn.addActionListener(e -> inHoaDon(hoaDon, khachHang));
        btnDong.addActionListener(e -> dialog.dispose());

        panelButton.add(btnIn);
        panelButton.add(btnDong);

        mainPanel.add(panelThongTin, BorderLayout.NORTH);
        mainPanel.add(panelVe, BorderLayout.CENTER);
        mainPanel.add(panelButton, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void inHoaDon(HoaDon hoaDon, KhachHang khachHang) {
        // Tạo nội dung hóa đơn
        StringBuilder content = new StringBuilder();
        content.append("=== HÓA ĐƠN BÁN VÉ MÁY BAY ===\n\n");
        content.append("Mã hóa đơn: ").append(hoaDon.getMaHoaDon()).append("\n");
        content.append("Ngày lập: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(hoaDon.getNgayLap())).append("\n");
        content.append("Khách hàng: ").append(khachHang.getHoTen()).append("\n");
        content.append("CMND: ").append(khachHang.getCmnd()).append("\n");
        content.append("Số ĐT: ").append(khachHang.getSoDT()).append("\n\n");
        
        content.append("DANH SÁCH VÉ:\n");
        content.append("----------------------------------------\n");
        
        double tongTien = 0;
        for (VeMayBay ve : hoaDon.getDanhSachVe()) {
            ChuyenBay cb = quanLy.getDsChuyenBay().timKiemTheoMa(ve.getMaChuyen());
            String chuyenBay = cb != null ? cb.getDiemDi() + " → " + cb.getDiemDen() : "N/A";
            
            content.append("- ").append(ve.getMaVe()).append(" | ")
                   .append(chuyenBay).append(" | ")
                   .append(ve.getSoGhe()).append(" | ")
                   .append(ve.getClass().getSimpleName().replace("Ve", "")).append(" | ")
                   .append(String.format("%,d", (int) ve.getGiaVe())).append(" VND\n");
            
            tongTien += ve.getGiaVe();
        }
        
        content.append("----------------------------------------\n");
        content.append("TỔNG TIỀN: ").append(String.format("%,d", (int) tongTien)).append(" VND\n");
        content.append("Phương thức TT: ").append(hoaDon.getPhuongThucTT()).append("\n");
        content.append("Trạng thái: ").append(hoaDon.getTrangThai()).append("\n\n");
        content.append("Cảm ơn quý khách!\n");

        // Hiển thị trong dialog
        JTextArea textArea = new JTextArea(content.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JOptionPane.showMessageDialog(mainGUI, 
            new JScrollPane(textArea), 
            "Hóa Đơn - " + hoaDon.getMaHoaDon(), 
            JOptionPane.INFORMATION_MESSAGE);
    }
}