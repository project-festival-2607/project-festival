package com.example.chook.resume.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResumeRequestDTO {

    // 자기소개
    private String introduction;

    // 프로필 사진 UUID (UploadedFile의 UUID)
    private String profileFileUuid;

    // 하위 데이터 목록
    private List<ResumeCareerDTO> careers;
    private List<ResumePortfolioDTO> portfolios;
}
