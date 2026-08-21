package com.example.chook.payment.controller;
// 토스페이먼츠 깃허브 샘플 원본 기능(빌링/브랜드페이)
// TODO: 빌링 키 관련 기능은 나중에 삭제 예정 (원래 컨트롤러에 있던 메모 그대로 옮김)
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.chook.payment.service.TossPaymentApiClient;

import java.util.HashMap;
import java.util.Map;
//토스 깃허브에서 기본적으로 주던 기능들.
@Slf4j
@RequiredArgsConstructor
@RestController
public class TossSampleController {

    private final TossPaymentApiClient tossPaymentApiClient;

    @Value("${toss.api-secret-key}")
    private String apiSecretKey;

    private final Map<String, String> billingKeyMap = new HashMap<>();

    @RequestMapping(value = "/confirm-billing")
    public ResponseEntity<JSONObject> confirmBilling(@RequestBody String jsonBody) throws Exception {
        JSONObject requestData = tossPaymentApiClient.parseRequestData(jsonBody);
        String billingKey = billingKeyMap.get(requestData.get("customerKey"));
        JSONObject response = tossPaymentApiClient.sendRequest(requestData, apiSecretKey, "https://api.tosspayments.com/v1/billing/" + billingKey);
        return ResponseEntity.status(response.containsKey("error") ? 400 : 200).body(response);
    }

    @RequestMapping(value = "/issue-billing-key")
    public ResponseEntity<JSONObject> issueBillingKey(@RequestBody String jsonBody) throws Exception {
        JSONObject requestData = tossPaymentApiClient.parseRequestData(jsonBody);
        JSONObject response = tossPaymentApiClient.sendRequest(requestData, apiSecretKey, "https://api.tosspayments.com/v1/billing/authorizations/issue");

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
        JSONObject response = tossPaymentApiClient.sendRequest(requestData, apiSecretKey, url);

        log.info("Response Data: {}", response);

        return ResponseEntity.status(response.containsKey("error") ? 400 : 200).body(response);
    }

    @RequestMapping(value = "/confirm/brandpay", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<JSONObject> confirmBrandpay(@RequestBody String jsonBody) throws Exception {
        JSONObject requestData = tossPaymentApiClient.parseRequestData(jsonBody);
        String url = "https://api.tosspayments.com/v1/brandpay/payments/confirm";
        JSONObject response = tossPaymentApiClient.sendRequest(requestData, apiSecretKey, url);
        return ResponseEntity.status(response.containsKey("error") ? 400 : 200).body(response);
    }
}
