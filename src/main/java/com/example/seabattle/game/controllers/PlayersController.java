package com.example.seabattle.game.controllers;


import com.example.seabattle.game.model.Player;
import com.example.seabattle.game.model.PlayersSessionList;
import com.example.seabattle.game.model.PrivateMessage;
import com.example.seabattle.game.services.PlayersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;

@Controller
public class PlayersController {

    @Autowired
    private PlayersService playersService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/addplayer")
    public void joinGame(@Payload String playerName,
                           SimpMessageHeaderAccessor headerAccessor) {
        //Добавляем пользователя в веб сокет сессию
        Player player = new Player();
        player.setLogin(playerName);
        PlayersSessionList.getInstance().addPlayerToSession(player);
        ArrayList<Player> playersList = PlayersSessionList.getInstance().getPlayersList();
        simpMessagingTemplate.convertAndSend("/topic/players", playersList);
        headerAccessor.getSessionAttributes().put("playername",
                player.getLogin());
        headerAccessor.getSessionAttributes().put("playerid", player.getId());
    }

    @MessageMapping("/private-message")
    public void send(@Payload PrivateMessage message){
        simpMessagingTemplate.convertAndSend("/private/messages"+message.getReceiverId(), message.getMessage());
    }

}
