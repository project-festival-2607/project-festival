package com.example.chook.member.oauth;

import com.example.chook.member.dto.LoginResponseDTO;
import com.example.chook.member.dto.SocialAuthSessionDTO;
import com.example.chook.member.exception.MemberDormantException;
import com.example.chook.member.exception.MemberSuspendedException;
import com.example.chook.member.service.MemberService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
// OAuth2 소셜 인증 성공 후속 처리
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    // 기존 회원 조회 및 소셜 로그인 처리 담당 서비스
    private final MemberService memberService;

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

        try {
            // 기존 회원인지 확인하고 로그인 처리
            LoginResponseDTO responseDTO = memberService.loginBySocial(
                    userInfo.getProvider(),
                    userInfo.getProviderId()
            );
            // 기존 회원 → 로그인 정보를 세션에 저장하고 홈으로 리다이렉트
            session.setAttribute("loginMember", responseDTO);
            response.sendRedirect("/");
        } catch (MemberDormantException e) {
            // 휴면 회원 → 휴면 해제 페이지
            response.sendRedirect("/member/verify");
        } catch (MemberSuspendedException e) {
            // 정지 회원 → 정지 안내 페이지
            response.sendRedirect("/member/suspended");
        } catch (IllegalArgumentException e) {
            // 연동된 계정이 없음 → 신규 소셜 가입 플로우
            session.setAttribute("socialAuthInfo", SocialAuthSessionDTO.builder()
                    .provider(userInfo.getProvider())
                    .providerId(userInfo.getProviderId())
                    .email(userInfo.getEmail())
                    .name(userInfo.getName())
                    .build());
            if (memberService.hasJobSeekerAccountWithEmail(userInfo.getEmail())) {
                // 같은 이메일을 사용하는 기존 개인 회원 계정이 있는지 확인
                // 있다면 기존 계정과 소셜 계정을 연결하는 가입 플로우로 이동
                response.sendRedirect("/member/signup/social/link");
            } else {
                // 해당 이메일로 기존 계정이 없다면
                // 새로운 소셜 회원가입 플로우로 이동
                response.sendRedirect("/member/signup/social");
            }
        }
    }

}