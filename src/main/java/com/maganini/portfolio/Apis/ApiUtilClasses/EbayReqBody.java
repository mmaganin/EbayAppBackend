package com.maganini.portfolio.Apis.ApiUtilClasses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EbayReqBody {
    public String keyword;
    public String lowPrice;
    public String highPrice;
    public String numberOfResults;
    public String condition;
    public String sortType;
    public String credsPath;
}
