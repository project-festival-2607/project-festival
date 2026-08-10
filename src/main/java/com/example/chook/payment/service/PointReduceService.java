package com.example.chook.payment.service;

import com.example.chook.member.entity.Member;
import com.example.chook.payment.entity.PointCalcUse;
import com.example.chook.payment.entity.PointHistory;
import com.example.chook.payment.repository.PMemberRepository;
import com.example.chook.payment.repository.PointCalcUseRepository;
import com.example.chook.payment.repository.PointHistoryRepository;
import com.example.chook.recruitment.entity.Recruitment;
import com.example.chook.recruitment.repository.RecruitmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Service
public class PointReduceService {

    private final PMemberRepository memberRepository;
    private final PointCalcUseRepository pointCalcUseRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final RecruitmentRepository recruitmentRepository;


    /**
     * 테스트용
     *
     * 로그인한 회원이 특정 모집공고에
     * 하드코딩된 5000포인트를 사용한다.
     */
    @Transactional
    public void reduceHardCodedPoints(Long memberId, Long recruitId) {

        int reducePoint = 14000;

        reducePoints(memberId, recruitId, reducePoint);
    }


    /**
     * 실제 포인트 차감
     *
     * 오래된 결제에서 발생한 포인트부터 차감한다.
     */
    @Transactional
    public void reducePoints(
            Long memberId,
            Long recruitId,
            int reducePoint
    ) {

        if (memberId == null) {
            throw new IllegalArgumentException("회원 ID가 없습니다.");
        }

        if (recruitId == null) {
            throw new IllegalArgumentException("모집공고 ID가 없습니다.");
        }

        if (reducePoint <= 0) {
            throw new IllegalArgumentException("차감할 포인트는 0보다 커야 합니다.");
        }


        // 1. 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "회원을 찾을 수 없습니다: " + memberId
                        )
                );


        // 2. 모집공고 조회
        Recruitment recruit = recruitmentRepository.findById(recruitId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "모집공고를 찾을 수 없습니다: " + recruitId
                        )
                );


        // 3. 사용 가능한 포인트 조회
        // 오래된 결제부터 FIFO
        List<PointCalcUse> calcUses =
                pointCalcUseRepository
                        .findByMember_IdAndLeftPointGreaterThanOrderByCreatedAtAsc(
                                memberId,
                                0
                        );


        // 4. 총 사용 가능 포인트 확인
        int totalAvailablePoint = calcUses.stream()
                .mapToInt(PointCalcUse::getLeftPoint)
                .sum();


        if (totalAvailablePoint < reducePoint) {
            throw new IllegalStateException(
                    "포인트가 부족합니다. " +
                            "현재 사용 가능 포인트: " + totalAvailablePoint +
                            ", 차감 요청 포인트: " + reducePoint
            );
        }


        // 5. FIFO 차감
        int remainingToReduce = reducePoint;

        for (PointCalcUse calcUse : calcUses) {

            if (remainingToReduce <= 0) {
                break;
            }

            int currentLeftPoint = calcUse.getLeftPoint();

            // 현재 결제에서 사용할 포인트
            int usePoint = Math.min(
                    currentLeftPoint,
                    remainingToReduce
            );


            // PointCalcUse 수정
            calcUse.setPointUsed(
                    calcUse.getPointUsed() + usePoint
            );

            calcUse.setLeftPoint(
                    currentLeftPoint - usePoint
            );

            calcUse.setPointWhere("recruitment");

            pointCalcUseRepository.save(calcUse);


            // PointHistory 기록
            //
            // 여기서 payment가 자동으로
            // "이번에 실제 사용된 포인트를 만든 결제"를 가리킨다.
            PointHistory history = PointHistory.builder()
                    .member(member)
                    .recruit(recruit)
                    .payment(calcUse.getPayment())
                    .pType("use")
                    .pointChanging(-usePoint)
                    .build();

            pointHistoryRepository.save(history);


            log.info(
                    "포인트 차감 - memberId={}, recruitId={}, paymentId={}, 차감={}, 잔여={}",
                    memberId,
                    recruitId,
                    calcUse.getPayment().getPaymentId(),
                    usePoint,
                    calcUse.getLeftPoint()
            );


            remainingToReduce -= usePoint;
        }


        // 6. 회원 총 포인트 차감
        member.setPoint(
                member.getPoint() - reducePoint
        );

        memberRepository.save(member);


        log.info(
                "포인트 사용 완료 - memberId={}, recruitId={}, 차감={}, 현재포인트={}",
                memberId,
                recruitId,
                reducePoint,
                member.getPoint()
        );
    }
}