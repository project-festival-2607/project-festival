package com.example.chook.admin.dto.board;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoticeTableDTO {

  Long bno;
  String title;
  LocalDateTime createdAt;
  Boolean highlight;

}
