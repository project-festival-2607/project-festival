package com.example.chook.admin.dto.payment;

import com.example.chook.admin.enums.payment.payment.PaymentMethod;
import com.example.chook.admin.enums.payment.payment.PaymentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentTableDTO {

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
