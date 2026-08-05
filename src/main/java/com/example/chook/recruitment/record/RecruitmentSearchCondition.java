package com.example.chook.recruitment.record;

import com.example.chook.recruitment.entity.enums.RecruitmentCategory;
import com.example.chook.recruitment.entity.enums.RecruitmentListCriteria;
import com.example.chook.recruitment.entity.enums.RecruitmentStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record RecruitmentSearchCondition(

  List<String> keywords,

  String regionSidoCode,
  String regionSigunguCode,

  RecruitmentCategory category,
  RecruitmentStatus status,

  LocalTime workingStartTime,
  LocalTime workingEndTime,
  LocalDate workingStartDate,
  LocalDate workingEndDate,

  RecruitmentListCriteria listCriteria

) {
}
