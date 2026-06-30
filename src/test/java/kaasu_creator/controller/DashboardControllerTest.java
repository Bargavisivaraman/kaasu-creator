package kaasu_creator.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import kaasu_creator.model.User;
import kaasu_creator.service.BudgetService;
import kaasu_creator.service.CurrentUserService;
import kaasu_creator.service.IncomeService;

@WebMvcTest(DashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private CurrentUserService currentUserService;
    @MockBean private IncomeService incomeService;
    @MockBean private BudgetService budgetService;

    @Test
    void showDashboard_computesBalanceFromIncomeAndExpenses() throws Exception {
        when(currentUserService.requireUser(any()))
                .thenReturn(new User(5L, "Bo", "bo@example.com", "hash", null));
        when(incomeService.getTotalIncome(5L)).thenReturn(new BigDecimal("2000"));
        when(budgetService.getTotalExpenses(5L)).thenReturn(new BigDecimal("500"));

        mockMvc.perform(get("/dashboard"))
                .andExpect(view().name("dashboard"))
                .andExpect(model().attribute("userName", "Bo"))
                .andExpect(model().attribute("totalIncome", new BigDecimal("2000")))
                .andExpect(model().attribute("totalExpenses", new BigDecimal("500")))
                .andExpect(model().attribute("currentBalance", new BigDecimal("1500")));
    }
}
