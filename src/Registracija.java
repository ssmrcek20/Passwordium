import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

            }
        });
    }
}
