package com.example.seabattle.game.model;

import lombok.Data;

/**
 * Класс для получения
 * информации об игре из
 * базы данных
 */
@Data
public class GameInfo {
    private String winner;
    private String loser;
    private String date;
}
