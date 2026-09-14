package Services;

import java.util.Arrays;

public class VaultSession {
    private static byte[] vaultKey;
    private static String jwt;

    private VaultSession() {}

    public static void unlock(byte[] key, String token) {
        vaultKey = key;
        jwt = token;
    }

    public static byte[] getVaultKey() {

        if (vaultKey == null) {
            throw new IllegalStateException(
                    "Vault nije otključan."
            );
        }

        return vaultKey;
    }

    public static String getJwt() {

        if (jwt == null) {
            throw new IllegalStateException("Korisnik nije prijavljen.");
        }

        return jwt;
    }

    public static void lock() {

        if (vaultKey != null) {

            Arrays.fill(vaultKey, (byte) 0);
            vaultKey = null;
        }

        jwt = null;
    }
}