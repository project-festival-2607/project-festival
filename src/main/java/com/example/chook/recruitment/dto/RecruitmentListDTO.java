package com.example.chook.recruitment.dto;

import com.example.chook.recruitment.entity.enums.RecruitmentCategory;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecruitmentListDTO {

  private String regionSidoName;
  private String regionSigunguName;

  private Long recruitmentId;
  private String recruitmentTitle;
  private String festivalContentId;
  private String festivalTitle;

  private RecruitmentCategory category;
  private RecruitmentSpecificDTO specific;

  private LocalDate workingStartDate;
  private LocalDate workingEndDate;
  private LocalTime workingStartTime;
  private LocalTime workingEndTime;

}
