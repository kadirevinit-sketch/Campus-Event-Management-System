package com.campus.event;

import com.campus.event.repository.EventRepository;
import com.campus.event.repository.RegistrationRepository;
import com.campus.event.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CampusEventApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Test
    @DisplayName("Application context loads and SQLite tables are initialized")
    void contextLoads() {
        assertThat(userRepository).isNotNull();
        assertThat(eventRepository).isNotNull();
        assertThat(registrationRepository).isNotNull();

        // Verify DataInitializer has created initial records in SQLite
        assertThat(userRepository.count()).isGreaterThanOrEqualTo(2);
        assertThat(eventRepository.count()).isGreaterThanOrEqualTo(6);
    }
}
