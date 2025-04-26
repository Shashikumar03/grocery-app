package org.example.grocery_app.serviceImplementation;

import org.example.grocery_app.payload.JwtResponse;
import org.example.grocery_app.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImplementation implements AuthService {



    @Override
    public JwtResponse doLogin(String email, String password) {
        return null;
    }
}
