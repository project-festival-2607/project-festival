package com.example.chook.admin.entity.enums.jobequip;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum JobEquipKeywordType {

  USERNAME("아이디"),
  NAME("이름"),
  PHONE("전화번호"),
  EMAIL("이메일"),
  BUSINESS_NUMBER("사업자등록번호"),
  ADDRESS("주소", false);

  private final String label;
  private final boolean exactMatchSupported;

  JobEquipKeywordType(String label) {
    this(label, true);
  }

}
