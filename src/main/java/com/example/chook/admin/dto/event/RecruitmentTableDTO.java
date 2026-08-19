package com.example.chook.admin.dto.event;

import com.example.chook.recruitment.entity.enums.RecruitmentCategory;
import com.example.chook.recruitment.entity.enums.RecruitmentStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecruitmentTableDTO {

  private Long id;
  private String title;
  private RecruitmentCategory category;
  private String festivalContentId;
  private String festivalTitle;

  private String sidoName;
  private String sigunguName;
  private String workingLocation;
  private LocalDate applicationDeadline;
  private LocalDate workingStartDate;
  private LocalDate workingEndDate;

  private RecruitmentStatus status;
  private LocalDateTime publishedAt;
  private LocalDateTime deletedAt;
  private Long applicantCount;

}
