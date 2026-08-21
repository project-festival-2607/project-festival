package com.example.chook.member.oauth;

import com.example.chook.member.dto.LoginResponseDTO;
import com.example.chook.member.dto.SocialAuthSessionDTO;
import com.example.chook.member.exception.MemberDormantException;
import com.example.chook.member.exception.MemberSuspendedException;
import com.example.chook.member.security.AuthenticationHelper;
import com.example.chook.member.service.MemberService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
// OAuth2 소셜 인증 성공 후속 처리
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    // 기존 회원 조회 및 소셜 로그인 처리 담당 서비스
    private final MemberService memberService;
    private final AuthenticationHelper authenticationHelper; // 추가

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        // Spring Security가 인증 결과로 저장한 Principal을 CustomOAuth2User로 변환
        CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();

        // UserInfo 가져오기
        OAuth2UserInfo userInfo = principal.getUserInfo();

        // 로그인 상태나 가입에 필요한 정보를 저장하기 위한 Http 세션
        HttpSession session = request.getSession();

        // 소셜 계정 연동 시도인지 먼저 확인
        Long linkingMemberId = (Long) session.getAttribute("linkingMemberId");
        if (linkingMemberId != null) {
            session.removeAttribute("linkingMemberId");

            String errorParam = "";
            try {
                memberService.linkSocialAccount(linkingMemberId,
                        userInfo.getProvider(),
                        userInfo.getProviderId());
            } catch (IllegalArgumentException | IllegalStateException e) {
                errorParam = "?linkError=true";
            }

            String username = memberService.getUsernameById(linkingMemberId);
            authenticationHelper.authenticate(username, request, response);

            response.sendRedirect("/mypage/social-login" + errorParam);
            return;
        }

        try {
            // 기존 회원인지 확인하고 로그인 처리
            LoginResponseDTO responseDTO = memberService.loginBySocial(
                    userInfo.getProvider(),
                    userInfo.getProviderId()
            );
            // 로그인 날짜 업데이트
            memberService.updateLastLoginAt(responseDTO.getId());
            // 기존 회원 → SecurityContext에 인증 정보 설정 후 홈으로 리다이렉트
            authenticationHelper.authenticate(responseDTO.getUsername(), request, response);
            response.sendRedirect("/");
        } catch (MemberDormantException e) {
            // 휴면 회원 → 휴면 해제 페이지
            response.sendRedirect("/member/verify");
        } catch (MemberSuspendedException e) {
            // 정지 회원 → 정지 안내 페이지
            response.sendRedirect("/member/suspended");
        } catch (IllegalArgumentException e) {
            session.setAttribute("socialAuthInfo", SocialAuthSessionDTO.builder()
                    .provider(userInfo.getProvider())
                    .providerId(userInfo.getProviderId())
                    .email(userInfo.getEmail())
                    .name(userInfo.getName())
                    .build());

            // OAuth2 로그인 필터가 이미 세션에 저장해버린 임시 인증(CustomOAuth2User)을 제거
            // 회원가입이 완료되기 전까지는 로그인 상태로 취급되면 안 됨
            SecurityContextHolder.clearContext();
            session.removeAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);

            if (memberService.hasJobSeekerAccountWithEmail(userInfo.getEmail())) {
                response.sendRedirect("/member/signup/social/link");
            } else {
                response.sendRedirect("/member/signup/social");
            }
        }
    }

}