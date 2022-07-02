package com.maganini.portfolio.Controller;

import com.maganini.portfolio.Apis.*;
import com.maganini.portfolio.Apis.ApiUtilClasses.EbayReqBody;
import com.maganini.portfolio.PortfolioInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PortfolioController {
    private final JavaMailSender javaMailSender;

    @GetMapping("/portfolio")
    public ResponseEntity<PortfolioInfo> getMyInfo() {
        return ResponseEntity.ok().body(new PortfolioInfo());
    }

    @GetMapping("/nasapicture")
    public ResponseEntity<NasaPictureOfDay> getNasaPicture() throws IOException, InterruptedException {
        return ResponseEntity.ok().body(NasaPictureOfDay.getNasaPicture());
    }

    @PostMapping("/gasprices")
    public ResponseEntity<GasPrices> getGasPricesByState(@RequestBody String stateAbbr) throws IOException, InterruptedException {
        return ResponseEntity.ok().body(GasPrices.getGasPrices(stateAbbr));
    }

    @GetMapping("/cryptoprices")
    public ResponseEntity<Crypto> getCryptoPrices() {
        return ResponseEntity.ok().body(Crypto.fetchMarketData(Crypto.slugsToRequest));
    }

    @PostMapping("/markets")
    public ResponseEntity<Map<String, Object>> getMarkets(@RequestBody String marketAbbr) throws IOException, InterruptedException {
        return ResponseEntity.ok().body(Markets.getMarkets(marketAbbr));
    }

    @PostMapping("/ebaySlow1")
    public void searchEbaySingle1(@RequestBody EbayReqBody ebayReqBody) {
//        EbayReqBody ebayReqBody = new EbayReqBody("rtx 3090", "300", "1000", "50", "NEW", "newlyListed");
        Ebay.ebayUtil(ebayReqBody, javaMailSender, "Single 1 " + ebayReqBody.keyword, 50,1);
    }

    @PostMapping("/ebaySlow2")
    public void searchEbaySingle2(@RequestBody EbayReqBody ebayReqBody) {
        Ebay.ebayUtil(ebayReqBody, javaMailSender, "Single 2 " + ebayReqBody.keyword, 50,2);
    }

    @PostMapping("/ebayFast1")
    public void searchEbayDouble1(@RequestBody EbayReqBody ebayReqBody) {
        Ebay.ebayUtil(ebayReqBody, javaMailSender, "Double 1 " + ebayReqBody.keyword, 25,1);
    }

    @PostMapping("/ebayFast2")
    public void searchEbayDouble2(@RequestBody EbayReqBody ebayReqBody) {
        Ebay.ebayUtil(ebayReqBody, javaMailSender, "Double 2 " + ebayReqBody.keyword, 25,2);
    }
}

