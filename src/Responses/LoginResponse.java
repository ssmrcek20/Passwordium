package Responses;

public class LoginResponse {
    private String jwt;
    private String refreshToken;
    private String vaultSalt;
    private String encryptedVaultKey;
    private String vaultKeyNonce;
    private String vaultKeyTag;

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getVaultSalt() {
        return vaultSalt;
    }

    public void setVaultSalt(String vaultSalt) {
        this.vaultSalt = vaultSalt;
    }

    public String getEncryptedVaultKey() {
        return encryptedVaultKey;
    }

    public void setEncryptedVaultKey(String encryptedVaultKey) {
        this.encryptedVaultKey = encryptedVaultKey;
    }

    public String getVaultKeyNonce() {
        return vaultKeyNonce;
    }

    public void setVaultKeyNonce(String vaultKeyNonce) {
        this.vaultKeyNonce = vaultKeyNonce;
    }

    public String getVaultKeyTag() {
        return vaultKeyTag;
    }

    public void setVaultKeyTag(String vaultKeyTag) {
        this.vaultKeyTag = vaultKeyTag;
    }
}
