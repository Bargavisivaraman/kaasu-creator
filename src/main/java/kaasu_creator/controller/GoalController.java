package kaasu_creator.controller;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kaasu_creator.model.Goal;
import kaasu_creator.service.CurrentUserService;
import kaasu_creator.service.GoalService;

/**
 * GoalController - handles savings goals and roadmaps.
 *
 * Key concept: Nested objects in the Model
 * We pass both the Goal AND its associated Roadmap entries to the template.
 * The roadmap shows the week-by-week milestones for reaching the goal.
 */
@Controller
public class GoalController {

    private final GoalService goalService;
    private final CurrentUserService currentUserService;

    public GoalController(GoalService goalService, CurrentUserService currentUserService) {
        this.goalService = goalService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/goal")
    public String showGoalPage(Authentication authentication, Model model) {
        Long userId = currentUserService.requireUserId(authentication);
        model.addAttribute("goals", goalService.getGoalsByUser(userId));
        return "goal";
    }

    @PostMapping("/goal/create")
    public String createGoal(Authentication authentication,
                             @RequestParam String name,
                             @RequestParam BigDecimal targetAmount,
                             @RequestParam String deadline,
                             RedirectAttributes redirectAttributes) {
        Long userId = currentUserService.requireUserId(authentication);
        LocalDate deadlineDate = LocalDate.parse(deadline);
        Goal goal = goalService.createGoal(userId, name, targetAmount, deadlineDate);
        redirectAttributes.addFlashAttribute("success",
            "Goal created! " + goal.getName() + " - roadmap generated.");
        return "redirect:/goal";
    }

    @PostMapping("/goal/add-savings")
    public String addSavings(Authentication authentication,
                             @RequestParam Long goalId,
                             @RequestParam BigDecimal amount,
                             RedirectAttributes redirectAttributes) {
        Long userId = currentUserService.requireUserId(authentication);
        if (!ownsGoal(goalId, userId)) {
            redirectAttributes.addFlashAttribute("error", "Goal not found.");
            return "redirect:/goal";
        }
        goalService.addSavings(goalId, amount);
        redirectAttributes.addFlashAttribute("success", "Savings added to goal!");
        return "redirect:/goal";
    }

    @GetMapping("/goal/view")
    public String viewGoal(Authentication authentication,
                           @RequestParam Long goalId,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        Long userId = currentUserService.requireUserId(authentication);
        if (!ownsGoal(goalId, userId)) {
            redirectAttributes.addFlashAttribute("error", "Goal not found.");
            return "redirect:/goal";
        }
        model.addAttribute("goal", goalService.getGoalById(goalId));
        model.addAttribute("roadmap", goalService.getRoadmap(goalId));
        model.addAttribute("progress", goalService.getProgress(goalId));
        return "goal-detail";
    }

    /**
     * Verify the goal exists and belongs to the given user. Prevents one user
     * from viewing or modifying another user's goal by guessing its id.
     */
    private boolean ownsGoal(Long goalId, Long userId) {
        Goal goal = goalService.getGoalById(goalId);
        return goal != null && userId.equals(goal.getUserId());
    }
}