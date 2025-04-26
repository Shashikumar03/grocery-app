package org.example.grocery_app.service;

import org.example.grocery_app.payload.JwtResponse;

public interface AuthService {

    JwtResponse doLogin(String email, String password);
}
