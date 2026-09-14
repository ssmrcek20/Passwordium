package Views;

import Objects.Account;
import Objects.VaultItem;
import Objects.Card;
import Objects.SecureNote;
import Responses.AccountResponse;
import Services.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.Timer;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.*;

public class PrikazSifri extends JFrame {
    private JPanel panSifre;
    private JTable tabLozinke;
    private JButton btnDodajLozinku;
    private JButton btnUrediLozinku;
    private JButton btnUkloniLozinku;
    private JScrollPane scrollPan;
    private JButton btnLogout;
    private JButton btnPromjeniKategoriju;
    private JButton btnUvoz;
    private JButton btnIzvoz;
    private JTextField txtPretraga;
    private JList<String> listKategorije;
    private JLabel lblNaslov;
    private JLabel lblBrojZapisa;
    private JLabel lblKategorije;
    private JLabel lblUputa;
    private JButton btnTotp;

    private final List<VaultItem> items = new ArrayList<>();
    private final List<VaultItem> shownItems = new ArrayList<>();

    public PrikazSifri() {
        setTitle("Passwordium");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setContentPane(panSifre);
        setSize(1180, 760);
        setMinimumSize(new Dimension(960, 620));
        setLocationRelativeTo(null);

        postaviIzgled();
        postaviListenere();
        postaviSigurnoGasnje();

        try {
            prikazPodataka();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Greška pri učitavanju vjerodajnica.",
                    "Greška", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        setVisible(true);
    }

    private void postaviIzgled() {
        lblNaslov.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblKategorije.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblBrojZapisa.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblUputa.setFont(new Font("SansSerif", Font.PLAIN, 12));

        txtPretraga.setFont(new Font("SansSerif", Font.PLAIN, 15));
        txtPretraga.setToolTipText("Pretraži po nazivu, korisničkom imenu ili poveznici");

        tabLozinke.setRowHeight(38);
        tabLozinke.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabLozinke.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        tabLozinke.getTableHeader().setReorderingAllowed(false);
        tabLozinke.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabLozinke.setShowVerticalLines(false);
        tabLozinke.setFillsViewportHeight(true);

        listKategorije.setFont(new Font("SansSerif", Font.PLAIN, 14));
        listKategorije.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        stilizirajGumb(btnDodajLozinku);
        stilizirajGumb(btnUrediLozinku);
        stilizirajGumb(btnUkloniLozinku);
        stilizirajGumb(btnLogout);
        stilizirajGumb(btnPromjeniKategoriju);
        stilizirajGumb(btnUvoz);
        stilizirajGumb(btnIzvoz);
        stilizirajGumb(btnTotp);
    }

    private void stilizirajGumb(JButton button) {
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBackground(new Color(235, 235, 235));
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void postaviSigurnoGasnje() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                AutoLockService.stop();
                VaultSession.lock();
                dispose();
                System.exit(0);
            }
        });
    }

    private void postaviListenere() {

        btnTotp.addActionListener(e -> {
            int row = tabLozinke.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Odaberite vjerodajnicu.");
                return;
            }

            VaultItem item = shownItems.get(row);

            if (!(item instanceof Account account)) {
                JOptionPane.showMessageDialog(this, "TOTP je dostupan samo za vjerodajnice.");
                return;
            }

            if (account.getTotpSecret() == null || account.getTotpSecret().isBlank()) {
                JOptionPane.showMessageDialog(this, "Za ovu vjerodajnicu nije spremljen TOTP ključ.");
                return;
            }

            prikaziTotp(account);
        });

        btnIzvoz.addActionListener(e -> {
            if (items.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Trezor nema zapisa za izvoz.");

                return;
            }

            int potvrda = JOptionPane.showConfirmDialog(this, """
                          Izvozna JSON datoteka sadržavat će osjetljive
                          podatke u čitljivom obliku:
      
                          • korisnička imena
                          • lozinke
                          • podatke kartica
                          • CVV
                          • sigurne bilješke
      
                          Datoteku čuvajte na sigurnom mjestu.
      
                          Želite li nastaviti?
                          """, "Sigurnosno upozorenje", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (potvrda != JOptionPane.YES_OPTION) {
                return;
            }

            try {
                VaultImportExportService service = new VaultImportExportService();

                int broj = service.exportVault(this, items);

                if (broj == 0) {
                    return;
                }

                JOptionPane.showMessageDialog(this, "Izvoz je uspješno završen.\n" +
                        "Izvezeno zapisa: " + broj, "Izvoz", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                ex.printStackTrace();

                JOptionPane.showMessageDialog(this, "Došlo je do greške tijekom izvoza:\n" +
                        ex.getMessage(), "Greška", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnUvoz.addActionListener(e -> {
            try {
                VaultImportExportService service = new VaultImportExportService();
                List<VaultItem> importedItems = service.readVault(this);

                if (importedItems.isEmpty()) {
                    return;
                }

                int credentials = 0;
                int cards = 0;
                int notes = 0;

                for (VaultItem item : importedItems) {
                    if (item instanceof Account) {
                        credentials++;
                    }
                    else if (item instanceof Card) {
                        cards++;
                    }
                    else if (item instanceof SecureNote) {
                        notes++;
                    }
                }

                String poruka = "Pronađeno je " + importedItems.size() + " zapisa:\n\n" + "Vjerodajnice: " +
                        credentials + "\nKartice: " + cards + "\nSigurne bilješke: " + notes + "\n\nŽelite li ih uvesti?";

                int potvrda = JOptionPane.showConfirmDialog(this, poruka, "Uvoz podataka",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (potvrda != JOptionPane.YES_OPTION) {
                    return;
                }

                int spremljeno = service.saveImportedItems(importedItems);

                prikazPodataka();

                JOptionPane.showMessageDialog(this, "Uvoz je uspješno završen.\n" +
                        "Uvezeno zapisa: " + spremljeno, "Uvoz", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {

                ex.printStackTrace();

                JOptionPane.showMessageDialog(this, "Došlo je do greške tijekom uvoza:\n"
                        + ex.getMessage(), "Greška", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnLogout.addActionListener(e -> {
            try {
                new UserService().logout();
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                AutoLockService.stop();
                VaultSession.lock();
                new Prijava();
                dispose();
            }
        });

        btnDodajLozinku.addActionListener(e -> {
            String[] opcije = {"Vjerodajnica", "Sigurna bilješka", "Kartica"};

            String izbor = (String) JOptionPane.showInputDialog(this, "Odaberite vrstu zapisa:",
                    "Dodaj zapis", JOptionPane.PLAIN_MESSAGE, null, opcije, opcije[0]);

            if (izbor == null) {
                return;
            }

            switch (izbor) {
                case "Vjerodajnica" -> new DodajLozinke();
                case "Sigurna bilješka" -> new DodajSecureNote();
                case "Kartica" -> new DodajKarticu();
            }

            dispose();
        });

        btnUrediLozinku.addActionListener(e -> {
            int odabraniRed = tabLozinke.getSelectedRow();

            if (odabraniRed == -1) {
                JOptionPane.showMessageDialog(
                        this,
                        "Odaberi zapis za uređivanje!"
                );
                return;
            }

            VaultItem item = shownItems.get(odabraniRed);

            if (item instanceof Account account) {

                UrediRacun prozor = new UrediRacun();
                prozor.podaci(account);
                prozor.prikazPodataka();

            } else if (item instanceof Card card) {
                UrediKarticu prozor = new UrediKarticu();
                prozor.podaci(card);
                prozor.prikazPodataka();

            } else if (item instanceof SecureNote note) {
                UrediSecureNote prozor = new UrediSecureNote();
                prozor.podaci(note);
                prozor.prikazPodataka();
            }

            dispose();
        });

        btnUkloniLozinku.addActionListener(e -> obrisiOdabraniAccount());
        btnPromjeniKategoriju.addActionListener(e -> promijeniKategoriju());

        tabLozinke.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                int red = tabLozinke.rowAtPoint(e.getPoint());
                if (red < 0 || red >= shownItems.size()) {
                    return;
                }

                VaultItem item = shownItems.get(red);

                if (item instanceof Account account && e.getClickCount() == 2) {

                    kopirajOsjetljiviTekst(account.getPassword(), "Lozinka je kopirana.");
                    return;
                }

                if (e.getClickCount() == 2) {
                    if (item instanceof Card card) {
                        prikaziKarticu(card);
                    }

                    else if (item instanceof SecureNote note) {
                        prikaziBiljesku(note);
                    }
                }
            }
        });

        listKategorije.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                primijeniFiltere();
            }
        });

        txtPretraga.getDocument().addDocumentListener(new DocumentListener() {
            private void promjena() {
                primijeniFiltere();
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
        });
    }

    private void obrisiOdabraniAccount() {

        int odabraniRed = tabLozinke.getSelectedRow();

        if (odabraniRed == -1) {JOptionPane.showMessageDialog(this, "Odaberi zapis za brisanje!");
            return;
        }

        VaultItem item = shownItems.get(odabraniRed);

        int potvrda = JOptionPane.showConfirmDialog(this, "Želiš li izbrisati zapis \""
                + item.getName() + "\"?", "Potvrda brisanja", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
        );

        if (potvrda != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            new AccountService().deleteAccount(item.getId());
            items.remove(item);

            osvjeziKategorije();
            primijeniFiltere();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Greška prilikom brisanja zapisa!",
                    "Greška", JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
        }
    }

    private void kopirajOsjetljiviTekst(String tekst, String poruka) {
        if (tekst == null || tekst.isBlank()) {
            return;
        }

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        clipboard.setContents(new StringSelection(tekst), null);

        JOptionPane.showMessageDialog(this, poruka + "\nMeđuspremnik će se očistiti nakon 15 sekundi.");

        Timer timer = createTimer(tekst, clipboard);
        timer.start();
    }

    private static Timer createTimer(String tekst, Clipboard clipboard) {
        Timer timer = new Timer(15000, event -> {
            try {
                if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {

                    Object trenutniSadrzaj = clipboard.getData(DataFlavor.stringFlavor);

                    if (tekst.equals(trenutniSadrzaj)) {
                        clipboard.setContents(new StringSelection(""), null);
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        timer.setRepeats(false);
        return timer;
    }

    public void prikazPodataka() throws Exception {
        AccountService accountService = new AccountService();
        VaultCryptoService cryptoService = new VaultCryptoService();
        Gson gson = new Gson();

        items.clear();
        AccountResponse[] responses = accountService.getAccounts();

        byte[] vaultKey = VaultSession.getVaultKey();

        for (AccountResponse response : responses) {
            String json = cryptoService.decryptData(response.getEncryptedData(), response.getNonce(), response.getTag(), vaultKey);

            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

            String type = "CREDENTIAL";

            if (jsonObject.has("type") && !jsonObject.get("type").isJsonNull()) {
                type = jsonObject.get("type").getAsString();
            }

            VaultItem item = switch (type) {
                case "CARD" -> gson.fromJson(json, Card.class);
                case "SECURE_NOTE" -> gson.fromJson(json, SecureNote.class);
                default -> gson.fromJson(json, Account.class);
            };

            item.setId(response.getId());

            if (item.getCategory() == null || item.getCategory().isBlank()) {
                item.setCategory("Ostalo");
            }

            items.add(item);
        }

        items.sort(
                Comparator.comparing(
                        item -> item.getName() == null
                                ? ""
                                : item.getName(),
                        String.CASE_INSENSITIVE_ORDER
                )
        );

        osvjeziKategorije();
        primijeniFiltere();
    }

    private void osvjeziKategorije() {
        String prethodnoOdabrana = listKategorije.getSelectedValue();

        DefaultListModel<String> model = new DefaultListModel<>();
        model.addElement("Sve");

        Set<String> kategorije = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

        for (VaultItem item : items) {
            if (item.getCategory() != null && !item.getCategory().isBlank()) {
                kategorije.add(item.getCategory());
            }
        }

        for (String kategorija : kategorije) {
            model.addElement(kategorija);
        }

        listKategorije.setModel(model);

        int indeks = 0;

        if (prethodnoOdabrana != null) {
            for (int i = 0; i < model.size(); i++) {
                if (prethodnoOdabrana.equals(model.get(i))) {
                    indeks = i;
                    break;
                }
            }
        }

        listKategorije.setSelectedIndex(indeks);
    }

    private void primijeniFiltere() {

        if (tabLozinke == null || listKategorije == null || txtPretraga == null) {
            return;
        }

        String kategorija = listKategorije.getSelectedValue();

        if (kategorija == null) {
            kategorija = "Sve";
        }

        String upit = txtPretraga.getText() == null ? "" : txtPretraga.getText().trim().toLowerCase();
        shownItems.clear();

        for (VaultItem item : items) {
            boolean odgovaraKategoriji = "Sve".equals(kategorija) || kategorija.equalsIgnoreCase(item.getCategory());

            boolean odgovaraPretrazi = upit.isBlank() || sadrzi(item.getName(), upit) || sadrzi(item.getCategory(), upit);

            if (item instanceof Account account) {
                odgovaraPretrazi = odgovaraPretrazi
                                || sadrzi(account.getUsername(), upit)
                                || sadrzi(account.getLink(), upit);
            }

            if (item instanceof Card card) {
                odgovaraPretrazi = odgovaraPretrazi
                                || sadrzi(card.getCardholderName(), upit)
                                || sadrzi(card.getCardNumber(), upit);
            }

            if (item instanceof SecureNote note) {
                odgovaraPretrazi = odgovaraPretrazi
                                || sadrzi(note.getContent(), upit);
            }

            if (odgovaraKategoriji && odgovaraPretrazi) {
                shownItems.add(item);
            }
        }

        osvjeziTablicu();
    }

    private boolean sadrzi(String vrijednost, String upit) {
        return vrijednost != null && vrijednost.toLowerCase().contains(upit);
    }

    private void osvjeziTablicu() {
        String[] stupci = {"Naziv", "Vrsta", "Korisničko ime / podatak", "Kategorija"};

        DefaultTableModel model =
                new DefaultTableModel(stupci, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };

        for (VaultItem item : shownItems) {
            String dodatniPodatak = "";

            if (item instanceof Account account) {
                dodatniPodatak = account.getUsername();
            }

            if (item instanceof Card card) {
                dodatniPodatak = maskirajKarticu(card.getCardNumber());
            }

            if (item instanceof SecureNote) {
                dodatniPodatak = "Sigurna bilješka";
            }

            model.addRow(new Object[]{item.getName(), nazivVrste(item), dodatniPodatak, item.getCategory()});
        }

        tabLozinke.setModel(model);

        lblBrojZapisa.setText(shownItems.size() + (shownItems.size() == 1 ? " zapis" : " zapisa"));
    }

    private void promijeniKategoriju() {
        int odabraniRed = tabLozinke.getSelectedRow();

        if (odabraniRed == -1) {JOptionPane.showMessageDialog(this,
                "Odaberi zapis kojem želiš promijeniti kategoriju!");
            return;
        }

        VaultItem item = shownItems.get(odabraniRed);
        Set<String> kategorije = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

        Collections.addAll(kategorije, CategoryService.List);

        for (VaultItem postojeciItem : items) {
            if (postojeciItem.getCategory() != null && !postojeciItem.getCategory().isBlank()) {
                kategorije.add(postojeciItem.getCategory());
            }
        }

        JComboBox<String> cmbKategorija = new JComboBox<>(kategorije.toArray(new String[0]));
        cmbKategorija.setEditable(true);

        if (item.getCategory() != null) {
            cmbKategorija.setSelectedItem(item.getCategory());
        }

        int rezultat = JOptionPane.showConfirmDialog(this, cmbKategorija, "Promijeni kategoriju - "
                + item.getName(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (rezultat != JOptionPane.OK_OPTION) {
            return;
        }

        Object vrijednost = cmbKategorija.getEditor().getItem();

        if (vrijednost == null) {
            return;
        }

        String novaKategorija = vrijednost.toString().trim();

        if (novaKategorija.isBlank()) {
            JOptionPane.showMessageDialog(this, "Naziv kategorije ne smije biti prazan!");
            return;
        }

        String staraKategorija = item.getCategory();

        try {
            item.setCategory(novaKategorija);
            Gson gson = new Gson();
            String json = gson.toJson(item);

            VaultCryptoService cryptoService = new VaultCryptoService();

            var encryptedData = cryptoService.encryptData(json, VaultSession.getVaultKey());
            new AccountService().updateAccount(item.getId(), encryptedData);
            osvjeziKategorije();

            listKategorije.setSelectedValue(novaKategorija, true);

            primijeniFiltere();

            JOptionPane.showMessageDialog(this, "Kategorija je uspješno promijenjena.");

        } catch (Exception ex) {

            item.setCategory(staraKategorija);

            JOptionPane.showMessageDialog(this, "Greška prilikom promjene kategorije!",
                    "Greška", JOptionPane.ERROR_MESSAGE);

            ex.printStackTrace();
        }
    }

    private String nazivVrste(VaultItem item) {

        if (item instanceof Account) {
            return "Vjerodajnica";
        }

        if (item instanceof Card) {
            return "Kartica";
        }

        if (item instanceof SecureNote) {
            return "Sigurna bilješka";
        }

        return "Nepoznato";
    }

    private String maskirajKarticu(String broj) {

        if (broj == null || broj.length() < 4) {
            return "••••";
        }

        String zadnjeCetiri =
                broj.substring(broj.length() - 4);

        return "•••• •••• •••• " + zadnjeCetiri;
    }

    private void prikaziKarticu(Card card) {
        JTextField txtNaziv = new JTextField(card.getName());
        JTextField txtVlasnik = new JTextField(card.getCardholderName());
        JTextField txtBroj = new JTextField(card.getCardNumber());
        JTextField txtIsteka = new JTextField(card.getExpiryDate());
        JPasswordField txtCvv = new JPasswordField(card.getCvv());
        JTextField txtKategorija = new JTextField(card.getCategory());

        txtNaziv.setEditable(false);
        txtVlasnik.setEditable(false);
        txtBroj.setEditable(false);
        txtIsteka.setEditable(false);
        txtCvv.setEditable(false);
        txtKategorija.setEditable(false);

        JButton btnKopirajBroj = new JButton("Kopiraj broj kartice");
        JButton btnKopirajCvv = new JButton("Kopiraj CVV");
        JCheckBox chkPrikaziCvv = new JCheckBox("Prikaži CVV");

        btnKopirajBroj.addActionListener(e ->
                kopirajOsjetljiviTekst(card.getCardNumber(), "Broj kartice je kopiran."));

        btnKopirajCvv.addActionListener(e ->
                kopirajOsjetljiviTekst(card.getCvv(), "CVV je kopiran."));

        chkPrikaziCvv.addActionListener(e -> {

            if (chkPrikaziCvv.isSelected()) {
                txtCvv.setEchoChar((char) 0);
            } else {
                txtCvv.setEchoChar('•');
            }
        });

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));

        panel.add(new JLabel("Naziv:"));
        panel.add(txtNaziv);

        panel.add(new JLabel("Vlasnik:"));
        panel.add(txtVlasnik);

        panel.add(new JLabel("Broj kartice:"));
        panel.add(txtBroj);

        panel.add(new JLabel(""));
        panel.add(btnKopirajBroj);

        panel.add(new JLabel("Datum isteka:"));
        panel.add(txtIsteka);

        panel.add(new JLabel("CVV:"));
        panel.add(txtCvv);

        panel.add(new JLabel(""));
        panel.add(chkPrikaziCvv);

        panel.add(new JLabel(""));
        panel.add(btnKopirajCvv);

        panel.add(new JLabel("Kategorija:"));
        panel.add(txtKategorija);

        JOptionPane.showMessageDialog(this, panel, "Kartica - " + card.getName(), JOptionPane.PLAIN_MESSAGE);
    }

    private void prikaziBiljesku(SecureNote note) {
        JTextArea txtSadrzaj = new JTextArea(note.getContent());

        txtSadrzaj.setEditable(false);
        txtSadrzaj.setLineWrap(true);
        txtSadrzaj.setWrapStyleWord(true);
        txtSadrzaj.setRows(12);
        txtSadrzaj.setColumns(40);

        JScrollPane scrollPane = new JScrollPane(txtSadrzaj);

        JButton btnKopiraj = new JButton("Kopiraj sadržaj");

        btnKopiraj.addActionListener(e ->
                kopirajOsjetljiviTekst(note.getContent(), "Sadržaj bilješke je kopiran."));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel lblNaziv = new JLabel(note.getName());
        lblNaziv.setFont(new Font("SansSerif", Font.BOLD, 18));
        panel.add(lblNaziv);
        panel.add(Box.createVerticalStrut(10));

        panel.add(new JLabel("Kategorija: " + note.getCategory()));
        panel.add(Box.createVerticalStrut(10));
        panel.add(scrollPane);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnKopiraj);

        JOptionPane.showMessageDialog(this, panel, "Sigurna bilješka", JOptionPane.PLAIN_MESSAGE);
    }

    private void prikaziTotp(Account account) {
        if (account.getTotpSecret() == null || account.getTotpSecret().isBlank()) {
            JOptionPane.showMessageDialog(this, "Za ovu vjerodajnicu nije spremljen TOTP ključ.");
            return;
        }

        JLabel lblKod = new JLabel();
        JLabel lblVrijeme = new JLabel();
        JButton btnKopiraj = new JButton("Kopiraj kod");
        lblKod.setFont(new Font("Monospaced", Font.BOLD, 30));
        lblKod.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        lblKod.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblVrijeme.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnKopiraj.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(lblKod);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblVrijeme);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnKopiraj);

        Timer timer = new Timer(500, e -> {
            try {
                String code =
                        TotpService.generateCode(account.getTotpSecret());
                                lblKod.setText(code.substring(0, 3) + " " + code.substring(3));
                                lblVrijeme.setText("Novi kod za " + TotpService.getRemainingSeconds() + " s");
                            } catch (Exception ex) {
                                lblKod.setText("Greška");
                                lblVrijeme.setText("");
                            }
                        }
                );

        try {
            String code = TotpService.generateCode(account.getTotpSecret());
            lblKod.setText(code.substring(0, 3) + " " + code.substring(3));
            lblVrijeme.setText("Novi kod za " + TotpService.getRemainingSeconds() + " s");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "TOTP kod nije moguće generirati.", "Greška", JOptionPane.ERROR_MESSAGE);
            return;
        }

        timer.start();
        btnKopiraj.addActionListener(e -> {
            try {
                String code = TotpService.generateCode(account.getTotpSecret());
                kopirajOsjetljiviTekst(code, "TOTP kod je kopiran.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "TOTP kod nije moguće kopirati.");
            }
        });

        JOptionPane.showMessageDialog(this, panel, "TOTP - " + account.getName(), JOptionPane.PLAIN_MESSAGE);
        timer.stop();
    }
}
