package kaasu_creator.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kaasu_creator.service.AuthService;

/**
 * AuthController - handles login and registration.
 *
 * Key concept: With Spring Security's formLogin, we actually don't need a POST
 * handler for login at all - Spring Security intercepts the form and handles it!
 * But we do need:
 * - GET /login (shows the login page)
 * - GET /register (shows registration page)
 * - POST /register (processes registration)
 *
 * Note: For registration, we can't use Spring Security's built-in registration
 * because it expects a UserDetailsService and User object. We handle registration
 * manually via our AuthService.
 */
@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginPage(@RequestParam(required = false) String logout,
                                @RequestParam(required = false) String error,
                                Model model) {
        if (logout != null) {
            model.addAttribute("logout", "You have been logged out successfully.");
        }
        if (error != null) {
            model.addAttribute("error", "Invalid email or password");
        }
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String fullName,
                               @RequestParam String email,
                               @RequestParam String password,
                               RedirectAttributes redirectAttributes) {
        try {
            authService.register(fullName, email, password);
            redirectAttributes.addFlashAttribute("success", "Account created! Please log in.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }
}