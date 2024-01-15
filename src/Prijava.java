import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class Prijava extends JFrame {
    private JPanel panPrijava;
    private JTextField txtKorIme;
    private JButton btnPrijava;
    private JButton btnRegistracija;
    private JPasswordField txtLozinka;
    private JPasswordField txtTOTP;

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
        btnPrijava.addActionListener(new ActionListener() {
            final LoginNadzornik loginNadzornik = new LoginNadzornik();
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
                                prikazSifri.prikazPodataka();
                                Prijava.this.dispose();
                            } else {

                                loginNadzornik.neuspjeliPokusaj();
                                if(loginNadzornik.viseOdTriPokusaja()){
                                    loginNadzornik.zakljucajLogin(btnPrijava);
                                    JOptionPane.showMessageDialog(Prijava.this, "Kriva lozinka!\nPreviše neuspjelih pokušaja pokušajte ponovno za 60s");
                                }else{
                                    JOptionPane.showMessageDialog(Prijava.this, "Kriva lozinka!\nPreostalo pokušaja " + (3-loginNadzornik.brojNeuspjelihPokusaja()) );
                                }
                            }
                        } catch (NoSuchAlgorithmException ex) {
                            JOptionPane.showMessageDialog(Prijava.this, "Ne postoji SHA-256 na računalu!");
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(Prijava.this, "Došlo je do greške prilikom dohvata računa");
                        }
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(Prijava.this, "Došlo je do greške tijekom čitanja datoteke!");
                    }
                } else {
                    JOptionPane.showMessageDialog(Prijava.this,"Korisnik sa tim korisničkim imenom ne postoji!");
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
