package com.teknisio.dto.responses;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ChatMessageResponse(
  UUID idPesan,
  UUID senderId,
  String senderName,
  String senderRole,
  String isi,
  OffsetDateTime createdAt
) {
}
