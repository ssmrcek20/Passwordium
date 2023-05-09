import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Registracija extends JFrame {
    private JPanel panRegistracija;
    private JTextField txtKorIme;
    private JButton btnRegistracija;
    private JPasswordField txtLozinka;
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

                if(ispravno){
                    txtLozinka.setBackground(Color.white);
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
                }
            }
        });
    }
}
