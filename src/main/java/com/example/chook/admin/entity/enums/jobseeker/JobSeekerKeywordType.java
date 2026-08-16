package com.example.chook.admin.entity.enums.jobseeker;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum JobSeekerKeywordType {

  USERNAME("아이디"),
  NAME("이름"),
  PHONE("전화번호"),
  EMAIL("이메일"),
  ADDRESS("주소", false);

  private final String label;
  private final boolean exactMatchSupported;

  JobSeekerKeywordType(String label) {
    this(label, true);
  }

}
