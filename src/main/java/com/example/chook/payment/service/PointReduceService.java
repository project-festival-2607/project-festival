package com.example.chook.payment.service;
//회원이 모집공고에 포인트를 사용하면, 회원 포인트를 차감하고 그 사용 내역을 DB에 기록하는 서비스   FIFO 사용중
import com.example.chook.member.entity.Member;
import com.example.chook.member.repository.MemberRepository;
import com.example.chook.payment.entity.PayClassify;
import com.example.chook.payment.entity.PointCalcUse;
import com.example.chook.payment.entity.PointHistory;
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
    private final MemberRepository memberRepository;
    private final PointCalcUseRepository pointCalcUseRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final RecruitmentRepository recruitmentRepository;

    // ★ 이번 거래를 PayClassify에 생성하는 서비스
    // = 이번 포인트 사용 거래를 하나의 거래로 묶어주는 서비스
    private final PayClassifyService payClassifyService;

    /**
     * 로그인한 회원이 특정 모집공고에
     * 하드코딩된 포인트를 사용한다.
     */
    @Transactional
    public void reduceHardCodedPoints(
            Long memberId,
            Long recruitId
    ) {
        int reducePoint = 14000;
        reducePoints(
                memberId,
                recruitId,
                reducePoint
        );
    }


    /**실제 포인트 차감
     *
     * 오래된 결제에서 발생한 포인트부터 차감한다.
     * 하나의 포인트 사용 요청에서
     * 여러 PointCalcUse가 차감될 수 있다.
     * 이 경우에도 하나의 PayClassify를 생성하고
     * 모든 PointHistory가 같은 PayClassify를 바라보게 한다.
     */
    @Transactional
    public void reducePoints(
            Long memberId,
            Long recruitId,
            int reducePoint
    ) {
        if (memberId == null) {
            throw new IllegalArgumentException(
                    "회원 ID가 없습니다."
            );
        }
        if (recruitId == null) {
            throw new IllegalArgumentException(
                    "모집공고 ID가 없습니다."
            );
        }
        if (reducePoint <= 0) {
            throw new IllegalArgumentException(
                    "차감할 포인트는 0보다 커야 합니다."
            );
        }



        // 1. 회원 조회
        Member member =
                memberRepository.findById(memberId)
                        .orElseThrow(() ->
                                new NoSuchElementException(
                                        "회원을 찾을 수 없습니다: "//사실상 본적은 없는거
                                                + memberId
                                )
                        );



        // 2. 모집공고 조회
        Recruitment recruit =
                recruitmentRepository.findById(recruitId)
                        .orElseThrow(() ->
                                new NoSuchElementException(
                                        "모집공고를 찾을 수 없습니다: "
                                                + recruitId
                                )
                        );



        // 3. 사용 가능한 포인트 조회
        //
        // 오래된 결제부터 FIFO
        List<PointCalcUse> calcUses =
                pointCalcUseRepository
                        .findByMember_IdAndLeftPointGreaterThanOrderByCreatedAtAsc(
                                memberId,
                                0
                        );



        // 4. 총 사용 가능 포인트 확인
        int totalAvailablePoint =
                calcUses.stream()
                        .mapToInt(PointCalcUse::getLeftPoint)
                        .sum();
        if (totalAvailablePoint < reducePoint) {
            throw new IllegalStateException(
                    "포인트가 부족합니다. "
                            + "현재 사용 가능 포인트: "
                            + totalAvailablePoint
                            + ", 차감 요청 포인트: "
                            + reducePoint
            );
        }



        // 5. ★ 이번 포인트 사용 거래를 PayClassify에 생성
        //
        // 예:
        // 4000포인트 사용
        //
        // PointCalcUse A → 3000 차감
        // PointCalcUse B → 1000 차감
        //
        // PointHistory A → PayClassify(user1-4)
        // PointHistory B → PayClassify(user1-4)
        //
        // 즉 하나의 사용 행동 = 하나의 PayClassify
        PayClassify payClassify =
                payClassifyService.createTransaction(
                        memberId,
                        "USE"
                );



        // 6. FIFO 차감
        int remainingToReduce = reducePoint;

        for (PointCalcUse calcUse : calcUses) {
            if (remainingToReduce <= 0) {
                break;
            }
            int currentLeftPoint = calcUse.getLeftPoint();
//각 결제에서 얼마 남았는지 보고 계속 차감해서 요청한만큼 차감.
            // 현재 결제에서 사용할 포인트
            int usePoint =
                    Math.min(
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
            calcUse.setPointWhere(
                    "recruitment"
            );
            pointCalcUseRepository.save(
                    calcUse
            );



            // PointHistory 기록
            // ★ transactionId 직접 저장 X
            // ★ PayClassify를 연결
            //
            // 이번 사용 요청에서 만들어진 모든 history가
            // 동일한 PayClassify를 가진다.
            PointHistory history =
                    PointHistory.builder()
                            .member(member)
                            .recruit(recruit)
                            .payment(calcUse.getPayment())
                            .payClassify(payClassify)
                            .pType("use")
                            .pointChanging(-usePoint)
                            .build();
            pointHistoryRepository.save(
                    history
            );
            //그냥 남기는 로그
            log.info(
                    "포인트 차감 - "
                            + "memberId={}, "
                            + "recruitId={}, "
                            + "paymentId={}, "
                            + "차감={}, "
                            + "잔여={}, "
                            + "transactionId={}",

                    memberId,
                    recruitId,
                    calcUse.getPayment().getPaymentId(),
                    usePoint,
                    calcUse.getLeftPoint(),
                    payClassify.getTransactionId()
            );


            remainingToReduce -= usePoint;
        }



        // 7. 회원 총 포인트 차감
        member.setPoint(
                member.getPoint() - reducePoint
        );
        memberRepository.save(
                member
        );



        // 8. 로그
        log.info(
                "포인트 사용 완료 - "
                        + "memberId={}, "
                        + "recruitId={}, "
                        + "차감={}, "
                        + "현재포인트={}, "
                        + "transactionId={}",

                memberId,
                recruitId,
                reducePoint,
                member.getPoint(),
                payClassify.getTransactionId()
        );
    }
}