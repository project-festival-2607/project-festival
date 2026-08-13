package com.example.chook.mypage.controller;

import com.example.chook.member.entity.enums.MemberRole;
import com.example.chook.member.dto.EmployerProfileUpdateRequestDTO;
import com.example.chook.member.dto.JobSeekerProfileUpdateRequestDTO;
import com.example.chook.member.dto.LoginResponseDTO;
import com.example.chook.member.security.CustomUserDetails;
import com.example.chook.member.service.MemberService;
import com.example.chook.mypage.dto.MyPageDTO;
import com.example.chook.mypage.service.MyPageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
@Slf4j
public class MyPageController {

    private final MyPageService myPageService;
    private final MemberService memberService;
    private static final String PASSWORD_VERIFIED_SESSION_KEY = "passwordVerified";

    // 조회기능
    @GetMapping
    public String mypage(@AuthenticationPrincipal UserDetails user, Model model){

        // 로그인하지 않은 경우
        if (user == null) {
            return "redirect:/member/login";
        }

        // 현재 로그인한 회원의 아이디
        String username = user.getUsername();

        // DB에서 회원 정보 조회
        MyPageDTO myPageDTO = myPageService.getMyPage(username);

        switch (myPageDTO.getRole()){
            // 구직자
            case JOB_SEEKER:
            case JOB_EQUIP:
                return "redirect:/mypage/jobseeker";

            // 구인자
            case RECRUITER:
                return "redirect:/mypage/recruiter";

            // 그 외
            default:
                return "redirect:/";
        }

    }

    // 구직자 마이페이지
    @GetMapping("/jobseeker")
    public String jobseekerMypage(@AuthenticationPrincipal UserDetails user, Model model) {

        // 로그인하지 않은 경우
        if (user == null) {
            return "redirect:/member/login";
        }

        // 현재 로그인한 회원의 아이디
        String username = user.getUsername();

        // DB에서 회원 정보 조회
        MyPageDTO myPageDTO = myPageService.getMyPage(username);

        // 구직자만 접근 가능
        if (myPageDTO.getRole() != MemberRole.JOB_SEEKER && myPageDTO.getRole() != MemberRole.JOB_EQUIP) {
            return "redirect:/";
        }

        // HTML에 회원 정보 전달
        model.addAttribute("myPageDTO", myPageDTO);

        // 구직자 마이페이지
        return "mypage/jobseeker/mypage";
    }

    // 구인자 마이페이지
    @GetMapping("/recruiter")
    public String recruiterMypage(@AuthenticationPrincipal UserDetails user, Model model) {

        // 로그인하지 않은 경우
        if (user == null) {
            return "redirect:/member/login";
        }

        // 현재 로그인한 회원의 아이디
        String username = user.getUsername();

        // DB에서 회원 정보 조회
        MyPageDTO myPageDTO = myPageService.getMyPage(username);

        // 구인자만 접근 가능
        if (myPageDTO.getRole() != MemberRole.RECRUITER) {
            return "redirect:/";
        }

        // HTML에 회원 정보 전달
        model.addAttribute("myPageDTO", myPageDTO);

        // 구직자 마이페이지
        return "mypage/recruiter/mypage";
    }

    // 비밀번호 확인 페이지
    @GetMapping("/password-check")
    public String passwordCheckForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/member/login";
        }
        if (userDetails.isSocialSignUp()) {
            return "redirect:/mypage/modify/job-seeker";
        }
        model.addAttribute("username", userDetails.getUsername());
        return "mypage/password-check";
    }

    // 비밀번호 확인 처리
    @PostMapping("/password-check")
    public String passwordCheck(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        if (!memberService.verifyPassword(userDetails.getId(), password)) {
            redirectAttributes.addFlashAttribute("FailureMsg", "비밀번호가 일치하지 않습니다.");
            return "redirect:/mypage/password-check";
        }
        session.setAttribute(PASSWORD_VERIFIED_SESSION_KEY, true);
        return userDetails.isRecruiter()
                ? "redirect:/mypage/modify/employer"
                : "redirect:/mypage/modify/job-seeker";
    }

    // 회원정보 수정 페이지
    @GetMapping("/modify/job-seeker")
    public String modifyJobSeekerForm(@AuthenticationPrincipal CustomUserDetails userDetails, HttpSession session, Model model) {
        if (userDetails == null) {
            return "redirect:/member/login";
        }
        if (requiresPasswordCheck(userDetails, session)) {
            return "redirect:/mypage/password-check";
        }
        model.addAttribute("myPageDTO", myPageService.getMyPage(userDetails.getUsername()));
        return "mypage/modify-job";
    }

    // 회원정보 수정 처리
    @PostMapping("/modify/job-seeker")
    public String modifyJobSeeker(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @ModelAttribute JobSeekerProfileUpdateRequestDTO requestDTO,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        if (requiresPasswordCheck(userDetails, session)) {
            return "redirect:/mypage/password-check";
        }
        try {
            LoginResponseDTO responseDTO = memberService.updateJobSeekerProfile(userDetails.getId(), requestDTO);
            session.setAttribute("loginMember", responseDTO);
            return "redirect:/mypage";
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("FailureMsg", e.getMessage());
            return "redirect:/mypage/modify/job-seeker";
        }
    }

    // 회원정보 수정 페이지 (구인자)
    @GetMapping("/modify/employer")
    public String modifyEmployerForm(@AuthenticationPrincipal CustomUserDetails userDetails, HttpSession session, Model model) {
        if (userDetails == null) {
            return "redirect:/member/login";
        }
        if (requiresPasswordCheck(userDetails, session)) {
            return "redirect:/mypage/password-check";
        }
        model.addAttribute("myPageDTO", myPageService.getMyPage(userDetails.getUsername()));
        return "mypage/modify-recruit";
    }

    // 회원정보 수정 처리 (구인자)
    @PostMapping("/modify/employer")
    public String modifyEmployer(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @ModelAttribute EmployerProfileUpdateRequestDTO requestDTO,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        if (requiresPasswordCheck(userDetails, session)) {
            return "redirect:/mypage/password-check";
        }
        try {
            LoginResponseDTO responseDTO = memberService.updateEmployerProfile(userDetails.getId(), requestDTO);
            session.setAttribute("loginMember", responseDTO);
            return "redirect:/mypage";
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("FailureMsg", e.getMessage());
            return "redirect:/mypage/modify/employer";
        }
    }

    @PostMapping("/business-number/delete")
    public String deleteBusinessNumber(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        if (requiresPasswordCheck(userDetails, session)) {
            return "redirect:/mypage/password-check";
        }

        try {
            LoginResponseDTO responseDTO = memberService.removeBusinessNumber(userDetails.getId());
            session.setAttribute("loginMember", responseDTO);
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("FailureMsg", e.getMessage());
        }

        return "redirect:/mypage/modify/job-seeker";
    }

    // 비밀번호 확인이 필요한 상태인지 (소셜 회원이면 불필요, 아니면 세션 플래그로 판단)
    private boolean requiresPasswordCheck(CustomUserDetails userDetails, HttpSession session) {
        if (userDetails.isSocialSignUp()) {
            return false;
        }
        return !Boolean.TRUE.equals(session.getAttribute(PASSWORD_VERIFIED_SESSION_KEY));
    }
}