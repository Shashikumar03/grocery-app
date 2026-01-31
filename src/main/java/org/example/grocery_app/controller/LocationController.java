package org.example.grocery_app.controller;

import org.example.grocery_app.entities.User;
import org.example.grocery_app.entities.UserLocation;
import org.example.grocery_app.payload.LocationRequest;
import org.example.grocery_app.repository.LocationRepository;
import org.example.grocery_app.repository.UserRepository;
import org.example.grocery_app.security.JwtHelper;
import org.example.grocery_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/location")
public class LocationController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtHelper jwtHelper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LocationRepository locationRepository;

    @PostMapping
    public ResponseEntity<?> saveLocation(
            @RequestBody LocationRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.substring(7);
        Long userId = jwtHelper.getIdFromToken(token);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserLocation location = new UserLocation();
        location.setLatitude(request.getLatitude());
        location.setLongitude(request.getLongitude());
        location.setTimestamp(LocalDateTime.now());
        location.setUser(user);

        locationRepository.save(location);

        return ResponseEntity.ok("Location saved");
    }

}
