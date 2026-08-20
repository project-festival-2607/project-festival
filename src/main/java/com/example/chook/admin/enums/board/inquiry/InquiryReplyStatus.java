package com.example.chook.admin.enums.board.inquiry;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum InquiryReplyStatus {

  REPLIED("답변 완료", "inquiry-replied"),
  NOT_REPLIED("답변 대기", "inquiry-not-replied");

  private final String label;
  private final String badgeClass;

}
