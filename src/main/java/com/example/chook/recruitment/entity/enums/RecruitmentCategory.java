package com.example.chook.recruitment.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RecruitmentCategory {

  INDIVIDUAL("인력"),
  FOOD_TRUCK("푸드트럭"),
  EQUIPMENT("장비"),
  ETC("기타");

  private final String label;

}
