package com.example.seabattle.game.services;


import com.example.seabattle.game.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
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
        logger.info("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String playerName = (String) headerAccessor.getSessionAttributes().get("playername");
        String playerId = (String) headerAccessor.getSessionAttributes().get("playerid");
        if(playerName != null) {
            logger.info("User Disconnected : " + playerName);
            Player player = new Player();
            player.setLogin(playerName);
            player.setId(playerId);
            HiddenPlayersSessionList.getInstance().removePlayerFromHiddenSession(player);
            PlayersSessionList.getInstance().removePlayerFromSession(player);
            //Отправляем сообщение игроку о технической победе
            Game game = GameSessionList.getInstance().getGameByPlayerId(playerId);
            if (game != null) {
                String receiverId;
                if (Objects.equals(game.getSecondPlayerId(), playerId)) {
                    receiverId = game.getFirstPlayerId();
                } else {
                    receiverId = game.getSecondPlayerId();
                }
                PrivateMessage message = new PrivateMessage();
                message.setMessage("ENEMY_DISCONNECTED");
                message.setReceiverId(receiverId);
                Player winPlayer = HiddenPlayersSessionList.getInstance().getPlayerById(receiverId);
                System.out.println("Победитель: " + winPlayer);
                HiddenPlayersSessionList.getInstance().removePlayerFromHiddenSession(winPlayer);
                System.out.println("Игрок добавлен через отключение");
                PlayersSessionList.getInstance().addPlayerToSession(winPlayer);
                System.out.println("Отправляю сообщение!");
                System.out.println(receiverId);
                messagingTemplate.convertAndSend("/private/messages"+message.getReceiverId(), message);
                GameSessionList.getInstance().removeGameFromSession(game);
            }
            ArrayList<Player> playersList = PlayersSessionList.getInstance().getPlayersList();
            System.out.println(playersList);

            messagingTemplate.convertAndSend("/topic/players", playersList);
        }
    }
}

