package frame;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import helpers.ComboBoxItem;
import helpers.Koneksi;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;

public class UsahaInputFrame  extends JFrame{
    private JPanel mainPanel;
    private JTextField idTextField;
    private JTextField namaTextField;
    private JButton simpanButton;
    private JButton batalButton;
    private JPanel buttonPanel;
    private JComboBox perusahaanComboBox;
    private JRadioButton jasaRadioButton;
    private JRadioButton barangRadioButton;
    private JPanel radiobuttonPanel;
    private JTextField jcabangTextField;
    private JTextField pendapatanTextField;
    private JLabel pendapatanLabel;
    private JTextField emailTextField;
    private JPanel tanggalPanel;
    private DatePicker tanggalBerdiriDatePicker;

    private ButtonGroup klasifikasiButtonGroup;

    private int id;

    public void setId(int id) {
        this.id = id;
    }

    public UsahaInputFrame(){
        simpanButton.addActionListener(e -> {
            String nama = namaTextField.getText();

            if (nama.equals("")){
                JOptionPane.showMessageDialog(null,
                        "isi nama usaha", "" +
                                "validasi data kosong",
                        JOptionPane.WARNING_MESSAGE);
                namaTextField.requestFocus();
                return;
            }

            ComboBoxItem item = (ComboBoxItem) perusahaanComboBox.getSelectedItem();
            int perusahaanId = item.getValue();
            if (perusahaanId == 0){
                JOptionPane.showMessageDialog(null,
                        "Pilih perusahaan", "" +
                                "validasi data kosong",
                        JOptionPane.WARNING_MESSAGE);
                perusahaanComboBox.requestFocus();
                return;
            }

            String klasifikasi = "";
            if (jasaRadioButton.isSelected()){
                klasifikasi = "Jasa";
            } else if (barangRadioButton.isSelected()){
                klasifikasi = "Barang";
            } else {
                JOptionPane.showMessageDialog(null,
                        "Pilih klasifikasi",
                        "validasi data kosong",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (jcabangTextField.getText().equals("")){
                jcabangTextField.setText("0");
            }
            int jcabang = Integer.parseInt(jcabangTextField.getText());
            if (jcabang == 0){
                JOptionPane.showMessageDialog(null,
                        "Isi Jumlah Cabang",
                        "validasi data kosong",
                        JOptionPane.WARNING_MESSAGE);
                jcabangTextField.requestFocus();
                return;
            }

            if (pendapatanTextField.getText().equals("")){
                pendapatanTextField.setText("0");
            }
            double pendapatan = Float.parseFloat(pendapatanTextField.getText());
            if (pendapatan == 0){
                JOptionPane.showMessageDialog(null,
                        "Isi Pendapatan",
                        "validasi data kosong",
                        JOptionPane.WARNING_MESSAGE);
                pendapatanTextField.requestFocus();
                return;
            }

            String email = emailTextField.getText();
            if (!email.contains("@") || !email.contains(".")){
                JOptionPane.showMessageDialog(null,
                        "Isi dengan email valid",
                        "validasi email",
                        JOptionPane.WARNING_MESSAGE);
                emailTextField.requestFocus();
                return;
            }

            String tanggalBerdiri = tanggalBerdiriDatePicker.getText();
            if (tanggalBerdiri.equals("")){
                JOptionPane.showMessageDialog(null,
                        "Isi tanggal berdiri",
                        "validasi tanggal",
                        JOptionPane.WARNING_MESSAGE);
                emailTextField.requestFocus();
                return;
            }

            Connection c = Koneksi.getConnection();
            PreparedStatement ps;

            try {
                if (id == 0){
                    String cekSQL = "SELECT * FROM usaha WHERE nama = ?";
                    ps = c.prepareStatement(cekSQL);
                    ps.setString(1, nama);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()){
                        JOptionPane.showMessageDialog(null,
                                "Data nama usaha sama sudah ada",
                                "validasi data sama",
                                JOptionPane.WARNING_MESSAGE);
                    } else {
                        String insertSQL = "INSERT INTO usaha SET nama = ?, perusahaan_id = ?, " +
                                "klasifikasi = ?, jumlah_cabang = ?, pendapatan = ?, email = ?, tanggal_berdiri = ?";
                        ps = c.prepareStatement(insertSQL);
                        ps.setString(1, nama);
                        ps.setInt(2, perusahaanId);
                        ps.setString(3, klasifikasi);
                        ps.setInt(4, jcabang);
                        ps.setDouble(5, pendapatan);
                        ps.setString(6, email);
                        ps.setString(7, tanggalBerdiri);
                        ps.executeUpdate();
                        dispose();
                    }
                } else {
                    String cekSQL = "SELECT * FROM usaha WHERE nama = ? AND id != ?";
                    ps = c.prepareStatement(cekSQL);
                    ps.setString(1, nama);
                    ps.setInt(2, id);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()){
                        JOptionPane.showMessageDialog(null,
                                "Data nama usaha sama sudah ada",
                                "validasi data sama",
                                JOptionPane.WARNING_MESSAGE);
                    } else {
                        String updateSQL = "UPDATE usaha SET nama = ?, perusahaan_id = ?, " +
                                "klasifikasi = ?, jumlah_cabang = ?, pendapatan = ?, email = ?, tanggal_berdiri = ? " +
                                "WHERE id = ?";
                        ps = c.prepareStatement(updateSQL);
                        ps.setString(1, nama);
                        ps.setInt(2, perusahaanId);
                        ps.setString(3, klasifikasi);
                        ps.setInt(4, jcabang);
                        ps.setDouble(5, pendapatan);
                        ps.setString(6, email);
                        ps.setString(7, tanggalBerdiri);
                        ps.setInt(8, id);
                        ps.executeUpdate();
                        dispose();
                    }
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

        });
        batalButton.addActionListener(e -> {
            dispose();
        });
        kustomisasiKomponen();
        init();
    }

    public void init(){
        setContentPane(mainPanel);
        setTitle("Input Usaha");
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public void isiKomponen(){
        Connection c = Koneksi.getConnection();
        String findSQL = "SELECT * FROM usaha WHERE id = ?";
        PreparedStatement ps;

        try {
            ps = c.prepareStatement(findSQL);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                idTextField.setText(String.valueOf(rs.getInt("id")));
                namaTextField.setText(rs.getString("nama"));
                int perusahaanId = rs.getInt("perusahaan_id");
                for (int i = 0; i < perusahaanComboBox.getItemCount(); i++) {
                    perusahaanComboBox.setSelectedIndex(i);
                    ComboBoxItem item = (ComboBoxItem) perusahaanComboBox.getSelectedItem();
                    if (perusahaanId == item.getValue()){
                        break;
                    }
                }
                String klasifikasi = rs.getString("klasifikasi");
                if (klasifikasi != null) {
                    if(klasifikasi.equals("Jasa")){
                        jasaRadioButton.setSelected(true);
                    } else if (klasifikasi.equals("Barang")){
                        barangRadioButton.setSelected(true);
                    }
                }
                jcabangTextField.setText(rs.getString("jumlah_cabang"));
                pendapatanTextField.setText(rs.getString("pendapatan"));
                emailTextField.setText(rs.getString("email"));
                tanggalBerdiriDatePicker.setText(rs.getString("tanggal_berdiri"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void kustomisasiKomponen(){
        Connection c = Koneksi.getConnection();
        String selectSQL = "SELECT * FROM perusahaan ORDER BY nama";

        try {
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(selectSQL);
            perusahaanComboBox.addItem(new ComboBoxItem(0, "Pilih Perusahaan"));
            while (rs.next()){
                perusahaanComboBox.addItem(new ComboBoxItem(
                        rs.getInt("id"),
                        rs.getString("nama")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        klasifikasiButtonGroup = new ButtonGroup();
        klasifikasiButtonGroup.add(jasaRadioButton);
        klasifikasiButtonGroup.add(barangRadioButton);

        pendapatanLabel.setText("Pendapatan (Rp.)");
        jcabangTextField.setHorizontalAlignment(SwingConstants.RIGHT);
        jcabangTextField.setText("0");
        jcabangTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                jcabangTextField.setEditable(
                        (e.getKeyChar() >= '0' && e.getKeyChar() <= '9' ||
                                e.getKeyChar() == KeyEvent.VK_BACK_SPACE ||
                                e.getKeyCode() == KeyEvent.VK_LEFT ||
                                e.getKeyCode() == KeyEvent.VK_RIGHT)
                );
            }
        });

        pendapatanTextField.setHorizontalAlignment(SwingConstants.RIGHT);
        pendapatanTextField.setText("0");
        pendapatanTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                pendapatanTextField.setEditable(
                        (e.getKeyChar() >= '0' && e.getKeyChar() <= '9' ||
                                e.getKeyChar() == KeyEvent.VK_BACK_SPACE ||
                                e.getKeyCode() == KeyEvent.VK_LEFT ||
                                e.getKeyCode() == KeyEvent.VK_RIGHT ||
                                e.getKeyCode() == KeyEvent.VK_PERIOD)
                );
            }
        });

        DatePickerSettings dps = new DatePickerSettings();
        dps.setFormatForDatesCommonEra("yyyy-MM-dd");
        tanggalBerdiriDatePicker.setSettings(dps);
    }
}
