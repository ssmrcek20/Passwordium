package Views;

import Objects.Account;
import Responses.AccountResponse;
import Services.AccountService;
import Services.UserService;
import Services.VaultCryptoService;
import Services.VaultSession;
import com.google.gson.Gson;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;

public class PrikazSifri extends JFrame {
    private JPanel panSifre;
    private JTable tabLozinke;
    private JButton btnDodajLozinku;
    private JButton btnUrediLozinku;
    private JButton btnUkloniLozinku;
    private JScrollPane scrollPan;
    private JButton btn2FAPostavke;
    private JButton btnLogout;
    private final java.util.List<Account> accounts = new java.util.ArrayList<>();

    public PrikazSifri(){
        setTitle("Passwordium");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {

                VaultSession.lock();

                dispose();
                System.exit(0);
            }
        });
        setContentPane(panSifre);

        setSize(1080, 720);
        setLocationRelativeTo(null);

        setVisible(true);

        btnLogout.setBorderPainted(false);
        btnLogout.setBackground(new Color(200,200,200));
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> {

            try {
                UserService userService = new UserService();

                userService.logout();

            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {

                VaultSession.lock();

                new Prijava();

                PrikazSifri.this.dispose();
            }
        });

        btn2FAPostavke.setBorderPainted(false);
        btn2FAPostavke.setBackground(new Color(200,200,200));
        btn2FAPostavke.setFocusPainted(false);

        btn2FAPostavke.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TwoFAPostavke FApostavke = new TwoFAPostavke();
                PrikazSifri.this.dispose();
            }
        });

        btnDodajLozinku.setBorderPainted(false);
        btnDodajLozinku.setBackground(new Color(200,200,200));
        btnDodajLozinku.setFocusPainted(false);
        btnDodajLozinku.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DodajLozinke dodajLozinke = new DodajLozinke();
                PrikazSifri.this.dispose();
            }
        });

        btnUrediLozinku.setBorderPainted(false);
        btnUrediLozinku.setBackground(new Color(200,200,200));
        btnUrediLozinku.setFocusPainted(false);
        btnUrediLozinku.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int odabraniRed = tabLozinke.getSelectedRow();
                if (odabraniRed != -1) {

                    Account account = accounts.get(odabraniRed);

                    UrediRacun urediRacun = new UrediRacun();
                    urediRacun.podaci(account);
                    urediRacun.prikazPodataka();

                    PrikazSifri.this.dispose();
                }else {
                    JOptionPane.showMessageDialog(PrikazSifri.this, "Odaberi red za uređivanje!");
                }
            }
        });

        btnUkloniLozinku.setBorderPainted(false);
        btnUkloniLozinku.setBackground(new Color(200,200,200));
        btnUkloniLozinku.setFocusPainted(false);
        btnUkloniLozinku.addActionListener(e -> {

            int odabraniRed = tabLozinke.getSelectedRow();

            if (odabraniRed == -1) {
                JOptionPane.showMessageDialog(PrikazSifri.this, "Odaberi red za brisanje!");
                return;
            }

            Account account = accounts.get(odabraniRed);

            try {
                AccountService accountService = new AccountService();

                accountService.deleteAccount(account.Id);

                accounts.remove(odabraniRed);

                DefaultTableModel model = (DefaultTableModel) tabLozinke.getModel();

                model.removeRow(odabraniRed);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(PrikazSifri.this, "Greška prilikom brisanja!");
            }
        });

        tabLozinke.setRowHeight(30);
        tabLozinke.getTableHeader().setReorderingAllowed(false);
        tabLozinke.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int red = tabLozinke.rowAtPoint(e.getPoint());
                if (red < 0 || red >= accounts.size()) {
                    return;
                }

                String kopiranaLozinka = accounts.get(red).Lozinka;
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection selection = new StringSelection(kopiranaLozinka);
                clipboard.setContents(selection, null);

                Timer timer = new Timer(15000, event -> {
                    try {
                        Object trenutniSadrzaj = clipboard.getData(DataFlavor.stringFlavor);

                        if (kopiranaLozinka.equals(trenutniSadrzaj)) {
                            clipboard.setContents(new StringSelection(""), null);
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

                timer.setRepeats(false);
                timer.start();
            }
        });
    }

    public void prikazPodataka() throws Exception {

        AccountService accountService = new AccountService();

        VaultCryptoService cryptoService = new VaultCryptoService();

        Gson gson = new Gson();

        accounts.clear();

        AccountResponse[] accountResponses = accountService.getAccounts();

        String[] stupci = {
                "Naziv",
                "Korisničko ime",
                "Lozinka",
                "Link"
        };

        DefaultTableModel model = new DefaultTableModel(stupci, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        byte[] vaultKey = VaultSession.getVaultKey();

        for (AccountResponse accountResponse : accountResponses) {

            String json = cryptoService.decryptData(accountResponse.getEncryptedData(),
                    accountResponse.getNonce(), accountResponse.getTag(), vaultKey);

            Account newAccount = gson.fromJson(json, Account.class);

            newAccount.Id = accountResponse.getId();

            accounts.add(newAccount);

            Object[] red = {
                    newAccount.Naziv,
                    newAccount.KorIme,
                    newAccount.Lozinka,
                    newAccount.Link
            };

            model.addRow(red);
        }

        tabLozinke.setModel(model);
        tabLozinke.getColumnModel().getColumn(2).setMinWidth(0);
        tabLozinke.getColumnModel().getColumn(2).setMaxWidth(0);
    }

}
