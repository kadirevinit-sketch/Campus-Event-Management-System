package com.campus.event.controller;

import com.campus.event.model.Registration;
import com.campus.event.model.User;
import com.campus.event.service.RegistrationService;
import com.campus.event.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class RegistrationController {

    private final RegistrationService registrationService;
    private final UserService userService;

    public RegistrationController(RegistrationService registrationService, UserService userService) {
        this.registrationService = registrationService;
        this.userService = userService;
    }

    @PostMapping("/events/{id}/register")
    public String registerForEvent(@PathVariable("id") Long eventId,
                                   RedirectAttributes redirectAttributes) {
        User currentUser = userService.getCurrentUser().orElse(null);
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please log in to register for events.");
            return "redirect:/login";
        }

        try {
            Registration reg = registrationService.registerStudentForEvent(currentUser, eventId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Success! You have officially registered for \"" + reg.getEvent().getTitle() + "\".");
            return "redirect:/my-registrations";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/events/" + eventId;
        }
    }

    @GetMapping("/my-registrations")
    public String myRegistrations(Model model, RedirectAttributes redirectAttributes) {
        User currentUser = userService.getCurrentUser().orElse(null);
        if (currentUser == null) {
            return "redirect:/login";
        }

        List<Registration> registrations = registrationService.getRegistrationsForStudent(currentUser);
        model.addAttribute("registrations", registrations);
        model.addAttribute("student", currentUser);

        long activeCount = registrations.stream().filter(Registration::isRegistered).count();
        model.addAttribute("activeCount", activeCount);

        return "my-registrations";
    }

    @PostMapping("/registrations/{id}/cancel")
    public String cancelRegistration(@PathVariable("id") Long registrationId,
                                     RedirectAttributes redirectAttributes) {
        User currentUser = userService.getCurrentUser().orElse(null);
        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            registrationService.cancelRegistration(currentUser, registrationId);
            redirectAttributes.addFlashAttribute("successMessage", "Your registration has been cancelled successfully. Your seat has been released.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/my-registrations";
    }
}
