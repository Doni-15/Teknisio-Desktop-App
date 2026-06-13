package com.teknisio.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * HTTP client wrapper for Teknisio backend API communication.
 */
public class ApiClient {

    private static final String DEFAULT_BASE_URL = "http://localhost:8080";
    private static String BASE_URL = DEFAULT_BASE_URL;
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private static final Gson gson = new Gson();

    private static String jwtToken = null;

    public static void setBaseUrl(String url) {
        BASE_URL = url;
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static void setToken(String token) {
        jwtToken = token;
    }

    public static String getToken() {
        return jwtToken;
    }

    public static void clearToken() {
        jwtToken = null;
    }

    /**
     * Send GET request.
     */
    public static <T> ApiResponse<T> get(String path, Type responseType) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(15))
                .GET();

        if (jwtToken != null && !jwtToken.isEmpty()) {
            builder.header("Authorization", "Bearer " + jwtToken);
        }

        HttpRequest request = builder.build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return parseResponse(response, responseType);
    }

    /**
     * Send POST request.
     */
    public static <T> ApiResponse<T> post(String path, Object body, Type responseType) throws IOException, InterruptedException {
        String jsonBody = gson.toJson(body);

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(15))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody));

        if (jwtToken != null && !jwtToken.isEmpty()) {
            builder.header("Authorization", "Bearer " + jwtToken);
        }

        HttpRequest request = builder.build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return parseResponse(response, responseType);
    }

    /**
     * Send PUT request.
     */
    public static <T> ApiResponse<T> put(String path, Object body, Type responseType) throws IOException, InterruptedException {
        String jsonBody = gson.toJson(body);

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(15))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody));

        if (jwtToken != null && !jwtToken.isEmpty()) {
            builder.header("Authorization", "Bearer " + jwtToken);
        }

        HttpRequest request = builder.build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return parseResponse(response, responseType);
    }

    private static <T> ApiResponse<T> parseResponse(HttpResponse<String> response, Type responseType) {
        int statusCode = response.statusCode();
        String body = response.body();

        if (statusCode >= 200 && statusCode < 300) {
            try {
                // Teknisio backend wraps all responses in ApiResponse<T> envelope
                JsonObject jsonObj = gson.fromJson(body, JsonObject.class);
                String message = jsonObj.has("message") ? jsonObj.get("message").getAsString() : "Success";
                T data = null;
                if (jsonObj.has("data") && !jsonObj.get("data").isJsonNull()) {
                    data = gson.fromJson(jsonObj.get("data"), responseType);
                }
                return new ApiResponse<>(true, message, data, statusCode);
            } catch (Exception e) {
                // Fallback: try to parse body directly as the response type
                try {
                    T data = gson.fromJson(body, responseType);
                    return new ApiResponse<>(true, "Success", data, statusCode);
                } catch (Exception e2) {
                    return new ApiResponse<>(true, "Success", null, statusCode);
                }
            }
        } else {
            String errorMsg = body;
            try {
                JsonObject errObj = gson.fromJson(body, JsonObject.class);
                if (errObj.has("message")) {
                    errorMsg = errObj.get("message").getAsString();
                }
            } catch (Exception ignored) {}
            return new ApiResponse<>(false, errorMsg, null, statusCode);
        }
    }

    /**
     * Generic API response wrapper matching the backend's ApiResponse structure.
     */
    public static class ApiResponse<T> {
        private final boolean success;
        private final String message;
        private final T data;
        private final int statusCode;

        public ApiResponse(boolean success, String message, T data, int statusCode) {
            this.success = success;
            this.message = message;
            this.data = data;
            this.statusCode = statusCode;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public T getData() { return data; }
        public int getStatusCode() { return statusCode; }
    }
}