package kaasu_creator.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import kaasu_creator.model.User;
import kaasu_creator.service.CurrentUserService;

/**
 * ProfileController - displays user profile information.
 *
 * Shows the logged-in user's information including their name and email.
 */
@Controller
public class ProfileController {

    private final CurrentUserService currentUserService;

    public ProfileController(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    @GetMapping("/profile")
    public String showProfile(Authentication authentication, Model model) {
        User user = currentUserService.requireUser(authentication);

        String fullName = user.getFullName();
        String displayName = (fullName != null && !fullName.trim().isEmpty())
                ? fullName
                : user.getEmail();

        model.addAttribute("userName", displayName);
        model.addAttribute("userEmail", user.getEmail());

        return "profile";
    }
}
