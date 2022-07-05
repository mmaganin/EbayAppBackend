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
@RequestMapping("/api/portfolio")
public class PortfolioController {

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

}

