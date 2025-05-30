package org.example.grocery_app.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.grocery_app.serviceImplementation.TokenBlacklistServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private TokenBlacklistServiceImpl tokenBlacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestHeader = request.getHeader("Authorization");
        String username = null;
        String token = null;

        if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
            token = requestHeader.substring(7);
            try {
                username = jwtHelper.getUsernameFromToken(token);
                logger.info("Extracted username: {}", username);
            } catch (IllegalArgumentException e) {
                logger.warn("Illegal Argument while extracting username from token.");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Illegal Argument while extracting username from token.");
                return;
            } catch (ExpiredJwtException e) {
                logger.warn("JWT token has expired.");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("JWT token has expired.");
                return;
            } catch (MalformedJwtException e) {
                logger.warn("Malformed JWT token detected.");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid JWT token. Please log in again.");
                return;
            } catch (Exception e) {
                logger.warn("JWT token processing failed: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid token.");
                return;
            }
        } else {
            logger.debug("No valid Authorization header found.");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                logger.info("Attempting to load user: {}", username);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (tokenBlacklistService.isTokenBlacklisted(token)) {
                    logger.warn("Token is blacklisted. Access denied.");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Token is expired or you have logged out. Please login again.");
                    return;
                }

                boolean isTokenValid = jwtHelper.validateToken(token, userDetails);
                logger.info("Token validation result: {}", isTokenValid);

                if (isTokenValid) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.info("Authentication successful for user: {}", username);
                }

            } catch (Exception ex) {
                logger.warn("Authentication failed for user '{}': {}", username, ex.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Unauthorized: " + ex.getMessage());
                return;
            }
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}




//package org.example.grocery_app.security;
//
//
//
//import io.jsonwebtoken.ExpiredJwtException;
//import io.jsonwebtoken.MalformedJwtException;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//
////import org.example.library.exceptions.ApiException;
//import org.example.grocery_app.serviceImplementation.TokenBlacklistServiceImpl;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;;
//
//@Component
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    //    private Logger logger = LoggerFactory.getLogger(OncePerRequestFilter.class);
//    @Autowired
//    private JwtHelper jwtHelper;
//
//
//    @Autowired
//    private UserDetailsService userDetailsService;
//    @Autowired
//    private TokenBlacklistServiceImpl tokenBlacklistService;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//
////        try {
////            Thread.sleep(500);
////        } catch (InterruptedException e) {
////            throw new RuntimeException(e);
////        }
//        //Authorization
//
//        String requestHeader = request.getHeader("Authorization");
//        //Bearer 2352345235sdfrsfgsdfsdf
////        logger.info(" Header :  {}", requestHeader);
//        String username = null;
//        String token = null;
//        if (requestHeader != null && requestHeader.startsWith("Bearer")) {
//            //looking good
//            token = requestHeader.substring(7);
//            try {
//
//                username = this.jwtHelper.getUsernameFromToken(token);
//                logger.info("userName1: "+username);
//                logger.info("token1: "+token);
//
//
//
//
//            } catch (IllegalArgumentException e) {
//                logger.info("Illegal Argument while fetching the username !!");
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                response.getWriter().write("Illegal Argument while fetching the username !!");
//                return;
//            } catch (ExpiredJwtException e) {
//                logger.info("Given jwt token is expired !!");
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                response.getWriter().write("Given jwt token is expired !!");
//                return;
//            } catch (MalformedJwtException e) {
//                logger.info("Some changed has done in token !! Invalid Token");
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                response.getWriter().write("Some changed has done in token !! Invalid Token !! please login first.");
//                return;
////                throw  new ApiException("Some changed has done in token !! Invalid Token");
//            } catch (Exception e) {
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                response.getWriter().write("Invalid Token.");
//                return;
//
//
//            }
//
//
//        } else {
//            logger.info("Invalid Header Value !! ");
////            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
////            response.getWriter().write("Invalid Header Value !!");
////            return;
//        }
//
//
//        //
//        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//
//
//            //fetch user detail from username
//            logger.info("shashi details"+ "Shashi");
//            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
//            Boolean validateToken = this.jwtHelper.validateToken(token, userDetails);
//            logger.info("userDetails: "+userDetails);
//            logger.info("validateToken: "+validateToken);
//
//            // Inside your JwtAuthenticationFilter.doFilterInternal
//
//            if (token != null) {
//                //  Check if blacklisted
//                if (tokenBlacklistService.isTokenBlacklisted(token)) {
//                    logger.info("Token is blacklisted. Rejecting request.");
//                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                    response.getWriter().write("Token has been expired or u logged out. Please login again.");
//                    return;
//                }
//            }
//
//
//            if (validateToken) {
//
//                //set the authentication
//                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                logger.info("authentication: "+authentication);
//                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//
//
//            } else {
//                logger.info("validation fails");
////                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
////                response.getWriter().write("Validation fails !!");
////                return;
////                logger.info("Validation fails !!");
//            }
//
//
//        }
//
//        filterChain.doFilter(request, response);
//
//
//    }
//}