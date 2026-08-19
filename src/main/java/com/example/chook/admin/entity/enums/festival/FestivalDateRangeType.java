package com.example.chook.admin.entity.enums.festival;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FestivalDateRangeType {

  OVERLAPPING("개최기간 겹침"),
  CONTAINED_IN("개최기간 전체 포함");

  private final String label;
  private final boolean dateOnly;

  FestivalDateRangeType(String label) {
    this(label, true);
  }
}
