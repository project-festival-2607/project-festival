package com.example.chook.admin.enums.payment.summary;

import com.example.chook.admin.enums.KeywordCriteria;
import com.example.chook.admin.enums.KeywordType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;

@AllArgsConstructor
@Getter
public enum SummaryKeywordType implements KeywordType {

  RECORD_ID("전체기록 UID", EnumSet.of(KeywordCriteria.EXACT)),

  PAYMENT_ID("충전 UID", EnumSet.of(KeywordCriteria.EXACT)),
  POINT_HISTORY_ID("사용 UID", EnumSet.of(KeywordCriteria.EXACT)),
  REFUND_ID("환불 UID", EnumSet.of(KeywordCriteria.EXACT)),

  MEMBER_ID("사용자 UID", EnumSet.of(KeywordCriteria.EXACT)),
  MEMBER_USERNAME("사용자 아이디", EnumSet.of(KeywordCriteria.EXACT)),
  MEMBER_NAME("사용자 이름"),

  RECRUITMENT_ID("지원공고 UID", EnumSet.of(KeywordCriteria.EXACT)),
  RECRUITMENT_TITLE("지원공고 제목");

  private final String label;
  private final EnumSet<KeywordCriteria> supportedCriteria;

  SummaryKeywordType(String label) {
    this(label, EnumSet.of(
      KeywordCriteria.ALL_WORDS_CONTAINS,
      KeywordCriteria.PHRASE_CONTAINS,
      KeywordCriteria.EXACT
    ));
  }

}
