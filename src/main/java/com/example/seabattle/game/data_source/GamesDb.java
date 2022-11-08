package com.example.seabattle.game.data_source;

import com.example.seabattle.game.model.GameInfo;

import java.sql.*;
import java.util.ArrayList;

public class GamesDb {
    private final String DB_URL = "jdbc:postgresql://127.0.0.01:5432/seabattle";
    private final String USER = "postgres";
    private final String PASS = "MyNameIsEminem";

    public void insertGameInfo(GameInfo gameInfo) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO games(winner, loser, date) VALUES " +
                    "('"+ gameInfo.getWinner() +"', '"+gameInfo.getLoser()+"', '"+gameInfo.getDate()+"')");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<GameInfo> getAllGames(){
        ArrayList<GameInfo> gameInfoList = new ArrayList<>();
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT winner, loser, date FROM games");
            while (resultSet.next()) {
                GameInfo gameInfo = new GameInfo();
                gameInfo.setWinner(resultSet.getString("winner"));
                gameInfo.setLoser(resultSet.getString("loser"));
                gameInfo.setDate(resultSet.getString("date"));
                gameInfoList.add(gameInfo);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gameInfoList;
    }
}
