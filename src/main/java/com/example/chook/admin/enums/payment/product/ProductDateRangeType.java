package com.example.chook.admin.enums.payment.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProductDateRangeType {

  CREATED_AT("판매개시일시"),
  DELETED_AT("판매종료일시");

  private final String label;
  private final boolean dateOnly;

  ProductDateRangeType(String label) {
    this(label, false);
  }

}
