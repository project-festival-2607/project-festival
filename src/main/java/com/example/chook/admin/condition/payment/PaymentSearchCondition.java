package com.example.chook.admin.condition.payment;

import com.example.chook.admin.enums.KeywordCriteria;
import com.example.chook.admin.enums.payment.payment.*;
import com.example.chook.admin.form.payment.PaymentSearchForm;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.chook.admin.util.KeywordUtils.getKeywordCriteria;
import static com.example.chook.admin.util.KeywordUtils.getKeywordList;
import static com.example.chook.common.util.CustomStringUtils.toEnum;

@Builder
public record PaymentSearchCondition(

  // 공통 검색 폼
  PaymentKeywordType keywordType,
  List<String> keywordList,
  KeywordCriteria keywordCriteria,
  PaymentDateRangeType dateRangeType,
  LocalDateTime startDateTime,
  LocalDateTime endDateTime,

  // 테이블 상단 필터
  PaymentMethod paymentMethod,
  PaymentStatus paymentStatus,

  // 테이블 상단 정렬
  PaymentSortCriteria sortCriteria,
  Boolean ascending

) {

  public static PaymentSearchCondition from(PaymentSearchForm form) {

    PaymentKeywordType keywordType = toEnum(form.keywordType(), PaymentKeywordType.class, null);
    KeywordCriteria keywordCriteria = getKeywordCriteria(keywordType, form.keywordCriteria());
    List<String> keywordList = getKeywordList(form.keywords(), keywordCriteria);

    return PaymentSearchCondition.builder()
      .keywordType(keywordType)
      .keywordList(keywordList)
      .keywordCriteria(keywordCriteria)
      .dateRangeType(toEnum(form.dateRangeType(), PaymentDateRangeType.class, null))
      .startDateTime(form.startDateTime())
      .endDateTime(form.endDateTime())
      .paymentMethod(toEnum(form.paymentMethod(), PaymentMethod.class, null))
      .paymentStatus(toEnum(form.paymentStatus(), PaymentStatus.class, null))
      .sortCriteria(toEnum(form.sortCriteria(), PaymentSortCriteria.class, null))
      .ascending(form.ascending())
      .build();

  }

}
