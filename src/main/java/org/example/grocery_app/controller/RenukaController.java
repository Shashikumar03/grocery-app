package org.example.grocery_app.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/renuka/webhook")
public class RenukaController {

    @PostMapping("/")
    public ResponseEntity<String> webhook(@RequestBody String payload) {
        System.out.println("Received webhook payload: " + payload);

        // TODO: process the payload if nee ded
        log.info(payload);

        return ResponseEntity.ok("Webhook received successfully");
    }
}
