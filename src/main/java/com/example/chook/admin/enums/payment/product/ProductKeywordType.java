package com.example.chook.admin.enums.payment.product;

import com.example.chook.admin.enums.KeywordCriteria;
import com.example.chook.admin.enums.KeywordType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;

@AllArgsConstructor
@Getter
public enum ProductKeywordType implements KeywordType {

  PRODUCT_ID("상품 UID", EnumSet.of(KeywordCriteria.EXACT)),
  PRODUCT_NAME("상품 이름");

  private final String label;
  private final EnumSet<KeywordCriteria> supportedCriteria;

  ProductKeywordType(String label) {
    this(label, EnumSet.of(
      KeywordCriteria.ALL_WORDS_CONTAINS,
      KeywordCriteria.PHRASE_CONTAINS,
      KeywordCriteria.EXACT
    ));
  }

}
