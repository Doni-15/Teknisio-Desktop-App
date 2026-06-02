package com.teknisio.common.util;

import java.util.Locale;

public final class TextUtil {

  private TextUtil() {
  }

  public static String trim(String value) {
    return value == null ? null : value.trim();
  }

  public static String normalizeEmail(String email) {
    String trimmedEmail = trim(email);
    return trimmedEmail == null ? null : trimmedEmail.toLowerCase(Locale.ROOT);
  }

  public static boolean isBlank(String value) {
    return value == null || value.isBlank();
  }
}
