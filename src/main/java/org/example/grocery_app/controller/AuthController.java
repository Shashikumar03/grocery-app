package org.example.grocery_app.controller;

import org.example.grocery_app.entities.User;
import org.example.grocery_app.payload.JwtRequest;
import org.example.grocery_app.payload.JwtResponse;
import org.example.grocery_app.repository.UserRepository;
import org.example.grocery_app.security.JwtHelper;
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
    private UserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository; // ✅ Add this

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private JwtHelper helper;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request) {

        this.doAuthenticate(request.getEmail(), request.getPassword());

        // Load UserDetails
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

        // ✅ Load the actual User entity
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new RuntimeException("User not found with email: " + request.getEmail())
        );

        // ✅ Now pass correct User object
        String token = this.helper.generateToken(user);
        Long idFromToken = this.helper.getIdFromToken(token);
        System.out.println("id from the token: "+ idFromToken);


        JwtResponse response = JwtResponse.builder()
                .jwtToken(token)
//                .username(user.getUsername())   // user's username/email
//                .role(user.getRole())            // user's role
//                .id(user.getId())                // user's id
                .user(user)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
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
