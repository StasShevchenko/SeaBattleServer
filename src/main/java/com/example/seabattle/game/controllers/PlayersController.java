package com.example.seabattle.game.controllers;


import com.example.seabattle.game.model.Player;
import com.example.seabattle.game.model.PlayersSessionList;
import com.example.seabattle.game.services.PlayersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;

@Controller
public class PlayersController {

    @Autowired
    private PlayersService playersService;

    @MessageMapping("/addplayer")
    public void joinGame(@Payload Player player,
                           SimpMessageHeaderAccessor headerAccessor) {
        //Добавляем пользователя в веб сокет сессию
        PlayersSessionList.getInstance().addPlayerToSession(player);
        ArrayList<Player> playersList = PlayersSessionList.getInstance().getPlayersList();
        playersService.emitPlayersList(playersList);
        headerAccessor.getSessionAttributes().put("playername",
                player.getLogin());
    }
}
