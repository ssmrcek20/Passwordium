package Services;

import Requests.LoginRequest;
import Requests.RegisterRequest;
import Responses.LoginResponse;
import com.google.gson.Gson;

import javax.security.auth.login.FailedLoginException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class UserService {
    private static final String API_URL =
            "https://passwordium-api-7jz2.onrender.com/api/Users/";

    private final HttpClient httpClient;
    private final Gson gson;

    public UserService() {
        httpClient = HttpClient.newHttpClient();
        gson = new Gson();
    }

    public LoginResponse login(String korisnickoIme, String lozinka) throws Exception {

        String json = gson.toJson(
                new LoginRequest(korisnickoIme, lozinka)
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL+"Login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response =
                httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );
        if (response.statusCode() == 401) {
            throw new FailedLoginException();
        }

        if (response.statusCode() != 200) {
            throw new RuntimeException(
                    "Greška API-ja: " + response.statusCode()
            );
        }

        return gson.fromJson(
                response.body(),
                LoginResponse.class
        );
    }

    public void register(String username, String password, String vaultSalt,
                         String encryptedVaultKey, String vaultKeyNonce, String vaultKeyTag) throws Exception {

        RegisterRequest registerRequest = new RegisterRequest(username, password, vaultSalt,
                                                            encryptedVaultKey, vaultKeyNonce, vaultKeyTag);

        String json = gson.toJson(registerRequest);

        HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(API_URL + "Register"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new Exception("Registracija nije uspjela. " + response.body()
            );
        }
    }

    public void logout() throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(API_URL + "/Logout"))
                        .header("Authorization", "Bearer " + VaultSession.getJwt())
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {

            throw new Exception(
                    "Logout nije uspio. Status: "
                            + response.statusCode()
            );
        }
    }
}
