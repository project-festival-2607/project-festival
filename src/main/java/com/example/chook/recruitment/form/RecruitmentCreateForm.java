package com.example.chook.recruitment.form;

import com.example.chook.recruitment.entity.enums.RecruitmentCategory;
import com.example.chook.recruitment.entity.enums.RecruitmentWageType;
import jakarta.validation.constraints.*;
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

  @NotNull
  @Pattern(regexp = "^[0-9]{2}$")
  private String regionSidoCode;
  @NotNull
  @Pattern(regexp = "^[0-9]{5}$")
  private String regionSigunguCode;

  @Size(max = 100)
  @NotBlank
  private String recruitmentTitle;

  @NotNull
  private String festivalContentId;

  private String content;

  @NotNull
  private RecruitmentCategory category;

  // RecruitmentIndividual
  private RecruitmentWageType wageType;
  @PositiveOrZero
  private Integer wageValue;

  // RecruitmentFoodTruck
  private boolean prepaid;
  private boolean boothFeeRequired;
  private boolean electricityProvided;

  @FutureOrPresent
  @NotNull
  private LocalDate applicationDeadline;
  @PositiveOrZero
  private Integer recruitmentCount;

  @Size(max = 255)
  private String workingLocation;
  @NotNull
  @FutureOrPresent
  private LocalDate workingStartDate;
  @NotNull
  @FutureOrPresent
  private LocalDate workingEndDate;
  private LocalTime workingStartTime;
  private LocalTime workingEndTime;

}