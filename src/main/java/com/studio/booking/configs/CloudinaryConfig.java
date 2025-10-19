package com.studio.booking.configs;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {
    @Value("${CLOUD_NAME}")
    private String cloudName;

    @Value("${CLOUD_API_KEY}")
    private String cloudApiKey;

    @Value("${CLOUD_API_SECRET}")
    private String cloudApiSecret;

    @Bean
    public Cloudinary cloudinary() {
        Map<Object, Object> configs = new HashMap<>();
        configs.put("cloud_name", cloudName);
        configs.put("api_key", cloudApiKey);
        configs.put("api_secret", cloudApiSecret);
        configs.put("secure", true);
        return new Cloudinary(configs);
    }
}
