package org.example.grocery_app.dto;

import jakarta.persistence.*;
import lombok.*;
import org.example.grocery_app.entities.User;

import java.time.LocalDateTime;

//@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PasswordResetTokenDto {


    private Long id;

    private Long userId;

    private String otp;

//    private LocalDateTime createdTime;

//    private int attemptCount;



}