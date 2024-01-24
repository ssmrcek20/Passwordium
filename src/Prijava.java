import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
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
            final LoginNadzornik loginNadzornik = LoginNadzornik.getInstance();
            @Override
            public void actionPerformed(ActionEvent e) {
                String korIme = txtKorIme.getText();
                String lozinka = new String(txtLozinka.getPassword());
                HasherLozinke hasherLozinke = new HasherLozinke();
                HttpRequestManager httpRequestManager = null;
                try {
                    httpRequestManager = new HttpRequestManager();
                } catch (MalformedURLException ex) {
                    throw new RuntimeException(ex);
                }
                String odgovor = null;
                try {
                    odgovor = httpRequestManager.sendLoginRequest(korIme,lozinka);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                if(odgovor!=null){
                    loginNadzornik.korime = korIme;
                    loginNadzornik.jwtToken = odgovor;
                    PrikazSifri prikazSifri= new PrikazSifri();
                    prikazSifri.podaci(korIme, lozinka);
                    try {
                        prikazSifri.prikazPodataka();
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                    System.out.println(odgovor);
                    Prijava.this.dispose();
                }else{
                    loginNadzornik.neuspjeliPokusaj();
                    if(loginNadzornik.viseOdTriPokusaja()){
                        loginNadzornik.zakljucajLogin(btnPrijava);
                        JOptionPane.showMessageDialog(Prijava.this, "Kriva lozinka!\nPreviše neuspjelih pokušaja pokušajte ponovno za 60s");
                    }else{
                        JOptionPane.showMessageDialog(Prijava.this, "Kriva lozinka!\nPreostalo pokušaja " + (3-loginNadzornik.brojNeuspjelihPokusaja()) );
                    }
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
