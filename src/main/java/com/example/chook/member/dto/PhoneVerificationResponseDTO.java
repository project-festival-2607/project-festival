package com.example.chook.member.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhoneVerificationResponseDTO {
    private boolean success;
    private String verificationToken; // 확인 성공 시에만 값 있음
    private String message;
}