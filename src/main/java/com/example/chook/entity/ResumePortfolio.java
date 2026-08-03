package com.example.chook.entity;

import com.example.chook.entity.enums.ResumePortfolioType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "resume_portfolio")
@Getter
@Setter
@Builder
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
            foreignKey = @ForeignKey(name = "fk_resume_portfolio _resume_id")
    )
    @ToString.Exclude
    private Resume resume;

    // 타입
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ResumePortfolioType type;

    // 제목
    private String title;

    // 등록일
    @Column(name = "registered_at")
    private LocalDateTime registeredAt;
}
