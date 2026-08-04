package com.example.chook.resume.dto;

import com.example.chook.resume.entity.enums.ResumePortfolioType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResumePortfolioDTO {

    // 포트폴리오 아이디
    private Long id;

    // 타입
    private ResumePortfolioType type;

    // 제목
    private String title;

    // 등록일
    private LocalDateTime registeredAt;

    // 포트폴리오와 1:1 관계인 첨부파일 정보
    private ResumeFileDTO resumeFile;

}
