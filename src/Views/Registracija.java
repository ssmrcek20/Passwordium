package Views;

import Objects.EncryptedVaultKey;
import Services.UserService;
import Services.VaultCryptoService;

import javax.swing.*;
import java.awt.*;

public class Registracija extends JFrame {
    private JPanel panRegistracija;
    private JTextField txtKorIme;
    private JButton btnRegistracija;
    private JPasswordField txtLozinka;
    private JLabel lblPrijava;
    private JLabel lblPotvrdiLozinku;
    private JPasswordField txtPotvrdaLozinke;
    private JProgressBar PgbJacinaLozinke;
    private JLabel lblJacinaLozinke;

    public Registracija() {
        setTitle("Passwordium");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1080, 720);
        setLocationRelativeTo(null);
        setVisible(true);
        setContentPane(panRegistracija);

        btnRegistracija.setBorderPainted(false);
        btnRegistracija.setBackground(new Color(200, 200, 200));
        btnRegistracija.setFocusPainted(false);

        btnRegistracija.addActionListener(
                e -> {

                    if (!provjeriJesuLiPodaciUneseni()) {
                        return;
                    }

                    txtLozinka.setBackground(Color.white);

                    if (!potvrdaLozinke()) {
                        JOptionPane.showMessageDialog(Registracija.this,
                                "Upisana lozinka se razlikuje " + "od potvrde lozinke!");
                        return;
                    }

                    if (provjeriJacinuLozinke() <= 40d) {
                        JOptionPane.showMessageDialog(Registracija.this,
                                "Upisana lozinka nije dovoljno snažna!");
                        return;
                    }

                    char[] passwordChars = txtLozinka.getPassword();

                    byte[] kek = null;
                    byte[] vaultKey = null;

                    try {

                        String username = txtKorIme.getText();

                        VaultCryptoService crypto = new VaultCryptoService();

                        UserService userService = new UserService();

                        byte[] vaultSalt = crypto.generateSalt();

                        kek = crypto.deriveKek(passwordChars, vaultSalt);

                        vaultKey = crypto.generateVaultKey();

                        EncryptedVaultKey encryptedVaultKey = crypto.encryptVaultKey(vaultKey, kek);

                        String vaultSaltBase64 = java.util.Base64.getEncoder().encodeToString(vaultSalt);

                        userService.register(username, new String(passwordChars), vaultSaltBase64, encryptedVaultKey.getEncryptedVaultKey(),
                                encryptedVaultKey.getNonce(),
                                encryptedVaultKey.getTag());

                        JOptionPane.showMessageDialog(Registracija.this, "Uspješna registracija!");

                        Registracija.this.dispose();

                    } catch (Exception ex) {
                        ex.printStackTrace();

                        JOptionPane.showMessageDialog(Registracija.this, "Registracija nije uspjela: "
                                        + ex.getMessage());

                    } finally {

                        java.util.Arrays.fill(passwordChars, '\0');

                        if (kek != null) {
                            java.util.Arrays.fill(kek, (byte) 0);
                        }

                        if (vaultKey != null) {
                            java.util.Arrays.fill(vaultKey, (byte) 0);
                        }
                    }
                }
        );
    }

    private double provjeriJacinuLozinke() {
        String lozinka = new String(txtLozinka.getPassword());
        return 50;
    }

    private boolean potvrdaLozinke(){
        boolean potvrdena = false;
        String lozinka = new String(txtLozinka.getPassword());
        String confLozinka = new String(txtPotvrdaLozinke.getPassword());

        if(lozinka.equals(confLozinka)) potvrdena = true;

        return potvrdena;
    }

    private boolean provjeriJesuLiPodaciUneseni(){
        boolean ispravno = true;
        if (txtKorIme.getText().equals("")) {
            txtKorIme.setBackground(Color.red);
            ispravno = false;
        } else {
            txtKorIme.setBackground(Color.white);
        }

        if (txtLozinka.getPassword().length == 0) {
            txtLozinka.setBackground(Color.red);
            ispravno = false;
        } else {
            txtLozinka.setBackground(Color.white);
        }

        if (txtPotvrdaLozinke.getPassword().length == 0) {
            txtPotvrdaLozinke.setBackground(Color.red);
            ispravno = false;
        } else {
            txtPotvrdaLozinke.setBackground(Color.white);
        }
        return ispravno;
    }
}
