package com.example.seabattle.game.services;


import com.example.seabattle.game.model.Player;
import com.example.seabattle.game.model.PlayersSessionList;
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
            PlayersSessionList.getInstance().removePlayerFromSession(player);
            ArrayList<Player> playersList = PlayersSessionList.getInstance().getPlayersList();
            messagingTemplate.convertAndSend("/topic/players", playersList);
        }
    }
}

