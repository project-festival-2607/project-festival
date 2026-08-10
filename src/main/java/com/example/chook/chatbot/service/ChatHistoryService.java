package com.example.chook.chatbot.service;

import com.example.chook.chatbot.dto.ChatHistoryDTO;
import com.example.chook.chatbot.entity.ChatHistory;

import java.util.List;

public interface ChatHistoryService {

    // chatHistory(Entity) -> chatHistoryDTO
    default ChatHistoryDTO chatHistoryEntityToDto(
            ChatHistory chatHistory
    ){
        return ChatHistoryDTO.builder()
                .chatId(chatHistory.getChatId())
                .memberId(
                        chatHistory.getMember() != null ? chatHistory.getMember().getId() : null
                )
                .sessionId(chatHistory.getSessionId())
                .question(chatHistory.getQuestion())
                .answer(chatHistory.getAnswer())
                .regDate(chatHistory.getRegDate())
                .build();
    }

    // chatHistoryDTO -> chatHistory(Entity)
    default ChatHistory chatHistoryDtoToEntity(
            ChatHistoryDTO chatHistoryDTO
    ){
        return ChatHistory.builder()
                .sessionId(chatHistoryDTO.getSessionId())
                .question(chatHistoryDTO.getQuestion())
                .answer(chatHistoryDTO.getAnswer())
                .regDate(chatHistoryDTO.getRegDate())
                .build();

    }

    // 로그인 유저 데이터 저장
    void saveMemberChat(Long memberId, String question, String answer);

    // 비로그린 유저 데이터 저장
    void saveGuestChat(String sessionId, String question, String answer);

    // 로그인 사용자 조회
    List<ChatHistoryDTO> getMemberHistory(Long memberId);

    // 비로그인 사용자 조회
    List<ChatHistoryDTO> getGuestHistory(String sessionId);

    // 이전 대화 조회
    List<ChatHistory> getLastChat(String sessionId);
}
