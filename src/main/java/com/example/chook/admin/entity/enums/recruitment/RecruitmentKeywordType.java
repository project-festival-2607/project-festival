package com.example.chook.admin.entity.enums.recruitment;

import com.example.chook.admin.entity.enums.KeywordCriteria;
import com.example.chook.admin.entity.enums.KeywordType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;

@AllArgsConstructor
@Getter
public enum RecruitmentKeywordType implements KeywordType {

  RECRUITMENT_ID("고유 아이디", EnumSet.of(KeywordCriteria.EXACT)),
  RECRUITMENT_TITLE("공고 제목"),
  RECRUITMENT_CONTENT("공고 내용", EnumSet.of(
    KeywordCriteria.ALL_WORDS_CONTAINS,
    KeywordCriteria.PHRASE_CONTAINS
  )),
  FESTIVAL_ID("행사 고유 아이디", EnumSet.of(KeywordCriteria.EXACT)),
  FESTIVAL_TITLE("행사 제목"),
  MEMBER_ID("담당자 고유 아이디", EnumSet.of(KeywordCriteria.EXACT)),
  LOCATION("근무 장소", EnumSet.of(
    KeywordCriteria.ALL_WORDS_CONTAINS,
    KeywordCriteria.PHRASE_CONTAINS
  )),
  ;

  private final String label;
  private final EnumSet<KeywordCriteria> supportedCriteria;

  RecruitmentKeywordType(String label) {
    this(label, EnumSet.of(
      KeywordCriteria.ALL_WORDS_CONTAINS,
      KeywordCriteria.PHRASE_CONTAINS,
      KeywordCriteria.EXACT
    ));
  }

}
