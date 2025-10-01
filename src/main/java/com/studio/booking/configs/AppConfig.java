package com.studio.booking.configs;

import com.studio.booking.entities.Account;
import com.studio.booking.enums.AccountRole;
import com.studio.booking.enums.AccountStatus;
import com.studio.booking.repositories.AccountRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    @Value("${ADMIN_EMAIL}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;

    @Autowired
    private AccountRepo accountRepo;

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    ApplicationRunner runner() {
        return args -> {
            // Create account admin
            Account adminAccount = accountRepo.findByEmail(adminEmail);

            if (adminAccount == null) {
                adminAccount = Account.builder()
                        .email(adminEmail)
                        .username("admin")
                        .fullName("Admin")
                        .password(passwordEncoder().encode(adminPassword))
                        .role(AccountRole.ADMIN)
                        .status(AccountStatus.ACTIVE)
                        .build();
                accountRepo.save(adminAccount);
            }
        };
    }
}
