package com.teknisio.services;

import com.teknisio.common.exception.BadRequestException;
import com.teknisio.common.exception.ResourceNotFoundException;
import com.teknisio.common.exception.UnauthorizedException;
import com.teknisio.common.util.TextUtil;
import com.teknisio.dto.requests.SendChatMessageRequest;
import com.teknisio.dto.responses.ChatMessageResponse;
import com.teknisio.model.entities.PermintaanLayanan;
import com.teknisio.model.entities.PesanChat;
import com.teknisio.model.entities.User;
import com.teknisio.model.enums.RequestStatus;
import com.teknisio.model.enums.UserStatus;
import com.teknisio.repositories.PermintaanLayananRepository;
import com.teknisio.repositories.PesanChatRepository;
import com.teknisio.repositories.UserRepository;
import com.teknisio.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {

  private final CurrentUserService currentUserService;
  private final UserRepository userRepository;
  private final PermintaanLayananRepository permintaanLayananRepository;
  private final PesanChatRepository pesanChatRepository;

  /**
   * Send a chat message on a service request.
   * Only the customer who owns the request or the assigned technician can send messages.
   * Chat is only allowed when status is ACCEPTED or ON_PROGRESS.
   */
  @Transactional
  public ChatMessageResponse sendMessage(String serviceRequestId, SendChatMessageRequest request) {
    User sender = getCurrentActiveUser();
    PermintaanLayanan serviceRequest = getAccessibleServiceRequest(serviceRequestId, sender);

    PesanChat message = PesanChat.builder()
      .permintaanLayanan(serviceRequest)
      .pengirim(sender)
      .isi(TextUtil.trim(request.isi()))
      .build();

    PesanChat saved = pesanChatRepository.saveAndFlush(message);
    return toResponse(saved);
  }

  /**
   * Get all chat messages for a service request.
   * Only the customer who owns the request or the assigned technician can read messages.
   */
  @Transactional(readOnly = true)
  public List<ChatMessageResponse> getMessages(String serviceRequestId) {
    User currentUser = getCurrentActiveUser();
    PermintaanLayanan serviceRequest = getAccessibleServiceRequest(serviceRequestId, currentUser);

    return pesanChatRepository
      .findByPermintaanLayanan_IdPermintaanOrderByCreatedAtAsc(serviceRequest.getIdPermintaan())
      .stream()
      .map(this::toResponse)
      .toList();
  }

  // ---- Helpers ----

  private User getCurrentActiveUser() {
    UUID currentUserId = currentUserService.getCurrentUserId();
    return userRepository.findByIdUserAndDeletedAtIsNull(currentUserId)
      .filter(user -> user.getStatusAkun() == UserStatus.ACTIVE)
      .orElseThrow(() -> new UnauthorizedException("Unauthorized"));
  }

  private PermintaanLayanan getAccessibleServiceRequest(String serviceRequestId, User currentUser) {
    UUID idPermintaan = parseServiceRequestId(serviceRequestId);

    PermintaanLayanan serviceRequest = permintaanLayananRepository.findById(idPermintaan)
      .orElseThrow(() -> new ResourceNotFoundException("Service request not found"));

    // Validate chat is only allowed for ACCEPTED or ON_PROGRESS requests
    if (serviceRequest.getStatus() != RequestStatus.ACCEPTED
      && serviceRequest.getStatus() != RequestStatus.ON_PROGRESS) {
      throw new BadRequestException(
        "Chat is only available when the service request is ACCEPTED or ON_PROGRESS"
      );
    }

    // Validate the current user is either the customer or the assigned technician
    boolean isCustomer = serviceRequest.getPengguna() != null
      && serviceRequest.getPengguna().getIdUser().equals(currentUser.getIdUser());

    boolean isTechnician = serviceRequest.getTeknisiProfile() != null
      && serviceRequest.getTeknisiProfile().getUser() != null
      && serviceRequest.getTeknisiProfile().getUser().getIdUser().equals(currentUser.getIdUser());

    if (!isCustomer && !isTechnician) {
      throw new UnauthorizedException("You are not authorized to access this chat");
    }

    return serviceRequest;
  }

  private UUID parseServiceRequestId(String serviceRequestId) {
    if (TextUtil.isBlank(serviceRequestId)) {
      throw new BadRequestException("Service request id is required");
    }
    try {
      return UUID.fromString(serviceRequestId);
    } catch (IllegalArgumentException e) {
      throw new BadRequestException("Invalid service request id");
    }
  }

  private ChatMessageResponse toResponse(PesanChat message) {
    User sender = message.getPengirim();
    String senderRole = sender.getRole() != null ? sender.getRole().name() : "UNKNOWN";

    return new ChatMessageResponse(
      message.getIdPesan(),
      sender.getIdUser(),
      sender.getNama(),
      senderRole,
      message.getIsi(),
      message.getCreatedAt()
    );
  }
}
