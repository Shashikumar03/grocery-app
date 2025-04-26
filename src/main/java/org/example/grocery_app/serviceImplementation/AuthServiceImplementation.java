package org.example.grocery_app.serviceImplementation;

import org.example.grocery_app.entities.User;
import org.example.grocery_app.payload.JwtResponse;
import org.example.grocery_app.repository.UserRepository;
import org.example.grocery_app.security.JwtHelper;
import org.example.grocery_app.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Service
public class AuthServiceImplementation implements AuthService {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository; // ✅ Add this

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private JwtHelper helper;



    @Override
    public JwtResponse doLogin(String email, String password) {
        this.doAuthenticate(email, password);

        // Load UserDetails
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);


        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("User not found with email: " + email)
        );

        String token = this.helper.generateToken(user);
        Long idFromToken = this.helper.getIdFromToken(token);
        System.out.println("id from the token: "+ idFromToken);


        return JwtResponse.builder()
                .jwtToken(token)
//                .username(user.getUsername())   // user's username/email
//                .role(user.getRole())            // user's role
//                .id(user.getId())                // user's id
                .user(user)
                .build();
    }
    private void doAuthenticate(String email, String password) {

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, password);
        try {
            manager.authenticate(authentication);

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid Username or Password !!");
        }
    }

    @ExceptionHandler(BadCredentialsException.class)
    public String exceptionHandler() {
        return "Credentials Invalid !!";
    }
}
