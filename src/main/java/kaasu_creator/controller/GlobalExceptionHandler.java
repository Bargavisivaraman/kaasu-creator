package kaasu_creator.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

/**
 * GlobalExceptionHandler logs uncaught exceptions and renders a friendly
 * error page instead of leaking a raw stacktrace to the user.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public String handleUncaught(Exception ex, HttpServletRequest request, Model model) {
        log.error("Unhandled exception on {} {}", request.getMethod(), request.getRequestURI(), ex);
        model.addAttribute("path", request.getRequestURI());
        return "error";
    }
}
