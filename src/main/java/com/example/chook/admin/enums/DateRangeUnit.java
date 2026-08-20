package com.example.chook.admin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DateRangeUnit {

  MINUTE("분"),
  HOUR("시간"),
  DAY("일");

  private final String label;

}
