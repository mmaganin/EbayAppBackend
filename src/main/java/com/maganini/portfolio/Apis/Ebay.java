package com.maganini.portfolio.Apis;

import com.ebay.api.client.auth.oauth2.CredentialUtil;
import com.ebay.api.client.auth.oauth2.OAuth2Api;
import com.ebay.api.client.auth.oauth2.model.AccessToken;
import com.ebay.api.client.auth.oauth2.model.Environment;
import com.ebay.api.client.auth.oauth2.model.OAuthResponse;
import com.ebay.api.client.auth.oauth2.model.RefreshToken;
import com.maganini.portfolio.Apis.ApiUtilClasses.*;
import lombok.Data;

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

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Data
public class Ebay {
    private static String accessToken = "";
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

    //can make 1.15 calls every 20 seconds to browse api if running 24 hours/day (5000 calls/day for browse api)
    //sort options: distance, -price, newlyListed, endingSoonest
    //condition options: NEW, USED, UNSPECIFIED
    public static String browseEbayListings(EbayReqBody ebayReqBody, String credsPath) throws IOException, InterruptedException {
        if (accessToken.equals("")) {
            getAuthToken(credsPath);
        }

        String uri = "https://api.ebay.com/buy/browse/v1/item_summary/search?" +
                "q=" + URLEncoder.encode(ebayReqBody.keyword, Charset.defaultCharset()) +
                "&limit=" + ebayReqBody.numberOfResults +
                "&sort=" + ebayReqBody.sortType +
                "&filter=" + URLEncoder.encode("price:[" + ebayReqBody.lowPrice + ".." + ebayReqBody.highPrice + "],priceCurrency:USD,conditions:{" + ebayReqBody.condition + "}", Charset.defaultCharset());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Authorization", "Bearer " + accessToken)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    public static void getAuthToken(String credsPath) throws IOException {
        OAuth2Api oauth2Api = new OAuth2Api();
        CredentialUtil.load(new FileInputStream(credsPath));
        OAuthResponse oAuthResponse = oauth2Api.getApplicationToken(Environment.PRODUCTION, List.of("https://api.ebay.com/oauth/api_scope"));
        accessToken = oAuthResponse.getAccessToken().orElse(new AccessToken()).getToken();
    }

    public static Map<String, Object> getEbay(EbayReqBody ebayReqBody, JavaMailSender javaMailSender) throws IOException, InterruptedException {
        String ebayResponse = Ebay.browseEbayListings(ebayReqBody, ebayReqBody.credsPath);
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
        if (ebayObj == null) {
            ebayResponse = Ebay.browseEbayListings(ebayReqBody, ebayReqBody.credsPath);
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
        boolean isActuallyNewItems = true;
        if(checkedListings.isEmpty()){
            isActuallyNewItems = false;
        }
        ArrayList<EbayItemSummary> newItems = new ArrayList<>();
        for (EbayItemSummary ebayItem : ebayObj.itemSummaries) {
//            if (!isListingChecked(ebayItem)) {
            if (!checkedListings.contains(ebayItem)) {
                newItems.add(ebayItem);
                Ebay.checkedListings.add(ebayItem);
                if(isActuallyNewItems){
                    System.out.println();
                    System.out.println("!!!NEW ITEM BELOW!!!");
                    System.out.println("Title: " + ebayItem.title + ", Price: " + ebayItem.price + ", Condition: " + ebayItem.condition + ", Listing Creation Date: " + ebayItem.itemCreationDate + ", URL: " + ebayItem.itemWebUrl);
                } else {
                    System.out.println("Item being reloaded into memory...");
                }
            }
        }

        //SENDS EMAIL: if new items list is not empty
        if (!newItems.isEmpty() && isActuallyNewItems) {
            String body = "";
            for (EbayItemSummary ebayItem : newItems) {
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
        String ebayResponse = Ebay.browseEbayListings(ebayReqBody, ebayReqBody.credsPath);

        Ebay ebayObj;
        try {
            ebayObj = (Ebay) ApiUtil.mapStrResponseToObj(ebayResponse, Ebay.class);
        } catch (Exception e) {
            System.out.println("Ebay Init API Call failure");
            e.printStackTrace();
            return ApiUtil.mapStrResponseToMap(ebayResponse);
        }

        for (EbayItemSummary ebayItem : ebayObj.itemSummaries) {
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

    //    public static boolean isListingChecked(EbayItemSummary ebayItemToCheck) {
//        for (EbayItemSummary ebayItem : Ebay.checkedListings) {
//            if (isStringsEqual(ebayItemToCheck.epid, ebayItem.epid)) {
//                if (isStringsEqual(ebayItemToCheck.itemId, ebayItem.itemId)) {
//                    if (isStringsEqual(ebayItemToCheck.legacyItemId, ebayItem.legacyItemId)) {
//                        return true;
//                    }
//                }
//            }
//        }
//
//        return false;
//    }
//
//    public static boolean isStringsEqual(String str1, String str2) {
//        return (str1 == null && str2 == null)
//                || ((str1 != null && str2 != null)
//                && str1.equals(str2));
//    }

    public static void setAccessToken(String accessToken) {
        Ebay.accessToken = accessToken;
    }
}

class Refinement {
    public ArrayList<AspectDistribution> aspectDistributions;
    public ArrayList<BuyingOptionDistribution> buyingOptionDistributions;
    public ArrayList<CategoryDistribution> categoryDistributions;
    public ArrayList<ConditionDistribution> conditionDistributions;
    public String dominantCategoryId;
}

class Warning {
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

class Parameter {
    public String name;
    public String value;
}

class ConditionDistribution {
    public String condition;
    public String conditionId;
    public String matchCount;
    public String refinementHref;
}

class CategoryDistribution {
    public String categoryId;
    public String categoryName;
    public String matchCount;
    public String refinementHref;
}

class BuyingOptionDistribution {
    public String buyingOption;
    public String matchCount;
    public String refinementHref;
}

class AutoCorrections {
    public String q;
}

class AspectDistribution {
    public ArrayList<AspectValueDistribution> aspectValueDistributions;
    public String localizedAspectName;
}

class AspectValueDistribution {
    public String localizedAspectValue;
    public String matchCount;
    public String refinementHref;
}