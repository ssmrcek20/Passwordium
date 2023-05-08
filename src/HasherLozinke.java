import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HasherLozinke {
    public String hash(String korIme, String lozinka) throws NoSuchAlgorithmException{
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

    public void spremiKorImeILozinku(String korIme, String hashedLozinka) throws IOException{
        File datoteka = new File(korIme + ".txt");
        if(datoteka.exists()){
           throw new FileAlreadyExistsException("To korisničko ime se već koristi!");
        } else {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(datoteka))) {
                writer.write(korIme + ":" + hashedLozinka);
            }
        }
    }
}
