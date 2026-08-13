package com.example.chook.mypage.controller;

import com.example.chook.member.dto.EmployerProfileUpdateRequestDTO;
import com.example.chook.member.dto.JobSeekerProfileUpdateRequestDTO;
import com.example.chook.member.dto.LoginResponseDTO;
import com.example.chook.member.entity.enums.Provider;
import com.example.chook.member.security.AuthenticationHelper;
import com.example.chook.member.security.CustomUserDetails;
import com.example.chook.member.service.MemberService;
import com.example.chook.mypage.dto.MyPageDTO;
import com.example.chook.mypage.service.MyPageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
@Slf4j
public class MyPageController {

    private final MyPageService myPageService;
    private final MemberService memberService;
    private final AuthenticationHelper authenticationHelper;
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
        MyPageDTO myPageDTO =
                myPageService.getMyPage(username);

        // HTML에 전달
        model.addAttribute("myPageDTO", myPageDTO);

        return "mypage/mypage";
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
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes
    ) {
        if (requiresPasswordCheck(userDetails, session)) {
            return "redirect:/mypage/password-check";
        }
        try {
            LoginResponseDTO responseDTO = memberService.updateJobSeekerProfile(userDetails.getId(), requestDTO);
            authenticationHelper.authenticate(responseDTO.getUsername(), request, response);
            return "redirect:/mypage";
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("FailureMsg", e.getMessage());
            return "redirect:/mypage/modify/job-seeker";
        }
    }

    // 회원정보 수정 페이지 (구인자)
    @GetMapping("/modify/employer")
    public String modifyEmployerForm(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpSession session, Model model) {
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
            memberService.updateEmployerProfile(userDetails.getId(), requestDTO);
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
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes
    ) {
        if (requiresPasswordCheck(userDetails, session)) {
            return "redirect:/mypage/password-check";
        }

        try {
            LoginResponseDTO responseDTO = memberService.removeBusinessNumber(userDetails.getId());
            authenticationHelper.authenticate(responseDTO.getUsername(), request, response);
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("FailureMsg", e.getMessage());
        }

        return "redirect:/mypage/modify/job-seeker";
    }

    // 비밀번호 수정
    @GetMapping("/password-change")
    public String passwordChangeForm(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/member/login";
        }
        if (userDetails.isSocialSignUp()) {
            return "redirect:/mypage";
        }
        return "mypage/password-change";
    }

    @PostMapping("/password-change")
    public String passwordChange(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String newPasswordConfirm,
            RedirectAttributes redirectAttributes) {
        try {
            memberService.changePassword(userDetails.getId(), currentPassword, newPassword, newPasswordConfirm);
            redirectAttributes.addFlashAttribute("SuccessMsg", "비밀번호가 변경되었습니다.");
            return "redirect:/mypage";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("FailureMsg", e.getMessage());
            return "redirect:/mypage/password-change";
        }
    }

    // 비밀번호 확인이 필요한 상태인지 (소셜 회원이면 불필요, 아니면 세션 플래그로 판단)
    private boolean requiresPasswordCheck(CustomUserDetails userDetails, HttpSession session) {
        if (userDetails.isSocialSignUp()) {
            return false;
        }
        return !Boolean.TRUE.equals(session.getAttribute(PASSWORD_VERIFIED_SESSION_KEY));
    }

    // 회원탈퇴 페이지
    @GetMapping("/account-leave")
    public String accountLeaveForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/member/login";
        }
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("socialSignUp", userDetails.isSocialSignUp());
        return "mypage/account-leave";
    }

    // 탈퇴 처리
    @PostMapping("/withdraw")
    @ResponseBody
    public ResponseEntity<Void> withdraw(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String confirmValue,
            HttpSession session
    ) {
        try {
            memberService.withdraw(userDetails.getId(), confirmValue);
            session.invalidate();
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 간편 로그인 관리 페이지
    @GetMapping("/social-login")
    public String socialLoginForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/member/login";
        }
        model.addAttribute("linkedProviders", memberService.getLinkedProviders(userDetails.getId()));
        return "mypage/social-login";
    }

    // 연동 시작 (OAuth2 플로우로 진입)
    @GetMapping("/social-login/{provider}/link")
    public String linkStart(
            @PathVariable String provider,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpSession session
    ) {
        if (userDetails == null) {
            return "redirect:/member/login";
        }
        if (userDetails.isRecruiter()) {
            return "redirect:/mypage/social-login";
        }
        session.setAttribute("linkingMemberId", userDetails.getId());
        return "redirect:/oauth2/authorization/" + provider;
    }

    // 연동 해제
    @PostMapping("/social-login/{provider}/unlink")
    @ResponseBody
    public ResponseEntity<Void> unlink(
            @PathVariable String provider,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        memberService.unlinkSocialAccount(userDetails.getId(), Provider.valueOf(provider.toUpperCase()));
        return ResponseEntity.ok().build();
    }
}