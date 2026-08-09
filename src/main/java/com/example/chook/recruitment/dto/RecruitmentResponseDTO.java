package com.example.chook.recruitment.dto;

import com.example.chook.recruitment.entity.enums.RecruitmentCategory;
import com.example.chook.recruitment.entity.enums.RecruitmentStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecruitmentResponseDTO {

  private Long recruitmentId;

  private String regionSidoName;
  private String regionSigunguName;
  private String workingLocation;

  private String festivalContentId;
  private String festivalTitle;

  private RecruitmentCategory category;
  private RecruitmentSpecificDTO specific;

  private String recruitmentTitle;
  private String content;

  private LocalDate applicationDeadline;
  private Integer recruitmentCount;

  private LocalDate workingStartDate;
  private LocalDate workingEndDate;
  private LocalTime workingStartTime;
  private LocalTime workingEndTime;

  private RecruitmentStatus status;
  private LocalDateTime publishedAt;
  private LocalDateTime updatedAt;
  private LocalDateTime deletedAt;

}
