package com.example.seabattle.game.model;

import java.util.ArrayList;
import java.util.Objects;

public class PlayersSessionList {
    private ArrayList<Player> playersList = new ArrayList<>();
    private static PlayersSessionList instance = null;

    private PlayersSessionList(){}

    public static PlayersSessionList getInstance(){
        if(instance == null){
            instance = new PlayersSessionList();
        }
        return instance;
    }

    public Player getPlayerByName(String name) {
        for (Player player : playersList) {
            if (Objects.equals(player.getLogin(), name)) {
                return player;
            }
        }
        return null;
    }

    public void addPlayerToSession(Player player) {
        playersList.add(player);
    }

    public void removePlayerFromSession(Player player) {
        playersList.remove(player);
    }

    public ArrayList<Player> getPlayersList(){
        return playersList;
    }
}
