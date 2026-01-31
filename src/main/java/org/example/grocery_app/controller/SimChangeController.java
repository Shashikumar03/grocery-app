package org.example.grocery_app.controller;

//package com.shashi03.Bazzario.controller; // change to your package

//import com.shashi03.Bazzario.dto.SimChangeRequest;
//import com.shashi03.Bazzario.entity.User;
//import com.shashi03.Bazzario.service.SimChangeService;
import org.example.grocery_app.entities.User;
import org.example.grocery_app.payload.SimChangeRequest;
import org.example.grocery_app.security.JwtHelper;
import org.example.grocery_app.serviceImplementation.SimChangeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLOutput;

/**
 * Receives SIM change reports from the app. User is identified from JWT.
 * Use this to log security events, invalidate other sessions, or send alerts.
 */
@RestController
@RequestMapping("/api/sim-change")
public class SimChangeController {

    private final SimChangeService simChangeService;
    private final JwtHelper jwtHelper; // your helper to parse JWT

    public SimChangeController(SimChangeService simChangeService, JwtHelper jwtHelper) {
        this.simChangeService = simChangeService;
        this.jwtHelper = jwtHelper;
    }

    @PostMapping
    public ResponseEntity<?> reportSimChange(
            @RequestBody SimChangeRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String token = authHeader.substring(7); // remove "Bearer "
        Long userId = jwtHelper.getIdFromToken(token); // extract userId
        if (userId == null) {
            return ResponseEntity.status(401).body("Invalid token");
        }

        simChangeService.recordSimChange(userId, request.getPreviousFingerprint(), request.getCurrentFingerprint());
        return ResponseEntity.ok("SIM change recorded");
    }
}
