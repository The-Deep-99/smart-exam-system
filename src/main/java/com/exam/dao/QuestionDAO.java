package com.exam.dao;

import com.exam.model.Question;
import com.exam.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAO {

    public static void addQuestion(Question q) {

        String sql = """
                INSERT INTO questions
                (question, optionA, optionB, optionC, optionD, correctOption)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, q.getQuestion());
            ps.setString(2, q.getOptionA());
            ps.setString(3, q.getOptionB());
            ps.setString(4, q.getOptionC());
            ps.setString(5, q.getOptionD());
            ps.setString(6, q.getCorrectOption());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addQuestion(Question q, int subjectId) {

        String sql = """
                INSERT INTO questions
                (subject_id, question, optionA, optionB, optionC, optionD, correctOption)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, subjectId);
            ps.setString(2, q.getQuestion());
            ps.setString(3, q.getOptionA());
            ps.setString(4, q.getOptionB());
            ps.setString(5, q.getOptionC());
            ps.setString(6, q.getOptionD());
            ps.setString(7, q.getCorrectOption());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Question> getAllQuestions() {

        List<Question> list = new ArrayList<>();
        String sql = """
            SELECT q.id, q.question, q.optionA, q.optionB, q.optionC, q.optionD, q.correctOption,
                   q.subject_id, s.name as subject_name
            FROM questions q
            LEFT JOIN subjects s ON q.subject_id = s.id
            ORDER BY s.name, q.id
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Question(
                        rs.getInt("id"),
                        rs.getString("question"),
                        rs.getString("optionA"),
                        rs.getString("optionB"),
                        rs.getString("optionC"),
                        rs.getString("optionD"),
                        rs.getString("correctOption"),
                        rs.getInt("subject_id"),
                        rs.getString("subject_name") != null ? rs.getString("subject_name") : "Unknown"
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static List<Question> getQuestionsBySubject(int subjectId) {

        List<Question> list = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE subject_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, subjectId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Question(
                        rs.getInt("id"),
                        rs.getString("question"),
                        rs.getString("optionA"),
                        rs.getString("optionB"),
                        rs.getString("optionC"),
                        rs.getString("optionD"),
                        rs.getString("correctOption")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}