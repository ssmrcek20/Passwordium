import me.gosimple.nbvcxz.resources.Generator;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.FileAlreadyExistsException;

public class DodajLozinke extends JFrame{
    private JPanel panDodaj;
    private JTextField txtKorIme;
    private JPasswordField txtLozinka;
    private JTextField txtNaziv;
    private JTextField txtLink;
    private JButton btnGenerirajLozinku;
    private JButton btnDodaj;
    private JLabel lblNatrag;
    private String korImeKorisnika;
    private String lozinkaKorisnika;

    public DodajLozinke(){
        setTitle("Passwordium");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1080,720);
        setLocationRelativeTo(null);
        setVisible(true);
        setContentPane(panDodaj);

        btnGenerirajLozinku.setBorderPainted(false);
        btnGenerirajLozinku.setBackground(new Color(200,200,200));
        btnGenerirajLozinku.setFocusPainted(false);

        btnDodaj.setBorderPainted(false);
        btnDodaj.setBackground(new Color(200,200,200));
        btnDodaj.setFocusPainted(false);
        btnDodaj.addActionListener(new ActionListener() {
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
                    Racun racun = new Racun(txtNaziv.getText(),txtKorIme.getText(),new String(txtLozinka.getPassword()),txtLink.getText());
                    try {
                        kripterPodataka.spremiPodatke(racun,korImeKorisnika,lozinkaKorisnika);
                        JOptionPane.showMessageDialog(DodajLozinke.this, "Uspješno dodavanje računa!");

                        PrikazSifri prikazSifri= new PrikazSifri();
                        prikazSifri.podaci(korImeKorisnika, lozinkaKorisnika);
                        prikazSifri.prikazPodataka();
                        DodajLozinke.this.dispose();

                    } catch (FileAlreadyExistsException ex){
                        JOptionPane.showMessageDialog(DodajLozinke.this,ex.getMessage());
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(DodajLozinke.this, "Došlo je do greške tijekom spremanja novog računa!");
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
                DodajLozinke.this.dispose();
            }
        });
        btnGenerirajLozinku.addActionListener(e -> {
            String lozinka = Generator.generatePassphrase("!-?", 5);
            txtLozinka.setText(lozinka);
        });
    }

    public void podaci(String korIme, String lozinka) {
        this.korImeKorisnika = korIme;
        this.lozinkaKorisnika = lozinka;
    }
}
