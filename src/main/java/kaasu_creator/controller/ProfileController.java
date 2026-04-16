package kaasu_creator.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import kaasu_creator.dao.UserDao;

/**
 * ProfileController - displays user profile information.
 *
 * Shows the logged-in user's information including their name and email.
 */
@Controller
public class ProfileController {

    private final UserDao userDao;

    public ProfileController(UserDao userDao) {
        this.userDao = userDao;
    }

    @GetMapping("/profile")
    public String showProfile(Authentication authentication, Model model) {
        // Get the full user object to access all user information
        String email = authentication.getName();
        String displayName = email; // fallback to email

        var userOptional = userDao.findByEmail(email);
        if (userOptional.isPresent()) {
            var user = userOptional.get();
            String fullName = user.getFullName();
            if (fullName != null && !fullName.trim().isEmpty()) {
                displayName = fullName;
            }
        }

        model.addAttribute("userName", displayName);
        model.addAttribute("userEmail", email);

        return "profile";
    }
}