package org.example.grocery_app.serviceImplementation;


//import com.example.grocery_app.model.DeviceToken;
//import com.example.grocery_app.repository.DeviceTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.example.grocery_app.dto.ExpoPushMessageDto;
import org.example.grocery_app.entities.DeviceToken;
import org.example.grocery_app.entities.User;
import org.example.grocery_app.exception.ResourceNotFoundException;
import org.example.grocery_app.repository.DeviceTokenRepository;
import org.example.grocery_app.repository.UserRepository;
import org.example.grocery_app.service.DeviceTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DeviceTokenServiceImp implements DeviceTokenService {

//    private final DeviceTokenRepository tokenRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeviceTokenRepository deviceTokenRepository;

    @Override
    public void saveOrUpdateToken(String userId, String token) {
        Optional<DeviceToken> existingTokenByUser = deviceTokenRepository.findByUserId(userId);
        Optional<DeviceToken> existingTokenByToken = deviceTokenRepository.findByToken(token);
        log.info("token find bu user: {}", existingTokenByUser);
        log.info("Existing token by token :{}", existingTokenByToken);
        if (existingTokenByToken.isPresent()) {
            // Token already exists - just update userId if different
            DeviceToken deviceToken = existingTokenByToken.get();
            if (!deviceToken.getUserId().equals(userId)) {
                deviceToken.setUserId(userId);
                deviceTokenRepository.save(deviceToken);
            }
        } else if (existingTokenByUser.isPresent()) {
            // User has a different token saved, update it
            DeviceToken deviceToken = existingTokenByUser.get();
            deviceToken.setToken(token);
            deviceTokenRepository.save(deviceToken);
        } else {

            // New token + user - save new entry
             DeviceToken deviceToken=new DeviceToken();
            try {
                long id = Long.parseLong(userId);
                System.out.println("Converted Long: " + id);
                User user = this.userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("user", "userId", id));
                deviceToken.setName(user.getName());
                deviceToken.setMobile(user.getPhoneNumber());
                deviceToken.setToken(token);
                deviceToken.setUserId(userId);
                deviceTokenRepository.save(deviceToken);
            } catch (NumberFormatException e) {
                System.err.println("Invalid number format: " + userId);
            }

        }
    }

    @Override
    public void sendPushToAllUsers(String title, String body, Map<String, Object> data) {
        List<String> tokens = deviceTokenRepository.findAllTokens();

        List<ExpoPushMessageDto> messages = tokens.stream().map(token -> {
            ExpoPushMessageDto msg = new ExpoPushMessageDto();
            msg.setTo(token);
            msg.setTitle(title);
            msg.setBody(body);
            msg.setData(data);
            return msg;
        }).collect(Collectors.toList());

        List<List<ExpoPushMessageDto>> chunks = chunkList(messages, 100);

        for (List<ExpoPushMessageDto> batch : chunks) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<List<ExpoPushMessageDto>> request = new HttpEntity<>(batch, headers);

                ResponseEntity<String> response = restTemplate.postForEntity(
                        "https://exp.host/--/api/v2/push/send",
                        request,
                        String.class
                );

                System.out.println("Expo response: " + response.getBody());
            } catch (Exception e) {
                System.err.println("Push notification error: " + e.getMessage());
            }
        }
    }
    public void sendPushToUser(String userId, String title, String body, Map<String, Object> data) {
        Optional<DeviceToken> optionalDeviceToken = deviceTokenRepository.findByUserId(userId);

        if (optionalDeviceToken.isPresent()) {
            DeviceToken deviceToken = optionalDeviceToken.get();
            ExpoPushMessageDto message = new ExpoPushMessageDto();
            message.setTo(deviceToken.getToken());
            message.setTitle(title);
            message.setBody(body);
            message.setData(data);

            sendPushMessage(List.of(message)); // helper method to send
        } else {
            System.err.println("No device token found for userId: " + userId);
        }
    }


    private <T> List<List<T>> chunkList(List<T> list, int size) {
            List<List<T>> chunks = new ArrayList<>();
            for (int i = 0; i < list.size(); i += size) {
                chunks.add(list.subList(i, Math.min(i + size, list.size())));
            }
            return chunks;
        }
    private void sendPushMessage(List<ExpoPushMessageDto> messages) {
        List<List<ExpoPushMessageDto>> chunks = chunkList(messages, 100);

        for (List<ExpoPushMessageDto> batch : chunks) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<List<ExpoPushMessageDto>> request = new HttpEntity<>(batch, headers);

                ResponseEntity<String> response = restTemplate.postForEntity(
                        "https://exp.host/--/api/v2/push/send",
                        request,
                        String.class
                );

                System.out.println("Expo response: " + response.getBody());
            } catch (Exception e) {
                System.err.println("Push notification error: " + e.getMessage());
            }
        }
    }

}