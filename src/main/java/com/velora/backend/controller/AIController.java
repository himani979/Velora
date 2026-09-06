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

    @PostMapping("/ask")
    public Map<String, String> askAI(
            @RequestBody Map<String, String> request) {

        String question = request.get("question");

        String answer = geminiService.askGemini(question);

        return Map.of(
                "answer", answer
        );
    }
}