package kaasu_creator.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import kaasu_creator.model.Goal;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(GoalDao.class)
@Sql(scripts = "classpath:schema.sql")
class GoalDaoTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private GoalDao goalDao;

    private Long userId;

    @BeforeEach
    void insertUser() {
        jdbc.update("INSERT INTO users (full_name, email, password) VALUES (?, ?, ?)",
                "Tester", "tester@example.com", "hash");
        userId = jdbc.queryForObject("SELECT id FROM users WHERE email = ?", Long.class,
                "tester@example.com");
    }

    private Goal newGoal() {
        return new Goal(null, userId, "Laptop", new BigDecimal("1000"),
                BigDecimal.ZERO, LocalDate.now().plusWeeks(10), null);
    }

    @Test
    void saveAndFindByUserId() {
        goalDao.save(newGoal());

        List<Goal> goals = goalDao.findByUserId(userId);
        assertThat(goals).hasSize(1);
        assertThat(goals.get(0).getName()).isEqualTo("Laptop");
        assertThat(goals.get(0).getDeadline()).isNotNull();
    }

    @Test
    void addToCurrentAmount_incrementsSavings() {
        goalDao.save(newGoal());
        Long goalId = goalDao.findByUserId(userId).get(0).getId();

        goalDao.addToCurrentAmount(goalId, new BigDecimal("150"));

        assertThat(goalDao.findById(goalId).getCurrentAmount()).isEqualByComparingTo("150");
    }

    @Test
    void updateCurrentAmount_setsAbsoluteValue() {
        goalDao.save(newGoal());
        Long goalId = goalDao.findByUserId(userId).get(0).getId();

        goalDao.updateCurrentAmount(goalId, new BigDecimal("500"));

        assertThat(goalDao.findById(goalId).getCurrentAmount()).isEqualByComparingTo("500");
    }
}
