package com.studio.booking.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
@EnableWebSecurity
@OpenAPIDefinition(info = @Info(
        title = "BookingStudio API",
        version = "1.0",
        description = "Information")
)
@SecurityScheme(
        name = "BearerAuth",
        scheme = "bearer",
        bearerFormat = "JWT",
        type = SecuritySchemeType.HTTP,
        in = SecuritySchemeIn.HEADER
)

public class SecurityConfig {
    private final JwtFilter jwtFilter;

    @Value("${JWT_HEADER}")
    private String jwtHeader;

    @Value("${FRONT_END_URL}")
    private String frontEndUrl;


    /// Process for Google Id Token From App
    @Bean
    @Order(1)
    public SecurityFilterChain googleResourceServerFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/auth/google/android-callback")
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(m -> m.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll()
                )
                // Kích hoạt Resource Server JWT để xử lý Google ID Token
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> {
                            // Cấu hình Google Issuer URI
                            jwt.decoder(jwtDecoder());
                        })
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return http.build();
    }

    /// Separate JwtDecoder to config Google Issuer
    @Bean
    public JwtDecoder jwtDecoder() {
        String jwksUri = "https://www.googleapis.com/oauth2/v3/certs";
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwksUri).build();

        // 1. Tạo trình xác thực tiêu chuẩn (Issuer và Time)
        OAuth2TokenValidator<Jwt> defaultValidators = JwtValidators.createDefaultWithIssuer("https://accounts.google.com");

        // 2. Kết hợp các Validator
        jwtDecoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(defaultValidators));

        return jwtDecoder;
    }

    @Bean
    @Order(2)
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement(m -> m.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/studio-types",
                                "/api/locations",
                                "/api/services/**",
                                "/api/studios/**",
                                "/api/price-tables/**",
                                "/api/price-items/**",
                                "/api/price-rules/**",
                                "/api/payments/vnpay/callback"
                        ).permitAll()
//                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        ;
        return http.build();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowCredentials(true);
            config.setAllowedOrigins(Arrays.asList(
                    frontEndUrl,
                    "http://localhost:3000",
                    "http://10.0.2.2:8080",
                    "http://127.0.0.1:8080"
            ));
            config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            config.setAllowedHeaders(Collections.singletonList("*"));
            config.setExposedHeaders(Collections.singletonList(jwtHeader));
            config.setMaxAge(3600L);
            return config;
        };
    }
}
