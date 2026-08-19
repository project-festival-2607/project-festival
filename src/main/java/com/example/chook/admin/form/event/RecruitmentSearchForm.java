package com.example.chook.admin.form.event;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.example.chook.common.util.CustomStringUtils.blankToNull;

public record RecruitmentSearchForm(

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
  String category,
  String sidoCode,
  String sigunguCode,
  String status,

  // 테이블 상단 정렬
  String sortCriteria,
  Boolean ascending

) {

  public RecruitmentSearchForm {

    keywordType = blankToNull(keywordType);
    keywords = blankToNull(keywords);
    keywordCriteria = blankToNull(keywordCriteria);
    dateRangeType = blankToNull(dateRangeType);
    category = blankToNull(category);
    sidoCode = blankToNull(sidoCode);
    status = blankToNull(status);
    sortCriteria = blankToNull(sortCriteria);

    String rawSigunguCode = blankToNull(sigunguCode);
    if (sidoCode == null ||
        rawSigunguCode != null && !rawSigunguCode.startsWith(sidoCode)) rawSigunguCode = null;
    sigunguCode = rawSigunguCode;
  }

}
