package com.example.chook.admin.condition.payment;

import com.example.chook.admin.enums.KeywordCriteria;
import com.example.chook.admin.enums.payment.summary.PaymentRecordType;
import com.example.chook.admin.enums.payment.summary.SummaryDateRangeType;
import com.example.chook.admin.enums.payment.summary.SummaryKeywordType;
import com.example.chook.admin.enums.payment.summary.SummarySortCriteria;
import com.example.chook.admin.form.payment.SummarySearchForm;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.chook.admin.util.KeywordUtils.getKeywordCriteria;
import static com.example.chook.admin.util.KeywordUtils.getKeywordList;
import static com.example.chook.common.util.CustomStringUtils.toEnum;

@Builder
public record SummarySearchCondition(

  // 공통 검색 폼
  SummaryKeywordType keywordType,
  List<String> keywordList,
  KeywordCriteria keywordCriteria,
  SummaryDateRangeType dateRangeType,
  LocalDateTime startDateTime,
  LocalDateTime endDateTime,

  // 테이블 상단 필터
  PaymentRecordType paymentRecordType,

  // 테이블 상단 정렬
  SummarySortCriteria sortCriteria,
  Boolean ascending

) {

  public static SummarySearchCondition from(SummarySearchForm form) {

    SummaryKeywordType keywordType = toEnum(form.keywordType(), SummaryKeywordType.class, null);
    KeywordCriteria keywordCriteria = getKeywordCriteria(keywordType, form.keywordCriteria());
    List<String> keywordList = getKeywordList(form.keywords(), keywordCriteria);

    return SummarySearchCondition.builder()
      .keywordType(keywordType)
      .keywordList(keywordList)
      .keywordCriteria(keywordCriteria)
      .dateRangeType(toEnum(form.dateRangeType(), SummaryDateRangeType.class, null))
      .startDateTime(form.startDateTime())
      .endDateTime(form.endDateTime())
      .paymentRecordType(toEnum(form.paymentRecordType(), PaymentRecordType.class, null))
      .sortCriteria(toEnum(form.sortCriteria(), SummarySortCriteria.class, null))
      .ascending(form.ascending())
      .build();

  }

}
