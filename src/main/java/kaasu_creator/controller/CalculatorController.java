package kaasu_creator.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * CalculatorController - Phone-style calculator.
 *
 * Displays the calculator interface. All calculation logic is handled
 * client-side with JavaScript for instant, interactive experience.
 */
@Controller
public class CalculatorController {

    @GetMapping("/calculator")
    public String showCalculator() {
        return "calculator";
    }
}