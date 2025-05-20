package org.example.grocery_app.service;

import org.example.grocery_app.dto.PasswordResetTokenDto;
import org.example.grocery_app.payload.JwtResponse;

import java.util.HashMap;

public interface AuthService {

    JwtResponse doLogin(String email, String password);

    PasswordResetTokenDto resetPassword(String  email);

    boolean verifyResetOtp(HashMap<String, String> otpDetails);


}
