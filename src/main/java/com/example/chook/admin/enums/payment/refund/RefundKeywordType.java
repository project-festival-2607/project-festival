package com.example.chook.admin.enums.payment.refund;

import com.example.chook.admin.enums.KeywordCriteria;
import com.example.chook.admin.enums.KeywordType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;

@AllArgsConstructor
@Getter
public enum RefundKeywordType implements KeywordType {

  REFUND_ID("환불 UID", EnumSet.of(KeywordCriteria.EXACT)),
  PAY_CLASSIFY_ID("전체기록 UID", EnumSet.of(KeywordCriteria.EXACT)),
  MEMBER_ID("사용자 UID", EnumSet.of(KeywordCriteria.EXACT)),
  MEMBER_USERNAME("사용자 아이디", EnumSet.of(KeywordCriteria.EXACT)),
  MEMBER_NAME("사용자 이름");

  private final String label;
  private final EnumSet<KeywordCriteria> supportedCriteria;

  RefundKeywordType(String label) {
    this(label, EnumSet.of(
      KeywordCriteria.ALL_WORDS_CONTAINS,
      KeywordCriteria.PHRASE_CONTAINS,
      KeywordCriteria.EXACT
    ));
  }
}
