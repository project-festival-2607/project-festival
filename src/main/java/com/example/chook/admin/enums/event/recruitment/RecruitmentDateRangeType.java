package com.example.chook.admin.enums.event.recruitment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RecruitmentDateRangeType {

  PUBLISHED_AT("게시일시"),
  DELETED_AT("삭제일시"),
  APPLICATION_DEADLINE("마감기한"),
  WORKING_START_DATE("근무시작일"),
  WORKING_END_DATE("근무종료일"),
  WORK_DURATION_OVERLAPPING("근무기간 겹침"),
  WORK_DURATION_CONTAINED_IN("근무기간 전체 포함");

  private final String label;
  private final boolean dateOnly;

  RecruitmentDateRangeType(String label) {
    this(label, true);
  }

}
