package com.example.chook.recruitment.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RecruitmentListCriteria {

  LATEST(null),
  DEADLINE(null),
  WAGE_HOURLY(RecruitmentWageType.HOURLY),
  WAGE_DAILY(RecruitmentWageType.DAILY),
  WAGE_WEEKLY(RecruitmentWageType.WEEKLY),
  WAGE_PER_TASK(RecruitmentWageType.PER_TASK);

  private final RecruitmentWageType wageType;

  public boolean isWageCriteria() {
    return wageType != null;
  }

}
