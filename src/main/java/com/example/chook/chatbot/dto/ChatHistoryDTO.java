package com.example.chook.chatbot.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatHistoryDTO {

    // 대화번호
    private Long chatId;

    // 회원 식별자
    private Long memberId;

    // 비회원 식별자
    private String sessionId;

    // 사용자 질문
    private String question;

    // GTP 답변
    private String answer;

    // 질문시간
    private LocalDateTime regDate;
}
