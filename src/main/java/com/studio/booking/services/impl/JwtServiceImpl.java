package com.studio.booking.services.impl;

import com.studio.booking.services.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    @Value("${JWT_KEY}")
    private String jwtKey;

    @Value("${TTL_ACCESS_TOKEN}")
    private Long tokenTimeToLive;

    @Override
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        return Jwts.builder()
                .issuedAt(new Date())
                .subject("Access Token")
                .expiration(new Date(new Date().getTime() + (1000 * 60 * tokenTimeToLive)))
                .claim("identifier", userDetails.getUsername())
                .claim("authorities", populateAuthorities(authorities))
                .signWith(getSecretKey())
                .compact();
    }

    @Override
    public String getIdentifierFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey()).build()
                .parseSignedClaims(token.substring(7))
                .getPayload()
                .get("identifier", String.class);
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtKey.getBytes());
    }

    private String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<String> roles = new HashSet<>();
        for (GrantedAuthority authority : authorities) {
            roles.add(authority.getAuthority());
        }
        return String.join(",", roles);
    }
}
