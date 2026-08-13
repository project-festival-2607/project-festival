package com.example.chook.payment.service;
// 토스 API 호출 + FIFO 반복 처리를 담당
// 실제 DB 반영(트랜잭션이 걸려야 하는 부분)은 RefundLedgerService에 위임
import com.example.chook.payment.entity.PointCalcUse;
import com.example.chook.payment.repository.PointCalcUseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    // payCreationTest.html이 "최대 이만큼 환불 가능"을 보여줄 때 쓰는 조회
    public int getRefundablePoint(Long memberId) {

        System.out.println("===== 환불 가능 포인트 조회 시작 =====");
        System.out.println("memberId = " + memberId);


        List<PointCalcUse> calcUses =
                pointCalcUseRepository.findByMember_IdAndLeftPointGreaterThanOrderByCreatedAtAsc(memberId, 0);
                //멤버 아이디를 가져오면서 그에대한 leftpoint 남은포인트가 0보다 큰거 가져올것. 단 날짜순으로.

        System.out.println("조회된 개수 = " + calcUses.size());


        System.out.println("===== 환불 가능 포인트 조회 종료 =====");


        return calcUses.stream()
                .mapToInt(PointCalcUse::getLeftPoint)
                .sum();
        //가져온 포인트들의 합산을 통해 총 포인트를 구함.
    }

    /**
     * 오래된 결제(payment)부터 순서대로 토스 결제취소 API를 호출해서 환불한다.
     * 여기가 실제로 "환불 가능한 포인트만큼만 환불되게 제한"하는 지점이다 -
     * 요청 금액이 totalAvailablePoint보다 크면 토스 호출 자체를 시작도 안 하고 막는다.
     */
    public int refundPoints(Long memberId, int refundAmount) {

        if (memberId == null) {
            throw new IllegalArgumentException("회원 ID가 없습니다.");
        } //id가 없으면 문제되긴하는데 애초에 받아둬서 걱정 없을듯?
        if (refundAmount <= 0) {
            throw new IllegalArgumentException("환불할 포인트는 0보다 커야 합니다.");
        } //맞말

        List<PointCalcUse> calcUses =
                pointCalcUseRepository.findByMember_IdAndLeftPointGreaterThanOrderByCreatedAtAsc(memberId, 0);

        int totalAvailablePoint = calcUses.stream()
                .mapToInt(PointCalcUse::getLeftPoint)
                .sum();

        // ★ 여기가 "환불 가능한 만큼만 환불되게" 막는 진짜 지점 (서버단 검증)
        if (totalAvailablePoint < refundAmount) {
            throw new IllegalStateException(
                    "환불 가능 포인트가 부족합니다. 현재 환불 가능 포인트: " + totalAvailablePoint
                            + ", 요청 포인트: " + refundAmount
            );
        }
        //환불 요청한 포인트가 본인 소유 포인트보다 많은가??

        int remainingToRefund = refundAmount;   //앞으로 환불해야되는 금액
        int actuallyRefunded = 0;  //지금까지 실제로 환불 완료된 금액

        for (PointCalcUse calcUse : calcUses) {

            if (remainingToRefund <= 0) {
                break;
            } //환불할 금액이 0이 되면 거기서 끝.

            //각 결제에서 얼만큼씩 환불처리할지.
            int amountFromThis = Math.min(calcUse.getLeftPoint(), remainingToRefund);
            String paymentKey = calcUse.getPayment().getPaymentKey();
            //환불 요청할 결제의 paymentkey를 받아옴.

            boolean cancelSucceeded = requestTossCancel(paymentKey, amountFromThis);
            //아래쪽 requestTossCancel 볼것.
//갈까?  < 후길이의 흔적
            if (!cancelSucceeded) {
                log.error("토스 결제취소 실패로 환불 중단 - paymentId={}", calcUse.getPayment().getPaymentId());
                break; // 여기서 멈춤 - 지금까지 성공한 것만 반영하고 종료
                //근데 난 이거 한번도 못봄 ㅇㅇ
            }

            // 토스에서 실제로 취소 성공한 건 - 즉시 DB에 반영 (부분 실패에도 안전)
            refundLedgerService.applyRefundToLot(calcUse.getCuId(), amountFromThis, memberId);

            remainingToRefund -= amountFromThis;
            actuallyRefunded += amountFromThis;
        }

        if (actuallyRefunded > 0) {
            refundLedgerService.finalizeRefund(memberId, refundAmount, actuallyRefunded);
        }//actuallyRefunded = 7,000이면 finalizeRefund(memberId, 7000, 7000);이 될것


        log.info("환불 처리 종료 - memberId={}, 요청={}, 실제환불={}", memberId, refundAmount, actuallyRefunded);

        if (actuallyRefunded < refundAmount) {
            throw new IllegalStateException(
                    "일부만 환불되었습니다. 요청: " + refundAmount + ", 실제 환불: " + actuallyRefunded
            );
        }

        return actuallyRefunded;
    }

    // 토스 결제취소 API 실제 호출
    // curl -X POST /v1/payments/{paymentKey}/cancel
    //      -d '{"cancelReason":"...", "cancelAmount":1000}'
    private boolean requestTossCancel(String paymentKey, int cancelAmount) {
        /*
        * requestTossCancel(String paymentKey, int cancelAmount)
        * 이거말인데 int moneytocancel로 바꾸고
        * 아래쪽에서 int cancelAmount = moneytocancel * 90 / 100으로 하면
        * 90%만 환불처리해주는게????
        *
        * 되긴하는데 DB에 별도로 반영해야겠지.  코드들도 조금씩 변경하고.
        * */
        try {
            JSONObject cancelRequest = new JSONObject();
            cancelRequest.put("cancelReason", "포인트 환불");
            cancelRequest.put("cancelAmount", cancelAmount);

            JSONObject response = tossPaymentApiClient.sendRequest(
                    cancelRequest,
                    widgetSecretKey,
                    "https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel"
            );

            if (response.containsKey("error")) {
                log.error("토스 결제취소 응답 에러 - paymentKey={}, response={}", paymentKey, response);
                return false;
            }
            return true;
        } catch (Exception e) {
            log.error("토스 결제취소 API 호출 실패 - paymentKey={}", paymentKey, e);
            return false;
        }
    }
}
