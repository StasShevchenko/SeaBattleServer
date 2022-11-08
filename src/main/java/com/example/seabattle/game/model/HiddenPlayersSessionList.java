package com.example.seabattle.game.model;

import java.util.ArrayList;
import java.util.Objects;

public class HiddenPlayersSessionList {
    private ArrayList<Player> playersList = new ArrayList<>();
    private static HiddenPlayersSessionList instance = null;

    private HiddenPlayersSessionList(){}

    public static HiddenPlayersSessionList getInstance(){
        if(instance == null){
            instance = new HiddenPlayersSessionList();
        }
        return instance;
    }

    public Player getPlayerById(String id) {
        for (Player player : playersList) {
            if (Objects.equals(player.getId(), id)) {
                return player;
            }
        }
        return null;
    }

    public void addPlayerToHiddenSession(Player player) {
        playersList.add(player);
    }

    public void removePlayerFromHiddenSession(Player player) {
        playersList.remove(player);
    }

}
