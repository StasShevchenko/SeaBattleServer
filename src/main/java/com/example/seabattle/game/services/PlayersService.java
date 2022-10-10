package com.example.seabattle.game.services;


import com.example.seabattle.game.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class PlayersService {

    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    public PlayersService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void emitPlayersList(final ArrayList<Player> playersList) {
        messagingTemplate.convertAndSend("/topic/players", playersList);
    }

}
