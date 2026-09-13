package Views;

import Objects.Account;
import Objects.EncryptedVaultKey;
import Services.*;
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
    private JComboBox<String> cmbKategorija;
    private JButton btnNovaKategorija;

    public DodajLozinke() {

        setTitle("Passwordium");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                AutoLockService.stop();
                VaultSession.lock();

                dispose();
                System.exit(0);
            }
        });

        setContentPane(panDodaj);

        setSize(1080, 720);
        setLocationRelativeTo(null);

        cmbKategorija.setModel(new DefaultComboBoxModel<>(CategoryService.List));

        btnGenerirajLozinku.setBorderPainted(false);
        btnGenerirajLozinku.setBackground(new Color(200, 200, 200));
        btnGenerirajLozinku.setFocusPainted(false);

        btnGenerirajLozinku.addActionListener(e -> {
            JSpinner spinnerLength = new JSpinner(new SpinnerNumberModel(16, 4, 128, 1));
            JCheckBox chkLowercase = new JCheckBox("Mala slova", true);
            JCheckBox chkUppercase = new JCheckBox("Velika slova", true);
            JCheckBox chkNumbers = new JCheckBox("Brojevi", true);
            JCheckBox chkSpecial = new JCheckBox("Posebni znakovi", true);
            JPanel panel = new JPanel();

            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.add(new JLabel("Duljina lozinke:"));
            panel.add(spinnerLength);
            panel.add(chkLowercase);
            panel.add(chkUppercase);
            panel.add(chkNumbers);
            panel.add(chkSpecial);

            int result = JOptionPane.showConfirmDialog(DodajLozinke.this, panel,
                    "Generator lozinke", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                try {
                    PasswordGeneratorService generator = new PasswordGeneratorService();

                    String password = generator.generatePassword((Integer) spinnerLength.getValue(),
                            chkLowercase.isSelected(), chkUppercase.isSelected(), chkNumbers.isSelected(),
                            chkSpecial.isSelected());

                    txtLozinka.setText(password);

                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(DodajLozinke.this, ex.getMessage());
                }
            }
        });

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

                Account account = new Account(txtNaziv.getText(), txtKorIme.getText(), new String(passwordChars), txtLink.getText(), (String) cmbKategorija.getSelectedItem());

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

        btnNovaKategorija.addActionListener(e -> {
            String novaKategorija = JOptionPane.showInputDialog(this, "Unesite naziv nove kategorije:");

            if (novaKategorija == null) {
                return;
            }

            novaKategorija = novaKategorija.trim();

            if (novaKategorija.isEmpty()) {JOptionPane.showMessageDialog(this, "Naziv kategorije ne smije biti prazan.");
                return;
            }

            ComboBoxModel<String> model = cmbKategorija.getModel();

            boolean postoji = false;

            for (int i = 0; i < model.getSize(); i++) {
                if (novaKategorija.equalsIgnoreCase(
                        model.getElementAt(i))) {

                    postoji = true;
                    break;
                }
            }

            if (!postoji) {
                cmbKategorija.addItem(novaKategorija);
            }

            cmbKategorija.setSelectedItem(novaKategorija);
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