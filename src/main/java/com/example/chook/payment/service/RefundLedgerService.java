package com.example.chook.payment.service;

// 토스에서 실제로 취소 성공한 결과를 DB에 반영하는 부분만 따로 분리.
import com.example.chook.member.entity.Member;
import com.example.chook.member.repository.MemberRepository;
import com.example.chook.payment.entity.PointCalcUse;
import com.example.chook.payment.entity.PointHistory;
import com.example.chook.payment.entity.Refund;
import com.example.chook.payment.repository.PointCalcUseRepository;
import com.example.chook.payment.repository.PointHistoryRepository;
import com.example.chook.payment.repository.RefundRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Service
public class RefundLedgerService {

    private final MemberRepository memberRepository;
    private final PointCalcUseRepository pointCalcUseRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final RefundRepository refundRepository;

    // PointCalcUse / PointHistory / Member 반영
    /**
     * Toss 결제취소가 성공한 뒤 호출된다.
     *
     * 해당 결제에서 환불된 포인트를
     *
     * 1. PointCalcUse
     * 2. PointHistory
     * 3. Member
     *
     * 에 반영한다.
     */
    @Transactional
    public void applyRefundToLot(
            Integer cuId,
            int amount,
            Long memberId
    ) {

        // 환불 대상 포인트 기록 조회
        PointCalcUse calcUse =
                pointCalcUseRepository.findById(cuId)
                        .orElseThrow(() ->
                                new NoSuchElementException(
                                        "포인트 기록을 찾을 수 없습니다: "
                                                + cuId
                                )
                        );

        // 회원 조회
        Member member =
                memberRepository.findById(memberId)
                        .orElseThrow(() ->
                                new NoSuchElementException(
                                        "회원을 찾을 수 없습니다: "
                                                + memberId
                                )
                        );

        // 환불한 만큼 PointCalcUse 반영
        calcUse.setPointUsed(
                calcUse.getPointUsed() + amount
        );


        calcUse.setLeftPoint(
                calcUse.getLeftPoint() - amount
        );

        pointCalcUseRepository.save(calcUse);

        // PointHistory 환불 이력 생성
        PointHistory history =
                PointHistory.builder()
                        .member(member)
                        .payment(calcUse.getPayment())
                        .pType("refund")
                        .pointChanging(-amount)
                        .build();


        pointHistoryRepository.save(history);

        // 회원 보유 포인트 차감
        member.setPoint(
                member.getPoint() - amount
        );

        memberRepository.save(member);


        log.info(
                "포인트 환불 반영 - memberId={}, paymentId={}, 환불={}, 잔여={}",
                memberId,
                calcUse.getPayment().getPaymentId(),
                amount,
                calcUse.getLeftPoint()
        );
    }

    // Refund 테이블 저장
    /**
     * Toss의 실제 취소 응답을 Refund 테이블에 저장한다.
     *
     * 이 메서드는 "한 결제의 한 번의 취소"에 대해 호출된다.
     *
     * 예:
     *
     * 결제 A → 3,000원 취소 → Refund 1줄
     * 결제 B → 7,000원 취소 → Refund 1줄
     */
    @Transactional
    public void finalizeRefund(
            Long memberId,
            int requestedAmount,
            int cancelAmount,
            String canceledAt,
            String transactionKey
    ) {

        // 회원 조회
        Member member =
                memberRepository.findById(memberId)
                        .orElseThrow(() ->
                                new NoSuchElementException(
                                        "회원을 찾을 수 없습니다: " + memberId
                                )
                        );
        // Toss canceledAt 변환
        LocalDateTime canceledDateTime = null;

        if (canceledAt != null && !canceledAt.isBlank()) {

            /*
             * Toss:
             *
             * 2022-01-01T00:00:00+09:00
             *
             * Entity:
             *
             * LocalDateTime
             *
             * 따라서 OffsetDateTime으로 읽은 뒤
             * LocalDateTime으로 변환한다.
             */

            canceledDateTime =
                    OffsetDateTime
                            .parse(canceledAt)
                            .toLocalDateTime();
        }

        // 환불 상태
        String refundStatus =
                cancelAmount == requestedAmount
                        ? "completed"
                        : "partially_failed";

        // Refund 객체 생성
        Refund refund =
                Refund.builder()
                        .member(member)
                        // 이번 결제에서 요청한 환불 금액
                        .refundAmount(cancelAmount)

                        // 실제 Toss 취소 금액
                        .refundReal(cancelAmount)

                        // Toss가 알려준 취소 완료 시간
                        .refundComplete(canceledDateTime)

                        // Toss가 알려준 transactionKey
                        .transactionKey(transactionKey)

                        // 환불 상태
                        .refundStatus(refundStatus)

                        .build();


        // DB 저장
        refundRepository.save(refund);

        log.info(
                "환불 이력 저장 - " +
                        "memberId={}, " +
                        "요청={}, " +
                        "Toss취소금액={}, " +
                        "canceledAt={}, " +
                        "transactionKey={}, " +
                        "상태={}",

                memberId,
                requestedAmount,
                cancelAmount,
                canceledAt,
                transactionKey,
                refundStatus
        );
    }
}