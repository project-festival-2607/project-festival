package com.example.chook.admin.condition.member;

import com.example.chook.admin.entity.enums.KeywordCriteria;
import com.example.chook.admin.entity.enums.MemberStatusFilter;
import com.example.chook.admin.entity.enums.recruiter.RecruiterDateRangeType;
import com.example.chook.admin.entity.enums.recruiter.RecruiterKeywordType;
import com.example.chook.admin.entity.enums.recruiter.RecruiterSortCriteria;
import com.example.chook.admin.form.member.RecruiterSearchForm;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.chook.admin.util.KeywordUtils.getKeywordCriteria;
import static com.example.chook.admin.util.KeywordUtils.getKeywordList;
import static com.example.chook.common.util.CustomStringUtils.toEnum;

@Builder
public record RecruiterSearchCondition(

  // 공통 검색 폼
  RecruiterKeywordType keywordType,
  List<String> keywordList,
  KeywordCriteria keywordCriteria,
  RecruiterDateRangeType dateRangeType,
  LocalDateTime startDateTime,
  LocalDateTime endDateTime,
  LocalDate startDate,
  LocalDate endDate,

  // 테이블 상단 필터
  MemberStatusFilter status,

  // 테이블 상단 정렬
  RecruiterSortCriteria sortCriteria,
  Boolean ascending

) {

  public static RecruiterSearchCondition from(RecruiterSearchForm form) {

    return RecruiterSearchCondition.builder()
      .keywordType(toEnum(form.keywordType(), RecruiterKeywordType.class, null))
      .keywordList(getKeywordList(form.keywords(), form.keywordCriteria()))
      .keywordCriteria(getKeywordCriteria(form.keywordType(), form.keywordCriteria()))
      .dateRangeType(toEnum(form.dateRangeType(), RecruiterDateRangeType.class, null))
      .startDateTime(form.startDateTime())
      .endDateTime(form.endDateTime())
      .startDate(form.startDate())
      .endDate(form.endDate())
      .status(toEnum(form.status(), MemberStatusFilter.class, null))
      .sortCriteria(toEnum(form.sortCriteria(), RecruiterSortCriteria.class, null))
      .ascending(form.ascending())
      .build();

  }
}
