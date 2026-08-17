package com.example.chook.admin.util;

import com.example.chook.admin.entity.enums.KeywordCriteria;
import com.example.chook.admin.entity.enums.jobseeker.JobSeekerKeywordType;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

import static com.example.chook.common.util.CustomStringUtils.splitByRegex;
import static com.example.chook.common.util.CustomStringUtils.toEnum;

public final class KeywordUtils {

  public static KeywordCriteria getKeywordCriteria(String keywordTypeStr, String keywordCriteriaStr) {
    JobSeekerKeywordType keywordType = toEnum(keywordTypeStr, JobSeekerKeywordType.class, null);
    if (keywordType == null) return KeywordCriteria.ALL_WORDS_CONTAINS;

    KeywordCriteria keywordCriteria = toEnum(keywordCriteriaStr, KeywordCriteria.class, KeywordCriteria.ALL_WORDS_CONTAINS);
    EnumSet<KeywordCriteria> supportedCriteria = keywordType.getSupportedCriteria();
    if (supportedCriteria.contains(keywordCriteria)) return keywordCriteria;
    return supportedCriteria.stream().max(Comparator.comparingInt(KeywordCriteria::getPriority))
      .orElse(KeywordCriteria.ALL_WORDS_CONTAINS);

  }

  public static List<String> getKeywordList(String keywords, String keywordCriteriaStr) {
    KeywordCriteria keywordCriteria = toEnum(keywordCriteriaStr, KeywordCriteria.class, KeywordCriteria.ALL_WORDS_CONTAINS);
    if (keywordCriteria != KeywordCriteria.ALL_WORDS_CONTAINS) return keywords == null ? List.of() : List.of(keywords);
    return splitByRegex(keywords, "[\\s,&]+");
  }

}
