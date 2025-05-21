package org.example.grocery_app.service;

import java.util.Map;

public interface DeviceTokenService {

    public void saveOrUpdateToken(String userId, String token);

    public void sendPushToAllUsers(String title, String body, Map<String, Object> data);
}
