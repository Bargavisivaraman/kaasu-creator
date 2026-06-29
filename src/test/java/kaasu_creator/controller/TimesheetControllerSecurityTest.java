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

import kaasu_creator.service.CurrentUserService;
import kaasu_creator.service.JobService;
import kaasu_creator.service.TimesheetService;

/**
 * Verifies TimesheetController reports an error (rather than a false success)
 * when the owner-scoped delete removes no row.
 */
@WebMvcTest(TimesheetController.class)
@AutoConfigureMockMvc(addFilters = false)
class TimesheetControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TimesheetService timesheetService;

    @MockBean
    private JobService jobService;

    @MockBean
    private CurrentUserService currentUserService;

    @Test
    void deleteEntry_ownEntry_reportsSuccess() throws Exception {
        when(currentUserService.requireUserId(any())).thenReturn(1L);
        when(timesheetService.deleteEntry(9L, 1L)).thenReturn(1);

        mockMvc.perform(post("/timesheet/entry/delete").param("entryId", "9"))
                .andExpect(redirectedUrl("/timesheet"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void deleteEntry_otherUsersEntry_reportsError() throws Exception {
        when(currentUserService.requireUserId(any())).thenReturn(1L);
        when(timesheetService.deleteEntry(9L, 1L)).thenReturn(0);

        mockMvc.perform(post("/timesheet/entry/delete").param("entryId", "9"))
                .andExpect(redirectedUrl("/timesheet"))
                .andExpect(flash().attributeExists("errorMessage"));
    }
}
