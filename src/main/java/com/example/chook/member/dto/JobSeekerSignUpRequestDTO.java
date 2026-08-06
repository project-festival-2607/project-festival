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
public class JobSeekerSignUpRequestDTO {

    // 공통 회원정보
    private String userName;
    private String password;
    private String name;
    private String phone;
    private String email;

    // 구직자 전용 프로필
    private Gender gender;
    private LocalDate birthDate;
    private String streetAddress;
    private String detailAddress;

    // JOB_EQUIP 전환용 옵션
    private String businessNumber;
    private String verificationToken;

}
