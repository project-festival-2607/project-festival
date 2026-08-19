package com.example.chook.admin.entity.enums.notice;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NoticeDateRangeType {

  CREATED_AT("게시일시");

  private final String label;
  private final boolean dateOnly;

  NoticeDateRangeType(String label) {
    this(label, true);
  }


}
