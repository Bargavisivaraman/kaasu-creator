package kaasu_creator.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import kaasu_creator.model.User;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(UserDao.class)
@Sql(scripts = "classpath:schema.sql")
class UserDaoTest {

    @Autowired
    private UserDao userDao;

    @Test
    void saveAndFindByEmail() {
        userDao.save(new User(null, "Ada", "ada@example.com", "hash", null));

        assertThat(userDao.findByEmail("ada@example.com"))
                .isPresent()
                .hasValueSatisfying(u -> {
                    assertThat(u.getFullName()).isEqualTo("Ada");
                    assertThat(u.getId()).isNotNull();
                });
    }

    @Test
    void findByEmail_isEmptyWhenMissing() {
        assertThat(userDao.findByEmail("nobody@example.com")).isEmpty();
    }

    @Test
    void emailExists_reflectsPresence() {
        assertThat(userDao.emailExists("ada@example.com")).isFalse();
        userDao.save(new User(null, "Ada", "ada@example.com", "hash", null));
        assertThat(userDao.emailExists("ada@example.com")).isTrue();
    }

    @Test
    void deleteByEmail_removesUser() {
        userDao.save(new User(null, "Ada", "ada@example.com", "hash", null));
        userDao.deleteByEmail("ada@example.com");
        assertThat(userDao.findByEmail("ada@example.com")).isEmpty();
    }

    @Test
    void deleteById_removesUser() {
        userDao.save(new User(null, "Ada", "ada@example.com", "hash", null));
        Long id = userDao.findByEmail("ada@example.com").orElseThrow().getId();
        userDao.deleteById(id);
        assertThat(userDao.findByEmail("ada@example.com")).isEmpty();
    }
}
