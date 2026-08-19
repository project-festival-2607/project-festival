package com.example.chook.recruitment.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RecruitmentStatus {

  DRAFT("초안", "recruitment-status-draft"),
  RECRUITING("모집중", "recruitment-status-recruiting"),
  PAUSED("모집중단", "recruitment-status-paused"),
  CLOSED("모집종료", "recruitment-status-closed");

  private final String label;
  private final String badgeClass;

}
