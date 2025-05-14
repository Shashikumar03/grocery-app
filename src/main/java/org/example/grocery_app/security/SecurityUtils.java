//package org.example.grocery_app.security;
//
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.extern.slf4j.Slf4j;
//import org.example.grocery_app.exception.ApiException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component //  Very Important
//public class SecurityUtils {
//
//    private final JwtHelper jwtHelper;
//
//    @Autowired
//    public SecurityUtils(JwtHelper jwtHelper) {
//        this.jwtHelper = jwtHelper;
//    }
//
//    public void validateUserAccess(Long userId, HttpServletRequest request) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String currentUserEmail = authentication.getName();
//        String bearerToken = request.getHeader("Authorization");
//
//        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
//            throw new ApiException("Invalid Authorization header");
//        }
//
//        String jwtToken = bearerToken.substring(7);
//        Long idFromToken = this.jwtHelper.getIdFromToken(jwtToken);
//
//        log.info("Logged userId from token : {}", idFromToken);
//        log.info("UserId from request : {}", userId);
//        log.info("Logged user email : {}", currentUserEmail);
//
//        if (!userId.equals(idFromToken)) {
//            throw new ApiException("Access denied. Please login with your credentials.");
//        }
//    }
//}
