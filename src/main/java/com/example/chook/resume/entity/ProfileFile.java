package com.example.chook.resume.entity;

import com.example.chook.file.entity.UploadedFile;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "profile_file")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileFile {

    // 식별자
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_file_id")
    private Long profileFileId;

    // 이력서 아이디
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "resume_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_profile_file_resume_id")
    )
    @ToString.Exclude
    private Resume resume;

    // 파일 아이디
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "file_uuid",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_profile_file_file_uuid")
    )
    @ToString.Exclude
    private UploadedFile uploadedFile;
}
