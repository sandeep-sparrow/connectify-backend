package com.videopostingsystem.videopostingsystem.videos;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.Callable;

public class AssemblyAI implements Callable<String> {
    public static final String API_KEY = CONSTANTS.API_KEY;
    private String link;

    public AssemblyAI(String link){
        this.link = link;
    }

    @Override
    public String call() {
        String url = "https://api.assemblyai.com/v2";
        Gson gson = new Gson();
        Transcript transcript = new Transcript();
        String summary = "";

        try {
            transcript.setAudio_url(link);
            transcript.setSummarization(true);
            String json = gson.toJson(transcript);

            HttpRequest postRequestSummarization = HttpRequest.newBuilder()
                    .uri(new URI(url + "/transcript"))
                    .header("Authorization", API_KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpClient getClient = HttpClient.newHttpClient();

            HttpResponse<String> getResponse = getClient.send(postRequestSummarization, HttpResponse.BodyHandlers.ofString());

            transcript = gson.fromJson(getResponse.body(), Transcript.class);

            HttpRequest idGetRequest = HttpRequest.newBuilder()
                    .uri(new URI(url + "/transcript/" + transcript.getId()))
                    .header("Authorization", API_KEY)
                    .build();


            while (true){

                HttpResponse<String> idGetResponse = getClient.send(idGetRequest, HttpResponse.BodyHandlers.ofString());
                transcript = gson.fromJson(idGetResponse.body(), Transcript.class);
                if (transcript.getStatus().equals("completed") || transcript.getStatus().equals("error")){
                    idGetResponse = getClient.send(idGetRequest, HttpResponse.BodyHandlers.ofString());
                    transcript = gson.fromJson(idGetResponse.body(), Transcript.class);
                    summary = transcript.getSummary();
                    break;
                }
                Thread.sleep(5000);

            }
        } catch (IOException | InterruptedException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return summary;
    }
}
