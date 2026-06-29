package kaasu_creator.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kaasu_creator.model.User;
import kaasu_creator.service.BudgetService;
import kaasu_creator.service.CurrentUserService;
import kaasu_creator.service.IncomeService;

/**
 * DashboardController - the main hub of the app.
 */
@Controller
public class DashboardController {

    private final CurrentUserService currentUserService;
    private final BudgetService budgetService;
    private final IncomeService incomeService;

    public DashboardController(CurrentUserService currentUserService, BudgetService budgetService,
                              IncomeService incomeService) {
        this.currentUserService = currentUserService;
        this.budgetService = budgetService;
        this.incomeService = incomeService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Authentication authentication, Model model) {
        User user = currentUserService.requireUser(authentication);

        String fullName = user.getFullName();
        String displayName = (fullName != null && !fullName.trim().isEmpty())
                ? fullName
                : user.getEmail();

        Long userId = user.getId();
        var totalIncome = incomeService.getTotalIncome(userId);
        var totalExpenses = budgetService.getTotalExpenses(userId);

        model.addAttribute("totalIncome", totalIncome);
        model.addAttribute("totalExpenses", totalExpenses);
        model.addAttribute("currentBalance", totalIncome.subtract(totalExpenses));
        model.addAttribute("userId", userId);
        model.addAttribute("userName", displayName);
        return "dashboard";
    }

    @GetMapping("/")
    public String showHome() {
        return "redirect:/dashboard";
    }

    @PostMapping("/dashboard/delete-account")
    public String deleteAccount(Authentication authentication, RedirectAttributes redirectAttributes) {
        currentUserService.deleteCurrentUser(authentication);
        redirectAttributes.addFlashAttribute("message", "Account deleted successfully.");
        return "redirect:/login?deleted";
    }
}