package com.example.seabattle.game.controllers;


import com.example.seabattle.game.model.UserCredentials;
import org.springframework.web.bind.annotation.*;

import java.sql.*;

@RestController
public class AuthController {

    private final String DB_URL = "jdbc:postgresql://127.0.0.01:5432/seabattle";
    private final String USER = "postgres";
    private final String PASS = "MyNameIsEminem";

    @PostMapping("/login")
    @CrossOrigin(origins = "*")
    public int logIn(@RequestBody UserCredentials userCredentials) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM users WHERE login = '" + userCredentials.getLogin() + "' " +
                    "and password = '" + userCredentials.getPassword() + "'");
            System.out.println("Игрок логинится");
            if (resultSet.next()) return 1;

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @PostMapping("/register")
    @CrossOrigin(origins = "*")
    public int register(@RequestBody UserCredentials userCredentials) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM users WHERE login = '"+userCredentials.getLogin()+"'");
            if(resultSet.next()){
                return 0;
            }
            else {
                statement.executeUpdate("INSERT INTO users(login, password) VALUES('"+userCredentials.getLogin()+"', '"+userCredentials.getPassword()+"')");
                return 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
