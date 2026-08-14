package com.example.chook.member.service;

import com.example.chook.member.PhoneVerificationTokenProvider;
import com.solapi.sdk.SolapiClient;
import com.solapi.sdk.message.exception.SolapiMessageNotReceivedException;
import com.solapi.sdk.message.model.Message;
import com.solapi.sdk.message.service.DefaultMessageService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;

@Service
@Slf4j
public class PhoneVerificationServiceImpl implements PhoneVerificationService {

    private static final String SESSION_PHONE_KEY = "phoneVerificationPhone";
    private static final String SESSION_CODE_KEY = "phoneVerificationCode";
    private static final String SESSION_EXPIRY_KEY = "phoneVerificationExpiresAt";
    private static final long CODE_EXPIRY_SECONDS = 180; // 3분

    private final DefaultMessageService messageService;
    private final String senderNumber;
    private final PhoneVerificationTokenProvider tokenProvider;
    private final SecureRandom random = new SecureRandom();

    public PhoneVerificationServiceImpl(
            @Value("${solapi.api-key}") String apiKey,
            @Value("${solapi.api-secret}") String apiSecret,
            @Value("${solapi.sender-number}") String senderNumber,
            PhoneVerificationTokenProvider tokenProvider
    ) {
        this.messageService = SolapiClient.INSTANCE.createInstance(apiKey, apiSecret);
        this.senderNumber = senderNumber;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void sendCode(String phone, HttpSession session) {
        String code = String.format("%06d", random.nextInt(1_000_000));

        Message message = new Message();
        message.setFrom(senderNumber);
        message.setTo(phone);
        message.setText("[chook] 인증번호는 " + code + " 입니다.");

        try {
            messageService.send(message);
        } catch (SolapiMessageNotReceivedException e) {
            log.error("문자 발송 실패: {}", e.getFailedMessageList(), e);
            throw new IllegalStateException("인증번호 발송에 실패했습니다.");
        } catch (Exception e) {
            log.error("문자 발송 실패", e);
            throw new IllegalStateException("인증번호 발송에 실패했습니다.");
        }

        session.setAttribute(SESSION_PHONE_KEY, phone);
        session.setAttribute(SESSION_CODE_KEY, code);
        session.setAttribute(SESSION_EXPIRY_KEY, Instant.now().getEpochSecond() + CODE_EXPIRY_SECONDS);
    }

    @Override
    public String verifyCode(String phone, String code, HttpSession session) {
        String sessionPhone = (String) session.getAttribute(SESSION_PHONE_KEY);
        String sessionCode = (String) session.getAttribute(SESSION_CODE_KEY);
        Long expiresAt = (Long) session.getAttribute(SESSION_EXPIRY_KEY);

        if (sessionPhone == null || sessionCode == null || expiresAt == null) {
            throw new IllegalArgumentException("인증번호를 먼저 요청해주세요.");
        }
        if (Instant.now().getEpochSecond() > expiresAt) {
            throw new IllegalArgumentException("인증번호가 만료되었습니다.");
        }
        if (!sessionPhone.equals(phone) || !sessionCode.equals(code)) {
            throw new IllegalArgumentException("인증번호가 일치하지 않습니다.");
        }

        session.removeAttribute(SESSION_PHONE_KEY);
        session.removeAttribute(SESSION_CODE_KEY);
        session.removeAttribute(SESSION_EXPIRY_KEY);

        return tokenProvider.sign(phone);
    }

}