package kaasu_creator.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

import kaasu_creator.service.FinancialCalculatorService;
import kaasu_creator.service.FinancialCalculatorService.BudgetRatio;

/**
 * FinancialCalculatorController - provides financial calculation tools.
 *
 * The calculations live in {@link FinancialCalculatorService}; this controller
 * validates input, invokes the service, and exposes results to the view.
 */
@Controller
public class FinancialCalculatorController {

    private final FinancialCalculatorService calculatorService;

    public FinancialCalculatorController(FinancialCalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }

    @GetMapping("/financial-calculator")
    public String showFinancialCalculator() {
        return "financial-calculator";
    }

    @PostMapping("/calculate-savings-goal")
    public String calculateSavingsGoal(
            @RequestParam BigDecimal targetAmount,
            @RequestParam BigDecimal monthlySavings,
            Model model) {

        if (targetAmount.compareTo(BigDecimal.ZERO) <= 0) {
            model.addAttribute("savingsError", "Target amount must be greater than 0");
            return "financial-calculator";
        }
        if (monthlySavings.compareTo(BigDecimal.ZERO) <= 0) {
            model.addAttribute("savingsError", "Monthly savings must be greater than 0");
            return "financial-calculator";
        }

        model.addAttribute("targetAmount", targetAmount);
        model.addAttribute("monthlySavings", monthlySavings);
        model.addAttribute("monthsNeeded", calculatorService.monthsToReachGoal(targetAmount, monthlySavings));
        model.addAttribute("savingsCalculated", true);
        return "financial-calculator";
    }

    @PostMapping("/calculate-compound-interest")
    public String calculateCompoundInterest(
            @RequestParam BigDecimal principal,
            @RequestParam BigDecimal interestRate,
            @RequestParam Integer years,
            Model model) {

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

        model.addAttribute("principal", principal);
        model.addAttribute("interestRate", interestRate);
        model.addAttribute("years", years);
        model.addAttribute("futureValue",
                calculatorService.compoundInterestFutureValue(principal, interestRate, years));
        model.addAttribute("interestCalculated", true);
        return "financial-calculator";
    }

    @PostMapping("/calculate-budget-ratio")
    public String calculateBudgetRatio(
            @RequestParam BigDecimal monthlyIncome,
            @RequestParam BigDecimal monthlyExpenses,
            Model model) {

        if (monthlyIncome.compareTo(BigDecimal.ZERO) <= 0) {
            model.addAttribute("budgetError", "Monthly income must be greater than 0");
            return "financial-calculator";
        }
        if (monthlyExpenses.compareTo(BigDecimal.ZERO) < 0) {
            model.addAttribute("budgetError", "Monthly expenses cannot be negative");
            return "financial-calculator";
        }

        BudgetRatio ratio = calculatorService.budgetRatio(monthlyIncome, monthlyExpenses);
        model.addAttribute("monthlyIncome", monthlyIncome);
        model.addAttribute("monthlyExpenses", monthlyExpenses);
        model.addAttribute("expenseRatio", ratio.expenseRatioPercent());
        model.addAttribute("remainingBalance", ratio.remainingBalance());
        model.addAttribute("budgetCalculated", true);
        return "financial-calculator";
    }
}
