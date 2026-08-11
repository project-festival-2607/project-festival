package com.example.chook.member.controller;

import com.example.chook.member.dto.*;
import com.example.chook.member.exception.MemberDormantException;
import com.example.chook.member.exception.MemberSuspendedException;
import com.example.chook.member.security.CustomUserDetails;
import com.example.chook.member.security.CustomUserDetailsService;
import com.example.chook.member.service.BusinessNumberVerifyService;
import com.example.chook.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final BusinessNumberVerifyService businessNumberVerifyService;
    private final CustomUserDetailsService customUserDetailsService;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    // 로그인 페이지
    @GetMapping("/login")
    public void login() {
    }

    // 로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // 회원가입
    @GetMapping("/signup")
    public String signUpSelect() {
        return "member/signup";
    }

    // 구직자 회원가입
    @GetMapping("/signup/job-seeker")
    public String signUpJobSeekerForm() {
        return "member/signup-job";
    }

    // 구직자 회원가입 처리
    @PostMapping("/signup/job-seeker")
    public String signUpJobSeeker(
            @ModelAttribute JobSeekerSignUpRequestDTO requestDTO,
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes
    ) {
        try {
            LoginResponseDTO responseDTO = memberService.signUpJobSeeker(requestDTO);
            loginAfterSignup(responseDTO.getUsername(), request, response);
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("FailureMsg", e.getMessage());
            return "redirect:/member/signup/job-seeker";
        }
    }

    // 구인자 회원가입
    @GetMapping("/signup/employer")
    public String signUpEmployerForm() {
        return "member/signup-recruit";
    }

    // 구인자 회원가입 처리
    @PostMapping("/signup/employer")
    public String signUpEmployer(
            @ModelAttribute EmployerSignUpRequestDTO requestDTO,
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes
    ) {
        try {
            LoginResponseDTO responseDTO = memberService.signUpEmployer(requestDTO);
            loginAfterSignup(responseDTO.getUsername(), request, response);
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("FailureMsg", e.getMessage());
            return "redirect:/member/signup/employer";
        }
    }

    // 비동기 사업자 번호 인증
    @PostMapping("/business-number/verify")
    @ResponseBody
    public BusinessNumberVerifyResponseDTO verifyBusinessNumber(
            @RequestBody BusinessNumberVerifyRequestDTO requestDTO
    ) {
        return businessNumberVerifyService.verify(requestDTO);
    }

    // 이메일 중복 계정 알림
    @GetMapping("/signup/social/link")
    public String socialLinkSuggestion() {
        return "member/signup-social-connect";
    }

    // 소셜 회원가입 시 회원정보 입력 페이지 맵핑
    @GetMapping("/signup/social")
    public String signupSocialForm(HttpSession session, Model model) {
        SocialAuthSessionDTO authInfo = (SocialAuthSessionDTO) session.getAttribute("socialAuthInfo");
        if (authInfo == null) {
            return "redirect:/member/login";
        }
        model.addAttribute("isSocial", true);
        model.addAttribute("prefillName", authInfo.getName());
        model.addAttribute("prefillEmail", authInfo.getEmail());
        return "member/signup-job";
    }

    // 소셜 회원가입
    @PostMapping("/signup/social")
    public String signupSocial(
            @ModelAttribute SocialSignUpRequestDTO requestDTO,
            HttpSession session,
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes
    ) {
        SocialAuthSessionDTO authInfo = (SocialAuthSessionDTO) session.getAttribute("socialAuthInfo");
        if (authInfo == null) {
            return "redirect:/member/login";
        }

        try {
            LoginResponseDTO responseDTO = memberService.signUpSocial(authInfo, requestDTO);
            session.removeAttribute("socialAuthInfo");
            loginAfterSignup(responseDTO.getUsername(), request, response);
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("FailureMsg", e.getMessage());
            return "redirect:/member/signup/social";
        }
    }

    // 회원가입 성공 직후 프로그래밍 방식으로 로그인 처리
    // (SecurityContext에 인증 정보를 직접 설정하고 세션에 저장해야
    //  헤더의 sec:authorize, @AuthenticationPrincipal이 정상적으로 로그인 상태를 인식한다)
    private void loginAfterSignup(String username, HttpServletRequest request, HttpServletResponse response) {
        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(username);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        securityContextRepository.saveContext(context, request, response);
    }

}