package com.example.chook.recruitment.form;

import com.example.chook.recruitment.entity.enums.RecruitmentCategory;
import com.example.chook.recruitment.entity.enums.RecruitmentListCriteria;
import com.example.chook.recruitment.entity.enums.RecruitmentWageType;

import java.time.LocalDate;
import java.time.LocalTime;

import static com.example.chook.common.util.CustomStringUtils.blankToNull;

public record RecruitmentSearchForm(

  String keywords,

  String regionSidoCode,
  String regionSigunguCode,

  RecruitmentCategory category,

  LocalTime workingStartTime,
  LocalTime workingEndTime,
  LocalDate workingStartDate,
  LocalDate workingEndDate,

  RecruitmentListCriteria listCriteria,

  // 알바(INDIVIDUAL) 전용 필터
  RecruitmentWageType wageType,

  // 푸드트럭(FOOD_TRUCK) 전용 필터
  Boolean boothFeeRequired,
  Boolean electricityProvided,
  Boolean prepaid

) {

  public RecruitmentSearchForm {

    regionSidoCode = blankToNull(regionSidoCode);
    regionSigunguCode = blankToNull(regionSigunguCode);

    if (category != RecruitmentCategory.INDIVIDUAL) {
      wageType = null;
    }
    if (category != RecruitmentCategory.FOOD_TRUCK) {
      boothFeeRequired = null;
      electricityProvided = null;
      prepaid = null;
    }

    listCriteria = listCriteria == null ? RecruitmentListCriteria.LATEST : listCriteria;
  }

}
