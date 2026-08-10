package com.example.chook.mypage;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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
        MyPageDTO myPageDTO =
                myPageService.getMyPage(username);

        // HTML에 전달
        model.addAttribute("myPageDTO", myPageDTO);

        return "mypage/mypage";
    }
}