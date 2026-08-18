package com.example.chook.payment.entity;

import com.example.chook.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "member_transaction_sequence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberTransactionSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 회원 한 명당 번호 관리 레코드 하나
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "member_id",
            nullable = false,
            unique = true
    )
    private Member member;

    // 이 회원이 마지막으로 사용한 거래 번호
    @Column(name = "last_transaction_id", nullable = false)
    private Long lastTransactionId;
}