package com.example.chook.chatbot.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageDTO {
    private String role; // 메시지 작성자의 역할 ("system", "user", "assistant")
    // "system": 챗봇의 성격, 페르소나, 규칙을 지정할 때 사용 (AI 지침)
    // "user": 사용자(질문자)가 입력한 질문 내용을 전달할 때 사용 (사용자 질문)
    // "assistant": 이전 대화 맥락을 유지하기 위해 DB에서 꺼낸 AI의 과거 답변을 전달할 때 사용 (LLM/AI 답변)
    private String content; // 실제 메시지 내용
}
