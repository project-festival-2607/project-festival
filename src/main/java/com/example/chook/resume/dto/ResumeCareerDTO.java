package com.example.chook.resume.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResumeCareerDTO {

    // 경력사항 아이디
    private Long id;

    // 회사명 / 행사명
    private String careerName;

    // 입사연월
    private LocalDate startDate;

    // 퇴사연월
    private LocalDate endDate;

    // 담당업무
    private String duties;
}
