import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.security.NoSuchAlgorithmException;
import me.gosimple.nbvcxz.Nbvcxz;

public class Registracija extends JFrame {
    private JPanel panRegistracija;
    private JTextField txtKorIme;
    private JButton btnRegistracija;
    private JPasswordField txtLozinka;
    private Nbvcxz nbvcxz;
    private JLabel lblPrijava;
    private JLabel lblPotvrdiLozinku;
    private JPasswordField txtPotvrdaLozinke;

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
        btnRegistracija.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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

                if(ispravno){
                    txtLozinka.setBackground(Color.white);

                    if(potvrdaLozinke()){
                        HasherLozinke hasherLozinke= new HasherLozinke();
                        String hashedLozinka = null;
                        try {
                            hashedLozinka = hasherLozinke.napraviHash(txtKorIme.getText(),new String(txtLozinka.getPassword()));

                            try {
                                hasherLozinke.spremiLozinku(txtKorIme.getText(),hashedLozinka);
                                JOptionPane.showMessageDialog(Registracija.this,"Uspješna registarcija!");
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
    }
    private boolean potvrdaLozinke(){
        boolean potvrdena = false;
        String lozinka = new String(txtLozinka.getPassword());
        String confLozinka = new String(txtPotvrdaLozinke.getPassword());

        if(lozinka.equals(confLozinka)) potvrdena = true;

        return potvrdena;
    }
}
