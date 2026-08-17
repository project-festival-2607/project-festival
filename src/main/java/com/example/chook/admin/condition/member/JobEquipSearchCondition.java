package com.example.chook.admin.condition.member;

import com.example.chook.admin.entity.enums.KeywordCriteria;
import com.example.chook.admin.entity.enums.MemberStatusFilter;
import com.example.chook.admin.entity.enums.jobequip.JobEquipDateRangeType;
import com.example.chook.admin.entity.enums.jobequip.JobEquipKeywordType;
import com.example.chook.admin.entity.enums.jobequip.JobEquipSortCriteria;
import com.example.chook.admin.form.member.JobSeekerSearchForm;
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
public record JobEquipSearchCondition(

  // 공통 검색 폼
  JobEquipKeywordType keywordType,
  List<String> keywordList,
  KeywordCriteria keywordCriteria,
  JobEquipDateRangeType dateRangeType,
  LocalDateTime startDateTime,
  LocalDateTime endDateTime,
  LocalDate startDate,
  LocalDate endDate,

  // 테이블 상단 필터
  MemberStatusFilter status,
  Gender gender,
  Provider provider,

  // 테이블 상단 정렬
  JobEquipSortCriteria sortCriteria,
  Boolean ascending

) {

  public static JobEquipSearchCondition from(JobSeekerSearchForm form) {

    return JobEquipSearchCondition.builder()
      .keywordType(toEnum(form.keywordType(), JobEquipKeywordType.class, null))
      .keywordList(getKeywordList(form.keywords(), form.keywordCriteria()))
      .keywordCriteria(getKeywordCriteria(form.keywordType(), form.keywordCriteria()))
      .dateRangeType(toEnum(form.dateRangeType(), JobEquipDateRangeType.class, null))
      .startDateTime(form.startDateTime())
      .endDateTime(form.endDateTime())
      .startDate(form.startDate())
      .endDate(form.endDate())
      .status(toEnum(form.status(), MemberStatusFilter.class, null))
      .gender(toEnum(form.gender(), Gender.class, null))
      .provider(toEnum(form.provider(), Provider.class, null))
      .sortCriteria(toEnum(form.sortCriteria(), JobEquipSortCriteria.class, null))
      .ascending(form.ascending())
      .build();

  }
}
