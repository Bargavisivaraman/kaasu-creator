package kaasu_creator.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import kaasu_creator.dao.UserDao;
import kaasu_creator.model.User;

/**
 * CurrentUserService resolves the database id of the authenticated user.
 *
 * This logic was previously duplicated as a private getUserId helper in
 * several controllers; centralizing it keeps the controllers thin and gives
 * the lookup a single, testable home.
 */
@Service
public class CurrentUserService {

    private final UserDao userDao;

    public CurrentUserService(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Resolve the id of the currently authenticated user.
     *
     * @throws IllegalStateException if no user matches the authentication name
     *         (should not happen for an authenticated request).
     */
    public Long requireUserId(Authentication authentication) {
        return userDao.findByEmail(authentication.getName())
                .map(User::getId)
                .orElseThrow(() -> new IllegalStateException(
                        "Authenticated user not found: " + authentication.getName()));
    }
}
