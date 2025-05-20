package org.example.grocery_app.controller;//package com.example.grocery_app.controller;

//import com.example.grocery_app.service.DeviceTokenService;
import lombok.extern.slf4j.Slf4j;
import org.example.grocery_app.serviceImplementation.DeviceTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class DeviceTokenController {

    @Autowired
    private DeviceTokenService deviceTokenService;

    @PostMapping("/device-token")
    public ResponseEntity<String> saveToken(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String userId = request.get("userId");
        log.info("shashi_token :{}", token);
        if (token == null || userId == null) {
            return ResponseEntity.badRequest().body("Missing token or userId");
        }

        deviceTokenService.saveOrUpdateToken(userId, token);
        return ResponseEntity.ok("Token saved/updated successfully");
    }

    @PostMapping("/push/notify/send-all")
    public ResponseEntity<String> sendToAllUsers(@RequestBody Map<String, String> payload) {
        String title = payload.getOrDefault("title", "New Update");
        String body = payload.getOrDefault("body", "Check out the latest news!");

        Map<String, Object> data = new HashMap<>();
        data.put("screen", "Home"); // optional extra payload

        deviceTokenService.sendPushToAllUsers(title, body, data);
        return ResponseEntity.ok("Push notifications sent to all users.");
    }
}
