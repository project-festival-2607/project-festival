package com.example.chook.admin.form;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.example.chook.common.util.CustomStringUtils.blankToNull;

public record JobSeekerSearchForm(

  String keywordType,       // JobSeekerKeywordType
  String keywords,

  String status,            // MemberStatusFilter

  String dateRangeCriteria, // dateRangeCriteria
  String gender,            // Gender
  LocalDateTime startDateTime,
  LocalDateTime endDateTime,
  LocalDate startDate,
  LocalDate endDate,

  String provider           // Provider

) {

  public JobSeekerSearchForm {

    keywordType = blankToNull(keywordType);
    keywords = blankToNull(keywords);
    status = blankToNull(status);
    dateRangeCriteria = blankToNull(dateRangeCriteria);
    gender = blankToNull(gender);
    provider = blankToNull(provider);

  }

}
