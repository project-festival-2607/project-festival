package com.example.chook.member.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobSeekerProfileUpdateRequestDTO {

    private String phone; // 휴대폰 인증 구현 전까지 처리 보류
    private String phoneVerificationToken; // 휴대폰 인증 구현 전까지 처리 보류
    private String email;
    private String streetAddress;
    private String detailAddress;
    private String businessNumber;
    private String verificationToken;

}
