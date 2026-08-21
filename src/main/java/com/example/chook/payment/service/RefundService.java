package com.example.chook.payment.service;
// 토스 API 호출 + FIFO 반복 처리 + 환불 거래 분류 생성 담당
import com.example.chook.payment.entity.PayClassify;
import com.example.chook.payment.entity.PointCalcUse;
import com.example.chook.payment.repository.PointCalcUseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class RefundService {
//pointCalcUseRepository	point_calc_use DB 조회/접근
//refundLedgerService	환불 결과를 여러 DB 테이블에 실제 반영
//tossPaymentApiClient	Toss 서버에 결제 취소 요청
    private final PointCalcUseRepository pointCalcUseRepository;
    private final RefundLedgerService refundLedgerService;
    private final TossPaymentApiClient tossPaymentApiClient;

    // 회원별 transactionId를 발급하고
    // PayClassify를 생성하는 Service
    private final PayClassifyService payClassifyService;

    @Value("${toss.widget-secret-key}")
    private String widgetSecretKey;



    // 환불 가능한 포인트 조회
    public int getRefundablePoint(Long memberId) {
        List<PointCalcUse> calcUses =
                pointCalcUseRepository
                        .findByMember_IdAndLeftPointGreaterThanOrderByCreatedAtAsc(
                                memberId,
                                0
                        );
        return calcUses.stream()
                .mapToInt(PointCalcUse::getLeftPoint)
                .sum();
    }
//예를 들어 DB에:
//
//결제 A → leftPoint 1,000
//결제 B → leftPoint 10,000
//결제 C → leftPoint 4,000
//
//가 있다면
//
//calcUses
//
//에 A → B → C 순서로



    // 환불 실행
    /**오래된 결제부터 FIFO 방식으로 환불한다.
     *
     * 예:
     * 환불 요청 = 15,000
     *
     * 결제 A = 1,000
     * 결제 B = 10,000
     * 결제 C = 4,000
     *
     * -> A에서 1,000 취소
     * -> B에서 10,000 취소
     * -> C에서 4,000 취소
     *
     *
     * 이 전체 환불 요청은 하나의 PayClassify를 사용한다.
     *
     * 예:
     *
     * PayClassify
     * transactionId = user1-5
     * transactionType = REFUND
     *
     * PointHistory
     *   -1,000  -> pay_classify_id = 5
     *   -10,000 -> pay_classify_id = 5
     *   -4,000  -> pay_classify_id = 5
     *
     * Refund
     *   -> pay_classify_id = 5
     */
    public RefundResult refundPoints(
            Long memberId,
            int refundAmount
    ) {
        if (memberId == null) {
            throw new IllegalArgumentException(
                    "회원 ID가 없습니다."
            );
        }

        if (refundAmount <= 0) {
            throw new IllegalArgumentException(
                    "환불할 포인트는 0보다 커야 합니다."
            );
        }



        // 1. 환불 가능한 포인트 조회
        List<PointCalcUse> calcUses =
                pointCalcUseRepository
                        .findByMember_IdAndLeftPointGreaterThanOrderByCreatedAtAsc(
                                memberId,
                                0
                        );//진짜로 총 환불 가능 포인트 계산
        //위에도 환불 가능한 포인트 조회라고 적힌게 있지만 둘이 다름.



        // 2. 환불 가능 금액 확인
        int totalAvailablePoint =
                calcUses.stream()
                        .mapToInt(PointCalcUse::getLeftPoint)
                        .sum();
        if (totalAvailablePoint < refundAmount) {
            throw new IllegalStateException(
                    "환불 가능 포인트가 부족합니다. "
                            + "현재 환불 가능 포인트: "
                            + totalAvailablePoint
                            + ", 요청 포인트: "
                            + refundAmount
            );
        }



        // 3. ★ 이번 환불 행동을 대표하는 PayClassify 생성
        //
        // 여기서 딱 한 번 만든다.
        //
        // 내부적으로:
        //
        // MemberTransactionSequence
        //        ↓
        // transactionId 발급
        //        ↓
        // PayClassify 저장
        //
        // 예:
        //
        // user1-5 / REFUND
        //
        PayClassify payClassify =
                payClassifyService.createTransaction(
                        memberId,
                        "REFUND"
                );
        log.info(
                "환불 거래 시작 - memberId={}, transactionId={}, payClassifyId={}, 요청금액={}",
                memberId,
                payClassify.getTransactionId(),
                payClassify.getPayClassifyId(),
                refundAmount
        );



        // 4. 환불 처리 준비
        int remainingToRefund = refundAmount;
        int actuallyRefunded = 0;
//환불 예정 금액과  실제 환불 처리된 금액.
        List<JSONObject> tossResponses =
                new ArrayList<>();



        // 5. FIFO 환불
        for (PointCalcUse calcUse : calcUses) {
            if (remainingToRefund <= 0) {
                break;
            }

            // 이번 결제에서 환불할 금액
            int amountFromThis =
                    Math.min(
                            calcUse.getLeftPoint(),
                            remainingToRefund
                    );

            // 해당 결제의 Toss paymentKey
            String paymentKey = calcUse.getPayment().getPaymentKey();

            log.info(
                    "Toss 결제취소 요청 - "
                            + "memberId={}, "
                            + "transactionId={}, "
                            + "paymentKey={}, "
                            + "cancelAmount={}",

                    memberId,
                    payClassify.getTransactionId(),
                    paymentKey,
                    amountFromThis
            );


            // 6. Toss 결제 취소 요청
            JSONObject tossResponse =
                    requestTossCancel(
                            paymentKey,
                            amountFromThis
                    );


            tossResponses.add(tossResponse);



            // 7. Toss 취소 실패
            if (tossResponse.containsKey("error")) {

                log.error(
                        "토스 결제취소 실패 - "
                                + "memberId={}, "
                                + "transactionId={}, "
                                + "paymentKey={}, "
                                + "response={}",

                        memberId,
                        payClassify.getTransactionId(),
                        paymentKey,
                        tossResponse
                );

                break;
            }



            // 8. 마지막 취소 정보 가져오기
            JSONObject cancelInfo =
                    getLastCancel(tossResponse);

            if (cancelInfo == null) {
                log.error(
                        "토스 응답에 cancels 정보가 없습니다. "
                                + "paymentKey={}, response={}",
                        paymentKey,
                        tossResponse
                );
                break;
            }



            // 9. Toss 실제 취소 금액
            Object cancelAmountObject =
                    cancelInfo.get("cancelAmount");

            if (!(cancelAmountObject instanceof Number)) {
                log.error(
                        "Toss 응답의 cancelAmount가 없습니다. "
                                + "paymentKey={}, response={}",
                        paymentKey,
                        tossResponse
                );
                break;
            }
            int cancelAmount = ((Number) cancelAmountObject).intValue();




            // 10. Toss 취소 날짜
            Object canceledAtObject = cancelInfo.get("canceledAt");
            String canceledAt = canceledAtObject != null
                            ? canceledAtObject.toString()
                            : null;



            // 11. Toss transactionKey
            Object transactionKeyObject =
                    cancelInfo.get("transactionKey");
            String transactionKey =
                    transactionKeyObject != null
                            ? transactionKeyObject.toString()
                            : null;


            // 12. PointCalcUse + PointHistory + Member 반영
            //
            //환불에 성공했으니 결과를 db에 반영할것

            refundLedgerService.applyRefundToLot(
                    calcUse.getCuId(),
                    //Toss에서 환불 성공한 cancelAmount만큼,
                    //이 회원의 해당 PointCalcUse 기록과 PointHistory,
                    // 회원 포인트를 DB에 반영하고,
                    // 이번 환불 거래가 어떤 거래인지 payClassify로 연결
                    cancelAmount,
                    memberId,
                    payClassify
            );



            // 13. Refund 테이블 기록
            //
            // 환불됐으니까 환불 이력 자체를 Refund 테이블에 기록
            refundLedgerService.finalizeRefund(
                    memberId,
                    amountFromThis,
                    cancelAmount,
                    canceledAt,
                    transactionKey,
                    payClassify
            );



            // 14. 다음 결제로 이동
            remainingToRefund -= cancelAmount;
            actuallyRefunded += cancelAmount;
        }


        // 15. 최종 로그
        log.info(
                "환불 처리 종료 - "
                        + "memberId={}, "
                        + "transactionId={}, "
                        + "payClassifyId={}, "
                        + "요청={}, "
                        + "실제환불={}",

                memberId,
                payClassify.getTransactionId(),
                payClassify.getPayClassifyId(),
                refundAmount,
                actuallyRefunded
        );



        // 16. 일부 환불 여부 확인
        if (actuallyRefunded < refundAmount) {

            throw new IllegalStateException(
                    "일부만 환불되었습니다. "
                            + "요청: "
                            + refundAmount
                            + ", 실제 환불: "
                            + actuallyRefunded
            );
        }


        // 17. Controller로 결과 전달
        return new RefundResult(
                actuallyRefunded,
                tossResponses
        );
    }


    // Toss 결제취소 API 호출
    private JSONObject requestTossCancel(
            String paymentKey,
            int cancelAmount
    ) {
        try {
            JSONObject cancelRequest = new JSONObject();
            cancelRequest.put(
                    "cancelReason",
                    "포인트 환불"
            );
            cancelRequest.put(
                    "cancelAmount", cancelAmount
            );
            JSONObject response =
                    tossPaymentApiClient.sendRequest(
                            cancelRequest,
                            widgetSecretKey,
                            "https://api.tosspayments.com/v1/payments/"
                                    + paymentKey
                                    + "/cancel"
                    );
            log.info(
                    "토스 결제취소 원본 응답 - " + "paymentKey={}, response={}",
                    paymentKey,
                    response
            );

            return response;
        } catch (Exception e) {
            log.error(
                    "토스 결제취소 API 호출 실패 - "
                            + "paymentKey={}",
                    paymentKey, e
            );

            JSONObject errorResponse = new JSONObject();
            errorResponse.put(
                    "error",
                    "Toss 결제취소 API 호출 실패"
            );
            errorResponse.put(
                    "message",
                    e.getMessage()
            );
            return errorResponse;
        }
    }



    // cancels 배열에서 마지막 취소 건 가져오기
    private JSONObject getLastCancel(JSONObject response) {
        Object cancelsObject = response.get("cancels");
        if (!(cancelsObject instanceof List<?> cancels)) {
            return null;
        }
        if (cancels.isEmpty()) {
            return null;
        }
        Object lastCancel = cancels.get(cancels.size() - 1);
        if (!(lastCancel instanceof JSONObject)) {
            return null;
        }
        return (JSONObject) lastCancel;
    }//Toss가 실제로 얼마를 취소했는지(cancelAmount)와 취소 시간, transactionKey를 가져오기 위해 사용


    // 환불 결과 DTO
    public record RefundResult(
            int actuallyRefunded,
            List<JSONObject> tossResponses
    ) {
    }
}