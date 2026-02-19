package com.exam.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    private static String getDatabasePath() {
        // Try to get path from resources first (for development)
        String resourcePath = "src/main/resources/database/exam.db";
        File resourceFile = new File(resourcePath);
        
        if (resourceFile.exists()) {
            return "jdbc:sqlite:" + resourceFile.getAbsolutePath();
        }
        
        // Try to get from classpath (for JAR deployment)
        try {
            java.net.URL url = DBConnection.class.getResource("/database/exam.db");
            if (url != null) {
                String path = url.getPath();
                // Handle Windows paths
                if (path.startsWith("/") && System.getProperty("os.name").toLowerCase().contains("win")) {
                    path = path.substring(1);
                }
                return "jdbc:sqlite:" + path;
            }
        } catch (Exception e) {
            // Ignore and try fallback
        }
        
        // Fallback: use user directory
        String userDir = System.getProperty("user.dir");
        String fallbackPath = userDir + File.separator + "database" + File.separator + "exam.db";
        File fallbackFile = new File(fallbackPath);
        
        // Create directory if it doesn't exist
        File dbDir = fallbackFile.getParentFile();
        if (dbDir != null && !dbDir.exists()) {
            dbDir.mkdirs();
        }
        
        return "jdbc:sqlite:" + fallbackFile.getAbsolutePath();
    }

    private static final String URL = getDatabasePath();

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL);
            // Enable foreign keys
            conn.createStatement().execute("PRAGMA foreign_keys = ON");
            return conn;
        } catch (Exception e) {
            System.err.println("Database connection error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}