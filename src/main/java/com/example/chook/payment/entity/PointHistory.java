package com.example.chook.payment.entity;

import com.example.chook.member.entity.Member;
import com.example.chook.recruitment.entity.Recruitment;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
//entity_yetdunguut/PointHistory
@Entity
@Table(name = "point_history")
@Getter
@Setter
@ToString(exclude = {"member", "recruit", "paying"})  // 양방향 참조시 순환 참조 방지
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointHistory {

    @Id//포인트 식별용...admin에서 보든가 말든가 하겠지.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_history_id")
    private Integer pointHistoryId;

    //회원 식별자Member의 id랑 연계해서 Member의 point를 업데이트 할거임.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")  // members.id 참조 (회원 식별자)
    private Member member;

    //모집공고 id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruit_id")  // p_type='use'일 때만 값 존재 (nullable)
    private Recruitment recruit;

    //결제번호id  인데 쓸모 있을진 모르겠음.... payment에서 받아오는거긴한데
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")  // p_type='charge'/'refund'일 때만 값 존재 (nullable)
    private Payment payment;

    //타입.  환불 사용 충전 3종류로 분류.
    @Column(name = "p_type", nullable = false, length = 50)
    private String pType;  // charge / use / refund

    //포인트의 변화... +10000   -3000 이런식으로 표기...
    //Member의 point에 업데이트해줄용도임.
    @Column(name = "point_changing", nullable = false)
    private Integer pointChanging;

    //기록 생성일.
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    //이건 뭐냐???
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}