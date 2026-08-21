package com.example.chook.support.dto;

import com.example.chook.file.dto.FileDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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
    private List<FileDTO> files; // 첨부파일 목록 (상세 조회 시에만 채워짐)
}
