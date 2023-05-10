import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Base64;

public class KripterPodataka {

    public Racun[] dohvatiPodatke(String korIme, String lozinka) throws Exception {
        ArrayList<Racun> racuni = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(korIme + ".txt"));
        reader.readLine();
        String racun = reader.readLine();
        while (racun != null) {
            String[] dijelovi = racun.split(":");
            Racun racunObjekt;

            if(dijelovi.length == 4) racunObjekt = new Racun(dijelovi[0],dijelovi[1],dijelovi[2],dijelovi[3]);
            else racunObjekt = new Racun(dijelovi[0],dijelovi[1],dijelovi[2]);

            racunObjekt.Lozinka = dekripter(racunObjekt,lozinka,korIme);
            racuni.add(racunObjekt);

            racun = reader.readLine();
        }
        reader.close();
        return racuni.toArray(new Racun[0]);
    }
    public void spremiPodatke(Racun racun, String korIme, String lozinka) throws Exception {
        if(vecPostojiNaziv(korIme,racun.Naziv)){
            throw new FileAlreadyExistsException("Taj naziv se već koristi!");
        }else {
            String kriptiranaLozinka = kripter(racun, korIme, lozinka);

            BufferedWriter writer = new BufferedWriter(new FileWriter(korIme + ".txt", true));
            writer.write(racun.Naziv + ":" + racun.KorIme + ":" + kriptiranaLozinka + ":" + racun.Link + "\n");
            writer.close();
        }
    }

    private String dekripter(Racun racun, String lozinka,String korIme) throws Exception {
        SecretKeySpec kljuc = new SecretKeySpec(generatorKljuca(lozinka,korIme,racun).getEncoded(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, kljuc);
        byte[] dekriptiranaLozinka = cipher.doFinal(Base64.getDecoder().decode(racun.Lozinka));
        return new String(dekriptiranaLozinka, StandardCharsets.UTF_8);
    }

    private String kripter(Racun racun,String korIme, String lozinka) throws Exception {
        SecretKeySpec kljuc = new SecretKeySpec(generatorKljuca(lozinka,korIme,racun).getEncoded(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, kljuc);
        byte[] kriptiranaLozinka = cipher.doFinal(racun.Lozinka.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(kriptiranaLozinka);
    }
    private SecretKey generatorKljuca(String lozinka,String korIme, Racun racun) throws Exception {
        String salt = racun.Naziv + korIme + "AJCKECHJEKJSJDSJRTZH";
        KeySpec spec = new PBEKeySpec(lozinka.toCharArray(), salt.getBytes(StandardCharsets.UTF_8), 10000, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return factory.generateSecret(spec);
    }

    public boolean vecPostojiNaziv(String korIme, String naziv) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(korIme + ".txt"));
        reader.readLine();
        String tekst;
        while ((tekst = reader.readLine()) != null) {
            String[] dijelovi = tekst.split(":");
            if (dijelovi[0].equals(naziv)) {
                return true;
            }
        }
        return false;
    }
}
