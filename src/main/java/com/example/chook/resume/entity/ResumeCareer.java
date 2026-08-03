package com.example.chook.resume.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "resume_career")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeCareer {

    // 경력사항 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resume_career_id")
    private Long id;

    // 이력서 아이디
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "resume_id",
            foreignKey = @ForeignKey(name = "fk_resume_career_resume_id")
    )
    @ToString.Exclude
    private Resume resume;

    // 회사명 / 행사명
    @Column(name = "career_name", length = 255)
    private String careerName;

    // 입사연월
    @Column(name = "start_date")
    private LocalDateTime startDate;

    // 퇴사연월
    @Column(name = "end_date")
    private LocalDateTime endDate;

    // 담당업무
    @Column(length = 5000)
    private String duties;
}

