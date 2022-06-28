package com.maganini.portfolio.Apis;

import com.maganini.portfolio.Apis.ApiUtilClasses.ApiUtil;
import com.maganini.portfolio.Apis.ApiUtilClasses.Creds;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

//alpha vantage api
public class Markets {
    public static Map<String,Object> getMarkets(String marketAbbr) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=" + marketAbbr.toUpperCase() + "&interval=60min&apikey=" + Creds.getNasaApiKey()))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        return ApiUtil.mapStrResponseToMap(response.body());
    }
}
