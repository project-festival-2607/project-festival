package com.example.chook.admin.enums.member.jobseeker;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum JobSeekerDateRangeType {

  CREATED_AT("생성일시"),
  UPDATED_AT("수정일시"),
  LAST_LOGIN_AT("최근접속일시"),
  DELETED_AT("삭제일시"),
  BIRTH_DATE("생년월일", true);

  private final String label;
  private final boolean dateOnly;

  JobSeekerDateRangeType(String label) {
    this(label, false);
  }

}
