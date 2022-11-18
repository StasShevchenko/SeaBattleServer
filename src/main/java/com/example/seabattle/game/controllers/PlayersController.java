package com.example.seabattle.game.controllers;


import com.example.seabattle.game.data_source.GamesDb;
import com.example.seabattle.game.data_source.GamesRepository;
import com.example.seabattle.game.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Objects;

@Controller
public class PlayersController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/addplayer")
    public synchronized void joinGame(@Payload Player player,
                           SimpMessageHeaderAccessor headerAccessor) {
        //Добавляем пользователя в веб сокет сессию

        PlayerSessionAttributesInteractor sessionInteractor = new PlayerSessionAttributesInteractor(headerAccessor);
        sessionInteractor.addPlayerToSession(player);


        PlayersSessionList playersSessionList = PlayersSessionList.getInstance();
        Player currentPlayer = playersSessionList.getPlayerByName(player.getLogin());
        //Добавляем игрока в список свободных игроков, если его там еще нет
        //Он там будет в случае обновления страницы или в случае окончания игры
        if (currentPlayer == null) {
            currentPlayer = HiddenPlayersSessionList.getInstance().getPlayerByName(player.getLogin());
            //Проверяем, не в игре ли сейчас игрок
            if(currentPlayer == null) {
                playersSessionList.addPlayerToSession(player);
            }
        }
        //Отсылаем список игроков всем вновь подключившимся игрокам
        ArrayList<Player> playersList = playersSessionList.getPlayersList();
        simpMessagingTemplate.convertAndSend("/topic/players", playersList);
    }

    @MessageMapping("/private-message")
    public void send(@Payload PrivateMessage message){
        switch (message.getMessage()) {
            case "INVITE" -> {
                hidePlayer(message.getSenderName());
                hidePlayer(message.getReceiverName());
            }
            case "REJECT", "CANCEL_INVITATION" -> {
                showPlayer(message.getSenderName());
                showPlayer(message.getReceiverName());
            }
            case "YOU_LOSE" -> {
                Game game = GameSessionList.getInstance().getGameByPlayerName(message.getReceiverName());
                String winnerId;
                String loserId;
                if (Objects.equals(game.getFirstPlayerName(), message.getSenderName())) {
                    winnerId = game.getFirstPlayerName();
                    loserId = game.getFirstPlayerName();
                } else {
                    winnerId = game.getSecondPlayerName();
                    loserId = game.getFirstPlayerName();
                }
                GamesDb gamesDb = new GamesDb();
                GamesRepository gamesRepository = new GamesRepository(gamesDb);
                ArrayList<GameInfo> gamesList = gamesRepository.addGame(winnerId, loserId);
                GameSessionList.getInstance().removeGameFromSession(game);
                showPlayer(message.getSenderName());
                showPlayer(message.getReceiverName());
            }
        }
        simpMessagingTemplate.convertAndSend("/private/messages"+message.getReceiverName(), message);
    }

    @MessageMapping("/init-game")
    public void initGame(@Payload Game game){
        Game currentGame = GameSessionList.getInstance().getGameByPlayerName(game.getFirstPlayerName());
        if(currentGame == null) {
            GameSessionList.getInstance().addGameToSession(game);
        }
    }

    @MessageMapping("/send-field")
    public void sendField(@Payload DestinationField destinationField){
        simpMessagingTemplate.convertAndSend("/private/game"+destinationField.getReceiverName(), destinationField);
    }

    @MessageMapping("/send-move")
    public void sendMove(@Payload Move move){
        simpMessagingTemplate.convertAndSend("/private/game" + move.getReceiverName(), move);
    }



    private void hidePlayer(String playerName) {
        Player player = PlayersSessionList.getInstance().getPlayerByName(playerName);
        HiddenPlayersSessionList.getInstance().addPlayerToHiddenSession(player);
        PlayersSessionList.getInstance().removePlayerFromSession(player);
        ArrayList<Player> playersList = PlayersSessionList.getInstance().getPlayersList();
        simpMessagingTemplate.convertAndSend("/topic/players", playersList);
    }

    private void showPlayer(String playerName) {
        Player player = HiddenPlayersSessionList.getInstance().getPlayerByName(playerName);
        PlayersSessionList.getInstance().addPlayerToSession(player);
        HiddenPlayersSessionList.getInstance().removePlayerFromHiddenSession(player);
        ArrayList<Player> playersList = PlayersSessionList.getInstance().getPlayersList();
        simpMessagingTemplate.convertAndSend("/topic/players", playersList);
    }

}
