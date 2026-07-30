package com.example.chook.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

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

  public static UploadedFileCategory fromMimeType(String mimeType) {
    if (mimeType == null) return OTHERS;
    String mimePrefix = mimeType.split("/")[0];
    return Stream.of(values())
      .filter(category -> category.value.equalsIgnoreCase(mimePrefix))
      .findFirst()
      .orElse(OTHERS);
  }

}
