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
  @Size(max = 255)
  String workingLocation,

  @NotNull
  String festivalContentId,
  @NotNull
  RecruitmentCategory category,

  @Size(max = 100)
  @NotBlank
  String recruitmentTitle,
  String content,

  @FutureOrPresent
  @NotNull
  LocalDate applicationDeadline,
  @PositiveOrZero
  @Max(value = 9999, message = "모집 인원은 9999명 이하로 입력해주세요.")
  Integer recruitmentCount,


  @NotNull
  @FutureOrPresent
  LocalDate workingStartDate,
  @NotNull
  @FutureOrPresent
  LocalDate workingEndDate,
  LocalTime workingStartTime,
  LocalTime workingEndTime,

  // RecruitmentIndividual
  RecruitmentWageType wageType,
  @PositiveOrZero
  @Max(value = 99999999, message = "급여는 99999999원 이하로 입력해주세요.")
  Integer wageValue,

  // RecruitmentFoodTruck
  boolean prepaid,
  boolean boothFeeRequired,
  boolean electricityProvided

) {
}