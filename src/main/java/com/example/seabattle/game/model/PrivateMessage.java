package com.example.seabattle.game.model;

import lombok.Data;

@Data
public class PrivateMessage {
    private String message;
    private String receiverName;
    private String senderName;
}
