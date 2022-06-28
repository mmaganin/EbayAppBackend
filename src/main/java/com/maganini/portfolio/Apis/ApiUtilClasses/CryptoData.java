package com.maganini.portfolio.Apis.ApiUtilClasses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
@Data
public class CryptoData{
    @JsonProperty("52")
    public CryptoInfo _52;
    @JsonProperty("512")
    public CryptoInfo _512;
    @JsonProperty("1")
    public CryptoInfo _1;
    @JsonProperty("2")
    public CryptoInfo _2;
    @JsonProperty("74")
    public CryptoInfo _74;
    @JsonProperty("328")
    public CryptoInfo _328;
    @JsonProperty("1028")
    public CryptoInfo _1028;
    @JsonProperty("1027")
    public CryptoInfo _1027;
    @JsonProperty("1831")
    public CryptoInfo _1831;
    @JsonProperty("1839")
    public CryptoInfo _1839;
    @JsonProperty("1958")
    public CryptoInfo _1958;
    @JsonProperty("2010")
    public CryptoInfo _2010;
    @JsonProperty("3077")
    public CryptoInfo _3077;
    @JsonProperty("3794")
    public CryptoInfo _3794;
    @JsonProperty("3890")
    public CryptoInfo _3890;
    @JsonProperty("4030")
    public CryptoInfo _4030;
    @JsonProperty("4172")
    public CryptoInfo _4172;
    @JsonProperty("5426")
    public CryptoInfo _5426;
    @JsonProperty("5805")
    public CryptoInfo _5805;
    @JsonProperty("6535")
    public CryptoInfo _6535;
    @JsonProperty("6636")
    public CryptoInfo _6636;
}
@Data
class CryptoInfo{
    public int id;
    public String name;
    public String symbol;
    public String slug;
    public int num_market_pairs;
    public String date_added;
    public ArrayList<Tag> tags;
    public long max_supply;
    public long circulating_supply;
    public long total_supply;
    public int is_active;
    public Object platform;
    public int cmc_rank;
    public int is_fiat;
    public Object self_reported_circulating_supply;
    public Object self_reported_market_cap;
    public Object tvl_ratio;
    public String last_updated;
    public Quote quote;
}
@Data
class Quote{
    @JsonProperty("USD")
    public Value uSD;
}
@Data
class Tag{
    public String slug;
    public String name;
    public String category;
}
@Data
class Value{
    public double price;
    public double volume_24h;
    public double volume_change_24h;
    public double percent_change_1h;
    public double percent_change_24h;
    public double percent_change_7d;
    public double percent_change_30d;
    public double percent_change_60d;
    public double percent_change_90d;
    public double market_cap;
    public double market_cap_dominance;
    public double fully_diluted_market_cap;
    public Object tvl;
    public String last_updated;
}