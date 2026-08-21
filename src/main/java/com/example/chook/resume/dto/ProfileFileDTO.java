package com.example.chook.resume.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileFileDTO {

    // 식별자
    private Long id;

    // 파일 아이디
    private UUID uuid;

    // 화면 표시용 원본 파일명
    private String originalName;
}
