package com.maganini.portfolio.Apis.ApiUtilClasses;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Creds {
    private static final String credsFilename = "creds.txt";
    private static final String nasaApiKey; //nasa api
    private static final String cryptoApiKey; //coinmarketcap api
    private static final String rapidApiKey; //gas prices api,
    private static final String marketsApiKey; //alpha vantage api
    private static final String ebayAppToken;

    static {
        try {
            nasaApiKey = Creds.getCreds("nasaApi");
            rapidApiKey = Creds.getCreds("rapidApi");
            cryptoApiKey = Creds.getCreds("cryptoApi");
            marketsApiKey = Creds.getCreds("marketsApi");
            ebayAppToken = Creds.getCreds("ebayAppToken");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getNasaApiKey(){
        return nasaApiKey;
    }

    public static String getRapidApiKey(){
        return rapidApiKey;
    }
    public static String getCryptoApiKey(){
        return cryptoApiKey;
    }
    public static String getEbayAppToken(){
        return ebayAppToken;
    }

    public static String getCreds(String credType) throws IOException {
        Path parent = Paths.get("").toAbsolutePath().getParent();
        Path credsPath = Paths.get(parent.toString() + "/" + credsFilename);
        String credsLine = Files.lines(credsPath).filter(line -> line.contains(credType)).reduce((acc, e) -> acc + e).orElse("");
        return credsLine.substring(credsLine.indexOf(',') + 1);
    }
}
