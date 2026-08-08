package com.example.chook.payment.controller;
// paying/controller/PaymentController
import com.example.chook.member.entity.Member;
import com.example.chook.payment.repository.PMemberRepository;
import com.example.chook.payment.entity.Payment;
import com.example.chook.payment.entity.PointHistory;
import com.example.chook.payment.entity.Product;
import com.example.chook.payment.repository.PaymentRepository;
import com.example.chook.payment.repository.PointHistoryRepository;
import com.example.chook.payment.repository.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

//로그인 구현용
import jakarta.servlet.http.HttpSession;


import com.example.chook.member.dto.LoginResponseDTO;

@Controller
public class PaymentController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());



    @Value("${toss.widget-secret-key}")
    private String widgetSecretKey;

    @Value("${toss.api-secret-key}")
    private String apiSecretKey;

    private final Map<String, String> billingKeyMap = new HashMap<>();

    @Autowired
    private PMemberRepository memberRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @RequestMapping(value = {"/confirm/widget", "/confirm/paying"})
    public ResponseEntity<JSONObject> confirmPayment(HttpServletRequest request, @RequestBody String jsonBody) throws Exception {

// 로그인 회원 확인 --------------------------------
        HttpSession session = request.getSession();

        LoginResponseDTO loginMember =
                (LoginResponseDTO) session.getAttribute("loginMember");

        if (loginMember == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        Long memberId = loginMember.getId();

        System.out.println("현재 로그인 회원 : " + memberId);
// ----------------------------------------


        String secretKey = request.getRequestURI().contains("/confirm/paying") ? apiSecretKey : widgetSecretKey;
        JSONObject requestData = parseRequestData(jsonBody);

        JSONObject response = sendRequest(
                requestData,
                secretKey,
                "https://api.tosspayments.com/v1/payments/confirm"
        );


// 여기부터 추가
        if (!response.containsKey("error")) {

            // paying 저장
            // member point 증가
            // point_history 저장
            try {
                savePaymentResult(memberId, response);
            } catch (Exception e) {
                // 토스 승인 자체는 이미 성공했으니, DB 저장 실패로 응답 자체를 실패 처리하진 않고 로그만 남김
                logger.error("결제 DB 저장 실패 (orderId=" + response.get("orderId") + ")", e);
            }

        }


        int statusCode = response.containsKey("error") ? 400 : 200;

        return ResponseEntity.status(statusCode).body(response);
    }

    // 토스 승인 응답(response)을 그대로 받아서 paying/point_history/members.point에 반영
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
    }

    // 토스가 주는 "2026-07-24T11:59:11+09:00" 같은 형식을 LocalDateTime으로 변환
    private LocalDateTime parseTossDateTime(String isoString) {
        if (isoString == null) return null;
        return OffsetDateTime.parse(isoString).toLocalDateTime();
    }

    // datatesting.html에서 "DB에 실제로 뭐가 저장됐는지" 보여주기 위한 조회용
    @GetMapping("/paying/order/{orderId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getOrderResult(@PathVariable String orderId) {
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
                    return ResponseEntity.ok(result);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @RequestMapping(value = "/confirm-billing")
    public ResponseEntity<JSONObject> confirmBilling(@RequestBody String jsonBody) throws Exception {
        JSONObject requestData = parseRequestData(jsonBody);
        String billingKey = billingKeyMap.get(requestData.get("customerKey"));
        JSONObject response = sendRequest(requestData, apiSecretKey, "https://api.tosspayments.com/v1/billing/" + billingKey);
        return ResponseEntity.status(response.containsKey("error") ? 400 : 200).body(response);
    }

    @RequestMapping(value = "/issue-billing-key")
    public ResponseEntity<JSONObject> issueBillingKey(@RequestBody String jsonBody) throws Exception {
        JSONObject requestData = parseRequestData(jsonBody);
        JSONObject response = sendRequest(requestData, apiSecretKey, "https://api.tosspayments.com/v1/billing/authorizations/issue");

        if (!response.containsKey("error")) {
            billingKeyMap.put((String) requestData.get("customerKey"), (String) response.get("billingKey"));
        }

        return ResponseEntity.status(response.containsKey("error") ? 400 : 200).body(response);
    }

    @RequestMapping(value = "/callback-auth", method = RequestMethod.GET)
    public ResponseEntity<JSONObject> callbackAuth(@RequestParam String customerKey, @RequestParam String code) throws Exception {
        JSONObject requestData = new JSONObject();
        requestData.put("grantType", "AuthorizationCode");
        requestData.put("customerKey", customerKey);
        requestData.put("code", code);

        String url = "https://api.tosspayments.com/v1/brandpay/authorizations/access-token";
        JSONObject response = sendRequest(requestData, apiSecretKey, url);

        logger.info("Response Data: {}", response);

        return ResponseEntity.status(response.containsKey("error") ? 400 : 200).body(response);
    }

    @RequestMapping(value = "/confirm/brandpay", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<JSONObject> confirmBrandpay(@RequestBody String jsonBody) throws Exception {
        JSONObject requestData = parseRequestData(jsonBody);
        String url = "https://api.tosspayments.com/v1/brandpay/payments/confirm";
        JSONObject response = sendRequest(requestData, apiSecretKey, url);
        return ResponseEntity.status(response.containsKey("error") ? 400 : 200).body(response);
    }

    private JSONObject parseRequestData(String jsonBody) {
        try {
            return (JSONObject) new JSONParser().parse(jsonBody);
        } catch (ParseException e) {
            logger.error("JSON Parsing Error", e);
            return new JSONObject();
        }
    }

    private JSONObject sendRequest(JSONObject requestData, String secretKey, String urlString) throws IOException {
        HttpURLConnection connection = createConnection(secretKey, urlString);
        try (OutputStream os = connection.getOutputStream()) {
            os.write(requestData.toString().getBytes(StandardCharsets.UTF_8));
        }

        try (InputStream responseStream = connection.getResponseCode() == 200 ? connection.getInputStream() : connection.getErrorStream();
             Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8)) {
            return (JSONObject) new JSONParser().parse(reader);
        } catch (Exception e) {
            logger.error("Error reading response", e);
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("error", "Error reading response");
            return errorResponse;
        }
    }

    private HttpURLConnection createConnection(String secretKey, String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8)));
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        return connection;
    }

//    @RequestMapping("/")  루트 주소 가로채서 처리함.
//    public String start(HttpSession session) {
//
//        System.out.println("현재 세션 로그인 : "
//                + session.getAttribute("loginMemberId"));
//
//        return "payment/starting";
//    }

    @GetMapping("/widget")
    public String widget() {
        return "payment/widget/index";
    }

    @RequestMapping(value = "/fail", method = RequestMethod.GET)
    public String failPayment(HttpServletRequest request, Model model) {
        model.addAttribute("code", request.getParameter("code"));
        model.addAttribute("message", request.getParameter("message"));
        return "payment/fail";
    }

    // ------------------- 로그인 테스트(결제 자격자만) -------------------

    // starting.html 드롭다운을 채울 "결제 자격 있는 회원" 목록
    // 테스트 계정(test_member)으로 즉시 로그인 처리 후 결제 생성 테스트 페이지로 이동
//    @GetMapping("/login-test/quick")
//    public String quickLoginAsTestMember(HttpSession session) {
//        Member testMember = memberRepository.findByUsername("test_member")
//                .orElseThrow(() -> new IllegalStateException("test_member 계정을 찾을 수 없습니다."));
//
//        session.setAttribute("loginMemberId", testMember.getId());
//
//        System.out.println("테스트 계정으로 로그인 : " + testMember.getId());
//
//        return "redirect:/payCreationTest";
//    }

    @GetMapping("/payCreationTest")
    public String payCreationTest(HttpSession session) {

        if(session.getAttribute("loginMember") == null){
            return "redirect:/member/login";
        }

        return "payment/payCreationTest";
    }

//    @GetMapping("/payCreationTest")
//    public String payCreationTest() {
//        return "payment/payCreationTest";
//    }

    @GetMapping("/widget/success")
    public String success() {
        return "payment/widget/success";
    }

    @GetMapping("/datatesting")
    public String datatesting() {
        return "payment/datatesting";
    }

//    @GetMapping("/login-test/eligible-members")
//    @ResponseBody
//    public List<Map<String, Object>> getEligibleMembers() {
//        return memberRepository.findEligiblePayers().stream()
//                .map(m -> {
//                    Map<String, Object> map = new HashMap<>();
//                    map.put("id", m.getId());
//                    map.put("username", m.getUsername());
//                    map.put("name", m.getName());
//                    return map;
//                })
//                .toList();
//    }
    // (파라미터 타입은 EligibleMemberView로 자동 추론됩니다 - Member 아님)

    //로그인 테스트용
    @PostMapping("/login-test")
    @ResponseBody
    public ResponseEntity<String> loginTest(@RequestParam Long memberId,
                                            HttpSession session) {

        // 서버단에서 다시 한 번 검증 (드롭다운 조작 등으로 다른 memberId가 와도 막음)
        if (!memberRepository.isEligiblePayer(memberId)) {
            return ResponseEntity.status(403).body("결제 권한이 없는 회원입니다.");
        }

        session.setAttribute("loginMemberId", memberId);

        System.out.println("로그인 회원 : " + memberId);

        return ResponseEntity.ok("OK");
    }


}
