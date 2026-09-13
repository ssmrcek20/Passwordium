package Objects;

import com.google.gson.annotations.SerializedName;

public abstract class VaultItem {

    private int id;
    @SerializedName(value = "name", alternate = {"Name"})
    private String name;

    @SerializedName(value = "category", alternate = {"Category"})
    private String category;

    @SerializedName(value = "type", alternate = {"Type"})
    private String type;

    public VaultItem() {
    }

    public VaultItem(
            String name,
            String category,
            String type
    ) {
        this.name = name;
        this.category = category;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    protected void setType(String type) {
        this.type = type;
    }
}