package Views;

import Responses.LoginResponse;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.AuthProvider;
import Services.LoginNadzornik;
import Services.UserService;

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

        setContentPane(panPrijava);

        setSize(1080, 720);
        setLocationRelativeTo(null);

        setVisible(true);

        btnPrijava.setBorderPainted(false);
        btnPrijava.setBackground(new Color(200,200,200));
        btnPrijava.setFocusPainted(false);
        btnPrijava.addActionListener(new ActionListener() {
            final LoginNadzornik loginNadzornik = new LoginNadzornik();
            final UserService userService = new UserService();
            @Override
            public void actionPerformed(ActionEvent e) {
                String korIme = txtKorIme.getText();
                char[] passwordChars = txtLozinka.getPassword();
                String lozinka = new String(passwordChars);
                try {

                    LoginResponse response =
                            userService.login(korIme, lozinka);


                    // ovdje:
                    // 1. iz lozinke + VaultSalt deriviraš KEK
                    // 2. decryptaš VaultKey
                    // 3. spremiš VaultKey samo u RAM

                    Prijava.this.dispose();

                } catch (Exception ex) {

                    loginNadzornik.neuspjeliPokusaj();

                    if (loginNadzornik.viseOdTriPokusaja()) {
                        loginNadzornik.zakljucajLogin(btnPrijava);
                    }

                } finally {
                    java.util.Arrays.fill(passwordChars, '\0');
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
            public void actionPerformed(ActionEvent e) {/*new Registracija();*/}
        });

    }

}
