package com.example.chook.recruitment.dto;

import com.example.chook.recruitment.entity.enums.RecruitmentCategory;
import com.example.chook.recruitment.entity.enums.RecruitmentStatus;
import com.example.chook.recruitment.entity.enums.RecruitmentWageType;
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
  // RecruitmentIndividual
  private RecruitmentWageType wageType;
  private Integer wageValue;
  // RecruitmentFoodTruck - 공고별 페이지에서 확인

  private LocalDate applicationDeadline;
  private RecruitmentStatus status;

  private String workingLocation;
  private LocalDate workingStartDate;
  private LocalDate workingEndDate;
  private LocalTime workingStartTime;
  private LocalTime workingEndTime;

  private LocalDateTime publishedAt;

}