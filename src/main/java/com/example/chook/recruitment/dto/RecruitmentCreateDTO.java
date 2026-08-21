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
public class RecruitmentCreateDTO {

  private String regionSidoCode;
  private String regionSigunguCode;
  private String workingLocation;

  private String festivalContentId;

  private RecruitmentCategory category;
  private RecruitmentSpecificDTO specific;

  private String recruitmentTitle;
  private String content;

  private LocalDate applicationDeadline;
  private Integer recruitmentCount;

  private LocalDate workingStartDate;
  private LocalDate workingEndDate;
  private LocalTime workingStartTime;
  private LocalTime workingEndTime;

}
