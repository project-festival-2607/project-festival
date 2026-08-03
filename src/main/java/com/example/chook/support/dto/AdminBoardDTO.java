package com.example.chook.support.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminBoardDTO {
    private Long bno;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private Boolean highlight;
}
