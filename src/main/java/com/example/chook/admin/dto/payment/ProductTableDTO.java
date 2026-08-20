package com.example.chook.admin.dto.payment;

import com.example.chook.admin.enums.payment.product.ProductStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductTableDTO {

  Integer productId;
  Integer pointGet;
  Integer productPrice;
  String productName;
  ProductStatus status;

  LocalDateTime createdAt;
  LocalDateTime deletedAt;

}
