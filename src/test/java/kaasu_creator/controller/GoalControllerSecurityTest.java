package kaasu_creator.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import kaasu_creator.dao.UserDao;
import kaasu_creator.model.Goal;
import kaasu_creator.model.User;
import kaasu_creator.service.GoalService;

/**
 * Verifies that GoalController rejects attempts to operate on a goal the
 * authenticated user does not own (the IDOR fix).
 */
@WebMvcTest(GoalController.class)
class GoalControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GoalService goalService;

    @MockBean
    private UserDao userDao;

    private User currentUser() {
        User u = new User();
        u.setId(1L);
        u.setEmail("me@example.com");
        return u;
    }

    private Goal goalOwnedBy(Long ownerId) {
        return new Goal(99L, ownerId, "Someone else's goal",
                new BigDecimal("1000"), BigDecimal.ZERO, LocalDate.now().plusWeeks(5), null);
    }

    @Test
    void addSavings_toAnotherUsersGoal_isRejected() throws Exception {
        when(userDao.findByEmail("me@example.com")).thenReturn(Optional.of(currentUser()));
        // Goal 99 belongs to user 2, not the logged-in user 1
        when(goalService.getGoalById(99L)).thenReturn(goalOwnedBy(2L));

        mockMvc.perform(post("/goal/add-savings")
                        .param("goalId", "99")
                        .param("amount", "50")
                        .with(user("me@example.com"))
                        .with(csrf()))
                .andExpect(redirectedUrl("/goal"));

        // The savings must NOT be applied to a goal the user does not own
        verify(goalService, never()).addSavings(eq(99L), any());
    }

    @Test
    void addSavings_toOwnGoal_succeeds() throws Exception {
        when(userDao.findByEmail("me@example.com")).thenReturn(Optional.of(currentUser()));
        when(goalService.getGoalById(99L)).thenReturn(goalOwnedBy(1L));

        mockMvc.perform(post("/goal/add-savings")
                        .param("goalId", "99")
                        .param("amount", "50")
                        .with(user("me@example.com"))
                        .with(csrf()))
                .andExpect(redirectedUrl("/goal"));

        verify(goalService).addSavings(eq(99L), eq(new BigDecimal("50")));
    }
}
