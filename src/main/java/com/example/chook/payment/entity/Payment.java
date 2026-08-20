package com.example.chook.payment.entity;

import com.example.chook.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
// payment/entity/Payment
@Entity
@Getter
@Setter
@ToString(exclude = {"member", "product"})  // 양방향 참조시 순환 참조 방지
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    //결제번호 admin 계정 아니면 볼일 없을 예정.  단순 분류용.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Integer paymentId;

    //누가 결제했는지 분류하기위한 id...member의 private Long id;를 받아오는거임.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")  // members.id 참조 (회원 식별자)
    private Member member;

    //뭘 샀는지 확인하기위한 상품번호.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    // ★ 추가: 결제 시점에 한 번만 복사해서 저장, 이후로는 Product와 연결 끊김
    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    //얻는 포인트양point history로 보내야됨.
    @Column(name = "point_get", nullable = false)
    private Integer pointGet;

    //결제 수단   카카오페이면 카카오페이라고 리턴되옴
    @Column(length = 50)
    private String method;

    //토스 결제 키값.
    @Column(name = "payment_key", length = 200, unique = true)
    private String paymentKey;

    //토스 주문번호
    @Column(name = "order_id", length = 200, unique = true)
    private String orderId;

    //토스 결제키값과 주문번호는 어쩌면 환불을 위해 필요할지도 모르니 일단 그대로 진행하겠음


    //결제 상태.  ready에서 toss 문서 확인.
    @Column(name = "payment_status", length = 30, nullable = false)
    @Builder.Default
    private String paymentStatus = "READY";

    //결제 요청시간.
    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    //결제 승인시간
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

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
