package com.example.chook.chatbot.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRequestDTO {
    private String model; // 사용할 AI 모델의 이름을 담는 변수
    private List<MessageDTO> messages;
    // 대화 내용을 담은 MessageDTO 객체들의 리스트(목록)
}
