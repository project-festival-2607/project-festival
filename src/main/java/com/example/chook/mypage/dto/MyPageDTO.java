package com.example.chook.mypage.dto;

import com.example.chook.member.entity.enums.Gender;
import com.example.chook.member.entity.enums.MemberRole;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyPageDTO {

    // 공통
    private String username;
    private String name;
    private String phone;
    private String email;
    private Long point;
    private MemberRole role;
    private boolean socialSignUp;
    private String businessNumber;

    // JobSeeker 전용 (JOB_SEEKER/JOB_EQUIP만 값 있음)
    private Gender gender;
    private LocalDate birthDate;
    private String streetAddress;
    private String detailAddress;

    // Employer 전용 (RECRUITER만 값 있음)
    private String companyName;
    private String ceoName;
    private LocalDate foundedAt;

}