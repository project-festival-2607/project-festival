package com.example.chook.admin.form;

import java.time.LocalDateTime;

public record JobSeekerSearchForm(

  String keywordType,       // JobSeekerKeywordType
  String keywords,

  String status,            // MemberStatus

  String JobSeekerDateRangeCriteria, // JobSeekerDateRangeCriteria
  String gender,            // Gender
  LocalDateTime startDate,
  LocalDateTime endDate,

  String provider           // Provider

) {

  public JobSeekerSearchForm {

    keywordType = blankToNull(keywordType);
    keywords = blankToNull(keywords);
    status = blankToNull(status);
    JobSeekerDateRangeCriteria = blankToNull(JobSeekerDateRangeCriteria);
    gender = blankToNull(gender);
    provider = blankToNull(provider);

  }

  private String blankToNull(String string) {
    return (string == null || string.isBlank()) ? null : string;
  }

}
