package com.example.chook.payment.service;
// 토스에서 실제로 취소 성공한 결과를 DB에 반영하는 부분만 따로 분리.
import com.example.chook.member.entity.Member;
import com.example.chook.member.repository.MemberRepository;
import com.example.chook.payment.entity.PayClassify;
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
     *
     * PayClassify는 이번 환불 행동 전체를 대표한다.
     */
    @Transactional
    public void applyRefundToLot(
            Integer cuId,
            int amount,
            Long memberId,
            PayClassify payClassify
    ) {
        // 1. 기본값 검증
        if (cuId == null) {
            throw new IllegalArgumentException("PointCalcUse ID가 없습니다.");
        }
        if (memberId == null) {
            throw new IllegalArgumentException("회원 ID가 없습니다.");
        }
        if (payClassify == null) {
            throw new IllegalArgumentException("PayClassify가 없습니다.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("환불 금액은 0보다 커야 합니다.");
        }


        // 2. 환불 대상 PointCalcUse 조회
        PointCalcUse calcUse = pointCalcUseRepository.findById(cuId)
                        .orElseThrow(() ->
                                new NoSuchElementException("포인트 기록을 찾을 수 없습니다: " + cuId)
                        );


        // 3. 회원 조회
        Member member = memberRepository.findById(memberId)
                        .orElseThrow(() ->
                                new NoSuchElementException("회원을 찾을 수 없습니다: " + memberId)
                        );


        // 4. PayClassify 회원 확인
        if (payClassify.getMember() == null || payClassify.getMember().getId() == null || !payClassify.getMember().getId().equals(memberId)) {
            throw new IllegalStateException(
                    "PayClassify의 회원 정보가 일치하지 않습니다. "
                            + "payClassifyId="
                            + payClassify.getPayClassifyId()
                            + ", memberId="
                            + memberId
            );
        }


        // 5. 거래 종류 확인
        if (!"REFUND".equalsIgnoreCase(payClassify.getTransactionType())) {
            throw new IllegalStateException(
                    "해당 PayClassify는 환불 거래가 아닙니다. "
                            + "transactionType="
                            + payClassify.getTransactionType()
            );
        }


        // 6. 회원과 PointCalcUse의 회원이 같은지 확인
        if (calcUse.getMember() == null || calcUse.getMember().getId() == null || !calcUse.getMember().getId().equals(memberId)) {
            throw new IllegalStateException(
                    "환불 대상 포인트 기록의 회원 정보가 일치하지 않습니다. "
                            + "cuId="
                            + cuId
                            + ", memberId="
                            + memberId
            );
        }


        // 7. 환불 가능한 포인트 확인
        if (calcUse.getLeftPoint() < amount) {
            throw new IllegalStateException(
                    "환불할 포인트보다 남은 포인트가 부족합니다. "
                            + "cuId="
                            + cuId
                            + ", 남은 포인트="
                            + calcUse.getLeftPoint()
                            + ", 환불 요청="
                            + amount
            );
        }


        // 8. 회원 보유 포인트 확인
        if (member.getPoint() < amount) {
            throw new IllegalStateException(
                    "회원 보유 포인트보다 환불 금액이 큽니다. "
                            + "memberId="
                            + memberId
                            + ", 현재 포인트="
                            + member.getPoint()
                            + ", 환불 금액="
                            + amount
            );
        }


        // 9. PointCalcUse 수정
        calcUse.setPointUsed(
                calcUse.getPointUsed() + amount
        );
        calcUse.setLeftPoint(
                calcUse.getLeftPoint() - amount
        );
        pointCalcUseRepository.save(calcUse);


        // 10. PointHistory 환불 이력 생성
        //
        // transactionId를 직접 저장하지 않는다.
        //
        // PayClassify를 외래키로 저장한다.
        //
        // DB:
        // point_history.pay_classify_id
        //             ↓
        // pay_classify.pay_classify_id
        PointHistory history =
                PointHistory.builder()
                        .member(member)
                        .payment(calcUse.getPayment())
                        .payClassify(payClassify)
                        .pType("refund")
                        .pointChanging(-amount)
                        .build();
        pointHistoryRepository.save(history);


        // 11. 회원 보유 포인트 차감
        member.setPoint(member.getPoint() - amount
        );
        memberRepository.save(member);


        // 12. 로그
        log.info(
                "포인트 환불 반영 - "
                        + "memberId={}, "
                        + "paymentId={}, "
                        + "cuId={}, "
                        + "환불={}, "
                        + "잔여={}, "
                        + "현재포인트={}, "
                        + "transactionId={}, "
                        + "payClassifyId={}",

                memberId,
                calcUse.getPayment().getPaymentId(),
                cuId,
                amount,
                calcUse.getLeftPoint(),
                member.getPoint(),
                payClassify.getTransactionId(),
                payClassify.getPayClassifyId()
        );
    }



    // Refund 테이블 저장
    /**
     * Toss의 실제 취소 응답을 Refund 테이블에 저장한다.
     *
     * 한 결제의 한 번의 취소에 대해 호출된다.
     *
     * PayClassify
     *     user1-5 / REFUND
     *
     * 결제 A → 3,000원 취소 → Refund
     * 결제 B → 7,000원 취소 → Refund
     *
     * 두 Refund 모두
     *
     * pay_classify_id = 같은 값을 가진다.
     */
    @Transactional
    public void finalizeRefund(
            Long memberId,
            int requestedAmount,
            int cancelAmount,
            String canceledAt,
            String transactionKey,
            PayClassify payClassify
    ) {
        // 1. 기본값 검증
        if (memberId == null) {
            throw new IllegalArgumentException(
                    "회원 ID가 없습니다."
            );
        }
        if (payClassify == null) {
            throw new IllegalArgumentException(
                    "PayClassify가 없습니다."
            );
        }
        if (requestedAmount <= 0) {
            throw new IllegalArgumentException(
                    "요청 환불 금액은 0보다 커야 합니다."
            );
        }
        if (cancelAmount <= 0) {
            throw new IllegalArgumentException(
                    "실제 취소 금액은 0보다 커야 합니다."
            );
        }


        // 2. PayClassify 회원 확인
        if (payClassify.getMember() == null
                || payClassify.getMember().getId() == null
                || !payClassify.getMember()
                .getId()
                .equals(memberId)) {
            throw new IllegalStateException(
                    "PayClassify의 회원 정보가 일치하지 않습니다."
            );
        }


        // 3. 거래 종류 확인
        if (!"REFUND".equalsIgnoreCase(
                payClassify.getTransactionType()
        )) {
            throw new IllegalStateException(
                    "해당 PayClassify는 환불 거래가 아닙니다."
            );
        }


        // 4. 실제 취소 금액 검증   본적은 없음.
        if (cancelAmount > requestedAmount) {
            throw new IllegalStateException(
                    "Toss 실제 취소 금액이 요청 금액보다 큽니다. "
                            + "요청="
                            + requestedAmount
                            + ", 실제 취소="
                            + cancelAmount
            );
        }


        // 5. 회원 조회
        Member member = memberRepository.findById(memberId).orElseThrow(() ->
                                new NoSuchElementException(
                                        "회원을 찾을 수 없습니다: " + memberId
                                )
                        );

        // 6. Toss transactionKey 확인
        if (transactionKey == null || transactionKey.isBlank()) {
            throw new IllegalStateException(
                    "Toss transactionKey가 없습니다."
            );
        }


        // 7. Toss canceledAt 변환
        LocalDateTime canceledDateTime = null;
        if (canceledAt != null && !canceledAt.isBlank()) {
            try {
                canceledDateTime = OffsetDateTime
                                .parse(canceledAt)
                                .toLocalDateTime();
            } catch (Exception e) {
                throw new IllegalStateException(
                        "Toss canceledAt 형식을 변환할 수 없습니다: " + canceledAt, e
                );
            }
        }


        // 8. 환불 상태
        String refundStatus =
                cancelAmount == requestedAmount
                        ? "completed"
                        : "partially_failed";


        // 9. Refund 객체 생성
        Refund refund =
                Refund.builder().member(member)
                        // ★ 핵심
                        // 이번 환불 행동을 대표하는 PayClassify
                        .payClassify(payClassify)
                        // 이번 결제에서 실제 취소된 금액
                        .refundAmount(cancelAmount)
                        // Toss가 실제로 취소한 금액
                        .refundReal(cancelAmount)
                        // Toss가 알려준 취소 완료 시간
                        .refundComplete(canceledDateTime)
                        // Toss transactionKey
                        .transactionKey(transactionKey)
                        // 환불 상태
                        .refundStatus(refundStatus)
                        .build();



        // 10. DB 저장
        refundRepository.save(refund);


        // 11. 로그
        log.info(
                "환불 이력 저장 - "
                        + "memberId={}, "
                        + "transactionId={}, "
                        + "payClassifyId={}, "
                        + "요청={}, "
                        + "Toss취소금액={}, "
                        + "canceledAt={}, "
                        + "transactionKey={}, "
                        + "상태={}",

                memberId,
                payClassify.getTransactionId(),
                payClassify.getPayClassifyId(),
                requestedAmount,
                cancelAmount,
                canceledAt,
                transactionKey,
                refundStatus
        );
    }
}