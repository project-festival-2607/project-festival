package com.example.chook.member.service;

import jakarta.servlet.http.HttpSession;

public interface PhoneVerificationService {

    void sendCode(String phone, HttpSession session);

    String verifyCode(String phone, String code, HttpSession session);

    // 문자 인증 유지 캐시 메서드
    void markVerified(String phone, HttpSession session);
    boolean isVerified(String phone, HttpSession session);
}