package com.exam.util;

import java.util.Random;

public class OpenAIGenerator {
    private static final String[] QUESTION_TEMPLATES = {
        "What is the main concept discussed in the following statement?",
        "Which of the following best describes the key idea?",
        "What can be inferred from the information provided?",
        "Which option accurately represents the main point?",
        "What is the primary focus of this statement?"
    };
    
    private static final String[] OPTIONS = {
        "True", "False", "Partially correct", "Not applicable",
        "Option A", "Option B", "Option C", "Option D",
        "Correct", "Incorrect", "Maybe", "Never"
    };
    
    public OpenAIGenerator() {
        // No external API key needed - using local generation
    }
    
    public String generateMCQs(String text) {
        StringBuilder mcqText = new StringBuilder();
        Random random = new Random();
        
        String[] sentences = text.split("\\.");
        int questionCount = Math.min(10, sentences.length / 5); // Generate reasonable number of questions
        
        for (int i = 0; i < questionCount; i++) {
            int sentenceIndex = random.nextInt(sentences.length);
            String sentence = sentences[sentenceIndex].trim();
            
            if (sentence.length() > 20) {
                String template = QUESTION_TEMPLATES[random.nextInt(QUESTION_TEMPLATES.length)];
                mcqText.append("Q: ").append(template).append("\n").append(sentence).append("\n");
                
                // Generate 4 options
                String correctAnswer = OPTIONS[random.nextInt(4)];
                mcqText.append("A) ").append(OPTIONS[random.nextInt(OPTIONS.length)]).append("\n");
                mcqText.append("B) ").append(OPTIONS[random.nextInt(OPTIONS.length)]).append("\n");
                mcqText.append("C) ").append(OPTIONS[random.nextInt(OPTIONS.length)]).append("\n");
                mcqText.append("D) ").append(OPTIONS[random.nextInt(OPTIONS.length)]).append("\n");
                mcqText.append("Correct: ").append(correctAnswer).append("\n\n");
            }
        }
        
        return mcqText.toString();
    }
}
