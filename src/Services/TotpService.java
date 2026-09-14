package Services;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;

public class TotpService {
    private static final int TIME_STEP_SECONDS = 30;
    private static final int DIGITS = 6;
    private TotpService() {
    }
    public static String generateCode(String base32Secret) throws Exception {

        if (base32Secret == null || base32Secret.isBlank()) {
            throw new IllegalArgumentException(
                    "TOTP secret nije unesen."
            );
        }

        byte[] secret = decodeBase32(normalizeSecret(base32Secret));
        long counter = (System.currentTimeMillis() / 1000L) / TIME_STEP_SECONDS;

        byte[] counterBytes = new byte[8];

        for (int i = 7; i >= 0; i--) {
            counterBytes[i] = (byte) (counter & 0xFF);
            counter >>= 8;
        }

        Mac mac = Mac.getInstance("HmacSHA1");
        SecretKeySpec key = new SecretKeySpec(secret, "HmacSHA1");
        mac.init(key);

        byte[] hash = mac.doFinal(counterBytes);
        int offset = hash[hash.length - 1] & 0x0F;
        int binary = ((hash[offset] & 0x7F) << 24) | ((hash[offset + 1] & 0xFF) << 16) |
                ((hash[offset + 2] & 0xFF) << 8) | (hash[offset + 3] & 0xFF);
        int otp = binary % (int) Math.pow(10, DIGITS);

        return String.format("%06d", otp);
    }

    public static int getRemainingSeconds() {
        long seconds = System.currentTimeMillis() / 1000L;

        return TIME_STEP_SECONDS - (int) (seconds % TIME_STEP_SECONDS);
    }

    public static boolean isValidSecret(String secret) {
        if (secret == null || secret.isBlank()) {
            return false;
        }

        try {
            byte[] decoded = decodeBase32(normalizeSecret(secret));
            return decoded.length > 0;
        } catch (Exception ex) {
            return false;
        }
    }

    private static String normalizeSecret(String secret) {
        return secret.replace(" ", "").replace("-", "").replace("=", "").toUpperCase();
    }

    private static byte[] decodeBase32(String base32) {
        final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        int buffer = 0;
        int bitsLeft = 0;

        for (char c : base32.toCharArray()) {
            int value = alphabet.indexOf(c);

            if (value == -1) {
                throw new IllegalArgumentException(
                        "Neispravan Base32 TOTP secret."
                );
            }

            buffer = (buffer << 5) | value;
            bitsLeft += 5;

            if (bitsLeft >= 8) {bitsLeft -= 8;output.write((buffer >> bitsLeft) & 0xFF);
            }
        }

        return output.toByteArray();
    }
}