import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class Prijava extends JFrame {
    private JPanel panPrijava;
    private JTextField txtKorIme;
    private JButton btnPrijava;
    private JButton btnRegistracija;
    private JPasswordField txtLozinka;


    public Prijava(){
        setTitle("Passwordium");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1080,720);
        setLocationRelativeTo(null);
        setVisible(true);
        setContentPane(panPrijava);

        btnPrijava.setBorderPainted(false);
        btnPrijava.setBackground(new Color(200,200,200));
        btnPrijava.setFocusPainted(false);
        btnPrijava.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String korIme = txtKorIme.getText();
                String lozinka = new String(txtLozinka.getPassword());
                HasherLozinke hasherLozinke = new HasherLozinke();
                if(hasherLozinke.postojiHashDatoteka(korIme)) {
                    try {
                        String lozinkaIzDatoteke = hasherLozinke.dohvatiHash(korIme);
                        try {
                            String upisanaLozinka = hasherLozinke.napraviHash(korIme, lozinka);

                            if (Objects.equals(lozinkaIzDatoteke, upisanaLozinka)) {
                                PrikazSifri prikazSifri= new PrikazSifri();
                                prikazSifri.podaci(korIme, lozinka);
                                Prijava.this.dispose();
                            } else {
                                JOptionPane.showMessageDialog(Prijava.this, "Kriva lozinka!");
                            }
                        } catch (NoSuchAlgorithmException ex) {
                            JOptionPane.showMessageDialog(Prijava.this, "Ne postoji SHA-256 na računalu!");
                        }
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(Prijava.this, "Došlo je do greške tijekom čitanja datoteke!");
                    }
                } else {
                    JOptionPane.showMessageDialog(Prijava.this,"Korisničko ime ne postoji!");
                }
            }
        });

        btnRegistracija.setContentAreaFilled(false);
        btnRegistracija.setBorderPainted(false);
        btnRegistracija.setOpaque(false);
        btnRegistracija.setFocusPainted(false);
        btnRegistracija.setForeground(Color.blue);
        btnRegistracija.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Registracija();
            }
        });

    }

}
