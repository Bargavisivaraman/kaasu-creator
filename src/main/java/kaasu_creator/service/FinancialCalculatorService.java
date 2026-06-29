package kaasu_creator.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

/**
 * FinancialCalculatorService holds the pure financial math used by the
 * calculator pages. Keeping it out of the controller makes each formula
 * independently unit-testable.
 */
@Service
public class FinancialCalculatorService {

    /** Result of a budget-ratio calculation. */
    public record BudgetRatio(BigDecimal expenseRatioPercent, BigDecimal remainingBalance) {}

    /**
     * Number of months needed to reach a savings target at a fixed monthly rate.
     */
    public BigDecimal monthsToReachGoal(BigDecimal targetAmount, BigDecimal monthlySavings) {
        if (targetAmount == null || monthlySavings == null
                || targetAmount.compareTo(BigDecimal.ZERO) <= 0
                || monthlySavings.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Target amount and monthly savings must be greater than zero");
        }
        return targetAmount.divide(monthlySavings, 2, RoundingMode.HALF_UP);
    }

    /**
     * Future value with annually compounded interest: A = P(1 + r)^t.
     *
     * @param annualRatePercent the rate as a percentage (for example 5 for 5%)
     */
    public BigDecimal compoundInterestFutureValue(BigDecimal principal, BigDecimal annualRatePercent, int years) {
        if (principal == null || principal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Principal must be greater than zero");
        }
        if (annualRatePercent == null
                || annualRatePercent.compareTo(BigDecimal.ZERO) < 0
                || annualRatePercent.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Interest rate must be between 0% and 100%");
        }
        if (years <= 0) {
            throw new IllegalArgumentException("Number of years must be greater than zero");
        }
        BigDecimal rate = annualRatePercent.divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP);
        BigDecimal futureValue = principal.multiply(BigDecimal.ONE.add(rate).pow(years));
        return futureValue.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Expense-to-income ratio (as a percentage) and the remaining balance.
     */
    public BudgetRatio budgetRatio(BigDecimal monthlyIncome, BigDecimal monthlyExpenses) {
        if (monthlyIncome == null || monthlyIncome.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Monthly income must be greater than zero");
        }
        if (monthlyExpenses == null || monthlyExpenses.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Monthly expenses cannot be negative");
        }
        BigDecimal expenseRatio = monthlyExpenses
                .divide(monthlyIncome, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal remaining = monthlyIncome.subtract(monthlyExpenses).setScale(2, RoundingMode.HALF_UP);
        return new BudgetRatio(expenseRatio, remaining);
    }
}
