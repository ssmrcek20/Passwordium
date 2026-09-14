package Views;

import Objects.EncryptedVaultKey;
import Services.PasswordStrengthService;
import Services.UserService;
import Services.VaultCryptoService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

public class Registracija extends JFrame {
    private JPanel panRegistracija;
    private JTextField txtKorIme;
    private JButton btnRegistracija;
    private JPasswordField txtLozinka;
    private JLabel lblPrijava;
    private JLabel lblPotvrdiLozinku;
    private JPasswordField txtPotvrdaLozinke;
    private JProgressBar progressJacina;
    private JLabel lblJacinaLozinke;

    private static final int MIN_PASSWORD_SCORE = 4;

    public Registracija() {
        setTitle("Passwordium");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(panRegistracija);
        setSize(1080, 720);
        setLocationRelativeTo(null);

        btnRegistracija.setBorderPainted(false);
        btnRegistracija.setBackground(new Color(200, 200, 200));
        btnRegistracija.setFocusPainted(false);
        btnRegistracija.setEnabled(false);

        progressJacina.setMinimum(0);
        progressJacina.setMaximum(5);
        progressJacina.setValue(0);
        progressJacina.setStringPainted(true);
        progressJacina.setString("Unesite lozinku");

        lblJacinaLozinke.setText("Jačina lozinke: nije unesena");

        lblPrijava.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        lblPrijava.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        new Prijava();
                        Registracija.this.dispose();
                    }
                }
        );

        DocumentListener passwordListener =
                new DocumentListener() {
                    private void promjena() {
                        osvjeziJacinuLozinke();
                    }
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        promjena();
                    }
                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        promjena();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        promjena();
                    }
                };

        txtLozinka.getDocument().addDocumentListener(passwordListener);
        txtKorIme.getDocument().addDocumentListener(passwordListener);

        btnRegistracija.addActionListener(
                e -> registrirajKorisnika()
        );

        setVisible(true);
    }

    private void registrirajKorisnika() {
        if (!provjeriJesuLiPodaciUneseni()) {
            return;
        }

        char[] passwordChars = txtLozinka.getPassword();
        char[] confirmationChars = txtPotvrdaLozinke.getPassword();

        byte[] kek = null;
        byte[] vaultKey = null;

        try {
            if (!Arrays.equals(passwordChars, confirmationChars)) {
                JOptionPane.showMessageDialog(this, "Upisana lozinka se razlikuje " +
                        "od potvrde lozinke!", "Lozinke se ne podudaraju", JOptionPane.WARNING_MESSAGE);
                return;
            }

            PasswordStrengthService.Result result = PasswordStrengthService.evaluate(passwordChars, txtKorIme.getText().trim());

            if (result.getScore() < MIN_PASSWORD_SCORE) {

                JOptionPane.showMessageDialog(this, "Upisana lozinka nije dovoljno jaka.\n\n" +
                        result.getMessage(), "Preslaba lozinka", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String username = txtKorIme.getText().trim();
            VaultCryptoService crypto = new VaultCryptoService();
            UserService userService = new UserService();
            byte[] vaultSalt = crypto.generateSalt();

            kek = crypto.deriveKek(passwordChars, vaultSalt);
            vaultKey = crypto.generateVaultKey();
            EncryptedVaultKey encryptedVaultKey = crypto.encryptVaultKey(vaultKey, kek);
            String vaultSaltBase64 = java.util.Base64.getEncoder().encodeToString(vaultSalt);

            userService.register(username, new String(passwordChars), vaultSaltBase64,
                    encryptedVaultKey.getEncryptedVaultKey(), encryptedVaultKey.getNonce(),
                    encryptedVaultKey.getTag()
            );

            JOptionPane.showMessageDialog(this, "Uspješna registracija!");

            dispose();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Registracija nije uspjela: "
                    + ex.getMessage(), "Greška", JOptionPane.ERROR_MESSAGE);
        } finally {
            Arrays.fill(passwordChars, '\0');
            Arrays.fill(confirmationChars, '\0');

            if (kek != null) {
                Arrays.fill(kek, (byte) 0);
            }

            if (vaultKey != null) {
                Arrays.fill(vaultKey, (byte) 0);
            }
        }
    }

    private void osvjeziJacinuLozinke() {

        char[] password = txtLozinka.getPassword();

        try {
            if (password.length == 0) {
                progressJacina.setValue(0);
                progressJacina.setString("Unesite lozinku");
                lblJacinaLozinke.setText("Jačina lozinke: nije unesena");
                btnRegistracija.setEnabled(false);

                return;
            }

            PasswordStrengthService.Result result = PasswordStrengthService.evaluate(password, txtKorIme.getText().trim());

            progressJacina.setValue(result.getScore());
            progressJacina.setString(result.getMessage());
            lblJacinaLozinke.setText("Jačina lozinke: " + nazivJacine(result.getStrength()));
            btnRegistracija.setEnabled(result.getScore() >= MIN_PASSWORD_SCORE);

        } finally {
            Arrays.fill(password, '\0');
        }
    }

    private String nazivJacine(PasswordStrengthService.Strength strength) {
        return switch (strength) {
            case VRLO_SLABA -> "Vrlo slaba";
            case SLABA -> "Slaba";
            case SREDNJA -> "Srednja";
            case JAKA -> "Jaka";
            case VRLO_JAKA -> "Vrlo jaka";
        };
    }

    private boolean provjeriJesuLiPodaciUneseni() {
        boolean ispravno = true;

        if (txtKorIme.getText().isBlank()) {
            txtKorIme.setBackground(Color.RED);
            ispravno = false;
        } else {
            txtKorIme.setBackground(Color.WHITE);
        }

        if (txtLozinka.getPassword().length == 0) {
            txtLozinka.setBackground(Color.RED);
            ispravno = false;
        } else {
            txtLozinka.setBackground(Color.WHITE);
        }

        if (txtPotvrdaLozinke.getPassword().length == 0) {
            txtPotvrdaLozinke.setBackground(Color.RED);
            ispravno = false;
        } else {
            txtPotvrdaLozinke.setBackground(Color.WHITE);
        }

        return ispravno;
    }
}