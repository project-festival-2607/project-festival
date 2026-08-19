package com.example.chook.admin.dto.board;

import com.example.chook.admin.entity.enums.inquiry.InquiryReplyStatus;
import com.example.chook.member.entity.enums.MemberRole;
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
  MemberRole memberRole;

  String title;

  LocalDateTime createdAt;
  LocalDateTime updatedAt;
  InquiryReplyStatus replyStatus;
  LocalDateTime repliedAt;

}
