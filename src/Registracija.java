import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.security.NoSuchAlgorithmException;
import me.gosimple.nbvcxz.Nbvcxz;
import me.gosimple.nbvcxz.resources.Configuration;
import me.gosimple.nbvcxz.resources.ConfigurationBuilder;
import me.gosimple.nbvcxz.scoring.TimeEstimate;

public class Registracija extends JFrame {
    private JPanel panRegistracija;
    private JTextField txtKorIme;
    private JButton btnRegistracija;
    private JPasswordField txtLozinka;
    private JLabel lblPrijava;
    private JLabel lblPotvrdiLozinku;
    private JPasswordField txtPotvrdaLozinke;
    private JProgressBar PgbJacinaLozinke;
    private JLabel lblJacinaLozinke;
    private Nbvcxz nbvcxz;

    public Registracija() {
        setTitle("Passwordium");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1080,720);
        setLocationRelativeTo(null);
        setVisible(true);
        setContentPane(panRegistracija);

        btnRegistracija.setBorderPainted(false);
        btnRegistracija.setBackground(new Color(200,200,200));
        btnRegistracija.setFocusPainted(false);

        Configuration configuration = new ConfigurationBuilder().setMinimumEntropy(40d).createConfiguration();
        nbvcxz = new Nbvcxz(configuration);
        btnRegistracija.addActionListener(new ActionListener() {


            @Override
            public void actionPerformed(ActionEvent e) {
                if( provjeriJesuLiPodaciUneseni() ){

                    txtLozinka.setBackground(Color.white);

                    if( potvrdaLozinke() ){

                            HasherLozinke hasherLozinke= new HasherLozinke();
                            String hashedLozinka = null;
                            if(provjeriJacinuLozinke()>40d){
                                try {
                                    hashedLozinka = hasherLozinke.napraviHash(txtKorIme.getText(),new String(txtLozinka.getPassword()));
                                    try {
                                        hasherLozinke.spremiLozinku(txtKorIme.getText(),hashedLozinka);
                                        JOptionPane.showMessageDialog(Registracija.this,"Uspješna registracija!");
                                        Registracija.this.dispose();
                                    } catch (FileAlreadyExistsException ex){
                                        JOptionPane.showMessageDialog(Registracija.this,ex.getMessage());
                                    } catch (IOException ex) {
                                        JOptionPane.showMessageDialog(Registracija.this,"Došlo je do greške tijekom upisa u datoteku!");
                                    }

                                } catch (NoSuchAlgorithmException ex) {
                                    JOptionPane.showMessageDialog(Registracija.this,"Ne postoji SHA-256 na računalu!");
                                }
                            }else{
                                JOptionPane.showMessageDialog(Registracija.this,"Upisana lozinka nije dovoljno snažna!");
                            }

                    }else{
                        JOptionPane.showMessageDialog(Registracija.this,"Upisana lozinka se razlikuje od vrijednosti upisane u polje za potvrdu lozinke!");
                    }
                }
            }
        });
        lblPrijava.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                Registracija.this.dispose();
            }
        });
        txtLozinka.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                double jacinaLozinke = provjeriJacinuLozinke();
                if(jacinaLozinke<40){
                    PgbJacinaLozinke.setValue((int)Math.round(jacinaLozinke));
                    PgbJacinaLozinke.setForeground(Color.red);
                }else{
                    PgbJacinaLozinke.setValue(40);
                    PgbJacinaLozinke.setForeground(Color.green);
                }
            }
        });
    }

    private double provjeriJacinuLozinke() {
        String lozinka = new String(txtLozinka.getPassword());
        me.gosimple.nbvcxz.scoring.Result rezultat = nbvcxz.estimate(lozinka);
        return rezultat.getEntropy();
    }

    private boolean potvrdaLozinke(){
        boolean potvrdena = false;
        String lozinka = new String(txtLozinka.getPassword());
        String confLozinka = new String(txtPotvrdaLozinke.getPassword());

        if(lozinka.equals(confLozinka)) potvrdena = true;

        return potvrdena;
    }

    private boolean provjeriJesuLiPodaciUneseni(){
        boolean ispravno = true;
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

        if (txtPotvrdaLozinke.getPassword().length == 0) {
            txtPotvrdaLozinke.setBackground(Color.red);
            ispravno = false;
        } else {
            txtPotvrdaLozinke.setBackground(Color.white);
        }
        return ispravno;
    }
}
