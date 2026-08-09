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

}
