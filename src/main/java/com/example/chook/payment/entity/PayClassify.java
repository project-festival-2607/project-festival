package com.example.chook.payment.entity;

import com.example.chook.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pay_classify")
@Getter
@Setter
@ToString(exclude = "member")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayClassify {

    // 거래 분류 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pay_classify_id")
    private Long payClassifyId;

    // 거래가 발생한 회원
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 회원별 거래 식별자
    // 예: user1-1, user1-2, user2-1 ...
    @Column(name = "transaction_id", nullable = false, unique = true, length = 50)
    private String transactionId;

    // 거래 종류
    // CHARGE / USE / REFUND
    @Column(name = "transaction_type", nullable = false, length = 30)
    private String transactionType;

    // 거래 발생 시간
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}