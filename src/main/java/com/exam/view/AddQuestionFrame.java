package com.exam.view;

import com.exam.dao.QuestionDAO;
import com.exam.dao.SubjectDAO;
import com.exam.model.Question;
import com.exam.model.Subject;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AddQuestionFrame extends JFrame {

    public AddQuestionFrame() {
        setTitle("Add Question");
        setSize(650, 550);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(9, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Subject dropdown
        List<Subject> subjects = SubjectDAO.getAllSubjects();
        Subject[] subjectArray = subjects.toArray(new Subject[0]);
        DefaultComboBoxModel<Subject> subjectModel = new DefaultComboBoxModel<>(subjectArray);
        JComboBox<Subject> subjectBox = new JComboBox<>(subjectModel);
        subjectBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Subject) {
                    value = ((Subject) value).getName();
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });

        JTextField qField = new JTextField();
        JTextField aField = new JTextField();
        JTextField bField = new JTextField();
        JTextField cField = new JTextField();
        JTextField dField = new JTextField();

        JComboBox<String> correctBox =
                new JComboBox<>(new String[]{"A", "B", "C", "D"});

        JButton addBtn = new JButton("Add Question");

        panel.add(new JLabel("Subject:"));
        panel.add(subjectBox);
        panel.add(new JLabel("Question:"));
        panel.add(qField);
        panel.add(new JLabel("Option A:"));
        panel.add(aField);
        panel.add(new JLabel("Option B:"));
        panel.add(bField);
        panel.add(new JLabel("Option C:"));
        panel.add(cField);
        panel.add(new JLabel("Option D:"));
        panel.add(dField);
        panel.add(new JLabel("Correct Option:"));
        panel.add(correctBox);
        panel.add(new JLabel());
        panel.add(addBtn);

        add(panel);

        addBtn.addActionListener(e -> {
            if (subjectBox.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Please select a subject!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Subject selectedSubject = (Subject) subjectBox.getSelectedItem();
            
            if (qField.getText().trim().isEmpty() || aField.getText().trim().isEmpty() || 
                bField.getText().trim().isEmpty() || cField.getText().trim().isEmpty() || 
                dField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Question q = new Question(
                    qField.getText(),
                    aField.getText(),
                    bField.getText(),
                    cField.getText(),
                    dField.getText(),
                    correctBox.getSelectedItem().toString()
            );

            QuestionDAO.addQuestion(q, selectedSubject.getId());
            JOptionPane.showMessageDialog(this, "Question Added to " + selectedSubject.getName() + "!");

            qField.setText("");
            aField.setText("");
            bField.setText("");
            cField.setText("");
            dField.setText("");
        });

        setVisible(true);
    }
}