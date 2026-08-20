package com.example.chook.admin.dto.payment;

import com.example.chook.admin.enums.payment.refund.RefundStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefundTableDTO {

  Integer id;

  Integer refundAmount;   // refund_amount
  Integer refundReal;   // refund_real

  LocalDateTime recordedAt;
  LocalDateTime canceledAt;

  RefundStatus status;
  String rawStatus;             // status 확정을 위해 repository에서 받아오는 원본 문자열
  String transactionKey;

  Long memberId;
  String memberUsername;
  String memberName;

  Long recordId;                // pay_classify_id

}
