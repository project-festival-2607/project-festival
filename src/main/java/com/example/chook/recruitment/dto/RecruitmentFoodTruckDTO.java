package com.example.chook.recruitment.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecruitmentFoodTruckDTO implements RecruitmentSpecificDTO {

  // RecruitmentFoodTruck
  private boolean prepaid;
  private boolean boothFeeRequired;
  private boolean electricityProvided;

}
