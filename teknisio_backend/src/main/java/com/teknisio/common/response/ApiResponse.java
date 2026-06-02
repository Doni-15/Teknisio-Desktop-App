package com.teknisio.common.response;

import java.util.Map;

public record ApiResponse<T>(
  boolean success,
  String message,
  T data,
  Map<String, String> errors
) {

  public static <T> ApiResponse<T> success(String message, T data) {
    return new ApiResponse<>(true, message, data, null);
  }

  public static ApiResponse<Void> success(String message) {
    return new ApiResponse<>(true, message, null, null);
  }

  public static ApiResponse<Void> error(String message) {
    return new ApiResponse<>(false, message, null, null);
  }

  public static ApiResponse<Void> error(String message, Map<String, String> errors) {
    return new ApiResponse<>(false, message, null, errors);
  }
}
