package kaasu_creator.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import kaasu_creator.model.Expense;

/**
 * Data-layer tests for ExpenseDao against an in-memory H2 database loaded
 * with the production schema.
 */
@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(ExpenseDao.class)
@Sql(scripts = "classpath:schema.sql")
class ExpenseDaoTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private ExpenseDao expenseDao;

    private Long userId;

    @BeforeEach
    void insertUser() {
        jdbc.update("INSERT INTO users (full_name, email, password) VALUES (?, ?, ?)",
                "Tester", "tester@example.com", "hash");
        userId = jdbc.queryForObject("SELECT id FROM users WHERE email = ?", Long.class,
                "tester@example.com");
    }

    @Test
    void saveAndFindByUserId() {
        expenseDao.save(new Expense(null, userId, "Coffee", "Food", new BigDecimal("4.50"), null));

        List<Expense> expenses = expenseDao.findByUserId(userId);
        assertThat(expenses).hasSize(1);
        assertThat(expenses.get(0).getTitle()).isEqualTo("Coffee");
        assertThat(expenses.get(0).getAmount()).isEqualByComparingTo("4.50");
    }

    @Test
    void sumByUserId_addsAmounts() {
        expenseDao.save(new Expense(null, userId, "A", "Food", new BigDecimal("4.50"), null));
        expenseDao.save(new Expense(null, userId, "B", "Rent", new BigDecimal("900.00"), null));

        assertThat(expenseDao.sumByUserId(userId)).isEqualByComparingTo("904.50");
    }

    @Test
    void sumByUserId_isZeroWhenNoExpenses() {
        assertThat(expenseDao.sumByUserId(userId)).isEqualByComparingTo("0");
    }

    @Test
    void deleteByIdAndUserId_onlyDeletesOwnedRow() {
        expenseDao.save(new Expense(null, userId, "A", "Food", new BigDecimal("4.50"), null));
        Long expenseId = expenseDao.findByUserId(userId).get(0).getId();

        // Wrong user cannot delete
        assertThat(expenseDao.deleteByIdAndUserId(expenseId, userId + 999)).isZero();
        assertThat(expenseDao.findByUserId(userId)).hasSize(1);

        // Owner can delete
        assertThat(expenseDao.deleteByIdAndUserId(expenseId, userId)).isEqualTo(1);
        assertThat(expenseDao.findByUserId(userId)).isEmpty();
    }
}
