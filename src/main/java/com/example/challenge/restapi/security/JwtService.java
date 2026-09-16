package com.example.challenge.restapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * Responsável por gerar, validar e extrair informações de tokens JWT.
 * Chave e tempo de expiração configurados em application.properties (jwt.secret, jwt.expiration-ms).
 */
@Service
public class JwtService {

    private static final int MIN_KEY_BITS = 256;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    /**
     * Valida a chave assim que a aplicação sobe, em vez de deixar o erro
     * estourar só na primeira tentativa de login (WeakKeyException).
     */
    @PostConstruct
    void validateSecret() {
        int bits = hexStringToByteArray(secret).length * 8;
        if (bits < MIN_KEY_BITS) {
            throw new IllegalStateException(
                    "jwt.secret muito curto: tem %d bits, o mínimo exigido para HS256 é %d bits (64 caracteres hexadecimais). Gere uma nova chave."
                            .formatted(bits, MIN_KEY_BITS));
        }
    }

    private SecretKey getSigningKey() {
        // jwt.secret é uma string hexadecimal; convertemos para bytes antes de gerar a chave.
        byte[] keyBytes = hexStringToByteArray(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private byte[] hexStringToByteArray(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    public String generateToken(UserDetails userDetails, Map<String, Object> extraClaims) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    public long getExpirationMs() {
        return expirationMs;
    }
}