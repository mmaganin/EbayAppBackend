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
import java.time.Instant;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Data
public class Ebay {
//    private static String accessToken = "";
//    public static final ArrayList<EbayItemSummary> checkedListings = new ArrayList<>();

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
//    public static String browseEbayListings(EbayReqBody ebayReqBody, String credsPath) throws IOException, InterruptedException {
    public static String browseEbayListings(EbayReqBody ebayReqBody, int option) throws IOException, InterruptedException {
        String accessToken = getAccessToken(option);

        if (accessToken.equals("")) {
            getAuthToken(ebayReqBody.credsPath, option);
            accessToken = getAccessToken(option);
        }
        String uri = "https://api.ebay.com/buy/browse/v1/item_summary/search?" +
                "q=" + URLEncoder.encode(ebayReqBody.keyword, Charset.defaultCharset()) +
                "&limit=" + ebayReqBody.numberOfResults +
                "&sort=" + ebayReqBody.sortType +
                "&filter=" + URLEncoder.encode("deliveryCountry:US,buyingOptions:{FIXED_PRICE|BEST_OFFER},conditions:{" + ebayReqBody.condition + "}", Charset.defaultCharset());
//                "&filter=" + URLEncoder.encode("price:[" + ebayReqBody.lowPrice + ".." + ebayReqBody.highPrice + "],priceCurrency:USD,deliveryCountry:US,conditions:{" + ebayReqBody.condition + "}", Charset.defaultCharset());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Authorization", "Bearer " + accessToken)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    public static void getAuthToken(String credsPath, int option) throws IOException {
        OAuth2Api oauth2Api = new OAuth2Api();
        CredentialUtil.load(new FileInputStream(credsPath));
        OAuthResponse oAuthResponse = oauth2Api.getApplicationToken(Environment.PRODUCTION, List.of("https://api.ebay.com/oauth/api_scope"));

        if(option == 1){
            EbayCreds.accessToken1 = oAuthResponse.getAccessToken().orElse(new AccessToken()).getToken();
        } else if(option == 2){
            EbayCreds.accessToken2 = oAuthResponse.getAccessToken().orElse(new AccessToken()).getToken();
        }
    }

    public static Map<String, Object> getEbay(EbayReqBody ebayReqBody, JavaMailSender javaMailSender, int option) throws IOException, InterruptedException {
        ArrayList<EbayItemSummary> checkedListings = getCheckedListings(option);

        String ebayResponse = Ebay.browseEbayListings(ebayReqBody, option);

        Ebay ebayObj = validateApiCall(ebayResponse, javaMailSender, ebayReqBody, option);
        if (ebayObj == null) {
            return ApiUtil.mapStrResponseToMap(ebayResponse);
        }

        //checks if there are any new listings and adds to list, these are emailed
        ArrayList<EbayItemSummary> newItems = new ArrayList<>();
        for (EbayItemSummary ebayItem : ebayObj.itemSummaries) {
            if (!isListingChecked(ebayItem, option)) {
                double lowPrice = Double.parseDouble(ebayReqBody.lowPrice);
                double highPrice = Double.parseDouble(ebayReqBody.highPrice);
                //handles if Price field is null, adds to newItems if so
                double itemValue = ebayItem.price == null || ebayItem.price.value == null || ebayItem.price.value.equals("")
                        ? lowPrice : Double.parseDouble(ebayItem.price.value);

                //adds to new items to email if: listing occurred today + if in provided price range
                if ((ebayItem.itemCreationDate == null || ebayItem.itemCreationDate.toString().equals(Date.valueOf(LocalDate.now()).toString()))
                        && (itemValue >= lowPrice && itemValue <= highPrice)) newItems.add(ebayItem);

                checkedListings.add(ebayItem);
                System.out.println();
                System.out.println("!!!ITEM NOT YET CHECKED BELOW!!!");
                System.out.println("Title: " + ebayItem.title + ", Price: " + ebayItem.price + ", Condition: " + ebayItem.condition + ", Listing Creation Date: " + ebayItem.itemCreationDate + ", URL: " + ebayItem.itemWebUrl);
            }
        }

        //SENDS EMAIL: if new items list is not empty
        if (!newItems.isEmpty()) {
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

    public static Map<String, Object> initEbay(EbayReqBody ebayReqBody, int option) throws IOException, InterruptedException {
        ArrayList<EbayItemSummary> checkedListings = getCheckedListings(option);

        String ebayResponse = Ebay.browseEbayListings(ebayReqBody, option);

        Ebay ebayObj;
        try {
            ebayObj = (Ebay) ApiUtil.mapStrResponseToObj(ebayResponse, Ebay.class);
        } catch (Exception e) {
            System.out.println("Ebay Init API Call failure");
            e.printStackTrace();
            return ApiUtil.mapStrResponseToMap(ebayResponse);
        }

        for (EbayItemSummary ebayItem : ebayObj.itemSummaries) {
            checkedListings.add(ebayItem);
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

    public static Ebay validateApiCall(String ebayResponse, JavaMailSender javaMailSender, EbayReqBody ebayReqBody, int option) throws IOException, InterruptedException {
        //Checks if valid access token
        Ebay ebayObj = null;
        try {
            ebayObj = (Ebay) ApiUtil.mapStrResponseToObj(ebayResponse, Ebay.class);
        } catch (Exception e) {
            Ebay.setAccessToken("", option);
            System.out.println("bad access token?");
            e.printStackTrace();
        }

        //checks if valid response is being received after access token validated
        if (ebayObj == null) {
            ebayResponse = Ebay.browseEbayListings(ebayReqBody, option);
            try {
                ebayObj = (Ebay) ApiUtil.mapStrResponseToObj(ebayResponse, Ebay.class);
            } catch (Exception e) {
                //SENDs EMAIL: if something is wrong with JSON to POJO mapping or API call
                sendEmail(javaMailSender, "michaelmags33@gmail.com", "!!EBAY!! JSON to POJO failure OR API call failure", e.getMessage());
                System.out.println("JSON to POJO failure OR API call failure");
                e.printStackTrace();
                return null;
            }
        }

        return ebayObj;
    }

    public static boolean isListingChecked(EbayItemSummary ebayItemToCheck, int option) {
        ArrayList<EbayItemSummary> checkedListings = getCheckedListings(option);

        for (EbayItemSummary ebayItem : checkedListings) {
            if (isStringsEqual(ebayItemToCheck.epid, ebayItem.epid)
                    && isStringsEqual(ebayItemToCheck.itemId, ebayItem.itemId)
                    && isStringsEqual(ebayItemToCheck.legacyItemId, ebayItem.legacyItemId)
                    && isStringsEqual(ebayItemToCheck.title, ebayItem.title)
                    && isStringsEqual(ebayItemToCheck.condition, ebayItem.condition)
                    && isStringsEqual(ebayItemToCheck.itemWebUrl, ebayItem.itemWebUrl)
            ) {
                return true;
            }
        }

        return false;
    }

    public static boolean isStringsEqual(String str1, String str2) {
        return (str1 == null && str2 == null)
                || ((str1 != null && str2 != null)
                && str1.equals(str2));
    }

    public static void ebayUtil(EbayReqBody ebayReqBody, JavaMailSender javaMailSender, String logMessage, int secsBetweenCalls, int option){
        String emailBody = "defaultBody";
        String initCallName = "END OF INIT API CALL FOR " + logMessage;
        String callName = "END OF API CALL FOR " + logMessage + " #";

        try {
            int i = 1;
            System.out.println(Ebay.initEbay(ebayReqBody, option));
            System.out.println();
            System.out.println(initCallName);
            System.out.println();
            while (true) {
                TimeUnit.SECONDS.sleep(secsBetweenCalls);
                System.out.println(Ebay.getEbay(ebayReqBody, javaMailSender, option));
                System.out.println(callName + i);
                System.out.println();
                i++;
            }
        } catch (Exception e){
            emailBody = e.getMessage();
        }

        System.out.println("EXECUTION FINISHED");
        Ebay.sendEmail(javaMailSender, "michaelmags33@gmail.com", "Ebay Execution stopped", "\"" + ebayReqBody.credsPath + "\" path stopped: " + emailBody);
    }

    public static ArrayList<EbayItemSummary> getCheckedListings(int option){
        if(option == 1){
            return EbayCreds.checkedListings1;
        } else {
            return EbayCreds.checkedListings2;
        }
    }

    public static String getAccessToken(int option){
        if(option == 1){
            return EbayCreds.accessToken1;
        } else {
            return EbayCreds.accessToken2;
        }
    }

    public static void setAccessToken(String accessToken, int option) {
        if(option == 1){
            EbayCreds.accessToken1 = accessToken;
        } else {
            EbayCreds.accessToken2 = accessToken;
        }
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