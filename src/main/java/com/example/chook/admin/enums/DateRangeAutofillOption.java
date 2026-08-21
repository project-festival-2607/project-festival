package com.example.chook.admin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DateRangeAutofillOption {

  THIRTY_MINUTES(30, DateRangeUnit.MINUTE),
  ONE_HOUR(1, DateRangeUnit.HOUR),
  SIX_HOURS(6, DateRangeUnit.HOUR),
  TWELVE_HOURS(12, DateRangeUnit.HOUR),
  ONE_DAY(1, DateRangeUnit.DAY),
  SEVEN_DAYS(7, DateRangeUnit.DAY),
  THIRTY_DAYS(30, DateRangeUnit.DAY);

  private final String label;
  private final Integer value;
  private final DateRangeUnit unit;

  DateRangeAutofillOption(Integer value, DateRangeUnit unit) {
    this(String.format("%d%s", value, unit.getLabel()), value, unit);
  }

}
