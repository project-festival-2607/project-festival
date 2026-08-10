package com.example.chook.payment.controller;

import com.example.chook.member.dto.LoginResponseDTO;
import com.example.chook.payment.service.PointReduceService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/point")
@RequiredArgsConstructor
public class PointReduceController {

    private final PointReduceService pointReduceService;

    @PostMapping("/reduce")
    public ResponseEntity<?> reducePoint(
            HttpSession session,
            @RequestParam Long recruitId
    ) {

        // 로그인 회원 확인
        LoginResponseDTO loginMember =
                (LoginResponseDTO) session.getAttribute("loginMember");

        if (loginMember == null) {
            return ResponseEntity.status(401)
                    .body(Map.of(
                            "success", false,
                            "message", "로그인이 필요합니다."
                    ));
        }

        Long memberId = loginMember.getId();

        try {

            // 글쓰기 비용 5,000P
            pointReduceService.reduceHardCodedPoints(
                    memberId,
                    recruitId
            );

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", "포인트가 차감되었습니다."
                    )
            );

        } catch (IllegalStateException e) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));

        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "success", false,
                            "message", "포인트 차감 중 오류가 발생했습니다."
                    ));
        }
    }
}