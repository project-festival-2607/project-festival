package com.example.chook.member.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Provider {

  GOOGLE("Google"),
  NAVER("NAVER"),
  KAKAO("kakao");

  private final String label;

}
