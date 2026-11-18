package com.example.seleniumportal;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.seleniumportal.model.PortalUser;
import com.example.seleniumportal.repository.PortalUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class PortalRegistrationLoginTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PortalUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void registrationStoresHashedPasswordAndAcceptsValidLogin() throws Exception {
        String username = "jane-" + System.nanoTime();
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("fullName", "Jane Example")
                        .param("username", username)
                        .param("password", "Selenium123")
                        .param("confirmPassword", "Selenium123")
                        .param("dob", "2000-01-15")
                        .param("gender", "Female")
                        .param("department", "CSE")
                        .param("section", "A")
                        .param("receiveUpdates", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register?success"));

        PortalUser savedUser = userRepository.findByUsername(username).orElseThrow();
        org.junit.jupiter.api.Assertions.assertNotEquals("Selenium123", savedUser.getPassword());
        org.junit.jupiter.api.Assertions.assertTrue(passwordEncoder.matches("Selenium123", savedUser.getPassword()));

        mockMvc.perform(post("/login").with(csrf())
                        .param("username", username)
                        .param("password", "Selenium123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    void registrationRejectsMismatchedPasswordsAndDuplicateUsername() throws Exception {
        String username = "unique-" + System.nanoTime();
        userRepository.saveAndFlush(new PortalUser(
                "Existing User", username, passwordEncoder.encode("Selenium123"),
                java.time.LocalDate.of(2000, 1, 15), "Other", "IT", "B", false));

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("fullName", "New User")
                        .param("username", username)
                        .param("password", "Selenium123")
                        .param("confirmPassword", "Different123")
                        .param("dob", "2000-01-15")
                        .param("gender", "Male")
                        .param("department", "CSE")
                        .param("section", "A")
                        .param("receiveUpdates", "false"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Passwords do not match.")));

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("fullName", "New User")
                        .param("username", username)
                        .param("password", "Selenium123")
                        .param("confirmPassword", "Selenium123")
                        .param("dob", "2000-01-15")
                        .param("gender", "Male")
                        .param("department", "CSE")
                        .param("section", "A")
                        .param("receiveUpdates", "false"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("That username is already taken.")));
    }

    @Test
    void invalidLoginShowsErrorAndDashboardRequiresAuthentication() throws Exception {
        mockMvc.perform(post("/login").with(csrf())
                        .param("username", "not-a-user")
                        .param("password", "wrong-password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));

        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

}
