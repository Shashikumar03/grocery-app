package org.example.grocery_app.service;

//package org.example.grocery_app.service;

public interface SmsService {
    String sendOtp(String phoneNumber);
    String verifyOtp(String phoneNumber, String code);
//    String sendMessage(String phoneNumber, String message);
    String resendOtp(String phoneNumber);
}
