package org.example.grocery_app.repository;


//import org.example.grocery_app.model.NotificationToken;
import org.example.grocery_app.entities.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;




public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
    Optional<DeviceToken> findByUserId(String userId);
    Optional<DeviceToken> findByToken(String token);
    @Query("SELECT d.token FROM DeviceToken d")
    List<String> findAllTokens();
}
