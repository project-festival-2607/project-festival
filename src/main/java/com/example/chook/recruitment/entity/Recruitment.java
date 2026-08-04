package com.example.chook.recruitment.entity;

import com.example.chook.festival.Festival;
import com.example.chook.recruitment.entity.enums.RecruitmentCategory;
import com.example.chook.recruitment.entity.enums.RecruitmentStatus;
import com.example.chook.region.entity.RegionSigungu;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recruitment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "recruit_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
    name = "sigungu_code",
    nullable = false,
    foreignKey = @ForeignKey(name = "fk_recruitment_sigungu_code")
  )
  private RegionSigungu sigungu;

  @Column(name = "working_location")
  private String workingLocation;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
    name = "festival_id",
    nullable = false,
    foreignKey = @ForeignKey(name = "fk_recruitment_festival_id")
  )
  @ToString.Exclude
  private Festival festival;

  @Enumerated(EnumType.STRING)
  @Column(name = "category", nullable = false, length = 10)
  @Builder.Default
  private RecruitmentCategory category = RecruitmentCategory.INDIVIDUAL;

  @Column(nullable = false, length = 100)
  private String title;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String content;

  @Column(nullable = false)
  private LocalDate applicationDeadline;

  @Column(name = "recruitment_count", nullable = false)
  private int recruitmentCount;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 10)
  @Builder.Default
  private RecruitmentStatus status = RecruitmentStatus.OPEN;

  @Column(name = "working_start_time", nullable = false)
  private LocalTime workingStartTime;

  @Column(name = "working_end_time", nullable = false)
  private LocalTime workingEndTime;

  @Column(name = "working_start_date", nullable = false)
  private LocalDate workingStartDate;

  @Column(name = "working_end_date", nullable = false)
  private LocalDate workingEndDate;

  @Builder.Default
  private boolean published = false;

  @Column(name = "published_at")
  private LocalDateTime publishedAt;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

}
