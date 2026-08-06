package com.example.chook.recruitment.form;

import com.example.chook.recruitment.entity.enums.RecruitmentCategory;
import com.example.chook.recruitment.entity.enums.RecruitmentWageType;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record RecruitmentCreateForm(

  @NotNull
  @Pattern(regexp = "^[0-9]{2}$")
  String regionSidoCode,
  @NotNull
  @Pattern(regexp = "^[0-9]{5}$")
  String regionSigunguCode,

  @Size(max = 100)
  @NotBlank
  String recruitmentTitle,

  @NotNull
  String festivalContentId,

  String content,

  @NotNull
  RecruitmentCategory category,

  // RecruitmentIndividual
  RecruitmentWageType wageType,
  @PositiveOrZero
  Integer wageValue,

  // RecruitmentFoodTruck
  boolean prepaid,
  boolean boothFeeRequired,
  boolean electricityProvided,

  @FutureOrPresent
  @NotNull
  LocalDate applicationDeadline,
  @PositiveOrZero
  Integer recruitmentCount,

  @Size(max = 255)
  String workingLocation,
  @NotNull
  @FutureOrPresent
  LocalDate workingStartDate,
  @NotNull
  @FutureOrPresent
  LocalDate workingEndDate,
  LocalTime workingStartTime,
  LocalTime workingEndTime

) {
}