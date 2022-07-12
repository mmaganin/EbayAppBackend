package com.maganini.ebayapp.Api.ApiUtilClasses;

import lombok.Data;

@Data
public class EbayPrice{
    public String convertedFromCurrency;
    public String convertedFromValue;
    public String currency;
    public String value;
}