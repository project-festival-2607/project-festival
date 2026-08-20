package com.example.chook.admin.enums.payment.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PaymentDateRangeType {

  REQUESTED_AT("결제요청시각"),
  APPROVED_AT("결제승인시각"),
//  CREATED_AT("생성일시"),
//  UPDATED_AT("수정일시")
  ;

  private final String label;
  private final boolean dateOnly;

  PaymentDateRangeType(String label) {
    this(label, false);
  }

}
