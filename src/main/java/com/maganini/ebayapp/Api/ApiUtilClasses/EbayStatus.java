package com.maganini.ebayapp.Api.ApiUtilClasses;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class EbayStatus {
    int checkedListingsFast1Size;
    int checkedListingsSlow1Size;
    boolean canRunFast1;
    boolean canRunSlow1;

    int checkedListingsFast2Size;
    int checkedListingsSlow2Size;
    boolean canRunFast2;
    boolean canRunSlow2;

    public static EbayStatus getEbayStatus() {
        return new EbayStatus(
                EbayCreds.checkedListingsFast1.size(),
                EbayCreds.checkedListingsSlow1.size(),
                EbayCreds.canRunFast1,
                EbayCreds.canRunSlow1,
                EbayCreds.checkedListingsFast2.size(),
                EbayCreds.checkedListingsSlow2.size(),
                EbayCreds.canRunFast2,
                EbayCreds.canRunSlow2
        );
    }
}