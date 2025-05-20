package org.example.grocery_app.serviceImplementation;


//import com.example.grocery_app.model.DeviceToken;
//import com.example.grocery_app.repository.DeviceTokenRepository;
import org.example.grocery_app.entities.DeviceToken;
import org.example.grocery_app.repository.DeviceTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeviceTokenService {

    @Autowired
    private DeviceTokenRepository repository;

    public void saveOrUpdateToken(String userId, String token) {
        Optional<DeviceToken> existingTokenByUser = repository.findByUserId(userId);
        Optional<DeviceToken> existingTokenByToken = repository.findByToken(token);

        if (existingTokenByToken.isPresent()) {
            // Token already exists - just update userId if different
            DeviceToken deviceToken = existingTokenByToken.get();
            if (!deviceToken.getUserId().equals(userId)) {
                deviceToken.setUserId(userId);
                repository.save(deviceToken);
            }
        } else if (existingTokenByUser.isPresent()) {
            // User has a different token saved, update it
            DeviceToken deviceToken = existingTokenByUser.get();
            deviceToken.setToken(token);
            repository.save(deviceToken);
        } else {
            // New token + user - save new entry
            repository.save(new DeviceToken(token, userId));
        }
    }
}