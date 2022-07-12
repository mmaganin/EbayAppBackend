package com.maganini.ebayapp.Api;

import com.ebay.api.client.auth.oauth2.CredentialUtil;
import com.ebay.api.client.auth.oauth2.OAuth2Api;
import com.ebay.api.client.auth.oauth2.model.AccessToken;
import com.ebay.api.client.auth.oauth2.model.Environment;
import com.ebay.api.client.auth.oauth2.model.OAuthResponse;
import com.maganini.ebayapp.Api.ApiUtilClasses.*;
import lombok.Data;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
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
    public static final String credsPath = "C:\\Users\\micha\\OneDrive\\Desktop\\javaPortfolioProj\\ebay-config.yaml";

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
    public static String browseEbayListings(EbayReqBody ebayReqBody, String credsPath, int option) throws IOException, InterruptedException {
        String accessToken = getAccessToken(option);

        if (accessToken.equals("")) {
            getAuthToken(credsPath, option);
            accessToken = getAccessToken(option);
        }
        String numResults = ebayReqBody.numberOfResults;
        try {
            Integer.parseInt(ebayReqBody.numberOfResults);
        } catch (Exception e) {
            numResults = "50";
        }

        String uri = "https://api.ebay.com/buy/browse/v1/item_summary/search?" +
                "q=" + URLEncoder.encode(ebayReqBody.keyword, Charset.defaultCharset()) +
                "&limit=" + numResults +
                "&filter=" + URLEncoder.encode("deliveryCountry:US,buyingOptions:{" + ebayReqBody.buyingOptions + "},conditions:{" + ebayReqBody.conditions + "}", Charset.defaultCharset());

        //input nothing for searching for Best Match
        if (ebayReqBody.sortType != null && !ebayReqBody.sortType.equals("")) {
            uri += "&sort=" + ebayReqBody.sortType;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Authorization", "Bearer " + accessToken)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    public static Map<String, Object> getEbay(EbayReqBody ebayReqBody, String credsPath, JavaMailSender javaMailSender, int secsBetweenCalls, int option) throws IOException, InterruptedException {
        ArrayList<EbayItemSummary> checkedListings = getCheckedListings(secsBetweenCalls, option);

        String ebayResponse = Ebay.browseEbayListings(ebayReqBody, credsPath, option);
        Ebay ebayObj = validateApiCall(ebayResponse, javaMailSender, ebayReqBody, credsPath, option);
        if (ebayObj == null) {
            return ApiUtil.mapStrResponseToMap(ebayResponse);
        }

        //checks if there are any new listings and adds to list, these are emailed
        ArrayList<EbayItemSummary> newItems = new ArrayList<>();
        for (EbayItemSummary ebayItem : ebayObj.itemSummaries) {
            if (!Ebay.isListingChecked(ebayItem, secsBetweenCalls, option)) {
                //adds to new items to email if: listing occurred today + if in provided price range
                if (Ebay.checkCanAddItem(ebayReqBody, ebayItem)) newItems.add(ebayItem);

                checkedListings.add(ebayItem);
                System.out.println();
                System.out.println("!!!ITEM NOT YET CHECKED BELOW!!!");
                System.out.println("Title: " + ebayItem.title + ", Price: " +
                        (ebayItem.price != null
                                ? ("$" + ebayItem.price.value)
                                : "NULL")
                        + ", Condition: " + ebayItem.condition + ", Listing Creation Date: " + ebayItem.itemCreationDate + ", URL: " + ebayItem.itemWebUrl);
            }
        }

        //SENDS EMAIL: if new items list is not empty
        if (!newItems.isEmpty()) {
            String body = "";
            for (EbayItemSummary ebayItem : newItems) {
                body += "Title: " + ebayItem.title +
                        ", \nCondition: " + ebayItem.condition +
                        ", \nListing Creation Date: " + ebayItem.itemCreationDate +
                        ", \nPrice: " + (ebayItem.price != null
                        ? ("$" + ebayItem.price.value)
                        : "No price data (Best Offer or Auction item)") +
                        ", \nExpected Selling Price: " + ebayReqBody.expectedSellPrice +
                        "\n" + ebayItem.itemWebUrl + "\n\n";
            }
            sendEmail(javaMailSender, "michaelmags33@gmail.com", "!!EBAY!! New Potential Buy(s)", body);
        }

        return ApiUtil.mapStrResponseToMap(ebayResponse);
    }

    public static boolean checkCanAddItem(EbayReqBody ebayReqBody, EbayItemSummary ebayItem) {
        double lowPrice;
        double highPrice;
        double itemValue;
        boolean canAddItem = true;

        //parses listing item's USD value, and high and low USD price search filter values
        try {
            lowPrice = Double.parseDouble(ebayReqBody.lowPrice);
        } catch (Exception e) {
            lowPrice = 0.0;
        }
        try {
            highPrice = Double.parseDouble(ebayReqBody.highPrice);
        } catch (Exception e) {
            highPrice = 0.0;
        }
        try {
            itemValue = Double.parseDouble(ebayItem.price.value);
        } catch (Exception e) {
            itemValue = lowPrice;
        }
        //if valid filter prices are entered, allows add item if item's value in range
        if (highPrice != 0.0) {
            if (itemValue < lowPrice || itemValue > highPrice) {
                canAddItem = false;
            }
        }
        //if sorting by newly listed items, allows add item if item listed today
        if (ebayReqBody.sortType.equals("newlyListed")) {
            if (ebayItem.itemCreationDate == null
                    || ebayItem.itemCreationDate.toString().equals(Date.valueOf(LocalDate.now()).toString())) {
            } else {
                canAddItem = false;
            }
        }

        return canAddItem;
    }

    public static Map<String, Object> initEbay(EbayReqBody ebayReqBody, String credsPath, int secsBetweenCalls, int option) throws IOException, InterruptedException {
        ArrayList<EbayItemSummary> checkedListings = getCheckedListings(secsBetweenCalls, option);

        String ebayResponse = Ebay.browseEbayListings(ebayReqBody, credsPath, option);

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
            System.out.println("Title: " + ebayItem.title + ", Price: " + ebayItem.price + ", Condition: "
                    + ebayItem.condition + ", Listing Creation Date: " + ebayItem.itemCreationDate + ", URL: " + ebayItem.itemWebUrl);
        }

        System.out.println("Request body: ");
        System.out.println(ebayReqBody);

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

    public static Ebay validateApiCall(String ebayResponse, JavaMailSender javaMailSender, EbayReqBody ebayReqBody, String credsPath, int option) throws IOException, InterruptedException {
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
            ebayResponse = Ebay.browseEbayListings(ebayReqBody, credsPath, option);
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

    public static boolean isListingChecked(EbayItemSummary ebayItemToCheck, int secsBetweenCalls, int option) {
        ArrayList<EbayItemSummary> checkedListings = getCheckedListings(secsBetweenCalls, option);

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

    public static void ebayUtil(EbayReqBody ebayReqBody, String credsPath, JavaMailSender javaMailSender, String logMessage, int secsBetweenCalls, int option) {
        String emailBody = "defaultBody";
        String initCallName = "END OF INIT API CALL FOR " + logMessage;
        String callName = "END OF API CALL FOR " + logMessage + " #";

        try {
            int i = 1;
            Ebay.initEbay(ebayReqBody, credsPath, secsBetweenCalls, option);
            System.out.println();
            System.out.println(initCallName);
            System.out.println("Expected Sell Price: " + ebayReqBody.expectedSellPrice);
            System.out.println();
            TimeUnit.SECONDS.sleep(secsBetweenCalls);
            while (!getCanRun(secsBetweenCalls, option)) {
//                System.out.println(Ebay.getEbay(ebayReqBody, javaMailSender, option));
                Ebay.getEbay(ebayReqBody, credsPath, javaMailSender, secsBetweenCalls, option);
                System.out.println(callName + i);
                System.out.println("Expected Sell Price: " + ebayReqBody.expectedSellPrice);
                System.out.println();
                i++;
                TimeUnit.SECONDS.sleep(secsBetweenCalls);
            }
        } catch (Exception e) {
            emailBody = e.getMessage();
        }

        Ebay.clearCheckedListings(secsBetweenCalls, option);
        System.out.println("EXECUTION FINISHED");
        Ebay.sendEmail(javaMailSender, "michaelmags33@gmail.com", "Ebay Execution stopped", "\"" + credsPath + "\" path stopped: " + emailBody);
    }

    public static void clearCheckedListings(int secsBetweenCalls, int option) {
        if (option == 1 && secsBetweenCalls == 50) {
            EbayCreds.checkedListingsSlow1.clear();
        } else if (option == 2 && secsBetweenCalls == 50) {
            EbayCreds.checkedListingsSlow2.clear();
        } else if (option == 1 && secsBetweenCalls == 25) {
            EbayCreds.checkedListingsFast1.clear();
        } else {
            EbayCreds.checkedListingsFast2.clear();
        }
    }

    public static EbayStatus searchEbay(EbayReqBody ebayReqBody, String credsPath, JavaMailSender javaMailSender, String logMessage, int secsBetweenCalls, int option) {
        if (!Ebay.getCanRun(secsBetweenCalls, option)) return EbayStatus.getEbayStatus();
        Ebay.setCanRun(secsBetweenCalls, option, false);
        Ebay.ebayUtil(ebayReqBody, credsPath, javaMailSender, logMessage, secsBetweenCalls, option);
        return EbayStatus.getEbayStatus();
    }

    public static void getAuthToken(String credsPath, int option) throws IOException {
        OAuth2Api oauth2Api = new OAuth2Api();
        CredentialUtil.load(new FileInputStream(credsPath));
        OAuthResponse oAuthResponse = oauth2Api.getApplicationToken(Environment.PRODUCTION, List.of("https://api.ebay.com/oauth/api_scope"));

//        if (option == 1) {
//            EbayCreds.accessToken1 = oAuthResponse.getAccessToken().orElse(new AccessToken()).getToken();
//        } else if (option == 2) {
//            EbayCreds.accessToken2 = oAuthResponse.getAccessToken().orElse(new AccessToken()).getToken();
//        }
        EbayCreds.accessToken1 = oAuthResponse.getAccessToken().orElse(new AccessToken()).getToken();
    }

    public static boolean getCanRun(int secsBetweenCalls, int option) {
        if (option == 1 && secsBetweenCalls == 50) {
            return EbayCreds.canRunSlow1;
        } else if (option == 2 && secsBetweenCalls == 50) {
            return EbayCreds.canRunSlow2;
        } else if (option == 1 && secsBetweenCalls == 25) {
            return EbayCreds.canRunFast1;
        } else {
            return EbayCreds.canRunFast2;
        }
    }

    public static void setCanRun(int secsBetweenCalls, int option, boolean newBoolean) {
        if (option == 1 && secsBetweenCalls == 50) {
            EbayCreds.canRunSlow1 = newBoolean;
        } else if (option == 2 && secsBetweenCalls == 50) {
            EbayCreds.canRunSlow2 = newBoolean;
        } else if (option == 1 && secsBetweenCalls == 25) {
            EbayCreds.canRunFast1 = newBoolean;
        } else {
            EbayCreds.canRunFast2 = newBoolean;
        }
    }

    public static ArrayList<EbayItemSummary> getCheckedListings(int secsBetweenCalls, int option) {
        if (option == 1 && secsBetweenCalls == 50) {
            return EbayCreds.checkedListingsSlow1;
        } else if (option == 2 && secsBetweenCalls == 50) {
            return EbayCreds.checkedListingsSlow2;
        } else if (option == 1 && secsBetweenCalls == 25) {
            return EbayCreds.checkedListingsFast1;
        } else {
            return EbayCreds.checkedListingsFast2;
        }
    }

    public static String getAccessToken(int option) {
//        if (option == 1) {
//            return EbayCreds.accessToken1;
//        } else {
//            return EbayCreds.accessToken2;
//        }
        return EbayCreds.accessToken1;
    }

    public static void setAccessToken(String accessToken, int option) {
//        if (option == 1) {
//            EbayCreds.accessToken1 = accessToken;
//        } else {
//            EbayCreds.accessToken2 = accessToken;
//        }
        EbayCreds.accessToken1 = accessToken;
    }
}

//Classes necessary for mapping JSON response from EBAY to POJO
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