package com.example.chook.member.security;

import com.example.chook.member.entity.enums.MemberRole;
import com.example.chook.member.entity.enums.MemberStatus;
import com.example.chook.member.service.MemberService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final MemberService memberService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        if (userDetails.getStatus() == MemberStatus.DORMANT) {
            response.sendRedirect("/member/verify");
            return;
        }
        if (userDetails.getStatus() == MemberStatus.SUSPENDED) {
            response.sendRedirect("/member/suspended");
            return;
        }

        // Admin일 때를 제외하고
        // 로그인 탭에서 고른 회원유형과 실제 role이 다르면, 인증은 성공했더라도 로그인을 무효화한다
        if (userDetails.getRole() != MemberRole.ADMIN) {
            String memberType = request.getParameter("memberType");
            boolean isRecruiter = userDetails.isRecruiter();
            boolean typeMismatch = ("business".equals(memberType) && !isRecruiter)
                    || ("personal".equals(memberType) && isRecruiter);

            if (typeMismatch) {
                SecurityContextHolder.clearContext();
                request.getSession().invalidate();
                response.sendRedirect("/member/login?typeError=true");
                return;
            }
        }

        memberService.updateLastLoginAt(userDetails.getId());

        HttpSession session = request.getSession();

        String redirectAfterLogin =
                (String) session.getAttribute("applicationRedirectAfterLogin");

        if (redirectAfterLogin != null) {
            session.removeAttribute("applicationRedirectAfterLogin");
            response.sendRedirect(redirectAfterLogin);
            return;
        }

        response.sendRedirect("/");
    }

}