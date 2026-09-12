package Responses;

public class AccountResponse {

    private int id;
    private String encryptedData;
    private String nonce;
    private String tag;

    public int getId() {
        return id;
    }

    public String getEncryptedData() {
        return encryptedData;
    }

    public String getNonce() {
        return nonce;
    }

    public String getTag() {
        return tag;
    }
}