package com.example.seabattle.game.model;

import lombok.Data;

/**
 * Класс, для хранения информации
 * об игровом поле
 */
@Data
public class DestinationField {
    private String receiverName;
    private Integer[][] gameField;
}
