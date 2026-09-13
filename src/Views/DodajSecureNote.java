package Views;

import Objects.EncryptedVaultKey;
import Objects.SecureNote;
import Services.*;
import com.google.gson.Gson;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class DodajSecureNote extends JFrame {

    private JPanel panDodaj;
    private JTextField txtNaziv;
    private JTextArea txtSadrzaj;
    private JScrollPane scrollSadrzaj;
    private JComboBox<String> cmbKategorija;
    private JButton btnNovaKategorija;
    private JButton btnDodaj;
    private JLabel lblNatrag;

    public DodajSecureNote() {
        setTitle("Passwordium - Dodaj sigurnu bilješku");
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

        txtSadrzaj.setLineWrap(true);
        txtSadrzaj.setWrapStyleWord(true);

        stilizirajGumb(btnDodaj);
        stilizirajGumb(btnNovaKategorija);

        btnDodaj.addActionListener(e -> spremiBiljesku());
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

    private void spremiBiljesku() {
        if (!provjeriUnos()) {
            return;
        }

        try {
            SecureNote note = new SecureNote(
                    txtNaziv.getText().trim(),
                    txtSadrzaj.getText(),
                    (String) cmbKategorija.getSelectedItem()
            );

            Gson gson = new Gson();
            String json = gson.toJson(note);

            VaultCryptoService cryptoService = new VaultCryptoService();
            EncryptedVaultKey encryptedData = cryptoService.encryptData(
                    json,
                    VaultSession.getVaultKey()
            );

            new AccountService().addAccount(encryptedData);

            JOptionPane.showMessageDialog(
                    this,
                    "Sigurna bilješka je uspješno spremljena!"
            );

            vratiNaPrikaz();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Došlo je do greške tijekom spremanja sigurne bilješke!",
                    "Greška",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private boolean provjeriUnos() {
        boolean ispravno = true;

        if (txtNaziv.getText() == null || txtNaziv.getText().isBlank()) {
            txtNaziv.setBackground(Color.RED);
            ispravno = false;
        } else {
            txtNaziv.setBackground(Color.WHITE);
        }

        if (txtSadrzaj.getText() == null || txtSadrzaj.getText().isBlank()) {
            txtSadrzaj.setBackground(Color.RED);
            ispravno = false;
        } else {
            txtSadrzaj.setBackground(Color.WHITE);
        }

        return ispravno;
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
