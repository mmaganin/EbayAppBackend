package com.maganini.portfolio.Apis;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.maganini.portfolio.Apis.ApiUtilClasses.ApiUtil;
import com.maganini.portfolio.Apis.ApiUtilClasses.Creds;
import com.maganini.portfolio.Apis.ApiUtilClasses.GasPricesResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//only 10 calls/month!!!
public class GasPrices {
    public boolean success;
    public GasPricesResult result;

    public static GasPrices getGasPrices(String stateAbbrToCheck) throws IOException, InterruptedException {
        if(stateAbbrToCheck.length() != 2){
            return null;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://gas-price.p.rapidapi.com/stateUsaPrice?state=" + stateAbbrToCheck.toUpperCase()))
                .header("X-RapidAPI-Key", Creds.getRapidApiKey())
                .header("X-RapidAPI-Host", "gas-price.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        return (GasPrices) ApiUtil.mapStrResponseToObj(response.body(), GasPrices.class);
    }
}
