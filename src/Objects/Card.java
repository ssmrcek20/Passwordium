package Objects;

public class Card extends VaultItem {
    private String cardType;
    private String cardholderName;
    private String cardNumber;
    private String expiryDate;
    private String cvv;

    public Card() {
        super();
        setType("CARD");
    }

    public Card(String name, String cardholderName, String cardNumber, String expiryDate, String cvv, String category) {
        super(name, category, "CARD");
        this.cardholderName = cardholderName;
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
    }

    public String getCardholderName() {
        return cardholderName;
    }

    public void setCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
}