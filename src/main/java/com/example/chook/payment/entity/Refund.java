package com.example.chook.payment.entity;

import com.example.chook.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

// chook/payment/entity/Refund
@Entity
@Table(name = "refund")
@Getter
@Setter
@ToString(exclude = {"member", "payClassify"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Refund {

    // 환불번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refund_id")
    private Integer refundId;

    // 환불 당사자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // ★ 이 환불이 어떤 거래에 속하는지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pay_classify_id", nullable = false)
    private PayClassify payClassify;

    // 환불 상태
    @Column(name = "refund_status", length = 30, nullable = false)
    @Builder.Default
    private String refundStatus = "requested";

    // 환불 처리한 포인트
    @Column(name = "refund_amount", nullable = false)
    private Integer refundAmount;

    // 실제 환불 금액
    @Column(name = "refund_real", nullable = false)
    private Integer refundReal;

    // 환불 완료 시간
    @Column(name = "refund_complete")
    private LocalDateTime refundComplete;

    // 토스 취소 거래 키
    @Column(name = "transaction_key", length = 64)
    private String transactionKey;

    // 토스에서 실제 결제가 취소된 시간
    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;
}