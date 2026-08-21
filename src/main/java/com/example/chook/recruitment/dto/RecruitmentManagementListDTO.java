package com.example.chook.recruitment.dto;

import com.example.chook.recruitment.entity.enums.RecruitmentCategory;
import com.example.chook.recruitment.entity.enums.RecruitmentStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecruitmentManagementListDTO {

  private Long recruitmentId;

  private String regionSidoName;
  private String regionSigunguName;

  private String festivalContentId;
  private String festivalTitle;

  private RecruitmentCategory category;
  private RecruitmentSpecificDTO specific;

  private String recruitmentTitle;

  private LocalDate applicationDeadline;

  private LocalDate workingStartDate;
  private LocalDate workingEndDate;
  private LocalTime workingStartTime;
  private LocalTime workingEndTime;

  private RecruitmentStatus status;
  private LocalDateTime publishedAt;
  private LocalDateTime updatedAt;
  private LocalDateTime deletedAt;

}