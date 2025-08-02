package org.example.grocery_app.controller;

//package org.example.grocery_app.controller;

import org.example.grocery_app.serviceImplementation.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
}
