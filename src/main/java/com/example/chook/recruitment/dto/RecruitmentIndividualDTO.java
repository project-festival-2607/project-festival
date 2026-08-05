package com.example.chook.recruitment.dto;

import com.example.chook.recruitment.entity.enums.RecruitmentWageType;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecruitmentIndividualDTO implements RecruitmentSpecificDTO {

  // RecruitmentIndividual
  private RecruitmentWageType wageType;
  private Integer wageValue;

}
