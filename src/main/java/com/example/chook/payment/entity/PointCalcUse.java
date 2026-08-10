package com.example.chook.payment.entity;
// payment/entity/PointCalcUse
import com.example.chook.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "point_calc_use")
@Getter
@Setter
@ToString(exclude = {"member", "payment"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointCalcUse {

    // 몇 번째 포인트 계산 기록인지
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cu_id")
    private Integer cuId;

    // 결제번호
    // Payment의 paymentId를 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    // 회원 식별자
    // Member의 id를 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", nullable = false)
    private Member member;

    // 해당 결제로 얻은 포인트
    // Payment의 pointGet을 기준으로 저장
    @Column(name = "point_get", nullable = false)
    private Integer pointGet;

    // 포인트 사용처
    @Column(name = "point_where", length = 100)
    private String pointWhere;

    // 사용한 포인트
    @Column(name = "point_used", nullable = false)
    private Integer pointUsed;

    // 해당 결제에서 현재 남아있는 포인트
    @Column(name = "left_point", nullable = false)
    private Integer leftPoint;

    // 포인트 기록 생성 시간
    // FIFO 환불 시 오래된 포인트부터 확인하기 위한 기준
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}