package com.example.chook.admin.enums.payment.summary;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SummaryDateRangeType {

  RECORDED_AT("기록일시");

  private final String label;
  private final boolean dateOnly;

  SummaryDateRangeType(String label) {
    this(label, false);
  }

}
