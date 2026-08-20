package com.example.chook.admin.enums.payment.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProductStatus {

  ON_SALE("판매중", "product-on-sale"),
  SALE_ENDED("판매종료", "product-sale-ended");

  private final String label;
  private final String badgeClass;
}
