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
public class BusinessNumberTokenProvider {

    // HMAC 방식 중 SHA-256 해시 알고리즘을 사용하는 방식
    // 같은 secret과 같은 payload를 사용하면 항상 같은 서명이 만들어진다.
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    // HMAC 서명에 사용할 비밀키
    // 외부에서 직접 값을 변경할 수 없도록 final로 선언한다.
    private final SecretKeySpec secretKey;

    // 토큰이 발급된 후 몇 초 동안 유효한지를 저장한다.
    private final long expirySeconds;

    public BusinessNumberTokenProvider(
            // application.properties의
            // business-number.token-secret 값을 주입받는다.
            @Value("${business-number.token-secret}") String secret,

            // application.properties의
            // business-number.token-expiry-seconds 값을 주입받는다.
            // 값이 없으면 기본값으로 600초(10분)를 사용한다.
            @Value("${business-number.token-expiry-seconds:600}") long expirySeconds
    ) {
        // String으로 받은 비밀키를 UTF-8 바이트 배열로 변환한 뒤
        // HMAC-SHA256에서 사용할 SecretKeySpec 객체를 만든다.
        this.secretKey = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                HMAC_ALGORITHM
        );

        // 전달받은 만료시간을 필드에 저장한다.
        this.expirySeconds = expirySeconds;
    }

    // 사업자번호 인증 성공 시 호출
    // businessNumber와 만료시각을 하나의 payload로 만들고
    // 그 payload에 비밀키를 이용한 HMAC 서명을 붙여 토큰을 발급
    public String sign(String businessNumber) {

        // 현재 시간을 초 단위 Unix timestamp로 가져온다.
        // 여기에 설정된 유효시간을 더해 토큰의 만료시각을 만든다.
        long expiresAt = Instant.now().getEpochSecond() + expirySeconds;

        // 토큰에 넣을 실제 데이터 생성
        // 앞부분 = 사업자등록번호
        // 뒷부분 = 토큰 만료시각
        String payload = businessNumber + ":" + expiresAt;

        // payload를 Base64 URL-safe 문자열로 변환
        // Base64는 암호화가 아니라 데이터를 문자열 형태로 표현하기 위한 인코딩이다.
        String encodedPayload = encode(
                payload.getBytes(StandardCharsets.UTF_8)
        );

        // 원본 payload에 비밀키를 사용하여 HMAC-SHA256 서명을 생성
        // payload가 변경되면 서명도 달라진다.
        String signature = encode(hmac(payload));

        // 최종 토큰 형태
        // encodedPayload.signature
        return encodedPayload + "." + signature;
    }

    // 저장 시점에 호출
    // 전달받은 토큰이
    // 1. 올바른 형식인지
    // 2. 위조되지 않았는지
    // 3. 아직 만료되지 않았는지
    // 4. 현재 businessNumber와 일치하는지
    // 확인한다.
    public boolean verify(String businessNumber, String token) {

        // 사업자번호나 토큰이 없으면 검증할 수 없다.
        if (token == null || businessNumber == null) return false;

        // 토큰을 "."을 기준으로 두 부분으로 나눈다.
        //
        // [0] = Base64로 인코딩된 payload
        // [1] = HMAC 서명
        //
        // split("\\.", 2)에서 "\\"는 정규식에서
        // "."을 문자 그대로 사용하기 위한 표현이다.
        String[] parts = token.split("\\.", 2);

        // payload와 signature 두 부분이 모두 없으면 잘못된 토큰이다.
        if (parts.length != 2) return false;

        String encodedPayload = parts[0];
        String signature = parts[1];

        byte[] payloadBytes;

        try {
            // Base64로 인코딩되어 있던 payload를 다시 원래의 바이트 배열로 변환한다.
            payloadBytes = Base64.getUrlDecoder().decode(encodedPayload);
        } catch (IllegalArgumentException e) {

            // Base64 형식이 잘못된 경우 검증 실패
            return false;
        }

        // UTF-8 바이트 배열을 다시 문자열로 변환한다.
        //
        // 예:
        // "1234567890:1750000000"
        String payload = new String(payloadBytes, StandardCharsets.UTF_8);

        // 현재 서버가 가지고 있는 비밀키로
        // 전달받은 payload의 HMAC 서명을 다시 계산한다.
        //
        // 정상적인 토큰이라면 sign()에서 만들어진 signature와
        // 정확히 같은 값이 나와야 한다.
        String expectedSignature = encode(hmac(payload));

        // 전달받은 signature와 서버가 직접 계산한 signature를 비교한다.
        //
        // MessageDigest.isEqual()은 일반적인 문자열 비교보다
        // 타이밍 공격에 유리하도록 설계된 비교 방식이다.
        if (!MessageDigest.isEqual(
                expectedSignature.getBytes(StandardCharsets.UTF_8),
                signature.getBytes(StandardCharsets.UTF_8)
        )) {
            // 서명이 다르다는 것은
            // payload가 변경되었거나 비밀키를 모르는 사람이
            // 임의로 만든 토큰일 가능성이 있으므로 검증 실패
            return false;
        }

        // 검증이 끝난 payload를 ":" 기준으로 나눈다.
        //
        // [0] = 사업자등록번호
        // [1] = 만료시각
        String[] payloadParts = payload.split(":", 2);

        // 사업자번호와 만료시각 두 부분이 모두 있어야 한다.
        if (payloadParts.length != 2) return false;

        String tokenBusinessNumber = payloadParts[0];

        // 문자열로 저장되어 있던 만료시각을 long으로 변환한다.
        long expiresAt;

        try {
            expiresAt = Long.parseLong(payloadParts[1]);
        } catch (NumberFormatException e) {

            // 만료시각 부분이 숫자가 아니면 잘못된 토큰이다.
            return false;
        }

        // 토큰 안에 들어있는 사업자번호와
        // 현재 검증하려는 사업자번호가 같은지 확인
        if (!tokenBusinessNumber.equals(businessNumber)) return false;

        // 현재 시간이 만료시각보다 지나갔는지 확인
        // 현재 시간이 만료시각보다 크면 만료된 토큰
        if (Instant.now().getEpochSecond() > expiresAt) return false;

        // 모든 검증을 통과했으므로 유효한 토큰
        return true;
    }

    // 전달받은 payload에 HMAC-SHA256 알고리즘을 적용하여
    // 위조 여부를 확인할 수 있는 서명을 생성
    private byte[] hmac(String payload) {

        try {
            // 지정한 알고리즘(HmacSHA256)을 사용하는 Mac 객체 생성
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);

            // 서버가 가지고 있는 비밀키를 Mac 객체에 설정
            mac.init(secretKey);

            // payload를 UTF-8 바이트로 변환하고
            // 비밀키와 함께 HMAC-SHA256 계산
            return mac.doFinal(
                    payload.getBytes(StandardCharsets.UTF_8)
            );

        } catch (Exception e) {

            // 정상적인 환경에서는 발생하기 어려운 예외이므로
            // 애플리케이션 내부 오류로 변환해서 전달
            throw new IllegalStateException("HMAC 서명 생성에 실패했습니다.", e);
        }
    }

    // 바이트 배열을 Base64 URL-safe 문자열로 변환한다.
    //
    // withoutPadding()을 사용하기 때문에
    // Base64 끝에 붙는 "=" 패딩 문자가 제거된다.
    //
    // 여기서 Base64는 암호화가 아니라
    // 바이트 데이터를 문자열로 표현하기 위한 인코딩이다.
    private String encode(byte[] bytes) {

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

}
