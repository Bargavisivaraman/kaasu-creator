package kaasu_creator.dao;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import kaasu_creator.model.Goal;

@Repository
public class GoalDao {

    private final JdbcTemplate jdbc;

    public GoalDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // RowMapper converts a database row into a Goal object
    private final RowMapper<Goal> goalRowMapper = (rs, rowNum) -> new Goal(
        rs.getLong("id"),
        rs.getLong("user_id"),
        rs.getString("name"),
        rs.getBigDecimal("target_amount"),
        rs.getBigDecimal("current_amount"),
        rs.getDate("deadline").toLocalDate(),
        rs.getTimestamp("created_at")
    );

    // INSERT a new goal
    public void save(Goal goal) {
        String sql = "INSERT INTO goals (user_id, name, target_amount, current_amount, deadline) VALUES (?, ?, ?, ?, ?)";
        jdbc.update(sql, goal.getUserId(), goal.getName(), goal.getTargetAmount(), goal.getCurrentAmount(), goal.getDeadline());
    }

    // FIND all goals for a specific user
    @SuppressWarnings("unchecked")
    public List<Goal> findByUserId(Long userId) {
        String sql = "SELECT * FROM goals WHERE user_id = ? ORDER BY created_at DESC";
        return jdbc.query(sql, goalRowMapper, userId);
    }

    // FIND a single goal by ID
    @SuppressWarnings("unchecked")
    public Goal findById(Long id) {
        String sql = "SELECT * FROM goals WHERE id = ?";
        return jdbc.query(sql, goalRowMapper, id).stream().findFirst().orElse(null);
    }

    // UPDATE the current_amount of a goal (when user adds savings)
    public void updateCurrentAmount(Long goalId, java.math.BigDecimal newAmount) {
        String sql = "UPDATE goals SET current_amount = ? WHERE id = ?";
        jdbc.update(sql, newAmount, goalId);
    }

    // UPDATE the current_amount incrementally
    public void addToCurrentAmount(Long goalId, java.math.BigDecimal amountToAdd) {
        String sql = "UPDATE goals SET current_amount = current_amount + ? WHERE id = ?";
        jdbc.update(sql, amountToAdd, goalId);
    }
}