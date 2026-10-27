package com.campus.event.dto;

import com.campus.event.model.Event;

public class EventDisplayDto {
    private Event event;
    private long registeredCount;
    private int availableSeats;
    private boolean registeredByCurrentUser;
    private boolean registrationAllowed;
    private int percentageFilled;

    public EventDisplayDto(Event event, long registeredCount, boolean registeredByCurrentUser) {
        this.event = event;
        this.registeredCount = registeredCount;
        this.availableSeats = event.getAvailableSeats(registeredCount);
        this.registeredByCurrentUser = registeredByCurrentUser;
        this.registrationAllowed = event.isRegistrationAllowed(registeredCount) && !registeredByCurrentUser;
        if (event.getCapacity() != null && event.getCapacity() > 0) {
            this.percentageFilled = (int) Math.min(100, Math.round(((double) registeredCount / event.getCapacity()) * 100));
        } else {
            this.percentageFilled = 0;
        }
    }

    // Getters and Setters
    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public long getRegisteredCount() {
        return registeredCount;
    }

    public void setRegisteredCount(long registeredCount) {
        this.registeredCount = registeredCount;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public boolean isRegisteredByCurrentUser() {
        return registeredByCurrentUser;
    }

    public void setRegisteredByCurrentUser(boolean registeredByCurrentUser) {
        this.registeredByCurrentUser = registeredByCurrentUser;
    }

    public boolean isRegistrationAllowed() {
        return registrationAllowed;
    }

    public void setRegistrationAllowed(boolean registrationAllowed) {
        this.registrationAllowed = registrationAllowed;
    }

    public int getPercentageFilled() {
        return percentageFilled;
    }

    public void setPercentageFilled(int percentageFilled) {
        this.percentageFilled = percentageFilled;
    }
}
