package Services;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HasherLozinke {
    public String napraviHash(String korIme, String lozinka) throws NoSuchAlgorithmException{
        String salt = korIme + "AJCKECHJEKJSJDSJRTZH";

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt.getBytes(StandardCharsets.UTF_8));
        byte[] hashedLozinka = md.digest(lozinka.getBytes(StandardCharsets.UTF_8));

        StringBuilder sb = new StringBuilder();
        for (byte b : hashedLozinka) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public void spremiLozinku(String korIme, String hashedLozinka) throws IOException{
        if(postojiHashDatoteka(korIme)){
           throw new FileAlreadyExistsException("To korisničko ime se već koristi!");
        } else {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(korIme + ".txt"))) {
                writer.write(hashedLozinka+ "\n");
            }
        }
    }

    public void dodajTOTPKljuc(String totpKljuc, String korIme)throws IOException{
        StringBuilder fileContent = new StringBuilder();
        if(postojiHashDatoteka(korIme)){
            String postojeciPodaci;
            String linija;
            String[] linije;
            try (BufferedReader reader = new BufferedReader(new FileReader(korIme + ".txt"))) {
                while ((linija = reader.readLine()) != null) {
                    fileContent.append(linija);
                    System.out.println(linija);
                }
                linije = fileContent.toString().split(System.lineSeparator());
                String[] prviDio = linije[0].split(";");
                postojeciPodaci = prviDio[0];
            }

            if (postojeciPodaci != null && postojeciPodaci.endsWith("\n")) {
                postojeciPodaci = postojeciPodaci.substring(0, postojeciPodaci.length() - 1);
            }

            linije[0] = postojeciPodaci + ";" + totpKljuc + "\n";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(korIme + ".txt"))) {
                for (String line : linije) {
                    writer.write(line);
                    System.out.println(line);
                }
            }

        } else {
            throw new FileNotFoundException("To korisničko ime ne postoji!");
        }
    }

    public boolean postojiHashDatoteka(String korIme){
        File datoteka = new File(korIme + ".txt");
        return datoteka.exists();
    }

    public String dohvatiHash(String korIme)throws IOException{
        try (BufferedReader reader = new BufferedReader(new FileReader(korIme + ".txt"))) {
            return reader.readLine();
        }

    }
}
