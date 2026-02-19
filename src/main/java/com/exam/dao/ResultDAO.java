package com.exam.dao;

import com.exam.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultDAO {

    public static void saveResult(String username, int subjectId, int score, int totalQuestions) {
        String sql = """
                INSERT INTO results (username, subject_id, score, total_questions, date_taken)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setInt(2, subjectId);
            ps.setInt(3, score);
            ps.setInt(4, totalQuestions);
            ps.setString(5, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Map<String, Object>> getStudentResults(String username) {
        List<Map<String, Object>> results = new ArrayList<>();
        String sql = """
                SELECT r.id, s.name, r.score, r.total_questions, r.date_taken,
                       ROUND((r.score * 100.0 / r.total_questions), 2) as percentage
                FROM results r
                JOIN subjects s ON r.subject_id = s.id
                WHERE r.username = ?
                ORDER BY r.date_taken DESC
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> result = new HashMap<>();
                result.put("id", rs.getInt("id"));
                result.put("subject", rs.getString("name"));
                result.put("score", rs.getInt("score"));
                result.put("total", rs.getInt("total_questions"));
                result.put("percentage", rs.getDouble("percentage"));
                result.put("date", rs.getString("date_taken"));
                results.add(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return results;
    }

    public static Map<String, Object> getSubjectStats(String username, int subjectId) {
        Map<String, Object> stats = new HashMap<>();
        String sql = """
                SELECT COUNT(*) as attempts,
                       AVG(score) as avg_score,
                       MAX(score) as best_score,
                       AVG(total_questions) as total_questions
                FROM results
                WHERE username = ? AND subject_id = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setInt(2, subjectId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                stats.put("attempts", rs.getInt("attempts"));
                stats.put("avgScore", Math.round(rs.getDouble("avg_score") * 100.0) / 100.0);
                stats.put("bestScore", rs.getInt("best_score"));
                stats.put("totalQuestions", rs.getInt("total_questions"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stats;
    }
}
