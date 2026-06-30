package kaasu_creator.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import kaasu_creator.model.TimesheetEntry;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TimesheetRepository.class)
@Sql(scripts = "classpath:schema.sql")
class TimesheetRepositoryTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private TimesheetRepository repository;

    private Long userId;
    private Long jobId;

    @BeforeEach
    void insertUserAndJob() {
        jdbc.update("INSERT INTO users (full_name, email, password) VALUES (?, ?, ?)",
                "Tester", "tester@example.com", "hash");
        userId = jdbc.queryForObject("SELECT id FROM users WHERE email = ?", Long.class,
                "tester@example.com");
        jdbc.update("INSERT INTO jobs (user_id, job_name, hourly_wage, notes) VALUES (?, ?, ?, ?)",
                userId, "Barista", new BigDecimal("20.00"), "");
        jobId = jdbc.queryForObject("SELECT id FROM jobs WHERE user_id = ?", Long.class, userId);
    }

    private void saveEntry(String hours) {
        repository.save(new TimesheetEntry(null, userId, jobId, Date.valueOf("2026-02-01"),
                new BigDecimal(hours), "shift", null));
    }

    @Test
    void saveAndFindByUserId_enrichesWithJobData() {
        saveEntry("5.00");

        List<TimesheetEntry> entries = repository.findByUserId(userId);
        assertThat(entries).hasSize(1);
        assertThat(entries.get(0).getJobName()).isEqualTo("Barista");
        assertThat(entries.get(0).getEarnedAmount()).isEqualByComparingTo("100.00");
    }

    @Test
    void sums_aggregateHoursAndEarnings() {
        saveEntry("5.00");
        saveEntry("2.50");

        assertThat(repository.sumHoursWorkedByUserId(userId)).isEqualByComparingTo("7.50");
        // (5.0 + 2.5) hours * 20.00 wage = 150.00
        assertThat(repository.sumEarnedAmountByUserId(userId)).isEqualByComparingTo("150.00");
    }

    @Test
    void deleteByIdAndUserId_respectsOwnership() {
        saveEntry("5.00");
        Long entryId = repository.findByUserId(userId).get(0).getId();

        assertThat(repository.deleteByIdAndUserId(entryId, userId + 999)).isZero();
        assertThat(repository.deleteByIdAndUserId(entryId, userId)).isEqualTo(1);
        assertThat(repository.findByUserId(userId)).isEmpty();
    }

    @Test
    void findByUserId_keepsEntriesAfterTheirJobIsDeleted() {
        saveEntry("5.00");
        // Simulate the ON DELETE SET NULL behaviour when a job is removed
        jdbc.update("UPDATE timesheet_entries SET job_id = NULL WHERE user_id = ?", userId);

        List<TimesheetEntry> entries = repository.findByUserId(userId);
        assertThat(entries).hasSize(1);
        assertThat(entries.get(0).getJobName()).isNull();
        // Hours are still tracked even though earnings can no longer be computed
        assertThat(repository.sumHoursWorkedByUserId(userId)).isEqualByComparingTo("5.00");
    }

    @Test
    void findJobSummariesByUserId_groupsByJob() {
        saveEntry("5.00");
        saveEntry("3.00");

        var summaries = repository.findJobSummariesByUserId(userId);
        assertThat(summaries).hasSize(1);
        assertThat(summaries.get(0).getTotalHours()).isEqualByComparingTo("8.00");
        assertThat(summaries.get(0).getTotalEarned()).isEqualByComparingTo("160.00");
    }
}
