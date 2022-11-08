package com.example.seabattle.game.controllers;


import com.example.seabattle.game.model.*;
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
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/addplayer")
    public void joinGame(@Payload String playerName,
                           SimpMessageHeaderAccessor headerAccessor) {
        //Добавляем пользователя в веб сокет сессию
        Player player = new Player();
        player.setLogin(playerName);
        System.out.println("Игрок добавлен через join");
        PlayersSessionList.getInstance().addPlayerToSession(player);
        ArrayList<Player> playersList = PlayersSessionList.getInstance().getPlayersList();
        simpMessagingTemplate.convertAndSend("/topic/players", playersList);
        headerAccessor.getSessionAttributes().put("playername",
                player.getLogin());
        headerAccessor.getSessionAttributes().put("playerid", player.getId());
    }

    @MessageMapping("/private-message")
    public void send(@Payload PrivateMessage message){
        switch (message.getMessage()) {
            case "INVITE" -> {
                hidePlayer(message.getSenderId());
                hidePlayer(message.getReceiverId());
            }
            case "REJECT", "CANCEL_INVITATION" -> {
                showPlayer(message.getSenderId());
                showPlayer(message.getReceiverId());
            }
            case "YOU_LOSE" -> {
                showPlayer(message.getSenderId());
                showPlayer(message.getReceiverId());
                Game game = GameSessionList.getInstance().getGameByPlayerId(message.getReceiverId());
                GameSessionList.getInstance().removeGameFromSession(game);
            }
        }
        simpMessagingTemplate.convertAndSend("/private/messages"+message.getReceiverId(), message);
    }

    @MessageMapping("/init-game")
    public void initGame(@Payload Game game){
        GameSessionList.getInstance().addGameToSession(game);
        simpMessagingTemplate.convertAndSend("/private/game"+game.getFirstPlayerId(), game);
        simpMessagingTemplate.convertAndSend("/private/game"+game.getSecondPlayerId(), game);
    }

    @MessageMapping("/send-field")
    public void sendField(@Payload DestinationField destinationField){
        simpMessagingTemplate.convertAndSend("/private/game"+destinationField.getReceiverId(), destinationField);
    }

    @MessageMapping("/send-move")
    public void sendMove(@Payload Move move){
        simpMessagingTemplate.convertAndSend("/private/game" + move.getReceiverId(), move);
    }



    private void hidePlayer(String playerId) {
        Player player = PlayersSessionList.getInstance().getPlayerById(playerId);
        System.out.println("Прячем игрока " + player);
        HiddenPlayersSessionList.getInstance().addPlayerToHiddenSession(player);
        PlayersSessionList.getInstance().removePlayerFromSession(player);
        ArrayList<Player> playersList = PlayersSessionList.getInstance().getPlayersList();
        simpMessagingTemplate.convertAndSend("/topic/players", playersList);
    }

    private void showPlayer(String playerId) {
        Player player = HiddenPlayersSessionList.getInstance().getPlayerById(playerId);
        System.out.println("Игрок добавлен через show Player "+ player);
        PlayersSessionList.getInstance().addPlayerToSession(player);
        HiddenPlayersSessionList.getInstance().removePlayerFromHiddenSession(player);
        ArrayList<Player> playersList = PlayersSessionList.getInstance().getPlayersList();
        simpMessagingTemplate.convertAndSend("/topic/players", playersList);
    }

}
