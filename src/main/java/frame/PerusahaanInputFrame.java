package frame;

import helpers.Koneksi;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PerusahaanInputFrame  extends JFrame{
    private JPanel mainPanel;
    private JTextField idTextField;
    private JTextField namaTextField;
    private JButton simpanButton;
    private JButton batalButton;
    private JPanel buttonPanel;

    private int id;

    public void setId(int id) {
        this.id = id;
    }

    public PerusahaanInputFrame(){
        simpanButton.addActionListener(e -> {
            String nama = namaTextField.getText();

            if (nama.equals("")){
                JOptionPane.showMessageDialog(null,
                        "isi nama perusahaan", "" +
                                "validasi data kosong",
                        JOptionPane.WARNING_MESSAGE);
                namaTextField.requestFocus();
                return;
            }

            Connection c = Koneksi.getConnection();
            PreparedStatement ps;

            try {
                if (id == 0){
                    String cekSQL = "SELECT * FROM perusahaan WHERE nama = ?";
                    ps = c.prepareStatement(cekSQL);
                    ps.setString(1, nama);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()){
                        JOptionPane.showMessageDialog(null,
                                "Data nama perusahaan sama sudah ada",
                                "validasi data sama",
                                JOptionPane.WARNING_MESSAGE);
                    } else {
                        String insertSQL = "INSERT INTO perusahaan SET nama = ?";
                        ps = c.prepareStatement(insertSQL);
                        ps.setString(1,nama);
                        ps.executeUpdate();
                        dispose();
                    }
                } else {
                    String cekSQL = "SELECT * FROM perusahaan WHERE nama = ? AND id != ?";
                    ps = c.prepareStatement(cekSQL);
                    ps.setString(1, nama);
                    ps.setInt(2, id);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()){
                        JOptionPane.showMessageDialog(null,
                                "Data nama perusahaan sama sudah ada",
                                "validasi data sama",
                                JOptionPane.WARNING_MESSAGE);
                    } else {
                        String updateSQL = "UPDATE perusahaan SET nama = ? WHERE id = ?";
                        ps = c.prepareStatement(updateSQL);
                        ps.setString(1,nama);
                        ps.setInt(2,id);
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
        init();
    }

    public void init(){
        setContentPane(mainPanel);
        setTitle("Input Perusahaan");
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public void isiKomponen(){
        Connection c = Koneksi.getConnection();
        String findSQL = "SELECT * FROM perusahaan WHERE id = ?";
        PreparedStatement ps;

        try {
            ps = c.prepareStatement(findSQL);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                idTextField.setText(String.valueOf(rs.getInt("id")));
                namaTextField.setText(rs.getString("nama"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
