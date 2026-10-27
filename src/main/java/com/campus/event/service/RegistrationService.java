package com.campus.event.service;

import com.campus.event.model.Event;
import com.campus.event.model.Registration;
import com.campus.event.model.User;
import com.campus.event.repository.EventRepository;
import com.campus.event.repository.RegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;

    // Lock object for concurrency safety during registration seat checks
    private final Object registrationLock = new Object();

    public RegistrationService(RegistrationRepository registrationRepository, EventRepository eventRepository) {
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional
    public Registration registerStudentForEvent(User student, Long eventId) {
        if (student == null) {
            throw new IllegalArgumentException("User must be logged in to register.");
        }
        if (!"ROLE_STUDENT".equalsIgnoreCase(student.getRole())) {
            throw new IllegalArgumentException("Only students can register for events.");
        }

        synchronized (registrationLock) {
            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + eventId));

            // Rule 2 & 3: Allowed only for published events, rejected for cancelled or completed events
            if (event.isCancelled()) {
                throw new IllegalStateException("This event has been cancelled.");
            }

            if (event.isCompleted() || event.isPastEvent()) {
                throw new IllegalStateException("This event has already concluded.");
            }

            if (!event.isPublished()) {
                throw new IllegalStateException("Registration is not open for this event. Event status: " + event.getStatus());
            }

            // Rule 4: Registration is rejected after the registration deadline
            if (event.isDeadlinePassed()) {
                throw new IllegalStateException("The registration deadline for this event (" + event.getRegistrationDeadline() + ") has passed.");
            }

            // Rule 1: Student cannot register more than once for the same event
            Optional<Registration> existingOpt = registrationRepository.findByStudentAndEvent(student, event);
            if (existingOpt.isPresent()) {
                Registration existing = existingOpt.get();
                if ("REGISTERED".equalsIgnoreCase(existing.getStatus())) {
                    throw new IllegalStateException("You are already registered for this event.");
                } else {
                    // Previously cancelled - reactivate if capacity permits
                    long activeCount = registrationRepository.countByEventIdAndStatus(eventId, "REGISTERED");
                    if (event.getCapacity() != null && activeCount >= event.getCapacity()) {
                        throw new IllegalStateException("Sorry, this event has reached its maximum capacity (" + event.getCapacity() + " participants).");
                    }
                    existing.setStatus("REGISTERED");
                    existing.setRegisteredAt(LocalDateTime.now());
                    return registrationRepository.save(existing);
                }
            }

            // Rule 5 & 10: Capacity check with concurrency lock
            long activeCount = registrationRepository.countByEventIdAndStatus(eventId, "REGISTERED");
            if (event.getCapacity() != null && activeCount >= event.getCapacity()) {
                throw new IllegalStateException("Sorry, this event has reached its maximum capacity (" + event.getCapacity() + " participants).");
            }

            Registration newRegistration = new Registration(student, event, "REGISTERED");
            return registrationRepository.save(newRegistration);
        }
    }

    @Transactional
    public void cancelRegistration(User student, Long registrationId) {
        if (student == null) {
            throw new IllegalArgumentException("Authentication required.");
        }

        Registration reg = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new IllegalArgumentException("Registration not found with ID: " + registrationId));

        // Rule 8: Students can only cancel their own registrations (or admin can manage)
        if (!reg.getStudent().getId().equals(student.getId()) && !student.isAdmin()) {
            throw new IllegalStateException("You are not authorized to cancel this registration.");
        }

        // Rule 6: Cancellation updates registration status and seat availability
        reg.setStatus("CANCELLED");
        registrationRepository.save(reg);
    }

    public List<Registration> getRegistrationsForStudent(User student) {
        return registrationRepository.findByStudentOrderByRegisteredAtDesc(student);
    }

    public List<Registration> getRegistrationsForEvent(Long eventId) {
        return registrationRepository.findByEventIdOrderByRegisteredAtDesc(eventId);
    }

    public boolean isStudentRegistered(Long studentId, Long eventId) {
        return registrationRepository.existsByStudentIdAndEventIdAndStatus(studentId, eventId, "REGISTERED");
    }

    public long getActiveRegistrationCount(Long eventId) {
        return registrationRepository.countByEventIdAndStatus(eventId, "REGISTERED");
    }
}
