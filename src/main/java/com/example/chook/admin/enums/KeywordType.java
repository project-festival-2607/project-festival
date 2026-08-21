package com.example.chook.admin.enums;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.stream.Collectors;

public interface KeywordType {

  String getLabel();

  EnumSet<KeywordCriteria> getSupportedCriteria();

  default String getSupportedCriteriaData() {
    return getSupportedCriteria().stream()
      .sorted(Comparator.comparingInt(KeywordCriteria::getPriority).reversed())
      .map(Enum::name)
      .collect(Collectors.joining(","));
  }

}
