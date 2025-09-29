package com.studio.booking.utils;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;
import org.springframework.context.annotation.Profile;

@Profile("!ci")
public class DotenvLoader {
    public static void loadEnv() {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .filename(".env")
                    .load();
            dotenv.entries().forEach(entry -> {
                System.setProperty(entry.getKey(), entry.getValue());
            });
        } catch (DotenvException e) {
            System.out.println("This is not an error because env will be loaded by third party");
        }
    }
}
