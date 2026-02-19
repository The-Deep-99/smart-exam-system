package com.exam.util;

import com.exam.model.Question;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileProcessor {

    public static List<Question> processCSVFile(File file, int subjectId) throws IOException {
        List<Question> questions = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                
                String[] values = line.split(",");
                if (values.length >= 6) {
                    Question question = new Question(
                        values[0].trim(), 
                        values[1].trim(), 
                        values[2].trim(), 
                        values[3].trim(), 
                        values[4].trim(), 
                        values[5].trim()
                    );
                    questions.add(question);
                }
            }
        }
        
        return questions;
    }

    public static List<Question> processExcelFile(File file, int subjectId) throws IOException {
        List<Question> questions = new ArrayList<>();
        
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            boolean isFirstRow = true;
            
            for (Row row : sheet) {
                if (isFirstRow) {
                    isFirstRow = false;
                    continue;
                }
                
                Cell questionCell = row.getCell(0);
                Cell optionACell = row.getCell(1);
                Cell optionBCell = row.getCell(2);
                Cell optionCCell = row.getCell(3);
                Cell optionDCell = row.getCell(4);
                Cell correctCell = row.getCell(5);
                
                if (questionCell != null && optionACell != null && optionBCell != null && 
                    optionCCell != null && optionDCell != null && correctCell != null) {
                    
                    Question question = new Question(
                        getCellValue(questionCell),
                        getCellValue(optionACell),
                        getCellValue(optionBCell),
                        getCellValue(optionCCell),
                        getCellValue(optionDCell),
                        getCellValue(correctCell)
                    );
                    questions.add(question);
                }
            }
        }
        
        return questions;
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    public static String extractTextFromPDF(File file) throws IOException {
        StringBuilder text = new StringBuilder();
        
        try (FileInputStream fis = new FileInputStream(file);
             PdfReader reader = new PdfReader(fis);
             PdfDocument pdfDoc = new PdfDocument(reader)) {
            
            for (int i = 1; i <= pdfDoc.getNumberOfPages(); i++) {
                text.append(PdfTextExtractor.getTextFromPage(pdfDoc.getPage(i)));
                text.append("\n");
            }
        }
        
        return text.toString();
    }

    public static List<Question> generateMCQsFromText(String text, int subjectId) {
        List<Question> questions = new ArrayList<>();
        
        try {
            OpenAIGenerator generator = new OpenAIGenerator();
            String mcqText = generator.generateMCQs(text);
            
            String[] lines = mcqText.split("\n");
            String currentQuestion = "";
            String optionA = "";
            String optionB = "";
            String optionC = "";
            String optionD = "";
            String correctOption = "";
            int questionCount = 0;
            
            for (String line : lines) {
                line = line.trim();
                
                if (line.startsWith("Q:") || line.startsWith("Question")) {
                    if (!currentQuestion.isEmpty() && questionCount < 10) {
                        questions.add(new Question(currentQuestion, optionA, optionB, optionC, optionD, correctOption));
                        questionCount++;
                    }
                    currentQuestion = line.startsWith("Q:") ? line.substring(2).trim() : line.trim();
                    optionA = optionB = optionC = optionD = correctOption = "";
                } else if (line.startsWith("A)") || line.startsWith("A.")) {
                    optionA = line.substring(2).trim();
                } else if (line.startsWith("B)") || line.startsWith("B.")) {
                    optionB = line.substring(2).trim();
                } else if (line.startsWith("C)") || line.startsWith("C.")) {
                    optionC = line.substring(2).trim();
                } else if (line.startsWith("D)") || line.startsWith("D.")) {
                    optionD = line.substring(2).trim();
                } else if (line.toLowerCase().startsWith("correct") || line.toLowerCase().startsWith("answer")) {
                    correctOption = line.split(":").length > 1 ? line.split(":")[1].trim() : "";
                }
            }
            
            if (!currentQuestion.isEmpty() && questionCount < 10) {
                questions.add(new Question(currentQuestion, optionA, optionB, optionC, optionD, correctOption));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return questions;
    }
}
