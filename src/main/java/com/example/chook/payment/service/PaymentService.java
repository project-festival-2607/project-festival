package com.example.chook.payment.service;
// 결제 승인 결과를 DB(payment/point_history/members.point)에 반영하는,
// 직접 추가한 비즈니스 로직
import com.example.chook.member.entity.Member;
import com.example.chook.payment.entity.Payment;
import com.example.chook.payment.entity.PayClassify;
import com.example.chook.payment.entity.PointCalcUse;
import com.example.chook.payment.entity.PointHistory;
import com.example.chook.payment.entity.Product;
import com.example.chook.member.repository.MemberRepository;
import com.example.chook.payment.repository.PaymentRepository;
import com.example.chook.payment.repository.PointCalcUseRepository;
import com.example.chook.payment.repository.PointHistoryRepository;
import com.example.chook.payment.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentService {

    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final PointCalcUseRepository pointCalcUseRepository;

    // 회원별 거래 생성 및 PayClassify 관리
    private final PayClassifyService payClassifyService;

    // 토스 결제 승인 결과를 DB에 저장
    @Transactional
    public void savePaymentResult(
            Long memberId,
            JSONObject response
    ) {
        if (memberId == null) {
            throw new IllegalStateException(
                    "로그인된 회원이 없어 결제 결과를 저장할 수 없습니다."
            );
        }

        // Toss 응답 데이터 추출    toss 응답중에 이런 애들이 있는데 내가 그거 뽑아쓰는거임
        String orderId = (String) response.get("orderId");
        String paymentKey = (String) response.get("paymentKey");
        String method = (String) response.get("method");
        String status = (String) response.get("status");


        // json-simple은 JSON 숫자를 Long으로 파싱할 수 있으므로
        // Number로 받아서 처리
        Number totalAmountNum = (Number) response.get("totalAmount");
        if (totalAmountNum == null) {
            throw new IllegalArgumentException(
                    "Toss 응답에 결제 금액(totalAmount)이 없습니다." //사실 이거 본적 없음.
            );
        }
//totalAmount와 시간 값들을 DB에 넣기 좋은 자바 타입으로 변환
        Integer totalAmount = totalAmountNum.intValue();
        LocalDateTime requestedAt = parseTossDateTime((String) response.get("requestedAt"));
        LocalDateTime approvedAt = parseTossDateTime((String) response.get("approvedAt"));


        // 회원 조회 (어지간해서는 됨)
        Member member = memberRepository.findById(memberId)
                        .orElseThrow(() ->
                                new NoSuchElementException(
                                        "회원을 찾을 수 없습니다: "
                                                + memberId
                                )
                        );


        // 결제 금액과 일치하는 상품 조회
        Product product = productRepository.findAll()
                        .stream()
                        .filter(p ->
                                p.getProductPrice()
                                        .equals(totalAmount)
                        )
                        .findFirst()
                        .orElseThrow(() ->
                                new NoSuchElementException(
                                        "결제 금액("
                                                + totalAmount
                                                + "원)과 일치하는 상품이 없습니다." //사실 이거 한번도 본적 없음)
                                )
                        );


        // 1. Payment 저장
        Payment payment =
                Payment.builder()
                        .member(member)
                        .product(product)
                        .pointGet(product.getPointGet())
                        .productName(product.getProductName())  // ★ 이 순간의 값을 복사, 이후 Product가 바뀌어도 이 값은 그대로 남음
                        .orderId(orderId)
                        .paymentKey(paymentKey)
                        .method(method)
                        .paymentStatus(status)
                        .requestedAt(requestedAt)
                        .approvedAt(approvedAt)
                        .build();

        paymentRepository.save(payment);


        // 2. 회원 포인트 증가
        member.setPoint(
                member.getPoint()
                        + product.getPointGet()
        );
        memberRepository.save(member);



        // 3. PayClassify 생성
        //
        // 이번 결제 = 하나의 거래
        // memberId = 1
        // transactionType = "charge"
        //
        // PayClassify
        //      transactionId = user1-1
        //      transactionType = charge  충전이니까.  transactionId는 일단 만들긴했는데 실제 효용은 몰?루
        //  transactionId는 user + "멤버 id"+ "-" + 몇번째인지 member_transaction_sequence 를 통해 last_transaction_id를 받음
        PayClassify payClassify =
                payClassifyService.createTransaction(
                        memberId,
                        "charge"
                );


        // 4. PointHistory 저장
        //
        // transactionId를 직접 저장하지 않고
        // PayClassify를 FK로 연결한다.
        PointHistory history =
                PointHistory.builder()
                        .member(member)
                        .payment(payment)
                        .payClassify(payClassify)
                        .pType("charge")
                        .pointChanging(product.getPointGet())
                        .build();
        pointHistoryRepository.save(history);


        // 5. FIFO 소비 추적용 PointCalcUse 저장
        PointCalcUse calcUse =
                PointCalcUse.builder()
                        .payment(payment)
                        .member(member)
                        .pointGet(product.getPointGet())
                        .pointWhere("charge")
                        .pointUsed(0)
                        .leftPoint(product.getPointGet())
                        .build();
        pointCalcUseRepository.save(calcUse);


        // 6. 로그
        log.info(
                "포인트 충전 완료 - " +
                        "memberId={}, " +
                        "paymentId={}, " +
                        "transactionId={}, " +
                        "payClassifyId={}, " +
                        "point={}",

                memberId,
                payment.getPaymentId(),
                payClassify.getTransactionId(),
                payClassify.getPayClassifyId(),
                product.getPointGet()
        );
    }



    // 주문 결과 조회
    //paymentController.java     @GetMapping("/payment/order/{orderId}")   나중에 날려야할것이 될지도
    public Optional<Map<String, Object>> getOrderResult(
            String orderId
    ) {
        return paymentRepository.findAll()
                .stream()
                .filter(p ->
                        orderId.equals(p.getOrderId())
                )
                .findFirst()
                .map(payment -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put(
                            "paymentId",
                            payment.getPaymentId()
                    );
                    result.put(
                            "memberId",
                            payment.getMember().getId()
                    );
                    result.put(
                            "memberUsername",
                            payment.getMember().getUsername()
                    );
                    result.put(
                            "memberPointAfter",
                            payment.getMember().getPoint()
                    );
                    result.put(
                            "productName",
                            payment.getProductName()
                    );
                    result.put(
                            "pointGet",
                            payment.getPointGet()
                    );
                    result.put(
                            "paymentStatus",
                            payment.getPaymentStatus()
                    );
                    result.put(
                            "paymentKey",
                            payment.getPaymentKey()
                    );
                    result.put(
                            "method",
                            payment.getMethod()
                    );
                    result.put(
                            "requestedAt",
                            payment.getRequestedAt()
                    );
                    result.put(
                            "approvedAt",
                            payment.getApprovedAt()
                    );
                    return result;
                });
    }



    // Toss 날짜 변환
    private LocalDateTime parseTossDateTime(
            String isoString
    ) {
        if (isoString == null) {
            return null;
        }
        return OffsetDateTime
                .parse(isoString)
                .toLocalDateTime();
    }
}