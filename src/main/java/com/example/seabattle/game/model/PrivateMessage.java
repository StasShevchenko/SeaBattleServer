package com.example.seabattle.game.model;

import lombok.Data;

@Data
public class PrivateMessage {
    private String message;
    private String receiverId;
    private String senderId;
    private String senderName;
}
