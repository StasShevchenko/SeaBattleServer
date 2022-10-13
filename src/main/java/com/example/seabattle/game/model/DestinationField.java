package com.example.seabattle.game.model;

import lombok.Data;

@Data
public class DestinationField {
    private String receiverId;
    private Integer[][] gameField;
}
