package com.example.chook.admin.entity.enums.festival;

import com.example.chook.admin.entity.enums.KeywordCriteria;
import com.example.chook.admin.entity.enums.KeywordType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;

@AllArgsConstructor
@Getter
public enum FestivalKeywordType implements KeywordType {

  CONTENT_ID("고유 아이디", EnumSet.of(KeywordCriteria.EXACT)),
  TITLE("제목"),
  PLACE("장소"),
  CONTENT("내용"),
  MEMBER_USERNAME("주최자 아이디", EnumSet.of(KeywordCriteria.EXACT));

  private final String label;
  private final EnumSet<KeywordCriteria> supportedCriteria;

  FestivalKeywordType(String label) {
    this(label, EnumSet.of(
      KeywordCriteria.ALL_WORDS_CONTAINS,
      KeywordCriteria.PHRASE_CONTAINS,
      KeywordCriteria.EXACT
    ));
  }

}
