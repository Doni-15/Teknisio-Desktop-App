package com.teknisio.service;

import com.google.gson.reflect.TypeToken;
import com.teknisio.dto.ChatMessageDto;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service for chat message operations via REST API.
 * Endpoint: /api/chat/{serviceRequestId}/messages
 */
public class ChatService {

    private static final Type LIST_TYPE = new TypeToken<List<ChatMessageDto>>() {}.getType();

    /**
     * Send a chat message to the backend.
     * POST /api/chat/{serviceRequestId}/messages
     *
     * @return the sent ChatMessageDto, or null on failure
     */
    public static ChatMessageDto sendMessage(String serviceRequestId, String isi) {
        try {
            Map<String, String> body = Map.of("isi", isi);
            ApiClient.ApiResponse<ChatMessageDto> response =
                ApiClient.post("/api/chat/" + serviceRequestId + "/messages", body, ChatMessageDto.class);
            if (response.isSuccess()) {
                return response.getData();
            }
            System.err.println("ChatService.sendMessage failed: " + response.getMessage());
        } catch (IOException | InterruptedException e) {
            System.err.println("ChatService.sendMessage error: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
        return null;
    }

    /**
     * Fetch all messages for a service request.
     * GET /api/chat/{serviceRequestId}/messages
     *
     * @return list of ChatMessageDto, empty list on failure
     */
    public static List<ChatMessageDto> getMessages(String serviceRequestId) {
        try {
            ApiClient.ApiResponse<List<ChatMessageDto>> response =
                ApiClient.get("/api/chat/" + serviceRequestId + "/messages", LIST_TYPE);
            if (response.isSuccess() && response.getData() != null) {
                return response.getData();
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("ChatService.getMessages error: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
        return new ArrayList<>();
    }
}
