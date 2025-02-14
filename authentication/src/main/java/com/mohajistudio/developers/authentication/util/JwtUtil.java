package com.mohajistudio.developers.authentication.util;

import com.mohajistudio.developers.authentication.config.JwtProperties;
import com.mohajistudio.developers.common.dto.GeneratedToken;
import com.mohajistudio.developers.database.dto.UserDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final JwtProperties jwtProperties;
    private SecretKey secretKey;

    @PostConstruct
    protected void init() {
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes());
    }

    public GeneratedToken generateToken(UserDto user) {
        String accessToken = generateToken(user, jwtProperties.getAccessTokenPeriod());
        String refreshToken = generateToken(user, jwtProperties.getRefreshTokenPeriod());

        return new GeneratedToken(accessToken, refreshToken);
    }

    private String generateToken(UserDto user, long expirationPeriod) {
        ClaimsBuilder claimsBuilder = Jwts.claims();

        claimsBuilder.add("user", user);
        Claims claims = claimsBuilder.build();

        return Jwts.builder()
                .issuedAt(new Date())
                .issuer(jwtProperties.getIssuer())
                .expiration(new Date(System.currentTimeMillis() + expirationPeriod))
                .signWith(secretKey)
                .claims(claims)
                .compact();
    }

    public Map<String, Object> extractPayload(String token) {
        try {
            Claims payload = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Map<String, Object> extractedMap = new HashMap<>();
            for (String key : payload.keySet()) {
                extractedMap.put(key, payload.get(key));
            }

            return extractedMap;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            throw new JwtException("잘못된 JWT 서명", e);
        } catch (ExpiredJwtException e) {
            throw new JwtException("만료된 JWT 토큰", e);
        } catch (UnsupportedJwtException e) {
            throw new JwtException("지원되지 않는 JWT 토큰", e);
        } catch (IllegalArgumentException e) {
            throw new JwtException("잘못된 JWT 토큰", e);
        }
    }
}
