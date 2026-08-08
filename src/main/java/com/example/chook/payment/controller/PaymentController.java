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
//요청을 받으면 해당 부분으로 보내버림.
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

    // 결제 승인 - 토스와 통신하는 부분(tossPaymentApiClient)과
    // DB에 반영하는 부분(paymentService)을 분리해서 순서대로 호출
    // 주의: 원본에 있던 경로를 그대로 뒀어요 - "/confirm/paying"이 오타인지
    //       실제로 프론트에서 이 경로를 부르고 있는 건지 한 번 확인해보세요.
    @RequestMapping(value = {"/confirm/widget", "/confirm/paying"})
    public ResponseEntity<JSONObject> confirmPayment(HttpServletRequest request, @RequestBody String jsonBody) throws Exception {
        //결제승인용 데이터 paymentkey orderid amount를 받음.
        //세션에서 로그인한 회원 확인.  TossPaymentApiClient에게 토스쪽으로 승인 요청 해달라고 부탁.
        //성공시 savePaymentResult()한테 db에 저장해달라고 부탁.
        HttpSession session = request.getSession();
        LoginResponseDTO loginMember = (LoginResponseDTO) session.getAttribute("loginMember");

        if (loginMember == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        Long memberId = loginMember.getId();
        log.info("현재 로그인 회원 : {}", memberId);

        String secretKey = request.getRequestURI().contains("/confirm/paying") ? apiSecretKey : widgetSecretKey;
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
    @GetMapping("/paying/order/{orderId}")
    @ResponseBody
    //사실상 조회 요청.
    public ResponseEntity<Map<String, Object>> getOrderResult(@PathVariable String orderId) {
        return paymentService.getOrderResult(orderId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //이 밑에건 다른 페이지로 연결용임.
    @GetMapping("/widget")
    public String widget() {
        return "payment/widget/index";
    }

    @GetMapping("/widget/success")
    public String success() {
        return "payment/widget/success";
    }

    @RequestMapping(value = "/fail", method = RequestMethod.GET)
    public String failPayment(HttpServletRequest request, Model model) {
        model.addAttribute("code", request.getParameter("code"));
        model.addAttribute("message", request.getParameter("message"));
        return "payment/fail";
    }

    @GetMapping("/payCreationTest")
    public String payCreationTest(HttpSession session) {
        if (session.getAttribute("loginMember") == null) {
            return "redirect:/member/login";
        }
        return "payment/payCreationTest";
    }

    @GetMapping("/datatesting")
    public String datatesting() {
        return "payment/datatesting";
    }
}
