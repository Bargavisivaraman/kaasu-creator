package kaasu_creator.controller;

import java.math.BigDecimal;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kaasu_creator.service.GeminiRoadmapService;

/**
 * AiRoadmapController - provides AI-powered financial roadmap generation.
 *
 * The actual Gemini API call lives in {@link GeminiRoadmapService}; this
 * controller only validates input and renders the result.
 */
@Controller
public class AiRoadmapController {

    private final GeminiRoadmapService geminiRoadmapService;

    public AiRoadmapController(GeminiRoadmapService geminiRoadmapService) {
        this.geminiRoadmapService = geminiRoadmapService;
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

        if (monthlyIncome.compareTo(BigDecimal.ZERO) <= 0) {
            model.addAttribute("error", "Monthly income must be greater than 0");
            return "ai-roadmap";
        }
        if (monthlyExpenses.compareTo(BigDecimal.ZERO) < 0) {
            model.addAttribute("error", "Monthly expenses cannot be negative");
            return "ai-roadmap";
        }

        try {
            String roadmap = geminiRoadmapService.generateRoadmap(
                    monthlyIncome, monthlyExpenses, savingsGoal, extraNotes);

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
}
