package org.example.grocery_app.controller;

//package org.example.grocery_app.controller;

import org.example.grocery_app.serviceImplementation.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/sms")
public class TwilioSmsController {

    @Autowired
    private SmsService smsService;

    // Example: /api/sms/send?to=%2B917073052300&message=Hii%20AMIT%20YOUR%20ACCOUNT%20IS%20BLOCKED%20Please%20CONTACT%20TO%20AXIS%20BANK!
    @PostMapping("/send")
    public String sendSms(@RequestParam String to, @RequestParam String message) {
        smsService.sendSms(to, message);
        return "✅ SMS sent successfully to " + to;
    }


    // ✅ Send SMS to all hardcoded numbers
//    @GetMapping("/send/all")
//    public String sendSmsToAll(@RequestParam String message) {
//        StringBuilder report = new StringBuilder();
//        for (String number : phoneNumbers) {
//            try {
//                smsService.sendSms(number, message);
//                report.append("✅ Sent to: ").append(number).append("\n");
//            } catch (Exception e) {
//                report.append("❌ Failed for: ").append(number).append(" | ").append(e.getMessage()).append("\n");
//            }
//        }
//        return report.toString();
//    }
}

