package com.example.chook.recruitment.record;

import com.example.chook.recruitment.entity.enums.RecruitmentCategory;
import com.example.chook.recruitment.entity.enums.RecruitmentListCriteria;
import com.example.chook.recruitment.entity.enums.RecruitmentStatus;
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
  RecruitmentStatus status,

  RecruitmentListCriteria listCriteria,
  Boolean publishedOnly,

  LocalTime workingStartTime,
  LocalTime workingEndTime,
  LocalDate workingStartDate,
  LocalDate workingEndDate,

  // 알바(INDIVIDUAL) 전용 필터
  RecruitmentWageType wageType,

  // 푸드트럭(FOOD_TRUCK) 전용 필터
  Boolean boothFeeRequired,
  Boolean electricityProvided,
  Boolean prepaid

) {

  public RecruitmentSearchCondition {
    regionSidoCode = blankToNull(regionSidoCode);
    regionSigunguCode = blankToNull(regionSigunguCode);
  }

  private String blankToNull(String string) {
    return (string == null || string.isBlank()) ? null : string;
  }

}
