package com.example.chook.resume.entity;

import com.example.chook.member.entity.Member;
import com.example.chook.recruitment.entity.Recruitment;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "application")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {

    // 지원 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apply_id")
    private Long applyId;

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

    // 결과
    @Column(length = 10 , nullable = false)
    private String result;
}

