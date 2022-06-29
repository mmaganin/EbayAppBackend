package com.maganini.portfolio.Controller;

import com.maganini.portfolio.Apis.*;
import com.maganini.portfolio.Apis.ApiUtilClasses.ApiUtil;
import com.maganini.portfolio.Apis.ApiUtilClasses.EbayItemSummary;
import com.maganini.portfolio.Apis.ApiUtilClasses.EbayReqBody;
import com.maganini.portfolio.PortfolioInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.maganini.portfolio.Apis.Ebay.initEbay;
import static com.maganini.portfolio.Apis.Ebay.sendEmail;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PortfolioController {
//    @Autowired
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

    @PostMapping("/ebay")
    public void searchEbay(@RequestBody EbayReqBody ebayReqBody) throws IOException, InterruptedException {
//        Ebay.sendEmail(javaMailSender, "michaelmags33@gmail.com", "test email subject","test email body");

//        EbayReqBody ebayReqBody = new EbayReqBody("rtx 3090", "300", "1000", "50", "NEW", "newlyListed");

        int i = 1;
        System.out.println(Ebay.initEbay(ebayReqBody));
        System.out.println();
        System.out.println("END OF INIT API CALL");
        System.out.println();
        while(true){
            TimeUnit.SECONDS.sleep(25);
            System.out.println(Ebay.getEbay(ebayReqBody, javaMailSender));
            System.out.println("END OF API CALL #" + i);
            System.out.println();
            i++;
        }


//        return ResponseEntity.ok().body(null);
//        return ResponseEntity.ok().body(Ebay.getEbay(ebayReqBody, javaMailSender));
    }

//    @PostMapping("/ebayinit")
//    public ResponseEntity<Map<String, Object>> ebayInit(@RequestBody EbayReqBody ebayReqBody) throws IOException, InterruptedException {
//        return ResponseEntity.ok().body(initEbay(ebayReqBody));
//    }
}