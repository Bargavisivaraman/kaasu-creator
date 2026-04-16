package kaasu_creator.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import kaasu_creator.model.Income;

@Repository
public class IncomeDao {

    private static final String EXTRA_INCOME_TYPE = "EXTRA";

    private final JdbcTemplate jdbc;

    public IncomeDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // RowMapper converts a database row into an Income object
    @SuppressWarnings("null")
    private final RowMapper<Income> incomeRowMapper = (rs, rowNum) -> new Income(
        rs.getLong("id"),
        rs.getLong("user_id"),
        rs.getString("income_type"),
        rs.getString("source"),
        rs.getBigDecimal("amount"),
        rs.getDate("income_date"),
        rs.getString("notes")
    );

    // INSERT a new income
public void save(Income income) {
    String sql = "INSERT INTO incomes (user_id, income_type, source, amount, income_date, notes) VALUES (?, ?, ?, ?, ?, ?)";
    String incomeType = (income.getIncomeType() == null || income.getIncomeType().isBlank())
            ? EXTRA_INCOME_TYPE
            : income.getIncomeType().trim();

    jdbc.update(sql,
        income.getUserId(),
        incomeType,
        income.getSource(),
        income.getAmount(),
        income.getIncomeDate(),
        income.getNotes()
    );
}

    // FIND all incomes for a specific user, ordered by income_date (newest first)
    @SuppressWarnings("null")
    public List<Income> findByUserId(Long userId) {
        String sql = "SELECT * FROM incomes WHERE user_id = ? ORDER BY income_date DESC";
        try {
            return jdbc.query(sql, incomeRowMapper, userId);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // SUM all incomes for a specific user
    public BigDecimal sumByUserId(Long userId) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM incomes WHERE user_id = ?";
        try {
            return jdbc.queryForObject(sql, BigDecimal.class, userId);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    // FIND extra incomes for a specific user
    @SuppressWarnings("null")
    public List<Income> findExtraByUserId(Long userId) {
        String sql = "SELECT * FROM incomes WHERE user_id = ? AND income_type = ? ORDER BY income_date DESC";
        try {
            return jdbc.query(sql, incomeRowMapper, userId, EXTRA_INCOME_TYPE);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // SUM extra incomes for a specific user
    public BigDecimal sumExtraByUserId(Long userId) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM incomes WHERE user_id = ? AND income_type = ?";
        try {
            return jdbc.queryForObject(sql, BigDecimal.class, userId, EXTRA_INCOME_TYPE);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
}