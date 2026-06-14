package com.teknisio.dto;

/**
 * Matches backend ChatMessageResponse record.
 */
public class ChatMessageDto {
    private String idPesan;
    private String senderId;
    private String senderName;
    private String senderRole; // "CUSTOMER" or "TECHNICIAN"
    private String isi;
    private String createdAt;

    public ChatMessageDto() {}

    public String getIdPesan() { return idPesan; }
    public String getSenderId() { return senderId; }
    public String getSenderName() { return senderName; }
    public String getSenderRole() { return senderRole; }
    public String getIsi() { return isi; }
    public String getCreatedAt() { return createdAt; }

    public void setIdPesan(String idPesan) { this.idPesan = idPesan; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public void setSenderRole(String senderRole) { this.senderRole = senderRole; }
    public void setIsi(String isi) { this.isi = isi; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
