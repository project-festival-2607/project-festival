package com.example.chook.admin.dto.event;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FestivalTableDTO {

  private String contentId;
  private String title;
  private String address;
  private String eventPlace;
  private String zipCode;
  private LocalDate startDate;
  private LocalDate endDate;

  private Long memberId;
  private String memberUsername;
  private String memberName;
  private Long recruitmentCount;

}
