package com.example.chook.admin.form.event;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.example.chook.common.util.CustomStringUtils.blankToNull;

public record FestivalSearchForm(

  // 공통 검색 폼
  String keywordType,
  String keywords,
  String keywordCriteria,
  String dateRangeType,
  LocalDateTime startDateTime,  // 여기에서는 사용되지 않으나 호환을 위해 삽입
  LocalDateTime endDateTime,    // 여기에서는 사용되지 않으나 호환을 위해 삽입
  LocalDate startDate,
  LocalDate endDate,

  // 테이블 상단 필터

  // 테이블 상단 정렬
  String sortCriteria,
  Boolean ascending

) {

  public FestivalSearchForm {

    keywordType = blankToNull(keywordType);
    keywords = blankToNull(keywords);
    keywordCriteria = blankToNull(keywordCriteria);
    dateRangeType = blankToNull(dateRangeType);
    sortCriteria = blankToNull(sortCriteria);

  }
}
