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

  private String organizerPhone;
  private Long organizerMemberId;

  // 마감일 당일까지는 수정 가능, 마감일이 지난 다음날부터 수정 불가
  // (status는 스케줄러가 마감일 당일 자정에 CLOSED로 바꾸므로 status==CLOSED와는 별개로 판단)
  public boolean isPastDeadline() {
    return applicationDeadline != null && applicationDeadline.isBefore(LocalDate.now());
  }

  public String getWorkingDurationText() {
    if (workingStartTime == null || workingEndTime == null) return null;
    long minutes = Duration.between(workingStartTime, workingEndTime).toMinutes();
    if (minutes < 0) minutes += 24 * 60;
    long hours = minutes / 60;
    long remainMinutes = minutes % 60;
    return remainMinutes > 0 ? hours + "시간 " + remainMinutes + "분" : hours + "시간";
  }

}
