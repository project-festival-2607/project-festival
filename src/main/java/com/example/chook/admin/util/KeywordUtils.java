package com.example.chook.admin.util;

import com.example.chook.admin.entity.enums.KeywordCriteria;
import com.example.chook.admin.entity.enums.jobseeker.JobSeekerKeywordType;

import java.util.List;

import static com.example.chook.common.util.CustomStringUtils.splitByRegex;
import static com.example.chook.common.util.CustomStringUtils.toEnum;

public final class KeywordUtils {

  public static KeywordCriteria getKeywordCriteria(String keywordTypeStr, String keywordCriteria) {
    JobSeekerKeywordType keywordType = toEnum(keywordTypeStr, JobSeekerKeywordType.class, null);
    if (keywordType == null || !keywordType.isExactMatchSupported()) return KeywordCriteria.CONTAINS;
    return toEnum(keywordCriteria, KeywordCriteria.class, KeywordCriteria.CONTAINS);
  }

  public static List<String> getKeywordList(String keywords, String keywordCriteriaStr) {
    KeywordCriteria keywordCriteria = toEnum(keywordCriteriaStr, KeywordCriteria.class, KeywordCriteria.CONTAINS);
    if (keywordCriteria == KeywordCriteria.EQUALS) return keywords == null ? List.of() : List.of(keywords);
    return splitByRegex(keywords, "[\\s,&]+");
  }

}
