package com.example.chook.admin.form;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.example.chook.common.util.CustomStringUtils.blankToNull;

public record RecruiterSearchForm(

  // 공통 검색 폼
  String keywordType,
  String keywords,
  String keywordCriteria,
  String dateRangeType,
  LocalDateTime startDateTime,
  LocalDateTime endDateTime,
  LocalDate startDate,
  LocalDate endDate,

  // 테이블 상단 필터
  String status,

  // 테이블 상단 정렬
  String sortCriteria,
  Boolean ascending

) {

  public RecruiterSearchForm {

    keywordType = blankToNull(keywordType);
    keywords = blankToNull(keywords);
    keywordCriteria = blankToNull(keywordCriteria);
    dateRangeType = blankToNull(dateRangeType);
    status = blankToNull(status);
    sortCriteria = blankToNull(sortCriteria);

  }

}
