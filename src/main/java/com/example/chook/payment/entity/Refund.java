package com.example.chook.payment.entity;

import com.example.chook.member.entity.Member;
import jakarta.persistence.*;
        import lombok.*;

        import java.time.LocalDateTime;
//chook/paying/entity/Refund
@Entity
@Table(name = "refund")
@Getter
@Setter
@ToString(exclude = "member")  // 순환 참조 방지 대상도 paying→member로 바뀜
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Refund {

    //환불번호  사실상 admin 계정에서나 볼만한것.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refund_id")
    private Integer refundId;

    //환불 당사자 id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")  // members.id 참조 (회원 식별자)
    private Member member;

    //환불상태
    @Column(name = "refund_status", length = 30, nullable = false)
    @Builder.Default
    private String refundStatus = "requested";

    //환불처리한 포인트양
    @Column(name = "refund_amount", nullable = false)
    private Integer refundAmount;

    //실제 환불 금액
    @Column(name = "refund_real", nullable = false)
    private Integer refundReal;

    //환불 완료 시간
    @Column(name = "refund_complete")
    private LocalDateTime refundComplete;

    //생성일
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    //수정일
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}