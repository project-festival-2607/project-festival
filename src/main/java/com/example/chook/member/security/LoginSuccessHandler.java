package com.example.chook.member.security;

import com.example.chook.member.dto.LoginResponseDTO;
import com.example.chook.member.entity.Member;
import com.example.chook.member.entity.enums.MemberStatus;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Member member = userDetails.getMember();

        // 비밀번호는 이미 검증 완료된 상태 → 여기서 상태를 확인해도 안전함
        if (member.getStatus() == MemberStatus.DORMANT) {
            response.sendRedirect("/member/verify");
            return;
        }
        if (member.getStatus() == MemberStatus.SUSPENDED) {
            response.sendRedirect("/member/suspended");
            return;
        }

        HttpSession session = request.getSession();
        session.setAttribute("loginMember", LoginResponseDTO.builder()
                .id(member.getId())
                .username(member.getUsername())
                .name(member.getName())
                .role(member.getRole())
                .build());

        response.sendRedirect("/");
    }

}