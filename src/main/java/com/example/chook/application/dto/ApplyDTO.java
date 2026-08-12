package com.example.chook.application.dto;

import com.example.chook.recruitment.dto.RecruitmentResponseDTO;
import com.example.chook.resume.dto.ResumeResponseDTO;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyDTO {

    // 회원
    private Long memberId;
    private String name;
    private String phone;
    private String email;

    // 공고
    private RecruitmentResponseDTO recruitment;

    // 이력서
    private ResumeResponseDTO resume;
}
