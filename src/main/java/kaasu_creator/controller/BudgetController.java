package kaasu_creator.controller;

import java.math.BigDecimal;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kaasu_creator.dao.UserDao;
import kaasu_creator.service.BudgetService;

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
    private final UserDao userDao;

    public BudgetController(BudgetService budgetService, UserDao userDao) {
        this.budgetService = budgetService;
        this.userDao = userDao;
    }

    @GetMapping("/budget")
    public String showBudgetPage(Authentication authentication, Model model) {
        Long userId = getUserId(authentication);
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
        Long userId = getUserId(authentication);
        budgetService.addExpense(userId, title, category, amount);
        redirectAttributes.addFlashAttribute("success", "Expense added!");
        return "redirect:/budget";
    }

    @PostMapping("/budget/delete")
    public String deleteExpense(@RequestParam Long expenseId, RedirectAttributes redirectAttributes) {
        budgetService.deleteExpense(expenseId);
        redirectAttributes.addFlashAttribute("success", "Expense deleted.");
        return "redirect:/budget";
    }

    // Helper method to get user ID from the Authentication object
    private Long getUserId(Authentication authentication) {
        return userDao.findByEmail(authentication.getName())
                      .map(kaasu_creator.model.User::getId)
                      .orElseThrow(() -> new RuntimeException("User not found"));
    }
}