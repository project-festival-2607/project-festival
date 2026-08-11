package com.example.chook.admin.form;

import java.time.LocalDateTime;

import static com.example.chook.common.util.CustomStringUtils.blankToNull;

public record JobSeekerSearchForm(

  String keywordType,       // JobSeekerKeywordType
  String keywords,

  String status,            // MemberStatus

  String JobSeekerDateRangeCriteria, // JobSeekerDateRangeCriteria
  String gender,            // Gender
  LocalDateTime startDateTime,
  LocalDateTime endDateTime,

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

}
