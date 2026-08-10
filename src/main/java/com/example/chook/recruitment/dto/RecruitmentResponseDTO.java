package com.example.chook.recruitment.dto;

import com.example.chook.recruitment.entity.enums.RecruitmentCategory;
import com.example.chook.recruitment.entity.enums.RecruitmentStatus;
import lombok.*;

import java.time.Duration;
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

  private String regionSidoName;
  private String regionSigunguName;

  private Long recruitmentId;
  private String recruitmentTitle;
  private String festivalContentId;
  private String festivalTitle;

  private String content;

  private RecruitmentCategory category;
  private RecruitmentSpecificDTO specific;

  private LocalDate applicationDeadline;
  private Integer recruitmentCount;
  private RecruitmentStatus status;

  private String workingLocation;
  private LocalDate workingStartDate;
  private LocalDate workingEndDate;
  private LocalTime workingStartTime;
  private LocalTime workingEndTime;

  private boolean published;
  private LocalDateTime publishedAt;

  public String getWorkingDurationText() {
    if (workingStartTime == null || workingEndTime == null) return null;
    long minutes = Duration.between(workingStartTime, workingEndTime).toMinutes();
    if (minutes < 0) minutes += 24 * 60;
    long hours = minutes / 60;
    long remainMinutes = minutes % 60;
    return remainMinutes > 0 ? hours + "시간 " + remainMinutes + "분" : hours + "시간";
  }

}
