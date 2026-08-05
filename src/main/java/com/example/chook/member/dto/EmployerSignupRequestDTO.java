package com.example.chook.member.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployerSignupRequestDTO {

    // 공통 회원정보
    private String username;
    private String password;
    private String name;
    private String phone;
    private String email;

    // 구인자 전용 프로필
    private String companyName;
    private String ceoName;
    private String streetAddress;
    private String detailAddress;
    private LocalDate foundedAt;

    // 사업자등록번호(필수)
    private String businessNumber;
    private String verificationToken;

}
