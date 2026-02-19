package com.exam.dao;

import com.exam.model.User;
import com.exam.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

    public static User authenticate(String username, String password) {

        String sql = "SELECT role, profile_picture FROM users WHERE username=? AND password=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new User(username, rs.getString("role"), rs.getString("profile_picture"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
    /**
     * Get user by username only (for OAuth authentication)
     */
    public static User getUserByUsername(String username) {
        String sql = "SELECT role, profile_picture FROM users WHERE username=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new User(username, rs.getString("role"), rs.getString("profile_picture"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
    /**
     * Create a new user account (for auto-registration via Google OAuth or sign up)
     */
    public static boolean createUser(String username, String role) {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, ""); // Empty password initially, will be updated
            ps.setString(3, role);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update user password
     */
    public static boolean updatePassword(String username, String password) {
        String sql = "UPDATE users SET password = ? WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, password);
            ps.setString(2, username);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update user profile picture path
     */
    public static boolean updateProfilePicture(String username, String profilePicturePath) {
        String sql = "UPDATE users SET profile_picture = ? WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, profilePicturePath);
            ps.setString(2, username);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}