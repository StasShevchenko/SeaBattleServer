package com.example.seabattle.game.controllers;

import com.example.seabattle.game.model.Player;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

/**
 * Класс обёртка
 * необходимый для сохранения
 * данных игрока в веб-сокет
 * сессию и получения данных
 * об игроке обратно
 */
public class PlayerSessionAttributesInteractor {
    private SimpMessageHeaderAccessor headerAccessor;

    public PlayerSessionAttributesInteractor(SimpMessageHeaderAccessor headerAccessor) {
        this.headerAccessor = headerAccessor;
    }

    /**
     * Метод добавления
     * данных об игроке в
     * веб-сокет сессию
     * @param player
     */
    public void addPlayerToSession(Player player) {
        headerAccessor.getSessionAttributes().put("playerName",
                player.getLogin());
        headerAccessor.getSessionAttributes().put("playerStatus", player.getStatus());
    }

    /**
     * Метод получения
     * данных об игроке из
     * веб-сокет сессии
     * @return
     */
    public Player getPlayerFromDisconnectedSession(){
        Player player = new Player();
        String playerName = (String) headerAccessor.getSessionAttributes().get("playerName");
        String playerStatus = (String) headerAccessor.getSessionAttributes().get("playerStatus");
        player.setStatus(playerStatus);
        player.setLogin(playerName);
        return player;
    }
}
