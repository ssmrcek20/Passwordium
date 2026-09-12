package Services;

import Objects.EncryptedVaultKey;
import Requests.AccountRequest;
import Responses.AccountResponse;
import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AccountService {

    private static final String API_URL =
            "https://passwordium-api-7jz2.onrender.com/api/Accounts";

    private final HttpClient httpClient =
            HttpClient.newHttpClient();

    private final Gson gson =
            new Gson();

    public AccountResponse[] getAccounts()
            throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(API_URL))
                        .header("Authorization", "Bearer " + VaultSession.getJwt())
                        .GET()
                        .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Dohvat računa nije uspio: " + response.statusCode());
        }

        return gson.fromJson(response.body(), AccountResponse[].class);
    }

    public void deleteAccount(int id) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/" + id))
                .header("Authorization", "Bearer " + VaultSession.getJwt())
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Brisanje računa nije uspjelo. Status: " + response.statusCode() + " " + response.body());
        }
    }

    public void updateAccount(int id, EncryptedVaultKey encryptedData) throws Exception {

        AccountRequest requestBody = new AccountRequest(
                id,
                encryptedData.getEncryptedVaultKey(),
                encryptedData.getNonce(),
                encryptedData.getTag()
        );

        String json = gson.toJson(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(API_URL))
                        .header("Authorization", "Bearer " + VaultSession.getJwt())
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(json))
                        .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Uređivanje računa nije uspjelo. Status: " + response.statusCode());
        }
    }

    public void addAccount(EncryptedVaultKey encryptedData) throws Exception {
        AccountRequest requestBody = new AccountRequest(0, encryptedData.getEncryptedVaultKey(),
                encryptedData.getNonce(), encryptedData.getTag());

        String json = gson.toJson(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(API_URL))
                        .header("Authorization", "Bearer " + VaultSession.getJwt())
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new Exception(
                    "Dodavanje računa nije uspjelo. Status: " + response.statusCode() + " " + response.body());
        }
    }
}