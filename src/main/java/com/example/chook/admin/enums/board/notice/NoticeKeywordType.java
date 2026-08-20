package com.example.chook.admin.enums.board.notice;

import com.example.chook.admin.enums.KeywordCriteria;
import com.example.chook.admin.enums.KeywordType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;

@AllArgsConstructor
@Getter
public enum NoticeKeywordType implements KeywordType {

  TITLE("제목"),
  CONTENT("내용", EnumSet.of(
    KeywordCriteria.ALL_WORDS_CONTAINS,
    KeywordCriteria.PHRASE_CONTAINS)
  );

  private final String label;
  private final EnumSet<KeywordCriteria> supportedCriteria;

  NoticeKeywordType(String label) {
    this(label, EnumSet.of(
      KeywordCriteria.ALL_WORDS_CONTAINS,
      KeywordCriteria.PHRASE_CONTAINS,
      KeywordCriteria.EXACT
    ));
  }
}
