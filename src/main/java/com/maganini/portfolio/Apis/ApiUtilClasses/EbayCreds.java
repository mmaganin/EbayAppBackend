package com.maganini.portfolio.Apis.ApiUtilClasses;

import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;

public class EbayCreds {
    public static String accessToken1 = "";
    public static final ArrayList<EbayItemSummary> checkedListings1 = new ArrayList<>();
    public static boolean isRunningFast1 = false;
    public static boolean isRunningSlow1 = false;

    public static String accessToken2 = "";
    public static final ArrayList<EbayItemSummary> checkedListings2 = new ArrayList<>();
    public static boolean isRunningFast2 = false;
    public static boolean isRunningSlow2 = false;
}
