package com.exam.dao;

import com.exam.model.ExamSession;
import com.exam.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ExamSessionDAO {

    public static void createExamSession(ExamSession session) {
        String sql = """
                INSERT INTO exam_sessions
                (exam_code, student_username, start_time, total_questions, status)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, session.getExamCode());
            ps.setString(2, session.getStudentUsername());
            ps.setObject(3, session.getStartTime());
            ps.setInt(4, session.getTotalQuestions());
            ps.setString(5, session.getStatus());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateExamSession(ExamSession session) {
        String sql = """
                UPDATE exam_sessions
                SET end_time = ?, score = ?, status = ?
                WHERE id = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, session.getEndTime());
            ps.setInt(2, session.getScore());
            ps.setString(3, session.getStatus());
            ps.setInt(4, session.getId());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ExamSession getActiveSession(String examCode, String studentUsername) {
        String sql = """
                SELECT * FROM exam_sessions
                WHERE exam_code = ? AND student_username = ? AND status = 'ONGOING'
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, examCode);
            ps.setString(2, studentUsername);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new ExamSession(
                    rs.getInt("id"),
                    rs.getString("exam_code"),
                    rs.getString("student_username"),
                    rs.getObject("start_time", LocalDateTime.class),
                    rs.getObject("end_time", LocalDateTime.class),
                    rs.getInt("score"),
                    rs.getInt("total_questions"),
                    rs.getString("status")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<ExamSession> getExamSessions(String examCode) {
        List<ExamSession> sessions = new ArrayList<>();
        String sql = """
                SELECT * FROM exam_sessions
                WHERE exam_code = ?
                ORDER BY start_time DESC
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, examCode);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                sessions.add(new ExamSession(
                    rs.getInt("id"),
                    rs.getString("exam_code"),
                    rs.getString("student_username"),
                    rs.getObject("start_time", LocalDateTime.class),
                    rs.getObject("end_time", LocalDateTime.class),
                    rs.getInt("score"),
                    rs.getInt("total_questions"),
                    rs.getString("status")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sessions;
    }

    public static boolean hasActiveSession(String examCode, String studentUsername) {
        String sql = """
                SELECT COUNT(*) FROM exam_sessions
                WHERE exam_code = ? AND student_username = ? AND status = 'ONGOING'
                """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, examCode);
            ps.setString(2, studentUsername);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return false;
    }
}
