package com.example.chook.admin.form;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.example.chook.common.util.CustomStringUtils.blankToNull;

public record JobSeekerSearchForm(

  String keywordType,       // JobSeekerKeywordType
  String keywords,
  String keywordCriteria,

  String status,            // MemberStatusFilter

  String dateRangeCriteria, // dateRangeCriteria
  String gender,            // Gender
  LocalDateTime startDateTime,
  LocalDateTime endDateTime,
  LocalDate startDate,
  LocalDate endDate,

  String provider,          // Provider

  String dateSortCriteria,
  Boolean ascending

) {

  public JobSeekerSearchForm {

    keywordType = blankToNull(keywordType);
    keywords = blankToNull(keywords);
    keywordCriteria = blankToNull(keywordCriteria);
    status = blankToNull(status);
    dateRangeCriteria = blankToNull(dateRangeCriteria);
    gender = blankToNull(gender);
    provider = blankToNull(provider);
    dateSortCriteria = blankToNull(dateSortCriteria);

  }

}
