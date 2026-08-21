package com.example.chook.chatbot.service;

import com.example.chook.chatbot.dto.ChatRequestDTO;
import com.example.chook.chatbot.dto.ChatResponseDTO;

public interface ChatService {

    ChatResponseDTO ask (ChatRequestDTO chatRequestDTO, Long memberId, String sessionId);
}
