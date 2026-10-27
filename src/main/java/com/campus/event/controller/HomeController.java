package com.campus.event.controller;

import com.campus.event.dto.EventDisplayDto;
import com.campus.event.model.User;
import com.campus.event.service.EventService;
import com.campus.event.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final EventService eventService;
    private final UserService userService;

    public HomeController(EventService eventService, UserService userService) {
        this.eventService = eventService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String index(Model model) {
        User currentUser = userService.getCurrentUser().orElse(null);
        List<EventDisplayDto> featuredEvents = eventService.getUpcomingDisplayEvents(6, currentUser);

        model.addAttribute("featuredEvents", featuredEvents);
        model.addAttribute("totalEvents", eventService.getTotalEvents());
        model.addAttribute("totalRegistrations", eventService.getTotalRegistrationsCount());
        model.addAttribute("upcomingEventsCount", eventService.getUpcomingEventsCount());

        return "index";
    }
}
