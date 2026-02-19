package com.exam.config;

import com.exam.util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConfig {

    public static Connection getConnection() throws SQLException {
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            throw new SQLException("Failed to establish database connection");
        }
        return conn;
    }
}