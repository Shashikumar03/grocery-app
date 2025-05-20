package org.example.grocery_app.serviceImplementation;// package: org.example.grocery_app.serviceImplementation

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenBlacklistServiceImpl {

    private static final String PREFIX = "blacklisted_token:";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void blacklistToken(String token, long expirationInMillis) {
        String key = PREFIX + token;
        redisTemplate.opsForValue().set(key, "true", expirationInMillis, TimeUnit.MILLISECONDS);
    }

    public boolean isTokenBlacklisted(String token) {
        String key = PREFIX + token;
        return redisTemplate.hasKey(key);
    }
}
