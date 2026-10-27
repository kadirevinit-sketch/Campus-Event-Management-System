package com.campus.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityAndControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Public endpoint: Homepage loads successfully")
    void testHomepageLoads() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    @DisplayName("Public endpoint: Browse events page loads successfully")
    void testEventsPageLoads() throws Exception {
        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(view().name("events"));
    }

    @Test
    @DisplayName("Security: Anonymous user is redirected to login when accessing admin dashboard")
    void testAnonymousAccessToAdminRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("Security: Student user receives 403 Forbidden when accessing admin dashboard")
    @WithMockUser(username = "student@campus.edu", roles = {"STUDENT"})
    void testStudentAccessToAdminForbidden() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Security: Administrator user can access admin dashboard")
    @WithMockUser(username = "admin@campus.edu", roles = {"ADMIN"})
    void testAdminAccessToDashboardSuccess() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"));
    }

    @Test
    @DisplayName("Student registration: Valid submission creates account and redirects to login")
    void testStudentRegistrationSuccess() throws Exception {
        String uniqueEmail = "new.student." + System.currentTimeMillis() + "@campus.edu";

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("fullName", "New College Student")
                        .param("email", uniqueEmail)
                        .param("password", "SecretPass123")
                        .param("confirmPassword", "SecretPass123")
                        .param("department", "Computer Science & Engineering")
                        .param("academicYear", "1st Year (Freshman)"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @DisplayName("Student registration: Password mismatch shows validation error")
    void testStudentRegistrationPasswordMismatch() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("fullName", "Mismatch Student")
                        .param("email", "mismatch@campus.edu")
                        .param("password", "Pass123")
                        .param("confirmPassword", "DifferentPass123")
                        .param("department", "Physics")
                        .param("academicYear", "1st Year"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().hasErrors());
    }
}
