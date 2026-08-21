package com.example.chook.admin.dto.payment;

import com.example.chook.admin.enums.payment.summary.PaymentRecordType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SummaryTableDTO {

  Long recordId;
  LocalDateTime recordedAt;

  Long memberId;
  String memberUsername;
  String memberName;

  PaymentRecordType paymentRecordType;
  String rawPaymentRecordType;

  Integer pointChanging;

  Long recruitmentId;
  String recruitmentTitle;

}
