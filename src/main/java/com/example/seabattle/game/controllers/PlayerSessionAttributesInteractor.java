package com.example.seabattle.game.controllers;

import com.example.seabattle.game.model.Player;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

public class PlayerSessionAttributesInteractor {
    private SimpMessageHeaderAccessor headerAccessor;

    public PlayerSessionAttributesInteractor(SimpMessageHeaderAccessor headerAccessor) {
        this.headerAccessor = headerAccessor;
    }

    public void addPlayerToSession(Player player) {
        headerAccessor.getSessionAttributes().put("playerName",
                player.getLogin());
        headerAccessor.getSessionAttributes().put("playerStatus", player.getStatus());
    }

    public Player getPlayerFromDisconnectedSession(){
        Player player = new Player();
        String playerName = (String) headerAccessor.getSessionAttributes().get("playerName");
        String playerStatus = (String) headerAccessor.getSessionAttributes().get("playerStatus");
        player.setStatus(playerStatus);
        player.setLogin(playerName);
        return player;
    }
}
