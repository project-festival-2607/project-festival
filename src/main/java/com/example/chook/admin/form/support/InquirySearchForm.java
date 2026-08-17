package com.example.chook.admin.form.support;

import java.time.LocalDateTime;

import static com.example.chook.common.util.CustomStringUtils.blankToNull;

public record InquirySearchForm(

  // 공통 검색 폼
  String keywordType,
  String keywords,
  String keywordCriteria,
  String dateRangeType,
  LocalDateTime startDateTime,
  LocalDateTime endDateTime,

  // 테이블 상단 필터
  String replyStatus,

  // 테이블 상단 정렬
  String sortCriteria,
  Boolean ascending

) {

  public InquirySearchForm {

    keywordType = blankToNull(keywordType);
    keywords = blankToNull(keywords);
    keywordCriteria = blankToNull(keywordCriteria);
    dateRangeType = blankToNull(dateRangeType);
    replyStatus = blankToNull(replyStatus);
    sortCriteria = blankToNull(sortCriteria);

  }

}
