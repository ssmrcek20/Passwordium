package Views;

import Objects.Card;
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

public class DodajKarticu extends JFrame {

    private JPanel panDodaj;
    private JTextField txtNaziv;
    private JTextField txtVlasnik;
    private JTextField txtBrojKartice;
    private JTextField txtDatumIsteka;
    private JPasswordField txtCvv;
    private JComboBox<String> cmbKategorija;
    private JButton btnNovaKategorija;
    private JButton btnDodaj;
    private JLabel lblNatrag;

    public DodajKarticu() {
        setTitle("Passwordium - Dodaj karticu");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
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

        stilizirajGumb(btnDodaj);
        stilizirajGumb(btnNovaKategorija);

        btnDodaj.addActionListener(e -> spremiKarticu());
        btnNovaKategorija.addActionListener(e -> dodajNovuKategoriju());

        lblNatrag.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblNatrag.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                vratiNaPrikaz();
            }
        });

        setVisible(true);
    }

    private void spremiKarticu() {
        if (!provjeriUnos()) {
            return;
        }

        char[] cvvChars = txtCvv.getPassword();

        try {
            Card card = new Card(
                    txtNaziv.getText().trim(),
                    txtVlasnik.getText().trim(),
                    txtBrojKartice.getText().trim(),
                    txtDatumIsteka.getText().trim(),
                    new String(cvvChars),
                    (String) cmbKategorija.getSelectedItem()
            );

            Gson gson = new Gson();
            String json = gson.toJson(card);

            VaultCryptoService cryptoService = new VaultCryptoService();
            EncryptedVaultKey encryptedData = cryptoService.encryptData(
                    json,
                    VaultSession.getVaultKey()
            );

            new AccountService().addAccount(encryptedData);

            JOptionPane.showMessageDialog(
                    this,
                    "Kartica je uspješno spremljena!"
            );

            vratiNaPrikaz();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Došlo je do greške tijekom spremanja kartice!",
                    "Greška",
                    JOptionPane.ERROR_MESSAGE
            );
        } finally {
            Arrays.fill(cvvChars, '\0');
            txtCvv.setText("");
        }
    }

    private boolean provjeriUnos() {
        boolean ispravno = true;

        ispravno &= provjeriPolje(txtNaziv);
        ispravno &= provjeriPolje(txtVlasnik);
        ispravno &= provjeriPolje(txtBrojKartice);
        ispravno &= provjeriPolje(txtDatumIsteka);

        if (txtCvv.getPassword().length == 0) {
            txtCvv.setBackground(Color.RED);
            ispravno = false;
        } else {
            txtCvv.setBackground(Color.WHITE);
        }

        return ispravno;
    }

    private boolean provjeriPolje(JTextField polje) {
        if (polje.getText() == null || polje.getText().isBlank()) {
            polje.setBackground(Color.RED);
            return false;
        }

        polje.setBackground(Color.WHITE);
        return true;
    }

    private void dodajNovuKategoriju() {
        String novaKategorija = JOptionPane.showInputDialog(
                this,
                "Unesite naziv nove kategorije:"
        );

        if (novaKategorija == null) {
            return;
        }

        novaKategorija = novaKategorija.trim();

        if (novaKategorija.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Naziv kategorije ne smije biti prazan."
            );
            return;
        }

        ComboBoxModel<String> model = cmbKategorija.getModel();
        boolean postoji = false;

        for (int i = 0; i < model.getSize(); i++) {
            if (novaKategorija.equalsIgnoreCase(model.getElementAt(i))) {
                postoji = true;
                break;
            }
        }

        if (!postoji) {
            cmbKategorija.addItem(novaKategorija);
        }

        cmbKategorija.setSelectedItem(novaKategorija);
    }

    private void vratiNaPrikaz() {
        new PrikazSifri();
        dispose();
    }

    private void stilizirajGumb(JButton button) {
        button.setBorderPainted(false);
        button.setBackground(new Color(200, 200, 200));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
