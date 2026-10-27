package com.campus.event.repository;

import com.campus.event.model.Event;
import com.campus.event.model.Registration;
import com.campus.event.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    Optional<Registration> findByStudentAndEvent(User student, Event event);

    Optional<Registration> findByStudentIdAndEventId(Long studentId, Long eventId);

    boolean existsByStudentIdAndEventIdAndStatus(Long studentId, Long eventId, String status);

    List<Registration> findByStudentOrderByRegisteredAtDesc(User student);

    List<Registration> findByEventOrderByRegisteredAtDesc(Event event);

    List<Registration> findByEventIdOrderByRegisteredAtDesc(Long eventId);

    long countByEventIdAndStatus(Long eventId, String status);

    long countByStatus(String status);
}
