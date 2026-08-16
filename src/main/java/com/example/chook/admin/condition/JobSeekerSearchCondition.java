package com.example.chook.admin.condition;

import com.example.chook.admin.entity.enums.KeywordCriteria;
import com.example.chook.admin.entity.enums.MemberStatusFilter;
import com.example.chook.admin.entity.enums.jobseeker.JobSeekerDateCriteria;
import com.example.chook.admin.entity.enums.jobseeker.JobSeekerKeywordType;
import com.example.chook.admin.entity.enums.jobseeker.JobSeekerSortCriteria;
import com.example.chook.admin.form.JobSeekerSearchForm;
import com.example.chook.member.entity.enums.Gender;
import com.example.chook.member.entity.enums.Provider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.chook.common.util.CustomStringUtils.splitByRegex;

public record JobSeekerSearchCondition(

  // 필터용

  JobSeekerKeywordType keywordType,
  List<String> keywordList,
  KeywordCriteria keywordCriteria,

  MemberStatusFilter status,

  Gender gender,
  JobSeekerDateCriteria dateRangeCriteria,
  LocalDateTime startDateTime,
  LocalDateTime endDateTime,
  LocalDate startDate,
  LocalDate endDate,

  Provider provider,

  // 정렬용
  JobSeekerSortCriteria sortCriteria,
  Boolean ascending


) {

  public JobSeekerSearchCondition(JobSeekerSearchForm form) {

    this(
      convertStringToEnum(form.keywordType(), JobSeekerKeywordType.class, null),
      getKeywordList(form.keywords(), form.keywordCriteria()),
      getKeywordCriteria(form.keywordType(), form.keywordCriteria()),
      convertStringToEnum(form.status(), MemberStatusFilter.class, null),
      convertStringToEnum(form.gender(), Gender.class, null),
      convertStringToEnum(form.dateRangeCriteria(), JobSeekerDateCriteria.class, null),
      form.startDateTime(),
      form.endDateTime(),
      form.startDate(),
      form.endDate(),
      convertStringToEnum(form.provider(), Provider.class, null),
      convertStringToEnum(form.sortCriteria(), JobSeekerSortCriteria.class, null),
      form.ascending()
    );

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
