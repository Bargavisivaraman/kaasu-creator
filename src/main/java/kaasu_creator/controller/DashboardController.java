package kaasu_creator.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kaasu_creator.dao.UserDao;
import kaasu_creator.service.BudgetService;
import kaasu_creator.service.IncomeService;

/**
 * DashboardController - the main hub of the app.
 */
@Controller
public class DashboardController {

    private final UserDao userDao;
    private final BudgetService budgetService;
    private final IncomeService incomeService;

    public DashboardController(UserDao userDao, BudgetService budgetService, IncomeService incomeService) {
        this.userDao = userDao;
        this.budgetService = budgetService;
        this.incomeService = incomeService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Authentication authentication, Model model) {
        String email = authentication.getName();
        String displayName = email;

        var userOptional = userDao.findByEmail(email);
        if (userOptional.isPresent()) {
            var user = userOptional.get();
            String fullName = user.getFullName();
            if (fullName != null && !fullName.trim().isEmpty()) {
                displayName = fullName;
            }

            Long userId = user.getId();
            var totalIncome = incomeService.getTotalIncome(userId);
            var totalExpenses = budgetService.getTotalExpenses(userId);
            var currentBalance = totalIncome.subtract(totalExpenses);

            model.addAttribute("totalIncome", totalIncome);
            model.addAttribute("totalExpenses", totalExpenses);
            model.addAttribute("currentBalance", currentBalance);
            model.addAttribute("userId", userId);
        }

        model.addAttribute("userName", displayName);
        return "dashboard";
    }

    @GetMapping("/")
    public String showHome() {
        return "redirect:/dashboard";
    }

    @PostMapping("/dashboard/delete-account")
    public String deleteAccount(Authentication authentication, RedirectAttributes redirectAttributes) {
        String email = authentication.getName();
        userDao.deleteByEmail(email);
        redirectAttributes.addFlashAttribute("message", "Account deleted successfully.");
        return "redirect:/login?deleted";
    }
}