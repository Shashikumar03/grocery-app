package org.example.grocery_app.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserLocation {
    @Id
    @GeneratedValue
    private Long id;

    private Double latitude;
    private Double longitude;
    private LocalDateTime timestamp;

    @ManyToOne
    private User user;
}
