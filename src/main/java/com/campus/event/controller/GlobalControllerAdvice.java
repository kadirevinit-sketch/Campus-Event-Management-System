package com.campus.event.controller;

import com.campus.event.model.User;
import com.campus.event.service.UserService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Optional;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final UserService userService;

    public GlobalControllerAdvice(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        Optional<User> currentUserOpt = userService.getCurrentUser();
        if (currentUserOpt.isPresent()) {
            User user = currentUserOpt.get();
            model.addAttribute("currentUser", user);
            model.addAttribute("isAdmin", user.isAdmin());
            model.addAttribute("isStudent", "ROLE_STUDENT".equalsIgnoreCase(user.getRole()));
            model.addAttribute("userFullName", user.getFullName());
            model.addAttribute("userEmail", user.getEmail());
            model.addAttribute("userRole", user.getRole());
        } else {
            model.addAttribute("currentUser", null);
            model.addAttribute("isAdmin", false);
            model.addAttribute("isStudent", false);
            model.addAttribute("userFullName", null);
            model.addAttribute("userEmail", null);
            model.addAttribute("userRole", null);
        }
    }
}
