package com.example.chook.admin.enums.payment.refund;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RefundStatus {

  REQUESTED("요청됨", "refund-requested"),
  PARTIALLY_FAILED("일부 실패", "refund-failed"),
  COMPLETED("완료됨", "refund-completed"),
  CANCELED("취소됨", "refund-canceled");

  private final String label;
  private final String badgeClass;

}
