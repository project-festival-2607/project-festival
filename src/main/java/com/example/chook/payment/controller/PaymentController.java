package com.example.chook.payment.controller;
// payment/controller/PaymentController - 실제로 사용하는 라우팅만 남긴 버전
import com.example.chook.member.dto.LoginResponseDTO;
import com.example.chook.payment.service.PaymentService;
import com.example.chook.payment.service.TossPaymentApiClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Controller
public class PaymentController {

    private final TossPaymentApiClient tossPaymentApiClient;
    private final PaymentService paymentService;

    @Value("${toss.widget-secret-key}")
    private String widgetSecretKey;

    @Value("${toss.api-secret-key}")
    private String apiSecretKey;

    // DB에 반영하는 부분(paymentService)을 분리해서 순서대로 호출
    @RequestMapping(value = {"/confirm/widget", "/confirm/payment"})
    public ResponseEntity<JSONObject> confirmPayment(HttpServletRequest request, @RequestBody String jsonBody) throws Exception {

        HttpSession session = request.getSession();
        LoginResponseDTO loginMember = (LoginResponseDTO) session.getAttribute("loginMember");

        if (loginMember == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        Long memberId = loginMember.getId();
        log.info("현재 로그인 회원 : {}", memberId);

        String secretKey = request.getRequestURI().contains("/confirm/payment") ? apiSecretKey : widgetSecretKey;
        JSONObject requestData = tossPaymentApiClient.parseRequestData(jsonBody);
        JSONObject response = tossPaymentApiClient.sendRequest(requestData, secretKey, "https://api.tosspayments.com/v1/payments/confirm");

        if (!response.containsKey("error")) {
            try {
                paymentService.savePaymentResult(memberId, response);
            } catch (Exception e) {
                // 토스 승인 자체는 이미 성공했으니, DB 저장 실패로 응답 자체를 실패 처리하진 않고 로그만 남김
                log.error("결제 DB 저장 실패 (orderId={})", response.get("orderId"), e);
            }
        }

        int statusCode = response.containsKey("error") ? 400 : 200;
        return ResponseEntity.status(statusCode).body(response);
    }

    // datatesting.html에서 "DB에 실제로 뭐가 저장됐는지" 보여주기 위한 조회용
    @GetMapping("/payment/order/{orderId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getOrderResult(@PathVariable String orderId) {
        return paymentService.getOrderResult(orderId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/widget")
    public String widget() {
        return "payment/widget/index";
    }

    @GetMapping("/widget/success")
    public String success() {
        return "payment/realactive/success";
    }

    @RequestMapping(value = "/fail", method = RequestMethod.GET)
    public String failPayment(HttpServletRequest request, Model model) {
        model.addAttribute("code", request.getParameter("code"));
        model.addAttribute("message", request.getParameter("message"));
        return "payment/realactive/fail";
    }

    @GetMapping("/payCreationTest")
    public String payCreationTest(HttpSession session) {
        if (session.getAttribute("loginMember") == null) {
            return "redirect:/member/login";
        }
        return "payment/realactive/payCreationTest";
    }

    @GetMapping("/datatesting")
    public String datatesting() {
        return "payment/realactive/datatesting";
    }

    @GetMapping("/refund")
    public String refund(HttpSession session) {

        // 로그인 여부 확인
        if (session.getAttribute("loginMember") == null) {
            return "redirect:/member/login";
        }

        // templates/payment/realactive/refund_widget_snippet.html
        return "payment/realactive/refund_widget_snippet";
    }
}