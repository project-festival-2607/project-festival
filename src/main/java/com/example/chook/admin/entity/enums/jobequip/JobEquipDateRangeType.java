package com.example.chook.admin.entity.enums.jobequip;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum JobEquipDateRangeType {

  CREATED_AT("생성일시"),
  UPDATED_AT("수정일시"),
  LAST_LOGIN_AT("최근접속일시"),
  DELETED_AT("삭제일시"),
  BIRTH_DATE("생년월일", true),
  BUSINESS_NUMBER_VERIFIED_AT("인증일시");

  private final String label;
  private final boolean dateOnly;

  JobEquipDateRangeType(String label) {
    this(label, false);
  }

}
