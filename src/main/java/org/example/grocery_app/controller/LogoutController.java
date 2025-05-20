package org.example.grocery_app.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.grocery_app.serviceImplementation.TokenBlacklistServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class LogoutController {

    private final TokenBlacklistServiceImpl tokenBlacklistService;

    public LogoutController(TokenBlacklistServiceImpl tokenBlacklistService) {
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        // Retrieve the token from the Authorization header
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Invalid Authorization header");
        }

        String jwtToken = bearerToken.substring(7); // Remove the "Bearer " prefix

        // Call the service to blacklist the token
        tokenBlacklistService.blacklistToken(jwtToken, 100000);

        // Logging the logout action (optional, but useful for tracking)
        // You can add any logger to capture this event
        System.out.println("Logged out successfully. Token: " + jwtToken);

        return ResponseEntity.ok("Logged out successfully");
    }
}
