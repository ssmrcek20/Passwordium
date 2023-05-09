import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Random;

public class KripterPodataka {

    public String[][] dohvatiPodatke(String korIme, String lozinka) {
        return null;
    }
    public void spremiPodatke(Racun racun, String korIme, String lozinka) throws Exception {
        String kriptiranaLozinka = kripter(racun, korIme, lozinka);

        BufferedWriter pisac = new BufferedWriter(new FileWriter(korIme + ".txt", true));
        pisac.write(racun.Naziv + ":" + racun.KorIme + ":" + kriptiranaLozinka + ":" + racun.Link + "\n");
        pisac.close();
    }

    private String[][] dekripter(String[][] kriptiraniPodaci,String lozinka) {
        return null;
    }

    private String kripter(Racun racun,String korIme, String lozinka) throws Exception {
        SecretKeySpec kljuc = new SecretKeySpec(generatorKljuca(lozinka,korIme).getEncoded(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, kljuc);
        byte[] kriptiranaLozinka = cipher.doFinal(racun.Lozinka.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(kriptiranaLozinka);
    }
    private SecretKey generatorKljuca(String lozinka,String korIme) throws Exception {
        String salt = korIme + "AJCKECHJEKJSJDSJRTZH";
        KeySpec spec = new PBEKeySpec(lozinka.toCharArray(), salt.getBytes(StandardCharsets.UTF_8), 10000, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return factory.generateSecret(spec);
    }
}
