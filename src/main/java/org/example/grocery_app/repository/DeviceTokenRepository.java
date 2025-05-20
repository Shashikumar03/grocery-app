package org.example.grocery_app.repository;


//import org.example.grocery_app.model.NotificationToken;
import org.example.grocery_app.entities.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;




public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
    Optional<DeviceToken> findByUserId(String userId);
    Optional<DeviceToken> findByToken(String token);
}
