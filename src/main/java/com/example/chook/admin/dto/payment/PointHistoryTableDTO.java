package com.example.chook.admin.dto.payment;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PointHistoryTableDTO {

  Long recordId;                    // pay_classify의 pay_classify_id
  LocalDateTime recordedAt;         // pay_classify의 created_at
  int pointChanging;                // point_history에서 pay_classify_id가 같은 모든 항목 SUM(point_changing)

  List<Long> pointHistoryIdList;    // point_history에서 pay_classify_id가 같은 모든 point_history_id
  List<Long> paymentIdList;         // point_history의 payment_id
  List<Long> refundIdList;          // refund에서 pay_classify_id가 같은 모든 refund_id

  Long memberId;                    // pay_classify의 member_id
  String memberUsername;            // members의 username
  String memberName;                // members의 name

  Long recruitmentId;               // point_history의 recruit_id
  String recruitmentTitle;          // recruitment의 title

}
