package api_interaction;

import com.google.gson.Gson;
import com.videopostingsystem.videopostingsystem.users.AuthenticateModel;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class Client {

    private final String url = "http://localhost:8080/";
    private String username;
    private String password;

    public Client(String username, String password){
        this.username = username;
        this.password = password;
    }

    public String signUp() {
        try {
            Gson gson = new Gson();
            AuthenticateModel model = new AuthenticateModel(username, password);
            String json = gson.toJson(model);
            HttpRequest signUpRequest = HttpRequest.newBuilder()
                    .uri(new URI(url + "sign-up"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse response = client.send(signUpRequest, HttpResponse.BodyHandlers.ofString());

            System.out.println(response.body());
            // Check if the response is successful and get the session ID from the response headers
            if (response.statusCode() == 200) {
                List<String> cookies = response.headers().map().get("Set-Cookie");
                if (cookies != null) {
                    String sessionId = getSessionIdFromCookie(cookies.get(0));
                    return sessionId;
                }
            }

            if (response.statusCode() == 200) {
                List<String> cookies = response.headers().map().get("Set-Cookie");
                if (cookies != null) {
                    String sessionId = getSessionIdFromCookie(cookies.get(0));
                    return sessionId;
                }
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String login() {
        try {
            Gson gson = new Gson();
            AuthenticateModel model = new AuthenticateModel(username, password);
            String json = gson.toJson(model);
            HttpRequest loginRequest = HttpRequest.newBuilder()
                    .uri(new URI(url + "login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse response = client.send(loginRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            // Check if the response is successful and get the session ID from the response headers
            if (response.statusCode() == 200) {
                List<String> cookies = response.headers().map().get("Set-Cookie");
                if (cookies != null) {
                    String sessionId = getSessionIdFromCookie(cookies.get(0));
                    return sessionId;
                }
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getSessionIdFromCookie(String cookie) {
        String[] cookieParts = cookie.split(";");
        String sessionId = null;
        for (String part : cookieParts) {
            if (part.startsWith("JSESSIONID=")) {
                sessionId = part.substring("JSESSIONID=".length());
            }
        }
        return sessionId;
    }

    class Validated{
        String sessionId;

        public Validated(String sessionId){
            this.sessionId = sessionId;
        }
        public String getAllPosts() {
            try {
                HttpRequest getPostRequest = HttpRequest.newBuilder()
                        .uri(new URI(url + "posts"))
                        .header("Content-Type", "application/json")
                        .header("Cookie", "JSESSIONID=" + sessionId)
                        .GET()
                        .build();

                HttpClient client = HttpClient.newHttpClient();
                HttpResponse response = client.send(getPostRequest, HttpResponse.BodyHandlers.ofString());

                return (String) response.body();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}