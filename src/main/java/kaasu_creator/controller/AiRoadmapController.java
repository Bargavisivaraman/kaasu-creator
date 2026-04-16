package kaasu_creator.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * AiRoadmapController - provides AI-powered financial roadmap generation.
 *
 * Uses Gemini API to analyze user's financial data and generate personalized
 * savings and budgeting recommendations.
 */
@Controller
public class AiRoadmapController {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public AiRoadmapController() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    @GetMapping("/ai-roadmap")
    public String showAiRoadmap() {
        return "ai-roadmap";
    }

    @PostMapping("/generate-roadmap")
    public String generateRoadmap(
            @RequestParam BigDecimal monthlyIncome,
            @RequestParam BigDecimal monthlyExpenses,
            @RequestParam(required = false) BigDecimal savingsGoal,
            @RequestParam(required = false) String extraNotes,
            Authentication authentication,
            Model model) {

        try {
            // Validate inputs
            if (monthlyIncome.compareTo(BigDecimal.ZERO) <= 0) {
                model.addAttribute("error", "Monthly income must be greater than 0");
                return "ai-roadmap";
            }

            if (monthlyExpenses.compareTo(BigDecimal.ZERO) < 0) {
                model.addAttribute("error", "Monthly expenses cannot be negative");
                return "ai-roadmap";
            }

            // Prepare data for AI
            Map<String, Object> financialData = new HashMap<>();
            financialData.put("monthlyIncome", monthlyIncome);
            financialData.put("monthlyExpenses", monthlyExpenses);
            financialData.put("savingsGoal", savingsGoal != null ? savingsGoal : "Not specified");
            financialData.put("extraNotes", extraNotes != null ? extraNotes : "None provided");

            // Generate AI roadmap
            String roadmap = generateAiRoadmap(financialData);

            // Add data to model for display
            model.addAttribute("monthlyIncome", monthlyIncome);
            model.addAttribute("monthlyExpenses", monthlyExpenses);
            model.addAttribute("savingsGoal", savingsGoal);
            model.addAttribute("extraNotes", extraNotes);
            model.addAttribute("roadmap", roadmap);
            model.addAttribute("generated", true);

        } catch (Exception e) {
            model.addAttribute("error", "Error generating roadmap: " + e.getMessage());
        }

        return "ai-roadmap";
    }

    @SuppressWarnings("unchecked")
    private String generateAiRoadmap(Map<String, Object> financialData) throws Exception {
        String systemPrompt = """
            You are Kaasu-chan, a friendly finance assistant inside a budgeting app.
            Your job is to create a short, practical financial roadmap based on the user's monthly income, monthly expenses, and savings goal.
            Be clear, supportive, and realistic.
            Give the user a step-by-step roadmap.
            Keep the answer under 200 words.
            Do not give legal, tax, or investment advice.
            Focus on budgeting, saving, and spending control.
            """;

        String userPrompt = String.format("""
            Monthly Income: $%s
            Monthly Expenses: $%s
            Savings Goal: %s
            Extra Notes: %s

            Please create a personalized financial roadmap for this user.
            """,
            financialData.get("monthlyIncome"),
            financialData.get("monthlyExpenses"),
            financialData.get("savingsGoal"),
            financialData.get("extraNotes")
        );

        // Prepare Gemini API request
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", new Object[]{
            Map.of("parts", new Object[]{
                Map.of("text", systemPrompt + "\n\n" + userPrompt)
            })
        });

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + geminiApiKey;

        ResponseEntity<String> response = restTemplate.exchange(
            apiUrl,
            HttpMethod.POST,
            entity,
            String.class
        );

        // Parse response
        JsonNode root = objectMapper.readTree(response.getBody());
        String generatedText = root.path("candidates")
            .get(0)
            .path("content")
            .path("parts")
            .get(0)
            .path("text")
            .asText();

        return generatedText.trim();
    }
}