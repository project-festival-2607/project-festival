package com.example.chook.recruitment.record;

import com.example.chook.recruitment.entity.enums.RecruitmentCategory;
import com.example.chook.recruitment.entity.enums.RecruitmentListCriteria;
import com.example.chook.recruitment.entity.enums.RecruitmentStatus;
import lombok.Builder;

@Builder
public record RecruitmentManagementCondition(

  String festivalContentId,
  String festivalUserName,

  RecruitmentCategory category,

  RecruitmentStatus status,
  Boolean isPublished,
  Boolean isDeleted,

  RecruitmentListCriteria listCriteria

) {
}
