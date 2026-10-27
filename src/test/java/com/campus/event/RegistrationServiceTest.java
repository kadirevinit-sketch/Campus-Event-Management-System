package com.campus.event;

import com.campus.event.dto.EventDto;
import com.campus.event.model.Event;
import com.campus.event.model.Registration;
import com.campus.event.model.User;
import com.campus.event.repository.EventRepository;
import com.campus.event.repository.RegistrationRepository;
import com.campus.event.repository.UserRepository;
import com.campus.event.service.EventService;
import com.campus.event.service.RegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class RegistrationServiceTest {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private EventService eventService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testStudent;
    private User testStudent2;
    private Event openEvent;

    @BeforeEach
    void setUp() {
        testStudent = userRepository.save(new User(
                "Test Student A",
                "test.student.a@campus.edu",
                passwordEncoder.encode("Password123"),
                "ROLE_STUDENT",
                "Computer Science",
                "2nd Year"
        ));

        testStudent2 = userRepository.save(new User(
                "Test Student B",
                "test.student.b@campus.edu",
                passwordEncoder.encode("Password123"),
                "ROLE_STUDENT",
                "Mechanical Engineering",
                "1st Year"
        ));

        openEvent = eventRepository.save(new Event(
                "Open Test Hackathon",
                "Testing registration business rules",
                "Hackathon",
                "Testing Committee",
                LocalDate.now().plusDays(7),
                LocalTime.of(10, 0),
                LocalTime.of(17, 0),
                "Lab 101",
                2, // Max capacity 2
                LocalDate.now().plusDays(5),
                null,
                "PUBLISHED"
        ));
    }

    @Test
    @DisplayName("Rule 1: Student can successfully register for an open event")
    void testSuccessfulRegistration() {
        Registration reg = registrationService.registerStudentForEvent(testStudent, openEvent.getId());
        assertThat(reg).isNotNull();
        assertThat(reg.getStatus()).isEqualTo("REGISTERED");
        assertThat(reg.getStudent().getId()).isEqualTo(testStudent.getId());

        long activeCount = registrationService.getActiveRegistrationCount(openEvent.getId());
        assertThat(activeCount).isEqualTo(1);
    }

    @Test
    @DisplayName("Rule 1: Duplicate registration by same student is rejected")
    void testDuplicateRegistrationRejected() {
        registrationService.registerStudentForEvent(testStudent, openEvent.getId());

        assertThatThrownBy(() -> registrationService.registerStudentForEvent(testStudent, openEvent.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already registered");
    }

    @Test
    @DisplayName("Rule 2 & 3: Registration is rejected if event is CANCELLED")
    void testRegistrationOnCancelledEventRejected() {
        openEvent.setStatus("CANCELLED");
        eventRepository.save(openEvent);

        assertThatThrownBy(() -> registrationService.registerStudentForEvent(testStudent, openEvent.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("cancelled");
    }

    @Test
    @DisplayName("Rule 4: Registration is rejected after deadline has passed")
    void testRegistrationAfterDeadlineRejected() {
        openEvent.setRegistrationDeadline(LocalDate.now().minusDays(1));
        eventRepository.save(openEvent);

        assertThatThrownBy(() -> registrationService.registerStudentForEvent(testStudent, openEvent.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("deadline");
    }

    @Test
    @DisplayName("Rule 5: Registration is rejected when event capacity is reached")
    void testCapacityLimitEnforced() {
        // Capacity is 2
        registrationService.registerStudentForEvent(testStudent, openEvent.getId());
        registrationService.registerStudentForEvent(testStudent2, openEvent.getId());

        User thirdStudent = userRepository.save(new User(
                "Student Three",
                "student3@campus.edu",
                passwordEncoder.encode("Pass123"),
                "ROLE_STUDENT",
                "Physics",
                "4th Year"
        ));

        assertThatThrownBy(() -> registrationService.registerStudentForEvent(thirdStudent, openEvent.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("capacity");
    }

    @Test
    @DisplayName("Rule 6: Cancellation updates status and releases available seat")
    void testCancellationReleasesSeat() {
        Registration reg = registrationService.registerStudentForEvent(testStudent, openEvent.getId());
        assertThat(registrationService.getActiveRegistrationCount(openEvent.getId())).isEqualTo(1);

        registrationService.cancelRegistration(testStudent, reg.getId());

        Registration cancelled = registrationRepository.findById(reg.getId()).orElseThrow();
        assertThat(cancelled.getStatus()).isEqualTo("CANCELLED");
        assertThat(registrationService.getActiveRegistrationCount(openEvent.getId())).isEqualTo(0);
    }

    @Test
    @DisplayName("Rule 8: Students cannot cancel another student's registration")
    void testStudentCannotCancelOthersRegistration() {
        Registration reg = registrationService.registerStudentForEvent(testStudent, openEvent.getId());

        assertThatThrownBy(() -> registrationService.cancelRegistration(testStudent2, reg.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not authorized");
    }
}
