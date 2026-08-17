package com.example.chook.payment.service;

// 토스 API 호출 + FIFO 반복 처리를 담당

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

    private final PointCalcUseRepository pointCalcUseRepository;
    private final RefundLedgerService refundLedgerService;
    private final TossPaymentApiClient tossPaymentApiClient;

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

    // 환불 실행
    /**
     * 오래된 결제부터 FIFO 방식으로 환불한다.
     *
     * 예:
     *
     * 환불 요청 = 10,000
     *
     * 결제 A 남은 포인트 = 3,000
     * 결제 B 남은 포인트 = 7,000
     *
     * -> A에서 3,000 취소
     * -> B에서 7,000 취소
     *
     * 각각의 Toss 응답을 List에 저장해서 Controller로 전달한다.
     */
    public RefundResult refundPoints(Long memberId, int refundAmount) {
        if (memberId == null) {
            throw new IllegalArgumentException("회원 ID가 없습니다.");
        }
        if (refundAmount <= 0) {
            throw new IllegalArgumentException("환불할 포인트는 0보다 커야 합니다.");
        }

        // 환불 가능한 포인트 조회
        List<PointCalcUse> calcUses =
                pointCalcUseRepository
                        .findByMember_IdAndLeftPointGreaterThanOrderByCreatedAtAsc(
                                memberId,
                                0
                        );

        int totalAvailablePoint = calcUses.stream()
                .mapToInt(PointCalcUse::getLeftPoint)
                .sum();

        // 환불 가능 금액 검증
        if (totalAvailablePoint < refundAmount) {
            throw new IllegalStateException(
                    "환불 가능 포인트가 부족합니다. 현재 환불 가능 포인트: "
                            + totalAvailablePoint
                            + ", 요청 포인트: "
                            + refundAmount
            );
        }

        int remainingToRefund = refundAmount;
        int actuallyRefunded = 0;

        // Toss 원본 응답들을 전부 저장
        List<JSONObject> tossResponses = new ArrayList<>();

        // FIFO 환불
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
                    "Toss 결제취소 요청 - memberId={}, paymentKey={}, cancelAmount={}",
                    memberId,
                    paymentKey,
                    amountFromThis
            );

            // Toss 결제 취소 요청
            JSONObject tossResponse =
                    requestTossCancel(
                            paymentKey,
                            amountFromThis
                    );

            // Toss에서 받은 원본 응답을 그대로 보관
            tossResponses.add(tossResponse);

            // Toss 취소 실패
            if (tossResponse.containsKey("error")) {
                log.error(
                        "토스 결제취소 실패 - paymentKey={}, response={}",
                        paymentKey,
                        tossResponse
                );

                break;
            }

            // Toss 응답에서 실제 취소 정보 추출
            JSONObject cancelInfo = getLastCancel(tossResponse);
            if (cancelInfo == null) {
                log.error(
                        "토스 응답에 cancels 정보가 없습니다. paymentKey={}, response={}",
                        paymentKey,
                        tossResponse
                );
                break;
            }


            // Toss가 실제로 취소한 금액
            Object cancelAmountObject = cancelInfo.get("cancelAmount");
            if (!(cancelAmountObject instanceof Number)) {
                log.error(
                        "Toss 응답의 cancelAmount가 없습니다. paymentKey={}, response={}",
                        paymentKey,
                        tossResponse
                );
                break;
            }

            int cancelAmount = ((Number) cancelAmountObject).intValue();

            // Toss가 취소한 날짜/시간
            Object canceledAtObject = cancelInfo.get("canceledAt");

            String canceledAt =
                    canceledAtObject != null
                            ? canceledAtObject.toString()
                            : null;


            // Toss transactionKey
            Object transactionKeyObject = cancelInfo.get("transactionKey");
            String transactionKey =
                    transactionKeyObject != null
                            ? transactionKeyObject.toString()
                            : null;

            // DB 반영

            // PointCalcUse / PointHistory / Member 반영
            refundLedgerService.applyRefundToLot(
                    calcUse.getCuId(),
                    cancelAmount,
                    memberId
            );

            // Refund 테이블에는
            // Toss가 실제로 준 값을 저장
            refundLedgerService.finalizeRefund(
                    memberId,
                    amountFromThis,
                    cancelAmount,
                    canceledAt,
                    transactionKey
            );

            // 다음 결제로 이동
            remainingToRefund -= cancelAmount;
            actuallyRefunded += cancelAmount;
        }

        // 최종 로그
        log.info(
                "환불 처리 종료 - memberId={}, 요청={}, 실제환불={}",
                memberId,
                refundAmount,
                actuallyRefunded
        );

        // 일부 환불 여부
        if (actuallyRefunded < refundAmount) {
            throw new IllegalStateException(
                    "일부만 환불되었습니다. 요청: "
                            + refundAmount
                            + ", 실제 환불: "
                            + actuallyRefunded
            );
        }

        // Controller로 결과 전달
        return new RefundResult(
                actuallyRefunded,
                tossResponses
        );
    }

    // Toss 결제취소 API 호출
    /**
     * Toss가 보내준 JSONObject를 그대로 반환한다.
     */
    private JSONObject requestTossCancel(
            String paymentKey,
            int cancelAmount
    ) {
        try {
            JSONObject cancelRequest =
                    new JSONObject();
            cancelRequest.put(
                    "cancelReason",
                    "포인트 환불"
            );
            cancelRequest.put(
                    "cancelAmount",
                    cancelAmount
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
                    "토스 결제취소 원본 응답 - paymentKey={}, response={}",
                    paymentKey,
                    response
            );
            return response;

        } catch (Exception e) {
            log.error(
                    "토스 결제취소 API 호출 실패 - paymentKey={}",
                    paymentKey,
                    e
            );

            JSONObject errorResponse = new JSONObject();

            errorResponse.put(
                    "error", "Toss 결제취소 API 호출 실패"
            );

            errorResponse.put(
                    "message", e.getMessage()
            );

            return errorResponse;
        }
    }

    // cancels 배열에서 마지막 취소 건 가져오기
    /**
     * Toss 응답의 cancels 배열에서
     * 가장 마지막 취소 정보를 가져온다.
     */
    private JSONObject getLastCancel(JSONObject response) {
        Object cancelsObject = response.get("cancels");

        if (!(cancelsObject instanceof List<?> cancels)) {
            return null;
        }

        if (cancels.isEmpty()) {
            return null;
        }

        Object lastCancel =cancels.get(cancels.size() - 1);

        if (!(lastCancel instanceof JSONObject)) {
            return null;
        }
        return (JSONObject) lastCancel;
    }

    // 환불 결과 DTO
    public record RefundResult(
            int actuallyRefunded,
            List<JSONObject> tossResponses
    ) {
    }
}