package com.maganini.ebayapp.Api.ApiUtilClasses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EbayReqBody {
    public String keyword;
    public String lowPrice;
    public String highPrice;
    public String numberOfResults;
    public String sortType;
    public String conditions;
    public String buyingOptions;
}
