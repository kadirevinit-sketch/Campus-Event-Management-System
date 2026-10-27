package com.campus.event.controller;

import com.campus.event.dto.EventDto;
import com.campus.event.model.Event;
import com.campus.event.model.Registration;
import com.campus.event.service.EventService;
import com.campus.event.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final EventService eventService;
    private final RegistrationService registrationService;

    public AdminController(EventService eventService, RegistrationService registrationService) {
        this.eventService = eventService;
        this.registrationService = registrationService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(value = "category", required = false) String category,
                            @RequestParam(value = "status", required = false) String status,
                            @RequestParam(value = "keyword", required = false) String keyword,
                            Model model) {
        List<Event> events = eventService.searchAllEventsForAdmin(category, status, keyword);

        // Map events with active participant counts
        var eventsWithStats = events.stream().map(e -> {
            long count = registrationService.getActiveRegistrationCount(e.getId());
            return new Object() {
                public final Event event = e;
                public final long registeredCount = count;
                public final int availableSeats = e.getAvailableSeats(count);
                public final int percentageFilled = (e.getCapacity() != null && e.getCapacity() > 0)
                        ? (int) Math.min(100, Math.round(((double) count / e.getCapacity()) * 100))
                        : 0;
            };
        }).toList();

        model.addAttribute("eventsWithStats", eventsWithStats);
        model.addAttribute("totalEvents", eventService.getTotalEvents());
        model.addAttribute("upcomingCount", eventService.getUpcomingEventsCount());
        model.addAttribute("completedCount", eventService.getCompletedEventsCount());
        model.addAttribute("totalRegistrations", eventService.getTotalRegistrationsCount());

        model.addAttribute("selectedCategory", category != null ? category : "ALL");
        model.addAttribute("selectedStatus", status != null ? status : "ALL");
        model.addAttribute("keyword", keyword != null ? keyword : "");

        return "dashboard";
    }

    @GetMapping("/events/new")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("eventDto")) {
            model.addAttribute("eventDto", new EventDto());
        }
        model.addAttribute("isEdit", false);
        return "event-form";
    }

    @PostMapping("/events/new")
    public String createEvent(@Valid @ModelAttribute("eventDto") EventDto eventDto,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (!eventDto.isTimeRangeValid()) {
            bindingResult.rejectValue("endTime", "error.eventDto", "End time must be later than start time");
        }
        if (!eventDto.isDeadlineValid()) {
            bindingResult.rejectValue("registrationDeadline", "error.eventDto", "Registration deadline cannot be after the event date");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "event-form";
        }

        try {
            Event event = eventService.createEvent(eventDto);
            redirectAttributes.addFlashAttribute("successMessage", "Event \"" + event.getTitle() + "\" created successfully!");
            return "redirect:/admin/dashboard";
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("isEdit", false);
            return "event-form";
        }
    }

    @GetMapping("/events/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Event event = eventService.getEventById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + id));

        EventDto dto = eventService.convertToDto(event);
        model.addAttribute("eventDto", dto);
        model.addAttribute("isEdit", true);
        model.addAttribute("eventId", id);
        return "event-form";
    }

    @PostMapping("/events/{id}/edit")
    public String updateEvent(@PathVariable("id") Long id,
                              @Valid @ModelAttribute("eventDto") EventDto eventDto,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (!eventDto.isTimeRangeValid()) {
            bindingResult.rejectValue("endTime", "error.eventDto", "End time must be later than start time");
        }
        if (!eventDto.isDeadlineValid()) {
            bindingResult.rejectValue("registrationDeadline", "error.eventDto", "Registration deadline cannot be after the event date");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            model.addAttribute("eventId", id);
            return "event-form";
        }

        try {
            Event updated = eventService.updateEvent(id, eventDto);
            redirectAttributes.addFlashAttribute("successMessage", "Event \"" + updated.getTitle() + "\" updated successfully!");
            return "redirect:/admin/dashboard";
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("isEdit", true);
            model.addAttribute("eventId", id);
            return "event-form";
        }
    }

    @PostMapping("/events/{id}/delete")
    public String deleteEvent(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            eventService.deleteEvent(id);
            redirectAttributes.addFlashAttribute("successMessage", "Event and its registrations were deleted successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting event: " + ex.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/events/{id}/status")
    public String updateStatus(@PathVariable("id") Long id,
                               @RequestParam("status") String status,
                               RedirectAttributes redirectAttributes) {
        try {
            eventService.updateStatus(id, status);
            redirectAttributes.addFlashAttribute("successMessage", "Event status updated to " + status + ".");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating status: " + ex.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/events/{id}/registrations")
    public String viewEventRegistrations(@PathVariable("id") Long id, Model model) {
        Event event = eventService.getEventById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + id));

        List<Registration> registrations = registrationService.getRegistrationsForEvent(id);
        long activeCount = registrations.stream().filter(Registration::isRegistered).count();

        model.addAttribute("event", event);
        model.addAttribute("registrations", registrations);
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("availableSeats", event.getAvailableSeats(activeCount));

        return "admin-registrations";
    }
}
