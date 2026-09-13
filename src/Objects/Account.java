package Objects;

import com.google.gson.annotations.SerializedName;

public class Account extends VaultItem {
    @SerializedName(value = "username", alternate = {"Username"})
    private String username;

    @SerializedName(value = "password", alternate = {"Password"})
    private String password;

    @SerializedName(value = "link", alternate = {"Link"})
    private String link;

    public Account() {
        super();
        setType("CREDENTIAL");
    }

    public Account(String name, String username, String password, String link, String category) {
        super(name, category, "CREDENTIAL");

        this.username = username;
        this.password = password;
        this.link = link;
    }

    public Account(String name, String username, String password, String category) {
        this(name, username, password, null, category);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}