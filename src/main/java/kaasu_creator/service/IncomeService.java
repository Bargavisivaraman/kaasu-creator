package kaasu_creator.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import kaasu_creator.dao.IncomeDao;
import kaasu_creator.model.Income;

/**
 * IncomeService handles income tracking for users.
 *
 * Key concept: BigDecimal for money
 * We use BigDecimal instead of double for money calculations.
 * Why? Because double can have rounding errors (e.g., 0.1 + 0.2 = 0.30000000000000004)
 * BigDecimal gives us precise decimal arithmetic for financial calculations.
 */
@Service
public class IncomeService {

    private final IncomeDao incomeDao;

    public IncomeService(IncomeDao incomeDao) {
        this.incomeDao = incomeDao;
    }

    /**
     * Add a new income entry for a user.
     */
    public void addIncome(Income income) {
        incomeDao.save(income);
    }

    /**
     * Get all incomes for a user.
     */
    public List<Income> getIncomesByUser(Long userId) {
        return incomeDao.findByUserId(userId);
    }

    /**
     * Get all extra income entries for a user.
     */
    public List<Income> getExtraIncomesByUser(Long userId) {
        return incomeDao.findExtraByUserId(userId);
    }

    /**
     * Calculate total incomes for a user.
     */
    public BigDecimal getTotalIncome(Long userId) {
        return incomeDao.sumByUserId(userId);
    }

    /**
     * Calculate total extra income for a user.
     */
    public BigDecimal getTotalExtraIncome(Long userId) {
        return incomeDao.sumExtraByUserId(userId);
    }
}