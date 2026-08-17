package com.example.chook.admin.dto.board;

import com.example.chook.admin.entity.enums.inquiry.InquiryReplyStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminInquiryTableDTO {

  Long ino;

  Long memberId;
  String memberUsername;
  String memberName;

  String title;

  LocalDateTime createdAt;
  LocalDateTime updatedAt;
  InquiryReplyStatus inquiryStatus;
  LocalDateTime repliedAt;

}
