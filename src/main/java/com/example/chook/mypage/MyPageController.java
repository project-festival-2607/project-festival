package com.example.chook.mypage;

import com.example.chook.member.dto.LoginResponseDTO;
import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
@Slf4j
public class MyPageController {

    private final MyPageService myPageService;

    // 조회기능
    @GetMapping
    public String mypage(HttpSession session, Model model){

        // 세션에서 로그인한 회원 정보 가져오기
        LoginResponseDTO loginMember =
                (LoginResponseDTO) session.getAttribute("loginMember");

        // 로그인하지 않은 경우
        if (loginMember == null) {
            return "redirect:/member/login";
        }

        // 로그인한 회원의 아이디
        String username = loginMember.getUsername();

        // DB에서 회원 정보 조회
        MyPageDTO myPageDTO =
                myPageService.getMyPage(username);

        // HTML로 전달
        model.addAttribute("myPageDTO", myPageDTO);

        return "mypage/mypage";
    }

}
