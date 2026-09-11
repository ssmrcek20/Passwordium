package Services;

import Objects.EncryptedVaultKey;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class VaultCryptoService {

    private static final SecureRandom secureRandom = new SecureRandom();

    private static final int SALT_SIZE = 16;
    private static final int VAULT_KEY_SIZE = 32;
    private static final int NONCE_SIZE = 12;

    private static final int MEMORY_KB = 65536;
    private static final int ITERATIONS = 3;
    private static final int PARALLELISM = 1;

    private static final int KEK_SIZE = 32;

    public byte[] generateSalt() {
        byte[] salt = new byte[SALT_SIZE];
        secureRandom.nextBytes(salt);
        return salt;
    }

    public byte[] generateVaultKey() {
        byte[] vaultKey = new byte[VAULT_KEY_SIZE];
        secureRandom.nextBytes(vaultKey);
        return vaultKey;
    }

    public byte[] deriveKek(char[] password, byte[] salt) {

        Argon2Parameters parameters = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                        .withSalt(salt)
                        .withMemoryAsKB(MEMORY_KB)
                        .withIterations(ITERATIONS)
                        .withParallelism(PARALLELISM)
                        .build();

        Argon2BytesGenerator generator = new Argon2BytesGenerator();

        generator.init(parameters);
        byte[] kek = new byte[KEK_SIZE];
        generator.generateBytes(password, kek);
        return kek;
    }

    public byte[] deriveKek(char[] password, String vaultSaltBase64
    ) {

        byte[] salt = Base64.getDecoder().decode(vaultSaltBase64);

        return deriveKek(password, salt);
    }

    public EncryptedVaultKey encryptVaultKey(byte[] vaultKey, byte[] kek) throws Exception {
        byte[] nonce = new byte[NONCE_SIZE];
        secureRandom.nextBytes(nonce);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKeySpec secretKey = new SecretKeySpec(kek, "AES");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(128, nonce);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);
        byte[] encryptedWithTag = cipher.doFinal(vaultKey);
        int tagLength = 16;
        int ciphertextLength = encryptedWithTag.length - tagLength;
        byte[] ciphertext = new byte[ciphertextLength];
        byte[] tag = new byte[tagLength];
        System.arraycopy(encryptedWithTag, 0, ciphertext, 0, ciphertextLength);
        System.arraycopy(encryptedWithTag, ciphertextLength, tag, 0, tagLength);

        return new EncryptedVaultKey(
                Base64.getEncoder().encodeToString(ciphertext),
                Base64.getEncoder().encodeToString(nonce),
                Base64.getEncoder().encodeToString(tag)
        );
    }

    public byte[] decryptVaultKey(byte[] kek, String encryptedVaultKeyBase64, String nonceBase64, String tagBase64) throws Exception {
        byte[] ciphertext = Base64.getDecoder().decode(encryptedVaultKeyBase64);
        byte[] nonce = Base64.getDecoder().decode(nonceBase64);
        byte[] tag = Base64.getDecoder().decode(tagBase64);
        byte[] encryptedWithTag = new byte[ciphertext.length + tag.length];

        System.arraycopy(ciphertext, 0, encryptedWithTag, 0, ciphertext.length);
        System.arraycopy(tag, 0, encryptedWithTag, ciphertext.length, tag.length);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        SecretKeySpec secretKey = new SecretKeySpec(kek, "AES");

        GCMParameterSpec gcmSpec = new GCMParameterSpec(128, nonce);

        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);

        return cipher.doFinal(encryptedWithTag);
    }

    public String decryptData(String encryptedDataBase64, String nonceBase64, String tagBase64, byte[] vaultKey) throws Exception {
        byte[] ciphertext = Base64.getDecoder().decode(encryptedDataBase64);
        byte[] nonce = Base64.getDecoder().decode(nonceBase64);
        byte[] tag = Base64.getDecoder().decode(tagBase64);
        byte[] encryptedWithTag = new byte[ciphertext.length + tag.length];

        System.arraycopy(ciphertext, 0, encryptedWithTag, 0, ciphertext.length);
        System.arraycopy(tag, 0, encryptedWithTag, ciphertext.length, tag.length);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKeySpec key = new SecretKeySpec(vaultKey, "AES");
        GCMParameterSpec spec = new GCMParameterSpec(128, nonce);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);

        byte[] plaintext = cipher.doFinal(encryptedWithTag);

        return new String(plaintext, StandardCharsets.UTF_8);
    }
}