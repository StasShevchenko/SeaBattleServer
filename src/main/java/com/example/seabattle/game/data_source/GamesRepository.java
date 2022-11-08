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

    public ArrayList<GameInfo> addGame(String winnerId, String loserId) {
        Player winnerPlayer = HiddenPlayersSessionList.getInstance().getPlayerById(winnerId);
        Player loserPlayer = HiddenPlayersSessionList.getInstance().getPlayerById(loserId);
        GameInfo gameInfo = new GameInfo();
        gameInfo.setWinner(winnerPlayer.getLogin());
        gameInfo.setLoser(loserPlayer.getLogin());
        String date = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
        gameInfo.setDate(date);
        gamesDb.insertGameInfo(gameInfo);
        return gamesDb.getAllGames();
    }

    public ArrayList<GameInfo> getGames(){
        return gamesDb.getAllGames();
    }
}
