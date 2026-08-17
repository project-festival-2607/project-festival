package com.example.chook.admin.entity.enums.inquiry;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum InquiryReplyStatus {

  REPLIED("답변 완료"),
  NOT_REPLIED("답변 대기");

  private final String label;

}
