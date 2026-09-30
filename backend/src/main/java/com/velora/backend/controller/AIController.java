
package com.velora.backend.controller;

import com.velora.backend.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "http://localhost:5173")
public class AIController {

    @Autowired
    private GeminiService geminiService;

    // Existing AI Assistant endpoint
    @PostMapping("/ask")
    public Map<String, String> askAI(
            @RequestBody Map<String, String> request) {

        String question = request.get("question");

        String answer = geminiService.askGemini(question);

        return Map.of(
                "answer", answer
        );
    }

    // New Future Lab AI Insights endpoint
    @PostMapping("/future-insights")
    public Map<String, String> futureInsights(
            @RequestBody Map<String, Object> request) {

        String prompt =
                "You are a financial education assistant " +
                        "inside an application called Velora. " +
                        "Explain the following financial simulation " +
                        "in simple, easy-to-understand English. " +
                        "Use Indian rupees (INR). " +
                        "Explain how income, expenses, extra savings, " +
                        "expense increases, and subscription " +
                        "cancellations affect the projection. " +
                        "Provide 3 practical budgeting suggestions " +
                        "based only on the supplied scenario. " +
                        "Do not invent missing financial information, " +
                        "promise returns, or recommend specific investments. " +
                        "Keep the response concise and well structured.\n\n" +
                        "Simulation data:\n" +
                        request.toString();

        String answer = geminiService.askGemini(prompt);

        return Map.of(
                "answer", answer
        );
    }
}
