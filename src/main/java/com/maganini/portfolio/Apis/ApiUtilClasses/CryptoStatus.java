package com.maganini.portfolio.Apis.ApiUtilClasses;

import lombok.Data;

@Data
public class CryptoStatus{
    public String timestamp;
    public int error_code;
    public Object error_message;
    public int elapsed;
    public int credit_count;
    public Object notice;
}