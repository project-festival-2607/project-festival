package com.example.chook.recruitment.form;

import com.example.chook.recruitment.entity.enums.RecruitmentCategory;
import com.example.chook.recruitment.entity.enums.RecruitmentListCriteria;
import com.example.chook.recruitment.entity.enums.RecruitmentStatus;

public record RecruitmentManagementForm(

  String festivalContentId,
  RecruitmentCategory category,

  RecruitmentStatus status,
  Boolean isPublished,
  Boolean isDeleted,

  RecruitmentListCriteria listCriteria

) {

  public RecruitmentManagementForm {

    listCriteria = listCriteria == null ? RecruitmentListCriteria.LATEST : listCriteria;
    festivalContentId = blankToNull(festivalContentId);

  }

  private String blankToNull(String string) {
    return (string == null || string.isBlank()) ? null : string;
  }

}
