package com.example.seabattle.game.data_source;

import com.example.seabattle.game.model.Game;
import com.example.seabattle.game.model.GameInfo;
import com.example.seabattle.game.model.HiddenPlayersSessionList;
import com.example.seabattle.game.model.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GamesRepository {
    GamesDb gamesDb;

    public GamesRepository(GamesDb gamesDb) {
        this.gamesDb = gamesDb;
    }

    public ArrayList<GameInfo> addGame(String winnerName, String loserName) {
        Player winnerPlayer = HiddenPlayersSessionList.getInstance().getPlayerByName(winnerName);
        Player loserPlayer = HiddenPlayersSessionList.getInstance().getPlayerByName(loserName);
        GameInfo gameInfo = new GameInfo();
        gameInfo.setWinner(winnerPlayer.getLogin());
        gameInfo.setLoser(loserPlayer.getLogin());
        String date = new SimpleDateFormat("dd.MM.yyyy.HH:mm").format(new Date());
        gameInfo.setDate(date);
        gamesDb.insertGameInfo(gameInfo);
        return gamesDb.getAllGames();
    }

    public ArrayList<GameInfo> getGames(){
        return gamesDb.getAllGames();
    }
}
