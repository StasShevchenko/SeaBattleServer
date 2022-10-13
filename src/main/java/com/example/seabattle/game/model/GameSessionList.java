package com.example.seabattle.game.model;

import java.util.ArrayList;

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

    public void removeGameFromSession(Game game) {
        gameList.remove(game);
    }

    public ArrayList<Game> getGameList(){
        return gameList;
    }

}
