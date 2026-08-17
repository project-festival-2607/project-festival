package com.example.chook.admin.entity.enums.inquiry;

import com.example.chook.admin.entity.enums.KeywordCriteria;
import com.example.chook.admin.entity.enums.KeywordType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;

@AllArgsConstructor
@Getter
public enum InquiryKeywordType implements KeywordType {

  INO("문의번호", EnumSet.of(KeywordCriteria.EXACT)),
  TITLE("문의 제목"),
  CONTENT("문의 내용", EnumSet.of(
    KeywordCriteria.ALL_WORDS_CONTAINS,
    KeywordCriteria.PHRASE_CONTAINS
  )),
  COMMENT("문의 답변", EnumSet.of(
    KeywordCriteria.ALL_WORDS_CONTAINS,
    KeywordCriteria.PHRASE_CONTAINS
  )),
  USERNAME("작성자 아이디"),
  NAME("작성자 이름");

  private final String label;
  private final EnumSet<KeywordCriteria> supportedCriteria;

  InquiryKeywordType(String label) {
    this(label, EnumSet.of(
      KeywordCriteria.ALL_WORDS_CONTAINS,
      KeywordCriteria.PHRASE_CONTAINS,
      KeywordCriteria.EXACT
    ));
  }


}
