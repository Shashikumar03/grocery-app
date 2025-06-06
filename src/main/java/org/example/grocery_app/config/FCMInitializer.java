package org.example.grocery_app.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class FCMInitializer {
    @PostConstruct
    public void initialize() {
        try {
            InputStream serviceAccount = new ClassPathResource("src/main/resources/serviceAccountKey.json").getInputStream();

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setStorageBucket("grocery-app-6fe52.appspot.com") // <--- Add your bucket name here
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("✅ Firebase has been initialized.");
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to initialize FirebaseApp: " + e.getMessage());
        }
    }
}
