package com.maganini.portfolio.Apis;

import com.ebay.api.client.auth.oauth2.CredentialUtil;
import com.ebay.api.client.auth.oauth2.OAuth2Api;
import com.ebay.api.client.auth.oauth2.model.AccessToken;
import com.ebay.api.client.auth.oauth2.model.Environment;
import com.ebay.api.client.auth.oauth2.model.OAuthResponse;
import com.ebay.api.client.auth.oauth2.model.RefreshToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.maganini.portfolio.Apis.ApiUtilClasses.*;
import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Data
public class Ebay {
    private static String accessToken = "";
    private static String refreshToken = "";
    public static final ArrayList<EbayItemSummary> checkedListings = new ArrayList<>();

    //fields for mapping JSON response to POJO
    public AutoCorrections autoCorrections;
    public String href;
    public ArrayList<EbayItemSummary> itemSummaries;
    public String limit;
    public String next;
    public String offset;
    public String prev;
    public Refinement refinement;
    public String total;
    public ArrayList<Warning> warnings;

    //can make 1.4 calls every 25 seconds to browse api if running 24 hours/day (5000 calls/day for browse api)
    //sort options: distance, -price, newlyListed, endingSoonest
    //condition options: NEW, USED, UNSPECIFIED
    public static String browseEbayListings(EbayReqBody ebayReqBody) throws IOException, InterruptedException {
        if (accessToken.equals("")) {
            getAuthToken();
        }

        String uri = "https://api.ebay.com/buy/browse/v1/item_summary/search?" +
                "q=" + URLEncoder.encode(ebayReqBody.keyword, Charset.defaultCharset()) +
                "&limit=" + ebayReqBody.numberOfResults +
                "&sort=" + ebayReqBody.sortType +
                "&filter=" + URLEncoder.encode("price:[" + ebayReqBody.lowPrice + ".." + ebayReqBody.highPrice + "],priceCurrency:USD,conditions:{" + ebayReqBody.condition + "}", Charset.defaultCharset());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
//                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", "Bearer " + accessToken)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    public static void getAuthToken() throws IOException {
        OAuth2Api oauth2Api = new OAuth2Api();
        CredentialUtil.load(new FileInputStream("../ebay-config.yaml"));
//        System.out.println(CredentialUtil.getCredentials(Environment.PRODUCTION).toString());
        OAuthResponse oAuthResponse = oauth2Api.getApplicationToken(Environment.PRODUCTION, List.of("https://api.ebay.com/oauth/api_scope"));
        refreshToken = oAuthResponse.getRefreshToken().orElse(new RefreshToken()).getToken();
        accessToken = oAuthResponse.getAccessToken().orElse(new AccessToken()).getToken();
//        System.out.println("refresh token: " + refreshToken);
//        System.out.println("access token: " + accessToken);
    }

    public static Map<String, Object> getEbay(EbayReqBody ebayReqBody, JavaMailSender javaMailSender) throws IOException, InterruptedException {
        String ebayResponse = Ebay.browseEbayListings(ebayReqBody);
        //Checks if valid access token
        Ebay ebayObj = null;
        try {
            ebayObj = (Ebay) ApiUtil.mapStrResponseToObj(ebayResponse, Ebay.class);
        } catch (Exception e) {
            Ebay.setAccessToken("");
            System.out.println("bad access token?");
            e.printStackTrace();
        }

        //checks if valid response is being received after access token validated
        if(ebayObj == null){
            ebayResponse = Ebay.browseEbayListings(ebayReqBody);
            try {
                ebayObj = (Ebay) ApiUtil.mapStrResponseToObj(ebayResponse, Ebay.class);
            } catch (Exception e) {
                //SENDs EMAIL: if something is wrong with JSON to POJO mapping or API call
                sendEmail(javaMailSender, "michaelmags33@gmail.com", "!!EBAY!! JSON to POJO failure OR API call failure", e.getMessage());
                System.out.println("JSON to POJO failure OR API call failure");
                e.printStackTrace();
                return ApiUtil.mapStrResponseToMap(ebayResponse);
            }
        }
        //checks if there are any new listings and adds to list
        ArrayList<EbayItemSummary> newItems = new ArrayList<>();
        for(EbayItemSummary ebayItem : ebayObj.itemSummaries){
            if(!Ebay.checkedListings.contains(ebayItem)){
                newItems.add(ebayItem);
                Ebay.checkedListings.add(ebayItem);
                System.out.println();
                System.out.println("!!!NEW ITEM BELOW!!!");
                System.out.println("Title: " + ebayItem.title + ", Price: " + ebayItem.price + ", Condition: " + ebayItem.condition + ", Listing Creation Date: " + ebayItem.itemCreationDate + ", URL: " + ebayItem.itemWebUrl);
            }
        }

        //SENDS EMAIL: if new items list is not empty
        if(!newItems.isEmpty()){
            String body = "";
            for(EbayItemSummary ebayItem : newItems){
                body += "Title: " + ebayItem.title +
                        ", \nCondition: " + ebayItem.condition +
                        ", \nListing Creation Date: " + ebayItem.itemCreationDate +
                        ", \nPrice: " + ebayItem.price +
                        "\n" + ebayItem.itemWebUrl + "\n\n";
            }
            sendEmail(javaMailSender, "michaelmags33@gmail.com", "!!EBAY!! New Potential Buy(s)", body);
        }

        return ApiUtil.mapStrResponseToMap(ebayResponse);
    }

    public static Map<String, Object> initEbay(EbayReqBody ebayReqBody) throws IOException, InterruptedException {
        String ebayResponse = Ebay.browseEbayListings(ebayReqBody);

        Ebay ebayObj;
        try {
            ebayObj = (Ebay) ApiUtil.mapStrResponseToObj(ebayResponse, Ebay.class);
        } catch (Exception e) {
            System.out.println("Ebay Init API Call failure");
            e.printStackTrace();
            return ApiUtil.mapStrResponseToMap(ebayResponse);
        }

        for(EbayItemSummary ebayItem : ebayObj.itemSummaries){
            Ebay.checkedListings.add(ebayItem);
            System.out.println("Title: " + ebayItem.title + ", Price: " + ebayItem.price + ", Condition: " + ebayItem.condition + ", Listing Creation Date: " + ebayItem.itemCreationDate + ", URL: " + ebayItem.itemWebUrl);
        }

        return ApiUtil.mapStrResponseToMap(ebayResponse);
    }

    public static void sendEmail(JavaMailSender javaMailSender, String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("michaelmags33@gmail.com");
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        javaMailSender.send(message);
        System.out.println("Mail Sent Successfully");
    }

    public static void setAccessToken(String accessToken) {
        Ebay.accessToken = accessToken;
    }

    public static void setRefreshToken(String refreshToken) {
        Ebay.refreshToken = refreshToken;
    }

//    public static void main(String[] args) throws IOException, InterruptedException {
//        JavaMailSender javaMailSender = new JavaMailSenderImpl();
////        sendEmail(javaMailSender, "michaelmags33@gmail.com", "test email subject","test email body");
//
//        EbayReqBody ebayReqBody = new EbayReqBody("rtx 3090", "300", "1000", "50", "NEW", "newlyListed");
//
//        System.out.println(initEbay(ebayReqBody));
//        System.out.println();
//        System.out.println("END OF INIT API CALL");
//        System.out.println();
//        while(true){
//            TimeUnit.SECONDS.sleep(25);
//            System.out.println(getEbay(ebayReqBody, javaMailSender));
//            System.out.println();
//            System.out.println("END OF API CALL");
//            System.out.println();
//        }
//    }
}

class Refinement{
    public ArrayList<AspectDistribution> aspectDistributions;
    public ArrayList<BuyingOptionDistribution> buyingOptionDistributions;
    public ArrayList<CategoryDistribution> categoryDistributions;
    public ArrayList<ConditionDistribution> conditionDistributions;
    public String dominantCategoryId;
}

class Warning{
    public String category;
    public String domain;
    public String errorId;
    public ArrayList<String> inputRefIds;
    public String longMessage;
    public String message;
    public ArrayList<String> outputRefIds;
    public ArrayList<Parameter> parameters;
    public String subdomain;
}

class Parameter{
    public String name;
    public String value;
}

class ConditionDistribution{
    public String condition;
    public String conditionId;
    public String matchCount;
    public String refinementHref;
}

class CategoryDistribution{
    public String categoryId;
    public String categoryName;
    public String matchCount;
    public String refinementHref;
}

class BuyingOptionDistribution{
    public String buyingOption;
    public String matchCount;
    public String refinementHref;
}

class AutoCorrections{
    public String q;
}

class AspectDistribution{
    public ArrayList<AspectValueDistribution> aspectValueDistributions;
    public String localizedAspectName;
}

class AspectValueDistribution{
    public String localizedAspectValue;
    public String matchCount;
    public String refinementHref;
}