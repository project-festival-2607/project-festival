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

import static com.example.chook.common.util.CustomStringUtils.splitByRegex;

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
      .keywordType(convertStringToEnum(form.keywordType(), JobSeekerKeywordType.class, null))
      .keywordList(getKeywordList(form.keywords(), form.keywordCriteria()))
      .keywordCriteria(getKeywordCriteria(form.keywordType(), form.keywordCriteria()))
      .dateRangeCriteria(convertStringToEnum(form.dateRangeCriteria(), JobSeekerDateCriteria.class, null))
      .startDateTime(form.startDateTime())
      .endDateTime(form.endDateTime())
      .startDate(form.startDate())
      .endDate(form.endDate())
      .status(convertStringToEnum(form.status(), MemberStatusFilter.class, null))
      .gender(convertStringToEnum(form.gender(), Gender.class, null))
      .provider(convertStringToEnum(form.provider(), Provider.class, null))
      .sortCriteria(convertStringToEnum(form.sortCriteria(), JobSeekerSortCriteria.class, null))
      .ascending(form.ascending())
      .build();

  }

  private static KeywordCriteria getKeywordCriteria(String keywordTypeStr, String keywordCriteria) {
    JobSeekerKeywordType keywordType = convertStringToEnum(keywordTypeStr, JobSeekerKeywordType.class, null);
    if (keywordType == null || !keywordType.isExactMatchSupported()) return KeywordCriteria.CONTAINS;
    return convertStringToEnum(keywordCriteria, KeywordCriteria.class, KeywordCriteria.CONTAINS);
  }

  private static List<String> getKeywordList(String keywords, String keywordCriteriaStr) {
    KeywordCriteria keywordCriteria = convertStringToEnum(keywordCriteriaStr, KeywordCriteria.class, KeywordCriteria.CONTAINS);
    if (keywordCriteria == KeywordCriteria.EQUALS) return keywords == null ? List.of() : List.of(keywords);
    return splitByRegex(keywords, "[\\s,&]+");
  }

  private static <T extends Enum<T>> T convertStringToEnum(
    String string,
    Class<T> enumClass,
    T defaultValue
  ) {
    if (string == null) return defaultValue;
    try {
      return Enum.valueOf(enumClass, string);
    } catch (IllegalArgumentException e) {
      return defaultValue;
    }
  }

}
