package com.example.chook.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "resume_portfolio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResumePortfolio {

    // 포트폴리오 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "portfolio_id")
    private Long portfolioId;

    // 이력서 아이디
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "resume_id",
            foreignKey = @ForeignKey(name = "fk_resume_portfollo_resume_id")
    )
    private Resume resume;

    // 타입
    @Column(name = "type")
    private String type;

    // 제목
    @Column(name = "title")
    private String title;

    // 등록일
    @Column(name = "registered_at")
    private LocalDateTime registeredAt;
}
