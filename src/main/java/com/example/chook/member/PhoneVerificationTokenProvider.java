package com.example.chook.member;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;

@Component
public class PhoneVerificationTokenProvider {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final SecretKeySpec secretKey;
    private final long expirySeconds;

    public PhoneVerificationTokenProvider(
            @Value("${phone-verification.token-secret}") String secret,
            @Value("${phone-verification.token-expiry-seconds:600}") long expirySeconds
    ) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
        this.expirySeconds = expirySeconds;
    }

    public String sign(String phone) {
        long expiresAt = Instant.now().getEpochSecond() + expirySeconds;
        String payload = phone + ":" + expiresAt;
        return encode(payload.getBytes(StandardCharsets.UTF_8)) + "." + encode(hmac(payload));
    }

    public boolean verify(String phone, String token) {
        if (token == null || phone == null) return false;

        String[] parts = token.split("\\.", 2);
        if (parts.length != 2) return false;

        byte[] payloadBytes;
        try {
            payloadBytes = Base64.getUrlDecoder().decode(parts[0]);
        } catch (IllegalArgumentException e) {
            return false;
        }
        String payload = new String(payloadBytes, StandardCharsets.UTF_8);

        String expectedSignature = encode(hmac(payload));
        if (!MessageDigest.isEqual(
                expectedSignature.getBytes(StandardCharsets.UTF_8),
                parts[1].getBytes(StandardCharsets.UTF_8)
        )) {
            return false;
        }

        String[] payloadParts = payload.split(":", 2);
        if (payloadParts.length != 2) return false;

        String tokenPhone = payloadParts[0];
        long expiresAt;
        try {
            expiresAt = Long.parseLong(payloadParts[1]);
        } catch (NumberFormatException e) {
            return false;
        }

        if (!tokenPhone.equals(phone)) return false;
        return Instant.now().getEpochSecond() <= expiresAt;
    }

    private byte[] hmac(String payload) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(secretKey);
            return mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("HMAC 서명 생성에 실패했습니다.", e);
        }
    }

    private String encode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

}