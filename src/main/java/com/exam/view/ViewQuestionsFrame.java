package com.exam.view;

import com.exam.dao.QuestionDAO;
import com.exam.model.Question;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class ViewQuestionsFrame extends JFrame {

    public ViewQuestionsFrame() {
        setTitle("All Questions");
        setSize(800, 400);
        setLocationRelativeTo(null);

        String[] columns = {
                "ID", "Question", "A", "B", "C", "D", "Correct"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        List<Question> questions = QuestionDAO.getAllQuestions();
        for (Question q : questions) {
            model.addRow(new Object[]{
                    q.getId(),
                    q.getQuestion(),
                    q.getOptionA(),
                    q.getOptionB(),
                    q.getOptionC(),
                    q.getOptionD(),
                    q.getCorrectOption()
            });
        }

        add(new JScrollPane(table));
        setVisible(true);
    }
}