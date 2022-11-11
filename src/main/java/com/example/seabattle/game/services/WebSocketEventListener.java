package com.example.seabattle.game.services;


import com.example.seabattle.game.controllers.PlayerSessionAttributesInteractor;
import com.example.seabattle.game.data_source.GamesDb;
import com.example.seabattle.game.data_source.GamesRepository;
import com.example.seabattle.game.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.ArrayList;
import java.util.Objects;

@Component
public class WebSocketEventListener {

    private static final Logger logger =
            LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
    }

    @EventListener
    public synchronized void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        PlayerSessionAttributesInteractor sessionInteractor = new PlayerSessionAttributesInteractor(headerAccessor);
        Player player = sessionInteractor.getPlayerFromDisconnectedSession();
        System.out.println("ОТКЛЮЧИЛСЯ ИГРОК: " + player);
        if (player.getLogin() != null) {
            logger.info("ЛОГИН ОТКЛЮЧИВШЕГОСЯ : " + player.getLogin());
            if (Objects.equals(player.getStatus(), "playing")) {
                //Отправляем сообщение игроку о технической победе
                Game game = GameSessionList.getInstance().getGameByPlayerName(player.getLogin());
                //null будет в случае удаления игры через LOSE в PlayersController
                if (game != null) {
                    System.out.println("ОТКЛЮЧИВШИЙСЯ БЫЛ В ИГРЕ " + game);
                    String receiverName;
                    String winnerName;
                    String loserName;
                    if (Objects.equals(game.getSecondPlayerName(), player.getLogin())) {
                        receiverName = game.getFirstPlayerName();
                        winnerName = receiverName;
                        loserName = game.getSecondPlayerName();
                    } else {
                        receiverName = game.getSecondPlayerName();
                        winnerName = receiverName;
                        loserName = game.getFirstPlayerName();
                    }
                    GamesDb gamesDb = new GamesDb();
                    GamesRepository gamesRepository = new GamesRepository(gamesDb);
                    ArrayList<GameInfo> gamesList = gamesRepository.addGame(winnerName, loserName);
                    GameSessionList.getInstance().removeGameFromSession(game);
                    messagingTemplate.convertAndSend("/topic/games", gamesList);
                    PrivateMessage message = new PrivateMessage();
                    message.setMessage("ENEMY_DISCONNECTED");
                    message.setReceiverName(receiverName);
                    Player winPlayer = HiddenPlayersSessionList.getInstance().getPlayerByName(receiverName);
                    Player losePlayer = HiddenPlayersSessionList.getInstance().getPlayerByName(loserName);
                    HiddenPlayersSessionList.getInstance().removePlayerFromHiddenSession(winPlayer);
                    HiddenPlayersSessionList.getInstance().removePlayerFromHiddenSession(losePlayer);
                    PlayersSessionList.getInstance().addPlayerToSession(winPlayer);
                    messagingTemplate.convertAndSend("/private/messages" + message.getReceiverName(), message);
                }
            } else if (Objects.equals(player.getStatus(), "waiting")) {
                System.out.println("ОТКЛЮЧИЛСЯ ОЖИДАЮЩИЙ ИГРОК");
                PlayersSessionList.getInstance().removePlayerFromSession(player);
                //Удаляем игрока из скрытых, если он не играет
                Game currentGame = GameSessionList.getInstance().getGameByPlayerName(player.getLogin());
                if (currentGame == null) {
                    HiddenPlayersSessionList.getInstance().removePlayerFromHiddenSession(player);
                }
            }
        }
    }
}

