package com.campus.event.controller;

import com.campus.event.dto.StudentRegisterDto;
import com.campus.event.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        if (!model.containsAttribute("registerDto")) {
            model.addAttribute("registerDto", new StudentRegisterDto());
        }
        return "register";
    }

    @PostMapping("/register")
    public String registerStudent(@Valid @ModelAttribute("registerDto") StudentRegisterDto registerDto,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        if (!registerDto.isPasswordMatching()) {
            bindingResult.rejectValue("confirmPassword", "error.registerDto", "Passwords do not match");
        }

        if (userService.emailExists(registerDto.getEmail())) {
            bindingResult.rejectValue("email", "error.registerDto", "An account with this email address already exists");
        }

        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            userService.registerStudent(registerDto);
            redirectAttributes.addFlashAttribute("successMessage", "Account created successfully! Please log in with your credentials.");
            return "redirect:/login";
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "register";
        }
    }
}
