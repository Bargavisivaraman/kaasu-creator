package kaasu_creator.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import kaasu_creator.dao.ExpenseDao;
import kaasu_creator.model.Expense;

/**
 * BudgetService handles expense tracking for users.
 *
 * Key concept: BigDecimal for money
 * We use BigDecimal instead of double for money calculations.
 * Why? Because double can have rounding errors (e.g., 0.1 + 0.2 = 0.30000000000000004)
 * BigDecimal gives us precise decimal arithmetic for financial calculations.
 */
@Service
public class BudgetService {

    private final ExpenseDao expenseDao;

    public BudgetService(ExpenseDao expenseDao) {
        this.expenseDao = expenseDao;
    }

    /**
     * Add a new expense for a user.
     */
    public void addExpense(Long userId, String title, String category, BigDecimal amount) {
        Expense expense = new Expense(null, userId, title, category, amount, null);
        expenseDao.save(expense);
    }

    /**
     * Get all expenses for a user.
     */
    public List<Expense> getExpensesByUser(Long userId) {
        return expenseDao.findByUserId(userId);
    }

    /**
     * Calculate total expenses for a user.
     */
    public BigDecimal getTotalExpenses(Long userId) {
        return expenseDao.sumByUserId(userId);
    }

    /**
     * Delete an expense by ID.
     */
    public void deleteExpense(Long expenseId) {
        expenseDao.deleteById(expenseId);
    }
}