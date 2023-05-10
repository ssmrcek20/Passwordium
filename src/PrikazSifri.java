import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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

        btnUkloniLozinku.setBorderPainted(false);
        btnUkloniLozinku.setBackground(new Color(200,200,200));
        btnUkloniLozinku.setFocusPainted(false);

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

        DefaultTableModel model = new DefaultTableModel(stupci, 0);
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
