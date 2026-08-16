package com.example.chook.common.util;

import java.util.Arrays;
import java.util.List;

public final class CustomStringUtils {

  public static List<String> splitByRegex(String targetString, String regex) {
    if (targetString == null || targetString.isBlank()) return List.of();
    return Arrays.stream(targetString.trim().split(regex)).toList();
  }

  public static String blankToNull(String string) {
    return (string == null || string.isBlank()) ? null : string;
  }

  public static <T extends Enum<T>> T toEnum(
    String string,
    Class<T> enumClass,
    T defaultValue
  ) {
    if (string == null) return defaultValue;
    try {
      return Enum.valueOf(enumClass, string);
    } catch (IllegalArgumentException e) {
      return defaultValue;
    }
  }
}
