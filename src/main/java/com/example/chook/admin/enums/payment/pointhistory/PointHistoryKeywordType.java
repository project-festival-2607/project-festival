package com.example.chook.admin.enums.payment.pointhistory;

import com.example.chook.admin.enums.KeywordCriteria;
import com.example.chook.admin.enums.KeywordType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;

@AllArgsConstructor
@Getter
public enum PointHistoryKeywordType implements KeywordType {

  PAY_CLASSIFY_ID("기록 UID", EnumSet.of(KeywordCriteria.EXACT)),
  MEMBER_ID("사용자 UID", EnumSet.of(KeywordCriteria.EXACT));

  private final String label;
  private final EnumSet<KeywordCriteria> supportedCriteria;

  PointHistoryKeywordType(String label) {
    this(label, EnumSet.of(
      KeywordCriteria.ALL_WORDS_CONTAINS,
      KeywordCriteria.PHRASE_CONTAINS,
      KeywordCriteria.EXACT
    ));
  }

}
