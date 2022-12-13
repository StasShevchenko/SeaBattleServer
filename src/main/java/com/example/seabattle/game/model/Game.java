package com.example.seabattle.game.model;

import lombok.Data;

/**
 * Класс для хранения
 * информации об игроках,
 * находящихся в игровой сессии
 */
@Data
public class Game {
    private String firstPlayerName;
    private String secondPlayerName;
}
