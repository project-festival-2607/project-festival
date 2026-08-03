package com.example.chook.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_history")
@Getter
@Setter
public class ChatHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Long chatId; // 대화번호

    private String id; // 회원 식별자

    private String sessionId; // 비회원 식별자

    private String question; // 시용자 질문

    private String answer; // GTP답변

    @Column(name = "reg_date")
    private LocalDateTime regDate; // 질문시간
}
