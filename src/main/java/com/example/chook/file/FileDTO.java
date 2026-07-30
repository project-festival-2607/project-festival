package com.example.chook.file;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileDTO {

  private String uuid;
  private String originalName;
  private String saveDir;
  private Long fileSize;
  private LocalDateTime uploadedAt;

}
