package com.example.chook.payment.controller;

import com.example.chook.member.dto.LoginResponseDTO;
import com.example.chook.payment.service.RefundService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/point")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;

    // payCreationTest.html이 "최대 얼마까지 환불 가능한지" 물어볼 때 호출
    @GetMapping("/refundable")
    public ResponseEntity<?> getRefundablePoint(HttpSession session) {

        LoginResponseDTO loginMember = (LoginResponseDTO) session.getAttribute("loginMember");
        //누가 로그인했나.

        if (loginMember == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "로그인이 필요합니다."));
        }

        int refundablePoint = refundService.getRefundablePoint(loginMember.getId());
        //refund service로.

        return ResponseEntity.ok(
                Map.of("success", true, "refundablePoint", refundablePoint)
        );  //http로 이 데이터를 보내겠다.
    }

    // 실제 환불 실행
    @PostMapping("/refund")
    public ResponseEntity<?> refund(
            HttpSession session,
            @RequestParam int refundAmount
    ) {

        LoginResponseDTO loginMember = (LoginResponseDTO) session.getAttribute("loginMember");

        if (loginMember == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "로그인이 필요합니다."));
        }

        Long memberId = loginMember.getId();

        try {
            int actuallyRefunded = refundService.refundPoints(memberId, refundAmount);

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", "환불이 완료되었습니다.",
                            "refundedAmount", actuallyRefunded
                    )
            );

        } catch (IllegalStateException | IllegalArgumentException e) {

            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));

        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "환불 처리 중 오류가 발생했습니다."));
        }
    }
}
