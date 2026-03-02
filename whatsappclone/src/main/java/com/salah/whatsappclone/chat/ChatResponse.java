package com.salah.whatsappclone.chat;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponse {
    private String id;
    private String name;
    private long unreadCount;
    private String lastMessage;
    private boolean isReceiverOnline;
    private String senderId;
    private String receiverId;


}
