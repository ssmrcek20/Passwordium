import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.SecureRandom;
import java.util.Base64;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import java.awt.image.BufferedImage;

public class TwoFAPostavke extends JFrame{
    private JCheckBox CheckBox2FA;
    private JButton btnNatrag;
    private JPanel pan2FA;
    private JLabel lblQRCode;

    public TwoFAPostavke(){
        setTitle("Passwordium");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1080,720);
        setLocationRelativeTo(null);
        setVisible(true);
        setContentPane(pan2FA);

        btnNatrag.setBorderPainted(false);
        btnNatrag.setBackground(new Color(200,200,200));
        btnNatrag.setFocusPainted(false);

        btnNatrag.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PrikazSifri prikazSifri = new PrikazSifri();
                TwoFAPostavke.this.dispose();
            }
        });

        CheckBox2FA.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBox source = (JCheckBox) e.getSource();
                if (source.isSelected()) {
                    byte[] randomBytes = new byte[20];
                    SecureRandom secureRandom = new SecureRandom();
                    secureRandom.nextBytes(randomBytes);
                    String secretKey = Base64.getEncoder().encodeToString(randomBytes);
                    BufferedImage qrCodeImage = generateQRCodeImage(secretKey);
                    ImageIcon icon = new ImageIcon(qrCodeImage);
                    lblQRCode.setIcon(icon);
                } else {
                    lblQRCode.setIcon(null);
                }
            }
        });
    }
    private BufferedImage generateQRCodeImage(String data) {
        try {
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 200, 200);
            int matrixWidth = bitMatrix.getWidth();
            BufferedImage image = new BufferedImage(matrixWidth, matrixWidth, BufferedImage.TYPE_INT_RGB);

            for (int x = 0; x < matrixWidth; x++) {
                for (int y = 0; y < matrixWidth; y++) {
                    image.setRGB(x, y, bitMatrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
                }
            }

            return image;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
