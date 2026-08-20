package com.example.chook.admin.enums.member.recruiter;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RecruiterDateRangeType {

  CREATED_AT("생성일시"),
  UPDATED_AT("수정일시"),
  LAST_LOGIN_AT("최근접속일시"),
  DELETED_AT("삭제일시"),
  FOUNDED_AT("설립일", true),
  BUSINESS_NUMBER_VERIFIED_AT("인증일시");

  private final String label;
  private final boolean dateOnly;

  RecruiterDateRangeType(String label) {
    this(label, false);
  }

}
