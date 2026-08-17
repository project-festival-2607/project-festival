package com.example.chook.admin.form;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.example.chook.common.util.CustomStringUtils.blankToNull;

public record JobSeekerSearchForm(

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
  String gender,
  String provider,

  // 테이블 상단 정렬
  String sortCriteria,
  Boolean ascending

) {

  public JobSeekerSearchForm {

    keywordType = blankToNull(keywordType);
    keywords = blankToNull(keywords);
    keywordCriteria = blankToNull(keywordCriteria);
    dateRangeType = blankToNull(dateRangeType);
    status = blankToNull(status);
    gender = blankToNull(gender);
    provider = blankToNull(provider);
    sortCriteria = blankToNull(sortCriteria);

  }

}
