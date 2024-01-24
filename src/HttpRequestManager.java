
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class HttpRequestManager {
    private final URL basePath = new URL("https://passwordium-api.onrender.com/api");
     public HttpRequestManager() throws MalformedURLException {
     }

     public int sendRegistrationRequest(String username, String password) throws IOException {
         URL url = new URL(basePath+"/Users/Register");
         HttpURLConnection connection = (HttpURLConnection) url.openConnection();
         connection.setRequestMethod("POST");
         connection.setDoOutput(true);
         connection.setRequestProperty("Content-Type", "application/json");
         String requestBody = "{\"username\":\""+username+"\",\"password\":\""+password+"\"}";
         try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
             outputStream.writeBytes(requestBody);
             outputStream.flush();
         }
         int responseCode = connection.getResponseCode();
         System.out.println("Response Code: " + responseCode);
         return responseCode;
     }

     public String sendLoginRequest(String username, String password) throws IOException{
         URL url = new URL(basePath+"/Users/Login");
         HttpURLConnection connection = (HttpURLConnection) url.openConnection();
         connection.setRequestMethod("POST");
         connection.setDoOutput(true);
         connection.setRequestProperty("Content-Type", "application/json");
         String requestBody = "{\"username\":\""+username+"\",\"password\":\""+password+"\"}";
         try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
             outputStream.writeBytes(requestBody);
             outputStream.flush();
         }
         int responseCode = connection.getResponseCode();
         System.out.println("Response Code: " + responseCode);
         if(responseCode==200){
             BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             String line;
             StringBuilder response = new StringBuilder();

             while ((line = reader.readLine()) != null) {
                 response.append(line);
             }
             reader.close();

             try {
                 JsonParser parser = new JsonParser();
                 JsonObject jsonObject = parser.parse(response.toString()).getAsJsonObject();
                 return jsonObject.get("jwt").getAsString();
             } catch (Exception e) {
                 e.printStackTrace();
             }
         }else{
             return null;
         }
         return null;
     }

    public List<Account> sendAccountsRequest(String jwt) throws IOException{
        URL url = new URL(basePath+"/Accounts");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + jwt);
        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);
        if(responseCode==200){
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            try {
                Gson gson = new Gson();
                Type personListType = new TypeToken<List<Account>>(){}.getType();

                return gson.fromJson(response.toString(), personListType);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            return null;
        }
        return null;
    }

    public void sendAccountsDeleteRequest(String jwt, int id) throws IOException{
        URL url = new URL(basePath+"/Accounts/" +id);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Authorization", "Bearer " + jwt);
        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);
        if(responseCode==200){
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
        }
    }

    public int sendAccountsPostRequest(String jwtToken, Racun racun) throws IOException {
        URL url = new URL(basePath+"/Accounts");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + jwtToken);
        String requestBody = "{" +
                "\"username\":\""+racun.KorIme +
                "\",\"password\":\""+racun.Lozinka +
                "\",\"url\":\""+racun.Link +
                "\",\"name\":\""+racun.Naziv+"\"}";
        try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
            outputStream.writeBytes(requestBody);
            outputStream.flush();
        }
        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);
        if(responseCode==200){
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
        }
        return responseCode;
    }
    public int sendAccountsPutRequest(String jwtToken, Racun racun) throws IOException {
        URL url = new URL(basePath+"/Accounts");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + jwtToken);
        String requestBody = "{" +
                "\"username\":\""+racun.KorIme +
                "\",\"password\":\""+racun.Lozinka +
                "\",\"url\":\""+racun.Link +
                "\",\"name\":\""+racun.Naziv+"\"}";
        try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
            outputStream.writeBytes(requestBody);
            outputStream.flush();
        }
        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);
        if(responseCode==200){
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
        }
        return responseCode;
    }
}
