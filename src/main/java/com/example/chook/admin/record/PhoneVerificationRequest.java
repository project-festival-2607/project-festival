package com.example.chook.admin.record;

public record PhoneVerificationRequest(
  String phone,
  String phoneVerify
) {
}