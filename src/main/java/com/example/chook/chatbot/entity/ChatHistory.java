package com.example.chook.chatbot.entity;

import com.example.chook.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_history")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Long chatId; // 대화번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
      name = "member_id",
      foreignKey = @ForeignKey(name = "fk_chat_history_member_id")
    )
    @ToString.Exclude
    private Member member;

    @Column(name = "session_id")
    private String sessionId; // 비회원 식별자

    private String question; // 시용자 질문

    @Column(columnDefinition = "TEXT")
    private String answer; // GTP답변

    @Column(name = "reg_date")
    private LocalDateTime regDate; // 질문시간
}
