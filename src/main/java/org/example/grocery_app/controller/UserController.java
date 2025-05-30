package org.example.grocery_app.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.grocery_app.dto.UserDto;
import org.example.grocery_app.entities.User;
import org.example.grocery_app.exception.ApiException;
import org.example.grocery_app.security.JwtHelper;
import org.example.grocery_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtHelper jwtHelper;

    // Create a new user
    @PostMapping("/")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        UserDto createdUser = userService.createUser(userDto);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    // Get user by ID
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        UserDto user = userService.getUserById(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    // Update user details
    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        UserDto updatedUser = userService.updateUser(userId, userDto);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    // Get all users
    @GetMapping("/")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // Delete user by ID
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Get user by email (Optional)
    @GetMapping("/email")
    public ResponseEntity<UserDto> getUserByEmail(@RequestParam String email) {
        UserDto user = userService.getUserByEmail(email);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
    @GetMapping("/current-user-info")
    public ResponseEntity<?> getCurrentUserInfo(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // Remove "Bearer " prefix

        Long userId = this.jwtHelper.getIdFromToken(token);
        String role = jwtHelper.getRoleFromToken(token);
//        User userFromToken = this.jwtHelper.getUserFromToken(token);

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", userId);
        userInfo.put("role", role);
//        userInfo.put("user",userFromToken);

        return ResponseEntity.ok(userInfo);
    }
    @GetMapping("role/{role}")
    public ResponseEntity<List<UserDto>> getUserByRole(@PathVariable String role){
        List<UserDto> userByRole = this.userService.getUserByRole(role);
        return  new ResponseEntity<>(userByRole, HttpStatus.OK);
    }

    @DeleteMapping("/account/delete")
    public ResponseEntity<?> deleteMyAccount(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You must be logged in to delete your account");
        }
       try {
           String userEmail = authentication.getName();
           log.info("userEmail :{}", userEmail);
           this.userService.deleteMyAccount(userEmail);
       }catch (Exception e){
            throw  new ApiException("User not found");
       }


        return ResponseEntity.ok("Account deleted successfully.");
    }

}
