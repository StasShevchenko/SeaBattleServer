package com.example.seabattle.game.model;

import java.util.ArrayList;
import java.util.Objects;

public class GameSessionList {

    private ArrayList<Game> gameList = new ArrayList<>();
    private static GameSessionList instance = null;

    private GameSessionList(){}

    public static GameSessionList getInstance(){
        if(instance == null){
            instance = new GameSessionList();
        }
        return instance;
    }

    public void addGameToSession(Game game) {
        gameList.add(game);
    }

    public Game getGameByPlayerId(String playerId) {
        for (Game game : gameList) {
            if (Objects.equals(playerId, game.getFirstPlayerId()) || Objects.equals(playerId, game.getSecondPlayerId())) {
                return game;
            }
        }
        return null;
    }

    public void removeGameFromSession(Game game) {
        gameList.remove(game);
    }

    public ArrayList<Game> getGameList(){
        return gameList;
    }

}
