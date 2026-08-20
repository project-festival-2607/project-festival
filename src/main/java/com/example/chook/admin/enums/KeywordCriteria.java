package com.example.chook.admin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum KeywordCriteria {

  ALL_WORDS_CONTAINS(0),
  PHRASE_CONTAINS(1),
  EXACT(2);

  private final int priority;

}
