import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PrikazSifri extends JFrame {
    private JPanel panSifre;
    private JTable tabLozinke;
    private JButton btnDodajLozinku;
    private JButton btnUrediLozinku;
    private JButton btnUkloniLozinku;
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
                new DodajLozinke();
            }
        });

        btnUrediLozinku.setBorderPainted(false);
        btnUrediLozinku.setBackground(new Color(200,200,200));
        btnUrediLozinku.setFocusPainted(false);

        btnUkloniLozinku.setBorderPainted(false);
        btnUkloniLozinku.setBackground(new Color(200,200,200));
        btnUkloniLozinku.setFocusPainted(false);

        prikazPodataka();

    }

    private void prikazPodataka() {
        KripterPodataka kripterPodataka = new KripterPodataka();

        String[][] podaci = kripterPodataka.dohvatiPodatke(korIme,lozinka);
        String[] stupci = {"Naziv", "Korisničko ime", "Lozinka", "Link"};

        tabLozinke = new JTable(podaci,stupci);
    }

    public void podaci(String korIme, String lozinka) {
        this.korIme = korIme;
        this.lozinka = lozinka;
    }
}
