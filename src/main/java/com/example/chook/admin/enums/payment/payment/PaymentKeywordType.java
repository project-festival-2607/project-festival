package com.example.chook.admin.enums.payment.payment;

import com.example.chook.admin.enums.KeywordCriteria;
import com.example.chook.admin.enums.KeywordType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;

@AllArgsConstructor
@Getter
public enum PaymentKeywordType implements KeywordType {

  PAYMENT_ID("결제번호", EnumSet.of(KeywordCriteria.EXACT)),

  MEMBER_ID("사용자 UID", EnumSet.of(KeywordCriteria.EXACT)),
  MEMBER_USERNAME("사용자 아이디", EnumSet.of(KeywordCriteria.EXACT)),
  MEMBER_NAME("사용자 이름"),

  PRODUCT_ID("상품 UID", EnumSet.of(KeywordCriteria.EXACT)),
  PRODUCT_NAME("상품 이름")
  ;

  private final String label;
  private final EnumSet<KeywordCriteria> supportedCriteria;

  PaymentKeywordType(String label) {
    this(label, EnumSet.of(
      KeywordCriteria.ALL_WORDS_CONTAINS,
      KeywordCriteria.PHRASE_CONTAINS,
      KeywordCriteria.EXACT
    ));
  }

}
