import javax.swing.*;
import java.awt.*;

public class Prijava extends JFrame {
    private JPanel panPrijava;
    private JTextField txtKorIme;
    private JTextField txtLozinka;
    private JButton btnPrijava;
    private JButton btnRegistracija;



    public Prijava(){
        setTitle("Passwordium");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1080,720);
        setLocationRelativeTo(null);
        setVisible(true);
        setContentPane(panPrijava);

        btnPrijava.setBorderPainted(false);
        btnPrijava.setBackground(new Color(200,200,200));
        btnPrijava.setFocusPainted(false);

        btnRegistracija.setContentAreaFilled(false);
        btnRegistracija.setBorderPainted(false);
        btnRegistracija.setOpaque(false);
        btnRegistracija.setFocusPainted(false);
        btnRegistracija.setForeground(Color.blue);
    }

}
