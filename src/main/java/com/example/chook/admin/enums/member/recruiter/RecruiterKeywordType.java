package com.example.chook.admin.enums.member.recruiter;

import com.example.chook.admin.enums.KeywordCriteria;
import com.example.chook.admin.enums.KeywordType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;

@AllArgsConstructor
@Getter
public enum RecruiterKeywordType implements KeywordType {

  USERNAME("아이디"),
  NAME("이름"),
  PHONE("전화번호"),
  EMAIL("이메일"),
  COMPANY_NAME("상호명"),
  CEO_NAME("대표자명"),
  BUSINESS_NUMBER("사업자등록번호"),
  ADDRESS("주소", EnumSet.of(
    KeywordCriteria.ALL_WORDS_CONTAINS,
    KeywordCriteria.PHRASE_CONTAINS
  ));

  private final String label;
  private final EnumSet<KeywordCriteria> supportedCriteria;

  RecruiterKeywordType(String label) {
    this(label, EnumSet.of(
      KeywordCriteria.ALL_WORDS_CONTAINS,
      KeywordCriteria.PHRASE_CONTAINS,
      KeywordCriteria.EXACT
    ));
  }

}
