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

import kaasu_creator.service.BudgetService;
import kaasu_creator.service.CurrentUserService;

/**
 * Verifies BudgetController only confirms a deletion when a row was actually
 * removed (the owner-scoped delete), so deleting another user's expense
 * surfaces an error rather than a false success.
 */
@WebMvcTest(BudgetController.class)
@AutoConfigureMockMvc(addFilters = false)
class BudgetControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BudgetService budgetService;

    @MockBean
    private CurrentUserService currentUserService;

    @Test
    void deleteExpense_ownExpense_reportsSuccess() throws Exception {
        when(currentUserService.requireUserId(any())).thenReturn(1L);
        when(budgetService.deleteExpense(5L, 1L)).thenReturn(1);

        mockMvc.perform(post("/budget/delete").param("expenseId", "5"))
                .andExpect(redirectedUrl("/budget"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    void deleteExpense_otherUsersExpense_reportsError() throws Exception {
        when(currentUserService.requireUserId(any())).thenReturn(1L);
        // Row belongs to another user, so the scoped delete removes nothing
        when(budgetService.deleteExpense(5L, 1L)).thenReturn(0);

        mockMvc.perform(post("/budget/delete").param("expenseId", "5"))
                .andExpect(redirectedUrl("/budget"))
                .andExpect(flash().attributeExists("error"));
    }
}
