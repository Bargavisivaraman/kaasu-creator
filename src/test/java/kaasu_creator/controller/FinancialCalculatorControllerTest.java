package kaasu_creator.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import kaasu_creator.service.FinancialCalculatorService;
import kaasu_creator.service.FinancialCalculatorService.BudgetRatio;

@WebMvcTest(FinancialCalculatorController.class)
@AutoConfigureMockMvc(addFilters = false)
class FinancialCalculatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FinancialCalculatorService calculatorService;

    @Test
    void savingsGoal_valid_invokesServiceAndExposesResult() throws Exception {
        when(calculatorService.monthsToReachGoal(any(), any())).thenReturn(new BigDecimal("4.00"));

        mockMvc.perform(post("/calculate-savings-goal")
                        .param("targetAmount", "1000")
                        .param("monthlySavings", "250"))
                .andExpect(view().name("financial-calculator"))
                .andExpect(model().attribute("savingsCalculated", true))
                .andExpect(model().attribute("monthsNeeded", new BigDecimal("4.00")));
    }

    @Test
    void savingsGoal_invalidTarget_showsErrorWithoutCallingService() throws Exception {
        mockMvc.perform(post("/calculate-savings-goal")
                        .param("targetAmount", "0")
                        .param("monthlySavings", "250"))
                .andExpect(view().name("financial-calculator"))
                .andExpect(model().attributeExists("savingsError"));

        verify(calculatorService, never()).monthsToReachGoal(any(), any());
    }

    @Test
    void compoundInterest_valid_exposesFutureValue() throws Exception {
        when(calculatorService.compoundInterestFutureValue(any(), any(), org.mockito.ArgumentMatchers.anyInt()))
                .thenReturn(new BigDecimal("1210.00"));

        mockMvc.perform(post("/calculate-compound-interest")
                        .param("principal", "1000")
                        .param("interestRate", "10")
                        .param("years", "2"))
                .andExpect(view().name("financial-calculator"))
                .andExpect(model().attribute("interestCalculated", true))
                .andExpect(model().attribute("futureValue", new BigDecimal("1210.00")));
    }

    @Test
    void budgetRatio_valid_exposesRatioAndRemaining() throws Exception {
        when(calculatorService.budgetRatio(any(), any()))
                .thenReturn(new BudgetRatio(new BigDecimal("25.00"), new BigDecimal("3000.00")));

        mockMvc.perform(post("/calculate-budget-ratio")
                        .param("monthlyIncome", "4000")
                        .param("monthlyExpenses", "1000"))
                .andExpect(view().name("financial-calculator"))
                .andExpect(model().attribute("budgetCalculated", true))
                .andExpect(model().attribute("expenseRatio", new BigDecimal("25.00")))
                .andExpect(model().attribute("remainingBalance", new BigDecimal("3000.00")));
    }
}
