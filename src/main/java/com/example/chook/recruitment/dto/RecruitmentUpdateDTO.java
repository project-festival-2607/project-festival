package com.example.chook.recruitment.dto;

import com.example.chook.recruitment.entity.enums.RecruitmentWageType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecruitmentUpdateDTO {

  private String regionSidoCode;
  private String regionSigunguCode;

  private String recruitmentTitle;

  private String content;

  private RecruitmentSpecificDTO specific;

  private LocalDate applicationDeadline;
  private Integer recruitmentCount;

  private String workingLocation;
  private LocalDate workingStartDate;
  private LocalDate workingEndDate;
  private LocalTime workingStartTime;
  private LocalTime workingEndTime;

}
