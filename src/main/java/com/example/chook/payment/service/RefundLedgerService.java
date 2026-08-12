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
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Service
public class RefundLedgerService {

    private final MemberRepository memberRepository;
    private final PointCalcUseRepository pointCalcUseRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final RefundRepository refundRepository;

    // 토스 취소 API가 이미 "성공"을 돌려준 뒤 호출됨 -> 이 저장은 반드시 되어야 함
    @Transactional
    public void applyRefundToLot(Integer cuId, int amount, Long memberId) {
        //매개변수 3개를 받음.
        PointCalcUse calcUse = pointCalcUseRepository.findById(cuId)
                .orElseThrow(() -> new NoSuchElementException("포인트 기록을 찾을 수 없습니다: " + cuId));
        //DB에서 cuId에 해당하는 PointCalcUse
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("회원을 찾을 수 없습니다: " + memberId));
        //회원을 DB에서

        calcUse.setPointUsed(calcUse.getPointUsed() + amount);
        //환불한만큼 해당 결제 남아있던 포인트 줄이기.  이 amount만큼 사용한것으로 기록

        calcUse.setLeftPoint(calcUse.getLeftPoint() - amount);
        //환불이력 남기기.

        pointCalcUseRepository.save(calcUse); //변경된걸 db로

        PointHistory history = PointHistory.builder() //새로운 환불 이력 객체 생성
                .member(member)
                .payment(calcUse.getPayment())
                .pType("refund")
                .pointChanging(-amount)
                .build(); //refund로 기록
        pointHistoryRepository.save(history);//db에 저장

        member.setPoint(member.getPoint() - amount);
        memberRepository.save(member);
        //이 회원의 현재 보유 포인트에서 환불 처리한 3,000포인트를 차감

        log.info("포인트 환불 반영 - memberId={}, paymentId={}, 환불={}, 잔여={}",
                memberId, calcUse.getPayment().getPaymentId(), amount, calcUse.getLeftPoint());
    }

    // 환불 전체가 끝난 뒤, refund 테이블에 이력 한 줄 남김
    @Transactional
    public void finalizeRefund(Long memberId, int requestedAmount, int actuallyRefunded) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("회원을 찾을 수 없습니다: " + memberId));

        Refund refund = Refund.builder()
                .member(member)
                .refundStatus(actuallyRefunded == requestedAmount ? "completed" : "partially_failed")
                .refundAmount(actuallyRefunded)
                .refundReal(actuallyRefunded) // 1포인트 = 1원
                .refundComplete(LocalDateTime.now())
                .build();
        refundRepository.save(refund);

        log.info("환불 이력 저장 - memberId={}, 요청={}, 실제환불={}, 상태={}",
                memberId, requestedAmount, actuallyRefunded, refund.getRefundStatus());
    }
}
