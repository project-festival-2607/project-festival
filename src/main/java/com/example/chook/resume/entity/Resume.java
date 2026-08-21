package com.example.chook.resume.entity;

import com.example.chook.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "resume")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resume {

    // 이력서 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resume_id")
    private Long id;

    // 회원 식별자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "member_id",
            foreignKey = @ForeignKey(name = "fk_resume_member_id")
    )
    @ToString.Exclude
    private Member member;

    // 자기소개
    @Column(length = 150)
    private String introduction;

    // 최종수정일
    @Column(name = "saved_at")
    private LocalDateTime savedAt;
}
