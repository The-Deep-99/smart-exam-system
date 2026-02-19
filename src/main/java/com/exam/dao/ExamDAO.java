package com.exam.dao;

import com.exam.model.Exam;
import com.exam.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ExamDAO {

    public static String generateExamCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }

    public static void createExam(Exam exam) {
        String sql = """
                INSERT INTO exams
                (exam_code, title, subject_id, duration_minutes, question_count, created_by, created_at, is_active)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, exam.getExamCode());
            ps.setString(2, exam.getTitle());
            ps.setInt(3, exam.getSubjectId());
            ps.setInt(4, exam.getDurationMinutes());
            ps.setInt(5, exam.getQuestionCount());
            ps.setString(6, exam.getCreatedBy());
            ps.setObject(7, exam.getCreatedAt());
            ps.setBoolean(8, exam.isActive());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Exam getExamByCode(String examCode) {
        String sql = """
                SELECT e.*, s.name as subject_name
                FROM exams e
                LEFT JOIN subjects s ON e.subject_id = s.id
                WHERE e.exam_code = ? AND e.is_active = 1
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, examCode);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Exam(
                    rs.getInt("id"),
                    rs.getString("exam_code"),
                    rs.getString("title"),
                    rs.getInt("subject_id"),
                    rs.getString("subject_name"),
                    rs.getInt("duration_minutes"),
                    rs.getInt("question_count"),
                    rs.getString("created_by"),
                    rs.getObject("created_at", LocalDateTime.class),
                    rs.getBoolean("is_active")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<Exam> getAllExams() {
        List<Exam> exams = new ArrayList<>();
        String sql = """
                SELECT e.*, s.name as subject_name
                FROM exams e
                LEFT JOIN subjects s ON e.subject_id = s.id
                ORDER BY e.created_at DESC
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                exams.add(new Exam(
                    rs.getInt("id"),
                    rs.getString("exam_code"),
                    rs.getString("title"),
                    rs.getInt("subject_id"),
                    rs.getString("subject_name"),
                    rs.getInt("duration_minutes"),
                    rs.getInt("question_count"),
                    rs.getString("created_by"),
                    rs.getObject("created_at", LocalDateTime.class),
                    rs.getBoolean("is_active")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return exams;
    }

    public static boolean isExamCodeUnique(String examCode) {
        String sql = "SELECT COUNT(*) FROM exams WHERE exam_code = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, examCode);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return false;
    }
}
