package com.example.chook.recruitment.form;

import com.example.chook.recruitment.entity.enums.RecruitmentCategory;
import com.example.chook.recruitment.entity.enums.RecruitmentStatus;

public record RecruitmentManagementForm(

  String festivalContentId,
  RecruitmentCategory category,

  RecruitmentStatus status,
  Boolean isPublished,
  Boolean isDeleted

) {
}
