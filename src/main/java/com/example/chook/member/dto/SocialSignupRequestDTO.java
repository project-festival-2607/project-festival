package com.example.chook.member.dto;

import com.example.chook.member.entity.enums.Gender;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 소셜 로그인은 JobSeeker 전용
public class SocialSignupRequestDTO {

    private String email;
    private String phone;
    private Gender gender;
    private LocalDate birthDate;
    private String streetAddress;
    private String detailAddress;

    private String businessNumber;
    private String verificationToken;

}
