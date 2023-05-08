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
                if(txtKorIme.getText().equals("")){
                    txtKorIme.setBackground(Color.red);
                } else if (txtLozinka.getPassword().length == 0) {
                    txtLozinka.setBackground(Color.red);
                    txtKorIme.setBackground(Color.white);
                } else {
                    txtLozinka.setBackground(Color.white);
                    HasherLozinke hasherLozinke= new HasherLozinke();
                    String hashedLozinka = null;
                    try {
                        hashedLozinka = hasherLozinke.hash(txtKorIme.getText(),new String(txtLozinka.getPassword()));
                    } catch (NoSuchAlgorithmException ex) {
                        JOptionPane.showMessageDialog(Registracija.this,"Ne postoji SHA-256 na računalu!");
                    }
                    try {
                        hasherLozinke.spremiKorImeILozinku(txtKorIme.getText(),hashedLozinka);
                        JOptionPane.showMessageDialog(Registracija.this,"Uspješna registarcija!");
                        Registracija.this.dispose();
                    } catch (FileAlreadyExistsException ex){
                        JOptionPane.showMessageDialog(Registracija.this,ex.getMessage());
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(Registracija.this,"Došlo je do greške tijekom upisa u datoteku!");
                    }
                }
            }
        });
    }
}
