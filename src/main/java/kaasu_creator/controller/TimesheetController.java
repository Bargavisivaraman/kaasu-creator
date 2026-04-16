package kaasu_creator.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kaasu_creator.dao.UserDao;
import kaasu_creator.model.Job;
import kaasu_creator.model.TimesheetEntry;
import kaasu_creator.service.JobService;
import kaasu_creator.service.TimesheetService;

@Controller
public class TimesheetController {

    private final TimesheetService timesheetService;
    private final JobService jobService;
    private final UserDao userDao;

    public TimesheetController(TimesheetService timesheetService, JobService jobService, UserDao userDao) {
        this.timesheetService = timesheetService;
        this.jobService = jobService;
        this.userDao = userDao;
    }

@GetMapping("/timesheet")
public String showTimesheet(Authentication authentication, Model model) {
    Long userId = getUserId(authentication);
    List<TimesheetEntry> entries = timesheetService.getEntriesByUser(userId);
    List<Job> jobs = jobService.getJobsByUser(userId);
    BigDecimal totalEarned = timesheetService.getTotalEarned(userId);
    BigDecimal totalHours = timesheetService.getTotalHours(userId);
    var jobSummaries = timesheetService.getJobSummariesByUser(userId);

    model.addAttribute("entries", entries);
    model.addAttribute("jobs", jobs);
    model.addAttribute("jobSummaries", jobSummaries);
    model.addAttribute("totalHours", totalHours);
    model.addAttribute("totalEarned", totalEarned);
    model.addAttribute("today", java.time.LocalDate.now().toString());

    return "timesheet";
}

    @PostMapping("/timesheet/job/save")
    public String saveJob(
            @RequestParam(value = "jobId", required = false) Long jobId,
            @RequestParam("jobName") String jobName,
            @RequestParam("hourlyWage") BigDecimal hourlyWage,
            @RequestParam(value = "notes", required = false) String notes,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (jobName == null || jobName.isBlank() || hourlyWage == null || hourlyWage.compareTo(BigDecimal.ZERO) <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Job name and hourly wage are required.");
            return "redirect:/timesheet";
        }

        try {
            Long userId = getUserId(authentication);
            Job job = new Job(jobId, userId, jobName.trim(), hourlyWage, notes != null ? notes.trim() : "", null);
            
            if (jobId != null) {
                jobService.updateJob(job);
                redirectAttributes.addFlashAttribute("successMessage", "Job updated successfully.");
            } else {
                jobService.addJob(job);
                redirectAttributes.addFlashAttribute("successMessage", "Job added successfully.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving job: " + e.getMessage());
        }

        return "redirect:/timesheet";
    }

    @PostMapping("/timesheet/entries")
    public String saveEntries(
            @RequestParam(value = "jobId", required = false) Long[] jobIds,
            @RequestParam(value = "workDate", required = false) String[] workDates,
            @RequestParam(value = "hoursWorked", required = false) String[] hoursWorkedStrings,
            @RequestParam(value = "notes", required = false) String[] notes,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (jobIds == null || workDates == null || hoursWorkedStrings == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please add at least one valid timesheet row before saving.");
            return "redirect:/timesheet";
        }

        Long userId = getUserId(authentication);
        int savedCount = 0;
        int rows = Math.min(Math.min(jobIds.length, workDates.length), hoursWorkedStrings.length);

        try {
            for (int i = 0; i < rows; i++) {
                Long jobId = jobIds[i];
                String dateValue = workDates[i];
                String hoursValue = hoursWorkedStrings[i];
                String notesValue = (notes != null && notes.length > i) ? notes[i] : "";

                if (jobId == null || dateValue == null || dateValue.isBlank() || hoursValue == null || hoursValue.isBlank()) {
                    continue;
                }

                var optionalJob = jobService.getJobByIdAndUser(jobId, userId);
                if (optionalJob.isEmpty()) {
                    continue;
                }

                Job job = optionalJob.get();
                BigDecimal hours = new BigDecimal(hoursValue).setScale(2, RoundingMode.HALF_UP);
                
                if (hours.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }

                Date workDateValue = Date.valueOf(dateValue);
                TimesheetEntry entry = new TimesheetEntry(
                        null,
                        userId,
                        jobId,
                        workDateValue,
                        hours,
                        notesValue != null ? notesValue.trim() : "",
                        null
                );
                timesheetService.save(entry);
                savedCount++;
            }

            if (savedCount == 0) {
                redirectAttributes.addFlashAttribute("errorMessage", "Please enter at least one valid timesheet row before saving.");
            } else {
                redirectAttributes.addFlashAttribute("successMessage", savedCount + " timesheet row(s) saved successfully.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving timesheet entries: " + e.getMessage());
        }

        return "redirect:/timesheet";
    }

    @PostMapping("/timesheet/entry/delete")
    public String deleteEntry(
            @RequestParam("entryId") Long entryId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            Long userId = getUserId(authentication);
            int deleted = timesheetService.deleteEntry(entryId, userId);
            if (deleted == 0) {
                redirectAttributes.addFlashAttribute("errorMessage", "Entry not found or you do not have permission to delete it.");
            } else {
                redirectAttributes.addFlashAttribute("successMessage", "Timesheet entry deleted successfully.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting entry: " + e.getMessage());
        }
        return "redirect:/timesheet";
    }

    private Long getUserId(Authentication authentication) {
        return userDao.findByEmail(authentication.getName())
                .map(kaasu_creator.model.User::getId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
