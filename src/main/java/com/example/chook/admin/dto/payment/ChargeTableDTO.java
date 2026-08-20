package com.example.chook.admin.dto.payment;

import com.example.chook.admin.enums.payment.charge.PaymentMethod;
import com.example.chook.admin.enums.payment.charge.PaymentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChargeTableDTO {

  Integer paymentId;

  Long memberId;
  String memberUsername;
  String memberName;

  Integer productId;
  Integer pointGet;
  String productName;

  PaymentMethod paymentMethod;
  String rawPaymentMethod;

  String paymentKey;
  String orderId;

  PaymentStatus paymentStatus;
  String rawPaymentStatus;

  LocalDateTime requestedAt;
  LocalDateTime approvedAt;
  LocalDateTime createdAt;
  LocalDateTime updatedAt;

}
