package com.example.chook.festival;

import lombok.Builder;

import static com.example.chook.common.util.CustomStringUtils.blankToNull;

@Builder
public record FestivalSearchForm(

  String type,
  String keyword,
  String month

) {
  public FestivalSearchForm {
    type = blankToNull(type);
    keyword = blankToNull(keyword);
    month = blankToNull(month);
  }

}
