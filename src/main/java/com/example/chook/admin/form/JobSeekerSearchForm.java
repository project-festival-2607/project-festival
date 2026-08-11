package com.example.chook.admin.form;

import java.time.LocalDateTime;

public record JobSeekerSearchForm(

  String keywordType,       // JobSeekerKeywordType
  String keywords,

  String status,            // MemberStatus

  String dateRangeCriteria, // DateRangeCriteria
  LocalDateTime startDate,
  LocalDateTime endDate,

  String provider           // Provider

) {

  public JobSeekerSearchForm {

    keywordType = blankToNull(keywordType);
    keywords = blankToNull(keywords);
    status = blankToNull(status);
    dateRangeCriteria = blankToNull(dateRangeCriteria);
    provider = blankToNull(provider);

  }

  private String blankToNull(String string) {
    return (string == null || string.isBlank()) ? null : string;
  }

}
