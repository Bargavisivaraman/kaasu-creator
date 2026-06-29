package kaasu_creator.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import kaasu_creator.model.Job;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JobRepository.class)
@Sql(scripts = "classpath:schema.sql")
class JobRepositoryTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private JobRepository jobRepository;

    private Long userId;

    @BeforeEach
    void insertUser() {
        jdbc.update("INSERT INTO users (full_name, email, password) VALUES (?, ?, ?)",
                "Tester", "tester@example.com", "hash");
        userId = jdbc.queryForObject("SELECT id FROM users WHERE email = ?", Long.class,
                "tester@example.com");
    }

    private Long saveJobAndGetId() {
        jobRepository.save(new Job(null, userId, "Barista", new BigDecimal("15.00"), "weekends", null));
        return jobRepository.findByUserId(userId).get(0).getId();
    }

    @Test
    void saveAndFindByUserId() {
        saveJobAndGetId();
        List<Job> jobs = jobRepository.findByUserId(userId);
        assertThat(jobs).hasSize(1);
        assertThat(jobs.get(0).getJobName()).isEqualTo("Barista");
    }

    @Test
    void findByIdAndUserId_respectsOwnership() {
        Long jobId = saveJobAndGetId();
        assertThat(jobRepository.findByIdAndUserId(jobId, userId)).isPresent();
        assertThat(jobRepository.findByIdAndUserId(jobId, userId + 999)).isEmpty();
    }

    @Test
    void update_onlyAffectsOwnedRow() {
        Long jobId = saveJobAndGetId();

        // Wrong user: no change
        jobRepository.update(new Job(jobId, userId + 999, "Hacked", new BigDecimal("99"), "", null));
        assertThat(jobRepository.findByIdAndUserId(jobId, userId).orElseThrow().getJobName())
                .isEqualTo("Barista");

        // Owner: updated
        jobRepository.update(new Job(jobId, userId, "Senior Barista", new BigDecimal("20"), "", null));
        assertThat(jobRepository.findByIdAndUserId(jobId, userId).orElseThrow().getJobName())
                .isEqualTo("Senior Barista");
    }

    @Test
    void deleteByIdAndUserId_respectsOwnership() {
        Long jobId = saveJobAndGetId();
        assertThat(jobRepository.deleteByIdAndUserId(jobId, userId + 999)).isZero();
        assertThat(jobRepository.deleteByIdAndUserId(jobId, userId)).isEqualTo(1);
        assertThat(jobRepository.findByUserId(userId)).isEmpty();
    }

    @Test
    void findWithSummaryByUserId_returnsZeroTotalsForJobWithoutEntries() {
        saveJobAndGetId();
        List<Job> jobs = jobRepository.findWithSummaryByUserId(userId);
        assertThat(jobs).hasSize(1);
        assertThat(jobs.get(0).getTotalHours()).isEqualByComparingTo("0");
        assertThat(jobs.get(0).getTotalEarned()).isEqualByComparingTo("0");
    }
}
