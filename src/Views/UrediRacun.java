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

public class UrediRacun extends JFrame {
    private JTextField txtKorIme;
    private JPasswordField txtLozinka;
    private JTextField txtNaziv;
    private JTextField txtLink;
    private JButton btnGenerirajLozinku;
    private JButton btnUredi;
    private JPanel panUredi;
    private JLabel lblNatrag;
    private Account account;

    public UrediRacun(){
        setTitle("Passwordium");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {

                VaultSession.lock();

                dispose();
                System.exit(0);
            }
        });
        setContentPane(panUredi);
        setSize(1080, 720);
        setLocationRelativeTo(null);
        setVisible(true);

        btnGenerirajLozinku.setBorderPainted(false);
        btnGenerirajLozinku.setBackground(new Color(200,200,200));
        btnGenerirajLozinku.setFocusPainted(false);

        btnUredi.setBorderPainted(false);
        btnUredi.setBackground(new Color(200,200,200));
        btnUredi.setFocusPainted(false);
        btnUredi.addActionListener(e -> {

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

            if (!ispravno) {
                return;
            }

            char[] passwordChars = txtLozinka.getPassword();

            try {

                account.Naziv = txtNaziv.getText();
                account.KorIme = txtKorIme.getText();
                account.Lozinka = new String(passwordChars);
                account.Link = txtLink.getText();

                Gson gson = new Gson();

                String json = gson.toJson(account);

                VaultCryptoService cryptoService =
                        new VaultCryptoService();

                EncryptedVaultKey encryptedData = cryptoService.encryptData(json, VaultSession.getVaultKey());

                AccountService accountService = new AccountService();
                accountService.updateAccount(account.Id, encryptedData);

                JOptionPane.showMessageDialog(UrediRacun.this, "Uspješno uređivanje računa!");

                PrikazSifri prikazSifri = new PrikazSifri();
                prikazSifri.prikazPodataka();

                UrediRacun.this.dispose();

            } catch (Exception ex) {

                ex.printStackTrace();
                JOptionPane.showMessageDialog(UrediRacun.this, "Došlo je do greške tijekom uređivanja računa!");

            } finally {
                java.util.Arrays.fill(passwordChars, '\0');
            }
        });

        lblNatrag.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                PrikazSifri prikazSifri= new PrikazSifri();
                try {
                    prikazSifri.prikazPodataka();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                UrediRacun.this.dispose();
            }
        });
    }

    public void prikazPodataka() {
        txtNaziv.setText(account.Naziv);
        txtKorIme.setText(account.KorIme);
        txtLink.setText(account.Link);
        txtLozinka.setText(account.Lozinka);
    }

    public void podaci(Account account) {
        this.account = account;
    }
}