package kaasu_creator.controller;

import java.math.BigDecimal;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kaasu_creator.service.BudgetService;
import kaasu_creator.service.CurrentUserService;

/**
 * BudgetController - handles expense tracking.
 *
 * Key concept: Getting the logged-in user's ID
 * We look up the user by their email (from Authentication) to get their user ID.
 * This ID is then used to scope all expense operations to the correct user.
 */
@Controller
public class BudgetController {

    private final BudgetService budgetService;
    private final CurrentUserService currentUserService;

    public BudgetController(BudgetService budgetService, CurrentUserService currentUserService) {
        this.budgetService = budgetService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/budget")
    public String showBudgetPage(Authentication authentication, Model model) {
        Long userId = currentUserService.requireUserId(authentication);
        model.addAttribute("expenses", budgetService.getExpensesByUser(userId));
        model.addAttribute("total", budgetService.getTotalExpenses(userId));
        return "budget";
    }

    @PostMapping("/budget/add")
    public String addExpense(Authentication authentication,
                             @RequestParam String title,
                             @RequestParam String category,
                             @RequestParam BigDecimal amount,
                             RedirectAttributes redirectAttributes) {
        Long userId = currentUserService.requireUserId(authentication);
        budgetService.addExpense(userId, title, category, amount);
        redirectAttributes.addFlashAttribute("success", "Expense added!");
        return "redirect:/budget";
    }

    @PostMapping("/budget/delete")
    public String deleteExpense(Authentication authentication,
                               @RequestParam Long expenseId,
                               RedirectAttributes redirectAttributes) {
        Long userId = currentUserService.requireUserId(authentication);
        int deleted = budgetService.deleteExpense(expenseId, userId);
        if (deleted > 0) {
            redirectAttributes.addFlashAttribute("success", "Expense deleted.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Expense not found.");
        }
        return "redirect:/budget";
    }
}