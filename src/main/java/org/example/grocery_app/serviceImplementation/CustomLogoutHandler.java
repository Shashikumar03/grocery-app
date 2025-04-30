package org.example.grocery_app.serviceImplementation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.grocery_app.security.JwtHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomLogoutHandler implements LogoutHandler {

    // Autowired directly on the field
    @Autowired
    private TokenBlacklistServiceImpl tokenBlacklistService;

    @Autowired
    private JwtHelper jwtHelper;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // Get expiration time from JWT
            long expiry = jwtHelper.getExpirationTimeInMillis(token);
            long now = System.currentTimeMillis();
            long ttl = expiry - now; // How long until token naturally expires

            if (ttl > 0) {
                tokenBlacklistService.blacklistToken(token, ttl);
            }
        }
    }
}
