package com.studio.booking.configs;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Value("${JWT_HEADER}")
    private String jwtHeader;

    @Value("${JWT_KEY}")
    private String jwtKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = request.getHeader(jwtHeader);
        if (jwt != null) {
            jwt = jwt.substring(7);

            try {

                SecretKey key = Keys.hmacShaKeyFor(jwtKey.getBytes());
                Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt).getPayload();
                String identifier = claims.get("identifier", String.class);
                String role = claims.get("authorities", String.class);
                updateAuthentication(identifier, role);

            } catch (ExpiredJwtException exception) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "The token is expired!");
            } catch (MalformedJwtException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "The token is not valid!");
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token validation failed");
            }
        }
        filterChain.doFilter(request, response);
    }

    private void updateAuthentication(String username, String role) {
        List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(role);
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        AntPathMatcher matcher = new AntPathMatcher();
        return Arrays
                .stream(WHITELIST)
                .anyMatch(p -> matcher.match(p, request.getRequestURI()));
    }

    public final String[] WHITELIST = {
            "/auth/google/android-callback",
            "/api/payments/vnpay/callback"
    };
}
