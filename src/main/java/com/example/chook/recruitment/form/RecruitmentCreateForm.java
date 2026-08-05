package com.example.chook.recruitment.form;

import com.example.chook.recruitment.entity.enums.RecruitmentCategory;
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
public class RecruitmentCreateForm {

  private String regionSidoCode;
  private String regionSigunguCode;

  private String recruitmentTitle;
  private String festivalContentId;

  private String content;

  private RecruitmentCategory category;

  // RecruitmentIndividual
  private RecruitmentWageType wageType;
  private Integer wageValue;

  // RecruitmentFoodTruck
  private Boolean prepaid;
  private Boolean boothFeeRequired;
  private Boolean electricityProvided;

  private LocalDate applicationDeadline;
  private Integer recruitmentCount;

  private String workingLocation;
  private LocalDate workingStartDate;
  private LocalDate workingEndDate;
  private LocalTime workingStartTime;
  private LocalTime workingEndTime;

}