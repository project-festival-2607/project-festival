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

  public static String getFormattedPhone(String phone) {
    return phone.length() == 11
      ? String.format("%s-%s-%s", phone.substring(0, 3), phone.substring(3, 7), phone.substring(7))
      : phone;
  }

  public static String getFormattedBusinessNumber(String businessNumber) {
    if (businessNumber == null) return "";
    return businessNumber.length() == 10
      ? String.format("%s-%s-%s", businessNumber.substring(0, 3), businessNumber.substring(3, 5), businessNumber.substring(5))
      : businessNumber;
  }
}
