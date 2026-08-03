package com.example.chook.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "resume")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 이력서 아이디
    private Long resumeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "member_id",
            foreignKey = @ForeignKey(name = "fk_resume_member_id")
    )
    // 회원 식별자
    private Resume resume;

    // 자기소개
    private String introduction;

    // 최종수정일
    private LocalDateTime savedAt;
}
