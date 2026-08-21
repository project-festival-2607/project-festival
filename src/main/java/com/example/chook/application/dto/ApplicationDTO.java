package com.example.chook.application.dto;

import com.example.chook.application.entity.enums.ApplicationResult;
import com.example.chook.recruitment.entity.enums.RecruitmentCategory;
import com.example.chook.resume.dto.ResumeResponseDTO;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicationDTO {

    // 지원 아이디
    private Long id;

    // 회원 식별자
    private Long memberId;

    // 모집공고 아이디
    private Long recruitmentId;

    // 이력서 아이디
    private Long resumeId;

    // 지원일시
    private LocalDateTime registerDate;

    // 열람일시
    private LocalDateTime readDate;

    // 결과
    private ApplicationResult result;

    // 행사 제목
    private String title;

    // 행사 주소
    private String address;

    // 지원자 정보
    private String name;

    // 모집공고 제목
    private String recruitmentTitle;

    // 모집공고 카테고리
    private RecruitmentCategory category;
}
