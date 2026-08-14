package com.example.chook.admin.record;

import com.example.chook.admin.entity.enums.JobSeekerDateCriteria;
import com.example.chook.admin.entity.enums.JobSeekerKeywordType;
import com.example.chook.admin.entity.enums.MemberStatusFilter;
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

  MemberStatusFilter status,

  Gender gender,
  JobSeekerDateCriteria dateRangeCriteria,
  LocalDateTime startDateTime,
  LocalDateTime endDateTime,
  LocalDate startDate,
  LocalDate endDate,

  Provider provider,

  // 정렬용
  JobSeekerDateCriteria dateSortCriteria,
  Boolean ascending


) {

  public JobSeekerSearchCondition(JobSeekerSearchForm form) {

    this(
      convertStringToEnum(form.keywordType(), JobSeekerKeywordType.class, JobSeekerKeywordType.USERNAME),
      splitByRegex(form.keywords(), "[\\s,&]+"),
      convertStringToEnum(form.status(), MemberStatusFilter.class, null),
      convertStringToEnum(form.gender(), Gender.class, null),
      convertStringToEnum(form.dateRangeCriteria(), JobSeekerDateCriteria.class, null),
      form.startDateTime(),
      form.endDateTime(),
      form.startDate(),
      form.endDate(),
      convertStringToEnum(form.provider(), Provider.class, null),
      convertStringToEnum(form.dateSortCriteria(), JobSeekerDateCriteria.class, null),
      form.ascending()
    );
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
