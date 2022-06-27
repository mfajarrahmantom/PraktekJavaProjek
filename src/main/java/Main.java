import frame.PerusahaanViewFrame;
import frame.UsahaViewFrame;
import helpers.Koneksi;

public class Main {
    public static void main(String[] args) {
        Koneksi.getConnection();
        PerusahaanViewFrame pviewFrame = new PerusahaanViewFrame();
        pviewFrame.setVisible(true);

//        UsahaViewFrame uviewFrame = new UsahaViewFrame();
//        uviewFrame.setVisible(true);
    }
}
