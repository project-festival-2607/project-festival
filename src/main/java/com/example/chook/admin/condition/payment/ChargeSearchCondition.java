package com.example.chook.admin.condition.payment;

import com.example.chook.admin.enums.KeywordCriteria;
import com.example.chook.admin.enums.payment.charge.*;
import com.example.chook.admin.form.payment.ChargeSearchForm;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.chook.admin.util.KeywordUtils.getKeywordCriteria;
import static com.example.chook.admin.util.KeywordUtils.getKeywordList;
import static com.example.chook.common.util.CustomStringUtils.toEnum;

@Builder
public record ChargeSearchCondition(

  // 공통 검색 폼
  ChargeKeywordType keywordType,
  List<String> keywordList,
  KeywordCriteria keywordCriteria,
  ChargeDateRangeType dateRangeType,
  LocalDateTime startDateTime,
  LocalDateTime endDateTime,

  // 테이블 상단 필터
  PaymentMethod paymentMethod,
  PaymentStatus paymentStatus,

  // 테이블 상단 정렬
  ChargeSortCriteria sortCriteria,
  Boolean ascending

) {

  public static ChargeSearchCondition from(ChargeSearchForm form) {

    ChargeKeywordType keywordType = toEnum(form.keywordType(), ChargeKeywordType.class, null);
    KeywordCriteria keywordCriteria = getKeywordCriteria(keywordType, form.keywordCriteria());
    List<String> keywordList = getKeywordList(form.keywords(), keywordCriteria);

    return ChargeSearchCondition.builder()
      .keywordType(keywordType)
      .keywordList(keywordList)
      .keywordCriteria(keywordCriteria)
      .dateRangeType(toEnum(form.dateRangeType(), ChargeDateRangeType.class, null))
      .startDateTime(form.startDateTime())
      .endDateTime(form.endDateTime())
      .paymentMethod(toEnum(form.paymentMethod(), PaymentMethod.class, null))
      .paymentStatus(toEnum(form.paymentStatus(), PaymentStatus.class, null))
      .sortCriteria(toEnum(form.sortCriteria(), ChargeSortCriteria.class, null))
      .ascending(form.ascending())
      .build();

  }

}
