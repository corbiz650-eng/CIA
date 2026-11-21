package com.civicportal.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.core.JacksonException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

@Service
public class JwtService {

    private static final String ALGORITHM = "HmacSHA256";
    private static final String JWT_ALGORITHM = "HS256";

    private final byte[] signingKey;
    private final long expirationMs;
    private final ObjectMapper objectMapper;

    public JwtService(
            @Value("${app.jwt.secret}") String jwtSecret,
            @Value("${app.jwt.expiration-ms:86400000}") long expirationMs,
            ObjectMapper objectMapper
    ) {
        try {
            this.signingKey = Base64.getDecoder().decode(jwtSecret);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "app.jwt.secret must be a Base64-encoded key",
                    exception
            );
        }

        if (signingKey.length < 32) {
            throw new IllegalArgumentException(
                    "JWT signing key must contain at least 32 bytes"
            );
        }

        if (expirationMs <= 0) {
            throw new IllegalArgumentException(
                    "JWT expiration must be greater than zero"
            );
        }

        this.expirationMs = expirationMs;
        this.objectMapper = objectMapper;
    }

    public String generateToken(String subject) {
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("JWT subject is required");
        }

        try {
            String header = encodeJson(Map.of(
                    "alg", JWT_ALGORITHM,
                    "typ", "JWT"
            ));

            Instant now = Instant.now();
            Instant expiresAt = now.plusMillis(expirationMs);

            String payload = encodeJson(Map.of(
                    "sub", subject,
                    "iat", now.getEpochSecond(),
                    "exp", expiresAt.getEpochSecond()
            ));

            String unsignedToken = header + "." + payload;
            String signature = Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(sign(unsignedToken));

            return unsignedToken + "." + signature;

        } catch (JacksonException exception) {
            throw new IllegalStateException("Could not create JWT", exception);
        }
    }

    public String extractUsername(String token) {
        return readClaims(token).path("sub").asString();
    }

    public boolean isTokenValid(String token, String expectedUsername) {
        if (expectedUsername == null || expectedUsername.isBlank()) {
            return false;
        }

        try {
            JsonNode claims = readClaims(token);
            return claims.path("sub").asString().equals(expectedUsername);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private JsonNode readClaims(String token) {
        if (token == null) {
            throw new IllegalArgumentException("JWT is required");
        }

        String[] parts = token.split("\\.", -1);
        if (parts.length != 3) {
            throw new IllegalArgumentException("Malformed JWT");
        }

        try {
            JsonNode header = objectMapper.readTree(
                    Base64.getUrlDecoder().decode(parts[0])
            );

            if (!JWT_ALGORITHM.equals(header.path("alg").asString())) {
                throw new IllegalArgumentException("Unsupported JWT algorithm");
            }

            String unsignedToken = parts[0] + "." + parts[1];
            byte[] suppliedSignature = Base64.getUrlDecoder().decode(parts[2]);
            byte[] expectedSignature = sign(unsignedToken);

            if (!MessageDigest.isEqual(suppliedSignature, expectedSignature)) {
                throw new IllegalArgumentException("Invalid JWT signature");
            }

            JsonNode claims = objectMapper.readTree(
                    Base64.getUrlDecoder().decode(parts[1])
            );

            long expiration = claims.path("exp").asLong(0);
            if (expiration <= Instant.now().getEpochSecond()) {
                throw new IllegalArgumentException("JWT has expired");
            }

            if (claims.path("sub").asString().isBlank()) {
                throw new IllegalArgumentException("JWT subject is missing");
            }

            return claims;
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid JWT", exception);
        }
    }

    private String encodeJson(Object value) throws JacksonException {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(objectMapper.writeValueAsBytes(value));
    }

    private byte[] sign(String value) {
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(new SecretKeySpec(signingKey, ALGORITHM));
            return mac.doFinal(value.getBytes(StandardCharsets.US_ASCII));
        } catch (Exception exception) {
            throw new IllegalStateException("Could not sign JWT", exception);
        }
    }
}
