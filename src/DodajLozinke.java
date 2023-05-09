import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class DodajLozinke extends JFrame{
    private JPanel panDodaj;
    private JTextField txtKorIme;
    private JPasswordField txtLozinka;
    private JTextField txtNaziv;
    private JTextField txtLink;
    private JButton btnGenerirajLozinku;
    private JButton btnDodaj;
    private String korImeKorisnika;
    private String lozinkaKorisnika;

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
        btnDodaj.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                KripterPodataka kripterPodataka = new KripterPodataka();
                Racun racun = new Racun(txtNaziv.getText(),txtKorIme.getText(),new String(txtLozinka.getPassword()),txtLink.getText());
                try {
                    kripterPodataka.spremiPodatke(racun,korImeKorisnika,lozinkaKorisnika);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    public void podaci(String korIme, String lozinka) {
        this.korImeKorisnika = korIme;
        this.lozinkaKorisnika = lozinka;
    }
}
