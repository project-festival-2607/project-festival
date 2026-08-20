package com.example.chook.admin.enums.payment.refund;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RefundDateRangeType {

  RECORDED_AT("환불일시"),
  CANCELED_AT("취소일시");

  private final String label;
  private final boolean dateOnly;

  RefundDateRangeType(String label) {
    this(label, true);
  }

}

