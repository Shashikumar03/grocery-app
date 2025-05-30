package org.example.grocery_app.service;

import org.example.grocery_app.dto.PasswordResetTokenDto;
import org.example.grocery_app.payload.JwtResponse;

public interface AuthService {

    JwtResponse doLogin(String email, String password);

    PasswordResetTokenDto requestResetPassword(String  email);

    void verifyResetOtp(String email, String otp, String password);

    void resetPassword(String email, String password);


}
