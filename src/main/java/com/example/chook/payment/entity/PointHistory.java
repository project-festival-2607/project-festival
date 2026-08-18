package com.example.chook.payment.entity;

import com.example.chook.member.entity.Member;
import com.example.chook.recruitment.entity.Recruitment;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

// entity/PointHistory
@Entity
@Table(name = "point_history")
@Getter
@Setter
@ToString(exclude = {"member", "recruit", "payment", "payClassify"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointHistory {

    // 포인트 이력 식별번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_history_id")
    private Integer pointHistoryId;

    // 회원
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // 모집공고
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruit_id")
    private Recruitment recruit;

    // 결제
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    // ★ 이 포인트 기록이 어떤 거래에 속하는지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pay_classify_id", nullable = false)
    private PayClassify payClassify;

    // 포인트 변화 종류
    // charge / use / refund / charge_admin
    @Column(name = "p_type", nullable = false, length = 50)
    private String pType;

    // 포인트 변화량
    // +10000 / -3000 등
    @Column(name = "point_changing", nullable = false)
    private Integer pointChanging;

    // 기록 생성일
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}