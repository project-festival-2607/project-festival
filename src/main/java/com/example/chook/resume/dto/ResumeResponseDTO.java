package com.example.chook.resume.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResumeResponseDTO {

    // 이력서 아이디
    private Long id;

    // 회원 식별자
    private Long memberId;

    // 자기소개
    private String introduction;

    // 최종 수정일
    private LocalDateTime savedAt;

    // 연관된 파일 및 하위 리스트 정보
    private ProfileFileDTO profileFile;
    private List<ResumeCareerDTO> careers;
    private List<ResumePortfolioDTO> portfolios;
}
