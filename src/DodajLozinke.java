import javax.swing.*;
import java.awt.*;

public class DodajLozinke extends JFrame{
    private JPanel panDodaj;
    private JTextField txtKorIme;
    private JPasswordField txtLozinka;
    private JTextField txtNaziv;
    private JTextField txtLink;
    private JButton btnGenerirajLozinku;
    private JButton btnDodaj;

    public DodajLozinke(){
        setTitle("Passwordium");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1080,720);
        setLocationRelativeTo(null);
        setVisible(true);
        setContentPane(panDodaj);

        btnDodaj.setBorderPainted(false);
        btnDodaj.setBackground(new Color(200,200,200));
        btnDodaj.setFocusPainted(false);

        btnGenerirajLozinku.setBorderPainted(false);
        btnGenerirajLozinku.setBackground(new Color(200,200,200));
        btnGenerirajLozinku.setFocusPainted(false);
    }
}
