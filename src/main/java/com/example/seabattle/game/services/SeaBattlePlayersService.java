package com.example.seabattle.game.services;

import com.example.seabattle.game.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class SeaBattlePlayersService {

    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    public SeaBattlePlayersService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void emitNewPlayer(final Player player) {
        messagingTemplate.convertAndSend("/topic/players", player);
    }

}
