package com.example.chook.admin.entity.enums.inquiry;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum InquiryDateRangeType {

  CREATED_AT("작성일시"),
  UPDATED_AT("수정일시"),
  REPLIED_AT("답변일시");

  private final String label;

}
