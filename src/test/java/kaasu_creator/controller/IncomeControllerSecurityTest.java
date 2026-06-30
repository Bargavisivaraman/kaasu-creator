package kaasu_creator.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import kaasu_creator.dao.UserDao;
import kaasu_creator.service.CurrentUserService;
import kaasu_creator.service.IncomeService;
import kaasu_creator.service.JobService;
import kaasu_creator.service.TimesheetService;

/**
 * Verifies IncomeController reports an error when the owner-scoped job delete
 * removes nothing, rather than falsely confirming.
 */
@WebMvcTest(IncomeController.class)
@AutoConfigureMockMvc(addFilters = false)
class IncomeControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private IncomeService incomeService;
    @MockBean private TimesheetService timesheetService;
    @MockBean private JobService jobService;
    @MockBean private UserDao userDao;
    @MockBean private CurrentUserService currentUserService;

    @Test
    void deleteJob_ownJob_reportsSuccess() throws Exception {
        when(currentUserService.requireUserId(any())).thenReturn(1L);
        when(jobService.deleteJob(7L, 1L)).thenReturn(1);

        mockMvc.perform(post("/income/job/delete").param("jobId", "7"))
                .andExpect(redirectedUrl("/income"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void deleteJob_otherUsersJob_reportsError() throws Exception {
        when(currentUserService.requireUserId(any())).thenReturn(1L);
        when(jobService.deleteJob(7L, 1L)).thenReturn(0);

        mockMvc.perform(post("/income/job/delete").param("jobId", "7"))
                .andExpect(redirectedUrl("/income"))
                .andExpect(flash().attributeExists("errorMessage"));
    }
}
