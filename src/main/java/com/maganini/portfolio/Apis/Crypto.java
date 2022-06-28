package com.maganini.portfolio.Apis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.maganini.portfolio.Apis.ApiUtilClasses.ApiUtil;
import com.maganini.portfolio.Apis.ApiUtilClasses.Creds;
import com.maganini.portfolio.Apis.ApiUtilClasses.CryptoData;
import com.maganini.portfolio.Apis.ApiUtilClasses.CryptoStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Makes API calls to coinmarketcap.com's market data API at https://pro-api.coinmarketcap.com
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Crypto {
    public CryptoStatus status;
    public CryptoData data;

    //coinmarketcap API URI all requests are made to
    private static String uri = "https://pro-api.coinmarketcap.com/v2/cryptocurrency/quotes/latest";
    //slugs associated with specific cryptocurrencies used in HTTP request
    public static String slugsToRequest =
            "algorand,cardano,vechain,cosmos,avalanche," +
                    "bitcoin-cash,bnb,bitcoin,dogecoin,monero," +
                    "polkadot-new,ethereum,litecoin,terra-luna,polygon," +
                    "near-protocol,solana,tron,stellar,xrp";

    /**
     * @param uri        fetch from URI
     * @param parameters list: slugs of cryptos to request
     * @return market data as String
     * @throws URISyntaxException
     * @throws IOException
     */
    public static String makeAPICall(String uri, List<NameValuePair> parameters)
            throws URISyntaxException, IOException {
        String response_content = "";
        URIBuilder query = new URIBuilder(uri);
        query.addParameters(parameters);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(query.build());
        request.setHeader(HttpHeaders.ACCEPT, "application/json");
        request.addHeader("X-CMC_PRO_API_KEY", Creds.getCryptoApiKey());
        CloseableHttpResponse response = client.execute(request);
        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            response_content = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }

        return response_content;
    }

    /**
     * fetches market data based on slugsToRequest
     *
     * @return market data as String
     */
    public static Crypto fetchMarketData(String slugsToRequest) {
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("slug", slugsToRequest));
        String result = "";
        Crypto cryptoOutput = new Crypto();
        try {
            result = makeAPICall(uri, parameters);
            cryptoOutput = (Crypto) ApiUtil.mapStrResponseToObj(result, Crypto.class);
            System.out.println(result);
        } catch (IOException e) {
            System.out.println("Error: cannot access content - " + e.toString());
        } catch (URISyntaxException e) {
            System.out.println("Error: Invalid URL " + e.toString());
        }

        return cryptoOutput;
    }

    public static void main(String[] args) {
        System.out.println(fetchMarketData(slugsToRequest));
    }
}