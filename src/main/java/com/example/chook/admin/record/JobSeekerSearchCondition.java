package com.example.chook.admin.record;

import com.example.chook.admin.entity.enums.JobSeekerDateRangeCriteria;
import com.example.chook.admin.entity.enums.JobSeekerKeywordType;
import com.example.chook.admin.form.JobSeekerSearchForm;
import com.example.chook.member.entity.enums.Gender;
import com.example.chook.member.entity.enums.MemberStatus;
import com.example.chook.member.entity.enums.Provider;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.chook.common.util.CustomStringUtils.splitByRegex;

public record JobSeekerSearchCondition(

  JobSeekerKeywordType keywordType,
  List<String> keywordList,

  MemberStatus status,

  Gender gender,
  JobSeekerDateRangeCriteria dateRangeCriteria,
  LocalDateTime startDateTime,
  LocalDateTime endDateTime,

  Provider provider

) {

  public JobSeekerSearchCondition(JobSeekerSearchForm form) {

    this(
      convertStringToEnum(form.keywordType(), JobSeekerKeywordType.class, JobSeekerKeywordType.USERNAME),
      splitByRegex(form.keywords(), "[\\s,&]+"),
      convertStringToEnum(form.status(), MemberStatus.class, null),
      convertStringToEnum(form.gender(), Gender.class, null),
      convertStringToEnum(form.JobSeekerDateRangeCriteria(), JobSeekerDateRangeCriteria.class, JobSeekerDateRangeCriteria.LAST_LOGIN_AT),
      form.startDateTime(),
      form.endDateTime(),
      convertStringToEnum(form.provider(), Provider.class, null)
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
