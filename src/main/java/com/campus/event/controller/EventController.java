package com.campus.event.controller;

import com.campus.event.dto.EventDisplayDto;
import com.campus.event.model.User;
import com.campus.event.service.EventService;
import com.campus.event.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class EventController {

    private final EventService eventService;
    private final UserService userService;

    public EventController(EventService eventService, UserService userService) {
        this.eventService = eventService;
        this.userService = userService;
    }

    @GetMapping("/events")
    public String listEvents(@RequestParam(value = "category", required = false) String category,
                             @RequestParam(value = "keyword", required = false) String keyword,
                             Model model) {
        User currentUser = userService.getCurrentUser().orElse(null);
        List<EventDisplayDto> events = eventService.searchDisplayEvents(category, keyword, currentUser);

        model.addAttribute("events", events);
        model.addAttribute("selectedCategory", category != null ? category : "ALL");
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("totalCount", events.size());

        return "events";
    }

    @GetMapping("/events/{id}")
    public String viewEventDetails(@PathVariable("id") Long id, Model model) {
        User currentUser = userService.getCurrentUser().orElse(null);

        EventDisplayDto eventDisplay = eventService.getEventDisplayById(id, currentUser)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + id));

        model.addAttribute("eventDto", eventDisplay);
        model.addAttribute("event", eventDisplay.getEvent());

        return "event-details";
    }
}
