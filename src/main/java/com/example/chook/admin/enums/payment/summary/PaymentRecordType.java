package com.example.chook.admin.enums.payment.summary;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PaymentRecordType {

  CHARGE("충전", "payment-record-type-charge"),
  USE("사용", "payment-record-type-use"),
  REFUND("환불", "payment-record-type-refund");

  private final String label;
  private final String badgeClass;

}
