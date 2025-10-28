package com.studio.booking.configs;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.studio.booking.exceptions.exceptions.MessagingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@Profile("!ci && !test")
@Configuration
public class FirebaseConfig {
    @Value("${FIREBASE_ADMIN_BASE64}")
    private String firebaseConfigBase64;

    /// Convert Firebase Admin SDK from Base64 Code to InputStream
    @PostConstruct
    public void initialize() {
        if (FirebaseApp.getApps().isEmpty()) {
            try {
                // 1. Decode Base64 to byte[]
                byte[] decodedBytes = Base64.getDecoder().decode(firebaseConfigBase64);

                // 2. Convert byte[] to InputStream
                InputStream serviceAccount = new ByteArrayInputStream(decodedBytes);

                // 3. Init Firebase by InputStream of JSON
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
                FirebaseApp.initializeApp(options);

            } catch (IOException e) {
                throw new MessagingException("Error initializing Firebase Admin SDK: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                throw new MessagingException("Invalid Base64 string: " + e.getMessage());
            }
        }
    }
}
