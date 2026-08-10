package com.example.chook.chatbot.controller;

import com.example.chook.chatbot.dto.ChatRequestDTO;
import com.example.chook.chatbot.dto.ChatResponseDTO;
import com.example.chook.chatbot.service.ChatService;
import com.example.chook.member.entity.Member;
import com.example.chook.member.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chatbot")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;
    private final MemberRepository memberRepository;

    @PostMapping("/ask")
    public ChatResponseDTO ask(
            @RequestBody ChatRequestDTO chatRequestDTO,
            // 현재 로그인한 사용자 정보
            Authentication authentication,
            // 현재 접속자의 세션 정보
            HttpSession session
    ){
        // 로그인 회원 확인
        Long memberId = null;
        if(authentication != null){
            Member member =
                    memberRepository.findByUsernameAndDeletedAtIsNull(
                            authentication.getName()
                    ).orElseThrow();
            memberId = member.getId();
        }

        // 비로그인 회원 확인
        String sessionId =
                session.getId();

        return chatService.ask(
                chatRequestDTO,
                memberId,
                sessionId
        );
    }
}
