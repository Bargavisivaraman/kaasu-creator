package kaasu_creator.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import kaasu_creator.service.FinancialCalculatorService.BudgetRatio;

class FinancialCalculatorServiceTest {

    private final FinancialCalculatorService service = new FinancialCalculatorService();

    // ── monthsToReachGoal ──────────────────────────────────────────────────

    @Test
    void monthsToReachGoal_dividesTargetByMonthly() {
        assertThat(service.monthsToReachGoal(new BigDecimal("1000"), new BigDecimal("250")))
                .isEqualByComparingTo("4.00");
    }

    @Test
    void monthsToReachGoal_roundsToTwoDecimals() {
        assertThat(service.monthsToReachGoal(new BigDecimal("1000"), new BigDecimal("300")))
                .isEqualByComparingTo("3.33");
    }

    @Test
    void monthsToReachGoal_rejectsNonPositiveInputs() {
        assertThatThrownBy(() -> service.monthsToReachGoal(BigDecimal.ZERO, new BigDecimal("10")))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.monthsToReachGoal(new BigDecimal("100"), BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── compoundInterestFutureValue ────────────────────────────────────────

    @Test
    void compoundInterest_computesFutureValue() {
        // 1000 at 10% for 2 years = 1000 * 1.1^2 = 1210.00
        assertThat(service.compoundInterestFutureValue(new BigDecimal("1000"), new BigDecimal("10"), 2))
                .isEqualByComparingTo("1210.00");
    }

    @Test
    void compoundInterest_zeroRateReturnsPrincipal() {
        assertThat(service.compoundInterestFutureValue(new BigDecimal("500"), BigDecimal.ZERO, 5))
                .isEqualByComparingTo("500.00");
    }

    @Test
    void compoundInterest_rejectsInvalidInputs() {
        assertThatThrownBy(() -> service.compoundInterestFutureValue(BigDecimal.ZERO, new BigDecimal("5"), 1))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.compoundInterestFutureValue(new BigDecimal("100"), new BigDecimal("150"), 1))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.compoundInterestFutureValue(new BigDecimal("100"), new BigDecimal("5"), 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── budgetRatio ────────────────────────────────────────────────────────

    @Test
    void budgetRatio_computesRatioAndRemaining() {
        BudgetRatio ratio = service.budgetRatio(new BigDecimal("4000"), new BigDecimal("1000"));
        assertThat(ratio.expenseRatioPercent()).isEqualByComparingTo("25.00");
        assertThat(ratio.remainingBalance()).isEqualByComparingTo("3000.00");
    }

    @Test
    void budgetRatio_allowsZeroExpenses() {
        BudgetRatio ratio = service.budgetRatio(new BigDecimal("2000"), BigDecimal.ZERO);
        assertThat(ratio.expenseRatioPercent()).isEqualByComparingTo("0.00");
        assertThat(ratio.remainingBalance()).isEqualByComparingTo("2000.00");
    }

    @Test
    void budgetRatio_rejectsInvalidInputs() {
        assertThatThrownBy(() -> service.budgetRatio(BigDecimal.ZERO, new BigDecimal("100")))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.budgetRatio(new BigDecimal("1000"), new BigDecimal("-1")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
