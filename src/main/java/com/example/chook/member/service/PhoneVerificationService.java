package com.example.chook.member.service;

import jakarta.servlet.http.HttpSession;

public interface PhoneVerificationService {

    void sendCode(String phone, HttpSession session);

    String verifyCode(String phone, String code, HttpSession session);

}