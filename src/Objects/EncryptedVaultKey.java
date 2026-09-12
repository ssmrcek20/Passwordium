package Objects;

public class EncryptedVaultKey {

    private final String encryptedVaultKey;
    private final String nonce;
    private final String tag;

    public EncryptedVaultKey(
            String encryptedVaultKey,
            String nonce,
            String tag
    ) {
        this.encryptedVaultKey =
                encryptedVaultKey;

        this.nonce = nonce;
        this.tag = tag;
    }

    public String getEncryptedVaultKey() {
        return encryptedVaultKey;
    }

    public String getNonce() {
        return nonce;
    }

    public String getTag() {
        return tag;
    }
}
