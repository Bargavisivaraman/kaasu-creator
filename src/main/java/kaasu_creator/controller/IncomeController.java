package kaasu_creator.controller;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kaasu_creator.dao.UserDao;
import kaasu_creator.model.Income;
import kaasu_creator.model.Job;
import kaasu_creator.model.TimesheetEntry;
import kaasu_creator.service.IncomeService;
import kaasu_creator.service.JobService;
import kaasu_creator.service.TimesheetService;

@Controller
public class IncomeController {

    private final IncomeService incomeService;
    private final TimesheetService timesheetService;
    private final JobService jobService;
    private final UserDao userDao;

    public IncomeController(IncomeService incomeService, TimesheetService timesheetService,
                            JobService jobService, UserDao userDao) {
        this.incomeService = incomeService;
        this.timesheetService = timesheetService;
        this.jobService = jobService;
        this.userDao = userDao;
    }

    private Long getUserId(Authentication authentication) {
        return userDao.findByEmail(authentication.getName())
                .map(kaasu_creator.model.User::getId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ── GET /income ────────────────────────────────────────────────────────────
    @GetMapping("/income")
    public String showIncome(
            Authentication authentication,
            Model model,
            @RequestParam(required = false) String tab,
            @RequestParam(required = false) Long selectedJobId) {

        var userOptional = userDao.findByEmail(authentication.getName());
        if (userOptional.isEmpty()) {
            model.addAttribute("errorMessage", "User not found.");
            return "income";
        }

        Long userId = userOptional.get().getId();

        // Jobs with aggregated totals (single LEFT JOIN query)
        List<Job> jobs = jobService.getJobsWithSummary(userId);

        // All work entries for history tab
        List<TimesheetEntry> allEntries = timesheetService.getEntriesByUser(userId);

        // Extra income (kept for the optional extra section)
        List<Income> extraIncomes = incomeService.getExtraIncomesByUser(userId);

        // Overall totals for summary bar
        BigDecimal totalEarned = timesheetService.getTotalEarned(userId);

        model.addAttribute("jobs", jobs);
        model.addAttribute("allEntries", allEntries);
        model.addAttribute("extraIncomes", extraIncomes);
        model.addAttribute("totalEarned", totalEarned);
        model.addAttribute("selectedJobId", selectedJobId);

        // activeTab comes from flash (redirect) OR URL param OR default
        if (!model.containsAttribute("activeTab")) {
            model.addAttribute("activeTab", tab != null ? tab : "jobs");
        }

        return "income";
    }

    // ── POST /income/job/save ──────────────────────────────────────────────────
    @PostMapping("/income/job/save")
    public String saveJob(
            @RequestParam String jobName,
            @RequestParam BigDecimal hourlyWage,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            Long userId = getUserId(authentication);
            Job job = new Job();
            job.setUserId(userId);
            job.setJobName(jobName.trim());
            job.setHourlyWage(hourlyWage);
            jobService.addJob(job);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Job '" + jobName.trim() + "' added successfully!");
            redirectAttributes.addFlashAttribute("activeTab", "jobs");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error adding job: " + e.getMessage());
        }
        return "redirect:/income";
    }

    // ── POST /income/job/delete ────────────────────────────────────────────────
    @PostMapping("/income/job/delete")
    public String deleteJob(
            @RequestParam Long jobId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            Long userId = getUserId(authentication);
            var jobOpt = jobService.getJobByIdAndUser(jobId, userId);
            if (jobOpt.isPresent()) {
                jobService.deleteJob(jobId);
                redirectAttributes.addFlashAttribute("successMessage", "Job deleted.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Job not found.");
            }
            redirectAttributes.addFlashAttribute("activeTab", "jobs");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error deleting job: " + e.getMessage());
        }
        return "redirect:/income";
    }

    // ── POST /income/work/save ─────────────────────────────────────────────────
    @PostMapping("/income/work/save")
    public String saveWorkEntry(
            @RequestParam Long jobId,
            @RequestParam String workDate,
            @RequestParam BigDecimal hoursWorked,
            @RequestParam(required = false) String notes,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            Long userId = getUserId(authentication);

            // Verify job belongs to this user
            var jobOpt = jobService.getJobByIdAndUser(jobId, userId);
            if (jobOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Job not found.");
                redirectAttributes.addFlashAttribute("activeTab", "log");
                return "redirect:/income";
            }

            TimesheetEntry entry = new TimesheetEntry();
            entry.setUserId(userId);
            entry.setJobId(jobId);
            entry.setWorkDate(Date.valueOf(workDate));
            entry.setHoursWorked(hoursWorked);
            entry.setNotes(notes != null ? notes.trim() : null);

            timesheetService.save(entry);

            BigDecimal wage     = jobOpt.get().getHourlyWage();
            BigDecimal earnings = hoursWorked.multiply(wage);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Work entry saved! Earned: $" + earnings.setScale(2, java.math.RoundingMode.HALF_UP));
            redirectAttributes.addFlashAttribute("activeTab", "history");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error saving entry: " + e.getMessage());
            redirectAttributes.addFlashAttribute("activeTab", "log");
        }
        return "redirect:/income";
    }

    // ── POST /income/work/delete ───────────────────────────────────────────────
    @PostMapping("/income/work/delete")
    public String deleteWorkEntry(
            @RequestParam Long entryId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            Long userId = getUserId(authentication);
            int deleted = timesheetService.deleteEntry(entryId, userId);
            if (deleted > 0) {
                redirectAttributes.addFlashAttribute("successMessage", "Entry deleted.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Entry not found.");
            }
            redirectAttributes.addFlashAttribute("activeTab", "history");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error deleting entry: " + e.getMessage());
        }
        return "redirect:/income";
    }

    // ── POST /income/save (extra income) ──────────────────────────────────────
    @PostMapping("/income/save")
    public String saveIncomeEntries(
            @RequestParam(value = "sourceName", required = false) List<String> sourceNames,
            @RequestParam(value = "amount",     required = false) List<BigDecimal> amounts,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (sourceNames == null || sourceNames.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Please add at least one extra income row before saving.");
            return "redirect:/income";
        }

        try {
            Long userId = getUserId(authentication);
            List<Income> toSave = new ArrayList<>();

            for (int i = 0; i < sourceNames.size(); i++) {
                String source = sourceNames.get(i);
                BigDecimal amount = (amounts != null && amounts.size() > i) ? amounts.get(i) : null;
                if (source == null || source.isBlank() || amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }
                toSave.add(new Income(null, userId, "EXTRA", source.trim(), amount,
                        new Date(System.currentTimeMillis()), null));
            }

            if (toSave.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "No valid rows to save.");
                return "redirect:/income";
            }

            toSave.forEach(incomeService::addIncome);
            redirectAttributes.addFlashAttribute("successMessage", "Extra income saved!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error saving extra income: " + e.getMessage());
        }
        return "redirect:/income";
    }
}
