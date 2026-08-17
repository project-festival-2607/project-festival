package com.example.chook.payment.controller;

import com.example.chook.payment.service.RefundService;
import com.example.chook.payment.service.RefundService.RefundResult;
import com.example.chook.member.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/point")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;

    // 환불 가능한 최대 포인트 조회
    @GetMapping("/refundable")
    public ResponseEntity<?> getRefundablePoint(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        if (userDetails == null) {

            return ResponseEntity.status(401)
                    .body(Map.of(
                            "success", false,
                            "message", "로그인이 필요합니다."));
        }

        Long memberId = userDetails.getId();

        int refundablePoint =
                refundService.getRefundablePoint(memberId);
        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "refundablePoint", refundablePoint
                )
        );
    }

    // 실제 환불 실행
    @PostMapping("/refund")
    public ResponseEntity<?> refund(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam int refundAmount
    ) {
        if (userDetails == null) {

            return ResponseEntity.status(401)
                    .body(Map.of(
                            "success", false,
                            "message", "로그인이 필요합니다."));
        }

        Long memberId = userDetails.getId();

        try {
            RefundResult result =
                    refundService.refundPoints(
                            memberId,
                            refundAmount);
            // ★ 브라우저로 토스 응답까지 전달
            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", "환불이 완료되었습니다.",
                            "refundedAmount",
                            result.actuallyRefunded(),

                            "tossResponses",
                            result.tossResponses()
                    )
            );


        } catch (IllegalStateException |
                 IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "success", false,
                            "message",
                            "환불 처리 중 오류가 발생했습니다."
                    ));
        }
    }
}