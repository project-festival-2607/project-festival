package com.example.chook.member.controller;

import com.example.chook.member.dto.*;
import com.example.chook.member.exception.MemberDormantException;
import com.example.chook.member.exception.MemberSuspendedException;
import com.example.chook.member.service.BusinessNumberVerifyService;
import com.example.chook.member.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        try {
            LoginResponseDTO responseDTO = memberService.signUpJobSeeker(requestDTO);
            session.setAttribute("loginMember", responseDTO);
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
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        try {
            LoginResponseDTO responseDTO = memberService.signUpEmployer(requestDTO);
            session.setAttribute("loginMember", responseDTO);
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
            RedirectAttributes redirectAttributes
    ) {
        SocialAuthSessionDTO authInfo = (SocialAuthSessionDTO) session.getAttribute("socialAuthInfo");
        if (authInfo == null) {
            return "redirect:/member/login";
        }

        try {
            LoginResponseDTO responseDTO = memberService.signUpSocial(authInfo, requestDTO);
            session.removeAttribute("socialAuthInfo");
            session.setAttribute("loginMember", responseDTO);
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("FailureMsg", e.getMessage());
            return "redirect:/member/signup/social";
        }
    }

}