package org.example.grocery_app.controller;

import org.example.grocery_app.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sms")
public class SmsController {

    @Autowired
    private SmsService smsService;

    // Send a generic SMS
    @PostMapping("/send")
    public ResponseEntity<String> sendSms(
            @RequestParam("to") String to,
            @RequestParam String message
    ) {
//        String response = smsService.sendMessage(to, message);
        return ResponseEntity.ok(null);
    }

    // Send OTP to the given phone number
    @PostMapping("/sendOtp")
    public ResponseEntity<String> sendOtp(
            @RequestParam("to") String to
    ) {
        // Calling the sendOtp method from SmsService
        String response = smsService.sendOtp(to);
        return ResponseEntity.ok(response);
    }

    // Verify the OTP entered by the user
    @PostMapping("/verifyOtp")
    public ResponseEntity<String> verifyOtp(
            @RequestParam("to") String to,
            @RequestParam("code") String code
    ) {
        // Calling the verifyOtp method from SmsService
        String response = smsService.verifyOtp(to, code);
        return ResponseEntity.ok(response);
    }

    // Resend OTP
    @PostMapping("/resend-otp")
    public ResponseEntity<String> resendOtp(@RequestParam("phone") String phoneNumber) {
        String response = smsService.resendOtp(phoneNumber);
        return ResponseEntity.ok(response);
    }


}
