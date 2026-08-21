package com.example.chook.chatbot.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatResponseDTO {
    private List<Choice> choices; // 여러 개의 답변 후보 목록

    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Choice {

        private MessageDTO message; // AI가 새로 생성한 메시지 1개

    }
}
