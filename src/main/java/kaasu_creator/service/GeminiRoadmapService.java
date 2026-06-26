package kaasu_creator.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * GeminiRoadmapService encapsulates the call to Google's Gemini API used to
 * generate a personalized financial roadmap. Extracting this out of the
 * controller keeps the web layer thin, makes the parsing logic unit-testable,
 * and centralizes configuration (model, endpoint, timeouts).
 */
@Service
public class GeminiRoadmapService {

    private static final String SYSTEM_PROMPT = """
        You are Kaasu-chan, a friendly finance assistant inside a budgeting app.
        Your job is to create a short, practical financial roadmap based on the user's monthly income, monthly expenses, and savings goal.
        Be clear, supportive, and realistic.
        Give the user a step-by-step roadmap.
        Keep the answer under 200 words.
        Do not give legal, tax, or investment advice.
        Focus on budgeting, saving, and spending control.
        """;

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String model;
    private final String baseUrl;

    public GeminiRoadmapService(
            ObjectMapper objectMapper,
            @Value("${gemini.api.key}") String apiKey,
            @Value("${gemini.api.model}") String model,
            @Value("${gemini.api.base-url}") String baseUrl,
            @Value("${gemini.api.connect-timeout-ms}") int connectTimeoutMs,
            @Value("${gemini.api.read-timeout-ms}") int readTimeoutMs) {
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.model = model;
        this.baseUrl = baseUrl;

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofMillis(connectTimeoutMs));
        factory.setReadTimeout(Duration.ofMillis(readTimeoutMs));
        this.restTemplate = new RestTemplate(factory);
    }

    /**
     * Call Gemini and return the generated roadmap text.
     *
     * @throws IllegalStateException if the API key is not configured or the
     *         response contains no usable content.
     */
    public String generateRoadmap(BigDecimal monthlyIncome, BigDecimal monthlyExpenses,
                                  BigDecimal savingsGoal, String extraNotes) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Gemini API key is not configured");
        }

        String userPrompt = String.format("""
            Monthly Income: $%s
            Monthly Expenses: $%s
            Savings Goal: %s
            Extra Notes: %s

            Please create a personalized financial roadmap for this user.
            """,
            monthlyIncome,
            monthlyExpenses,
            savingsGoal != null ? savingsGoal : "Not specified",
            extraNotes != null && !extraNotes.isBlank() ? extraNotes : "None provided");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", new Object[]{
            Map.of("parts", new Object[]{
                Map.of("text", SYSTEM_PROMPT + "\n\n" + userPrompt)
            })
        });

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String apiUrl = baseUrl + "/" + model + ":generateContent?key=" + apiKey;

        ResponseEntity<String> response = restTemplate.exchange(
            apiUrl, HttpMethod.POST, new HttpEntity<>(requestBody, headers), String.class);

        return parseRoadmap(response.getBody());
    }

    /**
     * Extract the generated text from a Gemini response body. Package-private so
     * it can be unit tested without making a network call.
     */
    String parseRoadmap(String responseBody) {
        if (responseBody == null || responseBody.isBlank()) {
            throw new IllegalStateException("Gemini returned an empty response");
        }
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode candidates = root.path("candidates");
            if (!candidates.isArray() || candidates.isEmpty()) {
                throw new IllegalStateException("Gemini returned no candidates");
            }
            JsonNode parts = candidates.get(0).path("content").path("parts");
            if (!parts.isArray() || parts.isEmpty()) {
                throw new IllegalStateException("Gemini returned no content parts");
            }
            String text = parts.get(0).path("text").asText("");
            if (text.isBlank()) {
                throw new IllegalStateException("Gemini returned blank text");
            }
            return text.trim();
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Could not parse Gemini response", e);
        }
    }
}
