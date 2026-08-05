package com.example.chook.application.entity;

import com.example.chook.application.entity.enums.ApplicationResult;
import com.example.chook.member.entity.Member;
import com.example.chook.recruitment.entity.Recruitment;
import com.example.chook.resume.entity.Resume;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_application_member_recruit",
                        columnNames = {"member_id", "recruit_id"}
                )
        }
)
public class Application {

    // 지원 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apply_id")
    private Long id;

    // 회원 식별자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "member_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_application_member_id")
    )
    @ToString.Exclude
    private Member member;

    // 모집공고 아이디
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "recruit_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_application_recruit_id")
    )
    @ToString.Exclude
    private Recruitment recruitment;

    // 이력서 아이디
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "resume_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_application_resume_id")
    )
    @ToString.Exclude
    private Resume resume;

    // 지원일시
    @Column(name = "register_date", nullable = false)
    private LocalDateTime registerDate;

    // 열람일시
    @Column(name = "read_date")
    private LocalDateTime readDate;

    // 결과 (enum으로)
    @Enumerated(EnumType.STRING)
    @Column(name = "result", nullable = false)
    private ApplicationResult result;
}

