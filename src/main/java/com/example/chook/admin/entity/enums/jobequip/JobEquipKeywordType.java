package com.example.chook.admin.entity.enums.jobequip;

import com.example.chook.admin.entity.enums.KeywordCriteria;
import com.example.chook.admin.entity.enums.KeywordType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;

@AllArgsConstructor
@Getter
public enum JobEquipKeywordType implements KeywordType {

  USERNAME("아이디"),
  NAME("이름"),
  PHONE("전화번호"),
  EMAIL("이메일"),
  BUSINESS_NUMBER("사업자등록번호"),
  ADDRESS("주소", EnumSet.of(
    KeywordCriteria.ALL_WORDS_CONTAINS,
    KeywordCriteria.PHRASE_CONTAINS
  ));

  private final String label;
  private final EnumSet<KeywordCriteria> supportedCriteria;

  JobEquipKeywordType(String label) {
    this(label, EnumSet.of(
      KeywordCriteria.ALL_WORDS_CONTAINS,
      KeywordCriteria.PHRASE_CONTAINS,
      KeywordCriteria.EXACT
    ));
  }

}
