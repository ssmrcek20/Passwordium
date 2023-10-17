import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

public class UrediRacun extends JFrame {
    private JTextField txtKorIme;
    private JPasswordField txtLozinka;
    private JTextField txtNaziv;
    private JTextField txtLink;
    private JButton btnGenerirajLozinku;
    private JButton btnUredi;
    private JPanel panUredi;
    private JLabel lblNatrag;
    private String korImeKorisnika;
    private String lozinkaKorisnika;
    private Racun racun;
    private int odabraniRed;

    public UrediRacun(){
        setTitle("Passwordium");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1080,720);
        setLocationRelativeTo(null);
        setVisible(true);
        setContentPane(panUredi);

        btnGenerirajLozinku.setBorderPainted(false);
        btnGenerirajLozinku.setBackground(new Color(200,200,200));
        btnGenerirajLozinku.setFocusPainted(false);

        btnUredi.setBorderPainted(false);
        btnUredi.setBackground(new Color(200,200,200));
        btnUredi.setFocusPainted(false);
        btnUredi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean ispravno = true;

                if (txtNaziv.getText().equals("")) {
                    txtNaziv.setBackground(Color.red);
                    ispravno = false;
                } else {
                    txtNaziv.setBackground(Color.white);
                }

                if (txtKorIme.getText().equals("")) {
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



                if(ispravno) {
                    KripterPodataka kripterPodataka = new KripterPodataka();
                    try {
                        kripterPodataka.izbrisiPodatke(odabraniRed, korImeKorisnika);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(UrediRacun.this, "Greška prilikom uređivanja!");
                    }

                    Racun noviRacun = new Racun(txtNaziv.getText(),txtKorIme.getText(),new String(txtLozinka.getPassword()),txtLink.getText());
                    try {
                        kripterPodataka.spremiPodatke(noviRacun,korImeKorisnika,lozinkaKorisnika);
                        JOptionPane.showMessageDialog(UrediRacun.this, "Uspješno uređivanje računa!");

                        PrikazSifri prikazSifri= new PrikazSifri();
                        prikazSifri.podaci(korImeKorisnika, lozinkaKorisnika);
                        prikazSifri.prikazPodataka();
                        UrediRacun.this.dispose();

                    } catch (FileAlreadyExistsException ex){
                        JOptionPane.showMessageDialog(UrediRacun.this,ex.getMessage());
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(UrediRacun.this, "Došlo je do greške tijekom uređivanja računa!");
                    }
                }
            }
        });
        lblNatrag.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                PrikazSifri prikazSifri= new PrikazSifri();
                prikazSifri.podaci(korImeKorisnika, lozinkaKorisnika);
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
        txtNaziv.setText(racun.Naziv);
        txtKorIme.setText(racun.KorIme);
        txtLink.setText(racun.Link);
        txtLozinka.setText(racun.Lozinka);
    }

    public void podaci(String korIme, String lozinka, Racun racun, int odabraniRed) {
        this.korImeKorisnika = korIme;
        this.lozinkaKorisnika = lozinka;
        this.racun = racun;
        this.odabraniRed = odabraniRed;
    }
}