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

  private String regionSidoName;
  private String regionSigunguName;

  private Long recruitmentId;
  private String recruitmentTitle;
  private String festivalContentId;
  private String festivalTitle;

  private RecruitmentCategory category;
  private RecruitmentSpecificDTO specific;

  private LocalDate applicationDeadline;
  private RecruitmentStatus status;

  private String workingLocation;
  private LocalDate workingStartDate;
  private LocalDate workingEndDate;
  private LocalTime workingStartTime;
  private LocalTime workingEndTime;

  private LocalDateTime publishedAt;

}