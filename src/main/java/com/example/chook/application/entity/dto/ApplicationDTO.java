package com.example.chook.application.entity.dto;

import com.example.chook.application.entity.enums.ApplicationResult;
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
    private Long Id;

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
}
