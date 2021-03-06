package frame;

import helpers.JasperDataSourceBuilder;
import helpers.Koneksi;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;

public class PerusahaanViewFrame extends JFrame{
    private JPanel mainPanel;
    private JTextField cariTextField;
    private JButton cariButton;
    private JTable viewTable;
    private JScrollPane viewScrollPane;
    private JPanel buttonPanel;
    private JPanel cariPanel;
    private JButton tambahButton;
    private JButton ubahButton;
    private JButton hapusButton;
    private JButton batalButton;
    private JButton cetakButton;
    private JButton tutupButton;

    public PerusahaanViewFrame(){
        cetakButton.addActionListener(e -> {
            Connection c = Koneksi.getConnection();
            String selectSQL = "SELECT * FROM perusahaan";
            Object[][] row;

            try {
                Statement s = c.createStatement(
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = s.executeQuery(selectSQL);
            rs.last();
            int jumlah = rs.getRow();
            row = new Object[jumlah][2];
            int i = 0;
            rs.beforeFirst();
            while (rs.next()){
                row[i][0] = rs.getInt("id");
                row[i][1] = rs.getString("nama");
                i++;
            }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            try {
                JasperReport jasperReport =
                        JasperCompileManager.compileReport("/Users/User/IdeaProjects/PraktekJavaProjek/src/main/resources/perusahaan_report.jrxml");
                        JasperPrint jasperPrint =
                JasperFillManager.fillReport(jasperReport,null, new
                JasperDataSourceBuilder(row));
                        JasperViewer viewer = new JasperViewer(jasperPrint, false);
                        viewer.setVisible(true);
            } catch (JRException ex) {
                throw new RuntimeException(ex);
            }
        });
        ubahButton.addActionListener(e -> {
            int barisTerpilih = viewTable.getSelectedRow();
            if(barisTerpilih < 0){
                JOptionPane.showMessageDialog(null,
                        "Pilih data dulu",
                        "validasi pilih data",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            TableModel tm = viewTable.getModel();
            int id = Integer.parseInt(tm.getValueAt(barisTerpilih, 0).toString());
            PerusahaanInputFrame inputFrame = new PerusahaanInputFrame();
            inputFrame.setId(id);
            inputFrame.isiKomponen();
            inputFrame.setVisible(true);
        });
        tambahButton.addActionListener(e -> {
            PerusahaanInputFrame inputFrame = new PerusahaanInputFrame();
            inputFrame.setVisible(true);
        });
        hapusButton.addActionListener(e -> {
            int barisTerpilih = viewTable.getSelectedRow();
            if(barisTerpilih < 0){
                JOptionPane.showMessageDialog(null,
                        "Pilih data dulu",
                        "validasi pilih data",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int pilihan = JOptionPane.showConfirmDialog(null,
                    "Yakin hapus?",
                    "Konfirmasi hapus",
                    JOptionPane.YES_NO_OPTION);

            if (pilihan == 0){
                TableModel tm = viewTable.getModel();
                int id = Integer.parseInt(tm.getValueAt(barisTerpilih, 0).toString());

                Connection c = Koneksi.getConnection();
                String deleteSQL = "DELETE FROM perusahaan WHERE id = ?";

                try {
                    PreparedStatement ps = c.prepareStatement(deleteSQL);
                    ps.setInt(1, id);
                    ps.executeUpdate();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }

        });
        cariButton.addActionListener(e -> {

            String keyword = cariTextField.getText();
            if (keyword.equals("")){
                JOptionPane.showMessageDialog(null,
                        "isi kata kunci pencarian", "" +
                        "validasi kata kunci pencarian kosong",
                        JOptionPane.WARNING_MESSAGE);
                cariTextField.requestFocus();
                return;
            }

            Connection c = Koneksi.getConnection();
            keyword = "%" + cariTextField.getText() + "%";
            String searchSQL = "SELECT * FROM perusahaan WHERE nama like ?";

            try {
                PreparedStatement ps = c.prepareStatement(searchSQL);
                ps.setString(1, keyword);
                ResultSet rs = ps.executeQuery();

                DefaultTableModel dtm = (DefaultTableModel) viewTable.getModel();
                dtm.setRowCount(0);
                Object[] row = new Object[2];

                while (rs.next()){
                    row[0] = rs.getInt("id");
                    row[1] = rs.getString("nama");
                    dtm.addRow(row);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        tutupButton.addActionListener(e -> {
            dispose();
        });
        batalButton.addActionListener(e -> {
            isiTable();
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                isiTable();
            }
        });
        isiTable();
        init();
    }

    public void init(){
        setContentPane(mainPanel);
        setTitle("Data Perusahaan");
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public void isiTable(){
        Connection c = Koneksi.getConnection();
        String selectSQL = "SELECT * FROM perusahaan";

        try {
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(selectSQL);

            String header[] = {"Id","Nama Perusahaan"};
            DefaultTableModel dtm = new DefaultTableModel(header, 0);
            viewTable.setModel(dtm);

            viewTable.getColumnModel().getColumn(0).setMaxWidth(32);

            Object[] row = new Object[2];
            while (rs.next()){
                row[0] = rs.getInt("id");
                row[1] = rs.getString("nama");
                dtm.addRow(row);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
