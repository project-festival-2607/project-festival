package com.example.chook.admin.condition.payment;

import com.example.chook.admin.enums.KeywordCriteria;
import com.example.chook.admin.enums.payment.refund.RefundDateRangeType;
import com.example.chook.admin.enums.payment.refund.RefundKeywordType;
import com.example.chook.admin.enums.payment.refund.RefundSortCriteria;
import com.example.chook.admin.enums.payment.refund.RefundStatus;
import com.example.chook.admin.form.payment.RefundSearchForm;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.chook.admin.util.KeywordUtils.getKeywordCriteria;
import static com.example.chook.admin.util.KeywordUtils.getKeywordList;
import static com.example.chook.common.util.CustomStringUtils.toEnum;

@Builder
public record RefundSearchCondition(

  // 공통 검색 폼
  RefundKeywordType keywordType,
  List<String> keywordList,
  KeywordCriteria keywordCriteria,
  RefundDateRangeType dateRangeType,
  LocalDateTime startDateTime,
  LocalDateTime endDateTime,

  // 테이블 상단 필터
  RefundStatus status,

  // 테이블 상단 정렬
  RefundSortCriteria sortCriteria,
  Boolean ascending

) {

  public static RefundSearchCondition from(RefundSearchForm form) {

    RefundKeywordType keywordType = toEnum(form.keywordType(), RefundKeywordType.class, null);
    KeywordCriteria keywordCriteria = getKeywordCriteria(keywordType, form.keywordCriteria());
    List<String> keywordList = getKeywordList(form.keywords(), keywordCriteria);

    return RefundSearchCondition.builder()
      .keywordType(keywordType)
      .keywordList(keywordList)
      .keywordCriteria(keywordCriteria)
      .dateRangeType(toEnum(form.dateRangeType(), RefundDateRangeType.class, null))
      .startDateTime(form.startDateTime())
      .endDateTime(form.endDateTime())
      .status(toEnum(form.status(), RefundStatus.class, null))
      .sortCriteria(toEnum(form.sortCriteria(), RefundSortCriteria.class, null))
      .ascending(form.ascending())
      .build();

  }

}
