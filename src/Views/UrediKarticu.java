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

public class UrediKarticu extends JFrame {

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

    private Card card;

    public UrediKarticu() {
        setTitle("Passwordium - Uredi karticu");
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

        btnDodaj.addActionListener(e -> spremiPromjene());
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

    public void podaci(Card card) {
        this.card = card;
    }

    public void prikazPodataka() {
        if (card == null) {
            return;
        }

        txtNaziv.setText(card.getName());
        txtVlasnik.setText(card.getCardholderName());
        txtBrojKartice.setText(card.getCardNumber());
        txtDatumIsteka.setText(card.getExpiryDate());
        txtCvv.setText(card.getCvv());

        if (card.getCategory() != null && !card.getCategory().isBlank()) {
            osigurajKategorijuPostoji(card.getCategory());
            cmbKategorija.setSelectedItem(card.getCategory());
        }
    }

    private void spremiPromjene() {
        if (card == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Kartica nije učitana!",
                    "Greška",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (!provjeriUnos()) {
            return;
        }

        char[] cvvChars = txtCvv.getPassword();

        try {
            card.setName(txtNaziv.getText().trim());
            card.setCardholderName(txtVlasnik.getText().trim());
            card.setCardNumber(txtBrojKartice.getText().trim());
            card.setExpiryDate(txtDatumIsteka.getText().trim());
            card.setCvv(new String(cvvChars));
            card.setCategory((String) cmbKategorija.getSelectedItem());

            Gson gson = new Gson();
            String json = gson.toJson(card);

            VaultCryptoService cryptoService = new VaultCryptoService();
            EncryptedVaultKey encryptedData = cryptoService.encryptData(
                    json,
                    VaultSession.getVaultKey()
            );

            new AccountService().updateAccount(
                    card.getId(),
                    encryptedData
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Kartica je uspješno ažurirana!"
            );

            vratiNaPrikaz();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Došlo je do greške tijekom ažuriranja kartice!",
                    "Greška",
                    JOptionPane.ERROR_MESSAGE
            );
        } finally {
            Arrays.fill(cvvChars, '\0');
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

        osigurajKategorijuPostoji(novaKategorija);
        cmbKategorija.setSelectedItem(novaKategorija);
    }

    private void osigurajKategorijuPostoji(String kategorija) {
        ComboBoxModel<String> model = cmbKategorija.getModel();

        for (int i = 0; i < model.getSize(); i++) {
            if (kategorija.equalsIgnoreCase(model.getElementAt(i))) {
                return;
            }
        }

        cmbKategorija.addItem(kategorija);
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
