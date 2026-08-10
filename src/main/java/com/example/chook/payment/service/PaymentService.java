package com.example.chook.payment.service;
// 결제 승인 결과를 DB(payment/point_history/members.point)에 반영하는,
// 직접 추가한 비즈니스 로직
import com.example.chook.member.entity.Member;
import com.example.chook.payment.entity.Payment;
import com.example.chook.payment.entity.PointCalcUse;
import com.example.chook.payment.entity.PointHistory;
import com.example.chook.payment.entity.Product;
//import com.example.chook.payment.repository.PMemberRepository;
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

    // 토스 승인 응답(response)을 그대로 받아서 payment/point_history/members.point에 반영
    @Transactional
    public void savePaymentResult(Long memberId, JSONObject response) {
        if (memberId == null) {
            throw new IllegalStateException("로그인된 회원이 없어 결제 결과를 저장할 수 없습니다.");
        }

        String orderId = (String) response.get("orderId");
        String paymentKey = (String) response.get("paymentKey");
        String method = (String) response.get("method");
        String status = (String) response.get("status");

        // json-simple은 JSON 숫자를 Long으로 파싱하는 경우가 많아 Number로 안전하게 받음
        Number totalAmountNum = (Number) response.get("totalAmount");
        Integer totalAmount = totalAmountNum.intValue();

        LocalDateTime requestedAt = parseTossDateTime((String) response.get("requestedAt"));
        LocalDateTime approvedAt = parseTossDateTime((String) response.get("approvedAt"));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("회원을 찾을 수 없습니다: " + memberId));

        // 결제 금액이 어느 상품 가격이랑 일치하는지로 상품을 역으로 찾음
        // (payCreationTest.html이 서버에 주문을 미리 등록하지 않고 orderId를 직접 만들어서 보내기 때문)
        Product product = productRepository.findAll().stream()
                .filter(p -> p.getProductPrice().equals(totalAmount))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("결제 금액(" + totalAmount + "원)과 일치하는 상품이 없습니다."));

        Payment payment = Payment.builder()
                .member(member)
                .product(product)
                .pointGet(product.getPointGet())
                .orderId(orderId)
                .paymentKey(paymentKey)
                .method(method)
                .paymentStatus(status)
                .requestedAt(requestedAt)
                .approvedAt(approvedAt)
                .build();
        paymentRepository.save(payment);

        member.setPoint(member.getPoint() + product.getPointGet());
        memberRepository.save(member);

        PointHistory history = PointHistory.builder()
                .member(member)
                .payment(payment)
                .pType("charge")
                .pointChanging(product.getPointGet())
                .build();
        pointHistoryRepository.save(history);

        // 이 결제 건의 FIFO 소비 추적용 "시작 행" - 아직 하나도 안 쓴 상태
        PointCalcUse calcUse = PointCalcUse.builder()
                .payment(payment)
                .member(member)
                .pointGet(product.getPointGet())
                .pointWhere("charge")
                .pointUsed(0)
                .leftPoint(product.getPointGet())
                .build();
        pointCalcUseRepository.save(calcUse);
    }

    // datatesting.html에서 "DB에 실제로 뭐가 저장됐는지" 보여주기 위한 조회용
    public Optional<Map<String, Object>> getOrderResult(String orderId) {
        return paymentRepository.findAll().stream()
                .filter(p -> orderId.equals(p.getOrderId()))
                .findFirst()
                .map(payment -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("paymentId", payment.getPaymentId());
                    result.put("memberId", payment.getMember().getId());
                    result.put("memberUsername", payment.getMember().getUsername());
                    result.put("memberPointAfter", payment.getMember().getPoint());
                    result.put("productName", payment.getProduct().getProductName());
                    result.put("pointGet", payment.getPointGet());
                    result.put("paymentStatus", payment.getPaymentStatus());
                    result.put("paymentKey", payment.getPaymentKey());
                    result.put("method", payment.getMethod());
                    result.put("requestedAt", payment.getRequestedAt());
                    result.put("approvedAt", payment.getApprovedAt());
                    return result;
                });
    }

    // 토스가 주는 "2026-07-24T11:59:11+09:00" 같은 형식을 LocalDateTime으로 변환
    private LocalDateTime parseTossDateTime(String isoString) {
        if (isoString == null) return null;
        return OffsetDateTime.parse(isoString).toLocalDateTime();
    }
}
