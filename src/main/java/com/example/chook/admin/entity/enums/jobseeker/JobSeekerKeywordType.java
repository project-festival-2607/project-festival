package com.example.chook.admin.entity.enums.jobseeker;

import com.example.chook.admin.entity.enums.KeywordCriteria;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum JobSeekerKeywordType {

  USERNAME("아이디"),
  NAME("이름"),
  PHONE("전화번호"),
  EMAIL("이메일"),
  ADDRESS("주소", EnumSet.of(
    KeywordCriteria.ALL_WORDS_CONTAINS,
    KeywordCriteria.PHRASE_CONTAINS
  ));

  private final String label;
  private final EnumSet<KeywordCriteria> supportedCriteria;

  JobSeekerKeywordType(String label) {
    this(label, EnumSet.of(
      KeywordCriteria.ALL_WORDS_CONTAINS,
      KeywordCriteria.PHRASE_CONTAINS,
      KeywordCriteria.EXACT
    ));
  }

  public String getSupportedCriteriaData() {
    return supportedCriteria.stream()
      .map(Enum::name)
      .collect(Collectors.joining(","));
  }

}
