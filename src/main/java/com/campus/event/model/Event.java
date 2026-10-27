package com.campus.event.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 3000)
    private String description;

    @Column(nullable = false)
    private String category; // Technical, Hackathon, Workshop, Seminar, Cultural, Sports, Other

    @Column(nullable = false)
    private String organizer;

    @Column(nullable = false)
    private LocalDate eventDate;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private String venue;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private LocalDate registrationDeadline;

    @Column(length = 1000)
    private String posterUrl;

    @Column(nullable = false)
    private String status; // DRAFT, PUBLISHED, CANCELLED, COMPLETED

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Event() {
    }

    public Event(String title, String description, String category, String organizer,
                 LocalDate eventDate, LocalTime startTime, LocalTime endTime,
                 String venue, Integer capacity, LocalDate registrationDeadline,
                 String posterUrl, String status) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.organizer = organizer;
        this.eventDate = eventDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.venue = venue;
        this.capacity = capacity;
        this.registrationDeadline = registrationDeadline;
        this.posterUrl = posterUrl;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.status == null || this.status.isBlank()) {
            this.status = "PUBLISHED";
        }
    }

    // Helper methods
    public boolean isPublished() {
        return "PUBLISHED".equalsIgnoreCase(this.status);
    }

    public boolean isCancelled() {
        return "CANCELLED".equalsIgnoreCase(this.status);
    }

    public boolean isCompleted() {
        return "COMPLETED".equalsIgnoreCase(this.status) || isPastEvent();
    }

    public boolean isDeadlinePassed() {
        if (this.registrationDeadline == null) return false;
        return LocalDate.now().isAfter(this.registrationDeadline);
    }

    public boolean isPastEvent() {
        if (this.eventDate == null) return false;
        return LocalDate.now().isAfter(this.eventDate);
    }

    public boolean isRegistrationAllowed(long activeRegistrationsCount) {
        if (!isPublished()) {
            return false;
        }
        if (isCancelled() || isCompleted() || isPastEvent()) {
            return false;
        }
        if (isDeadlinePassed()) {
            return false;
        }
        if (this.capacity != null && activeRegistrationsCount >= this.capacity) {
            return false;
        }
        return true;
    }

    public int getAvailableSeats(long activeRegistrationsCount) {
        if (this.capacity == null) return 0;
        int available = this.capacity - (int) activeRegistrationsCount;
        return Math.max(available, 0);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public LocalDate getRegistrationDeadline() {
        return registrationDeadline;
    }

    public void setRegistrationDeadline(LocalDate registrationDeadline) {
        this.registrationDeadline = registrationDeadline;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
