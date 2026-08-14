package com.example.chook.member.controller;

import com.example.chook.member.dto.*;
import com.example.chook.member.security.AuthenticationHelper;
import com.example.chook.member.security.CustomUserDetails;
import com.example.chook.member.service.BusinessNumberVerifyService;
import com.example.chook.member.service.MemberService;
import com.example.chook.member.service.PhoneVerificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final AuthenticationHelper authenticationHelper;
    private final PhoneVerificationService phoneVerificationService;

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
            memberService.updateLastLoginAt(responseDTO.getId());
            authenticationHelper.authenticate(responseDTO.getUsername(), request, response);
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
            memberService.updateLastLoginAt(responseDTO.getId());
            authenticationHelper.authenticate(responseDTO.getUsername(), request, response);
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
            memberService.updateLastLoginAt(responseDTO.getId());
            authenticationHelper.authenticate(responseDTO.getUsername(), request, response);
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("FailureMsg", e.getMessage());
            return "redirect:/member/signup/social";
        }
    }

    // 전화번호 인증번호 발송
    @PostMapping("/phone/send-code")
    @ResponseBody
    public PhoneVerificationResponseDTO sendPhoneCode(
            @RequestBody PhoneSendCodeRequestDTO requestDTO,
            HttpSession session
    ) {
        try {
            phoneVerificationService.sendCode(requestDTO.getPhone(), session);
            return PhoneVerificationResponseDTO.builder()
                    .success(true)
                    .build();
        } catch (IllegalStateException e) {
            return PhoneVerificationResponseDTO.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        }
    }

    // 전화번호 인증번호 확인
    @PostMapping("/phone/verify-code")
    @ResponseBody
    public PhoneVerificationResponseDTO verifyPhoneCode(
            @RequestBody PhoneVerifyCodeRequestDTO requestDTO,
            HttpSession session
    ) {
        try {
            String token = phoneVerificationService.verifyCode(
                    requestDTO.getPhone(), requestDTO.getCode(), session
            );
            return PhoneVerificationResponseDTO.builder()
                    .success(true)
                    .verificationToken(token)
                    .build();
        } catch (IllegalArgumentException e) {
            return PhoneVerificationResponseDTO.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        }
    }

}