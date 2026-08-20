package com.example.chook.admin.enums.payment.pointhistory;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PointHistoryDateRangeType {

  RECORDED_AT("게시일시");

  private final String label;
  private final boolean dateOnly;

  PointHistoryDateRangeType(String label) {
    this(label, true);
  }

}
