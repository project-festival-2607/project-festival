package com.example.chook.admin.enums.board.notice;

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
