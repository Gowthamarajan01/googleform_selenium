package com.example.seleniumportal.web;

import com.example.seleniumportal.model.PortalUser;
import com.example.seleniumportal.repository.PortalUserRepository;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PortalController {

    private final PortalUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PortalController(PortalUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registrationForm", new RegistrationForm());
        return "register";
    }

    @PostMapping("/register")
    public String submitRegistration(
            @Valid @ModelAttribute("registrationForm") RegistrationForm form,
            BindingResult bindingResult) {
        if (!bindingResult.hasFieldErrors("password")
                && !bindingResult.hasFieldErrors("confirmPassword")
                && form.getPassword() != null
                && !form.getPassword().equals(form.getConfirmPassword())) {
            bindingResult.addError(new FieldError(
                    "registrationForm", "confirmPassword", "Passwords do not match."));
        }

        if (!bindingResult.hasFieldErrors("username")
                && form.getUsername() != null
                && userRepository.existsByUsernameIgnoreCase(form.getUsername())) {
            bindingResult.addError(new FieldError(
                    "registrationForm", "username", "That username is already taken."));
        }

        if (bindingResult.hasErrors()) {
            return "register";
        }

        PortalUser user = new PortalUser(
                normalizeOptional(form.getFullName()),
                form.getUsername().trim(),
                passwordEncoder.encode(form.getPassword()),
                form.getDob(),
                normalizeOptional(form.getGender()),
                normalizeOptional(form.getDepartment()),
                normalizeOptional(form.getSection()),
                form.isReceiveUpdates());
        try {
            userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException exception) {
            bindingResult.addError(new FieldError(
                    "registrationForm", "username", "That username is already taken."));
            return "register";
        }
        return "redirect:/register?success";
    }

    private String normalizeOptional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        PortalUser user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user was not found."));
        model.addAttribute("portalUser", user);
        return "dashboard";
    }
}
