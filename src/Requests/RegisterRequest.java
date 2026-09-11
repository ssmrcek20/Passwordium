package Requests;

public class RegisterRequest {

    private String username;
    private String password;

    private String vaultSalt;
    private String encryptedVaultKey;
    private String vaultKeyNonce;
    private String vaultKeyTag;

    public RegisterRequest(String username, String password, String vaultSalt,
                           String encryptedVaultKey, String vaultKeyNonce, String vaultKeyTag) {
        this.username = username;
        this.password = password;
        this.vaultSalt = vaultSalt;
        this.encryptedVaultKey = encryptedVaultKey;
        this.vaultKeyNonce = vaultKeyNonce;
        this.vaultKeyTag = vaultKeyTag;
    }
}
