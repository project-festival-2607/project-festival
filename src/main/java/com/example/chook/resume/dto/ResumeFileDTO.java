package com.example.chook.resume.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResumeFileDTO {

    // 식별자
    private Long id;

    // 파일 아이디
    private String fileUuid;

    // 화면 표시용 원본 파일명
    private String originalFileName;
}
