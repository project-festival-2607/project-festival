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

    // 로그인 실행
    @PostMapping("/login")
    public String login(
            @ModelAttribute LoginRequestDTO requestDTO,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        try {
            LoginResponseDTO responseDTO = memberService.login(requestDTO);
            session.setAttribute("loginMember", responseDTO);
            return "redirect:/";
        } catch (MemberDormantException e) {
            return "redirect:/member/verify";
        } catch (MemberSuspendedException e) {
            return "redirect:/member/suspended";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("FailureMsg", e.getMessage());
            return "redirect:/member/login";
        }
    }

    // 회원가입
    @GetMapping("/signup")
    public String signupSelect() {
        return "member/signup";
    }

    // 구직자 회원가입
    @GetMapping("/signup/job-seeker")
    public String signupJobSeekerForm() {
        return "member/signup-job";
    }

    // 구직자 회원가입 처리
    @PostMapping("/signup/job-seeker")
    public String signupJobSeeker(
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
    public String signupEmployerForm() {
        return "member/signup-recruit";
    }

    // 구인자 회원가입 처리
    @PostMapping("/signup/employer")
    public String signupEmployer(
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

}