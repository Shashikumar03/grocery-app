package org.example.grocery_app.controller;//package com.example.grocery_app.controller;

//import com.example.grocery_app.service.DeviceTokenService;
import lombok.extern.slf4j.Slf4j;
import org.example.grocery_app.serviceImplementation.DeviceTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class DeviceTokenController {

    @Autowired
    private DeviceTokenService service;

    @PostMapping("/device-token")
    public ResponseEntity<String> saveToken(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String userId = request.get("userId");
        log.info("shahsi_token :{}", token);
        if (token == null || userId == null) {
            return ResponseEntity.badRequest().body("Missing token or userId");
        }

        service.saveOrUpdateToken(userId, token);
        return ResponseEntity.ok("Token saved/updated successfully");
    }
}
