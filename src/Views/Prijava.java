package Views;

import Responses.LoginResponse;
import Services.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Prijava extends JFrame {
    private JPanel panPrijava;
    private JTextField txtKorIme;
    private JButton btnPrijava;
    private JButton btnRegistracija;
    private JPasswordField txtLozinka;

    public Prijava() {
        setTitle("Passwordium");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setContentPane(panPrijava);

        setSize(1080, 720);
        setLocationRelativeTo(null);

        setVisible(true);

        btnPrijava.setBorderPainted(false);
        btnPrijava.setBackground(new Color(200, 200, 200));
        btnPrijava.setFocusPainted(false);
        btnPrijava.addActionListener(new ActionListener() {
            final LoginSurveyer loginSurveyer = new LoginSurveyer();
            final UserService userService = new UserService();
            final VaultCryptoService vaultCryptoService = new VaultCryptoService();

            @Override
            public void actionPerformed(ActionEvent e) {
                String korIme =
                        txtKorIme.getText();
                char[] passwordChars =
                        txtLozinka.getPassword();
                byte[] kek = null;
                try {
                    LoginResponse response = userService.login(korIme, new String(passwordChars));
                    kek = vaultCryptoService.deriveKek(passwordChars, response.getVaultSalt());
                    byte[] vaultKey = vaultCryptoService.decryptVaultKey(kek, response.getEncryptedVaultKey(),
                            response.getVaultKeyNonce(), response.getVaultKeyTag());
                    VaultSession.unlock(vaultKey, response.getJwt());
                    JOptionPane.showMessageDialog(Prijava.this, "Uspješna prijava!");
                    PrikazSifri prikazSifri = new PrikazSifri();
                    AutoLockService.start();
                    prikazSifri.setVisible(true);
                    Prijava.this.dispose();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    loginSurveyer.neuspjeliPokusaj();
                    if (loginSurveyer.viseOdTriPokusaja()) {
                        loginSurveyer.zakljucajLogin(btnPrijava);
                        JOptionPane.showMessageDialog(Prijava.this, "Previše neuspjelih pokušaja prijave.");
                    } else {
                        JOptionPane.showMessageDialog(Prijava.this, "Neispravno korisničko ime ili lozinka.");
                    }
                } finally {
                    java.util.Arrays.fill(passwordChars, '\0');
                    if (kek != null) {
                        java.util.Arrays.fill(kek, (byte) 0);
                    }
                }
            }
        });

        btnRegistracija.setContentAreaFilled(false);
        btnRegistracija.setBorderPainted(false);
        btnRegistracija.setOpaque(false);
        btnRegistracija.setFocusPainted(false);
        btnRegistracija.setForeground(Color.blue);
        btnRegistracija.addActionListener(e -> new Registracija());
    }
}