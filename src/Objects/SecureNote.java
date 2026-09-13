package Objects;

public class SecureNote extends VaultItem {
    private String content;

    public SecureNote() {
        super();
        setType("SECURE_NOTE");
    }

    public SecureNote(String name, String content, String category) {
        super(name, category, "SECURE_NOTE");
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}