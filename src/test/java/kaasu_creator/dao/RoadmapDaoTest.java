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

import kaasu_creator.model.Roadmap;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(RoadmapDao.class)
@Sql(scripts = "classpath:schema.sql")
class RoadmapDaoTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private RoadmapDao roadmapDao;

    private Long goalId;

    @BeforeEach
    void insertUserAndGoal() {
        jdbc.update("INSERT INTO users (full_name, email, password) VALUES (?, ?, ?)",
                "Tester", "tester@example.com", "hash");
        Long userId = jdbc.queryForObject("SELECT id FROM users WHERE email = ?", Long.class,
                "tester@example.com");
        jdbc.update("INSERT INTO goals (user_id, name, target_amount, current_amount, deadline) VALUES (?, ?, ?, ?, ?)",
                userId, "Laptop", new BigDecimal("1000"), BigDecimal.ZERO, LocalDate.now().plusWeeks(8));
        goalId = jdbc.queryForObject("SELECT id FROM goals WHERE user_id = ?", Long.class, userId);
    }

    @Test
    void saveAndFindByGoalId() {
        roadmapDao.save(new Roadmap(null, goalId, 1, new BigDecimal("125"), "pending"));

        List<Roadmap> roadmaps = roadmapDao.findByGoalId(goalId);
        assertThat(roadmaps).hasSize(1);
        assertThat(roadmaps.get(0).getWeekNumber()).isEqualTo(1);
    }

    @Test
    void saveAll_persistsAllOrderedByWeek() {
        roadmapDao.saveAll(List.of(
                new Roadmap(null, goalId, 2, new BigDecimal("125"), "pending"),
                new Roadmap(null, goalId, 1, new BigDecimal("125"), "pending")));

        List<Roadmap> roadmaps = roadmapDao.findByGoalId(goalId);
        assertThat(roadmaps).hasSize(2);
        assertThat(roadmaps.get(0).getWeekNumber()).isEqualTo(1);
        assertThat(roadmaps.get(1).getWeekNumber()).isEqualTo(2);
    }

    @Test
    void updateStatus_changesStatus() {
        roadmapDao.save(new Roadmap(null, goalId, 1, new BigDecimal("125"), "pending"));
        Long roadmapId = roadmapDao.findByGoalId(goalId).get(0).getId();

        roadmapDao.updateStatus(roadmapId, "completed");

        assertThat(roadmapDao.findByGoalId(goalId).get(0).getStatus()).isEqualTo("completed");
    }
}
