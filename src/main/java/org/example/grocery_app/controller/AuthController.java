package org.example.grocery_app.controller;

import org.example.grocery_app.apiPayload.ApiResponse;
import org.example.grocery_app.dto.PasswordResetTokenDto;
import org.example.grocery_app.entities.User;
import org.example.grocery_app.payload.JwtRequest;
import org.example.grocery_app.payload.JwtResponse;
import org.example.grocery_app.repository.UserRepository;
import org.example.grocery_app.security.JwtHelper;
import org.example.grocery_app.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

   @Autowired
   private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request) {
        JwtResponse jwtResponse = this.authService.doLogin(request.getEmail(), request.getPassword());
        return new ResponseEntity<>(jwtResponse, HttpStatus.OK);

    }


    @PutMapping("reset/password")
    public ResponseEntity<PasswordResetTokenDto> resetPassword(@RequestParam  String email){
        PasswordResetTokenDto passwordResetTokenDto = this.authService.requestResetPassword(email);
        return  new ResponseEntity<>(passwordResetTokenDto, HttpStatus.OK);
    }
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse> verifyOtp(@RequestParam String email, @RequestParam String otp, @RequestParam String password){
        this.authService.verifyResetOtp(email, otp,password);
        return new ResponseEntity<>(new ApiResponse("password reset successfully", true), HttpStatus.OK);
    }

    





}
