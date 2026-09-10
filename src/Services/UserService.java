package Services;

import Requests.LoginRequest;
import Responses.LoginResponse;
import com.google.gson.Gson;

import javax.security.auth.login.FailedLoginException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class UserService {
    private static final String API_URL =
            "https://passwordium-api-7jz2.onrender.com/api/Users/Login";

    private final HttpClient httpClient;
    private final Gson gson;

    public UserService() {
        httpClient = HttpClient.newHttpClient();
        gson = new Gson();
    }

    public LoginResponse login(String korisnickoIme, String lozinka)
            throws Exception {

        String json = gson.toJson(
                new LoginRequest(korisnickoIme, lozinka)
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response =
                httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );
        System.out.println(response);
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
}
