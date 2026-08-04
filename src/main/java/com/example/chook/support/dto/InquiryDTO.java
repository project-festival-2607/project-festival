package com.example.chook.support.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class InquiryDTO {
    private Long ino;
    private Long id; //user 테이블에서 id => 외래키
    private String title;
    private String content;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime commentTime;
}
