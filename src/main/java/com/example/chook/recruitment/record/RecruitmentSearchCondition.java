package com.example.chook.recruitment.record;

import com.example.chook.recruitment.entity.enums.RecruitmentCategory;
import com.example.chook.recruitment.entity.enums.RecruitmentListCriteria;
import com.example.chook.recruitment.entity.enums.RecruitmentWageType;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Builder
public record RecruitmentSearchCondition(

  List<String> keywordList,

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
}
