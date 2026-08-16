package com.example.chook.admin.condition;

import com.example.chook.admin.entity.enums.KeywordCriteria;
import com.example.chook.admin.entity.enums.MemberStatusFilter;
import com.example.chook.admin.entity.enums.jobseeker.JobSeekerDateCriteria;
import com.example.chook.admin.entity.enums.jobseeker.JobSeekerKeywordType;
import com.example.chook.admin.entity.enums.jobseeker.JobSeekerSortCriteria;
import com.example.chook.admin.form.JobSeekerSearchForm;
import com.example.chook.member.entity.enums.Gender;
import com.example.chook.member.entity.enums.Provider;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.chook.admin.util.KeywordUtils.getKeywordCriteria;
import static com.example.chook.admin.util.KeywordUtils.getKeywordList;
import static com.example.chook.common.util.CustomStringUtils.toEnum;

@Builder
public record JobSeekerSearchCondition(

  // 공통 검색 폼
  JobSeekerKeywordType keywordType,
  List<String> keywordList,
  KeywordCriteria keywordCriteria,
  JobSeekerDateCriteria dateRangeCriteria,
  LocalDateTime startDateTime,
  LocalDateTime endDateTime,
  LocalDate startDate,
  LocalDate endDate,

  // 테이블 상단 필터
  MemberStatusFilter status,
  Gender gender,
  Provider provider,

  // 테이블 상단 정렬
  JobSeekerSortCriteria sortCriteria,
  Boolean ascending


) {

  public static JobSeekerSearchCondition from(JobSeekerSearchForm form) {

    return JobSeekerSearchCondition.builder()
      .keywordType(toEnum(form.keywordType(), JobSeekerKeywordType.class, null))
      .keywordList(getKeywordList(form.keywords(), form.keywordCriteria()))
      .keywordCriteria(getKeywordCriteria(form.keywordType(), form.keywordCriteria()))
      .dateRangeCriteria(toEnum(form.dateRangeCriteria(), JobSeekerDateCriteria.class, null))
      .startDateTime(form.startDateTime())
      .endDateTime(form.endDateTime())
      .startDate(form.startDate())
      .endDate(form.endDate())
      .status(toEnum(form.status(), MemberStatusFilter.class, null))
      .gender(toEnum(form.gender(), Gender.class, null))
      .provider(toEnum(form.provider(), Provider.class, null))
      .sortCriteria(toEnum(form.sortCriteria(), JobSeekerSortCriteria.class, null))
      .ascending(form.ascending())
      .build();

  }

}
