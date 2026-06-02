package com.teknisio.common.util;

import com.teknisio.common.exception.BadRequestException;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

public final class EnumParser {

  private EnumParser() {
  }

  public static <E extends Enum<E>> E parseRequired(
    Class<E> enumType,
    String value,
    String fieldName
  ) {
    if (TextUtil.isBlank(value)) {
      throw new BadRequestException(fieldName + " is required");
    }

    return parse(enumType, value, fieldName);
  }

  public static <E extends Enum<E>> E parseOptional(
    Class<E> enumType,
    String value,
    String fieldName
  ) {
    if (TextUtil.isBlank(value)) {
      return null;
    }

    return parse(enumType, value, fieldName);
  }

  private static <E extends Enum<E>> E parse(
    Class<E> enumType,
    String value,
    String fieldName
  ) {
    try {
      return Enum.valueOf(enumType, value.trim().toUpperCase(Locale.ROOT));
    } catch (IllegalArgumentException exception) {
      throw new BadRequestException(
        "Invalid " + fieldName + ". Allowed values: " + allowedValues(enumType)
      );
    }
  }

  private static <E extends Enum<E>> String allowedValues(Class<E> enumType) {
    return Arrays.stream(enumType.getEnumConstants())
      .map(Enum::name)
      .collect(Collectors.joining(", "));
  }
}
