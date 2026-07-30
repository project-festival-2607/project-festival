package com.example.chook.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UploadedFileCategory {

  APPLICATION("application"),
  AUDIO("audio"),
  IMAGE("image"),
  TEXT("text"),
  VIDEO("video"),
  OTHERS("others");

  private final String value;

}
