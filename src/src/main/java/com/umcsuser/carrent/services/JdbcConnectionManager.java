package com.umcsuser.carrent.services;

import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class JdbcConnectionManager {
    private static JdbcConnectionManager instance;
    private final String url;
    private JdbcConnectionManager() {
        url = System.getenv("DB_URL");
        if (url == null) {
            throw new RuntimeException("DB_URL not set");
        }
    }

    public static JdbcConnectionManager getInstance() {
        if (instance == null) {
            instance = new JdbcConnectionManager();
        }
        return instance;
    }

    public Connection getConnection() {
        try{
            return DriverManager.getConnection(url);
        }catch (SQLException e) {
            throw new RuntimeException("Connection Failed!", e);
        }
    }
}
