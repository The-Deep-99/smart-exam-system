package com.exam.util;

import java.sql.Connection;
import java.sql.Statement;

public class DBInit {

    public static void initialize() {

        String userTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE,
                    password TEXT,
                    role TEXT,
                    profile_picture TEXT
                );
                """;

        String subjectTable = """
                CREATE TABLE IF NOT EXISTS subjects (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT UNIQUE,
                    description TEXT
                );
                """;

        String questionTable = """
                CREATE TABLE IF NOT EXISTS questions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    subject_id INTEGER,
                    question TEXT,
                    optionA TEXT,
                    optionB TEXT,
                    optionC TEXT,
                    optionD TEXT,
                    correctOption TEXT,
                    FOREIGN KEY (subject_id) REFERENCES subjects(id)
                );
                """;

        String resultTable = """
                CREATE TABLE IF NOT EXISTS results (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT,
                    subject_id INTEGER,
                    score INTEGER,
                    total_questions INTEGER,
                    date_taken DATETIME,
                    FOREIGN KEY (subject_id) REFERENCES subjects(id)
                );
                """;

        String examTable = """
                CREATE TABLE IF NOT EXISTS exams (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    exam_code TEXT UNIQUE,
                    title TEXT,
                    subject_id INTEGER,
                    duration_minutes INTEGER,
                    question_count INTEGER,
                    created_by TEXT,
                    created_at DATETIME,
                    is_active BOOLEAN DEFAULT 1,
                    FOREIGN KEY (subject_id) REFERENCES subjects(id)
                );
                """;

        String examSessionTable = """
                CREATE TABLE IF NOT EXISTS exam_sessions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    exam_code TEXT,
                    student_username TEXT,
                    start_time DATETIME,
                    end_time DATETIME,
                    score INTEGER,
                    total_questions INTEGER,
                    status TEXT DEFAULT 'ONGOING',
                    FOREIGN KEY (exam_code) REFERENCES exams(exam_code)
                );
                """;

        String insertAdmin = """
                INSERT OR IGNORE INTO users (username, password, role)
                VALUES ('admin', 'admin123', 'ADMIN');
                """;

        String insertStudent = """
                INSERT OR IGNORE INTO users (username, password, role)
                VALUES ('student', 'student123', 'STUDENT');
                """;

        String insertSubjects = """
                INSERT OR IGNORE INTO subjects (name, description) VALUES
                ('Applied Mathematics 3', 'Advanced Mathematical Concepts and Applications'),
                ('Java', 'Java Programming Language and Object-Oriented Concepts'),
                ('Operating System', 'OS Concepts, Process Management, and Memory Management'),
                ('Database', 'Database Management Systems, SQL, and Data Modeling');
                """;

        // Sample questions for Applied Mathematics 3 (subject_id = 1)
        String mathQuestions = """
                INSERT OR IGNORE INTO questions (subject_id, question, optionA, optionB, optionC, optionD, correctOption) VALUES
                (1, 'What is the derivative of x^3?', '3x^2', 'x^2', '3x', 'x^3', 'A'),
                (1, 'What is the integral of 2x?', 'x^2', 'x^2 + C', '2x^2', 'x', 'B'),
                (1, 'What is the limit of (x^2 - 4)/(x - 2) as x approaches 2?', '0', '2', '4', 'Undefined', 'C'),
                (1, 'What is the value of sin(90°)?', '0', '0.5', '1', '√2/2', 'C'),
                (1, 'What is the determinant of a 2x2 matrix [[a,b],[c,d]]?', 'ad + bc', 'ad - bc', 'ac - bd', 'ab - cd', 'B');
                """;

        // Sample questions for Java (subject_id = 2)
        String javaQuestions = """
                INSERT OR IGNORE INTO questions (subject_id, question, optionA, optionB, optionC, optionD, correctOption) VALUES
                (2, 'Which keyword is used to inherit a class in Java?', 'extends', 'implements', 'inherits', 'super', 'A'),
                (2, 'What is the default value of an int variable in Java?', 'null', '0', '1', 'undefined', 'B'),
                (2, 'Which method is used to start a thread in Java?', 'run()', 'start()', 'execute()', 'begin()', 'B'),
                (2, 'What is the size of int in Java?', '16 bits', '32 bits', '64 bits', 'Depends on platform', 'B'),
                (2, 'Which collection class is synchronized in Java?', 'ArrayList', 'Vector', 'LinkedList', 'HashSet', 'B');
                """;

        // Sample questions for Operating System (subject_id = 3)
        String osQuestions = """
                INSERT OR IGNORE INTO questions (subject_id, question, optionA, optionB, optionC, optionD, correctOption) VALUES
                (3, 'What is the main purpose of an operating system?', 'To manage hardware resources', 'To compile programs', 'To design websites', 'To create databases', 'A'),
                (3, 'Which scheduling algorithm can cause starvation?', 'FCFS', 'Round Robin', 'Shortest Job First', 'Priority Scheduling', 'D'),
                (3, 'What is a deadlock?', 'A process that runs forever', 'A situation where processes are waiting for each other', 'A memory error', 'A CPU overload', 'B'),
                (3, 'What is the purpose of virtual memory?', 'To increase RAM speed', 'To allow programs to use more memory than physically available', 'To store files', 'To manage CPU', 'B'),
                (3, 'Which is not a type of process scheduling?', 'Preemptive', 'Non-preemptive', 'Cooperative', 'Destructive', 'D');
                """;

        // Sample questions for Database (subject_id = 4)
        String dbQuestions = """
                INSERT OR IGNORE INTO questions (subject_id, question, optionA, optionB, optionC, optionD, correctOption) VALUES
                (4, 'What does ACID stand for in database transactions?', 'Atomicity, Consistency, Isolation, Durability', 'Accuracy, Consistency, Integrity, Durability', 'Atomicity, Correctness, Isolation, Durability', 'All, Consistency, Integrity, Data', 'A'),
                (4, 'What is a primary key?', 'A foreign key', 'A unique identifier for a row', 'A column that can be null', 'An index', 'B'),
                (4, 'Which SQL command is used to modify data?', 'UPDATE', 'MODIFY', 'CHANGE', 'ALTER', 'A'),
                (4, 'What is normalization?', 'Increasing database size', 'Organizing data to reduce redundancy', 'Deleting data', 'Backing up data', 'B'),
                (4, 'What is the purpose of an index in a database?', 'To store data', 'To speed up data retrieval', 'To delete data', 'To encrypt data', 'B');
                """;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(userTable);
            // Add profile_picture column if it doesn't exist (for existing databases)
            try {
                stmt.execute("ALTER TABLE users ADD COLUMN profile_picture TEXT");
            } catch (Exception e) {
                // Column might already exist, ignore error
            }
            stmt.execute(subjectTable);
            stmt.execute(questionTable);
            stmt.execute(resultTable);
            stmt.execute(examTable);
            stmt.execute(examSessionTable);
            stmt.execute(insertAdmin);
            stmt.execute(insertStudent);
            
            // Only insert subjects and questions if they don't already exist
            stmt.execute(insertSubjects);
            
            // Get subject IDs dynamically
            java.sql.ResultSet subjectRs = stmt.executeQuery("SELECT id, name FROM subjects ORDER BY id");
            int mathId = 0, javaId = 0, osId = 0, dbId = 0;
            
            while (subjectRs.next()) {
                String name = subjectRs.getString("name");
                int id = subjectRs.getInt("id");
                if (name.equals("Applied Mathematics 3")) mathId = id;
                else if (name.equals("Java")) javaId = id;
                else if (name.equals("Operating System")) osId = id;
                else if (name.equals("Database")) dbId = id;
            }
            subjectRs.close();
            
            // Insert questions with correct subject IDs (only if they don't exist)
            if (mathId > 0) {
                String mathQ = mathQuestions.replaceAll("\\(1,", "(" + mathId + ",");
                stmt.execute(mathQ);
            }
            if (javaId > 0) {
                String javaQ = javaQuestions.replaceAll("\\(2,", "(" + javaId + ",");
                stmt.execute(javaQ);
            }
            if (osId > 0) {
                String osQ = osQuestions.replaceAll("\\(3,", "(" + osId + ",");
                stmt.execute(osQ);
            }
            if (dbId > 0) {
                String dbQ = dbQuestions.replaceAll("\\(4,", "(" + dbId + ",");
                stmt.execute(dbQ);
            }

            System.out.println("Database initialized successfully.");
            System.out.println("Sample data loaded (if not already present).");
            System.out.println("Subject IDs - Applied Math: " + mathId + ", Java: " + javaId + 
                           ", OS: " + osId + ", Database: " + dbId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}