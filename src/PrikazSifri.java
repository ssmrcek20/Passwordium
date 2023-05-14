import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class PrikazSifri extends JFrame {
    private JPanel panSifre;
    private JTable tabLozinke;
    private JButton btnDodajLozinku;
    private JButton btnUrediLozinku;
    private JButton btnUkloniLozinku;
    private JScrollPane scrollPan;
    private String korIme;
    private String lozinka;

    public PrikazSifri(){
        setTitle("Passwordium");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1080,720);
        setLocationRelativeTo(null);
        setVisible(true);
        setContentPane(panSifre);

        btnDodajLozinku.setBorderPainted(false);
        btnDodajLozinku.setBackground(new Color(200,200,200));
        btnDodajLozinku.setFocusPainted(false);
        btnDodajLozinku.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DodajLozinke dodajLozinke = new DodajLozinke();
                dodajLozinke.podaci(korIme, lozinka);
                PrikazSifri.this.dispose();
            }
        });

        btnUrediLozinku.setBorderPainted(false);
        btnUrediLozinku.setBackground(new Color(200,200,200));
        btnUrediLozinku.setFocusPainted(false);
        btnUrediLozinku.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int odabraniRed = tabLozinke.getSelectedRow();
                if(odabraniRed != -1){
                    Racun racun = new Racun(
                            tabLozinke.getValueAt(odabraniRed,0).toString(),
                            tabLozinke.getValueAt(odabraniRed,1).toString(),
                            tabLozinke.getValueAt(odabraniRed,2).toString()
                    );
                    if(tabLozinke.getValueAt(odabraniRed,3) != null){
                        racun.Link = tabLozinke.getValueAt(odabraniRed,3).toString();
                    }

                    UrediRacun urediRacun = new UrediRacun();
                    urediRacun.podaci(korIme, lozinka, racun, odabraniRed);
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
        btnUkloniLozinku.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int odabraniRed = tabLozinke.getSelectedRow();
                if(odabraniRed != -1){
                    KripterPodataka kripterPodataka = new KripterPodataka();
                    try {
                        kripterPodataka.izbrisiPodatke(odabraniRed, korIme);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(PrikazSifri.this, "Greška prilikom brisanja!");
                    }

                    DefaultTableModel model = (DefaultTableModel) tabLozinke.getModel();
                    model.removeRow(odabraniRed);
                }else {
                    JOptionPane.showMessageDialog(PrikazSifri.this, "Odaberi red za brisanje!");
                }
            }
        });

        tabLozinke.setRowHeight(30);
        tabLozinke.getTableHeader().setReorderingAllowed(false);
        tabLozinke.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int red = tabLozinke.rowAtPoint(e.getPoint());
                String lozinkaRacuna = (String) tabLozinke.getValueAt(red, 2);
                StringSelection selection = new StringSelection(lozinkaRacuna);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);
            }
        });
    }

    public void prikazPodataka() throws Exception {
        KripterPodataka kripterPodataka = new KripterPodataka();

        Racun[] racuni = kripterPodataka.dohvatiPodatke(korIme,lozinka);
        String[] stupci = {"Naziv", "Korisničko ime", "Lozinka", "Link"};

        DefaultTableModel model = new DefaultTableModel(stupci, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (Racun racun : racuni) {
            Object[] red = {racun.Naziv,racun.KorIme,racun.Lozinka,racun.Link};
            model.addRow(red);
        }
        tabLozinke.setModel(model);
        tabLozinke.getColumnModel().getColumn(2).setMinWidth(0);
        tabLozinke.getColumnModel().getColumn(2).setMaxWidth(0);
    }

    public void podaci(String korIme, String lozinka) {
        this.korIme = korIme;
        this.lozinka = lozinka;
    }
}
