package com.maganini.portfolio.Apis.ApiUtilClasses;

import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;

public class EbayCreds {
    public static String accessToken1 = "";
    public static final ArrayList<EbayItemSummary> checkedListingsFast1 = new ArrayList<>();
    public static final ArrayList<EbayItemSummary> checkedListingsSlow1 = new ArrayList<>();

    public static boolean canRunFast1 = true;
    public static boolean canRunSlow1 = true;

//    public static String accessToken2 = "";
    public static final ArrayList<EbayItemSummary> checkedListingsFast2 = new ArrayList<>();
    public static final ArrayList<EbayItemSummary> checkedListingsSlow2 = new ArrayList<>();
    public static boolean canRunFast2 = true;
    public static boolean canRunSlow2 = true;
}
