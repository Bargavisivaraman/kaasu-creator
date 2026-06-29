package kaasu_creator.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import kaasu_creator.dao.UserDao;
import kaasu_creator.model.User;

@ExtendWith(MockitoExtension.class)
class CurrentUserServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private CurrentUserService currentUserService;

    private Authentication authFor(String email) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(email);
        return authentication;
    }

    @Test
    void requireUserId_returnsId_whenUserExists() {
        when(userDao.findByEmail("me@example.com"))
                .thenReturn(Optional.of(new User(42L, "Me", "me@example.com", "hash", null)));

        assertThat(currentUserService.requireUserId(authFor("me@example.com"))).isEqualTo(42L);
    }

    @Test
    void requireUserId_throws_whenUserMissing() {
        when(userDao.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> currentUserService.requireUserId(authFor("ghost@example.com")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ghost@example.com");
    }

    @Test
    void requireUser_returnsUser_whenExists() {
        User user = new User(42L, "Me", "me@example.com", "hash", null);
        when(userDao.findByEmail("me@example.com")).thenReturn(Optional.of(user));

        assertThat(currentUserService.requireUser(authFor("me@example.com"))).isSameAs(user);
    }

    @Test
    void requireUser_throws_whenUserMissing() {
        when(userDao.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> currentUserService.requireUser(authFor("ghost@example.com")))
                .isInstanceOf(IllegalStateException.class);
    }
}
