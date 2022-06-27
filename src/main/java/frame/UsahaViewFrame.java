package frame;

import helpers.Koneksi;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.text.NumberFormat;
import java.util.Locale;

public class UsahaViewFrame extends JFrame{
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

    public UsahaViewFrame(){
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
            UsahaInputFrame inputFrame = new UsahaInputFrame();
            inputFrame.setId(id);
            inputFrame.isiKomponen();
            inputFrame.setVisible(true);
        });
        tambahButton.addActionListener(e -> {
            UsahaInputFrame inputFrame = new UsahaInputFrame();
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
                String deleteSQL = "DELETE FROM usaha WHERE id = ?";

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
            String searchSQL = "SELECT K.*,B.nama AS nama_perusahaan FROM usaha AS K " +
                    "LEFT JOIN perusahaan AS B ON K.perusahaan_id = B.id " +
                    "WHERE B.nama like ? OR K.nama like ?";

            try {
                PreparedStatement ps = c.prepareStatement(searchSQL);
                ps.setString(1, keyword);
                ps.setString(2, keyword);
                ResultSet rs = ps.executeQuery();

                DefaultTableModel dtm = (DefaultTableModel) viewTable.getModel();
                dtm.setRowCount(0);
                Object[] row = new Object[6];

                while (rs.next()){
                    NumberFormat nf = NumberFormat.getInstance(Locale.US);
                    String rowJCabang = nf.format(rs.getInt("jumlah_cabang"));
                    String rowPendapatan = String.format("%,.2f",rs.getDouble("pendapatan"));

                    row[0] = rs.getInt("id");
                    row[1] = rs.getString("nama");
                    row[2] = rs.getString("nama_perusahaan");
                    row[3] = rs.getString("klasifikasi");
                    row[4] = rowJCabang;
                    row[5] = rowPendapatan;
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

    public void init()  {
        setContentPane(mainPanel);
        setTitle("Data Usaha");
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public void isiTable() {
        Connection c = Koneksi.getConnection();
        String selectSQL = "SELECT K.*,B.nama AS nama_perusahaan FROM usaha AS K " +
                "LEFT JOIN perusahaan AS B ON K.perusahaan_id = B.id";

        try {
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(selectSQL);

            String header[] = {"Id","Nama Usaha","Nama Perusahaan","Klasifikasi","Jumlah Cabang","Pendapatan"};
            DefaultTableModel dtm = new DefaultTableModel(header, 0);
            viewTable.setModel(dtm);

            viewTable.getColumnModel().getColumn(0).setMaxWidth(32);
            viewTable.getColumnModel().getColumn(1).setMinWidth(150);
            viewTable.getColumnModel().getColumn(2).setMinWidth(150);

            DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
            rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
            viewTable.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
            viewTable.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);

            Object[] row = new Object[6];
            while (rs.next()){
                NumberFormat nf = NumberFormat.getInstance(Locale.US);
                String rowJCabang = nf.format(rs.getInt("jumlah_cabang"));
                String rowPendapatan = String.format("%,.2f",rs.getDouble("pendapatan"));

                row[0] = rs.getInt("id");
                row[1] = rs.getString("nama");
                row[2] = rs.getString("nama_perusahaan");
                row[3] = rs.getString("klasifikasi");
                row[4] = rowJCabang;
                row[5] = rowPendapatan;
                dtm.addRow(row);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
