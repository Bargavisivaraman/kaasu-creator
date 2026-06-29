package kaasu_creator.dao;

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

import kaasu_creator.model.Income;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(IncomeDao.class)
@Sql(scripts = "classpath:schema.sql")
class IncomeDaoTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private IncomeDao incomeDao;

    private Long userId;

    @BeforeEach
    void insertUser() {
        jdbc.update("INSERT INTO users (full_name, email, password) VALUES (?, ?, ?)",
                "Tester", "tester@example.com", "hash");
        userId = jdbc.queryForObject("SELECT id FROM users WHERE email = ?", Long.class,
                "tester@example.com");
    }

    private Income income(String type, String source, String amount) {
        return new Income(null, userId, type, source, new BigDecimal(amount),
                Date.valueOf("2026-01-15"), null);
    }

    @Test
    void save_defaultsBlankTypeToExtra() {
        incomeDao.save(income(null, "Gift", "100"));

        List<Income> all = incomeDao.findByUserId(userId);
        assertThat(all).hasSize(1);
        assertThat(all.get(0).getIncomeType()).isEqualTo("EXTRA");
    }

    @Test
    void sumByUserId_sumsAllIncome() {
        incomeDao.save(income("SALARY", "Job", "2000"));
        incomeDao.save(income("EXTRA", "Gift", "100"));

        assertThat(incomeDao.sumByUserId(userId)).isEqualByComparingTo("2100");
    }

    @Test
    void extraQueries_onlyReturnExtraType() {
        incomeDao.save(income("SALARY", "Job", "2000"));
        incomeDao.save(income("EXTRA", "Gift", "100"));

        assertThat(incomeDao.findExtraByUserId(userId)).hasSize(1);
        assertThat(incomeDao.sumExtraByUserId(userId)).isEqualByComparingTo("100");
    }
}
