package com.example.chook.festival;

import lombok.Builder;

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

  private String blankToNull(String string) {
    return (string == null || string.isBlank()) ? null : string;
  }

}
