package com.example.chook.admin.form.payment;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.example.chook.common.util.CustomStringUtils.blankToNull;

public record SummarySearchForm(

  // 공통 검색 폼
  String keywordType,
  String keywords,
  String keywordCriteria,
  String dateRangeType,
  LocalDateTime startDateTime,
  LocalDateTime endDateTime,
  LocalDate startDate,        // 여기에서는 사용되지 않으나 호환을 위해 삽입
  LocalDate endDate,          // 여기에서는 사용되지 않으나 호환을 위해 삽입

  // 테이블 상단 필터
  String paymentRecordType,

  // 테이블 상단 정렬
  String sortCriteria,
  Boolean ascending

) {

  public SummarySearchForm {

    keywordType = blankToNull(keywordType);
    keywords = blankToNull(keywords);
    keywordCriteria = blankToNull(keywordCriteria);
    dateRangeType = blankToNull(dateRangeType);
    paymentRecordType = blankToNull(paymentRecordType);
    sortCriteria = blankToNull(sortCriteria);

  }

}
