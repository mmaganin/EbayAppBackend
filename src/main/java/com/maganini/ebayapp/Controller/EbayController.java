package com.maganini.ebayapp.Controller;

import com.maganini.ebayapp.Api.ApiUtilClasses.EbayCreds;
import com.maganini.ebayapp.Api.ApiUtilClasses.EbayReqBody;
import com.maganini.ebayapp.Api.ApiUtilClasses.EbayStatus;
import com.maganini.ebayapp.Api.Ebay;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/ebay")
public class EbayController {
    private final JavaMailSender javaMailSender;

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/status")
    public EbayStatus isRunningSlow1() {
        return EbayStatus.getEbayStatus();
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PutMapping("/slow1")
    public EbayStatus stopSlow1() {
        EbayCreds.canRunSlow1 = true;
        return EbayStatus.getEbayStatus();
    }
    @CrossOrigin(origins = "http://localhost:3000")
    @PutMapping("/slow2")
    public EbayStatus stopSlow2() {
        EbayCreds.canRunSlow2 = true;
        return EbayStatus.getEbayStatus();
    }
    @CrossOrigin(origins = "http://localhost:3000")
    @PutMapping("/fast1")
    public EbayStatus stopFast1() {
        EbayCreds.canRunFast1 = true;
        return EbayStatus.getEbayStatus();
    }
    @CrossOrigin(origins = "http://localhost:3000")
    @PutMapping("/fast2")
    public EbayStatus stopFast2() {
        EbayCreds.canRunFast2 = true;
        return EbayStatus.getEbayStatus();
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/slow1")
    public EbayStatus searchEbaySlow1(@RequestBody EbayReqBody ebayReqBody) {
//        return Ebay.searchEbay(ebayReqBody, "../ebay-config.yaml", javaMailSender, "Slow 1 " + ebayReqBody.keyword, 50, 1);
        return Ebay.searchEbay(ebayReqBody, Ebay.credsPath, javaMailSender, "Slow 1 " + ebayReqBody.keyword, 50, 1);

    }
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/slow2")
    public EbayStatus searchEbaySlow2(@RequestBody EbayReqBody ebayReqBody) {
        return Ebay.searchEbay(ebayReqBody, Ebay.credsPath, javaMailSender, "Slow 2 " + ebayReqBody.keyword, 50, 2);
    }
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/fast1")
    public EbayStatus searchEbayFast1(@RequestBody EbayReqBody ebayReqBody) {
        return Ebay.searchEbay(ebayReqBody, Ebay.credsPath, javaMailSender, "Fast 1 " + ebayReqBody.keyword, 25, 1);

    }
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/fast2")
    public EbayStatus searchEbayFast2(@RequestBody EbayReqBody ebayReqBody) {
        return Ebay.searchEbay(ebayReqBody, Ebay.credsPath, javaMailSender, "Fast 2 " + ebayReqBody.keyword, 25, 2);
    }

}
