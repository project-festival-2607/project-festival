package com.example.chook.chatbot.service;

import com.example.chook.chatbot.dto.ChatHistoryDTO;
import com.example.chook.chatbot.entity.ChatHistory;
import com.example.chook.chatbot.repository.ChatHistoryRepository;
import com.example.chook.member.entity.Member;
import com.example.chook.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class ChatHistoryServiceImpl implements ChatHistoryService {

    private final ChatHistoryRepository chatHistoryRepository;
    private final MemberRepository memberRepository;

    // 로그인 회원 데이터 저장
    @Override
    public void saveMemberChat(Long memberId, String question, String answer) {

        Member member =
                memberRepository.findById(memberId)
                        .orElseThrow();

        ChatHistory chatHistory =
                ChatHistory.builder()
                        .member(member)
                        .question(question)
                        .answer(answer)
                        .regDate(LocalDateTime.now())
                        .build();
        chatHistoryRepository.save(chatHistory);
    }

    // 비로그인 데이터 저장
    @Override
    public void saveGuestChat(String sessionId, String question, String answer) {

        ChatHistory chatHistory =
                ChatHistory.builder()
                        .sessionId(sessionId)
                        .question(question)
                        .answer(answer)
                        .regDate(LocalDateTime.now())
                        .build();
        chatHistoryRepository.save(chatHistory);
    }

    // 로그인 사용자 조회
    @Override
    public List<ChatHistoryDTO> getMemberHistory(Long memberId) {
        List<ChatHistory> historyList =
                chatHistoryRepository
                        .findTop5ByMember_IdOrderByRegDateDesc(memberId);

        return historyList.stream()
                .map(this::chatHistoryEntityToDto)
                .toList();
    }

    // 비로그인 사용자 조회
    @Override
    public List<ChatHistoryDTO> getGuestHistory(String sessionId) {
        List<ChatHistory> historyList =
                chatHistoryRepository
                        .findTop5BySessionIdOrderByRegDateDesc(sessionId);

        return historyList.stream()
                .map(this::chatHistoryEntityToDto)
                .toList();
    }

    @Override
    public List<ChatHistory> getLastChat(String sessionId) {
        return chatHistoryRepository
                .findTop5BySessionIdOrderByRegDateDesc(sessionId);
    }
}
