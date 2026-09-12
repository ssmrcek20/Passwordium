package Services;

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
}