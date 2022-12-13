package com.example.seabattle.game.controllers;

import com.example.seabattle.game.model.GameInfo;
import com.example.seabattle.game.model.PlayerStatistic;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Класс, необходимый для
 * генирации статистики и отправки её
 * на клиент
 */
@RestController
public class StatisticController {
    private final String DB_URL = "jdbc:postgresql://127.0.0.01:5432/seabattle";
    private final String USER = "postgres";
    private final String PASS = "MyNameIsEminem";

    /**
     * Метод получения статистики
     * конкретного игрока
     * @param name
     * @return
     */
    @GetMapping("/playerstatistic")
    @CrossOrigin(origins = "*")
    @ResponseBody
    public ArrayList<GameInfo> getPlayerStatistic(@RequestParam(name = "name") String name) {
        ArrayList<GameInfo> gameInfoList = new ArrayList<>();
        try{
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT winner, loser, date FROM games " +
                    "WHERE winner = '"+name+"' OR loser = '"+name+"' ORDER BY date DESC");
            while (resultSet.next()) {
                GameInfo gameInfo = new GameInfo();
                gameInfo.setWinner(resultSet.getString("winner"));
                gameInfo.setLoser(resultSet.getString("loser"));
                gameInfo.setDate(resultSet.getString("date"));
                gameInfoList.add(gameInfo);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return gameInfoList;
    }

    /**
     * Метод для получения статистики
     * лучших игроков за всё время
     * @return
     */
    @GetMapping("/overallstatistic")
    @CrossOrigin(origins = "*")
    @ResponseBody
    public  List<PlayerStatistic> getOverallStatistic(){
        ArrayList<PlayerStatistic> overallStatisticList = new ArrayList<>();
        ArrayList<String> players = new ArrayList<>();
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement statement = connection.createStatement();
            ResultSet nameSet = statement.executeQuery("SELECT login FROM users");
            while (nameSet.next()) {
               players.add(nameSet.getString("login"));
            }
            for (String name : players) {
                PlayerStatistic playerStatistic = new PlayerStatistic();
                Statement winStatement = connection.createStatement();
                ResultSet winCountSet = winStatement.executeQuery("SELECT COUNT(*) as winCount FROM games WHERE winner = '"+name+"'");
                Statement loseStatement = connection.createStatement();
                ResultSet loseCountSet = loseStatement.executeQuery("SELECT COUNT(*) as loseCount FROM games WHERE loser = '"+name+"'");
                winCountSet.next();
                loseCountSet.next();
                playerStatistic.setPlayerName(name);
                playerStatistic.setWinCount(winCountSet.getString("winCount"));
                playerStatistic.setLoseCount(loseCountSet.getString("loseCount"));
                Double winPercentage = Double.parseDouble(winCountSet.getString("winCount")) / (Double.parseDouble(winCountSet.getString("winCount")) + Double.parseDouble(loseCountSet.getString("loseCount"))) * 100;
                DecimalFormat numberFormat = new DecimalFormat("##.##");
                String winPercentageString = numberFormat.format(winPercentage);
                playerStatistic.setWinPercentage(winPercentageString);
                if(!(Integer.parseInt(playerStatistic.getWinCount()) == 0 && Integer.parseInt(playerStatistic.getLoseCount()) == 0))
                overallStatisticList.add(playerStatistic);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        List<PlayerStatistic> sortedList =  overallStatisticList.stream().sorted(((o1, o2) -> -(o1.getWinPercentage().compareTo(o2.getWinPercentage())))).toList();
        return sortedList;
    }
}
