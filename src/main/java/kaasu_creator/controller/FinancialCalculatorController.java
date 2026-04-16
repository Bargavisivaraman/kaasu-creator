package kaasu_creator.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * FinancialCalculatorController - provides financial calculation tools.
 *
 * Contains three calculators:
 * 1. Savings Goal Calculator - calculates months needed to reach a savings goal
 * 2. Compound Interest Calculator - calculates future value with compound interest
 * 3. Budget Ratio Calculator - calculates expense ratio and remaining balance
 */
@Controller
public class FinancialCalculatorController {

    @GetMapping("/financial-calculator")
    public String showFinancialCalculator() {
        return "financial-calculator";
    }

    @PostMapping("/calculate-savings-goal")
    public String calculateSavingsGoal(
            @RequestParam BigDecimal targetAmount,
            @RequestParam BigDecimal monthlySavings,
            Model model) {

        try {
            // Validate inputs
            if (targetAmount.compareTo(BigDecimal.ZERO) <= 0) {
                model.addAttribute("savingsError", "Target amount must be greater than 0");
                return "financial-calculator";
            }
            if (monthlySavings.compareTo(BigDecimal.ZERO) <= 0) {
                model.addAttribute("savingsError", "Monthly savings must be greater than 0");
                return "financial-calculator";
            }

            // Calculate months needed
            BigDecimal monthsNeeded = targetAmount.divide(monthlySavings, 2, RoundingMode.HALF_UP);

            model.addAttribute("targetAmount", targetAmount);
            model.addAttribute("monthlySavings", monthlySavings);
            model.addAttribute("monthsNeeded", monthsNeeded);
            model.addAttribute("savingsCalculated", true);

        } catch (Exception e) {
            model.addAttribute("savingsError", "Error calculating savings goal: " + e.getMessage());
        }

        return "financial-calculator";
    }

    @PostMapping("/calculate-compound-interest")
    public String calculateCompoundInterest(
            @RequestParam BigDecimal principal,
            @RequestParam BigDecimal interestRate,
            @RequestParam Integer years,
            Model model) {

        try {
            // Validate inputs
            if (principal.compareTo(BigDecimal.ZERO) <= 0) {
                model.addAttribute("interestError", "Principal amount must be greater than 0");
                return "financial-calculator";
            }
            if (interestRate.compareTo(BigDecimal.ZERO) < 0 || interestRate.compareTo(new BigDecimal("100")) > 0) {
                model.addAttribute("interestError", "Interest rate must be between 0% and 100%");
                return "financial-calculator";
            }
            if (years <= 0) {
                model.addAttribute("interestError", "Number of years must be greater than 0");
                return "financial-calculator";
            }

            // Calculate compound interest: A = P(1 + r)^t
            BigDecimal rate = interestRate.divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP);
            BigDecimal onePlusRate = BigDecimal.ONE.add(rate);
            BigDecimal futureValue = principal.multiply(onePlusRate.pow(years));

            model.addAttribute("principal", principal);
            model.addAttribute("interestRate", interestRate);
            model.addAttribute("years", years);
            model.addAttribute("futureValue", futureValue.setScale(2, RoundingMode.HALF_UP));
            model.addAttribute("interestCalculated", true);

        } catch (Exception e) {
            model.addAttribute("interestError", "Error calculating compound interest: " + e.getMessage());
        }

        return "financial-calculator";
    }

    @PostMapping("/calculate-budget-ratio")
    public String calculateBudgetRatio(
            @RequestParam BigDecimal monthlyIncome,
            @RequestParam BigDecimal monthlyExpenses,
            Model model) {

        try {
            // Validate inputs
            if (monthlyIncome.compareTo(BigDecimal.ZERO) <= 0) {
                model.addAttribute("budgetError", "Monthly income must be greater than 0");
                return "financial-calculator";
            }
            if (monthlyExpenses.compareTo(BigDecimal.ZERO) < 0) {
                model.addAttribute("budgetError", "Monthly expenses cannot be negative");
                return "financial-calculator";
            }

            // Calculate expense ratio and remaining balance
            BigDecimal expenseRatio = BigDecimal.ZERO;
            if (monthlyIncome.compareTo(BigDecimal.ZERO) > 0) {
                expenseRatio = monthlyExpenses.divide(monthlyIncome, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
            }

            BigDecimal remainingBalance = monthlyIncome.subtract(monthlyExpenses);

            model.addAttribute("monthlyIncome", monthlyIncome);
            model.addAttribute("monthlyExpenses", monthlyExpenses);
            model.addAttribute("expenseRatio", expenseRatio.setScale(2, RoundingMode.HALF_UP));
            model.addAttribute("remainingBalance", remainingBalance.setScale(2, RoundingMode.HALF_UP));
            model.addAttribute("budgetCalculated", true);

        } catch (Exception e) {
            model.addAttribute("budgetError", "Error calculating budget ratio: " + e.getMessage());
        }

        return "financial-calculator";
    }
}