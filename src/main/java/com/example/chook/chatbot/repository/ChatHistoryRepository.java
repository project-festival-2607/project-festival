package com.example.chook.chatbot.repository;

import com.example.chook.chatbot.entity.ChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {

    // 로그인 회원의 최근 채팅 기록 5개 조회
    List<ChatHistory> findTop5ByMember_IdOrderByRegDateDesc(Long memberId);

    // 비로그인 회윈의 최근 채팅 기록 5개 조회
    List<ChatHistory> findTop5BySessionIdOrderByRegDateDesc(String sessionId);
}
