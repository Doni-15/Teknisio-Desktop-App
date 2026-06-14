package com.teknisio.controllers;

import com.teknisio.common.response.ApiResponse;
import com.teknisio.dto.requests.SendChatMessageRequest;
import com.teknisio.dto.responses.ChatMessageResponse;
import com.teknisio.services.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

  private final ChatService chatService;

  /**
   * POST /api/chat/{serviceRequestId}/messages
   * Send a chat message. Accessible by the customer or technician of the request.
   */
  @PostMapping("/{serviceRequestId}/messages")
  public ResponseEntity<ApiResponse<ChatMessageResponse>> sendMessage(
    @PathVariable String serviceRequestId,
    @Valid @RequestBody SendChatMessageRequest request
  ) {
    ChatMessageResponse response = chatService.sendMessage(serviceRequestId, request);

    return ResponseEntity
      .status(HttpStatus.CREATED)
      .body(ApiResponse.success("Message sent successfully", response));
  }

  /**
   * GET /api/chat/{serviceRequestId}/messages
   * Retrieve all chat messages for a service request. Accessible by the customer or technician.
   */
  @GetMapping("/{serviceRequestId}/messages")
  public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> getMessages(
    @PathVariable String serviceRequestId
  ) {
    List<ChatMessageResponse> response = chatService.getMessages(serviceRequestId);

    return ResponseEntity.ok(
      ApiResponse.success("Messages retrieved successfully", response)
    );
  }
}
