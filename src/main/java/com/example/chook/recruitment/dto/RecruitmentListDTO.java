package com.example.chook.recruitment.dto;

import com.example.chook.recruitment.entity.enums.RecruitmentCategory;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecruitmentListDTO {

  private Long recruitmentId;

  private String regionSidoName;
  private String regionSigunguName;

  private String festivalContentId;
  private String festivalTitle;

  private RecruitmentCategory category;
  private RecruitmentSpecificDTO specific;

  private String recruitmentTitle;

  private LocalDate applicationDeadline;

  private LocalDate workingStartDate;
  private LocalDate workingEndDate;
  private LocalTime workingStartTime;
  private LocalTime workingEndTime;

  // 목록 조회 후 컨트롤러에서 로그인한 구직자의 찜 여부를 채워넣음 (기본 false)
  private boolean bookmarked;

}
