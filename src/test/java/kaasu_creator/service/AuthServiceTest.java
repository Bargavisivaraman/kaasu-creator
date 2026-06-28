package kaasu_creator.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import kaasu_creator.dao.UserDao;
import kaasu_creator.model.User;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_hashesPasswordAndSaves_whenEmailIsNew() {
        when(userDao.emailExists("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("hashed-secret");

        authService.register("New User", "new@example.com", "secret");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userDao).save(captor.capture());
        User saved = captor.getValue();
        assertThat(saved.getFullName()).isEqualTo("New User");
        assertThat(saved.getEmail()).isEqualTo("new@example.com");
        assertThat(saved.getPassword()).isEqualTo("hashed-secret");
    }

    @Test
    void register_throwsAndDoesNotSave_whenEmailExists() {
        when(userDao.emailExists("taken@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register("X", "taken@example.com", "pw"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("already registered");

        verify(userDao, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void authenticate_returnsUser_whenPasswordMatches() {
        User user = new User(1L, "Me", "me@example.com", "hash", null);
        when(userDao.findByEmail("me@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pw", "hash")).thenReturn(true);

        assertThat(authService.authenticate("me@example.com", "pw")).isSameAs(user);
    }

    @Test
    void authenticate_returnsNull_whenPasswordDoesNotMatch() {
        User user = new User(1L, "Me", "me@example.com", "hash", null);
        when(userDao.findByEmail("me@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hash")).thenReturn(false);

        assertThat(authService.authenticate("me@example.com", "wrong")).isNull();
    }

    @Test
    void authenticate_returnsNull_whenUserNotFound() {
        when(userDao.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThat(authService.authenticate("missing@example.com", "pw")).isNull();
    }
}
