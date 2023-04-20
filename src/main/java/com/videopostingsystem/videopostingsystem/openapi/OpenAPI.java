package com.videopostingsystem.videopostingsystem.openapi;

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
            OpenAPIRequestBody openAPIRequestBody = new OpenAPIRequestBody();
            openAPIRequestBody.setMessages(new OpenAPIRequestBody.Messages[]{new OpenAPIRequestBody.Messages("user", content)});
            openAPIRequestBody.setModel("gpt-3.5-turbo");
            openAPIRequestBody.setTemperature(0.7);
            String json = gson.toJson(openAPIRequestBody);

            System.out.println(json);

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
                OpenAPIResponseBody response = gson.fromJson(responseJson, OpenAPIResponseBody.class);
                return response.getChoices()[0].getMessage().getContent();
            } catch (URISyntaxException | IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

