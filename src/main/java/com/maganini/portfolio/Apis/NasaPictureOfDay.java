package com.maganini.portfolio.Apis;

import com.maganini.portfolio.Apis.ApiUtilClasses.ApiUtil;
import com.maganini.portfolio.Apis.ApiUtilClasses.Creds;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NasaPictureOfDay {
    public String date;
    public String explanation;
    public String media_type;
    public String service_version;
    public String title;
    public String url;
    public String copyright;
    public String hdurl;

    public static NasaPictureOfDay getNasaPicture() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.nasa.gov/planetary/apod?api_key=" + Creds.getNasaApiKey()))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        return (NasaPictureOfDay) ApiUtil.mapStrResponseToObj(response.body(), NasaPictureOfDay.class);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println(getNasaPicture());
    }
}