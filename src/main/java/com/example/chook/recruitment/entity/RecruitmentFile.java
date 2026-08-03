package com.example.chook.recruitment.entity;

import com.example.chook.file.entity.UploadedFile;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "recruitment_file")
public class RecruitmentFile {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "recruit_file_id")
  private Long recruitFileId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
    name = "recruit_id",
    nullable = false,
    foreignKey = @ForeignKey(name = "fk_recruitment_file_recruit_id")
  )
  @ToString.Exclude
  private Recruitment recruitment;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(
    name = "file_uuid",
    nullable = false,
    unique = true,
    foreignKey = @ForeignKey(name = "fk_recruitment_file_file_uuid")
  )
  @ToString.Exclude
  private UploadedFile uploadedFile;
}
