package com.example.chook.admin.dto.payment;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UseTableDTO {

  Integer pointHistoryId;

  LocalDateTime usedAt;
  Integer pointChanging;

  Long memberId;
  String memberUsername;
  String memberName;

  Integer paymentId;

  Long recruitmentId;
  String recruitmentTitle;

  Long recordId;

}
