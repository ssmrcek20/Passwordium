package Services;

import Objects.Account;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Base64;

public class KripterPodataka {

    public Account[] dohvatiPodatke(String korIme, String lozinka) throws Exception {
        ArrayList<Account> racuni = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(korIme + ".txt"));
        reader.readLine();
        String racun = reader.readLine();
        while (racun != null) {
            String[] dijelovi = racun.split(";");
            Account accountObject;

            if(dijelovi.length == 4) accountObject = new Account(dijelovi[0],dijelovi[1],dijelovi[2],dijelovi[3]);
            else accountObject = new Account(dijelovi[0],dijelovi[1],dijelovi[2]);

            accountObject.Lozinka = dekripter(accountObject,lozinka,korIme);
            racuni.add(accountObject);

            racun = reader.readLine();
        }
        reader.close();
        return racuni.toArray(new Account[0]);
    }
    public void spremiPodatke(Account account, String korIme, String lozinka) throws Exception {
        if(vecPostojiNaziv(korIme, account.Naziv)){
            throw new FileAlreadyExistsException("Taj naziv se već koristi!");
        }else {
            String kriptiranaLozinka = kripter(account, korIme, lozinka);

            BufferedWriter writer = new BufferedWriter(new FileWriter(korIme + ".txt", true));
            writer.write(account.Naziv + ";" + account.KorIme + ";" + kriptiranaLozinka + ";" + account.Link + "\n");
            writer.close();
        }
    }

    private String dekripter(Account account, String lozinka, String korIme) throws Exception {
        SecretKeySpec kljuc = new SecretKeySpec(generatorKljuca(lozinka,korIme, account).getEncoded(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, kljuc);
        byte[] dekriptiranaLozinka = cipher.doFinal(Base64.getDecoder().decode(account.Lozinka));
        return new String(dekriptiranaLozinka, StandardCharsets.UTF_8);
    }

    private String kripter(Account account, String korIme, String lozinka) throws Exception {
        SecretKeySpec kljuc = new SecretKeySpec(generatorKljuca(lozinka,korIme, account).getEncoded(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, kljuc);
        byte[] kriptiranaLozinka = cipher.doFinal(account.Lozinka.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(kriptiranaLozinka);
    }
    private SecretKey generatorKljuca(String lozinka,String korIme, Account account) throws Exception {
        String salt = account.Naziv + korIme + "AJCKECHJEKJSJDSJRTZH";
        KeySpec spec = new PBEKeySpec(lozinka.toCharArray(), salt.getBytes(StandardCharsets.UTF_8), 10000, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return factory.generateSecret(spec);
    }

    public boolean vecPostojiNaziv(String korIme, String naziv) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(korIme + ".txt"));
        reader.readLine();
        String racun;
        while ((racun = reader.readLine()) != null) {
            String[] dijelovi = racun.split(";");
            if (dijelovi[0].equals(naziv)) {
                return true;
            }
        }
        return false;
    }

    public void izbrisiPodatke(int odabraniRed, String korIme) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(korIme + ".txt"));
        StringBuilder sb = new StringBuilder();
        String racun;
        int linija = -1;
        while ((racun = reader.readLine()) != null) {
            if (linija != odabraniRed) {
                sb.append(racun).append("\n");
            }
            linija++;
        }
        reader.close();

        BufferedWriter writer = new BufferedWriter(new FileWriter(korIme + ".txt"));
        writer.write(sb.toString());
        writer.close();
    }
}
