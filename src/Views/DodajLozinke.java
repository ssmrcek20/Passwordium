package Views;

import Objects.Account;
import Objects.EncryptedVaultKey;
import Services.AccountService;
import Services.VaultCryptoService;
import Services.VaultSession;
import com.google.gson.Gson;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

public class DodajLozinke extends JFrame {
    private JPanel panDodaj;
    private JTextField txtKorIme;
    private JPasswordField txtLozinka;
    private JTextField txtNaziv;
    private JTextField txtLink;
    private JButton btnGenerirajLozinku;
    private JButton btnDodaj;
    private JLabel lblNatrag;

    public DodajLozinke() {

        setTitle("Passwordium");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {

                VaultSession.lock();

                dispose();
                System.exit(0);
            }
        });

        setContentPane(panDodaj);

        setSize(1080, 720);
        setLocationRelativeTo(null);

        btnGenerirajLozinku.setBorderPainted(false);
        btnGenerirajLozinku.setBackground(new Color(200, 200, 200));
        btnGenerirajLozinku.setFocusPainted(false);

        btnDodaj.setBorderPainted(false);
        btnDodaj.setBackground(new Color(200, 200, 200));
        btnDodaj.setFocusPainted(false);

        btnDodaj.addActionListener(e -> {

            if (!provjeriUnos()) {
                return;
            }

            char[] passwordChars =
                    txtLozinka.getPassword();

            try {

                Account account = new Account(txtNaziv.getText(), txtKorIme.getText(), new String(passwordChars), txtLink.getText());

                Gson gson = new Gson();
                String json = gson.toJson(account);
                VaultCryptoService cryptoService = new VaultCryptoService();
                EncryptedVaultKey encryptedData = cryptoService.encryptData(json, VaultSession.getVaultKey());
                AccountService accountService = new AccountService();
                accountService.addAccount(encryptedData);

                JOptionPane.showMessageDialog(DodajLozinke.this, "Uspješno dodavanje računa!");

                PrikazSifri prikazSifri = new PrikazSifri();
                prikazSifri.prikazPodataka();
                DodajLozinke.this.dispose();

            } catch (Exception ex) {

                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                        DodajLozinke.this,
                        "Došlo je do greške tijekom spremanja novog računa!"
                );

            } finally {
                Arrays.fill(passwordChars, '\0');
            }
        });

        lblNatrag.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {

                        try {
                            PrikazSifri prikazSifri = new PrikazSifri();
                            prikazSifri.prikazPodataka();
                            DodajLozinke.this.dispose();
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(DodajLozinke.this, "Greška prilikom učitavanja računa!");
                        }
                    }
                }
        );

        btnGenerirajLozinku.addActionListener(e -> {
            String lozinka = "Abac";
            txtLozinka.setText(lozinka);
        });

        setVisible(true);
    }

    private boolean provjeriUnos() {

        boolean ispravno = true;

        if (txtNaziv.getText().isBlank()) {
            txtNaziv.setBackground(Color.red);
            ispravno = false;
        } else {
            txtNaziv.setBackground(Color.white);
        }

        if (txtKorIme.getText().isBlank()) {
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

        return ispravno;
    }
}