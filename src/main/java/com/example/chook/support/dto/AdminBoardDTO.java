package com.example.chook.support.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    // 목록 번호칸 표시용 (하이라이트면 "중요", 아니면 화면상 순번) - bno와 무관하게 렌더링 시점에 채워짐
    private String displayNo;

    public String getCreatedAtStr() {
        return createdAt == null ? "" : createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
