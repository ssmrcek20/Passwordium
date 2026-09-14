package Services;

import Objects.Account;
import Objects.Card;
import Objects.EncryptedVaultKey;
import Objects.SecureNote;
import Objects.VaultItem;
import com.google.gson.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class VaultImportExportService {
    private final Gson gson;
    public VaultImportExportService() {

        gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }
    public int exportVault(Component parent, Collection<VaultItem> items) throws IOException {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Izvoz Passwordium trezora");
        chooser.setFileFilter(new FileNameExtensionFilter("JSON datoteka (*.json)", "json"));
        chooser.setSelectedFile(new File("passwordium_export.json"));

        int rezultat = chooser.showSaveDialog(parent);

        if (rezultat != JFileChooser.APPROVE_OPTION) {
            return 0;
        }

        File file = chooser.getSelectedFile();

        if (!file.getName().toLowerCase().endsWith(".json")) {
            file = new File(file.getAbsolutePath() + ".json");
        }

        JsonObject root = new JsonObject();
        root.addProperty("formatVersion", 1);
        root.addProperty("application", "Passwordium");

        JsonArray jsonItems = new JsonArray();
        int brojIzvezenih = 0;

        for (VaultItem item : items) {
            JsonObject jsonItem = pretvoriZaIzvoz(item);

            if (jsonItem != null) {
                jsonItems.add(jsonItem);
                brojIzvezenih++;
            }
        }

        root.add("items", jsonItems);

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            gson.toJson(root, writer);
        }

        return brojIzvezenih;
    }

    private JsonObject pretvoriZaIzvoz(VaultItem item) {
        JsonObject object = new JsonObject();

        if (item instanceof Account account) {
            object.addProperty("type", "CREDENTIAL");
            object.addProperty("name", vrijednost(account.getName()));
            object.addProperty("category", vrijednost(account.getCategory()));
            object.addProperty("username", vrijednost(account.getUsername()));
            object.addProperty("password", vrijednost(account.getPassword()));
            object.addProperty("link", vrijednost(account.getLink()));

            return object;
        }

        if (item instanceof Card card) {
            object.addProperty("type", "CARD");
            object.addProperty("name", vrijednost(card.getName()));
            object.addProperty("category", vrijednost(card.getCategory()));
            object.addProperty("cardholderName", vrijednost(card.getCardholderName()));
            object.addProperty("cardNumber", vrijednost(card.getCardNumber()));
            object.addProperty("expiryDate", vrijednost(card.getExpiryDate()));
            object.addProperty("cvv", vrijednost(card.getCvv()));

            return object;
        }

        if (item instanceof SecureNote note) {
            object.addProperty("type", "SECURE_NOTE");
            object.addProperty("name", vrijednost(note.getName()));
            object.addProperty("category", vrijednost(note.getCategory()));
            object.addProperty("content", vrijednost(note.getContent()));

            return object;
        }

        return null;
    }

    public List<VaultItem> readVault(Component parent) throws IOException {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Uvoz Passwordium trezora");
        chooser.setFileFilter(new FileNameExtensionFilter("JSON datoteka (*.json)", "json"));

        int rezultat = chooser.showOpenDialog(parent);

        if (rezultat != JFileChooser.APPROVE_OPTION) {
            return List.of();
        }

        File file = chooser.getSelectedFile();
        JsonObject root;

        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {

            JsonElement element = JsonParser.parseReader(reader);
            if (!element.isJsonObject()) {
                throw new IOException("Neispravan format JSON datoteke.");
            }

            root = element.getAsJsonObject();
        }

        provjeriFormat(root);
        JsonArray jsonItems = root.getAsJsonArray("items");
        List<VaultItem> items = new ArrayList<>();
        int indeks = 0;

        for (JsonElement element : jsonItems) {
            indeks++;
            if (!element.isJsonObject()) {
                throw new IOException("Neispravan zapis broj " + indeks);
            }

            JsonObject object = element.getAsJsonObject();
            VaultItem item = pretvoriIzUvoza(object, indeks);
            items.add(item);
        }

        return items;
    }

    private void provjeriFormat(JsonObject root) throws IOException {
        if (!root.has("items") || !root.get("items").isJsonArray()) {
            throw new IOException("Datoteka ne sadrži Passwordium zapise.");
        }

        if (root.has("formatVersion")) {
            int verzija = root.get("formatVersion").getAsInt();
            if (verzija != 1) {
                throw new IOException("Nepodržana verzija izvozne datoteke: " + verzija);
            }
        }
    }

    private VaultItem pretvoriIzUvoza(JsonObject object, int indeks) throws IOException {
        if (!object.has("type") || object.get("type").isJsonNull()) {
            throw new IOException("Zapis " + indeks + " nema definiran tip.");
        }

        String type = object.get("type").getAsString();
        return switch (type) {
            case "CREDENTIAL" -> kreirajAccount(object, indeks);
            case "CARD" -> kreirajCard(object, indeks);
            case "SECURE_NOTE" -> kreirajSecureNote(object, indeks);

            default -> throw new IOException("Nepoznata vrsta zapisa '" + type + "' u zapisu " + indeks);
        };
    }

    private Account kreirajAccount(JsonObject object, int indeks) throws IOException {
        String name = dohvati(object, "name");
        String username = dohvati(object, "username");
        String password = dohvati(object, "password");
        String link = dohvati(object, "link");
        String category = kategorija(object);

        if (name.isBlank()) {
            throw new IOException("Vjerodajnica " + indeks + " nema naziv.");
        }

        if (username.isBlank()) {
            throw new IOException("Vjerodajnica " + indeks + " nema korisničko ime.");
        }

        if (password.isBlank()) {
            throw new IOException("Vjerodajnica " + indeks + " nema lozinku.");
        }

        return new Account(name, username, password, link, category);
    }


    private Card kreirajCard(JsonObject object, int indeks) throws IOException {
        String name = dohvati(object, "name");
        String cardholderName = dohvati(object, "cardholderName");
        String cardNumber = dohvati(object, "cardNumber");
        String expiryDate = dohvati(object, "expiryDate");
        String cvv = dohvati(object, "cvv");
        String category = kategorija(object);

        if (name.isBlank()) {
            throw new IOException("Kartica " + indeks + " nema naziv.");
        }

        if (cardNumber.isBlank()) {
            throw new IOException("Kartica " + indeks + " nema broj kartice.");
        }

        return new Card(name, cardholderName, cardNumber, expiryDate, cvv, category);
    }

    private SecureNote kreirajSecureNote(JsonObject object, int indeks) throws IOException {
        String name = dohvati(object, "name");
        String content = dohvati(object, "content");
        String category = kategorija(object);

        if (name.isBlank()) {
            throw new IOException("Sigurna bilješka " + indeks + " nema naziv.");
        }

        if (content.isBlank()) {
            throw new IOException("Sigurna bilješka " + indeks + " nema sadržaj.");
        }

        return new SecureNote(name, content, category);
    }

    public int saveImportedItems(List<VaultItem> items) throws Exception {
        if (items == null || items.isEmpty()) {
            return 0;
        }

        VaultCryptoService cryptoService = new VaultCryptoService();
        AccountService accountService = new AccountService();

        byte[] vaultKey = VaultSession.getVaultKey();

        if (vaultKey == null) {
            throw new IllegalStateException("Trezor nije otključan.");
        }

        int spremljeno = 0;

        for (VaultItem item : items) {
            String json;

            if (item instanceof Account account) {
                json = gson.toJson(account);
            } else if (item instanceof Card card) {
                json = gson.toJson(card);
            } else if (item instanceof SecureNote note) {
                json = gson.toJson(note);
            } else {
                continue;
            }

            EncryptedVaultKey encryptedData = cryptoService.encryptData(json, vaultKey);
            accountService.addAccount(encryptedData);
            spremljeno++;
        }

        return spremljeno;
    }

    private String kategorija(JsonObject object) {
        String category = dohvati(object, "category").trim();
        if (category.isBlank()) {
            return "Ostalo";
        }

        return category;
    }

    private String dohvati(JsonObject object, String property) {
        if (!object.has(property) || object.get(property).isJsonNull()) {
            return "";
        }

        return object.get(property).getAsString();
    }

    private String vrijednost(String value) {
        return value == null ? "" : value;
    }
}