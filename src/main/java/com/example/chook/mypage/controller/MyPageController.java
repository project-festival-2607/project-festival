package com.example.chook.mypage.controller;

import com.example.chook.member.entity.enums.MemberRole;
import com.example.chook.mypage.DTO.MyPageDTO;
import com.example.chook.mypage.service.MyPageService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
@Slf4j
public class MyPageController {

    private final MyPageService myPageService;

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

        // 구직자만 구직자 마이페이지 접근 가능
        if (myPageDTO.getRole() != MemberRole.JOB_SEEKER && myPageDTO.getRole() != MemberRole.JOB_EQUIP) {
            return "redirect:/";
        }

        // HTML에 전달
        model.addAttribute("myPageDTO", myPageDTO);

        return "mypage/jobseeker/mypage";
    }

    // 수정 페이지
    @GetMapping("/modify")
    public String modify(@AuthenticationPrincipal UserDetails user, Model model){

        if (user == null) {
            return "redirect:/member/login";
        }

        String username = user.getUsername();

        MyPageDTO myPageDTO =
                myPageService.getMyPage(username);

        model.addAttribute("myPageDTO", myPageDTO);

        return "mypage/modify";
    }

    // 개인정보 수정
    @PostMapping("/modify")
    public String modify(@AuthenticationPrincipal UserDetails user,MyPageDTO myPageDTO){

        String username = user.getUsername();

        log.info("수정 username = {}", username);
        log.info("수정 DTO = {}", myPageDTO);

        myPageService.modify(username, myPageDTO);

        return "redirect:/mypage";
    }
}