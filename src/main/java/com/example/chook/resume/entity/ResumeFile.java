package com.example.chook.resume.entity;

import com.example.chook.file.entity.UploadedFile;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "resume_file")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeFile {

    // 식별자
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resume_file_id")
    private Long resumeFileId;

    // 포트폴리오 아이디
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "portfolio_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_resume_file_portfolio_id")
    )
    @ToString.Exclude
    private ResumePortfolio resumePortfolio;

    // 파일 아이디
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "file_uuid",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_resume_file_file_uuid")
    )
    @ToString.Exclude
    private UploadedFile uploadedFile;
}