package com.example.chook.admin.enums.payment.use;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UseDateRangeType {

  USED_AT("사용일시");

  private final String label;
  private final boolean dateOnly;

  UseDateRangeType(String label) {
    this(label, false);
  }

}
