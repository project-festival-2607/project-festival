package com.example.chook.admin.entity.enums.recruiter;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RecruiterKeywordType {

  USERNAME("아이디"),
  NAME("이름"),
  PHONE("전화번호"),
  EMAIL("이메일"),
  COMPANY_NAME("상호명"),
  CEO_NAME("대표자명"),
  BUSINESS_NUMBER("사업자등록번호"),
  ADDRESS("주소", false);

  private final String label;
  private final boolean exactMatchSupported;

  RecruiterKeywordType(String label) {
    this(label, true);
  }

}
