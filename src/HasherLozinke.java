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
                writer.write(hashedLozinka);
            }
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
