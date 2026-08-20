package com.example.chook.admin.entity.enums.pointhistory;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PointHistoryTransactionType {

  CHARGE("충전", "payment-charge"),
  USE("사용", "payment-use"),
  REFUND("환불", "payment-refund");

  private final String label;
  private final String badgeClass;

}
