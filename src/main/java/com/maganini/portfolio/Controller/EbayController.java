package com.maganini.portfolio.Controller;

import com.maganini.portfolio.Apis.ApiUtilClasses.EbayCreds;
import com.maganini.portfolio.Apis.ApiUtilClasses.EbayReqBody;
import com.maganini.portfolio.Apis.Ebay;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/ebay")
public class EbayController {
    private final JavaMailSender javaMailSender;

    @GetMapping("/slow1")
    public boolean isRunningSlow1() {
        return EbayCreds.isRunningSlow1;
    }

    @GetMapping("/slow2")
    public boolean isRunningSlow2() {
        return EbayCreds.isRunningSlow2;
    }

    @GetMapping("/fast1")
    public boolean isRunningFast1() {
        return EbayCreds.isRunningFast1;
    }

    @GetMapping("/fast2")
    public boolean isRunningFast2() {
        return EbayCreds.isRunningFast2;
    }


    @PostMapping("/slow1")
    public void searchEbaySlow1(@RequestBody EbayReqBody ebayReqBody) {
//        EbayReqBody ebayReqBody = new EbayReqBody("rtx 3090", "300", "1000", "50", "NEW", "newlyListed");
        if(EbayCreds.isRunningSlow1) return;
        EbayCreds.isRunningSlow1 = true;
        Ebay.ebayUtil(ebayReqBody, javaMailSender, "Slow 1 " + ebayReqBody.keyword, 50, 1);
        EbayCreds.isRunningSlow1 = false;
    }

    @PostMapping("/slow2")
    public void searchEbaySlow2(@RequestBody EbayReqBody ebayReqBody) {
        if(EbayCreds.isRunningSlow2) return;
        EbayCreds.isRunningSlow2 = true;
        Ebay.ebayUtil(ebayReqBody, javaMailSender, "Slow 2 " + ebayReqBody.keyword, 50, 2);
        EbayCreds.isRunningSlow2 = false;
    }

    @PostMapping("/fast1")
    public void searchEbayFast1(@RequestBody EbayReqBody ebayReqBody) {
        if(EbayCreds.isRunningFast1) return;
        EbayCreds.isRunningFast1 = true;
        Ebay.ebayUtil(ebayReqBody, javaMailSender, "Fast 1 " + ebayReqBody.keyword, 25, 1);
        EbayCreds.isRunningFast1 = false;
    }

    @PostMapping("/fast2")
    public void searchEbayFast2(@RequestBody EbayReqBody ebayReqBody) {
        if(EbayCreds.isRunningFast2) return;
        EbayCreds.isRunningFast2 = true;
        Ebay.ebayUtil(ebayReqBody, javaMailSender, "Fast 2 " + ebayReqBody.keyword, 25, 2);
        EbayCreds.isRunningFast2 = false;
    }
}
