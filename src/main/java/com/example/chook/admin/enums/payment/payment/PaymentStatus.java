package com.example.chook.admin.enums.payment.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PaymentStatus {

  // 2026년 8월 20일자 토스페이먼츠 코어 API 문서를 기반으로 작성됨.

  READY("READY", "payment-ready"),
  IN_PROGRESS("IN_PROGRESS", "payment-in-progress"),
  WAITING_FOR_DEPOSIT("WAITING_FOR_DEPOSIT", "payment-waiting-for-deposit"),
  DONE("DONE", "payment-done"),
  CANCELED("CANCELED", "payment-canceled"),
  PARTIAL_CANCELED("PARTIAL_CANCELED", "payment-partial-canceled"),
  ABORTED("ABORTED", "payment-aborted"),
  EXPIRED("EXPIRED", "payment-expired");

  private final String label;
  private final String badgeClass;

}