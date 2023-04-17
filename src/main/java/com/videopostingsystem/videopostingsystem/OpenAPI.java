package com.videopostingsystem.videopostingsystem;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OpenAPI {

        public static String request(String content){
            Gson gson = new Gson();
            RequestBody requestBody = new RequestBody();
            requestBody.setMessages(new RequestBody.Messages[]{new RequestBody.Messages("user", content)});
            requestBody.setModel("gpt-3.5-turbo");
            requestBody.setTemperature(0.7);
            String json = gson.toJson(requestBody);

            try {
                HttpRequest httpRequest = HttpRequest.newBuilder()
                        .uri(new URI("https://api.openai.com/v1/chat/completions"))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + CONSTANTS.OPEN_API_KEY)
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                HttpClient client = HttpClient.newHttpClient();
                HttpResponse<String> postResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                String responseJson = postResponse.body();
                ResponseBody response = gson.fromJson(responseJson, ResponseBody.class);
                return response.getChoices()[0].getMessage().getContent();
            } catch (URISyntaxException | IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
