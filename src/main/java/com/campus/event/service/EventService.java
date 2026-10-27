package com.campus.event.service;

import com.campus.event.dto.EventDisplayDto;
import com.campus.event.dto.EventDto;
import com.campus.event.model.Event;
import com.campus.event.model.User;
import com.campus.event.repository.EventRepository;
import com.campus.event.repository.RegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;

    public EventService(EventRepository eventRepository, RegistrationRepository registrationRepository) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAllByOrderByEventDateDesc();
    }

    public List<Event> getPublishedEvents() {
        return eventRepository.findByStatusOrderByEventDateAsc("PUBLISHED");
    }

    public List<EventDisplayDto> getUpcomingDisplayEvents(int limit, User currentUser) {
        List<Event> events = eventRepository.findByStatusAndEventDateGreaterThanEqualOrderByEventDateAsc("PUBLISHED", LocalDate.now());
        return events.stream()
                .limit(limit)
                .map(e -> getEventDisplay(e, currentUser))
                .collect(Collectors.toList());
    }

    public List<EventDisplayDto> searchDisplayEvents(String category, String keyword, User currentUser) {
        String catParam = (category == null || category.equalsIgnoreCase("ALL") || category.isBlank()) ? null : category;
        String keyParam = (keyword == null || keyword.isBlank()) ? null : keyword.trim();

        List<Event> events = eventRepository.searchEvents("PUBLISHED", catParam, keyParam);
        return events.stream()
                .map(e -> getEventDisplay(e, currentUser))
                .collect(Collectors.toList());
    }

    public List<Event> searchAllEventsForAdmin(String category, String status, String keyword) {
        String catParam = (category == null || category.equalsIgnoreCase("ALL") || category.isBlank()) ? null : category;
        String statusParam = (status == null || status.equalsIgnoreCase("ALL") || status.isBlank()) ? null : status;
        String keyParam = (keyword == null || keyword.isBlank()) ? null : keyword.trim();

        return eventRepository.searchAllEventsForAdmin(catParam, statusParam, keyParam);
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public EventDisplayDto getEventDisplay(Event event, User currentUser) {
        long registeredCount = registrationRepository.countByEventIdAndStatus(event.getId(), "REGISTERED");
        boolean isRegistered = false;
        if (currentUser != null && "ROLE_STUDENT".equalsIgnoreCase(currentUser.getRole())) {
            isRegistered = registrationRepository.existsByStudentIdAndEventIdAndStatus(currentUser.getId(), event.getId(), "REGISTERED");
        }
        return new EventDisplayDto(event, registeredCount, isRegistered);
    }

    public Optional<EventDisplayDto> getEventDisplayById(Long id, User currentUser) {
        return eventRepository.findById(id).map(e -> getEventDisplay(e, currentUser));
    }

    public long getActiveRegistrationsCount(Long eventId) {
        return registrationRepository.countByEventIdAndStatus(eventId, "REGISTERED");
    }

    @Transactional
    public Event createEvent(EventDto dto) {
        validateEventDto(dto);

        Event event = new Event();
        copyDtoToEvent(dto, event);
        return eventRepository.save(event);
    }

    @Transactional
    public Event updateEvent(Long id, EventDto dto) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + id));

        validateEventDto(dto);
        copyDtoToEvent(dto, event);
        return eventRepository.save(event);
    }

    @Transactional
    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + id));

        // Delete all registrations associated with the event
        var registrations = registrationRepository.findByEventOrderByRegisteredAtDesc(event);
        registrationRepository.deleteAll(registrations);

        eventRepository.delete(event);
    }

    @Transactional
    public Event updateStatus(Long id, String status) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + id));
        event.setStatus(status.toUpperCase());
        return eventRepository.save(event);
    }

    public EventDto convertToDto(Event event) {
        EventDto dto = new EventDto();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setCategory(event.getCategory());
        dto.setOrganizer(event.getOrganizer());
        dto.setEventDate(event.getEventDate());
        dto.setStartTime(event.getStartTime());
        dto.setEndTime(event.getEndTime());
        dto.setVenue(event.getVenue());
        dto.setCapacity(event.getCapacity());
        dto.setRegistrationDeadline(event.getRegistrationDeadline());
        dto.setPosterUrl(event.getPosterUrl());
        dto.setStatus(event.getStatus());
        return dto;
    }

    // Admin dashboard metrics
    public long getTotalEvents() {
        return eventRepository.count();
    }

    public long getUpcomingEventsCount() {
        return eventRepository.countByEventDateGreaterThanEqual(LocalDate.now());
    }

    public long getCompletedEventsCount() {
        return eventRepository.countByEventDateBefore(LocalDate.now()) + eventRepository.countByStatus("COMPLETED");
    }

    public long getTotalRegistrationsCount() {
        return registrationRepository.countByStatus("REGISTERED");
    }

    private void validateEventDto(EventDto dto) {
        if (!dto.isTimeRangeValid()) {
            throw new IllegalArgumentException("End time must be after start time.");
        }
        if (!dto.isDeadlineValid()) {
            throw new IllegalArgumentException("Registration deadline cannot be after the event date.");
        }
        if (dto.getCapacity() == null || dto.getCapacity() <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero.");
        }
    }

    private void copyDtoToEvent(EventDto dto, Event event) {
        event.setTitle(dto.getTitle().trim());
        event.setDescription(dto.getDescription().trim());
        event.setCategory(dto.getCategory().trim());
        event.setOrganizer(dto.getOrganizer().trim());
        event.setEventDate(dto.getEventDate());
        event.setStartTime(dto.getStartTime());
        event.setEndTime(dto.getEndTime());
        event.setVenue(dto.getVenue().trim());
        event.setCapacity(dto.getCapacity());
        event.setRegistrationDeadline(dto.getRegistrationDeadline());
        event.setPosterUrl(dto.getPosterUrl() != null && !dto.getPosterUrl().isBlank() ? dto.getPosterUrl().trim() : null);
        event.setStatus(dto.getStatus() != null ? dto.getStatus().trim().toUpperCase() : "PUBLISHED");
    }
}
