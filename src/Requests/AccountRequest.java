package Requests;

public class AccountRequest {

    private int id;
    private String encryptedData;
    private String nonce;
    private String tag;

    public AccountRequest(
            int id,
            String encryptedData,
            String nonce,
            String tag
    ) {
        this.id = id;
        this.encryptedData = encryptedData;
        this.nonce = nonce;
        this.tag = tag;
    }
}