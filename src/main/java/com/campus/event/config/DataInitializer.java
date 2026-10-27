package com.campus.event.config;

import com.campus.event.model.Event;
import com.campus.event.model.Registration;
import com.campus.event.model.User;
import com.campus.event.repository.EventRepository;
import com.campus.event.repository.RegistrationRepository;
import com.campus.event.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@campus.edu}")
    private String adminEmail;

    @Value("${app.admin.password:Admin@123}")
    private String adminPassword;

    @Value("${app.admin.name:Campus Administrator}")
    private String adminName;

    @Value("${app.admin.department:Dean of Student Affairs}")
    private String adminDepartment;

    public DataInitializer(UserRepository userRepository,
                           EventRepository eventRepository,
                           RegistrationRepository registrationRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        initAdminAccount();
        initSampleStudent();
        initSampleEvents();
    }

    private void initAdminAccount() {
        if (userRepository.countByRole("ROLE_ADMIN") == 0 && !userRepository.existsByEmailIgnoreCase(adminEmail)) {
            User admin = new User(
                    adminName,
                    adminEmail.toLowerCase(),
                    passwordEncoder.encode(adminPassword),
                    "ROLE_ADMIN",
                    adminDepartment,
                    "Faculty / Staff"
            );
            userRepository.save(admin);
            log.info("==================================================================");
            log.info("INITIAL ADMIN ACCOUNT CREATED:");
            log.info("Email:    {}", adminEmail);
            log.info("Role:     ROLE_ADMIN");
            log.info("Use the configured app.admin.password to log in.");
            log.info("==================================================================");
        }
    }

    private void initSampleStudent() {
        String studentEmail = "student@campus.edu";
        if (!userRepository.existsByEmailIgnoreCase(studentEmail)) {
            User student = new User(
                    "Sarah Jenkins",
                    studentEmail,
                    passwordEncoder.encode("Student@123"),
                    "ROLE_STUDENT",
                    "Computer Science & Engineering",
                    "3rd Year"
            );
            userRepository.save(student);
            log.info("Sample student account initialized: {}", studentEmail);
        }
    }

    private void initSampleEvents() {
        if (eventRepository.count() == 0) {
            LocalDate today = LocalDate.now();

            Event e1 = new Event(
                    "Annual 24-Hour CodeFest Hackathon 2026",
                    "Join over 200 passionate coders, designers, and innovators for 24 hours of non-stop creativity. Build software or hardware solutions addressing sustainable campus life and real-world civic challenges. Free food, swag packs, mentorship from top tech companies, and cash prizes worth $5,000!",
                    "Hackathon",
                    "Campus Developer Club & IEEE Student Branch",
                    today.plusDays(10),
                    LocalTime.of(9, 0),
                    LocalTime.of(18, 0),
                    "Main Innovation Center, Auditorium B",
                    120,
                    today.plusDays(8),
                    "https://images.unsplash.com/photo-1504384308090-c894fdcc538d?auto=format&fit=crop&w=1200&q=80",
                    "PUBLISHED"
            );

            Event e2 = new Event(
                    "Next-Gen Artificial Intelligence & LLM Workshop",
                    "A deep-dive, practical workshop covering Large Language Models, prompt engineering, fine-tuning, and modern Retrieval-Augmented Generation (RAG). Laptops required. Participants will deploy an interactive AI assistant by the end of the day.",
                    "Workshop",
                    "Department of Artificial Intelligence & Data Science",
                    today.plusDays(15),
                    LocalTime.of(10, 0),
                    LocalTime.of(16, 0),
                    "Turing Lab 402, Technology Block",
                    60,
                    today.plusDays(12),
                    "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=1200&q=80",
                    "PUBLISHED"
            );

            Event e3 = new Event(
                    "Campus Symphony & Spring Cultural Showcase",
                    "Celebrate the vibrant talents of our college community! Enjoy classical and contemporary musical performances, dance battles, theatrical skits, and student art galleries. Food stalls and festival games will be available on the central lawn.",
                    "Cultural",
                    "Student Cultural Affairs Council",
                    today.plusDays(20),
                    LocalTime.of(17, 30),
                    LocalTime.of(22, 0),
                    "Grand Amphitheatre & University Quad",
                    350,
                    today.plusDays(18),
                    "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=1200&q=80",
                    "PUBLISHED"
            );

            Event e4 = new Event(
                    "Inter-Department 5v5 Futsal Championship",
                    "Show off your skills in the most competitive sports tournament of the semester! Teams of 5 with 2 substitutes will compete in a fast-paced knockout bracket. Trophies and medals awarded to champions and runners-up.",
                    "Sports",
                    "Athletic Department & Sports Club",
                    today.plusDays(25),
                    LocalTime.of(15, 0),
                    LocalTime.of(20, 0),
                    "Indoor Sports Complex, Court 1",
                    80,
                    today.plusDays(22),
                    "https://images.unsplash.com/photo-1574629810360-7efbbe195018?auto=format&fit=crop&w=1200&q=80",
                    "PUBLISHED"
            );

            Event e5 = new Event(
                    "Modern Cloud Architecture & Cybersecurity Summit",
                    "Distinguished guest speakers from leading cybersecurity firms discuss zero-trust security architectures, cloud vulnerability defense, ethical hacking techniques, and career pathways in infosec for graduating engineers.",
                    "Seminar",
                    "School of Information Technology",
                    today.plusDays(30),
                    LocalTime.of(14, 0),
                    LocalTime.of(17, 0),
                    "Sir CV Raman Hall, Science Complex",
                    150,
                    today.plusDays(28),
                    "https://images.unsplash.com/photo-1550751827-4bd374c3f58b?auto=format&fit=crop&w=1200&q=80",
                    "PUBLISHED"
            );

            Event e6 = new Event(
                    "Robotics & Autonomous Drones Expo 2026",
                    "Experience live demonstrations of custom quadcopters, line-following robotics, and autonomous rover prototypes built by student research groups. Includes an obstacle course flight challenge open to all attendees.",
                    "Technical",
                    "Mechatronics & Robotics Club",
                    today.plusDays(35),
                    LocalTime.of(11, 0),
                    LocalTime.of(16, 30),
                    "North Lawn Exhibition Arena",
                    100,
                    today.plusDays(32),
                    "https://images.unsplash.com/photo-1485827404703-89b55fcc595e?auto=format&fit=crop&w=1200&q=80",
                    "PUBLISHED"
            );

            eventRepository.save(e1);
            eventRepository.save(e2);
            eventRepository.save(e3);
            eventRepository.save(e4);
            eventRepository.save(e5);
            eventRepository.save(e6);

            // Create a sample registration for Sarah Jenkins on Event 1
            userRepository.findByEmailIgnoreCase("student@campus.edu").ifPresent(student -> {
                Registration sampleReg = new Registration(student, e1, "REGISTERED");
                registrationRepository.save(sampleReg);
            });

            log.info("Initialized 6 sample campus events with realistic categories and venues.");
        }
    }
}
